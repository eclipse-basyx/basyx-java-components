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
package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.junit.Test;

/**
 * Tests if AASServerComponent correctly deregisteres automatically registered
 * AASs/SMs
 * 
 * @author conradi, danish
 *
 */
public class AASServerComponentTest {
	private static AASServerComponent component;
	private static IAASRegistry registry;

	private static final String XML_SOURCE = "xml/aas.xml";
	
	private static final String MULTIPLE_DIFFERENT_AAS_SERIALIZATION = "[\"json/aas.json\",\"aasx/01_Festo.aasx\",\"xml/aas.xml\"]";
	private static final String SINGLE_JSON_AAS_SERIALIZATION = "[\"json/aas.json\"]";
	private static final String EMPTY_JSON_ARRAY = "[]";
	private static final String EMPTY_STRING = "";
	
	private static BaSyxMongoDBConfiguration mongoDBConfig;
	private static BaSyxContextConfiguration contextConfig;
	private static BaSyxAASServerConfiguration aasConfig;
	
	private static ConnectedAssetAdministrationShellManager manager;
	
	private static final String SM1_IDSHORT = "testSubmodel1IdShort";
	private static final String SM2_IDSHORT = "testSubmodel2IdShort";
	private static final String AAS_ID = "aasServerComponentTestId";	
	private static final String AAS_IDSHORT = "aasServerComponentTestIdShort";
	
	private static IIdentifier aasIdentifier = new ModelUrn(AAS_ID);
	private static IIdentifier submodel1Identifier = new CustomId("submodelId1");
	private static IIdentifier submodel2Identifier = new CustomId("submodelId2");
	
	private static IAASAggregator aggregator;

	private static void setUp(String source) {
		initConfiguration(source);

		createAASServerComponentAndRegistry();
		
		createAndResetAggregator();
		
		resetMongoDBTestData();
		
		createConnectedAASManager();
	}
	
	private static void initConfiguration(String source) {
		mongoDBConfig = new BaSyxMongoDBConfiguration();
		mongoDBConfig.setAASCollection("basyxTestAASServerComponent");
		mongoDBConfig.setSubmodelCollection("basyxTestSMServerComponent");
		
		contextConfig = createBaSyxContextConfiguration();
		aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.MONGODB, source);
	}
	
	private static BaSyxContextConfiguration createBaSyxContextConfiguration() {
		BaSyxContextConfiguration config = new BaSyxContextConfiguration();
		config.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		return config;
	}

	private static void createAASServerComponentAndRegistry() {
		component = new AASServerComponent(contextConfig, aasConfig, mongoDBConfig);
		registry = new AASRegistry(new MongoDBRegistryHandler(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH));
		component.setRegistry(registry);
	}
	
	private static void createAndResetAggregator() {
		aggregator = createAASAggregator();
		((MongoDBAASAggregator) aggregator).reset();
	}
	
	private static IAASAggregator createAASAggregator() {
		return new MongoDBAASServerComponentFactory(mongoDBConfig, new ArrayList<IAASServerDecorator>(), registry).create();
	}
	
	private static void resetMongoDBTestData() {
		((MongoDBAASAggregator) aggregator).reset();
	}
	
	private static void createConnectedAASManager() {
		manager =  new ConnectedAssetAdministrationShellManager(registry, new IConnectorFactory() {

			@Override
			public IModelProvider getConnector(String addr) {
				return new AASAggregatorProvider(aggregator);
			}
		});
	}
	
	private static void doPresetting() {
		createAssetAdministrationShell();
		
		createSubmodel(SM1_IDSHORT, submodel1Identifier);
		createSubmodel(SM2_IDSHORT, submodel2Identifier);
		
		deregisterAASandSMsRegisteredInitiallyByManager();
	}
	
	private static void deregisterAASandSMsRegisteredInitiallyByManager() {
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

	@Test
	public void checkMultipleSerializedAasSourceOfDifferentTypes() {
		setUp(MULTIPLE_DIFFERENT_AAS_SERIALIZATION);
		
		startAASServerComponent();

		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(4, aasDescriptors.size());

		stopAASServerComponent();
	}

	@Test
	public void checkSingleSerializedAasJsonSource() {
		setUp(SINGLE_JSON_AAS_SERIALIZATION);
		
		startAASServerComponent();

		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(1, aasDescriptors.size());

		stopAASServerComponent();
	}

	@Test
	public void checkBehaviorWithEmptyJsonArray() {
		setUp(EMPTY_JSON_ARRAY);
		
		startAASServerComponent();

		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		System.out.println(aasDescriptors.size());
		assertEquals(0, aasDescriptors.size());

		stopAASServerComponent();
	}

	/**
	 * Tests if AASServerComponent deregisters all AASs/SMs that it registered
	 * automatically on startup
	 */
	@Test
	public void testServerCleanup() {
		setUp(XML_SOURCE);
		
		startAASServerComponent();

		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(2, aasDescriptors.size());

		stopAASServerComponent();

		// Try to lookup all previously registered AASs
		for (AASDescriptor aasDescriptor : aasDescriptors) {
			try {
				registry.lookupAAS(aasDescriptor.getIdentifier());
				fail();
			} catch (ResourceNotFoundException e) {
			}

			// Try to lookup all previously registered SMs
			for (SubmodelDescriptor smDescriptor : aasDescriptor.getSubmodelDescriptors()) {
				try {
					registry.lookupSubmodel(aasDescriptor.getIdentifier(), smDescriptor.getIdentifier());
					fail();
				} catch (ResourceNotFoundException e) {
				}
			}
		}
	}
	
	@Test
	public void aasPresentInDBIsRegisteredAfterStartingAASServerComponent() {
		setUp(EMPTY_STRING);
		
		doPresetting();
		
		startAASServerComponent();
		
		AASDescriptor aasDescriptor = registry.lookupAAS(aasIdentifier);
		assertEquals(aasIdentifier.getId(), aasDescriptor.getIdentifier().getId());
		
		stopAASServerComponent();
	}
	
	@Test
	public void submodelPresentInDBIsRegisteredAfterStartingAASServerComponent() {
		setUp(EMPTY_STRING);
		
		doPresetting();
		
		startAASServerComponent();
		
		AASDescriptor aasDescriptor = registry.lookupAAS(aasIdentifier);
		
		Collection<SubmodelDescriptor> smDescriptor = aasDescriptor.getSubmodelDescriptors(); 
		assertEquals(2, smDescriptor.size());
		
		stopAASServerComponent();
	}

	public void stopAASServerComponent() {
		component.stopComponent();
	}
	
	@AfterClass
	public static void tearDownClass() {
		resetMongoDBTestData();
	}
}