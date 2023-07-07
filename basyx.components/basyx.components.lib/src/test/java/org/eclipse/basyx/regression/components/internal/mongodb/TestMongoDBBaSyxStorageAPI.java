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
package org.eclipse.basyx.regression.components.internal.mongodb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPI;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPIFactory;
import org.eclipse.basyx.extensions.internal.storage.BaSyxStorageAPI;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.testsuite.regression.extensions.storage.BaSyxStorageAPISuite;
import org.junit.After;
import org.springframework.data.mongodb.core.MongoOperations;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class TestMongoDBBaSyxStorageAPI extends BaSyxStorageAPISuite {
	private final static String connectionString = "mongodb://localhost:27017";
	private final static String testSubmodelCollectioName = "testsubnmodels";

	private static BaSyxMongoDBConfiguration config = createTestConfig(connectionString, testSubmodelCollectioName);

	private static BaSyxMongoDBConfiguration createTestConfig(String connectionstring, String submodelcollectioname) {
		BaSyxMongoDBConfiguration config = new BaSyxMongoDBConfiguration();
		config.setConnectionUrl(connectionstring);
		config.setSubmodelCollection(submodelcollectioname);
		return config;
	}

	@After
	public void cleanUp() {
		this.storageAPI.deleteCollection();
	}

	@Override
	protected BaSyxStorageAPI<Submodel> getStorageAPI() {
		MongoDBBaSyxStorageAPIFactory<Submodel> storageAPIFactory = new MongoDBBaSyxStorageAPIFactory<Submodel>(config, Submodel.class, config.getSubmodelCollection());
		return storageAPIFactory.create();
	}

	@Override
	protected BaSyxStorageAPI<Submodel> getSecondStorageAPI() {
		// Please use the MongoDBBaSyxStorageAPIFactory in production code.
		MongoClient client = MongoClients.create(connectionString);
		return new MongoDBBaSyxStorageAPI<Submodel>(testSubmodelCollectioName, Submodel.class, config, client);
	}

	@Override
	public void createCollectionIfNotExists() {
		// Not Implemented for MongoDBBaSyxStorageAPI as Collections are created
		// dynamically.
	}

	@Override
	public void deleteCollection() {
		triggerCollectionCreation();
		MongoOperations mongoOps = (MongoOperations) storageAPI.getStorageConnection();
		assertTrue(mongoOps.collectionExists(testSubmodelCollectioName));

		this.storageAPI.deleteCollection();
		assertFalse(mongoOps.collectionExists(testSubmodelCollectioName));
	}

	private void triggerCollectionCreation() {
		this.storageAPI.createOrUpdate(testSubmodel);
	}
}
