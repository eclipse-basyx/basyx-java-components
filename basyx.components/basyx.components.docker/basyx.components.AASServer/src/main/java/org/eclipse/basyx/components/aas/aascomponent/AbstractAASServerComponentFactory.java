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

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

public abstract class AbstractAASServerComponentFactory implements IAASServerComponentFactory {

	protected List<IAASServerDecorator> aasServerDecorators;
	protected IAASRegistry aasServerRegistry;

	@Override
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

	protected abstract ISubmodelAPIFactory createSubmodelAPIFactory();

	protected abstract ISubmodelAggregatorFactory createSubmodelAggregatorFactory(ISubmodelAPIFactory submodelAPIFactory);

	protected abstract IAASAPIFactory createAASAPIFactory();

	protected abstract IAASAggregatorFactory createAASAggregatorFactory(IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory);
}
