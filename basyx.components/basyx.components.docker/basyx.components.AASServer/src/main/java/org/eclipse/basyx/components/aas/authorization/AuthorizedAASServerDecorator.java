/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.authorization;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.AuthorizedDecoratingAASAggregatorFactory;
import org.eclipse.basyx.extensions.aas.api.authorization.AuthorizedDecoratingAASAPIFactory;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.AuthorizedDecoratingSubmodelAggregatorFactory;
import org.eclipse.basyx.extensions.submodel.authorization.AuthorizedDecoratingSubmodelAPIFactory;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

/**
 * 
 * Decorator for Authorization of Submodel and Shell access
 * 
 * @author fischer, fried
 *
 */
public class AuthorizedAASServerDecorator implements IAASServerDecorator {

	@Override
	public ISubmodelAPIFactory decorateSubmodelAPIFactory(ISubmodelAPIFactory submodelAPIFactory) {
		return new AuthorizedDecoratingSubmodelAPIFactory(submodelAPIFactory);
	}

	@Override
	public ISubmodelAggregatorFactory decorateSubmodelAggregatorFactory(ISubmodelAggregatorFactory submodelAggregatorFactory) {
		return new AuthorizedDecoratingSubmodelAggregatorFactory(submodelAggregatorFactory);
	}

	@Override
	public IAASAPIFactory decorateAASAPIFactory(IAASAPIFactory aasAPIFactory) {
		return new AuthorizedDecoratingAASAPIFactory(aasAPIFactory);
	}

	@Override
	public IAASAggregatorFactory decorateAASAggregatorFactory(IAASAggregatorFactory aasAggregatorFactory) {
		return new AuthorizedDecoratingAASAggregatorFactory(aasAggregatorFactory);
	}

}
