/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregator;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.registry.mongodb.MongoDBRegistryHandler;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.aas.aggregator.AASAggregatorSuite;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.xml.sax.SAXException;
import org.eclipse.basyx.aas.registration.memory.AASRegistry;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


public class TestMongoDBAggregator extends AASAggregatorSuite {
	private static final Identifier SM_IDENTIFICATION = new Identifier(IdentifierType.CUSTOM, "MongoDBId");
	private static final String SM_IDSHORT = "MongoDB";
	private static AASServerComponent component;
	private static BaSyxMongoDBConfiguration mongoDBConfig;
	private static BaSyxContextConfiguration contextConfig;
	private static BaSyxAASServerConfiguration aasConfig;
	private static IAASRegistry registry;
	protected static ConnectedAssetAdministrationShellManager manager;
	protected static String aasId = "testId";
	
	@BeforeClass
	public static void setUpClass() throws ParserConfigurationException, SAXException, IOException {
		initConfiguration();
		resetMongoDBTestData();
		
		component = new AASServerComponent(contextConfig, aasConfig, mongoDBConfig);
		registry = new AASRegistry(new MongoDBRegistryHandler("mongodb.properties"));
		
		IConnectorFactory connectorFactory = new HTTPConnectorFactory();
	
		manager = new ConnectedAssetAdministrationShellManager(registry, connectorFactory);
		
		component.setRegistry(registry);
		component.startComponent();
	}
	
	private static void initConfiguration() {
		mongoDBConfig = new BaSyxMongoDBConfiguration();
		mongoDBConfig.setAASCollection("basyxTestAAS");
		mongoDBConfig.setSubmodelCollection("basyxTestSM");
		
		contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		
		aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.MONGODB, "");
	}
	
	private static void resetMongoDBTestData() {
		new MongoDBAASAggregator(mongoDBConfig).reset();
	}

	@Test
	public void testDeleteReachesDatabase() {
		final BaSyxMongoDBConfiguration config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH);
		
		final MongoClient client = MongoClients.create(config.getConnectionUrl());
		
		final MongoTemplate mongoOps = new MongoTemplate(client, config.getDatabase());
		
		final String aasCollection = config.getAASCollection();
		
		final IAASAggregator aggregator = getAggregator();

		// initial state: no data in the database
		{
			final List<AssetAdministrationShell> data = mongoOps.findAll(AssetAdministrationShell.class, aasCollection);
			assertEquals(0, data.size());
		}

		// if we add one AAS
		{
			aggregator.createAAS(aas1);
		}

		// there should be that single AAS in the database
		{
			final List<AssetAdministrationShell> data = mongoOps.findAll(AssetAdministrationShell.class, aasCollection);
			
			assertEquals(1, data.size());
			assertEquals(aas1.getIdentification(), data.get(0).getIdentification());
		}

		// if we delete that AAS
		{
			aggregator.deleteAAS(aas1.getIdentification());
		}

		// there should be no AAS in the database
		{
			final List<AssetAdministrationShell> data = mongoOps.findAll(AssetAdministrationShell.class, aasCollection);
			
			assertEquals(0, data.size());
		}
	}
	
	@Override
	protected IAASAggregator getAggregator() {
		MongoDBAASAggregator aggregator = new MongoDBAASAggregator(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH);
		aggregator.reset();

		return aggregator;
	}
	
	@Test
	public void checkForMongoDBAASAggregatorRegistryIsNotNull() throws Exception {
		MongoDBAASAggregator aggregator = new MongoDBAASAggregator(mongoDBConfig, registry);
		
		assertEquals(false, aggregator.isRegistryNull());
	}
	
	@Test
	public void checkMongoDBAASAggregatorRegistryIsNull() throws Exception {
		MongoDBAASAggregator aggregator = new MongoDBAASAggregator(mongoDBConfig);

		assertEquals(true, aggregator.isRegistryNull());
	}
	
	@Test
	public void checkPersistencyOfAggregator() throws Exception {
		createAssetAdministrationShell();
		createSubmodel();

		MongoDBAASAggregator aggregator = new MongoDBAASAggregator(mongoDBConfig, registry);
		ISubmodel persistentSubmodel = getSubmodelFromAggregator(aggregator);

		assertEquals(SM_IDSHORT, persistentSubmodel.getIdShort());
	}
	
	private void createSubmodel() {
		Submodel sm = new Submodel(SM_IDSHORT, SM_IDENTIFICATION);
		manager.createSubmodel(new ModelUrn(aasId), sm);
	}
	
	@SuppressWarnings("unchecked")
	private ISubmodel getSubmodelFromAggregator(MongoDBAASAggregator aggregator) {
		IModelProvider aasProvider = aggregator.getAASProvider(new ModelUrn(aasId));
		
		Object submodelObject = aasProvider.getValue("/aas/submodels/" + SM_IDSHORT + "/submodel");
		
		ISubmodel persistentSubmodel = Submodel.createAsFacade((Map<String, Object>) submodelObject);
		
		return persistentSubmodel;
	}

	private void createAssetAdministrationShell() {
		AssetAdministrationShell assetAdministrationShell = new AssetAdministrationShell();
		
		IIdentifier identifier = new ModelUrn(aasId);
		
		assetAdministrationShell.setIdentification(identifier);
		assetAdministrationShell.setIdShort("aasIdShort");
		
		manager.createAAS(assetAdministrationShell, getURL());
	}
	
	protected String getURL() {
		return component.getURL() + "/shells";
	}
	
	@AfterClass
	public static void tearDownClass() {
		resetMongoDBTestData();
		component.stopComponent();
	}
}
