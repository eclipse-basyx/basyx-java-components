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
package org.eclipse.basyx.components.registry.executable;

import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.components.registry.configuration.RegistryEventBackend;

/**
 * A registry executable for a registry with any backend.
 * 
 * @author espen
 */
public class RegistryExecutable {
	private RegistryExecutable() {
	}

	public static void main(String[] args) {
		// Load context configuration from default source
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromDefaultSource();

		// Load registry configuration from default source
		BaSyxRegistryConfiguration registryConfig = new BaSyxRegistryConfiguration();
		registryConfig.loadFromDefaultSource();

		// Create and start component according to the configuration
		RegistryComponent component = new RegistryComponent(contextConfig, registryConfig);

		setMqttConfiguration(registryConfig, component);

		component.startComponent();
	}

	private static void setMqttConfiguration(BaSyxRegistryConfiguration registryConfig, RegistryComponent component) {
		if (isMqttBackendSelected(registryConfig)) {
			BaSyxMqttConfiguration mqttConfig = new BaSyxMqttConfiguration();
			mqttConfig.loadFromDefaultSource();

			component.enableMQTT(mqttConfig);
		}
	}

	private static boolean isMqttBackendSelected(BaSyxRegistryConfiguration registryConfig) {
		return registryConfig.getRegistryEvents().equals(RegistryEventBackend.MQTT) || registryConfig.getRegistryEvents().equals(RegistryEventBackend.MQTTV2);
	}
}
