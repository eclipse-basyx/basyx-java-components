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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAPI;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregator;
import org.eclipse.basyx.components.aas.mongodb.MongoDBSubmodelAPI;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.regression.AASServer.AASServerSuite;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.OperationProvider;
import org.eclipse.basyx.submodel.restapi.operation.DelegatedInvocationManager;
import org.eclipse.basyx.testsuite.regression.vab.gateway.ConnectorProviderStub;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Tests the component using the test suite
 * 
 * @author espen
 *
 */
public class TestMongoDBServer extends AASServerSuite {
	private static final Identifier SM_IDENTIFICATION = new Identifier(IdentifierType.CUSTOM, "MongoDBId");
	private static final String SM_IDSHORT = "MongoDB";

	private static final String DELEGATE_OP_ID_SHORT = "delegateOp";
	private static final String DELEGATE_OP_INVOKE_PATH = "delegateOp/invoke";
	private static final String OP_ID_SHORT = "op";

	private static AASServerComponent component;
	private static BaSyxMongoDBConfiguration mongoDBConfig;
	private static BaSyxContextConfiguration contextConfig;
	private static BaSyxAASServerConfiguration aasConfig;

	private boolean executed = false;

	@Override
	protected String getURL() {
		return component.getURL();
	}

	@BeforeClass
	public static void setUpClass() throws ParserConfigurationException, SAXException, IOException {
		initConfiguration();
		resetMongoDBTestData();
		component = new AASServerComponent(contextConfig, aasConfig, mongoDBConfig);
		component.startComponent();
	}

	@SuppressWarnings("deprecation")
	private static void resetMongoDBTestData() {
		new MongoDBAASAggregator(mongoDBConfig).reset();
	}

	private static void initConfiguration() {
		mongoDBConfig = new BaSyxMongoDBConfiguration();
		mongoDBConfig.setAASCollection("basyxTestAAS");
		mongoDBConfig.setSubmodelCollection("basyxTestSM");
		contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.MONGODB, "");
	}

	@Test
	public void testAddSubmodelPersistency() throws Exception {
		createAssetAdministrationShell();
		createSubmodel();
		checkIfSubmodelHasBeenPersisted(SM_IDENTIFICATION);
		checkSubmodelReferencesSize(1);
	}

	@Test
	public void testDeleteSubmodelPersistency() {
		createAssetAdministrationShell();
		createSubmodel();
		deleteSubmodel();
		checkSubmodelReferencesSize(0);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testAggregatorPersistency() throws Exception {
		createAssetAdministrationShell();
		createSubmodel();

		MongoDBAASAggregator aggregator = new MongoDBAASAggregator(mongoDBConfig);
		ISubmodel persistentSM = getSubmodelFromAggregator(aggregator);

		assertEquals(SM_IDSHORT, persistentSM.getIdShort());
	}

	@Test
	public void testInvokeDelegatedOperation() {
		createAssetAdministrationShell();
		createSubmodel();

		Operation op = new Operation(OP_ID_SHORT);
		op.setInvokable((Runnable) -> {
			executed = true;
		});
		OperationProvider opProvider = new OperationProvider(new VABMapProvider(op));

		ConnectorProviderStub connector = new ConnectorProviderStub();
		connector.addMapping(OP_ID_SHORT, opProvider);

		MongoDBSubmodelAPI api = new MongoDBSubmodelAPI(mongoDBConfig, SM_IDENTIFICATION.getId(), new DelegatedInvocationManager(connector));

		executed = false;
		api.invokeOperation(DELEGATE_OP_INVOKE_PATH);

		assertTrue(executed);
	}

	private void checkIfSubmodelHasBeenPersisted(Identifier smIdentification) {
		MongoDBSubmodelAPI smAPI = new MongoDBSubmodelAPI(mongoDBConfig, smIdentification.getId());
		ISubmodel persistentSM = smAPI.getSubmodel();
		assertEquals(SM_IDSHORT, persistentSM.getIdShort());
	}

	private void checkSubmodelReferencesSize(int expectedSize) {
		MongoDBAASAPI aasAPI = new MongoDBAASAPI(mongoDBConfig, shellIdentifier.getId());
		Collection<IReference> submodelReferences = aasAPI.getAAS().getSubmodelReferences();
		assertEquals(expectedSize, submodelReferences.size());
	}

	@SuppressWarnings("unchecked")
	private ISubmodel getSubmodelFromAggregator(MongoDBAASAggregator aggregator) {
		IModelProvider aasProvider = aggregator.getAASProvider(shellIdentifier);
		Object smObject = aasProvider.getValue("/aas/submodels/MongoDB/submodel");
		ISubmodel persistentSM = Submodel.createAsFacade((Map<String, Object>) smObject);
		return persistentSM;
	}

	private void createSubmodel() {
		Submodel sm = new Submodel(SM_IDSHORT, SM_IDENTIFICATION);
		Operation delegateOp = new Operation(DELEGATE_OP_ID_SHORT);
		Qualifier qualifier = DelegatedInvocationManager.createDelegationQualifier(OP_ID_SHORT);
		delegateOp.setQualifiers(Arrays.asList(qualifier));
		sm.addSubmodelElement(delegateOp);

		manager.createSubmodel(shellIdentifier, sm);
	}

	private void deleteSubmodel() {
		manager.deleteSubmodel(shellIdentifier, SM_IDENTIFICATION);
	}

	private void createAssetAdministrationShell() {
		AssetAdministrationShell shell = new AssetAdministrationShell();
		shell.setIdentification(shellIdentifier);
		shell.setIdShort("aasIdShort");
		manager.createAAS(shell, getURL());
	}

	@AfterClass
	public static void tearDownClass() {
		component.stopComponent();
	}
}
