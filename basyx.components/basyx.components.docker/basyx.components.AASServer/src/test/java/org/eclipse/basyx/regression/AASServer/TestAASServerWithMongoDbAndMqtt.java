package org.eclipse.basyx.regression.AASServer;

import java.io.IOException;

import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregator;
import org.junit.BeforeClass;

public class TestAASServerWithMongoDbAndMqtt extends MqttAASServerSuite {
	@SuppressWarnings("deprecation")
	@BeforeClass
	public static void setUpClass() throws IOException {
		new MongoDBAASAggregator().reset();
		BaSyxAASServerConfiguration serverConfig = new BaSyxAASServerConfiguration(AASServerBackend.MONGODB, BaSyxAASServerConfiguration.DEFAULT_SOURCE);
		genericSetupClass(serverConfig);
	}
}
