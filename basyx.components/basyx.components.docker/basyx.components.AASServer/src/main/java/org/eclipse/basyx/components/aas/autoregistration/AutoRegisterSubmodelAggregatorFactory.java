/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.components.aas.autoregistration;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * 
 * Factory for creating a {@link AutoRegisterSubmodelAggregator}
 * 
 * @author fried
 *
 */
public class AutoRegisterSubmodelAggregatorFactory implements ISubmodelAggregatorFactory {

	private ISubmodelAggregatorFactory aggregatorFactory;
	private IAASRegistry registry;
	private String endpoint;

	public AutoRegisterSubmodelAggregatorFactory(ISubmodelAggregatorFactory aggregatorFactory, IAASRegistry registry,
			String endpoint) {
		this.aggregatorFactory = aggregatorFactory;
		this.registry = registry;
		this.endpoint = endpoint;
	}

	@Override
	public ISubmodelAggregator create() {
		return aggregatorFactory.create();
	}

	@Override
	public ISubmodelAggregator create(IIdentifier aasIdentifier) {
		return new AutoRegisterSubmodelAggregator(aggregatorFactory.create(aasIdentifier), registry, aasIdentifier, endpoint);
	}

}
