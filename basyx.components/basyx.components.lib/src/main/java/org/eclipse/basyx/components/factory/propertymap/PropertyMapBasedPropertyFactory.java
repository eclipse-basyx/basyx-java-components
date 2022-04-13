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

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

/**
 * Factory enabling {@link Property} creation from a property map
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedPropertyFactory {
	public static final String VALUE = "value";

	/**
	 * Creates a {@link Property} based on the data contained in the property map.
	 * Right now, only String is supported as value <br>
	 * <br>
	 * Example:
	 * 
	 * <pre>
	 * { 
	 * 		"idShort": "propertyId", 
	 * 		"value": "TestValue"
	 * }
	 * </pre>
	 * 
	 * @param propertyMap
	 * @return
	 */
	public Property create(Map<String, String> propertyMap) {
		String idShort = PropertyMapConstantsHelper.getIdShort(propertyMap);
		Object value = getValue(propertyMap);
		return new Property(idShort, value);
	}

	private Object getValue(Map<String, String> propertyMap) {
		return propertyMap.get(VALUE);
	}

}
