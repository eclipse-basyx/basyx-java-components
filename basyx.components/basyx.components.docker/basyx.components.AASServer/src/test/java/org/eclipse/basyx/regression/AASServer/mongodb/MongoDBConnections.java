/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.AASServer.mongodb;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAPIFactory;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregator;
import org.eclipse.basyx.components.aas.mongodb.MongoDBSubmodelAPIFactory;
import org.eclipse.basyx.components.aas.mongodb.MongoDBSubmodelAggregator;
import org.eclipse.basyx.components.aas.mongodb.MongoDBSubmodelAggregatorFactory;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Test class to ensure that each Submodel/AAS Aggregator only has one active
 * connection. This test is ignored by default, as it is time consuming, as it
 * pushes thousands of Shells/Submodels to the MongoDB
 * 
 * @author fried
 *
 */
@Ignore
public class MongoDBConnections {
	private static MongoDBSubmodelAggregator aggregator;
	private static MongoDBAASAggregator aasAggregator;
	private int numberOfPushes = 10000; // Must be bigger than 5080, to test if the Server crashes.

	@BeforeClass
	public static void initialize() {
		BaSyxMongoDBConfiguration config = getMongoDBConfiguration();
		MongoClient submodelClient = MongoClients.create(config.getConnectionUrl());
		MongoClient aasClient = MongoClients.create(config.getConnectionUrl());
		aggregator = new MongoDBSubmodelAggregator(new MongoDBSubmodelAPIFactory(config, submodelClient), config, submodelClient);
		aasAggregator = new MongoDBAASAggregator(config, new MongoDBAASAPIFactory(config, aasClient), new MongoDBSubmodelAggregatorFactory(config, new MongoDBSubmodelAPIFactory(config, submodelClient), submodelClient), aasClient);
	}

	@Test
	public void testConnectionLimit() {
		for (int i = 0; i < numberOfPushes; i++) {
			Identifier aasIdentifier = new Identifier(IdentifierType.CUSTOM, "" + i);
			aasAggregator.createAAS(new AssetAdministrationShell("id_" + i, aasIdentifier, new Asset("assetId_" + i, new Identifier(IdentifierType.CUSTOM, "assetIdentifier_" + i), AssetKind.INSTANCE)));
			aasAggregator.deleteAAS(aasIdentifier);
			aggregator.createSubmodel(new Submodel("id_" + i, new Identifier(IdentifierType.CUSTOM, "" + i)));
			aggregator.deleteSubmodelByIdShort("id_"+i);
		}

	}

	private static BaSyxMongoDBConfiguration getMongoDBConfiguration() {
		BaSyxMongoDBConfiguration config = new BaSyxMongoDBConfiguration();
		config.setAASCollection("basyxTestAASAggregator2");
		config.setSubmodelCollection("basyxTestSMAggregator2");
		return config;
	}

}
