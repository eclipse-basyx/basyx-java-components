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
package org.eclipse.basyx.components.configuration;

import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.basyx.connector.BaSyxConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;

/**
 * Enumerate supported BaSyx protocol types
 * 
 * @author kuhn
 *
 */
public enum CFGBaSyxProtocolType {

	/**
	 * HTTP protocol
	 */
	HTTP(),

	/**
	 * BaSyx protocol
	 */
	BASYX();

	/**
	 * Return BaSyx protocol type by value
	 */
	public static CFGBaSyxProtocolType byValue(String cfgKey) {
		// Parse configuration key
		switch (cfgKey.toLowerCase()) {
		// Parse known protocols
		case "http":
			return CFGBaSyxProtocolType.HTTP;
		case "basyx":
			return CFGBaSyxProtocolType.BASYX;

		// Unknown protocol
		default:
			return null;
		}
	}

	/**
	 * Create protocol instance
	 */
	public IConnectorFactory createInstance() {
		// Create protocol instance
		if (this.equals(HTTP))
			return new HTTPConnectorFactory();
		if (this.equals(BASYX))
			return new BaSyxConnectorFactory();

		// Unknown protocol
		return null;
	}
}
