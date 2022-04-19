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
package org.eclipse.basyx.tools.webserviceclient;

import java.io.Serializable;

import javax.ws.rs.ServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.HttpUrlConnectorProvider;

/**
 * Helper class that supports invocation of remote web services. The class sets
 * up JSON parameter types for input and output parameter, but does not
 * implement the coding.
 * 
 * @author kuhn
 *
 */
public class WebServiceRawClient implements Serializable {

	/**
	 * Version of serialized instances
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Web service client instance for invoking service calls via web services
	 */
	protected Client client = ClientBuilder.newClient();

	/**
	 * Execute a web service, return JSON string
	 */
	protected Builder buildRequest(Client client, String wsURL) {
		// Called URL
		WebTarget resource = client.target(wsURL);

		// Build request, set JSON encoding
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);

		// Return JSON request
		return request;
	}

	/**
	 * Execute a web service, return deserialized object
	 */
	public String get(String wsURL) {
		// Build web service URL
		Builder request = buildRequest(client, wsURL);

		// Perform request, return response
		String result = request.get(String.class);

		// Return result
		return result;
	}

	/**
	 * Execute a web service put operation, return JSON string
	 */
	public String put(String wsURL, String jsonParameter) {
		// Build web service URL
		Builder request = buildRequest(client, wsURL);

		// Perform request
		Response rsp = request.put(Entity.entity(jsonParameter.toString(), MediaType.APPLICATION_JSON));

		// Throw exception that indicates an error
		if (!((rsp.getStatus() == 0) || (rsp.getStatus() == 200) || (rsp.getStatus() == 201)))
			throw new ServerErrorException(rsp);

		// Return result
		return rsp.readEntity(String.class);
	}

	/**
	 * Execute a web service post operation, return JSON string
	 */
	public String post(String wsURL, String jsonParameter) {
		// Build web service URL
		Builder request = buildRequest(client, wsURL);

		// Perform request
		Response rsp = request.post(Entity.entity(jsonParameter, MediaType.APPLICATION_JSON));

		// Throw exception that indicates an error
		if (!((rsp.getStatus() == 0) || (rsp.getStatus() == 200) || (rsp.getStatus() == 201)))
			throw new ServerErrorException(rsp);

		// Return result
		return rsp.readEntity(String.class);
	}

	/**
	 * Execute a web service post operation, return JSON string
	 */
	public String patch(String wsURL, String action, String jsonParameter) {
		// Build and perform patch request
		Response rsp = client.target(wsURL).queryParam("action", action).request().build("PATCH", Entity.text(jsonParameter.toString())).property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true).invoke();

		// Throw exception that indicates an error
		if (!((rsp.getStatus() == 0) || (rsp.getStatus() == 200) || (rsp.getStatus() == 201)))
			throw new ServerErrorException(rsp);

		// Return result
		return rsp.readEntity(String.class);
	}

	/**
	 * Execute a web service delete operation, return JSON string
	 */
	public String delete(String wsURL) {
		// Build web service URL
		Builder request = buildRequest(client, wsURL);

		// Perform request
		String result = request.delete(String.class);

		// Return result
		return result;
	}

}
