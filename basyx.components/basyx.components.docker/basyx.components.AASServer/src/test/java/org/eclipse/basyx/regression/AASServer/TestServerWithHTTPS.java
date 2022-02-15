package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.fail;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.eclipse.basyx.vab.protocol.https.HTTPSConnectorProvider;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
		BaSyxContextConfiguration config = new BaSyxContextConfiguration(8080, "");
		config.setSSLKeyStoreLocation("resources/basyxtest.jks");
		config.setSSLKeyPassword("pass123");
		return config;
	}

	@Test
	public void testWithoutHTTPS() {
		IConnectorFactory httpConnectorFactory = new HTTPConnectorFactory();
		ConnectedAssetAdministrationShellManager httpManager = new ConnectedAssetAdministrationShellManager(aasRegistry, httpConnectorFactory);

		try {
			AssetAdministrationShell shell = createShell(shellIdentifier.getId(), shellIdentifier);
			httpManager.createAAS(shell, getURL());
			fail();
		} catch (ProviderException expected) {
		}
	}

	@AfterClass
	public static void tearDownClass() {
		component.stopComponent();
	}
}
