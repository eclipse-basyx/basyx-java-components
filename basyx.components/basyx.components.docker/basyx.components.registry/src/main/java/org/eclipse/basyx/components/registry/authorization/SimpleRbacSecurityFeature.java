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
package org.eclipse.basyx.components.registry.authorization;

import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration;
import org.eclipse.basyx.extensions.aas.directory.tagged.authorized.SimpleRbacTaggedDirectoryAuthorizer;
import org.eclipse.basyx.extensions.aas.registration.authorization.SimpleRbacAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.IRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.PredefinedSetRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.RbacRuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specialization of {@link SecurityFeature} for the SimpleRbac authorization scheme.
 *
 * @author wege
 */
public class SimpleRbacSecurityFeature extends SecurityFeature {
  private static Logger logger = LoggerFactory.getLogger(SimpleRbacSecurityFeature.class);

  public SimpleRbacSecurityFeature(final BaSyxSecurityConfiguration securityConfig) {
    super(securityConfig);
  }

  @Override
  public <SubjectInformationType> IAASRegistryDecorator getDecorator() {
    logger.info("use SimpleRbac authorization strategy");
    final RbacRuleSet rbacRuleSet = RbacRuleSet.fromFile(securityConfig.getAuthorizationStrategySimpleRbacRulesFilePath());
    final IRbacRuleChecker rbacRuleChecker = new PredefinedSetRbacRuleChecker(rbacRuleSet);
    final IRoleAuthenticator<SubjectInformationType> roleAuthenticator = getRoleAuthenticator();
    final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSubjectInformationProvider();

    return new AuthorizedRegistryDecorator<>(
        new SimpleRbacAASRegistryAuthorizer<>(rbacRuleChecker, roleAuthenticator),
        new SimpleRbacTaggedDirectoryAuthorizer<>(rbacRuleChecker, roleAuthenticator),
        subjectInformationProvider
    );
  }

  @SuppressWarnings("unchecked")
  private <SubjectInformationType> IRoleAuthenticator<SubjectInformationType> getRoleAuthenticator() {
    return securityConfig.loadInstanceDynamically(BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_SIMPLERBAC_ROLE_AUTHENTICATOR, IRoleAuthenticator.class);
  }

  @SuppressWarnings("unchecked")
  private <SubjectInformationType> ISubjectInformationProvider<SubjectInformationType> getSubjectInformationProvider() {
    return securityConfig.loadInstanceDynamically(BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_SIMPLERBAC_SUBJECT_INFORMATION_PROVIDER, ISubjectInformationProvider.class);
  }
}
