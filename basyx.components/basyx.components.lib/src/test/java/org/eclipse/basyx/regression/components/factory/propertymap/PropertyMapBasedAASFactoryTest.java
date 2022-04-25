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

package org.eclipse.basyx.regression.components.factory.propertymap;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.components.factory.propertymap.PropertyMapBasedAASFactory;
import org.eclipse.basyx.components.factory.propertymap.PropertyMapConstants;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.junit.Test;

/**
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedAASFactoryTest {

	@Test
	public void testAASCreation() {
		String aas1IdShort = "aas1";
		String asset1Id = "asset1";
		String sm1Id = "sm1";

		Asset expectedAsset = buildAsset(asset1Id);
		Submodel expectedSubmodel = buildSubmodel(sm1Id);
		AssetAdministrationShell expected = buildAAS(aas1IdShort, expectedAsset, expectedSubmodel);
		Map<String, String> aasMap = buildAASMap(aas1IdShort, asset1Id, sm1Id);

		AssetAdministrationShell actual = new PropertyMapBasedAASFactory(buildSubmodelMapWithDummySubmodel(expectedSubmodel), buildAssetCollectionWithDummyAsset(expectedAsset)).create(aasMap);

		assertEquals(expected, actual);
	}

	/**
	 * Builds a Map containing an AAS's attributes
	 * 
	 * @param aasId
	 * @param assetId
	 * @param sm1Id
	 * @return
	 */
	public static Map<String, String> buildAASMap(String aasId, String assetId, String sm1Id) {
		Map<String, String> aasMap = new HashMap<>();
		aasMap.put(PropertyMapConstants.IDVALUE, aasId);
		aasMap.put(PropertyMapConstants.IDSHORT, getIdShortFromId(aasId));
		aasMap.put(PropertyMapBasedAASFactory.ASSET, assetId);
		aasMap.put(PropertyMapBasedAASFactory.SUBMODELS, sm1Id);

		return aasMap;
	}

	private static Submodel buildSubmodel(String sm1Id) {
		return new Submodel(getIdShortFromId(sm1Id), new CustomId(sm1Id));
	}

	private static Asset buildAsset(String asset1Id) {
		return new Asset(getIdShortFromId(asset1Id), new CustomId(asset1Id), AssetKind.INSTANCE);
	}

	private static AssetAdministrationShell buildAAS(String id, Asset asset, Submodel sm) {
		AssetAdministrationShell shell = new AssetAdministrationShell(getIdShortFromId(id), new CustomId(id), asset);
		shell.addSubmodelReference(sm.getReference());
		return shell;
	}

	private static String getIdShortFromId(String id) {
		return id + "IdShort";
	}

	private static Map<String, Asset> buildAssetCollectionWithDummyAsset(Asset expected) {
		String dummyId = "dummyAsset";
		Asset dummy = buildAsset(dummyId);

		Map<String, Asset> assets = new HashMap<>();
		assets.put(dummyId, dummy);
		assets.put(expected.getIdentification().getId(), expected);

		return assets;
	}

	private static Map<String, Submodel> buildSubmodelMapWithDummySubmodel(Submodel submodel) {
		String dummyId = "dummySubmodel";
		Submodel dummy = buildSubmodel(dummyId);
		Map<String, Submodel> submodels = new HashMap<>();
		submodels.put(dummyId, dummy);
		submodels.put(submodel.getIdentification().getId(), submodel);

		return submodels;
	}

}
