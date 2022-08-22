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
package org.eclipse.basyx.components.aas.aascomponent;

import java.util.List;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAPIFactory;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregatorFactory;
import org.eclipse.basyx.components.aas.mongodb.MongoDBSubmodelAPIFactory;
import org.eclipse.basyx.components.aas.mongodb.MongoDBSubmodelAggregatorFactory;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * 
 * Factory building the AASAggregator for the AASComponent with given decorators
 * for the MongoDB backend
 * 
 * @author fischer, fried
 *
 */
public class MongoDBAASServerComponentFactory extends AbstractAASServerComponentFactory {

	private BaSyxMongoDBConfiguration mongoDBConfig;
	private MongoClient client;

	public MongoDBAASServerComponentFactory(BaSyxMongoDBConfiguration config, List<IAASServerDecorator> decorators, IAASRegistry aasServerRegistry) {
		this.mongoDBConfig = config;
		this.aasServerRegistry = aasServerRegistry;
		this.aasServerDecorators = decorators;
		this.client = MongoClients.create(config.getConnectionUrl());
	}

	public MongoDBAASServerComponentFactory(BaSyxMongoDBConfiguration config, IAASRegistry aasServerRegistry) {
		this.mongoDBConfig = config;
		this.aasServerRegistry = aasServerRegistry;
		this.client = MongoClients.create(config.getConnectionUrl());
	}

	@Override
	protected ISubmodelAPIFactory createSubmodelAPIFactory() {
		return new MongoDBSubmodelAPIFactory(mongoDBConfig, client);
	}

	@Override
	protected ISubmodelAggregatorFactory createSubmodelAggregatorFactory(ISubmodelAPIFactory submodelAPIFactory) {
		return new MongoDBSubmodelAggregatorFactory(mongoDBConfig, submodelAPIFactory, client);
	}

	@Override
	protected IAASAPIFactory createAASAPIFactory() {
		return new MongoDBAASAPIFactory(mongoDBConfig, client);
	}

	@Override
	protected IAASAggregatorFactory createAASAggregatorFactory(IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		return new MongoDBAASAggregatorFactory(mongoDBConfig, aasServerRegistry, aasAPIFactory, submodelAggregatorFactory, client);
	}
}
