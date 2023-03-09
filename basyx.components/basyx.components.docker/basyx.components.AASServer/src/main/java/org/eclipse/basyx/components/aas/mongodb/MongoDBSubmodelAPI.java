/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.components.aas.mongodb;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.facade.submodelelement.SubmodelElementFacadeFactory;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.SubmodelElementProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.operation.DelegatedInvocationManager;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.model.Filters;

/**
 * Implements the ISubmodelAPI for a mongoDB backend.
 * 
 * @author espen
 */
public class MongoDBSubmodelAPI implements ISubmodelAPI {
	private static final String DEFAULT_CONFIG_PATH = "mongodb.properties";
	public static final String SMIDPATH = Identifiable.IDENTIFICATION + "." + Identifier.ID;

	protected DelegatedInvocationManager invocationHelper;

	protected BaSyxMongoDBConfiguration config;
	protected MongoOperations mongoOps;
	protected String collection;
	protected String smId;
	private MongoClient client;

	/**
	 * Receives the path of the configuration.properties file in its constructor.
	 * 
	 * @param config
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBSubmodelAPI(BaSyxMongoDBConfiguration config, String smId) {
		this(config, smId, new DelegatedInvocationManager(new HTTPConnectorFactory()));
	}

	/**
	 * Receives the path of the configuration.properties file in its constructor.
	 * 
	 * @param config
	 */
	public MongoDBSubmodelAPI(BaSyxMongoDBConfiguration config, String smId, MongoClient client) {
		this(config, smId, new DelegatedInvocationManager(new HTTPConnectorFactory()), client);
	}

	@Deprecated
	public MongoDBSubmodelAPI(BaSyxMongoDBConfiguration config, String smId,
			DelegatedInvocationManager invocationHelper) {
		this(config, smId, invocationHelper, MongoClients.create(config.getConnectionUrl()));
	}

	public MongoDBSubmodelAPI(BaSyxMongoDBConfiguration config, String smId,
			DelegatedInvocationManager invocationHelper, MongoClient client) {
		this.client = client;
		this.setConfiguration(config);
		this.setSubmodelId(smId);
		this.invocationHelper = invocationHelper;
	}

	/**
	 * Receives the path of the .properties file in its constructor from a resource.
	 * 
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBSubmodelAPI(String resourceConfigPath, String smId) {
		this(resourceConfigPath, smId, new DelegatedInvocationManager(new HTTPConnectorFactory()));
	}

	/**
	 * Receives the path of the .properties file in its constructor from a resource.
	 */
	public MongoDBSubmodelAPI(String resourceConfigPath, String smId, MongoClient client) {
		this(resourceConfigPath, smId, new DelegatedInvocationManager(new HTTPConnectorFactory()), client);
	}

	@Deprecated
	public MongoDBSubmodelAPI(String resourceConfigPath, String smId, DelegatedInvocationManager invocationHelper) {
		config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(resourceConfigPath);
		this.client = MongoClients.create(config.getConnectionUrl());
		this.setConfiguration(config);
		this.setSubmodelId(smId);
		this.invocationHelper = invocationHelper;
	}

	public MongoDBSubmodelAPI(String resourceConfigPath, String smId, DelegatedInvocationManager invocationHelper,
			MongoClient client) {
		config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(resourceConfigPath);
		this.client = client;
		this.setConfiguration(config);
		this.setSubmodelId(smId);
		this.invocationHelper = invocationHelper;
	}

	/**
	 * Constructor using default MongoDB connections
	 * 
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBSubmodelAPI(String smId) {
		this(DEFAULT_CONFIG_PATH, smId);
	}

	@Deprecated
	public MongoDBSubmodelAPI(String smId, DelegatedInvocationManager invocationHelper) {
		this(DEFAULT_CONFIG_PATH, smId, invocationHelper);
	}

	/**
	 * Constructor using default MongoDB connections
	 */
	public MongoDBSubmodelAPI(String smId, MongoClient client) {
		this(DEFAULT_CONFIG_PATH, smId, client);
	}

	public MongoDBSubmodelAPI(String smId, DelegatedInvocationManager invocationHelper, MongoClient client) {
		this(DEFAULT_CONFIG_PATH, smId, invocationHelper, client);
	}

	/**
	 * Sets the db configuration for the submodel API.
	 * 
	 * @param config
	 */
	public void setConfiguration(BaSyxMongoDBConfiguration config) {
		this.config = config;
		this.mongoOps = new MongoTemplate(client, config.getDatabase());
		this.collection = config.getSubmodelCollection();
	}

	/**
	 * Sets the submodel id, so that this API points to the submodel with smId. Can
	 * be changed to point to a different submodel in the database.
	 * 
	 * @param smId
	 */
	public void setSubmodelId(String smId) {
		this.smId = smId;
	}

	/**
	 * Depending on whether the model is already in the db, this method inserts or
	 * replaces the existing data. The new submodel id for this API is taken from
	 * the given submodel.
	 * 
	 * @param sm
	 */
	public void setSubmodel(Submodel sm) {
		String id = sm.getIdentification().getId();
		this.setSubmodelId(id);

		Submodel replaced = writeSubmodelInDB(sm);
		if (replaced == null) {
			mongoOps.insert(sm, collection);
		}
		// Remove mongoDB-specific map attribute from SM
		// mongoOps modify sm on save - thus _id has to be removed here...
		sm.remove("_id");
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISubmodel getSubmodel() {
		// Query Submodel from MongoDB
		Query hasId = query(where(SMIDPATH).is(smId));
		Submodel result = mongoOps.findOne(hasId, Submodel.class, collection);
		if (result == null) {
			throw new ResourceNotFoundException("The submodel " + smId + " could not be found in the database.");
		}

		// Remove mongoDB-specific map attribute from AASDescriptor
		result.remove("_id");

		// Cast all SubmodelElement maps to ISubmodelElements before returning the
		// submodel
		Map<String, ISubmodelElement> elements = new HashMap<>();
		Map<String, Map<String, Object>> elemMaps = (Map<String, Map<String, Object>>) result
				.get(Submodel.SUBMODELELEMENT);
		for (Entry<String, Map<String, Object>> entry : elemMaps.entrySet()) {
			String shortId = entry.getKey();
			Map<String, Object> elemMap = entry.getValue();
			ISubmodelElement element = SubmodelElementFacadeFactory.createSubmodelElement(elemMap);
			elements.put(shortId, element);
		}
		// Replace the element map in the submodel
		result.put(Submodel.SUBMODELELEMENT, elements);
		// Return the "fixed" submodel
		return result;
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		// Get sm from db
		Submodel sm = (Submodel) getSubmodel();
		// Add element
		sm.addSubmodelElement(elem);
		writeSubmodelInDB(sm);
	}

	private ISubmodelElement getTopLevelSubmodelElement(String idShort) {
		Submodel sm = (Submodel) getSubmodel();
		Map<String, ISubmodelElement> submodelElements = sm.getSubmodelElements();
		ISubmodelElement element = submodelElements.get(idShort);
		if (element == null) {
			throw new ResourceNotFoundException("The element \"" + idShort + "\" could not be found");
		}
		return convertSubmodelElement(element);
	}

	@SuppressWarnings("unchecked")
	private ISubmodelElement convertSubmodelElement(ISubmodelElement element) {
		// FIXME: Convert internal data structure of ISubmodelElement
		Map<String, Object> elementMap = (Map<String, Object>) element;
		IModelProvider elementProvider = new SubmodelElementProvider(new VABMapProvider(elementMap));
		Object elementVABObj = elementProvider.getValue("");
		return SubmodelElement.createAsFacade((Map<String, Object>) elementVABObj);
	}

	private void deleteTopLevelSubmodelElement(String idShort) {
		// Get sm from db
		Submodel sm = (Submodel) getSubmodel();
		// Remove element
		
		deleteAllFilesFromGridFsIfIsFileSubmodelElement(idShort, sm);

		sm.getSubmodelElements().remove(idShort);
		writeSubmodelInDB(sm);

	}

	@SuppressWarnings("unchecked")
	private void deleteAllFilesFromGridFsIfIsFileSubmodelElement(String idShort, Submodel sm) {
		Map<String,Object> submodelElement = (Map<String, Object>) sm.getSubmodelElement(idShort);
		if(File.isFile(submodelElement)) {
			File file = File.createAsFacade(submodelElement);
			GridFSBucket bucket = getGridFSBucket();
			bucket.find(Filters.eq("filename", file.getValue()))
					.forEach(gridFile -> bucket.delete(gridFile.getObjectId()));
		}
	}

	@Override
	public Collection<IOperation> getOperations() {
		Submodel sm = (Submodel) getSubmodel();
		return sm.getOperations().values();
	}

	private void addNestedSubmodelElement(List<String> idShorts, ISubmodelElement elem) {
		Submodel sm = (Submodel) getSubmodel();
		// > 1 idShorts => add new sm element to an existing sm element
		if (idShorts.size() > 1) {
			idShorts = idShorts.subList(0, idShorts.size() - 1);
			// Get parent SM element if more than 1 idShort
			ISubmodelElement parentElement = getNestedSubmodelElement(sm, idShorts);
			if (parentElement instanceof SubmodelElementCollection) {
				((SubmodelElementCollection) parentElement).addSubmodelElement(elem);
				writeSubmodelInDB(sm);
			}
		} else {
			// else => directly add it to the submodel
			sm.addSubmodelElement(elem);
			writeSubmodelInDB(sm);
		}
	}

	@Override
	public Collection<ISubmodelElement> getSubmodelElements() {
		Submodel sm = (Submodel) getSubmodel();
		return sm.getSubmodelElements().values();
	}

	@SuppressWarnings("unchecked")
	private void updateSubmodelElementInDB(List<String> idShorts, Object newValue) {
		Submodel sm = (Submodel) getSubmodel();
		ISubmodelElement element = getNestedSubmodelElement(sm, idShorts);
		if (isNewValueAFile(newValue)) {
			newValue = updateFileInDB(newValue, element);
		}
		IModelProvider mapProvider = new VABLambdaProvider((Map<String, Object>) element);
		SubmodelElementProvider smeProvider = new SubmodelElementProvider(mapProvider);

		smeProvider.setValue(Property.VALUE, newValue);
		ISubmodelElement updatedElement = SubmodelElementFacadeFactory
				.createSubmodelElement((Map<String, Object>) smeProvider.getValue(""));

		sm.addSubmodelElement(updatedElement);

		writeSubmodelInDB(sm);
	}

	private Object updateFileInDB(Object newValue, ISubmodelElement element) {
		File file = File.createAsFacade((Map<String, Object>) element);
		GridFSBucket bucket = getGridFSBucket();
		String fileName = file.getValue();
		if(fileName.isEmpty()) {
			fileName = file.getIdShort()+"."+file.getMimeType();
		}
		deleteAllDuplicateFiles(bucket, fileName);
		bucket.uploadFromStream(fileName, (FileInputStream) newValue);				
		newValue = fileName;
		return newValue;
	}

	private boolean isNewValueAFile(Object newValue) {
		return newValue instanceof FileInputStream;
	}

	private void deleteAllDuplicateFiles(GridFSBucket bucket, String fileName) {
		bucket.find(Filters.eq("filename", fileName))
				.forEach(gridFile -> bucket.delete(gridFile.getObjectId()));
	}

	private GridFSBucket getGridFSBucket() {
		MongoDatabase database = client.getDatabase(config.getDatabase());
		GridFSBucket bucket = GridFSBuckets.create(database, config.getFileCollection());
		return bucket;
	}

	private Object getTopLevelSubmodelElementValue(String idShort) {
		Submodel sm = (Submodel) getSubmodel();
		return getElementProvider(sm, idShort).getValue("/value");
	}

	@SuppressWarnings("unchecked")
	private Object getNestedSubmodelElementValue(List<String> idShorts) {
		ISubmodelElement lastElement = getNestedSubmodelElement(idShorts);
		IModelProvider mapProvider = new VABLambdaProvider((Map<String, Object>) lastElement);
		return new SubmodelElementProvider(mapProvider).getValue("/value");
	}

	@SuppressWarnings("unchecked")
	protected Object unwrapParameter(Object parameter) {
		if (parameter instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) parameter;
			// Parameters have a strictly defined order and may not be omitted at all.
			// Enforcing the structure with valueType is ok, but we should unwrap null
			// values, too.
			if (map.get("valueType") != null && map.containsKey("value")) {
				return map.get("value");
			}
		}
		return parameter;
	}

	@SuppressWarnings("unchecked")
	private static SubmodelElementProvider getElementProvider(Submodel sm, String idShort) {
		ISubmodelElement elem = sm.getSubmodelElement(idShort);
		IModelProvider mapProvider = new VABMapProvider((Map<String, Object>) elem);
		return new SubmodelElementProvider(mapProvider);
	}

	private ISubmodelElement getNestedSubmodelElement(Submodel sm, List<String> idShorts) {
		Map<String, ISubmodelElement> elemMap = sm.getSubmodelElements();
		// Get last nested submodel element
		for (int i = 0; i < idShorts.size() - 1; i++) {
			String idShort = idShorts.get(i);
			ISubmodelElement elem = elemMap.get(idShort);
			if (elem instanceof SubmodelElementCollection) {
				elemMap = ((SubmodelElementCollection) elem).getSubmodelElements();
			} else {
				throw new ResourceNotFoundException(
						idShort + " in the nested submodel element path could not be resolved.");
			}
		}
		String lastIdShort = idShorts.get(idShorts.size() - 1);
		if (!elemMap.containsKey(lastIdShort)) {
			throw new ResourceNotFoundException(
					lastIdShort + " in the nested submodel element path could not be resolved.");
		}
		return elemMap.get(lastIdShort);
	}

	private ISubmodelElement getNestedSubmodelElement(List<String> idShorts) {
		// Get sm from db
		Submodel sm = (Submodel) getSubmodel();
		// Get nested sm element from this sm
		return convertSubmodelElement(getNestedSubmodelElement(sm, idShorts));
	}

	private void deleteNestedSubmodelElement(List<String> idShorts) {
		if (idShorts.size() == 1) {
			deleteSubmodelElement(idShorts.get(0));
			return;
		}

		// Get sm from db
		Submodel sm = (Submodel) getSubmodel();
		// Get parent collection
		List<String> parentIds = idShorts.subList(0, idShorts.size() - 1);
		ISubmodelElement parentElement = getNestedSubmodelElement(sm, parentIds);
		// Remove element
		SubmodelElementCollection coll = (SubmodelElementCollection) parentElement;
		coll.deleteSubmodelElement(idShorts.get(idShorts.size() - 1));
		writeSubmodelInDB(sm);
	}

	private Object invokeNestedOperationAsync(List<String> idShorts, Object... params) {
		// not possible to invoke operations on a submodel that is stored in a db
		throw new MalformedRequestException("Invoke not supported by this backend");
	}

	@Override
	public Object getOperationResult(String idShort, String requestId) {
		// not possible to invoke operations on a submodel that is stored in a db
		throw new MalformedRequestException("Invoke not supported by this backend");
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		if (idShortPath.contains("/")) {
			String[] splitted = VABPathTools.splitPath(idShortPath);
			List<String> idShorts = Arrays.asList(splitted);
			return getNestedSubmodelElement(idShorts);
		} else {
			return getTopLevelSubmodelElement(idShortPath);
		}
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		if (idShortPath.contains("/")) {
			String[] splitted = VABPathTools.splitPath(idShortPath);
			List<String> idShorts = Arrays.asList(splitted);
			deleteNestedSubmodelElement(idShorts);
		} else {
			deleteTopLevelSubmodelElement(idShortPath);
		}
	}

	@Override
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		String[] splitted = VABPathTools.splitPath(idShortPath);
		List<String> idShorts = Arrays.asList(splitted);
		updateSubmodelElementInDB(idShorts, newValue);
	}

	/**
	 * Returns the updated Submodel or null if not found
	 * 
	 * @param sm
	 * @return
	 */
	private Submodel writeSubmodelInDB(Submodel sm) {
		Query hasId = query(where(SMIDPATH).is(smId));
		return mongoOps.findAndReplace(hasId, sm, collection);
	}

	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		if (idShortPath.contains("/")) {
			String[] splitted = VABPathTools.splitPath(idShortPath);
			List<String> idShorts = Arrays.asList(splitted);
			return getNestedSubmodelElementValue(idShorts);
		} else {
			return getTopLevelSubmodelElementValue(idShortPath);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object invokeOperation(String idShortPath, Object... params) {
		Operation operation = (Operation) SubmodelElementFacadeFactory
				.createSubmodelElement((Map<String, Object>) getSubmodelElement(idShortPath));
		if (!DelegatedInvocationManager.isDelegatingOperation(operation)) {
			throw new MalformedRequestException("This backend supports only delegating operations.");
		}
		return invocationHelper.invokeDelegatedOperation(operation, params);
	}

	@Override
	public Object invokeAsync(String idShortPath, Object... params) {
		String[] splitted = VABPathTools.splitPath(idShortPath);
		List<String> idShorts = Arrays.asList(splitted);
		return invokeNestedOperationAsync(idShorts, params);
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		String[] splitted = VABPathTools.splitPath(idShortPath);
		List<String> idShorts = Arrays.asList(splitted);
		addNestedSubmodelElement(idShorts, elem);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getSubmodelElementFile(String idShortPath) {
		try {
			Map<String, Object> submodelElement = (Map<String, Object>) getSubmodelElement(idShortPath);
			File fileSubmodelElement = File.createAsFacade(submodelElement);
			GridFSBucket bucket = getGridFSBucket();
			String fileName = fileSubmodelElement.getIdShort() + "." + fileSubmodelElement.getMimeType();
			java.io.File file = new java.io.File(fileName);
			FileOutputStream fileOutputStream;
			fileOutputStream = new FileOutputStream(file);
			bucket.downloadToStream(fileName, fileOutputStream);
			return file;
		} catch (FileNotFoundException e) {
			throw new ProviderException("The File Submodel Element does not contain a File");
		}
	}
}
