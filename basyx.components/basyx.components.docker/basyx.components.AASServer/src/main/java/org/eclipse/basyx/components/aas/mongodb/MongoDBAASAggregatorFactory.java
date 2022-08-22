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
package org.eclipse.basyx.components.aas.mongodb;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * 
 * Factory that constructs a MongoDBAASAggregator with the given attributes
 * 
 * @author fried
 *
 */
public class MongoDBAASAggregatorFactory implements IAASAggregatorFactory {

	private BaSyxMongoDBConfiguration config;
	private IAASRegistry registry;
	private IAASAPIFactory aasAPIFactory;
	private ISubmodelAggregatorFactory submodelAggregatorFactory;
	private String resourceConfigPath;
	private MongoClient client;

	@Deprecated
	public MongoDBAASAggregatorFactory(BaSyxMongoDBConfiguration config, IAASRegistry registry, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this(config, registry, aasAPIFactory, submodelAggregatorFactory, MongoClients.create(config.getConnectionUrl()));
	}

	@Deprecated
	public MongoDBAASAggregatorFactory(BaSyxMongoDBConfiguration config, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this(config, aasAPIFactory, submodelAggregatorFactory, MongoClients.create(config.getConnectionUrl()));
	}

	@Deprecated
	public MongoDBAASAggregatorFactory(String resourceConfigPath, IAASRegistry registry, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this.resourceConfigPath = resourceConfigPath;
		this.registry = registry;
		this.aasAPIFactory = aasAPIFactory;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.client = MongoClients.create(config.getConnectionUrl());
	}

	@Deprecated
	public MongoDBAASAggregatorFactory(String resourceConfigPath, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this.resourceConfigPath = resourceConfigPath;
		this.aasAPIFactory = aasAPIFactory;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.client = MongoClients.create(config.getConnectionUrl());
	}

	@Deprecated
	public MongoDBAASAggregatorFactory(IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH, aasAPIFactory, submodelAggregatorFactory);
	}

	public MongoDBAASAggregatorFactory(BaSyxMongoDBConfiguration config, IAASRegistry registry, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory, MongoClient client) {
		this.config = config;
		this.registry = registry;
		this.aasAPIFactory = aasAPIFactory;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.client = client;
	}

	public MongoDBAASAggregatorFactory(BaSyxMongoDBConfiguration config, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory, MongoClient client) {
		this.config = config;
		this.aasAPIFactory = aasAPIFactory;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.client = client;
	}

	public MongoDBAASAggregatorFactory(String resourceConfigPath, IAASRegistry registry, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory, MongoClient client) {
		this.resourceConfigPath = resourceConfigPath;
		this.registry = registry;
		this.aasAPIFactory = aasAPIFactory;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.client = client;
	}

	public MongoDBAASAggregatorFactory(String resourceConfigPath, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory, MongoClient client) {
		this.resourceConfigPath = resourceConfigPath;
		this.aasAPIFactory = aasAPIFactory;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.client = client;
	}

	public MongoDBAASAggregatorFactory(IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory, MongoClient client) {
		this(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH, aasAPIFactory, submodelAggregatorFactory);
	}

	@Override
	public IAASAggregator create() {
		if (this.config != null && this.registry != null) {
			return new MongoDBAASAggregator(this.config, this.registry, this.aasAPIFactory, this.submodelAggregatorFactory, this.client);
		} else if (this.config != null) {
			return new MongoDBAASAggregator(this.config, this.aasAPIFactory, this.submodelAggregatorFactory, this.client);
		} else if (this.resourceConfigPath != null && this.registry != null) {
			return new MongoDBAASAggregator(this.resourceConfigPath, this.registry, this.aasAPIFactory, this.submodelAggregatorFactory, this.client);
		} else {
			return new MongoDBAASAggregator(this.resourceConfigPath, this.aasAPIFactory, this.submodelAggregatorFactory, this.client);
		}
	}

}
