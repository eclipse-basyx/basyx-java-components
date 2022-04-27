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
		taggedDirectoryConfig.enableTaggedDirectory();

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