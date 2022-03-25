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
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.bundle.AASBundleFactory;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.components.propertymap.PropertyMapHelper;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

/**
 * Factory enabling List of {@link AASBundle}s creation from a property map
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedAASBundlesFactory {

	public static final String SHELLS = "shells";
	public static final String ASSETS = "assets";
	public static final String SUBMODELS = "submodels";
	public static final String PROPERTIES = "properties";

	private PropertyMapBasedAssetFactory assetFactory = new PropertyMapBasedAssetFactory();
	private PropertyMapBasedPropertyFactory propertyFactory = new PropertyMapBasedPropertyFactory();

	/**
	 * Creates a List of {@link AASBundle}s based on the data contained in the
	 * property map<br>
	 * <br>
	 * Example: <br>
	 * 
	 * <pre>
	 * {
	 * "shells": "AAS1, AAS2",
	 * "assets": "ASSET1, ASSET2",
	 * "submodels": "SM1, SM2",
	 * "properties": "PROP1, PROP2, PROP3",
	 * "AAS1": {
	 * 	"id": "AAS1Id",
	 * 	"idShort": "AAS1IdShort",
	 * 	"asset": "ASSET1",
	 * 	"submodels": "SM1"
	 * },
	 * "AAS2": {
	 * 	"id": "AAS2Id",
	 * 	"idShort": "AAS2IdShort",
	 * 	"asset": "ASSET2",
	 * 	"submodels": "SM3"
	 * },
	 * "ASSET1": {
	 * 	"id": "Asset1Id",
	 * 	"idShort": "Asset1IdShort"
	 * },
	 * "ASSET2": {
	 * 	"id": "Asset2Id",
	 * 	"idShort": "Asset2IdShort"
	 * },
	 * "SM1": {
	 * 	"id": "SM1Id",
	 * 	"idShort": "SM1IdShort",
	 * 	"properties": "PROP1, PROP2"
	 * },
	 * "SM2": {
	 * 	"id": "SM2Id",
	 * 	"idShort": "SM2IdShort",
	 * 	"properties": "PROP3"
	 * },
	 * "PROP1": {
	 * 	"idShort": "Prop1",
	 * 	"value": "Hello"
	 * },
	 * "PROP2": {
	 * 	"idShort": "Prop2",
	 * 	"value": "123"
	 * }
	 * </pre>
	 * 
	 * @param properties
	 * @return
	 */
	public Set<AASBundle> create(Map<String, Object> properties) {
		Map<String, Asset> assets = getAssets(properties);
		Map<String, Property> submodelProperties = getSubmodelProperties(properties);
		Map<String, Submodel> submodels = getSubmodels(properties, submodelProperties);
		List<AssetAdministrationShell> shells = getAssetAdministrationShells(properties, submodels, assets);

		return new AASBundleFactory().create(shells, submodels.values(), assets.values());
	}

	private static List<AssetAdministrationShell> getAssetAdministrationShells(Map<String, Object> properties, Map<String, Submodel> submodels, Map<String, Asset> assets) {
		PropertyMapBasedAASFactory aasFactory = new PropertyMapBasedAASFactory(submodels, assets);
		return getAASIds(properties).stream().map(id -> getAASMap(id, properties)).map(aasFactory::create).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private static Map<String, String> getAASMap(String id, Map<String, Object> properties) {
		return (Map<String, String>) properties.get(id);
	}

	private static Map<String, Submodel> getSubmodels(Map<String, Object> properties, Map<String, Property> submodelProperties) {
		Map<String, Submodel> ret = new HashMap<>();
		PropertyMapBasedSubmodelFactory submodelFactory = new PropertyMapBasedSubmodelFactory(submodelProperties);
		for (String id : getSubmodelIds(properties)) {
			Submodel submodel = getSubmodel(properties, submodelFactory, id);
			ret.put(id, submodel);
		}

		return ret;
	}

	private static Submodel getSubmodel(Map<String, Object> properties, PropertyMapBasedSubmodelFactory submodelFactory, String id) {
		Map<String, String> submodelMap = getSubmodelMap(id, properties);
		Submodel submodel = submodelFactory.create(submodelMap);
		return submodel;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, String> getSubmodelMap(String id, Map<String, Object> properties) {
		return (Map<String, String>) properties.get(id);
	}

	private static List<String> getSubmodelIds(Map<String, Object> properties) {
		String submodelIdList = getSubmodelStringList(properties);
		return PropertyMapHelper.getListFromStringList(submodelIdList);
	}

	private static String getSubmodelStringList(Map<String, Object> properties) {
		return (String) properties.get(SUBMODELS);
	}

	private Map<String, Property> getSubmodelProperties(Map<String, Object> properties) {
		Map<String, Property> ret = new HashMap<>();
		for (String id : getPropertyIds(properties)) {
			ret.put(id, getProperty(id, properties));
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	private Property getProperty(String id, Map<String, Object> properties) {
		Map<String, String> propertyMap = (Map<String, String>) properties.get(id);
		return propertyFactory.create(propertyMap);
	}

	private static String getPropertyIdString(Map<String, Object> properties) {
		return (String) properties.get(PROPERTIES);
	}

	private Map<String, Asset> getAssets(Map<String, Object> properties) {
		Map<String, Asset> ret = new HashMap<>();
		for (String id : getAssetIds(properties)) {
			ret.put(id, getAsset(id, properties));
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	private Asset getAsset(String id, Map<String, Object> properties) {
		Map<String, String> assetMap = (Map<String, String>) properties.get(id);
		return assetFactory.create(assetMap);
	}

	private static List<String> getAssetIds(Map<String, Object> properties) {
		String assetStringList = getAssetIdString(properties);
		return PropertyMapHelper.getListFromStringList(assetStringList);
	}

	private static String getAssetIdString(Map<String, Object> properties) {
		return (String) properties.get(ASSETS);
	}

	private static List<String> getAASIds(Map<String, Object> properties) {
		String aasStringList = getAASIdString(properties);
		return PropertyMapHelper.getListFromStringList(aasStringList);
	}

	private static List<String> getPropertyIds(Map<String, Object> properties) {
		String propertyStringList = getPropertyIdString(properties);
		return PropertyMapHelper.getListFromStringList(propertyStringList);
	}

	private static String getAASIdString(Map<String, Object> properties) {
		return (String) properties.get(SHELLS);
	}

}
