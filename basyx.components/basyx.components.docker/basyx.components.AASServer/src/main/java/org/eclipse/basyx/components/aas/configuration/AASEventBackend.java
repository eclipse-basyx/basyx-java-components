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
package org.eclipse.basyx.components.aas.configuration;

import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;

import com.google.common.base.Strings;

/**
 * Possible types for AAS event backends.
 * 
 * @author espen
 *
 */
public enum AASEventBackend {
	NONE("NONE"), MQTT("MQTT");

	private String literal;

	private AASEventBackend(String literal) {
		this.literal = literal;
	}

	@Override
	public String toString() {
		return literal;
	}

	/**
	 * Method to transform string literal to AAS event enum.
	 * 
	 * @see StandardizedLiteralEnumHelper StandardizedLiteralEnumHelper
	 * 
	 * @param literal
	 * @return
	 */
	public static AASEventBackend fromString(String literal) {
		if (Strings.isNullOrEmpty(literal)) {
			return null;
		}

		AASEventBackend[] enumConstants = AASEventBackend.class.getEnumConstants();
		for (AASEventBackend constant : enumConstants) {
			if (constant.toString().equals(literal)) {
				return constant;
			}
		}
		throw new IllegalArgumentException("The literal '" + literal + "' is not a valid EventBackend");
	}
}
