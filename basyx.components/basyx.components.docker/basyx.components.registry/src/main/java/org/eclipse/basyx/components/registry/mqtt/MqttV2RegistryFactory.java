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

package org.eclipse.basyx.components.registry.mqtt;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.observing.ObservableAASRegistryServiceV2;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttV2AASRegistryServiceObserver;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttV2AASRegistryTopicFactory;
import org.eclipse.basyx.extensions.shared.encoding.IEncoder;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for building a Mqtt-Registry model provider
 * 
 * @author espen, siebert
 * 
 */
public class MqttV2RegistryFactory {

	private static Logger logger = LoggerFactory.getLogger(MqttV2RegistryFactory.class);

	public IAASRegistry create(IAASRegistry registry, BaSyxMqttConfiguration mqttConfig, BaSyxRegistryConfiguration registryConfig, IEncoder idEncoder) {
		return wrapRegistryInMqttObserver(registry, mqttConfig, registryConfig, idEncoder);
	}

	private static IAASRegistry wrapRegistryInMqttObserver(IAASRegistry registry, BaSyxMqttConfiguration mqttConfig, BaSyxRegistryConfiguration registryConfig, IEncoder idEncoder) {
		ObservableAASRegistryServiceV2 observedAPI = new ObservableAASRegistryServiceV2(registry, registryConfig.getRegistryId());
		addAASRegistryServiceObserver(observedAPI, mqttConfig, idEncoder);
		return observedAPI;
	}

	protected static void addAASRegistryServiceObserver(ObservableAASRegistryServiceV2 observedAPI, BaSyxMqttConfiguration mqttConfig, IEncoder idEncoder) {
		try {
			MqttClient mqttClient = createAndConnectMqttClient(mqttConfig);

			MqttV2AASRegistryServiceObserver mqttObserver = new MqttV2AASRegistryServiceObserver(mqttClient,
					new MqttV2AASRegistryTopicFactory(idEncoder));
			observedAPI.addObserver(mqttObserver);
		} catch (MqttException e) {
			logger.error("Could not establish MQTT connection for MqttAASRegistry", e);
		}
	}

	private static MqttClient createAndConnectMqttClient(BaSyxMqttConfiguration mqttConfig) throws MqttException, MqttSecurityException {
		MqttClientPersistence mqttPersistence = MqttRegistryFactory.getMqttPersistenceFromConfig(mqttConfig);

		MqttClient mqttClient = new MqttClient(mqttConfig.getServer(), mqttConfig.getClientId(), mqttPersistence);
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(mqttConfig.getUser());
		options.setPassword(mqttConfig.getPass().toCharArray());
		
		mqttClient.connect(options);
		return mqttClient;
	}
}
