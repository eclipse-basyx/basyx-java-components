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

import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttV2AASAggregatorTopicFactory;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttV2DecoratingAASAggregatorFactory;
import org.eclipse.basyx.extensions.shared.encoding.Base64URLEncoder;
import org.eclipse.basyx.extensions.shared.encoding.IEncoder;
import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttV2DecoratingSubmodelAggregatorFactory;
import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttV2SubmodelAggregatorTopicFactory;
import org.eclipse.basyx.extensions.submodel.mqtt.MqttV2DecoratingSubmodelAPIFactory;
import org.eclipse.basyx.extensions.submodel.mqtt.MqttV2SubmodelAPITopicFactory;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;

/**
 * 
 * Decorator for Mqtt eventing of Submodel and Shell interactions
 * 
 * @author fischer, fried, siebert
 *
 */
public class MqttV2AASServerDecorator implements IAASServerDecorator {

	private MqttClient client;
	private String aasRepoId;
	private IEncoder idEncoder;

	/**
	 * Creates the aas server decorator for integrating the MqttV2 decorations in
	 * the AAS Server
	 * 
	 * @param client
	 * @param aasRepoId
	 * @param idEncoder
	 */
	public MqttV2AASServerDecorator(MqttClient client, String aasRepoId, IEncoder idEncoder) {
		this.client = client;
		this.aasRepoId = aasRepoId;
		this.idEncoder = idEncoder;
	}

	@Override
	public ISubmodelAPIFactory decorateSubmodelAPIFactory(ISubmodelAPIFactory submodelAPIFactory) {
		return new MqttV2DecoratingSubmodelAPIFactory(submodelAPIFactory, client, this.aasRepoId, new MqttV2SubmodelAPITopicFactory(idEncoder));
	}

	@Override
	public ISubmodelAggregatorFactory decorateSubmodelAggregatorFactory(ISubmodelAggregatorFactory submodelAggregatorFactory) {
		return new MqttV2DecoratingSubmodelAggregatorFactory(submodelAggregatorFactory, client, this.aasRepoId, new MqttV2SubmodelAggregatorTopicFactory(idEncoder));
	}

	@Override
	public IAASAPIFactory decorateAASAPIFactory(IAASAPIFactory aasAPIFactory) {
		return aasAPIFactory;
	}

	@Override
	public IAASAggregatorFactory decorateAASAggregatorFactory(IAASAggregatorFactory aasAggregatorFactory) {
		return new MqttV2DecoratingAASAggregatorFactory(aasAggregatorFactory, client, this.aasRepoId, new MqttV2AASAggregatorTopicFactory(new Base64URLEncoder()));
	}

}
