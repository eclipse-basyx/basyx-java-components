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
package org.eclipse.basyx.components.aas.authorization;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.AuthorizedDecoratingAASAggregatorFactory;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.IAASAggregatorAuthorizer;
import org.eclipse.basyx.extensions.aas.api.authorization.AuthorizedDecoratingAASAPIFactory;
import org.eclipse.basyx.extensions.aas.api.authorization.IAASAPIAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.AuthorizedDecoratingSubmodelAggregatorFactory;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.ISubmodelAggregatorAuthorizer;
import org.eclipse.basyx.extensions.submodel.authorization.AuthorizedDecoratingSubmodelAPIFactory;
import org.eclipse.basyx.extensions.submodel.authorization.ISubmodelAPIAuthorizer;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

/**
 * 
 * Decorator for Authorization of Submodel and Shell access
 * 
 * @author fischer, fried, wege
 *
 */
public class AuthorizedAASServerDecorator<SubjectInformationType> implements IAASServerDecorator {
	protected final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer;
	protected final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer;
	protected final IAASAPIAuthorizer<SubjectInformationType> aasAPIAuthorizer;
	protected final IAASAggregatorAuthorizer<SubjectInformationType> aasAggregatorAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedAASServerDecorator(
			final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer,
			final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer,
			final IAASAPIAuthorizer<SubjectInformationType> aasAPIAuthorizer,
			final IAASAggregatorAuthorizer<SubjectInformationType> aasAggregatorAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		this.submodelAPIAuthorizer = submodelAPIAuthorizer;
		this.submodelAggregatorAuthorizer = submodelAggregatorAuthorizer;
		this.aasAPIAuthorizer = aasAPIAuthorizer;
		this.aasAggregatorAuthorizer = aasAggregatorAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	@Override
	public ISubmodelAPIFactory decorateSubmodelAPIFactory(ISubmodelAPIFactory submodelAPIFactory) {
		return new AuthorizedDecoratingSubmodelAPIFactory<>(
				null,
				submodelAPIFactory,
				submodelAPIAuthorizer,
				subjectInformationProvider
		);
	}

	@Override
	public ISubmodelAggregatorFactory decorateSubmodelAggregatorFactory(ISubmodelAggregatorFactory submodelAggregatorFactory) {
		return new AuthorizedDecoratingSubmodelAggregatorFactory<>(
				null,
				submodelAggregatorFactory,
				submodelAggregatorAuthorizer,
				subjectInformationProvider
		);
	}

	@Override
	public IAASAPIFactory decorateAASAPIFactory(IAASAPIFactory aasAPIFactory) {
		return new AuthorizedDecoratingAASAPIFactory<>(
				aasAPIFactory,
				aasAPIAuthorizer,
				subjectInformationProvider
		);
	}

	@Override
	public IAASAggregatorFactory decorateAASAggregatorFactory(IAASAggregatorFactory aasAggregatorFactory) {
		return new AuthorizedDecoratingAASAggregatorFactory<>(
				aasAggregatorFactory,
				aasAggregatorAuthorizer,
				subjectInformationProvider
		);
	}

}
