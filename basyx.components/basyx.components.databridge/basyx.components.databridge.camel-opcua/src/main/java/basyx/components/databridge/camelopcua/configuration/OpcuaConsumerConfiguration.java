/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.databridge.camelopcua.configuration;

import basyx.components.databridge.core.configuration.entity.DataSourceConfiguration;

/**
 * An implementation of OpcUa consumer configuration
 * 
 * @author Daniele Rossi
 *
 */
public class OpcuaConsumerConfiguration extends DataSourceConfiguration {
	private String pathToService;
	private String nodeInformation;
	private String username;
	private String password;

	public OpcuaConsumerConfiguration() {
	}

	public OpcuaConsumerConfiguration(String uniqueId, String serverUrl, int serverPort, String pathToService,
			String nodeInformation, String username, String password) {
		super(uniqueId, serverUrl, serverPort);
		this.pathToService = pathToService;
		this.nodeInformation = nodeInformation;
		this.username = username;
		this.password = password;
	}

	public String getPathToService() {
		return pathToService;
	}

	public void setPathToService(String pathToService) {
		this.pathToService = pathToService;
	}

	public String getNodeInformation() {
		return nodeInformation;
	}

	public void setNodeInformation(String nodeInformation) {
		this.nodeInformation = nodeInformation;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConnectionURI() {
		String credentials = username == null || password == null ? "" : username + ":" + password + "@";
		return "milo-client:opc.tcp://" + credentials + getServerUrl() + ":" + getServerPort() + "/" + pathToService
				+ "?allowedSecurityPolicies=None&node=RAW(" + nodeInformation + ")";
	}
}
