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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.components.factory.propertymap.PropertyMapBasedSubmodelFactory;
import org.eclipse.basyx.components.factory.propertymap.PropertyMapConstants;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.junit.Test;

/**
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedSubmodelFactoryTest {

	@Test
	public void testSubmodelCreation() {
		Property prop1 = new Property("prop1", "StringValue");
		Property prop2 = new Property("prop2", "123");
		String smId = "SM1";
		String smIdShort = "submodel1IdShort";

		List<Property> properties = Arrays.asList(prop1, prop2);
		Map<String, Property> propertyMap = getPropertyMap(properties);
		Submodel expected = buildSubmodel(smIdShort, smId, properties);
		Map<String, String> smMap = buildSubmodelMap(smIdShort, smId, getPropertyEntry(prop1, prop2));

		Submodel actual = new PropertyMapBasedSubmodelFactory(propertyMap).create(smMap);

		assertEquals(expected, actual);
	}

	/**
	 * Builds a Map containing a Submodel's attributes
	 * 
	 * @param idShort
	 * @param id
	 * @param properties
	 * @return
	 */
	public static Map<String, String> buildSubmodelMap(String idShort, String id, String properties) {
		Map<String, String> smMap = new HashMap<>();
		smMap.put(PropertyMapConstants.IDSHORT, idShort);
		smMap.put(PropertyMapConstants.IDVALUE, id);
		smMap.put(PropertyMapBasedSubmodelFactory.PROPERTIES, properties);

		return smMap;
	}

	private static Map<String, Property> getPropertyMap(List<Property> properties) {
		Map<String, Property> propertyMap = new HashMap<>();
		properties.forEach(p -> propertyMap.put(p.getIdShort(), p));
		return propertyMap;
	}

	private static String getPropertyEntry(Property prop1, Property prop2) {
		return prop1.getIdShort() + ", " + prop2.getIdShort();
	}

	private static Submodel buildSubmodel(String idShort, String id, List<Property> properties) {
		Submodel sm = new Submodel(idShort, new CustomId(id));
		properties.forEach(p -> sm.addSubmodelElement(p));
		return sm;
	}

}
