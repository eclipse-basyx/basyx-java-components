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
package org.eclipse.basyx.components.aas.mqtt;

import java.util.Set;

import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.configuration.MqttPersistence;
import org.eclipse.basyx.extensions.submodel.mqtt.MqttSubmodelAPI;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Api provider for constructing a new Submodel API that emits MQTT events
 * 
 * @author espen
 */
@Deprecated
public class MqttSubmodelAPIFactory implements ISubmodelAPIFactory {
	private static Logger logger = LoggerFactory.getLogger(MqttSubmodelAPIFactory.class);

	private BaSyxMqttConfiguration config;

	/**
	 * Constructor with MQTT configuration for providing submodel APIs
	 * 
	 * @param config
	 */
	public MqttSubmodelAPIFactory(BaSyxMqttConfiguration config) {
		this.config = config;
	}

	@Override
	public ISubmodelAPI getSubmodelAPI(Submodel sm) {
		// Get the submodel's id from the given provider
		String smId = sm.getIdentification().getId();

		// Create the API
		IModelProvider provider = new VABLambdaProvider(sm);
		VABSubmodelAPI observedApi = new VABSubmodelAPI(provider);

		// Configure the API according to the given configs
		String brokerEndpoint = config.getServer();
		String clientId = smId;

		MqttSubmodelAPI api;
		try {
			MqttClientPersistence persistence = getMqttPersistenceFromConfig(config);
			if (config.getUser() != null) {
				String user = config.getUser();
				String pass = config.getPass();
				api = new MqttSubmodelAPI(observedApi, brokerEndpoint, clientId, user, pass.toCharArray(), persistence);
			} else {
				api = new MqttSubmodelAPI(observedApi, brokerEndpoint, clientId, persistence);
			}
			setWhitelist(api, smId);
		} catch (MqttException e) {
			logger.error("Could not create MqttSubmodelApi", e);
			return observedApi;
		}
		return api;
	}

	private static MqttClientPersistence getMqttPersistenceFromConfig(BaSyxMqttConfiguration config) {
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

	private void setWhitelist(MqttSubmodelAPI api, String smId) {
		if (!config.isWhitelistEnabled(smId)) {
			// Do not use the whitelist if it has been disabled
			api.disableWhitelist();
			return;
		}

		// Read whitelist from configuration
		Set<String> whitelist = config.getWhitelist(smId);

		logger.info("Set MQTT whitelist for " + smId + " with " + whitelist.size() + " entries");
		api.setWhitelist(whitelist);
		api.enableWhitelist();
	}
}
