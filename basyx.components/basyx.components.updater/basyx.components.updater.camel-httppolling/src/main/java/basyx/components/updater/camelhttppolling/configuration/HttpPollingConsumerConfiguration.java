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

import basyx.components.updater.core.configuration.DataSourceConfiguration;
/**
 * An implementation of http consumer configuration
 * @author n14s
 *
 */
public class HttpPollingConsumerConfiguration extends DataSourceConfiguration {
    private String query;

	public HttpPollingConsumerConfiguration() {}
	
	public HttpPollingConsumerConfiguration(String uniqueId, String serverUrl, int serverPort, String query) {
		super(uniqueId, serverUrl, serverPort);
		this.query= query;
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
