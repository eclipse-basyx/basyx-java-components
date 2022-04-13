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
package org.eclipse.basyx.components.registry.mongodb;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.registration.memory.IRegistryHandler;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * A registry handler based on MongoDB
 * 
 * @author espen
 */
public class MongoDBRegistryHandler implements IRegistryHandler {
	private static final String DEFAULT_CONFIG_PATH = "mongodb.properties";

	protected BaSyxMongoDBConfiguration config;
	protected MongoOperations mongoOps;
	protected String collection;

	private static final String AASID = Identifiable.IDENTIFICATION + "." + Identifier.ID;
	private static final String ASSETID = AASDescriptor.ASSET + "." + Identifiable.IDENTIFICATION + "." + Identifier.ID;

	/**
	 * Receives the path of the configuration.properties file in it's constructor.
	 * 
	 * @param config
	 */
	public MongoDBRegistryHandler(BaSyxMongoDBConfiguration config) {
		this.setConfiguration(config);
	}

	/**
	 * Receives the path of the .properties file in it's constructor from a
	 * resource.
	 */
	public MongoDBRegistryHandler(String resourceConfigPath) {
		config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(resourceConfigPath);
		this.setConfiguration(config);
	}

	/**
	 * Constructor using default sql connections
	 */
	public MongoDBRegistryHandler() {
		this(DEFAULT_CONFIG_PATH);
	}

	public void setConfiguration(BaSyxMongoDBConfiguration config) {
		this.config = config;
		MongoClient client = MongoClients.create(config.getConnectionUrl());
		this.mongoOps = new MongoTemplate(client, config.getDatabase());
		this.collection = config.getRegistryCollection();
	}

	@Override
	public boolean contains(IIdentifier identifier) {
		String id = identifier.getId();
		Criteria hasId = new Criteria();
		hasId.orOperator(where(AASID).is(id), where(ASSETID).is(id));
		return mongoOps.exists(query(hasId), collection);
	}

	@Override
	public void remove(IIdentifier identifier) {
		String id = identifier.getId();
		Criteria hasId = new Criteria();
		hasId.orOperator(where(AASID).is(id), where(ASSETID).is(id));
		mongoOps.remove(query(hasId), collection);
	}

	@Override
	public void insert(AASDescriptor descriptor) {
		mongoOps.insert(descriptor, collection);
	}

	@Override
	public void update(AASDescriptor descriptor) {
		String aasId = descriptor.getIdentifier().getId();
		Object result = mongoOps.findAndReplace(query(where(AASID).is(aasId)), descriptor, collection);
		if (result == null) {
			insert(descriptor);
		}
	}

	@Override
	public AASDescriptor get(IIdentifier identifier) {
		String id = identifier.getId();
		Criteria hasId = new Criteria();
		hasId.orOperator(where(AASID).is(id), where(ASSETID).is(id));
		AASDescriptor result = mongoOps.findOne(query(hasId), AASDescriptor.class, collection);
		if (result != null) {
			// Remove mongoDB-specific map attribute from AASDescriptor
			result.remove("_id");
		}
		return result;
	}

	@Override
	public List<AASDescriptor> getAll() {
		List<AASDescriptor> result = mongoOps.findAll(AASDescriptor.class, collection);
		// Remove mongoDB-specific map attribute from AASDescriptor
		result.forEach(desc -> desc.remove("_id"));
		return result;
	}
}
