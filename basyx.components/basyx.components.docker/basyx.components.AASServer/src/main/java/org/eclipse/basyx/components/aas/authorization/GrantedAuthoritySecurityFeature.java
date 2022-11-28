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

import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.GrantedAuthorityAASAggregatorAuthorizer;
import org.eclipse.basyx.extensions.aas.api.authorization.GrantedAuthorityAASAPIAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.IGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.GrantedAuthoritySubmodelAggregatorAuthorizer;
import org.eclipse.basyx.extensions.submodel.authorization.GrantedAuthoritySubmodelAPIAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specialization of {@link SecurityFeature} for the GrantedAuthority authorization scheme.
 *
 * @author wege
 */
public class GrantedAuthoritySecurityFeature extends SecurityFeature {
  private static Logger logger = LoggerFactory.getLogger(GrantedAuthoritySecurityFeature.class);

  public GrantedAuthoritySecurityFeature(final BaSyxSecurityConfiguration securityConfig) {
    super(securityConfig);
  }

  @Override
  public  <SubjectInformationType> IAASServerDecorator getDecorator() {
    logger.info("use GrantedAuthority authorization strategy");
    final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSubjectInformationProvider();
    final IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator = getGrantedAuthorityAuthenticator();

    return new AuthorizedAASServerDecorator<>(
        new GrantedAuthoritySubmodelAPIAuthorizer<>(grantedAuthorityAuthenticator),
        new GrantedAuthoritySubmodelAggregatorAuthorizer<>(grantedAuthorityAuthenticator),
        new GrantedAuthorityAASAPIAuthorizer<>(grantedAuthorityAuthenticator),
        new GrantedAuthorityAASAggregatorAuthorizer<>(grantedAuthorityAuthenticator),
        subjectInformationProvider
    );
  }

  @Override
  public  <SubjectInformationType> AuthorizedDefaultServletParams<SubjectInformationType> getFilesAuthorizerParams() {
    logger.info("use GrantedAuthority authorization strategy for files authorizer");
    final IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator = getGrantedAuthorityAuthenticator();
    final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSubjectInformationProvider();

    return new AuthorizedDefaultServletParams<>(
        new GrantedAuthorityFilesAuthorizer<>(grantedAuthorityAuthenticator),
        subjectInformationProvider
    );
  }

  @SuppressWarnings("unchecked")
  private <SubjectInformationType> IGrantedAuthorityAuthenticator<SubjectInformationType> getGrantedAuthorityAuthenticator() {
    return securityConfig.loadInstanceDynamically(BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_GRANTED_AUTHORITY_GRANTED_AUTHORITY_AUTHENTICATOR, IGrantedAuthorityAuthenticator.class);
  }

  @SuppressWarnings("unchecked")
  private <SubjectInformationType> ISubjectInformationProvider<SubjectInformationType> getSubjectInformationProvider() {
    return securityConfig.loadInstanceDynamically(BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_SUBJECT_INFORMATION_PROVIDER, ISubjectInformationProvider.class);
  }
}
