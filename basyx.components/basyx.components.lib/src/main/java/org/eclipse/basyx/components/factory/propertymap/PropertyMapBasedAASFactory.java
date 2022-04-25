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

package org.eclipse.basyx.components.factory.propertymap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.components.propertymap.PropertyMapHelper;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;

/**
 * Factory enabling {@link AssetAdministrationShell} creation from a property
 * map
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedAASFactory {
	public static final String ASSET = "asset";
	public static final String SUBMODELS = "submodels";

	private Map<String, Asset> assetMap = new HashMap<>();
	private Map<String, Submodel> submodelMap = new HashMap<>();

	/**
	 * Creates a new factory instance using the passed map of Id to {@link Submodel}
	 * and {@link Asset}. The keys of these maps have to reflect the ids used in the
	 * AAS's property map.<br>
	 * <br>
	 * Example:
	 * 
	 * <pre>
	 * { 
	 * 		"id": "AASId",
	 * 		"idShort": "AASIdShort", 
	 * 		"value": "TestValue",
	 * 		"asset": "Asset",
	 * 		"submodels": "SM1, SM2"
	 * }
	 * </pre>
	 * 
	 * @param submodels
	 * @param assets
	 */
	public PropertyMapBasedAASFactory(Map<String, Submodel> submodels, Map<String, Asset> assets) {
		this.submodelMap = submodels;
		this.assetMap = assets;
	}

	/**
	 * Creates an {@link AssetAdministrationShell} based on the data contained in
	 * the property map
	 * 
	 * @param aasMap
	 * @return
	 */
	public AssetAdministrationShell create(Map<String, String> aasMap) {
		Asset asset = getAsset(aasMap);

		AssetAdministrationShell shell = createAASWithoutSubmodels(aasMap, asset);
		addSubmodelToAAS(aasMap, shell);

		return shell;
	}

	private void addSubmodelToAAS(Map<String, String> aasMap, AssetAdministrationShell shell) {
		List<Submodel> submodels = getSubmodels(aasMap);
		addSubmodelReferences(shell, submodels);
	}

	private static AssetAdministrationShell createAASWithoutSubmodels(Map<String, String> aasMap, Asset asset) {
		String aasId = PropertyMapConstantsHelper.getIdValue(aasMap);
		String aasIdShort = PropertyMapConstantsHelper.getIdShort(aasMap);
		AssetAdministrationShell shell = new AssetAdministrationShell(aasIdShort, new CustomId(aasId), asset);

		return shell;
	}

	private static void addSubmodelReferences(AssetAdministrationShell shell, List<Submodel> submodels) {
		submodels.stream().map(sm -> sm.getReference()).forEach(shell::addSubmodelReference);
	}

	private List<Submodel> getSubmodels(Map<String, String> aasMap) {
		return getSubmodelIds(aasMap).stream().map(id -> submodelMap.get(id)).collect(Collectors.toList());
	}

	private Asset getAsset(Map<String, String> aasMap) {
		String assetId = getAssetId(aasMap);

		return assetMap.get(assetId);
	}

	private static List<String> getSubmodelIds(Map<String, String> aasMap) {
		String submodels = aasMap.get(SUBMODELS);
		return PropertyMapHelper.getListFromStringList(submodels);
	}

	private static String getAssetId(Map<String, String> aasMap) {
		return aasMap.get(ASSET);
	}

}
