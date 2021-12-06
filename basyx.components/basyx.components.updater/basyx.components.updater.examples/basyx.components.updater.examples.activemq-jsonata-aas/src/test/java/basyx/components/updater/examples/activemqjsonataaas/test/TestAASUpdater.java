package basyx.components.updater.examples.activemqjsonataaas.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.usage.SystemUsage;
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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import basyx.components.updater.camelactivemq.configuration.factory.ActiveMQDefaultConfigurationFactory;
import basyx.components.updater.aas.configuration.factory.AASProducerDefaultConfigurationFactory;
import basyx.components.updater.core.component.UpdaterComponent;
import basyx.components.updater.core.configuration.factory.DefaultRoutesConfigurationFactory;
import basyx.components.updater.core.configuration.route.RoutesConfiguration;
import basyx.components.updater.transformer.cameljsonata.configuration.factory.JsonataDefaultConfigurationFactory;

public class TestAASUpdater {
	private static AASServerComponent aasServer;
	private static UpdaterComponent updater;
	private static InMemoryRegistry registry;
	private static Connection connection;
	private static Session session;
	private static Destination destination;

	protected static IIdentifier deviceAAS = new CustomId("TestUpdatedDeviceAAS");
	private static BaSyxContextConfiguration aasContextConfig;

	@BeforeClass
	public static void setUp() throws IOException {
		startActiveMQBroker();
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
		DefaultRoutesConfigurationFactory routesFactory = new DefaultRoutesConfigurationFactory(loader);
		configuration.addRoutes(routesFactory.getRouteConfigurations());

		// Extend configutation for Kafka Source
		ActiveMQDefaultConfigurationFactory activeMQConfigFactory = new ActiveMQDefaultConfigurationFactory(loader);
		configuration.addDatasources(activeMQConfigFactory.getDataSourceConfigurations());

		// Extend configuration for AAS
		AASProducerDefaultConfigurationFactory aasConfigFactory = new AASProducerDefaultConfigurationFactory(loader);
		configuration.addDatasinks(aasConfigFactory.getDataSinkConfigurations());

		// Extend configuration for Jsonata
		JsonataDefaultConfigurationFactory jsonataConfigFactory = new JsonataDefaultConfigurationFactory(loader);
		configuration.addTransformers(jsonataConfigFactory.getDataTransformerConfigurations());

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
		ISubmodelElement updatedProp = sm.getSubmodelElement("ConnectedPropertyA");
		Object propValue = updatedProp.getValue();
		System.out.println("UpdatedPROP" + propValue);
		assertEquals("336.36", propValue);

	}

	private void publishNewDatapoint() {
		// Create a MessageProducer from the Session to the Topic or Queue
		try {
			// Create a ConnectionFactory
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
			// Create a Connection
			connection = connectionFactory.createConnection();
			connection.start();
			// Create a Session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// Create the destination (Topic or Queue)
			destination = session.createQueue("first-topic");
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			// Create a messages
			String json = "{\"Account\":{\"Account Name\":\"Firefly\",\"Order\":[{\"OrderID\":\"order103\",\"Product\":[{\"Product Name\":\"Bowler Hat\",\"ProductID\":858383,\"SKU\":\"0406654608\",\"Description\":{\"Colour\":\"Purple\",\"Width\":300,\"Height\":200,\"Depth\":210,\"Weight\":0.75},\"Price\":34.45,\"Quantity\":2},{\"Product Name\":\"Trilby hat\",\"ProductID\":858236,\"SKU\":\"0406634348\",\"Description\":{\"Colour\":\"Orange\",\"Width\":300,\"Height\":200,\"Depth\":210,\"Weight\":0.6},\"Price\":21.67,\"Quantity\":1}]},{\"OrderID\":\"order104\",\"Product\":[{\"Product Name\":\"Bowler Hat\",\"ProductID\":858383,\"SKU\":\"040657863\",\"Description\":{\"Colour\":\"Purple\",\"Width\":300,\"Height\":200,\"Depth\":210,\"Weight\":0.75},\"Price\":34.45,\"Quantity\":4},{\"ProductID\":345664,\"SKU\":\"0406654603\",\"Product Name\":\"Cloak\",\"Description\":{\"Colour\":\"Black\",\"Width\":30,\"Height\":20,\"Depth\":210,\"Weight\":2},\"Price\":107.99,\"Quantity\":1}]}]}}";
			TextMessage message = session.createTextMessage(json);
			producer.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startActiveMQBroker() {
		try {
			
			BrokerService broker = new BrokerService();
			broker.addConnector("tcp://localhost:61616");
			broker.setPersistent(false);
			SystemUsage systemUsage = broker.getSystemUsage();
			systemUsage.getStoreUsage().setLimit(1024 * 1024 * 8);
			systemUsage.getTempUsage().setLimit(1024 * 1024 * 8);
			broker.start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void tearDown() throws JMSException {
		session.close();
		connection.close();
	}
}
