/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.regression.AASServer.mongodb;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.AASRegistry;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
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
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.testsuite.regression.aas.aggregator.AASAggregatorSuite;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.xml.sax.SAXException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Testing various behaviors of MongoDBAASAggregator Using
 * MongoDBAASAggregator's constructors to set registry and adding AAS and
 * Submodels from MongoDB
 *
 * @author danish
 *
 */
public class TestMongoDBAggregator extends AASAggregatorSuite {

	protected static final String AAS_ID = "testId";
	private static final Identifier SM_IDENTIFICATION = new Identifier(IdentifierType.CUSTOM, "MongoDBId");
	private static final String SM_IDSHORT = "MongoDB";

	protected static final String AAS_ID_2 = "testId2";
	private static final Identifier SM_IDENTIFICATION_2 = new Identifier(IdentifierType.CUSTOM, "MongoDBId2");

	private static final String PREFIX_SUBMODEL_PATH = "/aas/submodels/";
	private static final String SUFFIX_SUBMODEL_PATH = "/submodel";

	private static AASServerComponent component;
	private static BaSyxMongoDBConfiguration mongoDBConfig;
	private static BaSyxContextConfiguration contextConfig;
	private static BaSyxAASServerConfiguration aasConfig;
	private static IAASRegistry registry;

	protected static ConnectedAssetAdministrationShellManager manager;

	@BeforeClass
	public static void setUpClass() throws ParserConfigurationException, SAXException, IOException {
		initConfiguration();
		resetMongoDBTestData();

		component = new AASServerComponent(contextConfig, aasConfig, mongoDBConfig);
		registry = new AASRegistry(new MongoDBRegistryHandler(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH));

		IConnectorFactory connectorFactory = new HTTPConnectorFactory();

		manager = new ConnectedAssetAdministrationShellManager(registry, connectorFactory);

		component.setRegistry(registry);
		component.startComponent();

		createAssetAdministrationShell(AAS_ID);
		createSubmodel(SM_IDSHORT, SM_IDENTIFICATION, AAS_ID);
	}

	private static void initConfiguration() {
		mongoDBConfig = new BaSyxMongoDBConfiguration();
		mongoDBConfig.setAASCollection("basyxTestAAS");
		mongoDBConfig.setSubmodelCollection("basyxTestSM");

		contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);

		aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.MONGODB, "");
	}

	@SuppressWarnings("deprecation")
	private static void resetMongoDBTestData() {
		new MongoDBAASAggregator(mongoDBConfig).reset();
	}

	private static void createAssetAdministrationShell(String aasId) {
		AssetAdministrationShell assetAdministrationShell = new AssetAdministrationShell();

		IIdentifier identifier = new ModelUrn(aasId);

		assetAdministrationShell.setIdentification(identifier);
		assetAdministrationShell.setIdShort("aasIdShort");

		manager.createAAS(assetAdministrationShell, getURL());
	}

	protected static String getURL() {
		return component.getURL();
	}

	private static void createSubmodel(String smIdShort, Identifier smIdentifier, String aasId) {
		Submodel sm = new Submodel(smIdShort, smIdentifier);
		manager.createSubmodel(new ModelUrn(aasId), sm);
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

	@SuppressWarnings("deprecation")
	@Override
	protected IAASAggregator getAggregator() {
		MongoDBAASAggregator aggregator = new MongoDBAASAggregator(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH);
		aggregator.reset();

		return aggregator;
	}

	@SuppressWarnings("deprecation")
	@Test
	public void checkInitialSetupAfterCreatingAndRegisteringAasAndSubmodel() {
		MongoDBAASAggregator aggregator = new MongoDBAASAggregator(mongoDBConfig);
		ISubmodel persistentSubmodel = getSubmodelFromAggregator(aggregator, AAS_ID, SM_IDSHORT);

		assertEquals(SM_IDSHORT, persistentSubmodel.getIdShort());
	}

	@SuppressWarnings("unchecked")
	private ISubmodel getSubmodelFromAggregator(IAASAggregator aggregator, String aasId, String smIdShort) {
		IModelProvider aasProvider = aggregator.getAASProvider(new ModelUrn(aasId));

		Object submodelObject = aasProvider.getValue(PREFIX_SUBMODEL_PATH + smIdShort + SUFFIX_SUBMODEL_PATH);

		ISubmodel persistentSubmodel = Submodel.createAsFacade((Map<String, Object>) submodelObject);
		return persistentSubmodel;
	}

	private void removeProviderFromMultiSubmodelProviderHashMap(MultiSubmodelProvider aasProvider, String smIdShort) {
		aasProvider.removeProvider(smIdShort);
	}

	@Test
	public void checkSupportForMultipleAasAndSameSubmodelIdShort() {
		IAASAggregator aggregator = new AASAggregatorProxy(getURL());

		createAASWithSubmodelWithCollidingIdShort();

		ISubmodel persistentSubmodel = getSubmodelFromAggregator(aggregator, AAS_ID, SM_IDSHORT);
		ISubmodel persistentSubmodel2 = getSubmodelFromAggregator(aggregator, AAS_ID_2, SM_IDSHORT);

		assertSubmodelsAreResolvedCorrectly(persistentSubmodel, persistentSubmodel2);
	}

	private void assertSubmodelsAreResolvedCorrectly(ISubmodel persistentSubmodel, ISubmodel persistentSubmodel2) {
		assertEquals(SM_IDSHORT, persistentSubmodel.getIdShort());
		assertEquals(SM_IDENTIFICATION, persistentSubmodel.getIdentification());

		assertEquals(SM_IDSHORT, persistentSubmodel2.getIdShort());
		assertEquals(SM_IDENTIFICATION_2, persistentSubmodel2.getIdentification());
	}

	private void createAASWithSubmodelWithCollidingIdShort() {
		createAssetAdministrationShell(AAS_ID);
		createSubmodel(SM_IDSHORT, SM_IDENTIFICATION, AAS_ID);

		createAssetAdministrationShell(AAS_ID_2);
		createSubmodel(SM_IDSHORT, SM_IDENTIFICATION_2, AAS_ID_2);
	}

	@SuppressWarnings("deprecation")
	@Test(expected = ResourceNotFoundException.class)
	public void checkForResourceNotFoundExceptionWhileNotPassingRegistryAfterServerRestart() {
		restartAasServer();

		MongoDBAASAggregator aggregator = new MongoDBAASAggregator(mongoDBConfig);

		MultiSubmodelProvider aasProvider = (MultiSubmodelProvider) getAssetAdministrationShellProviderFromMongoDBAggregator(aggregator, AAS_ID, SM_IDSHORT);

		aasProvider.getValue(PREFIX_SUBMODEL_PATH + SM_IDSHORT + SUFFIX_SUBMODEL_PATH);
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Test
	public void checkNoExceptionIsObservedAfterPassingRegistry() {
		restartAasServer();

		MongoDBAASAggregator aggregator = new MongoDBAASAggregator(mongoDBConfig, registry);
		MultiSubmodelProvider aasProvider = (MultiSubmodelProvider) getAssetAdministrationShellProviderFromMongoDBAggregator(aggregator, AAS_ID, SM_IDSHORT);

		Map<String, Object> submodelObject = (Map<String, Object>) aasProvider.getValue(PREFIX_SUBMODEL_PATH + SM_IDSHORT + SUFFIX_SUBMODEL_PATH);

		assertEquals(SM_IDSHORT, submodelObject.get(Referable.IDSHORT));
	}

	private void restartAasServer() {
		component.stopComponent();
		component.startComponent();
	}

	private IModelProvider getAssetAdministrationShellProviderFromMongoDBAggregator(MongoDBAASAggregator aggregator, String aasId, String smIdShort) {
		MultiSubmodelProvider aasProvider = (MultiSubmodelProvider) aggregator.getAASProvider(new ModelUrn(aasId));

		removeProviderFromMultiSubmodelProviderHashMap(aasProvider, smIdShort);

		return aasProvider;
	}

	@AfterClass
	public static void tearDownClass() {
		registry.delete(new ModelUrn(AAS_ID));
		registry.delete(new ModelUrn(AAS_ID_2));

		resetMongoDBTestData();

		component.stopComponent();
	}
}
