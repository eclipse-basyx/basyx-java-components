/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
		} catch (RuntimeException expected) {}
	}

	@Test
	public void directedDirectoryWithAuthorization() {
		try {
			registryConfig.enableAuthorization();
			RegistryComponent taggedDirectoryComponent = new RegistryComponent(contextConfig, registryConfig);
			taggedDirectoryComponent.startComponent();
			fail();
		} catch (RuntimeException expected) {}
	}

	@Test
	public void directedDirectoryWithMongoDb() {
		try {
			registryConfig.setRegistryBackend(RegistryBackend.MONGODB);
			RegistryComponent taggedDirectoryComponent = new RegistryComponent(contextConfig, registryConfig);
			taggedDirectoryComponent.startComponent();
			fail();
		} catch (RuntimeException expected) {}
	}

	@Test
	public void directedDirectoryWithSQL() {
		try {
			registryConfig.setRegistryBackend(RegistryBackend.SQL);
			RegistryComponent taggedDirectoryComponent = new RegistryComponent(contextConfig, registryConfig);
			taggedDirectoryComponent.startComponent();
			fail();
		} catch (RuntimeException expected) {}
	}
}
