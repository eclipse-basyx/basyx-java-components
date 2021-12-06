package basyx.components.updater.examples.aasinternal.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.BeforeClass;
import org.junit.Test;

import basyx.components.updater.aas.builder.BasyxRoutesConfigurationBuilder;
import basyx.components.updater.core.component.UpdaterComponent;
import basyx.components.updater.core.configuration.route.RoutesConfiguration;
import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;

public class TestAASUpdater {
	private static AASServerComponent aasServer;
	private static UpdaterComponent updater;
	private static InMemoryRegistry registry;
	protected static Server mqttBroker;

	protected static IIdentifier deviceAAS = new CustomId("TestUpdatedDeviceAAS");
	private static BaSyxContextConfiguration aasContextConfig;

	@BeforeClass
	public static void setUp() throws IOException {
		startMqttBroker();

		registry = new InMemoryRegistry();
		
		aasContextConfig = new BaSyxContextConfiguration(4001, "");
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.INMEMORY, "aasx/updatertest.aasx");		
		aasServer = new AASServerComponent(aasContextConfig, aasConfig);
		aasServer.setRegistry(registry);
		

	}

	@Test
	public void test() throws Exception {
		aasServer.startComponent();
		System.out.println("AAS STARTED");
		System.out.println("START UPDATER");
		BasyxRoutesConfigurationBuilder configBuilder = new BasyxRoutesConfigurationBuilder(registry, deviceAAS);
		RoutesConfiguration configuration = configBuilder.getRoutesConfiguration(); 
		updater = new UpdaterComponent(configuration);
		updater.startComponent();
		System.out.println("UPDATER STARTED");
		System.out.println("PUBLISH EVENT");
		publishNewDatapoint();
		System.out.println("EVENT PUBLISHED");
		checkIfPropertyIsUpdated();
		updater.stopComponent();
		aasServer.stopComponent();
	}

	private void checkIfPropertyIsUpdated() throws InterruptedException {
		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry);
		ConnectedAssetAdministrationShell aas = manager.retrieveAAS(deviceAAS);
		ISubmodel sm = aas.getSubmodels().get("ConnectedSubmodel");
		ISubmodelElement updatedProp = sm.getSubmodelElement("ConnectedPropertyB");
		Object propValue = updatedProp.getValue();
		System.out.println("UpdatedPROP" + propValue);
		assertEquals("3", propValue);
	}

	private void publishNewDatapoint() throws MqttException, MqttSecurityException, MqttPersistenceException {
		MqttClient mqttClient = new MqttClient("tcp://localhost:1884", "testClient", new MemoryPersistence());
		mqttClient.connect();
		mqttClient.publish("PropertyB", new MqttMessage("3".getBytes()));
		mqttClient.disconnect();
		mqttClient.close();
	}

	private static void startMqttBroker() throws IOException {
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);
	}
}
