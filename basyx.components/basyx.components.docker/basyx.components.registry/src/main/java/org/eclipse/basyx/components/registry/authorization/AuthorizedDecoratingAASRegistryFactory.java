/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.components.registry.authorization;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.components.registry.registrycomponent.IAASRegistryFactory;
import org.eclipse.basyx.extensions.aas.registration.authorization.AuthorizedAASRegistry;
import org.eclipse.basyx.extensions.aas.registration.authorization.GrantedAuthorityAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.aas.registration.authorization.IAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;

/**
 * Api provider for constructing a new Submodel aggregator that is authorized
 * 
 * @author wege
 */
public class AuthorizedDecoratingAASRegistryFactory<SubjectInformationType> implements
    IAASRegistryFactory {
	protected final IAASRegistryFactory registryFactory;
	protected final IAASRegistryAuthorizer<SubjectInformationType> aasRegistryAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedDecoratingAASRegistryFactory(
			final IAASRegistryFactory registryFactory,
			final IAASRegistryAuthorizer<SubjectInformationType> aasRegistryAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		this.registryFactory = registryFactory;
		this.aasRegistryAuthorizer = aasRegistryAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	public AuthorizedDecoratingAASRegistryFactory(
			final IAASRegistryFactory registryFactory
	) {
		this(
				registryFactory,
				(IAASRegistryAuthorizer<SubjectInformationType>) new GrantedAuthorityAASRegistryAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
				(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider()
		);
	}

	@Override
	public IAASRegistry create() {
		return new AuthorizedAASRegistry<>(registryFactory.create(), aasRegistryAuthorizer, subjectInformationProvider);
	}
}