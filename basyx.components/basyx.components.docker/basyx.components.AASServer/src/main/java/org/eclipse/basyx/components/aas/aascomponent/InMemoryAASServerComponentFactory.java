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

import org.eclipse.basyx.aas.aggregator.AASAggregatorFactory;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.aas.restapi.vab.VABAASAPIFactory;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;

/**
 * 
 * Factory building the AASAggregator for the AASComponent with given decorators
 * for the InMemory backend
 * 
 * @author fischer, fried
 *
 */
public class InMemoryAASServerComponentFactory extends AbstractAASServerComponentFactory {

	public InMemoryAASServerComponentFactory(List<IAASServerDecorator> decorators, IAASRegistry aasServerRegistry) {
		this.aasServerRegistry = aasServerRegistry;
		this.aasServerDecorators = decorators;
	}

	public InMemoryAASServerComponentFactory(IAASRegistry aasServerRegistry) {
		this.aasServerRegistry = aasServerRegistry;
	}

	protected ISubmodelAPIFactory createSubmodelAPIFactory() {
		return new VABSubmodelAPIFactory();
	}

	protected ISubmodelAggregatorFactory createSubmodelAggregatorFactory(ISubmodelAPIFactory submodelAPIFactory) {
		return new SubmodelAggregatorFactory(submodelAPIFactory);
	}

	protected IAASAPIFactory createAASAPIFactory() {
		return new VABAASAPIFactory();
	}

	protected IAASAggregatorFactory createAASAggregatorFactory(IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		return new AASAggregatorFactory(aasAPIFactory, submodelAggregatorFactory, aasServerRegistry);
	}
}
