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
package org.eclipse.basyx.components.registry.mongodb;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.registration.memory.IRegistryHandler;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPI;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPIFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * A registry handler based on MongoDB
 * 
 * @author espen, jungjan, witt
 */
public class MongoDBRegistryHandler implements IRegistryHandler {
	private static final String DEFAULT_CONFIG_PATH = "mongodb.properties";

	private MongoDBBaSyxStorageAPI<AASDescriptor> storageApi;

	private static final String AASID = Identifiable.IDENTIFICATION + "." + Identifier.ID;
	private static final String ASSETID = AASDescriptor.ASSET + "." + Identifiable.IDENTIFICATION + "." + Identifier.ID;

	/**
	 * Receives the path of the configuration.properties file in it's constructor.
	 * 
	 * @param config
	 */
	public MongoDBRegistryHandler(BaSyxMongoDBConfiguration config) {
		this.initStorageApi(config);
	}

	/**
	 * Receives the path of the .properties file in it's constructor from a
	 * resource.
	 */
	public MongoDBRegistryHandler(String resourceConfigPath) {
		this(configFromResource(resourceConfigPath));
	}

	private static BaSyxMongoDBConfiguration configFromResource(String resourceConfigPath) {
		BaSyxMongoDBConfiguration config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(resourceConfigPath);
		return config;
	}

	/**
	 * Constructor using default sql connections
	 */
	public MongoDBRegistryHandler() {
		this(DEFAULT_CONFIG_PATH);
	}

	public void setConfiguration(BaSyxMongoDBConfiguration config) {
		this.initStorageApi(config);
	}

	private void initStorageApi(BaSyxMongoDBConfiguration config) {
		String collectionName = config.getRegistryCollection();
		MongoDBBaSyxStorageAPIFactory<AASDescriptor> storageApiFactory = new MongoDBBaSyxStorageAPIFactory<>(config, AASDescriptor.class, collectionName);
		this.storageApi = storageApiFactory.create();
	}

	@Override
	public boolean contains(IIdentifier identifier) {
		String identificationId = identifier.getId();
		Criteria hasId = new Criteria();
		hasId.orOperator(where(AASID).is(identificationId), where(ASSETID).is(identificationId));

		return getStorageConnection().exists(query(hasId), this.storageApi.getCollectionName());
	}

	private MongoOperations getStorageConnection() {
		return (MongoOperations) this.storageApi.getStorageConnection();
	}

	@Override
	public void remove(IIdentifier identifier) {
		String indentificationId = identifier.getId();
		Criteria hasId = new Criteria();
		hasId.orOperator(where(AASID).is(indentificationId), where(ASSETID).is(indentificationId));
		getStorageConnection().remove(query(hasId), this.storageApi.getCollectionName());
	}

	@Override
	public void insert(AASDescriptor descriptor) {
		this.update(descriptor);
	}

	@Override
	public void update(AASDescriptor descriptor) {
		this.storageApi.createOrUpdate(descriptor);
	}

	@Override
	public AASDescriptor get(IIdentifier identifier) {
		String indentificationId = identifier.getId();
		Criteria hasId = new Criteria();
		hasId.orOperator(where(AASID).is(indentificationId), where(ASSETID).is(indentificationId));

		AASDescriptor result = getStorageConnection().findOne(query(hasId), AASDescriptor.class, this.storageApi.getCollectionName());
		return this.storageApi.handleMongoDbIdAttribute(result);
	}

	@Override
	public List<AASDescriptor> getAll() {
		return (List<AASDescriptor>) this.storageApi.retrieveAll();
	}
}
