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
package org.eclipse.basyx.regression.registry;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.configuration.MqttPersistence;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttAASRegistryHelper;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;

/**
 * Tests, if the RegistryComponent Mqtt-Event feature is enabled for the
 * possible backend configurations
 * 
 * @author espen
 *
 */
public abstract class TestMqttRegistryBackend {
	protected static String registryUrl;

	protected static BaSyxMqttConfiguration mqttConfig;
	protected static Server mqttBroker;

	protected static AASRegistryProxy aasRegistryProxy;

	protected MqttTestListener listener;
	protected RegistryComponent registryComponent;

	/**
	 * Sets up the MQTT broker and AASRegistryService for tests
	 */
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		startMqttBroker();
		mqttConfig = createMqttConfig();
		aasRegistryProxy = createRegistryProxy();
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.stopServer();
	}

	@Before
	public void setUp() {
		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);

		registryComponent = createRegistryComponent();
		registryComponent.enableMQTT(mqttConfig);
		registryComponent.startComponent();
	}

	@After
	public void tearDown() {
		registryComponent.stopComponent();
	}

	public abstract RegistryComponent createRegistryComponent();

	@Test
	public void testEventsWithComponent() {
		AASDescriptor aasDescriptor = createTestAASDescriptor();
		aasRegistryProxy.register(aasDescriptor);
		assertEquals(MqttAASRegistryHelper.TOPIC_REGISTERAAS, listener.lastTopic);
	}

	private static AASRegistryProxy createRegistryProxy() {
		registryUrl = new BaSyxContextConfiguration().getUrl();
		return new AASRegistryProxy(registryUrl);
	}

	private static BaSyxMqttConfiguration createMqttConfig() {
		BaSyxMqttConfiguration config = new BaSyxMqttConfiguration();
		config.setServer("tcp://localhost:" + mqttBroker.getPort());
		config.setPersistenceType(MqttPersistence.INMEMORY);
		return config;
	}

	private static void startMqttBroker() throws IOException {
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);
	}

	private static AASDescriptor createTestAASDescriptor() {
		Identifier aasIdentifier = new Identifier(IdentifierType.CUSTOM, "testAAS");
		String aasEndpoint = "http://localhost:8080/aasList/" + aasIdentifier.getId() + "/aas";
		AASDescriptor aasDescriptor = new AASDescriptor(aasIdentifier, aasEndpoint);
		return aasDescriptor;
	}
}
