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


package org.eclipse.basyx.regression.AASServer.feature;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.components.aas.fileadaptation.FileValueAdaptingSubmodelAPI;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.restapi.MultiSubmodelElementProvider;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.junit.Test;

/**
 * 
 * @author schnicke
 *
 */
public class TestFileValueAdaptingSubmodelAPI {

	@Test
	public void valueIsAdapted() throws FileNotFoundException, IOException {
		String fileIdShort = "fileSME";
		String submodelUrl = "http://test/submodel";

		Submodel submodel = createSubmodelWithFileSubmodelElement(fileIdShort);
		ISubmodelAPI adaptingAPI = createURLAdaptingAPI(submodelUrl, submodel);
		
		byte[] expectedFileContent = new byte[] { 0, 1, 2, 3 };
		adaptingAPI.uploadSubmodelElementFile(fileIdShort, new ByteArrayInputStream(expectedFileContent));

		assertValueIsAdapted(adaptingAPI, fileIdShort, submodelUrl);
	}

	private void assertValueIsAdapted(ISubmodelAPI adaptingAPI, String fileIdShort, String url) {
		String path = (String) adaptingAPI.getSubmodelElementValue(fileIdShort);
		String expectedPath = VABPathTools.concatenatePaths(url, MultiSubmodelElementProvider.ELEMENTS, fileIdShort, SubmodelProvider.FILE);

		assertEquals(expectedPath, path);
	}

	private ISubmodelAPI createURLAdaptingAPI(String url, Submodel submodel) {
		ISubmodelAPI inMemoryAPI = new VABSubmodelAPIFactory().create(submodel);
		ISubmodelAPI adaptingAPI = new FileValueAdaptingSubmodelAPI(inMemoryAPI, url);
		return adaptingAPI;
	}

	private Submodel createSubmodelWithFileSubmodelElement(String fileIdShort) {
		Submodel sm = new Submodel("idShort", new CustomId("id"));
		File file = new File("application/json");
		file.setIdShort(fileIdShort);
		sm.addSubmodelElement(file);
		return sm;
	}
}
