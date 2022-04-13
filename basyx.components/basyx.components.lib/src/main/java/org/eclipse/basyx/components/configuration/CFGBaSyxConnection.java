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
import org.eclipse.basyx.vab.registry.api.IVABRegistryService;

/**
 * Configure a server connection
 * 
 * @author kuhn
 *
 */
public class CFGBaSyxConnection {

	/**
	 * Protocol type
	 */
	protected CFGBaSyxProtocolType protocol = null;

	/**
	 * Directory type for this connection
	 */
	protected String directoryProviderName = null;

	/**
	 * Constructor
	 */
	public CFGBaSyxConnection() {
		// Do nothing
	}

	/**
	 * Set protocol type
	 * 
	 * @return CFGBaSyxConnection to support builder pattern
	 */
	public CFGBaSyxConnection setProtocol(CFGBaSyxProtocolType proto) {
		// Store protocol type
		protocol = proto;

		// Return 'this' instance
		return this;
	}

	/**
	 * Set directory
	 * 
	 * @return CFGBaSyxConnection to support builder pattern
	 */
	public CFGBaSyxConnection setDirectoryProvider(String providerName) {
		// Store protocol type
		directoryProviderName = providerName;

		// Return 'this' instance
		return this;
	}

	/**
	 * Create protocol provider
	 */
	public IConnectorFactory createConnectorProvider() {
		// Create connector provider instance
		return protocol.createInstance();
	}

	/**
	 * Instantiate the directory class
	 */
	public IVABRegistryService createDirectoryInstance() {
		// Try to create instance
		try {
			// Get Java class by name
			Class<?> clazz = Class.forName(directoryProviderName);

			// Instantiate class
			IVABRegistryService directoryService = (IVABRegistryService) clazz.newInstance();

			// Return directory service instance
			return directoryService;
		} catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
			// this is more or less fatal, so just inform the user
			e.printStackTrace();
		}

		// Return null pointer
		return null;
	}
}
