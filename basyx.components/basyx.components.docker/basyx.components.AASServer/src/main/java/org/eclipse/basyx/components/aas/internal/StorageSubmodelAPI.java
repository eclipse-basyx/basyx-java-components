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

package org.eclipse.basyx.components.aas.internal;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.extensions.internal.storage.BaSyxStorageAPI;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.facade.submodelelement.SubmodelElementFacadeFactory;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.SubmodelElementProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.operation.DelegatedInvocationManager;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;

/**
 * Abstract submodel api for storage backends.
 * 
 * @author fischer
 *
 */
public abstract class StorageSubmodelAPI implements ISubmodelAPI {
	protected BaSyxStorageAPI<Submodel> storageApi;
	private String identificationId;
	private DelegatedInvocationManager invocationManager;

	protected StorageSubmodelAPI(BaSyxStorageAPI<Submodel> storageAPI, String identificationId, DelegatedInvocationManager invocatonManager) {
		this.storageApi = storageAPI;
		this.identificationId = identificationId;
		this.invocationManager = invocatonManager;
	}

	public String getSubmodelId() {
		return identificationId;
	}

	/**
	 * Sets the submodel id, so that this API points to the submodel with smId. Can
	 * be changed to point to a different submodel in the database.
	 * 
	 * @param identificationId
	 */
	public void setSubmodelId(String identificationId) {
		this.identificationId = identificationId;
	}

	/**
	 * Depending on whether the model is already in the db, this method inserts or
	 * replaces the existing data. The new submodel id for this API is taken from
	 * the given submodel.
	 * 
	 * @param submodel
	 */
	public void setSubmodel(Submodel submodel) {
		String submodelId = submodel.getIdentification().getId();
		setSubmodelId(submodelId);
		storageApi.update(submodel, submodelId);
	}

	@Override
	public ISubmodel getSubmodel() {
		return storageApi.retrieve(identificationId);
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		Submodel submodel = (Submodel) getSubmodel();
		submodel.addSubmodelElement(elem);
		storageApi.update(submodel, identificationId);
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		List<String> idShorts = idShortsPathAsList(idShortPath);
		addNestedSubmodelElement(idShorts, elem);
	}

	private void addNestedSubmodelElement(List<String> idShorts, ISubmodelElement element) {
		Submodel submodel = (Submodel) getSubmodel();
		if (idShorts.size() > 1) {
			idShorts = removeLastElement(idShorts);
			ISubmodelElement parentElement = getNestedSubmodelElement(submodel, idShorts);
			if (parentElement instanceof SubmodelElementCollection) {
				addToSubmodelElementCollection(element, submodel, parentElement);
			}
		} else {
			submodel.addSubmodelElement(element);
			storageApi.update(submodel, identificationId);
		}
	}

	private <T> List<T> removeLastElement(List<T> list) {
		return list.subList(0, list.size() - 1);
	}

	private void addToSubmodelElementCollection(ISubmodelElement element, Submodel submodel, ISubmodelElement parentElement) {
		((SubmodelElementCollection) parentElement).addSubmodelElement(element);
		submodel.addSubmodelElement(parentElement);
		storageApi.update(submodel, identificationId);
	}

	private ISubmodelElement getNestedSubmodelElement(Submodel submodel, List<String> idShorts) {
		Map<String, ISubmodelElement> elementMap = submodel.getSubmodelElements();
		Map<String, ISubmodelElement> resolvedElementMap = resolveElementPath(idShorts, elementMap);

		String lastIdShort = idShorts.get(idShorts.size() - 1);
		if (!resolvedElementMap.containsKey(lastIdShort)) {
			throwPathCouldNotBeResolvedException(lastIdShort);
		}

		return resolvedElementMap.get(lastIdShort);
	}

	private Map<String, ISubmodelElement> resolveElementPath(List<String> idShorts, Map<String, ISubmodelElement> elementMap) {
		var elementMapWrapper = new Object() {
			Map<String, ISubmodelElement> wrappedElementMap = elementMap;
		};

		String lastIdShort = idShorts.get(idShorts.size() - 1);
		idShorts.stream().takeWhile(idShort -> !lastIdShort.equals(idShort)).forEachOrdered(idShort -> {
			ISubmodelElement element = elementMapWrapper.wrappedElementMap.get(idShort);
			if (element instanceof SubmodelElementCollection) {
				elementMapWrapper.wrappedElementMap = ((SubmodelElementCollection) element).getSubmodelElements();
			} else {
				throwPathCouldNotBeResolvedException(idShort);
			}
		});
		return elementMapWrapper.wrappedElementMap;
	}

	private void throwPathCouldNotBeResolvedException(String idShort) {
		throw new ResourceNotFoundException(idShort + " in the nested submodel element path could not be resolved.");
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		List<String> idShorts = idShortsPathAsList(idShortPath);
		ISubmodelElement submodelElement = getNestedSubmodelElement(idShorts);
		return convertSubmodelElement(submodelElement);
	}

	private ISubmodelElement getNestedSubmodelElement(List<String> idShorts) {
		Submodel submodel = (Submodel) getSubmodel();
		return getNestedSubmodelElement(submodel, idShorts);
	}

	@SuppressWarnings("unchecked")
	private ISubmodelElement convertSubmodelElement(ISubmodelElement element) {
		Map<String, Object> convertedCollectionMap = SubmodelElementMapCollectionConverter.smElementToMap((Map<String, Object>) element);
		return SubmodelElement.createAsFacade(convertedCollectionMap);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		if (idShortPath.contains("/")) {
			List<String> idShorts = idShortsPathAsList(idShortPath);
			deleteNestedSubmodelElement(idShorts);
		} else {
			deleteTopLevelSubmodelElement(idShortPath);
		}

	}

	private void deleteNestedSubmodelElement(List<String> idShorts) {
		if (idShorts.size() == 1) {
			deleteSubmodelElement(idShorts.get(0));
			return;
		}
		Submodel submodel = (Submodel) getSubmodel();
		List<String> parentIdShorts = removeLastElement(idShorts);
		ISubmodelElement parentElement = getNestedSubmodelElement(submodel, parentIdShorts);
		SubmodelElementCollection elementCollection = (SubmodelElementCollection) parentElement;
		elementCollection.deleteSubmodelElement(idShorts.get(idShorts.size() - 1));
		storageApi.update(submodel, identificationId);
	}

	private void deleteTopLevelSubmodelElement(String idShort) {
		Submodel submodel = (Submodel) getSubmodel();
		storageApi.deleteFile(submodel, idShort);
		submodel.getSubmodelElements().remove(idShort);
		storageApi.update(submodel, identificationId);
	}

	@Override
	public Collection<IOperation> getOperations() {
		Submodel submodel = (Submodel) getSubmodel();
		return submodel.getOperations().values();
	}

	@Override
	public Collection<ISubmodelElement> getSubmodelElements() {
		Submodel submodel = (Submodel) getSubmodel();
		return submodel.getSubmodelElements().values();
	}

	@Override
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		List<String> idShorts = idShortsPathAsList(idShortPath);
		updateSubmodelElement(idShorts, newValue);
	}

	@SuppressWarnings("unchecked")
	private void updateSubmodelElement(List<String> idShorts, Object newValue) {
		Submodel submodel = (Submodel) getSubmodel();
		ISubmodelElement element = getNestedSubmodelElement(submodel, idShorts);

		IModelProvider mapProvider = new VABLambdaProvider((Map<String, Object>) element);
		IModelProvider smeProvider = new SubmodelElementProvider(mapProvider);

		smeProvider.setValue(Property.VALUE, newValue);
		ISubmodelElement updatedLastLevelElement = SubmodelElementFacadeFactory.createSubmodelElement((Map<String, Object>) smeProvider.getValue(""));
		ISubmodelElement updatedNestedElement = createUpdatedNestedSubmodelElement(submodel, updatedLastLevelElement, idShorts);

		submodel.addSubmodelElement(updatedNestedElement);

		storageApi.update(submodel, identificationId);
	}

	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		if (idShortPath.contains("/")) {
			List<String> idShorts = idShortsPathAsList(idShortPath);
			return getNestedSubmodelElementValue(idShorts);
		} else {
			return getTopLevelSubmodelElementValue(idShortPath);
		}
	}

	private Object getTopLevelSubmodelElementValue(String idShort) {
		Submodel submodel = (Submodel) getSubmodel();
		return getElementProvider(submodel, idShort).getValue("/value");
	}

	@SuppressWarnings("unchecked")
	private IModelProvider getElementProvider(Submodel submodel, String idShortPath) {
		ISubmodelElement elem = submodel.getSubmodelElement(idShortPath);
		IModelProvider mapProvider = new VABMapProvider((Map<String, Object>) elem);
		return new SubmodelElementProvider(mapProvider);
	}

	@SuppressWarnings("unchecked")
	private Object getNestedSubmodelElementValue(List<String> idShorts) {
		ISubmodelElement lastElement = getNestedSubmodelElement(idShorts);
		IModelProvider mapProvider = new VABMapProvider((Map<String, Object>) lastElement);
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
	@Override
	public Object invokeOperation(String idShortPath, Object... params) {
		Operation operation = (Operation) SubmodelElementFacadeFactory.createSubmodelElement((Map<String, Object>) getSubmodelElement(idShortPath));
		if (!DelegatedInvocationManager.isDelegatingOperation(operation)) {
			throw new MalformedRequestException("This backend supports only delegating operations.");
		}
		return invocationManager.invokeDelegatedOperation(operation, params);
	}

	@Override
	public Object invokeAsync(String idShortPath, Object... params) {
		List<String> idShorts = idShortsPathAsList(idShortPath);
		return invokeNestedOperationAsync(idShorts, params);
	}

	private List<String> idShortsPathAsList(String idShortPath) {
		String[] splittedIdShortPath = VABPathTools.splitPath(idShortPath);
		return Arrays.asList(splittedIdShortPath);
	}

	private Object invokeNestedOperationAsync(List<String> idShorts, Object... params) {
		throw new MalformedRequestException("Invoke not supported by this backend");
	}

	@Override
	public Object getOperationResult(String idShort, String requestId) {
		throw new MalformedRequestException("Invoke not supported by this backend");
	}

	private ISubmodelElement createUpdatedNestedSubmodelElement(Submodel sm, ISubmodelElement updatedLastLevelElement, List<String> idShorts) {
		ISubmodelElement updatedNestedElement = updatedLastLevelElement;
		for (int i = idShorts.size() - 1; i > 0; i--) {
			idShorts = idShorts.subList(0, i);
			ISubmodelElementCollection nextLevelElementCollection = (ISubmodelElementCollection) getNestedSubmodelElement(sm, idShorts);
			nextLevelElementCollection.addSubmodelElement(updatedNestedElement);
			updatedNestedElement = nextLevelElementCollection;
		}

		return updatedNestedElement;
	}

	@SuppressWarnings("unchecked")
	@Override
	public java.io.File getSubmodelElementFile(String idShortPath) {
		Map<String, Object> submodelElement = (Map<String, Object>) getSubmodelElement(idShortPath);
		String parentKey = getSubmodel().getIdentification().getId();
		return storageApi.getFile(idShortPath, parentKey, submodelElement);
	}

	@Override
	public void uploadSubmodelElementFile(String idShortPath, InputStream fileStream) {
		String[] splitted = VABPathTools.splitPath(idShortPath);
		List<String> idShorts = Arrays.asList(splitted);
		Submodel sm = (Submodel) getSubmodel();
		ISubmodelElement element = getNestedSubmodelElement(sm, idShorts);
		String fileName = storageApi.writeFile(idShortPath, getSubmodel().getIdentification().getId(), fileStream, element);
		updateSubmodelElement(idShorts, fileName);
	}

}
