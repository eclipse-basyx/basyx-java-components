package org.eclipse.basyx.regression.registry;

import java.io.IOException;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.proxy.TaggedDirectoryProxy;
import org.eclipse.basyx.testsuite.regression.extensions.aas.directory.tagged.TestTaggedDirectorySuite;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class TestMongoDBTaggedDirectoryComponent extends TestTaggedDirectorySuite {
	private static final String PATH = "mongodbRegistry.properties";
	private static RegistryComponent taggedDirectoryComponent;

	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		taggedDirectoryComponent = createTaggedDirectoryComponent();
		taggedDirectoryComponent.startComponent();
	}

	@AfterClass
	public static void tearDownClass() throws MqttException, IOException {
		taggedDirectoryComponent.stopComponent();
		cleanupMongoDB();
	}

	private static void cleanupMongoDB() {
		BaSyxMongoDBConfiguration mongoDBConfig = new BaSyxMongoDBConfiguration();
		mongoDBConfig.loadFromResource(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH);
		MongoClient client = MongoClients.create(mongoDBConfig.getConnectionUrl());
		MongoOperations mongoOps = new MongoTemplate(client, mongoDBConfig.getDatabase());
		mongoOps.dropCollection(mongoDBConfig.getRegistryCollection());
	}

	private static RegistryComponent createTaggedDirectoryComponent() {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		BaSyxRegistryConfiguration taggedDirectoryConfig = new BaSyxRegistryConfiguration();
		taggedDirectoryConfig.loadFromResource(PATH);

		return new RegistryComponent(contextConfig, taggedDirectoryConfig);
	}

	@Override
	protected IAASTaggedDirectory getDirectory() {
		return new TaggedDirectoryProxy(new BaSyxContextConfiguration().getUrl());
	}

	@Override
	protected IAASRegistry getRegistryService() {
		return getDirectory();
	}

}
