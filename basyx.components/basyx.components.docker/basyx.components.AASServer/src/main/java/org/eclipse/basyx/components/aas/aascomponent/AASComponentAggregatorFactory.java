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
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.aas.restapi.vab.VABAASAPIFactory;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;

/**
 * 
 * Factory building the AASAggregator for the AASComponent with given decorators
 * 
 * @author fischer, fried
 *
 */
public class AASComponentAggregatorFactory {

	private List<IAASServerDecorator> aasServerDecorators;
	private AASServerBackend aasServerBackend;
	private IAASRegistry aasServerRegistry;

	public void setAASServerDecorators(List<IAASServerDecorator> decorators) {
		this.aasServerDecorators = decorators;
	}

	public void setAASServerBackend(AASServerBackend aasServerBackend) {
		this.aasServerBackend = aasServerBackend;
	}

	public void setAASServerRegistry(IAASRegistry aasServerRegistry) {
		this.aasServerRegistry = aasServerRegistry;
	}

	public IAASAggregator create() {
		ISubmodelAPIFactory submodelAPIFactory = createAndDecorateSubmodelAPIFactory();

		ISubmodelAggregatorFactory submodelAggregatorFactory = createAndDecorateSubmodelAggregatorFactory(submodelAPIFactory);

		IAASAPIFactory aasAPIFactory = createAndDecorateAASAPIFactory();

		IAASAggregatorFactory aasAggregatorFactory = createAndDecorateAASAggregatorFactory(aasAPIFactory, submodelAggregatorFactory);

		return aasAggregatorFactory.create();
	}

	private ISubmodelAPIFactory createAndDecorateSubmodelAPIFactory() {
		ISubmodelAPIFactory submodelAPIFactory = createSubmodelAPIFactory();
		for (IAASServerDecorator aasServerDecorator : aasServerDecorators) {
			submodelAPIFactory = aasServerDecorator.decorateSubmodelAPIFactory(submodelAPIFactory);
		}

		return submodelAPIFactory;
	}

	private ISubmodelAggregatorFactory createAndDecorateSubmodelAggregatorFactory(ISubmodelAPIFactory submodelAPIFactory) {
		ISubmodelAggregatorFactory submodelAggregatorFactory = createSubmodelAggregatorFactory(submodelAPIFactory);
		for (IAASServerDecorator aasServerDecorator : aasServerDecorators) {
			submodelAggregatorFactory = aasServerDecorator.decorateSubmodelAggregatorFactory(submodelAggregatorFactory);
		}

		return submodelAggregatorFactory;
	}

	private IAASAPIFactory createAndDecorateAASAPIFactory() {
		IAASAPIFactory aasAPIFactory = createAASAPIFactory();
		for (IAASServerDecorator aasServerDecorator : aasServerDecorators) {
			aasAPIFactory = aasServerDecorator.decorateAASAPIFactory(aasAPIFactory);
		}

		return aasAPIFactory;
	}

	private IAASAggregatorFactory createAndDecorateAASAggregatorFactory(IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		IAASAggregatorFactory aasAggregatorFactory = createAASAggregatorFactory(aasAPIFactory, submodelAggregatorFactory);
		for (IAASServerDecorator aasServerDecorator : aasServerDecorators) {
			aasAggregatorFactory = aasServerDecorator.decorateAASAggregatorFactory(aasAggregatorFactory);
		}

		return aasAggregatorFactory;
	}

	private ISubmodelAPIFactory createSubmodelAPIFactory() {
		// TODO: take backend into account
		return new VABSubmodelAPIFactory();
	}

	private ISubmodelAggregatorFactory createSubmodelAggregatorFactory(ISubmodelAPIFactory submodelAPIFactory) {
		// TODO: take backend into account
		return new SubmodelAggregatorFactory(submodelAPIFactory);
	}

	private IAASAPIFactory createAASAPIFactory() {
		// TODO: take backend into account
		return new VABAASAPIFactory();
	}

	private IAASAggregatorFactory createAASAggregatorFactory(IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		// TODO: take backend into account
		return new AASAggregatorFactory(aasAPIFactory, submodelAggregatorFactory, aasServerRegistry);
	}
}
