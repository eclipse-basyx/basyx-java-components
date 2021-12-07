/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.aas.configuration;

/**
 * An implementation of Basyx internal MQTT data source configuration
 * @author haque
 *
 */
public class MQTTDatasourceConfiguration extends BasyxInternalDatasourceConfiguration {
	public static final String TYPE = "MQTT";
	private String topic;
	
	@Override
	public String getConnectionURI() {
		return "paho:" + this.getTopic() + "?brokerUrl=" + this.getServerUrl();
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
}
