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

import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttDecoratingAASAggregatorFactory;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttDecoratingAASAPIFactory;
import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttDecoratingSubmodelAggregatorFactory;
import org.eclipse.basyx.extensions.submodel.mqtt.MqttDecoratingSubmodelAPIFactory;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;

/**
 * 
 * Decorator for Mqtt eventing of Submodel and Shell interactions
 * 
 * @author fischer, fried
 *
 */
public class MqttAASServerDecorator implements IAASServerDecorator {

	private MqttClient client;

	public MqttAASServerDecorator(MqttClient client) {
		this.client = client;
	}

	@Override
	public ISubmodelAPIFactory decorateSubmodelAPIFactory(ISubmodelAPIFactory submodelAPIFactory) {
		return new MqttDecoratingSubmodelAPIFactory(submodelAPIFactory, client);
	}

	@Override
	public ISubmodelAggregatorFactory decorateSubmodelAggregatorFactory(ISubmodelAggregatorFactory submodelAggregatorFactory) {
		return new MqttDecoratingSubmodelAggregatorFactory(submodelAggregatorFactory, client);
	}

	@Override
	public IAASAPIFactory decorateAASAPIFactory(IAASAPIFactory aasAPIFactory) {
		return new MqttDecoratingAASAPIFactory(aasAPIFactory, client);
	}

	@Override
	public IAASAggregatorFactory decorateAASAggregatorFactory(IAASAggregatorFactory aasAggregatorFactory) {
		return new MqttDecoratingAASAggregatorFactory(aasAggregatorFactory, client);
	}

}
