package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregator;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.configuration.MqttPersistence;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttAASAggregatorHelper;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;

public class TestAASServerWithMongoDbAndMqtt extends AASServerSuite {
	private static AASServerComponent component;
	private static Server mqttBroker;
	private MqttTestListener listener;

	@Override
	protected String getURL() {
		return component.getURL();
	}

	@BeforeClass
	public static void setUpClass() throws ParserConfigurationException, SAXException, IOException {
		new MongoDBAASAggregator().reset();
		startMqttBroker();

		BaSyxContextConfiguration contextConfig = createBaSyxContextConfiguration();
		BaSyxAASServerConfiguration serverConfig = new BaSyxAASServerConfiguration(AASServerBackend.MONGODB, BaSyxAASServerConfiguration.DEFAULT_SOURCE);

		component = new AASServerComponent(contextConfig, serverConfig);

		BaSyxMqttConfiguration mqttConfig = createMqttConfig();
		component.enableMQTT(mqttConfig);

		component.startComponent();
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
			// expected
		}
	}

	private static void startMqttBroker() throws IOException {
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);
	}

	private static BaSyxContextConfiguration createBaSyxContextConfiguration() {
		BaSyxContextConfiguration config = new BaSyxContextConfiguration();
		config.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		return config;
	}

	private static BaSyxMqttConfiguration createMqttConfig() {
		BaSyxMqttConfiguration mqttConfig = new BaSyxMqttConfiguration();
		mqttConfig.setServer("tcp://localhost:" + mqttBroker.getPort());
		mqttConfig.setPersistenceType(MqttPersistence.INMEMORY);
		return mqttConfig;
	}

	private AssetAdministrationShell createShell(String idShort, IIdentifier identifier) {
		AssetAdministrationShell shell = new AssetAdministrationShell();
		shell.setIdentification(identifier);
		shell.setIdShort(idShort);
		return shell;
	}
}
