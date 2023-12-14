/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

import java.io.IOException;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregator;
import org.eclipse.basyx.components.aas.mqtt.MqttV2AASServerFeature;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.extensions.shared.encoding.Base64URLEncoder;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test MQTT Observer behavior with MongoDB as backend
 * 
 * @author mateusmolina
 * 
 */
public class TestAASServerWithMongoDBMqttV2 extends MqttV2AASServerSuite {

	private static BaSyxMongoDBConfiguration mongoDBConfig = buildBasyxMongoDBConfiguration("TestAASServerWithMongoDBMqttV2");
	private static IIdentifier mongoDBTestShell = new CustomId("mongoDBTestShellId");

	@BeforeClass
	public static void setUpClass() throws IOException {
		BaSyxAASServerConfiguration serverConfig = new BaSyxAASServerConfiguration();
		serverConfig.setAASBackend(AASServerBackend.MONGODB);

		startMqttBroker();
		BaSyxContextConfiguration contextConfig = createBaSyxContextConfiguration();
		BaSyxMqttConfiguration mqttConfig = createMqttConfig();

		resetMongoDBTestData();

		component = new AASServerComponent(contextConfig, serverConfig, mongoDBConfig);
		component.addAASServerFeature(new MqttV2AASServerFeature(mqttConfig, "MqttAASServerSuiteClientId", AAS_SERVER_ID, new Base64URLEncoder()));
		component.startComponent();
	}

	@Test
	public void observerIsNotTriggered_WhenGettingAAS() {
		addMongoDBTestShellToServer();

		int expectedMsgCounter = listener.msgCounter;

		restartAASServerComponent();
		manager.retrieveAAS(mongoDBTestShell);

		assertEquals(expectedMsgCounter, listener.msgCounter);
	}

	private void restartAASServerComponent() {
		component.stopComponent();
		component.startComponent();
	}

	private void addMongoDBTestShellToServer() {
		AssetAdministrationShell shell = createShell(mongoDBTestShell.getId(), mongoDBTestShell);
		manager.createAAS(shell, getURL());

		Submodel submodel = createSubmodel(submodelIdentifier.getId(), submodelIdentifier);
		manager.createSubmodel(mongoDBTestShell, submodel);
	}

	@SuppressWarnings("deprecation")
	private static void resetMongoDBTestData() {
		new MongoDBAASAggregator(mongoDBConfig).reset();
	}

	private static BaSyxMongoDBConfiguration buildBasyxMongoDBConfiguration(String collectionPrefix) {
		BaSyxMongoDBConfiguration mongoDBConfig;
		mongoDBConfig = new BaSyxMongoDBConfiguration();
		mongoDBConfig.setAASCollection(collectionPrefix + "_AAS");
		mongoDBConfig.setSubmodelCollection(collectionPrefix + "_SM");
		return mongoDBConfig;
	}

}
