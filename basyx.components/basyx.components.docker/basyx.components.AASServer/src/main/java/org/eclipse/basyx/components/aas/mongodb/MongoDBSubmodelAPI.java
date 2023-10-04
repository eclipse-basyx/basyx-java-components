/*******************************************************************************
 * Copyright (C) 2021, 2023 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.components.aas.mongodb;

import org.eclipse.basyx.components.aas.internal.StorageSubmodelAPI;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPIFactory;
import org.eclipse.basyx.extensions.internal.storage.BaSyxStorageAPI;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.restapi.operation.DelegatedInvocationManager;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Implements the ISubmodelAPI for a mongoDB backend.
 * 
 * @author fischer
 */
public class MongoDBSubmodelAPI extends StorageSubmodelAPI {
	private static final String DEFAULT_CONFIG_PATH = "mongodb.properties";
	public static final String SMIDPATH = Identifiable.IDENTIFICATION + "." + Identifier.ID;

	protected DelegatedInvocationManager invocationHelper;

	protected BaSyxMongoDBConfiguration config;
	protected String collection;


	/**
	 * Receives the path of the configuration.properties file in its constructor.
	 * 
	 * @param config
	 */
	public MongoDBSubmodelAPI(BaSyxMongoDBConfiguration config, String submdoelIdentificationId, MongoClient client) {
		this(config, submdoelIdentificationId, new DelegatedInvocationManager(new HTTPConnectorFactory()), client);
	}

	/**
	 * Constructor using default MongoDB connections
	 */
	public MongoDBSubmodelAPI(String submodelIdentificationId, MongoClient client) {
		this(DEFAULT_CONFIG_PATH, submodelIdentificationId, client);
	}

	public MongoDBSubmodelAPI(String submodelIdentificationId, DelegatedInvocationManager invocationHelper, MongoClient client) {
		this(DEFAULT_CONFIG_PATH, submodelIdentificationId, invocationHelper, client);
	}

	// NEUER KONSTRUKTOR?
	public MongoDBSubmodelAPI(BaSyxStorageAPI<Submodel> storageAPI, String identificationId, BaSyxMongoDBConfiguration config) {
		super(storageAPI, identificationId, new DelegatedInvocationManager(new HTTPConnectorFactory()));
		this.setConfiguration(config);
	}

	public MongoDBSubmodelAPI(BaSyxStorageAPI<Submodel> storageAPI, String identificationId) {
		super(storageAPI, identificationId, new DelegatedInvocationManager(new HTTPConnectorFactory()));
	}

	/**
	 * Receives the path of the .properties file in its constructor from a resource.
	 */
	public MongoDBSubmodelAPI(String resourceConfigPath, String submodelIdentificationId, MongoClient client) {
		this(resourceConfigPath, submodelIdentificationId, new DelegatedInvocationManager(new HTTPConnectorFactory()), client);
	}

	public MongoDBSubmodelAPI(BaSyxMongoDBConfiguration config, String submodelIdentificationId, DelegatedInvocationManager invocationHelper, MongoClient client) {
		super(createSubmodelStorageAPI(config, client), submodelIdentificationId, invocationHelper);
		this.setConfiguration(config);
		this.setSubmodelId(submodelIdentificationId);
		this.invocationHelper = invocationHelper;
	}

	public MongoDBSubmodelAPI(String resourceConfigPath, String submodelIdentificationId, DelegatedInvocationManager invocationHelper, MongoClient client) {
		super(createSubmodelStorageAPI(createConfig(resourceConfigPath), client), submodelIdentificationId, invocationHelper);
		this.config = createConfig(resourceConfigPath);
		this.setConfiguration(config);
		this.setSubmodelId(submodelIdentificationId);
		this.invocationHelper = invocationHelper;
	}

	/**
	 * Receives the path of the configuration.properties file in its constructor.
	 * 
	 * @param config
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBSubmodelAPI(BaSyxMongoDBConfiguration config, String submodelIdentificationId) {
		this(config, submodelIdentificationId, new DelegatedInvocationManager(new HTTPConnectorFactory()));
	}

	@Deprecated
	public MongoDBSubmodelAPI(BaSyxMongoDBConfiguration config, String submodelIdentificationId, DelegatedInvocationManager invocationHelper) {
		this(config, submodelIdentificationId, invocationHelper, MongoClients.create(config.getConnectionUrl()));
	}

	/**
	 * Receives the path of the .properties file in its constructor from a resource.
	 * 
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBSubmodelAPI(String resourceConfigPath, String submodelIdentificationId) {
		this(resourceConfigPath, submodelIdentificationId, new DelegatedInvocationManager(new HTTPConnectorFactory()));
	}

	/**
	 * Constructor using default MongoDB connections
	 * 
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBSubmodelAPI(String submodelIdentificationId) {
		this(DEFAULT_CONFIG_PATH, submodelIdentificationId);
	}

	@Deprecated
	public MongoDBSubmodelAPI(String submodelIdentificationId, DelegatedInvocationManager invocationHelper) {
		this(DEFAULT_CONFIG_PATH, submodelIdentificationId, invocationHelper);
	}

	@Deprecated
	public MongoDBSubmodelAPI(String resourceConfigPath, String submodelIdentificationId, DelegatedInvocationManager invocationHelper) {
		super(createSubmodelStorageAPI(createConfig(resourceConfigPath)), submodelIdentificationId, invocationHelper);
		this.config = createConfig(resourceConfigPath);
		this.setConfiguration(config);
		this.setSubmodelId(submodelIdentificationId);
		this.invocationHelper = invocationHelper;
	}

	private static BaSyxStorageAPI<Submodel> createSubmodelStorageAPI(BaSyxMongoDBConfiguration config) {
		MongoDBBaSyxStorageAPIFactory<Submodel> storageAPIFactory = new MongoDBBaSyxStorageAPIFactory<>(config, Submodel.class, config.getSubmodelCollection());
		return storageAPIFactory.create();
	}

	private static BaSyxStorageAPI<Submodel> createSubmodelStorageAPI(BaSyxMongoDBConfiguration config, MongoClient client) {
		MongoDBBaSyxStorageAPIFactory<Submodel> storageAPIFactory = new MongoDBBaSyxStorageAPIFactory<>(config, Submodel.class, config.getSubmodelCollection(), client);
		return storageAPIFactory.create();
	}

	private static BaSyxMongoDBConfiguration createConfig(String resourceConfigPath) {
		BaSyxMongoDBConfiguration config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(resourceConfigPath);
		return config;
	}

	/**
	 * Sets the db configuration for the submodel API.
	 * 
	 * @param config
	 */
	public void setConfiguration(BaSyxMongoDBConfiguration config) {
		this.storageApi = createSubmodelStorageAPI(config);
	}
}