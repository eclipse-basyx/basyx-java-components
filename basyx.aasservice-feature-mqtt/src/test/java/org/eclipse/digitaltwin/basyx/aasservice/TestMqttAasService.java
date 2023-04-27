/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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


package org.eclipse.digitaltwin.basyx.aasservice;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.InMemoryAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.eclipse.digitaltwin.basyx.aasservice.feature.mqtt.MqttAasService;
import org.eclipse.digitaltwin.basyx.aasservice.feature.mqtt.MqttAasServiceFeature;
import org.eclipse.digitaltwin.basyx.aasservice.feature.mqtt.MqttAasServiceTopicFactory;
import org.eclipse.digitaltwin.basyx.common.dataformat.json.ExtendedJsonDeserializer;
import org.eclipse.digitaltwin.basyx.common.encoding.Base64URLEncoder;
import org.eclipse.digitaltwin.basyx.common.mqtt.MqttTestListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;

public class TestMqttAasService extends AasServiceSuite {
	
	private static Server mqttBroker;
	private static MqttClient mqttClient;
	private static MqttTestListener listener;
	private static MqttAasServiceTopicFactory topicFactory = new MqttAasServiceTopicFactory(new Base64URLEncoder());
	
	private static AasRepository aasRepository;
	private static AasServiceFactory mqttAasServiceFactory;
	
	private MqttAasService mqttAasService;
	private AssetAdministrationShell shell;


	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		mqttBroker = startBroker();
		listener = configureInterceptListener(mqttBroker);
		mqttClient = createAndConnectClient();
		
		aasRepository = createMqttAasRepository();
		mqttAasServiceFactory = createMqttAasServiceFactory(mqttClient);
		
	}

	@Before
	public void setUp() {
		shell = DummyAssetAdministrationShell.getDummyShell();
		mqttAasService = (MqttAasService) getAASServiceFactory().create(shell);
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.removeInterceptHandler(listener);
		mqttBroker.stopServer();
	}

	@Override
	protected AasServiceFactory getAASServiceFactory() {
		return mqttAasServiceFactory;
	}
	
	private static AasServiceFactory createMqttAasServiceFactory(MqttClient client) {
		AasServiceFactory serviceFactory = new InMemoryAasServiceFactory();
		MqttAasServiceFeature mqttFeature = new MqttAasServiceFeature(client, aasRepository);
		return mqttFeature.decorate(serviceFactory);
	}
	
	@Test
	public void setAssetInformation() {
		AssetInformation assetInfo = createDummyAssetInformation();
		mqttAasService.setAssetInformation(assetInfo);
		String repoId = aasRepository.getName();
		
		assertEquals(topicFactory.createSetAssetInformationTopic(repoId, shell.getId()), listener.lastTopic);
		try {
			assertEquals(assetInfo, deserializeAssetInformation(listener.lastPayload));
		} catch (DeserializationException e) {
			e.printStackTrace();
		}

	}

	private AssetInformation createDummyAssetInformation() {
		AssetInformation assetInfo = new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE)
				.globalAssetId(
						new DefaultReference.Builder().keys(new DefaultKey.Builder().value("assetIDTestKey").build()).build())
				.build();
		return assetInfo;
	}
	
	@Test
	public void addSubmodelReferenceEvent() throws DeserializationException, JsonProcessingException {
		Reference submodelReference = DummyAssetAdministrationShell.submodelReference;
		mqttAasService.addSubmodelReference(submodelReference);
		String repoId = aasRepository.getName();
		
		assertEquals(topicFactory.createAddSubmodelReferenceTopic(repoId, shell.getId()), listener.lastTopic);
		assertEquals(submodelReference, deserializeReference(listener.lastPayload));
	}
	
	@Test
	public void removeSubmodelReferenceEvent() throws DeserializationException, JsonProcessingException {
		String repoId = aasRepository.getName();
		
		DummyAssetAdministrationShell.addDummySubmodelReference(mqttAasService.getAAS());
		mqttAasService.removeSubmodelReference(DummyAssetAdministrationShell.SUBMODEL_ID);

		assertEquals(topicFactory.createRemoveSubmodelReferenceTopic(repoId, shell.getId()), listener.lastTopic);
		assertEquals(DummyAssetAdministrationShell.submodelReference, deserializeReference(listener.lastPayload));
	}

	
	private Object deserializeAssetInformation(String payload) throws DeserializationException {
		return new ExtendedJsonDeserializer().readAssetInformation(payload, AssetInformation.class);
	}
	
	private Object deserializeReference(String payload) throws DeserializationException {
		return new ExtendedJsonDeserializer().readReference(payload, Reference.class);
	}
	
	private static AasRepository createMqttAasRepository() {
		AasRepository repo = new InMemoryAasRepositoryFactory(mqttAasServiceFactory).create();
		return repo;
	}

	private static MqttClient createAndConnectClient() throws MqttException, MqttSecurityException {
		MqttClient client = new MqttClient("tcp://localhost:1884", "testClient");
		client.connect();
		return client;
	}
	
	private static MqttTestListener configureInterceptListener(Server broker) {
		MqttTestListener testListener = new MqttTestListener();
		broker.addInterceptHandler(testListener);

		return testListener;
	}
	
	private static Server startBroker() throws IOException {
		Server broker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();

		IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		broker.startServer(classPathConfig);

		return broker;
	}

}
