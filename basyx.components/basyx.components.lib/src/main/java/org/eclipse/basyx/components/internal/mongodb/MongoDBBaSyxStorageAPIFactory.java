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

package org.eclipse.basyx.components.internal.mongodb;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * 
 * @author fischer, jung
 * 
 * @param <T>
 *            Generic type of the objects to be managed by the produced API
 */
public class MongoDBBaSyxStorageAPIFactory<T> {
	private static Map<String, MongoClient> knownClients = new HashMap<>();

	private final BaSyxMongoDBConfiguration config;
	private final Class<T> type;
	private final String collectionName;
	private final MongoClient client;

	/**
	 * Constructor for a generic BaSyxMongoDBAPIFactory
	 * 
	 * @param config
	 *            BaSyx MongoDB Configuration
	 * @param type
	 *            Must be the exact same type as the type of the generic parameter
	 *            {@code <T>}
	 * @param collectionName
	 *            The name of the collection, managed by the produced API
	 */
	public MongoDBBaSyxStorageAPIFactory(BaSyxMongoDBConfiguration config, Class<T> type, String collectionName) {
		this.config = config;
		this.type = type;
		this.collectionName = collectionName;
		this.client = null;
	}

	/**
	 * Constructor for a generic BaSyxMongoDBAPIFactory
	 * 
	 * @param config
	 *            BaSyx MongoDB Configuration
	 * @param type
	 *            Must be the exact same type as the type of the generic parameter
	 *            {@code <T>}
	 * @param collectionName
	 *            The name of the collection, managed by the produced API
	 * @param client
	 *            The client of the MongoDB connection
	 */
	public MongoDBBaSyxStorageAPIFactory(BaSyxMongoDBConfiguration config, Class<T> type, String collectionName, MongoClient client) {
		this.config = config;
		this.type = type;
		this.collectionName = collectionName;
		this.client = client;
	}

	/**
	 * Creates a generic MongoDBBaSyxStorageAPI. This method has been designed to
	 * ensure efficient resource utilization by reusing existing storage clients if
	 * they already exist.
	 * 
	 * @param <T>
	 * @param config
	 *            BaSyx MongoDB Configuration
	 * @param type
	 *            Must be the exact same type as the type of the generic parameter
	 *            {@code <T>}
	 * @param collectionName
	 *            The name of the collection, managed by the produced API
	 * @param client
	 *            The client of the MongoDB connection
	 * @return
	 */
	public static synchronized <T> MongoDBBaSyxStorageAPI<T> create(String collectionName, Class<T> type, BaSyxMongoDBConfiguration config, MongoClient client) {
		String connectionUrl = config.getConnectionUrl();
		if (!knownClients.containsKey(connectionUrl)) {
			knownClients.put(connectionUrl, client);
		}
		return new MongoDBBaSyxStorageAPI<T>(collectionName, type, config, knownClients.get(connectionUrl));
	}

	/**
	 * Creates a generic MongoDBBaSyxStorageAPI. If an appropriate MongoClient
	 * already exists, it will be reused.
	 * 
	 * @param <T>
	 * @param config
	 *            BaSyx MongoDB Configuration
	 * @param type
	 *            Must be the exact same type as the type of the generic parameter
	 *            {@code <T>}
	 * @param collectionName
	 *            The name of the collection, managed by the produced API
	 * @return
	 */
	public static synchronized <T> MongoDBBaSyxStorageAPI<T> create(String collectionName, Class<T> type, BaSyxMongoDBConfiguration config) {
		String connectionUrl = config.getConnectionUrl();
		return knownClients.containsKey(connectionUrl)
				? create(collectionName, type, config, knownClients.get(connectionUrl))
				: create(collectionName, type, config, createNewClient(config));
	}

	private static MongoClient createNewClient(BaSyxMongoDBConfiguration config) {
		return MongoClients.create(config.getConnectionUrl());
	}

	/**
	 * Creates a generic MongoDBBaSyxStorageAPI. If an appropriate MongoClient
	 * already exists, it will be reused
	 * 
	 * @return
	 */
	public MongoDBBaSyxStorageAPI<T> create() {
		return this.client == null 
				? MongoDBBaSyxStorageAPIFactory.<T>create(collectionName, type, config)
				: MongoDBBaSyxStorageAPIFactory.<T>create(collectionName, type, config, client);
	}
}
