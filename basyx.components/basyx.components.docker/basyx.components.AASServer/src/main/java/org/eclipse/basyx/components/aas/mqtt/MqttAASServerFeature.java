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

import com.google.common.base.Strings;
import java.security.ProviderException;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerFeature;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

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

	@Override
	public void addToContext(BaSyxContext context) {
	}

}
