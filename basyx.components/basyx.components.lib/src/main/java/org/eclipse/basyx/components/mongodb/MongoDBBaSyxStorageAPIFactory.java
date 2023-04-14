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

package org.eclipse.basyx.components.mongodb;

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;

/**
 * 
 * @author fischer
 * 
 * @param <T>
 *            Generic type of the objects to be managed by the produced API
 */
public class MongoDBBaSyxStorageAPIFactory<T> {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final BaSyxMongoDBConfiguration CONFIG;
	private final Class<T> TYPE;
	private final String COLLECTION_NAME;
	private final MongoClient CLIENT;

	/**
	 * Constructor for a generic BaSyxS3APIFactory
	 * 
	 * @param config
	 *            BaSyx S3 Configuration
	 * @param type
	 *            Must be the exact same type as the type of the generic parameter
	 *            {@code <T>}
	 * @param collectionName
	 *            The name of the collection, managed by the produced API
	 */
	public MongoDBBaSyxStorageAPIFactory(BaSyxMongoDBConfiguration config, Class<T> type, String collectionName) {
		CONFIG = config;
		TYPE = type;
		COLLECTION_NAME = collectionName;
		CLIENT = null;
	}

	/**
	 * Constructor for a generic BaSyxS3APIFactory
	 * 
	 * @param config
	 *            BaSyx S3 Configuration
	 * @param type
	 *            Must be the exact same type as the type of the generic parameter
	 *            {@code <T>}
	 * @param collectionName
	 *            The name of the collection, managed by the produced API
	 * @param client
	 *            The client of the MongoDB connection
	 */
	public MongoDBBaSyxStorageAPIFactory(BaSyxMongoDBConfiguration config, Class<T> type, String collectionName, MongoClient client) {
		CONFIG = config;
		TYPE = type;
		COLLECTION_NAME = collectionName;
		CLIENT = client;
	}

	public MongoDBBaSyxStorageAPI<T> create() {
		logger.info("Create MongoDB client...");
		if (CLIENT == null) {
			return new MongoDBBaSyxStorageAPI<T>(COLLECTION_NAME, TYPE, CONFIG);
		} else {
			return new MongoDBBaSyxStorageAPI<T>(COLLECTION_NAME, TYPE, CONFIG, CLIENT);
		}
	}
}
