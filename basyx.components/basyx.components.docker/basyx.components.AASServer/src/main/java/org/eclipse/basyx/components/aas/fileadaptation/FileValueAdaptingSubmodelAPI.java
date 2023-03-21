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


package org.eclipse.basyx.components.aas.fileadaptation;

import java.io.InputStream;
import java.util.Collection;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.restapi.MultiSubmodelElementProvider;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * Submodel API that adapts a File's value after uploading a new file to point
 * to the download endpoint
 * 
 * @author schnicke
 *
 */
public class FileValueAdaptingSubmodelAPI implements ISubmodelAPI {
	private ISubmodelAPI decorated;

	private String submodelURL;

	public FileValueAdaptingSubmodelAPI(ISubmodelAPI decorated, String submodelUrl) {
		this.decorated = decorated;
		this.submodelURL = submodelUrl;
	}

	@Override
	public ISubmodel getSubmodel() {
		return decorated.getSubmodel();
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		decorated.addSubmodelElement(elem);
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		decorated.addSubmodelElement(idShortPath, elem);
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		return decorated.getSubmodelElement(idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		decorated.deleteSubmodelElement(idShortPath);
	}

	@Override
	public Collection<IOperation> getOperations() {
		return decorated.getOperations();
	}

	@Override
	public Collection<ISubmodelElement> getSubmodelElements() {
		return decorated.getSubmodelElements();
	}

	@Override
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		decorated.updateSubmodelElement(idShortPath, newValue);
	}

	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		return decorated.getSubmodelElementValue(idShortPath);
	}

	@Override
	public Object invokeOperation(String idShortPath, Object... params) {
		return decorated.invokeOperation(idShortPath, params);
	}

	@Override
	public Object invokeAsync(String idShortPath, Object... params) {
		return decorated.invokeAsync(idShortPath, params);
	}

	@Override
	public Object getOperationResult(String idShort, String requestId) {
		return decorated.getOperationResult(idShort, requestId);
	}

	@Override
	public java.io.File getSubmodelElementFile(String idShortPath) {
		return decorated.getSubmodelElementFile(idShortPath);
	}

	@Override
	public void uploadSubmodelElementFile(String idShortPath, InputStream fileStream) {
		decorated.uploadSubmodelElementFile(idShortPath, fileStream);
		decorated.updateSubmodelElement(idShortPath, getURL(idShortPath));
	}

	private Object getURL(String idShortPath) {
		return VABPathTools.concatenatePaths(submodelURL, MultiSubmodelElementProvider.ELEMENTS, idShortPath, SubmodelProvider.FILE);
	}

}
