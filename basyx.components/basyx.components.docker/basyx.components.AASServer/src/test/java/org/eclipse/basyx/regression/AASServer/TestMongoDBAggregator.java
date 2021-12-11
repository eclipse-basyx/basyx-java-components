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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
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
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.xml.sax.SAXException;
import org.eclipse.basyx.aas.registration.memory.AASRegistry;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.logging.Logger;


public class TestMongoDBAggregator {
	private static final Logger LOGGER = Logger.getLogger(TestMongoDBAggregator.class.getName());
	private static final Identifier SM_IDENTIFICATION = new Identifier(IdentifierType.CUSTOM, "MongoDBId");
	private static final String SM_IDSHORT = "MongoDB";
	private static AASServerComponent component;
	private static BaSyxMongoDBConfiguration mongoDBConfig;
	private static BaSyxContextConfiguration contextConfig;
	private static BaSyxAASServerConfiguration aasConfig;
	private static IAASRegistry registry;
	protected static ConnectedAssetAdministrationShellManager manager;
	protected static String aasId = "testId";
	
	
	@Test
	public void mainTest()  throws ParserConfigurationException, SAXException, IOException{
		mongoDBConfig = new BaSyxMongoDBConfiguration();
		mongoDBConfig.setAASCollection("basyxTestAAS");
		mongoDBConfig.setSubmodelCollection("basyxTestSM");
		
		MongoDBAASAggregator aggregator = new MongoDBAASAggregator(mongoDBConfig, registry);
		ISubmodel persistentSubmodel = getSubmodelFromAggregator(aggregator);
		
		IAssetAdministrationShell shell = aggregator.getAAS(new ModelUrn(aasId));
		
		System.out.println("shell =" + shell);
	}
	
	@SuppressWarnings("unchecked")
	private ISubmodel getSubmodelFromAggregator(MongoDBAASAggregator aggregator) throws ParserConfigurationException, SAXException, IOException {
		MultiSubmodelProvider aasProvider = (MultiSubmodelProvider) aggregator.getAASProvider(new ModelUrn(aasId));
		
		Object omnd = getValue("/aas/submodels/" + SM_IDSHORT + "/submodel", aasProvider);
		
		Object submodelObject = aasProvider.getValue("/aas/submodels/" + SM_IDSHORT + "/submodel");
		
		aasProvider.removeProvider(SM_IDSHORT);
		System.out.println("provider : " + aasProvider.getClass());
		
		Object submodelObject3 = aasProvider.getValue("/aas/submodels/" + SM_IDSHORT + "/submodel");
		setUpClass();
		getTotalDistance();
		
		component.stopComponent();
		setUpClass();
		getTotalDistance();
		System.out.println("URL : " + component.getURL());
		
		Object submodelObject2 = aasProvider.getValue("/aas/submodels/" + SM_IDSHORT + "/submodel");
		System.out.println("aasProvider class : " + aasProvider.getClass());
		System.out.println("submodelObject : " + submodelObject);
		System.out.println("submodelObject2 : " + submodelObject2);
		
		ISubmodel persistentSubmodel = Submodel.createAsFacade((Map<String, Object>) submodelObject);
		
		return persistentSubmodel;
	}
	
	public Object getValue(String path, IModelProvider aasProvider) throws ProviderException {
		VABPathTools.checkPathForNull(path);
		path = VABPathTools.stripSlashes(path);
		String[] pathElements = VABPathTools.splitPath(path);
		if (pathElements.length > 0 && pathElements[0].equals("aas")) {
			if (pathElements.length == 1) {
				//return aas_provider.getValue("");
			}
			if (pathElements[1].equals(AssetAdministrationShell.SUBMODELS)) {
				if (pathElements.length == 2) {
					//return retrieveSubmodels();
				} else {
					System.out.println("Inside else");
//					IModelProvider provider = submodel_providers.get(pathElements[2]);

//					if (provider == null) {
//						// Get a model provider for the submodel in the registry
//						provider = getModelProvider(pathElements[2]);
//					}

					// - Retrieve submodel or property value
//					return provider.getValue(VABPathTools.buildPath(pathElements, 3));
				}
			} else {
				// Handle access to AAS
//				return aas_provider.getValue(VABPathTools.buildPath(pathElements, 1));
			}
		} else {
			throw new MalformedRequestException("The request " + path + " is not allowed for this endpoint");
		}
		return pathElements;
	}
	
	public void setUpClass() throws ParserConfigurationException, SAXException, IOException {
		initConfiguration();
//		resetMongoDBTestData();
		
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
	
	
    public void getTotalDistance() {
            String BASE_URL2 = "http://localhost:4001/aasServer/shells/testId/aas/submodels/MongoDB/submodel";
            LOGGER.info("Full URL : " + BASE_URL2 );
            Double totalDistance = 0.0;
            URL url = null;
            try {
                url = new URL(BASE_URL2);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                LOGGER.info("Response code : " + con.getResponseCode() );

                if(con.getResponseCode() == 200 || con.getResponseCode() == 201){
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    String data = sb.toString();
                    System.out.println(data);
//                    JSONObject json = new JSONObject(data.trim());
//                    System.out.println(json.getJSONArray("resourceSets").getJSONObject(0).getJSONArray("resources").getJSONObject(0).get("travelDistance"));
//                    totalDistance = (Double) json.getJSONArray("resourceSets").getJSONObject(0).getJSONArray("resources").getJSONObject(0).get("travelDistance");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	
	/*
	
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
		IAssetAdministrationShell shell = createAssetAdministrationShell();
		ISubmodel submodel = createSubmodel();

		MongoDBAASAggregator aggregator = new MongoDBAASAggregator(mongoDBConfig, registry);
		ISubmodel persistentSubmodel = getSubmodelFromAggregator(aggregator);

		assertEquals(SM_IDSHORT, persistentSubmodel.getIdShort());
	}
	
	private ISubmodel createSubmodel() {
		Submodel sm = new Submodel(SM_IDSHORT, SM_IDENTIFICATION);
		manager.createSubmodel(new ModelUrn(aasId), sm);
		return sm;
	}
	
	@SuppressWarnings("unchecked")
	private ISubmodel getSubmodelFromAggregator(MongoDBAASAggregator aggregator) {
		IModelProvider aasProvider = aggregator.getAASProvider(new ModelUrn(aasId));
		
		Object submodelObject = aasProvider.getValue("/aas/submodels/" + SM_IDSHORT + "/submodel");
		
		System.out.println("aasProvider class : " + aasProvider.getClass());
		
		ISubmodel persistentSubmodel = Submodel.createAsFacade((Map<String, Object>) submodelObject);
		
		return persistentSubmodel;
	}

	private IAssetAdministrationShell createAssetAdministrationShell() {
		AssetAdministrationShell assetAdministrationShell = new AssetAdministrationShell();
		
		IIdentifier identifier = new ModelUrn(aasId);
		
		assetAdministrationShell.setIdentification(identifier);
		assetAdministrationShell.setIdShort("aasIdShort");
		
		
		manager.createAAS(assetAdministrationShell, getURL());
		
		return assetAdministrationShell;
	}
	
	protected String getURL() {
		return component.getURL() + "/shells";
	}
	
	@AfterClass
	public static void tearDownClass() {
//		resetMongoDBTestData();
		component.stopComponent();
	}
	*/
}
