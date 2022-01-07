package org.eclipse.basyx.regression.AASServer;

import java.io.IOException;

import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.junit.BeforeClass;

public class TestAASServerWithInMemoryMqtt extends MqttAASServerSuite {
	@BeforeClass
	public static void setUpClass() throws IOException {
		BaSyxAASServerConfiguration serverConfig = new BaSyxAASServerConfiguration();
		genericSetupClass(serverConfig);
	}
}
