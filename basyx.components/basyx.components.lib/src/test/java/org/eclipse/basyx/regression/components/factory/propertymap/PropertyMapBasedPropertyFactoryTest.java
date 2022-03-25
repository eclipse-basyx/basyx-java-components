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

import org.eclipse.basyx.components.factory.propertymap.PropertyMapBasedPropertyFactory;
import org.eclipse.basyx.components.factory.propertymap.PropertyMapConstants;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.junit.Test;

/**
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedPropertyFactoryTest {

	@Test
	public void testPropertyCreation() {
		String propIdShort = "Prop1";
		String propValue = "Test123";

		Property expected = buildProperty(propIdShort, propValue);
		Map<String, String> propertyMap = buildPropertyMap(propIdShort, propValue);
		Property actual = new PropertyMapBasedPropertyFactory().create(propertyMap);

		assertEquals(expected, actual);
	}

	/**
	 * Builds a Map containing a Property's attributes
	 * 
	 * @param idShort
	 * @param value
	 * @return
	 */
	public static Map<String, String> buildPropertyMap(String idShort, String value) {
		Map<String, String> propertyMap = new HashMap<>();
		propertyMap.put(PropertyMapConstants.IDSHORT, idShort);
		propertyMap.put(PropertyMapBasedPropertyFactory.VALUE, value);
		return propertyMap;
	}

	private static Property buildProperty(String idShort, Object value) {
		return new Property(idShort, value);
	}
}
