package basyx.components.updater.examples.opcuaaas.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

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
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import basyx.components.updater.camelopcua.configuration.factory.OpcuaDefaultConfigurationFactory;
import basyx.components.updater.aas.configuration.factory.AASProducerDefaultConfigurationFactory;
import basyx.components.updater.core.component.UpdaterComponent;
import basyx.components.updater.core.configuration.factory.DefaultRoutesConfigurationFactory;
import basyx.components.updater.core.configuration.route.RoutesConfiguration;
import basyx.components.updater.transformer.cameljsonjackson.configuration.factory.JsonjacksonDefaultConfigurationFactory;

public class TestAASUpdater {
	private static AASServerComponent aasServer;
	private static UpdaterComponent updater;
	private static InMemoryRegistry registry;

	protected static IIdentifier deviceAAS = new CustomId("TestUpdatedDeviceAAS");
	private static BaSyxContextConfiguration aasContextConfig;

	@BeforeClass
	public static void setUp() throws IOException {
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
		ClassLoader loader = this.getClass().getClassLoader();
		RoutesConfiguration configuration = new RoutesConfiguration();

		// Extend configutation for connections
		DefaultRoutesConfigurationFactory routesFactory = new DefaultRoutesConfigurationFactory(loader);
		configuration.addRoutes(routesFactory.getRouteConfigurations());

		// Extend configutation for Opcua Source
		OpcuaDefaultConfigurationFactory opcuaConfigFactory = new OpcuaDefaultConfigurationFactory(loader);
		configuration.addDatasources(opcuaConfigFactory.getDataSourceConfigurations());

		// Extend configuration for AAS
		AASProducerDefaultConfigurationFactory aasConfigFactory = new AASProducerDefaultConfigurationFactory(loader);
		configuration.addDatasinks(aasConfigFactory.getDataSinkConfigurations());
		
		// Extend configuration for Jsonata
		JsonjacksonDefaultConfigurationFactory jsonJacksonConfigFactory = new JsonjacksonDefaultConfigurationFactory(loader);
		configuration.addTransformers(jsonJacksonConfigFactory.getDataTransformerConfigurations());

		updater = new UpdaterComponent(configuration);
		updater.startComponent();
		System.out.println("UPDATER STARTED");
		Thread.sleep(60000);
		updater.stopComponent();
		aasServer.stopComponent();
	}
	
}
