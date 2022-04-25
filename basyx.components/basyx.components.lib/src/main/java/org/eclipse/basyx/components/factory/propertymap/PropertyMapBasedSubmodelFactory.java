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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.components.propertymap.PropertyMapHelper;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

/**
 * Factory enabling {@link Submodel} creation from a property map
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedSubmodelFactory {

	public static final String PROPERTIES = "properties";

	private final Map<String, Property> propertyMap;

	/**
	 * Creates a new factory instance using the passed map of Id to
	 * {@link Property}. The keys of this map have to reflect the ids used in the
	 * Submodels's property map.
	 * 
	 * @param properties
	 */
	public PropertyMapBasedSubmodelFactory(Map<String, Property> properties) {
		this.propertyMap = properties;
	}

	/**
	 * Creates a {@link Submodel} based on the data contained in the property map.
	 * <br>
	 * <br>
	 * Example:
	 * 
	 * <pre>
	 * { 
	 * 		"id": "SMId", 
	 * 		"idShort": "SMIdShort", 
	 * 		"properties": "propertyId1, propertyId2" 
	 * }
	 * </pre>
	 * 
	 * @param submodelMap
	 * @return
	 */
	public Submodel create(Map<String, String> submodelMap) {
		String smId = PropertyMapConstantsHelper.getIdValue(submodelMap);
		String smIdShort = PropertyMapConstantsHelper.getIdShort(submodelMap);

		List<Property> properties = getProperties(submodelMap);

		Submodel sm = new Submodel(smIdShort, new CustomId(smId));
		properties.stream().forEach(p -> sm.addSubmodelElement(p));

		return sm;
	}

	private List<Property> getProperties(Map<String, String> smMap) {
		String propertyNames = getPropertyNames(smMap);
		List<String> propertyNamesList = getPropertyNamesAsList(propertyNames);
		return getPropertiesFromNameList(propertyNamesList);
	}

	private String getPropertyNames(Map<String, String> smMap) {
		return smMap.get(PROPERTIES);
	}

	private List<Property> getPropertiesFromNameList(List<String> propertyNames) {
		List<Property> ret = new ArrayList<>();
		for (String propName : propertyNames) {
			Property toAdd = getProperty(propName);
			ret.add(toAdd);
		}

		return ret;
	}

	private Property getProperty(String propName) throws RuntimeException {
		if (!propertyMap.containsKey(propName)) {
			throw new RuntimeException("Property with name " + propName + " could not be resolved");
		}

		return propertyMap.get(propName);
	}

	private List<String> getPropertyNamesAsList(String names) {
		return PropertyMapHelper.getListFromStringList(names);
	}

}
