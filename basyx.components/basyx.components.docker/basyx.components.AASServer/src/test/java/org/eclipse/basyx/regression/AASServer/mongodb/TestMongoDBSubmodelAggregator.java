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


package org.eclipse.basyx.regression.AASServer.mongodb;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.components.aas.mongodb.MongoDBSubmodelAPIFactory;
import org.eclipse.basyx.components.aas.mongodb.MongoDBSubmodelAggregator;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.testsuite.regression.submodel.aggregator.SubmodelAggregatorSuite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class TestMongoDBSubmodelAggregator extends SubmodelAggregatorSuite {

	private static MongoDBSubmodelAggregator aggregator;

	@BeforeClass
	public static void initialize() {
		BaSyxMongoDBConfiguration config = getMongoDBConfiguration();
		MongoClient client = MongoClients.create(config.getConnectionUrl());
		aggregator = new MongoDBSubmodelAggregator(new MongoDBSubmodelAPIFactory(config, client), config, client);
	}

	@Override
	protected ISubmodelAggregator getSubmodelAggregator() {
		return aggregator;
	}

	@Test
	public void deletedSubmodelIsRemovedFromDB() {
		Submodel toDelete = new Submodel("deleteMeIdShort", new CustomId("deleteMe"));

		getSubmodelAggregator().createSubmodel(toDelete);

		getSubmodelAggregator().deleteSubmodelByIdentifier(toDelete.getIdentification());

		assertSubmodelDoesNotExist(toDelete);
	}

	private void assertSubmodelDoesNotExist(Submodel toDelete) {
		BaSyxMongoDBConfiguration config = getMongoDBConfiguration();

		MongoTemplate mongoOps = getMongoTemplate(config);

		List<Submodel> submodels = mongoOps.findAll(Submodel.class, config.getSubmodelCollection());

		Optional<Submodel> possibleSm = submodels.stream().filter(sm -> sm.getIdentification().equals(toDelete.getIdentification())).findFirst();
		assertTrue(possibleSm.isEmpty());
	}

	private static MongoTemplate getMongoTemplate(BaSyxMongoDBConfiguration config) {
		MongoClient client = MongoClients.create(config.getConnectionUrl());
		MongoTemplate mongoOps = new MongoTemplate(client, config.getDatabase());
		return mongoOps;
	}

	private static BaSyxMongoDBConfiguration getMongoDBConfiguration() {
		BaSyxMongoDBConfiguration config = new BaSyxMongoDBConfiguration();
		config.setSubmodelCollection("basyxTestSMAggregator");
		return config;
	}
}
