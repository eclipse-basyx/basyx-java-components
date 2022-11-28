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
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the {@link IAASServerDecorator} and {@link AuthorizedDefaultServletParams} instances that are required for authorization.
 * This base class uses the custom authorization configuration properties to load classes.
 *
 * @author wege
 */
public class SecurityFeature {
  private static Logger logger = LoggerFactory.getLogger(SecurityFeature.class);

  protected final BaSyxSecurityConfiguration securityConfig;

  public SecurityFeature(final BaSyxSecurityConfiguration securityConfig) {
    this.securityConfig = securityConfig;
  }

  public  <SubjectInformationType> IAASServerDecorator getDecorator() {
    logger.info("use Custom authorization strategy");
    final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSubjectInformationProvider();
    final IAuthorizersProvider<SubjectInformationType> authorizersProvider = getAuthorizersProvider();

    final Authorizers<SubjectInformationType> authorizers = authorizersProvider.get(securityConfig);

    return new AuthorizedAASServerDecorator<>(
        authorizers.getSubmodelAPIAuthorizer(),
        authorizers.getSubmodelAggregatorAuthorizer(),
        authorizers.getAasApiAuthorizer(),
        authorizers.getAasAggregatorAuthorizer(),
        subjectInformationProvider
    );
  }

  public  <SubjectInformationType> AuthorizedDefaultServletParams<SubjectInformationType> getFilesAuthorizerParams() {
    logger.info("use Custom authorization strategy for files authorizer");
    final IAuthorizersProvider<SubjectInformationType> authorizersProvider = getAuthorizersProvider();
    final Authorizers<SubjectInformationType> authorizers = authorizersProvider.get(securityConfig);
    final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSubjectInformationProvider();

    return new AuthorizedDefaultServletParams<>(
        authorizers.getFilesAuthorizer(),
        subjectInformationProvider
    );
  }

  @SuppressWarnings("unchecked")
  private <SubjectInformationType> IAuthorizersProvider<SubjectInformationType> getAuthorizersProvider() {
    return securityConfig.loadInstanceDynamically(BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_CUSTOM_AUTHORIZERS_PROVIDER, IAuthorizersProvider.class);
  }

  @SuppressWarnings("unchecked")
  private <SubjectInformationType> ISubjectInformationProvider<SubjectInformationType> getSubjectInformationProvider() {
    return securityConfig.loadInstanceDynamically(BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_CUSTOM_SUBJECT_INFORMATION_PROVIDER, ISubjectInformationProvider.class);
  }
}
