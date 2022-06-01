/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.camelpaho.configuration;

import basyx.components.updater.core.configuration.route.sources.DataSourceConfiguration;

/**
 * An implementation of MQTT consumer configuration
 * @author haque
 *
 */
public class MqttConsumerConfiguration extends DataSourceConfiguration {
	private String topic;
	
	public MqttConsumerConfiguration() {}
	
	public MqttConsumerConfiguration(String uniqueId, String serverUrl, int serverPort, String topic) {
		super(uniqueId, serverUrl, serverPort);
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getConnectionURI() {
		return "paho:" + getTopic() + "?brokerUrl=tcp://" 
				+ getServerUrl() + ":" + getServerPort();
	}
}
