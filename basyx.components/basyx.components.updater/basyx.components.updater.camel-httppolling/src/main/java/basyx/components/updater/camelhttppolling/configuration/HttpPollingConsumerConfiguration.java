/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.camelhttppolling.configuration;

import basyx.components.updater.core.configuration.entity.DataSinkConfiguration;

/**
 * An implementation of httppolling consumer configuration
 * @author n14s - Niklas Mertens
 *
 */
public class HttpPollingConsumerConfiguration extends DataSinkConfiguration {
    private String serverUrl;
    private int serverPort;
    private String query;

	public HttpPollingConsumerConfiguration() {}
	
	public HttpPollingConsumerConfiguration(String uniqueId, String serverUrl, int serverPort, String query) {
		super(uniqueId);
		this.serverUrl = serverUrl;
		this.serverPort = serverPort;
		this.query= query;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getConnectionURI() {
		return getServerUrl();
	}
}
