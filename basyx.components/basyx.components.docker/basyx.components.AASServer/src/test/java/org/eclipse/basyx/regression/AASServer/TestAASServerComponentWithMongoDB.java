/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.regression.AASServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.AASRegistry;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.aascomponent.MongoDBAASServerComponentFactory;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregator;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.registry.mongodb.MongoDBRegistryHandler;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Testing if AASs and SMs are getting registered from DB
 * after starting AAS Server Component and also if those registered AASs and SMs
 * are getting deregistered after stopping the AAS Server Component.
 *
 * @author danish
 *
 */
public class TestAASServerComponentWithMongoDB {
	private static AASServerComponent component;
	
	private static IAASRegistry registry;
	
	private static ConnectedAssetAdministrationShellManager manager;
	
	private static IAASAggregator aggregator;
	
	private static final String SM1_IDSHORT = "testSubmodel1IdShort";
	
	private static final String SM2_IDSHORT = "testSubmodel2IdShort";
	
	private static final String AAS_ID = "aasServerComponentTestId";
	
	private static final String AAS_IDSHORT = "aasServerComponentTestIdShort";
	
	private static BaSyxMongoDBConfiguration mongoDBConfig;
	private static BaSyxContextConfiguration contextConfig;
	private static BaSyxAASServerConfiguration serverConfig;
	
	private static IIdentifier aasIdentifier = new ModelUrn(AAS_ID);
	private static IIdentifier submodel1Identifier = new CustomId("submodelId1");
	private static IIdentifier submodel2Identifier = new CustomId("submodelId2");

	@BeforeClass
	public static void setUpClass() throws ParserConfigurationException, SAXException, IOException {
		initConfiguration();
		
		component = new AASServerComponent(contextConfig, serverConfig, mongoDBConfig);
		
		registry = new AASRegistry(new MongoDBRegistryHandler(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH));
		
		component.setRegistry(registry);
		
		aggregator = getAggregator();
		
		resetMongoDBTestData();
		
		manager = createConnectedAASManager(aggregator);
	}

	private static void initConfiguration() {
		mongoDBConfig = new BaSyxMongoDBConfiguration();
		mongoDBConfig.setAASCollection("basyxTestAASServerComponent");
		mongoDBConfig.setSubmodelCollection("basyxTestSMServerComponent");
		
		contextConfig = createBaSyxContextConfiguration();
		serverConfig = new BaSyxAASServerConfiguration(AASServerBackend.MONGODB, "");
	}
	
	private static BaSyxContextConfiguration createBaSyxContextConfiguration() {
		BaSyxContextConfiguration config = new BaSyxContextConfiguration();
		config.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		return config;
	}
	
	private static IAASAggregator getAggregator() {
		MongoDBAASAggregator aggregator = (MongoDBAASAggregator) createAASAggregator();
		aggregator.reset();

		return aggregator;
	}
	
	private static IAASAggregator createAASAggregator() {
			return new MongoDBAASServerComponentFactory(mongoDBConfig, new ArrayList<IAASServerDecorator>(), registry).create();
	}
	
	private static void resetMongoDBTestData() {
		((MongoDBAASAggregator) aggregator).reset();
	}
	
	private static ConnectedAssetAdministrationShellManager createConnectedAASManager(IAASAggregator aggregator) {
		return new ConnectedAssetAdministrationShellManager(registry, new IConnectorFactory() {

			@Override
			public IModelProvider getConnector(String addr) {
				return new AASAggregatorProvider(aggregator);
			}
		});
	}
		
	@Before
	public void init() {
		createAssetAdministrationShell();
		
		createSubmodel(SM1_IDSHORT, submodel1Identifier);
		createSubmodel(SM2_IDSHORT, submodel2Identifier);
		
		deregisterAASandSMsRegisteredInitiallyByManager();
	}
	
	private static void createAssetAdministrationShell() {
		AssetAdministrationShell assetAdministrationShell = new AssetAdministrationShell();

		assetAdministrationShell.setIdentification(aasIdentifier);
		assetAdministrationShell.setIdShort(AAS_IDSHORT);
		
		aggregator.createAAS(assetAdministrationShell);
		
		manager.createAAS(assetAdministrationShell, "");
	}
	
	private static void createSubmodel(String idShort, IIdentifier submodelIdentifier) {
		Submodel submodel = new Submodel();
		submodel.setIdentification(submodelIdentifier);
		submodel.setIdShort(idShort);
		
		manager.createSubmodel(aasIdentifier, submodel);
	}

	private void deregisterAASandSMsRegisteredInitiallyByManager() {
		deleteSubmodelsFromRegistry();
		
		deleteAASFromRegistry();
	}

	private static void deleteSubmodelsFromRegistry() {
		registry.delete(aasIdentifier, submodel1Identifier);
		registry.delete(aasIdentifier, submodel2Identifier);
	}
	
	private static void deleteAASFromRegistry() {
		registry.delete(new ModelUrn(AAS_ID));
	}

	private static void startAASServerComponent() {
		component.startComponent();
	}

	private static void stopAASServerComponent() {
		component.stopComponent();
	}
	
	@Test
	public void checkIfAASPresentInDBIsRegisteredAfterStartingAASServerComponent() {
		startAASServerComponent();
		
		AASDescriptor aasDescriptor = registry.lookupAAS(aasIdentifier);
		assertEquals(aasIdentifier.getId(), aasDescriptor.getIdentifier().getId());
		
		stopAASServerComponent();
	}
	
	@Test
	public void checkIfSMPresentInDBIsRegisteredAfterStartingAASServerComponent() {
		startAASServerComponent();
		
		AASDescriptor aasDescriptor = registry.lookupAAS(aasIdentifier);
		
		Collection<SubmodelDescriptor> smDescriptor = aasDescriptor.getSubmodelDescriptors(); 
		assertEquals(2, smDescriptor.size());
		
		stopAASServerComponent();
	}
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Test
	public void checkIfAASandSMRegisteredAfterStartingAASServerComponentIsDeregisteredAfterComponentStop() {
		startAASServerComponent();
		
		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(1, aasDescriptors.size());
		
		stopAASServerComponent();
		
		exceptionRule.expect(ResourceNotFoundException.class);
		registry.lookupAAS(aasIdentifier);
	}
	
	@AfterClass
	public static void tearDownClass() {
		resetMongoDBTestData();
	}
}
