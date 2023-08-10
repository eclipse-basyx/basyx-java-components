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
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.facade.submodelelement.SubmodelElementFacadeFactory;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.operation.DelegatedInvocationManager;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;

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
	public Submodel getSubmodel() {
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
		VABSubmodelAPI api = new VABSubmodelAPI(new VABLambdaProvider(getSubmodel()));
		api.addSubmodelElement(idShortPath, elem);
		storageApi.update(api.getSubmodel(), identificationId);
	}


	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		VABSubmodelAPI api = new VABSubmodelAPI(new VABLambdaProvider(getSubmodel()));
		return api.getSubmodelElement(idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		VABSubmodelAPI api = new VABSubmodelAPI(new VABLambdaProvider(getSubmodel()));
		api.deleteSubmodelElement(idShortPath);
		storageApi.update(api.getSubmodel(), identificationId);
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
		VABSubmodelAPI api = new VABSubmodelAPI(new VABLambdaProvider(getSubmodel()));
		api.updateSubmodelElement(idShortPath, newValue);
		storageApi.update(api.getSubmodel(), identificationId);
	}


	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		VABSubmodelAPI api = new VABSubmodelAPI(new VABLambdaProvider(getSubmodel()));
		return api.getSubmodelElementValue(idShortPath);
	}

	@Deprecated
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

	@SuppressWarnings("unchecked")
	@Override
	public java.io.File getSubmodelElementFile(String idShortPath) {
		Map<String, Object> submodelElement = (Map<String, Object>) getSubmodelElement(idShortPath);
		String parentKey = getSubmodel().getIdentification().getId();
		return storageApi.getFile(idShortPath, parentKey, submodelElement);
	}

	@Override
	public void uploadSubmodelElementFile(String idShortPath, InputStream fileStream) {
		VABSubmodelAPI api = new VABSubmodelAPI(new VABLambdaProvider(getSubmodel()));
		ISubmodelElement element = api.getSubmodelElement(idShortPath);
		String fileName = storageApi.writeFile(idShortPath, getSubmodel().getIdentification().getId(), fileStream, element);
		updateSubmodelElement(idShortPath, fileName);
	}

}
