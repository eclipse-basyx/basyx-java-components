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
package org.eclipse.basyx.regression.registry;

import static org.junit.Assert.fail;

import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.components.registry.configuration.RegistryBackend;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTaggedDirectoryRaw {
	private static BaSyxContextConfiguration contextConfig;
	private static BaSyxRegistryConfiguration registryConfig;

	protected static AASRegistryProxy aasRegistryProxy;

	@BeforeClass
	public static void setUpClass() {
		contextConfig = new BaSyxContextConfiguration();
		registryConfig = new BaSyxRegistryConfiguration();
		registryConfig.enableTaggedDirectory();
	}

	@Test
	public void directedDirectoryWithMqtt() {
		try {
			RegistryComponent taggedDirectoryComponent = new RegistryComponent(contextConfig, registryConfig);
			taggedDirectoryComponent.enableMQTT(new BaSyxMqttConfiguration());
			taggedDirectoryComponent.startComponent();
			fail();
		} catch (RuntimeException expected) {
		}
	}

	@Test
	public void directedDirectoryWithAuthorization() {
		try {
			registryConfig.enableAuthorization();
			RegistryComponent taggedDirectoryComponent = new RegistryComponent(contextConfig, registryConfig);
			taggedDirectoryComponent.startComponent();
			fail();
		} catch (RuntimeException expected) {
		}
	}

	@Test
	public void directedDirectoryWithMongoDb() {
		try {
			registryConfig.setRegistryBackend(RegistryBackend.MONGODB);
			RegistryComponent taggedDirectoryComponent = new RegistryComponent(contextConfig, registryConfig);
			taggedDirectoryComponent.startComponent();
			fail();
		} catch (RuntimeException expected) {
		}
	}

	@Test
	public void directedDirectoryWithSQL() {
		try {
			registryConfig.setRegistryBackend(RegistryBackend.SQL);
			RegistryComponent taggedDirectoryComponent = new RegistryComponent(contextConfig, registryConfig);
			taggedDirectoryComponent.startComponent();
			fail();
		} catch (RuntimeException expected) {
		}
	}
}
