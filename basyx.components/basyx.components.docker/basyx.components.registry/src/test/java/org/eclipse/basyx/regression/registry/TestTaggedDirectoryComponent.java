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

import java.io.IOException;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.proxy.TaggedDirectoryProxy;
import org.eclipse.basyx.testsuite.regression.extensions.aas.directory.tagged.TestTaggedDirectorySuite;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class TestTaggedDirectoryComponent extends TestTaggedDirectorySuite {

	private static RegistryComponent taggedDirectoryComponent;

	/**
	 * Sets up a TaggedDirectoryProxy to test the RegistryComponent with tagged
	 * directory functionality
	 */
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		taggedDirectoryComponent = createTaggedDirectoryComponent();
		taggedDirectoryComponent.startComponent();
	}

	@AfterClass
	public static void tearDownClass() throws MqttException, IOException {
		taggedDirectoryComponent.stopComponent();
	}

	private static RegistryComponent createTaggedDirectoryComponent() {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		BaSyxRegistryConfiguration taggedDirectoryConfig = new BaSyxRegistryConfiguration();
		taggedDirectoryConfig.setTaggedDirectoryEnabled(true);

		return new RegistryComponent(contextConfig, taggedDirectoryConfig);
	}

	@Override
	protected IAASTaggedDirectory getDirectory() {
		return new TaggedDirectoryProxy(new BaSyxContextConfiguration().getUrl());
	}

	@Override
	protected IAASRegistry getRegistryService() {
		return getDirectory();
	}
}