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

import static org.junit.Assert.assertSame;

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPI;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPIFactory;
import org.eclipse.basyx.testsuite.regression.extensions.storage.VABTestType;
import org.junit.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class TestMongoDBBaSyxStorageAPIFactory {
	static BaSyxMongoDBConfiguration config = createTestConfig("mongodb://localhost:27017");
	static MongoClient client = MongoClients.create(config.getConnectionUrl());

	private static BaSyxMongoDBConfiguration createTestConfig(String connectionUrl) {
		BaSyxMongoDBConfiguration config = new BaSyxMongoDBConfiguration();
		config.setConnectionUrl(connectionUrl);
		return config;
	}

	@Test
	public void assureFactoryReusesMongoClient() throws NoSuchFieldException, SecurityException {
		MongoDBBaSyxStorageAPI<VABTestType> storageAPI0 = new MongoDBBaSyxStorageAPIFactory<VABTestType>(config, VABTestType.class, "c0", client).create();
		MongoDBBaSyxStorageAPI<VABTestType> storageAPI1 = new MongoDBBaSyxStorageAPIFactory<VABTestType>(config, VABTestType.class, "c1", client).create();
		MongoDBBaSyxStorageAPI<VABTestType> storageAPI2 = new MongoDBBaSyxStorageAPIFactory<VABTestType>(config, VABTestType.class, "c2").create();
		MongoDBBaSyxStorageAPI<VABTestType> storageAPI3 = MongoDBBaSyxStorageAPIFactory.create("c3", VABTestType.class, config, client);
		MongoDBBaSyxStorageAPI<VABTestType> storageAPI4 = MongoDBBaSyxStorageAPIFactory.create("c4", VABTestType.class, config);

		assertSame(storageAPI0.getClient(), storageAPI1.getClient());
		assertSame(storageAPI0.getClient(), storageAPI2.getClient());
		assertSame(storageAPI0.getClient(), storageAPI3.getClient());
		assertSame(storageAPI0.getClient(), storageAPI4.getClient());
	}

	@Test
	public void dynamicCreatedFirst() {
		MongoDBBaSyxStorageAPI<VABTestType> storageAPI0 = new MongoDBBaSyxStorageAPIFactory<VABTestType>(config, VABTestType.class, "c0").create();
		MongoDBBaSyxStorageAPI<VABTestType> storageAPI1 = new MongoDBBaSyxStorageAPIFactory<VABTestType>(config, VABTestType.class, "c1", client).create();

		assertSame(storageAPI0.getClient(), storageAPI1.getClient());
	}

}
