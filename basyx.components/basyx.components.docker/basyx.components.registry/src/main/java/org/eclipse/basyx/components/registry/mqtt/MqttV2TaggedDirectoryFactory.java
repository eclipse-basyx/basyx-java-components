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

import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.observing.ObservableAASTaggedDirectoryServiceV2;
import org.eclipse.basyx.extensions.shared.encoding.IEncoder;

/**
 * Factory for building a Mqtt-Registry model provider for AASTaggedDirectory
 * implementations
 * 
 * @author espen, siebert
 * 
 */
public class MqttV2TaggedDirectoryFactory extends MqttV2RegistryFactory {
	public IAASTaggedDirectory create(IAASTaggedDirectory taggedDirectory, BaSyxMqttConfiguration mqttConfig, BaSyxRegistryConfiguration registryConfig, IEncoder idEncoder) {
		return wrapRegistryInMqttObserver(taggedDirectory, mqttConfig, registryConfig, idEncoder);
	}

	private static IAASTaggedDirectory wrapRegistryInMqttObserver(IAASTaggedDirectory taggedDirectory, BaSyxMqttConfiguration mqttConfig, BaSyxRegistryConfiguration registryConfig, IEncoder idEncoder) {
		ObservableAASTaggedDirectoryServiceV2 observedAPI = new ObservableAASTaggedDirectoryServiceV2(taggedDirectory, registryConfig.getRegistryId());
		addAASRegistryServiceObserver(observedAPI, mqttConfig, idEncoder);
		return observedAPI;
	}
}
