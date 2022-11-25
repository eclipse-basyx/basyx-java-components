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
import org.eclipse.basyx.aas.registration.observing.ObservableAASRegistryService;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.configuration.MqttPersistence;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttAASRegistryServiceObserver;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for building a Mqtt-Registry model provider
 * 
 * @author espen
 * 
 */
public class MqttRegistryFactory {

	private static Logger logger = LoggerFactory.getLogger(MqttRegistryFactory.class);

	public IAASRegistry create(IAASRegistry registry, BaSyxMqttConfiguration mqttConfig) {
		return wrapRegistryInMqttObserver(registry, mqttConfig);
	}

	private static IAASRegistry wrapRegistryInMqttObserver(IAASRegistry registry, BaSyxMqttConfiguration mqttConfig) {
		ObservableAASRegistryService observedAPI = new ObservableAASRegistryService(registry);
		addAASRegistryServiceObserver(observedAPI, mqttConfig);
		return observedAPI;
	}

	protected static void addAASRegistryServiceObserver(ObservableAASRegistryService observedAPI, BaSyxMqttConfiguration mqttConfig) {
		String brokerEndpoint = mqttConfig.getServer();
		MqttClientPersistence mqttPersistence = getMqttPersistenceFromConfig(mqttConfig);
		try {
			MqttAASRegistryServiceObserver mqttObserver = new MqttAASRegistryServiceObserver(brokerEndpoint, mqttConfig.getClientId(), mqttConfig.getUser(), mqttConfig.getPass().toCharArray(), mqttPersistence);
			observedAPI.addObserver(mqttObserver);
		} catch (MqttException e) {
			logger.error("Could not establish MQTT connection for MqttAASRegistry", e);
		}
	}

	protected static MqttClientPersistence getMqttPersistenceFromConfig(BaSyxMqttConfiguration config) {
		String persistenceFilePath = config.getPersistencePath();
		MqttPersistence persistenceType = config.getPersistenceType();
		if (isFilePersistenceType(persistenceType)) {
			return createMqttFilePersistence(persistenceFilePath);
		} else {
			return new MemoryPersistence();
		}
	}

	private static MqttClientPersistence createMqttFilePersistence(String persistenceFilePath) {
		if (!isFilePathSet(persistenceFilePath)) {
			return new MqttDefaultFilePersistence();
		} else {
			return new MqttDefaultFilePersistence(persistenceFilePath);
		}
	}

	private static boolean isFilePathSet(String persistenceFilePath) {
		return persistenceFilePath != null && !persistenceFilePath.isEmpty();
	}

	private static boolean isFilePersistenceType(MqttPersistence persistenceType) {
		return persistenceType == MqttPersistence.FILE;
	}

}
