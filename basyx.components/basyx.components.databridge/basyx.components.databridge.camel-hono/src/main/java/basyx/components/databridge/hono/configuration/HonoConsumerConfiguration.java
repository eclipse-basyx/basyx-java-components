/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/

 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package basyx.components.databridge.hono.configuration;

import basyx.components.databridge.core.configuration.entity.DataSourceConfiguration;

/**
 * An implementation of ActiveMQ consumer configuration
 * @author haque
 *
 */
public class HonoConsumerConfiguration extends DataSourceConfiguration {
	private String userName;
	private String password;
	private String tenantId;
	private String deviceId;

	public HonoConsumerConfiguration() {}

	public HonoConsumerConfiguration(String uniqueId, String serverUrl, int serverPort, String userName, String password, String tenantId, String deviceId) {
		super(uniqueId, serverUrl, serverPort);
		this.userName = userName;
		this.password = password;
		this.tenantId = tenantId;
		this.deviceId = deviceId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getConnectionURI() {
		return "hono:" + getServerUrl() + 
				"?port=" + getServerPort() +
				"&userName=" + getUserName() +
				"&password=" + getPassword() +
				"&deviceId=" + getDeviceId() +
				"&tenantId=" + getTenantId();
	}
}
