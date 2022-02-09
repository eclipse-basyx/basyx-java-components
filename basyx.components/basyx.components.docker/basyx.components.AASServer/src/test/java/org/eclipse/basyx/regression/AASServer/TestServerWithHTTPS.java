package org.eclipse.basyx.regression.AASServer;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.https.HTTPSConnectorProvider;
import org.junit.Before;
import org.junit.BeforeClass;

public class TestServerWithHTTPS extends AASServerSuite {
	private static AASServerComponent component;

	@Override
	protected String getURL() {
		return component.getURL();
	}

	@BeforeClass
	public static void setUpClass() {
		BaSyxContextConfiguration contextConfig = createBaSyxContextConfiguration();
		BaSyxAASServerConfiguration serverConfig = new BaSyxAASServerConfiguration(AASServerBackend.INMEMORY, "xml/aas.xml");

		component = new AASServerComponent(contextConfig, serverConfig);
		component.startComponent();
	}

	@Override
	@Before
	public void setUp() {
		// Create a dummy registry to test integration of XML AAS
		aasRegistry = new InMemoryRegistry();

		IConnectorFactory connectorFactory = new HTTPSConnectorProvider();
		manager = new ConnectedAssetAdministrationShellManager(aasRegistry, connectorFactory);
	}

	private static BaSyxContextConfiguration createBaSyxContextConfiguration() {
		BaSyxContextConfiguration config = new BaSyxContextConfiguration();
		config.setSSLKeyPath("resources/ssl.cert");
		config.setSSLKeyPassword("pass123");
		return config;
	}

}
