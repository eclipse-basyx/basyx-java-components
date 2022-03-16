/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.mqtt;

import com.google.common.base.Strings;
import java.security.ProviderException;

import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerFeature;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.jetbrains.annotations.NotNull;

/**
 * 
 * Feature for Mqtt eventing of the AASServer
 * 
 * @author fischer, fried
 *
 */
public class MqttAASServerFeature implements IAASServerFeature {
	private BaSyxMqttConfiguration mqttConfig;
	private MqttClient client;
	private String clientId;

	public MqttAASServerFeature(BaSyxMqttConfiguration mqttConfig, String clientId) {
		this.mqttConfig = mqttConfig;
		this.clientId = clientId;
	}

	@Override
	public void initialize() {
		try {
			String serverEndpoint = mqttConfig.getServer();
			MqttConnectOptions options = createMqttConnectOptions();
			client = new MqttClient(serverEndpoint, clientId);
			client.connect(options);
		} catch (MqttException e) {
			throw new ProviderException("moquette.conf Error ", e);
		}
	}

	protected MqttConnectOptions createMqttConnectOptions() {
		MqttConnectOptions options = new MqttConnectOptions();
		if (!Strings.isNullOrEmpty(mqttConfig.getUser())) {
			options.setUserName(mqttConfig.getUser());
			options.setPassword(mqttConfig.getPass().toCharArray());
		}
		return options;
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public IAASServerDecorator getDecorator() {
		return new MqttAASServerDecorator(client);
	}

}
