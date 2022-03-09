/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.mongodb;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;

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

	public MongoDBAASAggregatorFactory(BaSyxMongoDBConfiguration config, IAASRegistry registry, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this.config = config;
		this.registry = registry;
		this.aasAPIFactory = aasAPIFactory;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
	}

	public MongoDBAASAggregatorFactory(BaSyxMongoDBConfiguration config, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this.config = config;
		this.aasAPIFactory = aasAPIFactory;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
	}

	public MongoDBAASAggregatorFactory(String resourceConfigPath, IAASRegistry registry, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this.resourceConfigPath = resourceConfigPath;
		this.registry = registry;
		this.aasAPIFactory = aasAPIFactory;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
	}

	public MongoDBAASAggregatorFactory(String resourceConfigPath, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this.resourceConfigPath = resourceConfigPath;
		this.aasAPIFactory = aasAPIFactory;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
	}

	public MongoDBAASAggregatorFactory(IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH, aasAPIFactory, submodelAggregatorFactory);
	}

	@Override
	public IAASAggregator create() {
		if (this.config != null && this.registry != null) {
			return new MongoDBAASAggregator(this.config, this.registry, this.aasAPIFactory, this.submodelAggregatorFactory);
		} else if (this.config != null) {
			return new MongoDBAASAggregator(this.config, this.aasAPIFactory, this.submodelAggregatorFactory);
		} else if (this.resourceConfigPath != null && this.registry != null) {
			return new MongoDBAASAggregator(this.resourceConfigPath, this.registry, this.aasAPIFactory, this.submodelAggregatorFactory);
		} else {
			return new MongoDBAASAggregator(this.resourceConfigPath, this.aasAPIFactory, this.submodelAggregatorFactory);
		}
	}

}
