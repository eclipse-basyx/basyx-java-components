/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.camelhttp.configuration;

import basyx.components.updater.core.configuration.DataSourceConfiguration;

/**
 * An implementation of http consumer configuration
 * @author haque
 *
 */
public class HttpConsumerConfiguration extends DataSourceConfiguration {
	private String path = "";
	
	public HttpConsumerConfiguration() {}
	
	public HttpConsumerConfiguration(String uniqueId, String serverUrl, int serverPort, String path) {
		super(uniqueId, serverUrl, serverPort);
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	public String getConnectionURI() {
		return getServerUrl() + ":" + getServerPort() + "/" + getPath();
	}
}
