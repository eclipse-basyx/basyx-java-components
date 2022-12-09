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
package org.eclipse.basyx.components.aas.authorization.internal;

import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.authorization.AuthorizedAASServerFeature;
import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration;
import org.eclipse.basyx.components.security.authorization.internal.AuthorizationDynamicClassLoader;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.internal.GrantedAuthorityAASAggregatorAuthorizer;
import org.eclipse.basyx.extensions.aas.api.authorization.internal.GrantedAuthorityAASAPIAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.internal.IGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.internal.GrantedAuthoritySubmodelAggregatorAuthorizer;
import org.eclipse.basyx.extensions.submodel.authorization.internal.GrantedAuthoritySubmodelAPIAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specialization of {@link AuthorizedAASServerFeature} for the GrantedAuthority
 * authorization scheme.
 *
 * @author wege
 */
public class GrantedAuthorityAuthorizedAASServerFeature<SubjectInformationType> extends AuthorizedAASServerFeature<SubjectInformationType> {
	private static Logger logger = LoggerFactory.getLogger(GrantedAuthorityAuthorizedAASServerFeature.class);

	public GrantedAuthorityAuthorizedAASServerFeature(final BaSyxSecurityConfiguration securityConfig) {
		super(securityConfig);
	}

	@Override
	public IAASServerDecorator getDecorator() {
		logger.info("use GrantedAuthority authorization strategy");
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSubjectInformationProvider();
		final IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator = getGrantedAuthorityAuthenticator();

		return new AuthorizedAASServerDecorator<>(new GrantedAuthoritySubmodelAPIAuthorizer<>(grantedAuthorityAuthenticator), new GrantedAuthoritySubmodelAggregatorAuthorizer<>(grantedAuthorityAuthenticator),
				new GrantedAuthorityAASAPIAuthorizer<>(grantedAuthorityAuthenticator), new GrantedAuthorityAASAggregatorAuthorizer<>(grantedAuthorityAuthenticator), subjectInformationProvider);
	}

	@Override
	public AuthorizedDefaultServletParams<SubjectInformationType> getFilesAuthorizerParams() {
		logger.info("use GrantedAuthority authorization strategy for files authorizer");
		final IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator = getGrantedAuthorityAuthenticator();
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSubjectInformationProvider();

		return new AuthorizedDefaultServletParams<>(new GrantedAuthorityFilesAuthorizer<>(grantedAuthorityAuthenticator), subjectInformationProvider);
	}

	@SuppressWarnings("unchecked")
	private IGrantedAuthorityAuthenticator<SubjectInformationType> getGrantedAuthorityAuthenticator() {
		return AuthorizationDynamicClassLoader.loadInstanceDynamically(securityConfig, BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_GRANTED_AUTHORITY_GRANTED_AUTHORITY_AUTHENTICATOR,
				IGrantedAuthorityAuthenticator.class);
	}

	@SuppressWarnings("unchecked")
	private ISubjectInformationProvider<SubjectInformationType> getSubjectInformationProvider() {
		return AuthorizationDynamicClassLoader.loadInstanceDynamically(securityConfig, BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_SUBJECT_INFORMATION_PROVIDER, ISubjectInformationProvider.class);
	}
}
