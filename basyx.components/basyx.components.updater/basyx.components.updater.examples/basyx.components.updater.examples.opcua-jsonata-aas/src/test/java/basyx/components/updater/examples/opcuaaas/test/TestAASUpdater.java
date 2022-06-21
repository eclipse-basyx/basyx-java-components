package basyx.components.updater.examples.opcuaaas.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutionException;

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
import org.eclipse.milo.examples.server.ExampleServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import basyx.components.updater.aas.configuration.factory.AASProducerDefaultConfigurationFactory;
import basyx.components.updater.camelopcua.configuration.factory.OpcuaDefaultConfigurationFactory;
import basyx.components.updater.core.component.UpdaterComponent;
import basyx.components.updater.core.configuration.factory.RoutesConfigurationFactory;
import basyx.components.updater.core.configuration.route.core.RoutesConfiguration;
import basyx.components.updater.transformer.cameljsonata.configuration.factory.JsonataDefaultConfigurationFactory;
import basyx.components.updater.transformer.cameljsonjackson.configuration.factory.JsonJacksonDefaultConfigurationFactory;

public class TestAASUpdater {
	private static AASServerComponent aasServer;
	private static UpdaterComponent updater;
	private static InMemoryRegistry registry;
	protected static ExampleServer opcUaServer;

	protected static IIdentifier deviceAAS = new CustomId("TestUpdatedDeviceAAS");
	private static BaSyxContextConfiguration aasContextConfig;

	@Before
	public void setUp() throws Exception {
		System.out.println("Setting up env...");
		startOpcUaServer();
		registry = new InMemoryRegistry();

		aasContextConfig = new BaSyxContextConfiguration(4001, "");
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.INMEMORY, "aasx/updatertest.aasx");
		aasServer = new AASServerComponent(aasContextConfig, aasConfig);
		aasServer.setRegistry(registry);
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("Tearing down env...");
		updater.stopComponent();
		aasServer.stopComponent();
		stopOpcUaServer();
	}

	@Test
	public void test() throws Exception {
		aasServer.startComponent();
		System.out.println("AAS STARTED");
		System.out.println("START UPDATER");
		ClassLoader loader = this.getClass().getClassLoader();
		RoutesConfiguration configuration = new RoutesConfiguration();

		// Extend configutation for connections
		RoutesConfigurationFactory routesFactory = new RoutesConfigurationFactory(loader);
		configuration.addRoutes(routesFactory.create());

		// Extend configutation for Opcua Source
		OpcuaDefaultConfigurationFactory opcuaConfigFactory = new OpcuaDefaultConfigurationFactory(loader);
		configuration.addDatasources(opcuaConfigFactory.create());

		// Extend configuration for AAS
		AASProducerDefaultConfigurationFactory aasConfigFactory = new AASProducerDefaultConfigurationFactory(loader);
		configuration.addDatasinks(aasConfigFactory.create());

		JsonJacksonDefaultConfigurationFactory jsonJacksonConfigFactory = new JsonJacksonDefaultConfigurationFactory(loader);
		configuration.addTransformers(jsonJacksonConfigFactory.create());

		// Extend configuration for Jsonata
		JsonataDefaultConfigurationFactory jsonataConfigFactory = new JsonataDefaultConfigurationFactory(loader);
		configuration.addTransformers(jsonataConfigFactory.create());

		updater = new UpdaterComponent(configuration);
		updater.startComponent();
		System.out.println("UPDATER STARTED");
		Thread.sleep(5000);
		System.out.println("CHECK PROPERTY");
		checkProperty();
	}

	private void checkProperty() {
		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry);
		ConnectedAssetAdministrationShell aas = manager.retrieveAAS(deviceAAS);
		ISubmodel sm = aas.getSubmodels().get("ConnectedSubmodel");
		ISubmodelElement propertyA = sm.getSubmodelElement("ConnectedPropertyA");
		Object propAValue = propertyA.getValue();
		ISubmodelElement propertyB = sm.getSubmodelElement("ConnectedPropertyB");
		Object propBValue = propertyB.getValue();
		assertEquals("3.14", propAValue);
		assertEquals("32", propBValue);
	}

	private static void startOpcUaServer() throws Exception {
		opcUaServer = new ExampleServer();
		opcUaServer.startup().get();
	}

	private static void stopOpcUaServer() throws InterruptedException, ExecutionException {
		opcUaServer.shutdown().get();
	}
}
