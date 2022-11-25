/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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

import java.security.ProviderException;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerFeature;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.extensions.shared.encoding.IEncoder;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * 
 * Feature for Mqtt eventing of the AASServer
 * 
 * @author fischer, fried, siebert
 *
 */
public class MqttV2AASServerFeature implements IAASServerFeature {
	private BaSyxMqttConfiguration mqttConfig;
	private MqttClient client;
	private String clientId;
	private String aasRepoId;
	private IEncoder idEncoder;

	/**
	 * Creates the aas server feature for integrating the MqttV2 feature in the AAS
	 * Server
	 * 
	 * @param mqttConfig
	 * @param clientId
	 * @param aasRepoId
	 * @param idEncoder
	 */
	public MqttV2AASServerFeature(BaSyxMqttConfiguration mqttConfig, String clientId, String aasRepoId, IEncoder idEncoder) {
		this.mqttConfig = mqttConfig;
		this.clientId = clientId;
		this.aasRepoId = aasRepoId;
		this.idEncoder = idEncoder;
	}

	@Override
	public void initialize() {
	  MqttAASServerFeature mqttAASServerFeature = new MqttAASServerFeature(mqttConfig, clientId);
		try {
			String serverEndpoint = mqttConfig.getServer();
			MqttConnectOptions options = mqttAASServerFeature.createMqttConnectOptions();
			client = new MqttClient(serverEndpoint, clientId);
			client.connect(options);
		} catch (MqttException e) {
			throw new ProviderException("moquette.conf Error ", e);
		}
	}

	@Override
	public void cleanUp() {
		// nothing to do regarding cleanup for this feature at the moment
	}

	@Override
	public IAASServerDecorator getDecorator() {
		return new MqttV2AASServerDecorator(client, this.aasRepoId, idEncoder);
	}

	@Override
	public void addToContext(BaSyxContext context) {
		// nothing to add to the context for this feature at the moment
	}

}
