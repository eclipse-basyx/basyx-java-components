/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.databridge.core.configuration.entity;

/**
 * A generic class of Data source configurations
 * @author haque
 *
 */
public abstract class DataSourceConfiguration extends RouteEntity {
	private String serverUrl;
	private int serverPort;
	
	public DataSourceConfiguration() {
		super();
	}
	
	public DataSourceConfiguration(String uniqueId, String serverUrl, int serverPort) {
		super(uniqueId);
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
}
