package basyx.components.updater.examples.httppollingjsonataaas.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import basyx.components.updater.camelhttppolling.configuration.factory.HttpPollingDefaultConfigurationFactory;
import basyx.components.updater.cameltimer.configuration.factory.TimerDefaultConfigurationFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
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
import org.junit.Test;

import basyx.components.updater.aas.configuration.factory.AASProducerDefaultConfigurationFactory;
import basyx.components.updater.core.component.UpdaterComponent;
import basyx.components.updater.core.configuration.factory.DefaultRoutesConfigurationFactory;
import basyx.components.updater.core.configuration.route.RoutesConfiguration;

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
		ClassLoader loader = TestAASUpdater.class.getClassLoader();
		RoutesConfiguration configuration = new RoutesConfiguration();

		// Extend configutation for connections
		// DefaulRoutesConfigFac takes default routes.json as to config
		DefaultRoutesConfigurationFactory routesFactory = new DefaultRoutesConfigurationFactory(loader);
		configuration.addRoutes(routesFactory.getRouteConfigurations());

		// Extend configuration for Http Source
		HttpPollingDefaultConfigurationFactory httpPollingConfigFactory = new HttpPollingDefaultConfigurationFactory(loader);
		configuration.addDatasinks(httpPollingConfigFactory.getDataSinkConfigurations());

		// Extend configuration for AAS
		// DefaulRoutesConfigFactory takes default aasserver.json as to config
		AASProducerDefaultConfigurationFactory aasConfigFactory = new AASProducerDefaultConfigurationFactory(loader);
		configuration.addDatasinks(aasConfigFactory.getDataSinkConfigurations());

		// Extend configuration for Jsonata
		//JsonataDefaultConfigurationFactory jsonataConfigFactory = new JsonataDefaultConfigurationFactory(loader);
		//configuration.addTransformers(jsonataConfigFactory.getDataTransformerConfigurations());

		// Extend configuration for Timer
		TimerDefaultConfigurationFactory timerConfigFactory = new TimerDefaultConfigurationFactory(loader);
		configuration.addDatasources(timerConfigFactory.getDataSourceConfigurations());


		updater = new UpdaterComponent(configuration);
		updater.startComponent();
		System.out.println("UPDATER STARTED");
		// System.out.println("PUBLISH EVENT");
		// publishNewDatapoint();
		// System.out.println("EVENT PUBLISHED");
		// checkIfPropertyIsUpdated();
		updater.stopComponent();
		aasServer.stopComponent();

		System.out.println("moin");
	}
	@Test
	public void WrapperlessTest() throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			public void configure() {
				from("timer://foo?fixedRate=true&delay=0&period=10000").to("https://08b33bff-279a-4879-a2e4-530b9cd21fb1.mock.pstmn.io/status");
			}
		});
		context.start();
		Thread.sleep(10000);
		context.stop();
	}


	/**
	 * public void startComponent() {
		camelContext = new DefaultCamelContext();
		try {
			camelContext.addRoutes(new UpdaterRouteBuilder(configuration));
			camelContext.start();
			logger.info("Updater started");
		} catch (Exception e) {
			e.printStackTrace();
			camelContext = null;
		}
	} */
	private void checkIfPropertyIsUpdated() throws InterruptedException {
		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry);
		ConnectedAssetAdministrationShell aas = manager.retrieveAAS(deviceAAS);
		ISubmodel sm = aas.getSubmodels().get("ConnectedSubmodel");
		ISubmodelElement updatedProp = sm.getSubmodelElement("ConnectedPropertyB");
		Object propValue = updatedProp.getValue();
		System.out.println("UpdatedPROP" + propValue);
		assertEquals("858383", propValue);
	}
}
