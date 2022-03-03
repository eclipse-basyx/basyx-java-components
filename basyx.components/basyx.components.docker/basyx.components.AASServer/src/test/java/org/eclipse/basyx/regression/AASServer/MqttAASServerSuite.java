package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.mqtt.MqttAASServerFeature;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.configuration.MqttPersistence;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttAASAggregatorHelper;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttAASAPIHelper;
import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttSubmodelAggregatorHelper;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;

public abstract class MqttAASServerSuite extends AASServerSuite {
	protected static AASServerComponent component;
	protected static Server mqttBroker;
	protected MqttTestListener listener;

	@Override
	protected String getURL() {
		return component.getURL();
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.stopServer();
		component.stopComponent();
	}

	@Override
	@Before
	public void setUp() {
		super.setUp();
		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);
	}

	@After
	public void tearDown() {
		mqttBroker.removeInterceptHandler(listener);
	}

	protected static void genericSetupClass(BaSyxAASServerConfiguration serverConfig) throws IOException {
		startMqttBroker();
		BaSyxContextConfiguration contextConfig = createBaSyxContextConfiguration();
		BaSyxMqttConfiguration mqttConfig = createMqttConfig();

		component = new AASServerComponent(contextConfig, serverConfig);
		component.addAASServerFeature(new MqttAASServerFeature(mqttConfig, "MqttAASServerSuiteClientId"));
		component.startComponent();
	}

	@Test
	public void shellLifeCycle() {
		AssetAdministrationShell shell = createShell(shellIdentifier.getId(), shellIdentifier);

		manager.createAAS(shell, getURL());

		assertEquals(MqttAASAggregatorHelper.TOPIC_CREATEAAS, listener.lastTopic);
		assertEquals(shell.getIdShort(), manager.retrieveAAS(shellIdentifier).getIdShort());

		manager.deleteAAS(shellIdentifier);
		assertEquals(MqttAASAggregatorHelper.TOPIC_DELETEAAS, listener.lastTopic);
		try {
			manager.retrieveAAS(shellIdentifier);
			fail();
		} catch (ResourceNotFoundException e) {
			// ResourceNotFoundException expected
		}
	}

	@Test
	public void submodelLifeCycle() {
		IIdentifier shellIdentifierForSubmodel = new CustomId("shellSubmodelId");
		AssetAdministrationShell shell = createShell(shellIdentifierForSubmodel.getId(), shellIdentifierForSubmodel);
		manager.createAAS(shell, getURL());

		Submodel submodel = createSubmodel(submodelIdentifier.getId(), submodelIdentifier);
		manager.createSubmodel(shellIdentifierForSubmodel, submodel);

		assertTrue(listener.getTopics().stream().anyMatch(t -> t.equals(MqttAASAPIHelper.TOPIC_ADDSUBMODEL)));
		assertTrue(listener.getTopics().stream().anyMatch(t -> t.equals(MqttSubmodelAggregatorHelper.TOPIC_CREATESUBMODEL)));

		assertEquals(submodel.getIdShort(), manager.retrieveSubmodel(shellIdentifierForSubmodel, submodelIdentifier).getIdShort());

		manager.deleteSubmodel(shellIdentifierForSubmodel, submodelIdentifier);

		assertTrue(listener.getTopics().stream().anyMatch(t -> t.equals(MqttAASAPIHelper.TOPIC_REMOVESUBMODEL)));
		assertTrue(listener.getTopics().stream().anyMatch(t -> t.equals(MqttSubmodelAggregatorHelper.TOPIC_DELETESUBMODEL)));
		try {
			manager.retrieveSubmodel(shellIdentifierForSubmodel, submodelIdentifier);
			fail();
		} catch (ResourceNotFoundException e) {
			// ResourceNotFoundException expected
		}
		manager.deleteAAS(shellIdentifierForSubmodel);
	}

	protected static BaSyxContextConfiguration createBaSyxContextConfiguration() {
		BaSyxContextConfiguration config = new BaSyxContextConfiguration();
		config.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		return config;
	}

	protected static BaSyxMqttConfiguration createMqttConfig() {
		BaSyxMqttConfiguration mqttConfig = new BaSyxMqttConfiguration();
		mqttConfig.setServer("tcp://localhost:" + mqttBroker.getPort());
		mqttConfig.setPersistenceType(MqttPersistence.INMEMORY);
		return mqttConfig;
	}

	protected static void startMqttBroker() throws IOException {
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);
	}
}
