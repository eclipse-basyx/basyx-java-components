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
package org.eclipse.basyx.tools.aas.active;

import java.io.Serializable;
import java.util.function.Supplier;

import org.eclipse.basyx.tools.webserviceclient.WebServiceRawClient;

/**
 * Implement a getter function that queries the value from a HTTP server.
 * Expects a string response.
 * 
 * @author kuhn
 *
 */
public class HTTPGetter implements Supplier<Object>, Serializable {

	/**
	 * Version number of serialized instances
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * URL of server that provides the requested information
	 */
	protected String serverURL = null;

	/**
	 * Constructor
	 */
	public HTTPGetter(String url) {
		// Store URL
		serverURL = url;
	}

	/**
	 * Return value
	 */
	@Override
	public Object get() {
		// Create web service client
		WebServiceRawClient rawClient = new WebServiceRawClient();

		// Delegate call to WebService RAW client
		return rawClient.get(serverURL);
	}
}
