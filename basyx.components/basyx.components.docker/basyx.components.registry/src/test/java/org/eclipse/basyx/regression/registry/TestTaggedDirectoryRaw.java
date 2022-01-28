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
	private static BaSyxContextConfiguration contectConfig;
	private static BaSyxRegistryConfiguration registryCongig;

	protected static AASRegistryProxy aasRegistryProxy;

	@BeforeClass
	public static void setUpClass() {
		contectConfig = new BaSyxContextConfiguration();
		registryCongig = new BaSyxRegistryConfiguration();
		registryCongig.setTaggedDirectoryEnabled(true);
	}

	@Test
	public void directedDirectoryWithMqtt() {
		try {
			RegistryComponent taggedDirectoryComponent = new RegistryComponent(contectConfig, registryCongig);
			taggedDirectoryComponent.enableMQTT(new BaSyxMqttConfiguration());
			taggedDirectoryComponent.startComponent();
			fail();
		} catch (RuntimeException e) {
			// expected
		}
	}

	@Test
	public void directedDirectoryWithAuthorization() {
		try {
			registryCongig.setAuthorizationEnabled(true);
			RegistryComponent taggedDirectoryComponent = new RegistryComponent(contectConfig, registryCongig);
			taggedDirectoryComponent.startComponent();
			fail();
		} catch (RuntimeException e) {
			// expected
		}
	}

	@Test
	public void directedDirectoryWithMongoDb() {
		try {
			registryCongig.setRegistryBackend(RegistryBackend.MONGODB);
			RegistryComponent taggedDirectoryComponent = new RegistryComponent(contectConfig, registryCongig);
			taggedDirectoryComponent.startComponent();
			fail();
		} catch (RuntimeException e) {
			// expected
		}
	}

	@Test
	public void directedDirectoryWithSQL() {
		try {
			registryCongig.setRegistryBackend(RegistryBackend.SQL);
			RegistryComponent taggedDirectoryComponent = new RegistryComponent(contectConfig, registryCongig);
			taggedDirectoryComponent.startComponent();
			fail();
		} catch (RuntimeException e) {
			// expected
		}
	}
}
