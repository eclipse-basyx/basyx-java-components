/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.basyx.components.internal.mongodb;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.extensions.internal.storage.BaSyxStorageAPI;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.result.DeleteResult;

/**
 * Provides BaSyxStorageAPI implementation for MongoDB
 * 
 * @author fischer, jungjan, witt
 *
 * @param <T>
 */
public class MongoDBBaSyxStorageAPI<T> extends BaSyxStorageAPI<T> {
	private final String INDEX_KEY = Identifiable.IDENTIFICATION + "." + Identifier.ID;

	protected BaSyxMongoDBConfiguration config;
	protected MongoClient client;
	protected MongoOperations mongoOps;

	/**
	 * @deprecated Please use the other constructor with MongoClient client. 
	 *             Using this constructor may lead to inefficient resource utilization.
	 */
	@Deprecated
	public MongoDBBaSyxStorageAPI(String collectionName, Class<T> type, BaSyxMongoDBConfiguration config) {
		this(collectionName, type, config, MongoClients.create(config.getConnectionUrl()));
	}

	public MongoDBBaSyxStorageAPI(String collectionName, Class<T> type, BaSyxMongoDBConfiguration config, MongoClient client) {
		super(collectionName, type);
		this.config = config;
		this.client = client;
		this.mongoOps = new MongoTemplate(client, config.getDatabase());
		this.configureIndexKey();
	}

	private void configureIndexKey() {
		TextIndexDefinition idIndex = TextIndexDefinition.builder().onField(INDEX_KEY).build();
		this.mongoOps.indexOps(TYPE).ensureIndex(idIndex);
	}

	@Override
	public T createOrUpdate(T obj) {
		String key = getKey(obj);
		if (alreadyExists(key)) {
			return update(obj, key);
		}

		T created = mongoOps.insert(obj, getCollectionName());
		return handleMongoDbIdAttribute(created);
	}

	private boolean alreadyExists(String key) {
		Query hasId = query(where(INDEX_KEY).is(key));
		return mongoOps.exists(hasId, getCollectionName());
	}

	@Override
	public T update(T obj, String key) {
		T replaced = findAndReplaceIfExists(obj, key);
		if (replaced == null) {
			logger.warn("Could not execute update for key {} as it does not exist in the database; Creating new entry...", key);
			return createOrUpdate(obj);
		}
		replaced = handleMongoDbIdAttribute(replaced);
		return replaced;
	}

	private T findAndReplaceIfExists(T obj, String key) {
		Query hasId = query(where(INDEX_KEY).is(key));
		FindAndReplaceOptions replacementOptions = setupReplacemantOptionsToReturnNew();
		T replaced = mongoOps.findAndReplace(hasId, obj, replacementOptions.returnNew(), getCollectionName());
		return replaced;
	}

	private FindAndReplaceOptions setupReplacemantOptionsToReturnNew() {
		FindAndReplaceOptions replacementOptions = FindAndReplaceOptions.empty();
		replacementOptions.returnNew();
		return replacementOptions;
	}

	@SuppressWarnings("unchecked")
	public T handleMongoDbIdAttribute(T data) {
		if (data instanceof Map)
			((Map<String, Object>) data).remove("_id");
		return data;
	}

	@Override
	public boolean delete(String key) {
		Query hasId = query(where(INDEX_KEY).is(key));
		DeleteResult result = mongoOps.remove(hasId, getCollectionName());
		return result.getDeletedCount() == 1L;
	}

	@Override
	public void createCollectionIfNotExists(String collectionName) {
		// MongoOperations implicitly creates Collections.
	}

	@Override
	public void deleteCollection() {
		mongoOps.dropCollection(getCollectionName());
	}

	@Override
	public T rawRetrieve(String key) {
		Query hasId = query(where(INDEX_KEY).is(key));
		var result = mongoOps.findOne(hasId, TYPE, getCollectionName());
		if (result == null) {
			throw new ResourceNotFoundException("No Object for key '" + key + "' found in the database.");
		}
		result = handleMongoDbIdAttribute(result);
		return (T) result;
	}

	@Override
	public java.io.File getFile(String idShortPath, String parentKey, Map<String, Object> objMap) {
		try {
			File fileSubmodelElement = File.createAsFacade(objMap);
			GridFSBucket bucket = MongoDBFileHelper.getGridFSBucket(client, config);
			String fileName = MongoDBFileHelper.constructFileName(parentKey, fileSubmodelElement, idShortPath);
			java.io.File file = new java.io.File(fileName);
			// check if file with this filename exist in MongoDB
			// there might be older files constructed with old (=legacy) filenames, use this one instead
			// the real file in file system still uses the new filename pattern!
			if (!MongoDBFileHelper.fileExists(bucket, fileName)) {
				fileName = MongoDBFileHelper.legacyFileName(parentKey, fileSubmodelElement, idShortPath);
			}
			try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
				bucket.downloadToStream(fileName, fileOutputStream);
			}
			return file;
		} catch (IOException e) {
			throw new ResourceNotFoundException("The File Submodel Element does not contain a File");
		}
	}

	@Override
	public String writeFile(String idShortPath, String parentKey, InputStream inputStream, ISubmodelElement element) {
		return MongoDBFileHelper.updateFileInDB(client, config, parentKey, inputStream, element, idShortPath);
	}

	@Override
	public void deleteFile(Submodel submodel, String idShort) {
		MongoDBFileHelper.deleteAllFilesFromGridFsIfIsFileSubmodelElement(client, config, submodel, idShort);
	}

	@Override
	public Collection<T> rawRetrieveAll() {
		Collection<T> data = mongoOps.findAll(TYPE, getCollectionName());
		data = data.stream()
				.map(this::handleMongoDbIdAttribute)
				.collect(Collectors.toList());
		return data;
	}

	@Override
	public Object getStorageConnection() {
		return mongoOps;
	}

	public MongoClient getClient() {
		return client;
	}
}
