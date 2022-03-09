/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.aascomponent;

import java.util.List;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAPIFactory;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregatorFactory;
import org.eclipse.basyx.components.aas.mongodb.MongoDBSubmodelAPIFactory;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

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

	public MongoDBAASServerComponentFactory(BaSyxMongoDBConfiguration config, List<IAASServerDecorator> decorators, IAASRegistry aasServerRegistry) {
		this.mongoDBConfig = config;
		this.aasServerRegistry = aasServerRegistry;
		this.aasServerDecorators = decorators;
	}

	public MongoDBAASServerComponentFactory(BaSyxMongoDBConfiguration config, IAASRegistry aasServerRegistry) {
		this.mongoDBConfig = config;
		this.aasServerRegistry = aasServerRegistry;
	}

	protected ISubmodelAPIFactory createSubmodelAPIFactory() {
		return new MongoDBSubmodelAPIFactory(mongoDBConfig);
	}

	protected ISubmodelAggregatorFactory createSubmodelAggregatorFactory(ISubmodelAPIFactory submodelAPIFactory) {
		return new SubmodelAggregatorFactory(submodelAPIFactory);
	}

	protected IAASAPIFactory createAASAPIFactory() {
		return new MongoDBAASAPIFactory(mongoDBConfig);
	}

	protected IAASAggregatorFactory createAASAggregatorFactory(IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		return new MongoDBAASAggregatorFactory(mongoDBConfig, aasServerRegistry, aasAPIFactory, submodelAggregatorFactory);
	}
}
