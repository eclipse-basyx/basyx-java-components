package org.eclipse.basyx.regression.AASServer;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.xml.sax.SAXException;

public class TestAASServerWithMongoDb extends AASServerSuite {
	private static AASServerComponent component;

	@Override
	protected String getURL() {
		return component.getURL();
	}

	@BeforeClass
	public static void setUpClass() throws ParserConfigurationException, SAXException, IOException {
		BaSyxContextConfiguration contextConfig = createBaSyxContextConfiguration();
		BaSyxAASServerConfiguration serverConfig = new BaSyxAASServerConfiguration(AASServerBackend.MONGODB, BaSyxAASServerConfiguration.DEFAULT_SOURCE);
		component = new AASServerComponent(contextConfig, serverConfig);
		component.startComponent();
	}

	@AfterClass
	public static void tearDownClass() {
		component.stopComponent();
	}

	private static BaSyxContextConfiguration createBaSyxContextConfiguration() {
		BaSyxContextConfiguration config = new BaSyxContextConfiguration();
		config.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		return config;
	}

}
