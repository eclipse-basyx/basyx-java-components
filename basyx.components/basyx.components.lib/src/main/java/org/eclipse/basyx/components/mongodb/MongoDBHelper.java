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

package org.eclipse.basyx.components.mongodb;

import java.util.Map;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.restapi.SubmodelElementProvider;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;

public class MongoDBHelper {
	private MongoDBHelper() {
	}

	/**
	 * Construct the file name for storage
	 * 
	 * @param submodelId
	 * @param file
	 * @param idShortPath
	 */
	public static String constructFileName(String submodelId, File file, String idShortPath) {
		return submodelId + "-" + idShortPath.replaceAll("/", "-") + getFileExtension(file);
	}

	private static String getFileExtension(File file) {
		MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
		try {
			MimeType mimeType = allTypes.forName(file.getMimeType());
			return mimeType.getExtension();
		} catch (MimeTypeException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static ISubmodelElement getTopLevelSubmodelElement(Submodel sm, String idShort) {
		Map<String, ISubmodelElement> submodelElements = sm.getSubmodelElements();
		ISubmodelElement element = submodelElements.get(idShort);
		if (element == null) {
			throw new ResourceNotFoundException("The element \"" + idShort + "\" could not be found");
		}
		return convertSubmodelElement(element);
	}

	@SuppressWarnings("unchecked")
	public static ISubmodelElement convertSubmodelElement(ISubmodelElement element) {
		// FIXME: Convert internal data structure of ISubmodelElement
		Map<String, Object> elementMap = (Map<String, Object>) element;
		IModelProvider elementProvider = new SubmodelElementProvider(new VABMapProvider(elementMap));
		Object elementVABObj = elementProvider.getValue("");
		return SubmodelElement.createAsFacade((Map<String, Object>) elementVABObj);
	}

	public static Object getTopLevelSubmodelElementValue(Submodel sm, String idShort) {
		return getElementProvider(sm, idShort).getValue("/value");
	}

	@SuppressWarnings("unchecked")
	private static SubmodelElementProvider getElementProvider(Submodel sm, String idShort) {
		ISubmodelElement elem = sm.getSubmodelElement(idShort);
		IModelProvider mapProvider = new VABMapProvider((Map<String, Object>) elem);
		return new SubmodelElementProvider(mapProvider);
	}

}
