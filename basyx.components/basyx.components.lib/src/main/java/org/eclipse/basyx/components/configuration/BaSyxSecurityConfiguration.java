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
package org.eclipse.basyx.components.configuration;

import java.util.HashMap;
import java.util.Map;

public class BaSyxSecurityConfiguration extends BaSyxConfiguration {
  // Prefix for environment variables
  public static final String ENV_PREFIX = "BaSyxSecurity_";

  // Feature enabling options
  public static final String FEATURE_ENABLED = "Enabled";
  public static final String FEATURE_DISABLED = "Disabled";

  // Default BaSyx Security configuration
  public static final String DEFAULT_AUTHORIZATION = FEATURE_DISABLED;
  public static final String DEFAULT_AUTHORIZATION_STRATEGY_SIMPLERBAC_RULES_FILE_PATH = "/rbac_rules.json";

  // Configuration keys
  public static final String AUTHORIZATION = "aas.authorization";
  public static final String AUTHORIZATION_STRATEGY = "aas.authorization.strategy";
  public static final String AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER = "aas.authorization.strategy.jwtBearerTokenAuthenticationConfigurationProvider";
  public static final String AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER_KEYCLOAK_SERVER_URL = "aas.authorization.strategy.jwtBearerTokenAuthenticationConfigurationProvider.keycloak.serverUrl";
  public static final String AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER_KEYCLOAK_REALM = "aas.authorization.strategy.jwtBearerTokenAuthenticationConfigurationProvider.keycloak.realm";
  public static final String AUTHORIZATION_STRATEGY_SIMPLERBAC_RULES_FILE_PATH = "aas.authorization.strategy.simpleRbac.rulesFilePath";
  public static final String AUTHORIZATION_STRATEGY_SIMPLERBAC_ROLE_AUTHENTICATOR = "aas.authorization.strategy.simpleRbac.roleAuthenticator";
  public static final String AUTHORIZATION_STRATEGY_SIMPLERBAC_SUBJECT_INFORMATION_PROVIDER = "aas.authorization.strategy.simpleRbac.subjectInformationProvider";
  public static final String AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_GRANTED_AUTHORITY_GRANTED_AUTHORITY_AUTHENTICATOR = "registry.authorization.strategy.grantedAuthority.grantedAuthorityAuthenticator";
  public static final String AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_SUBJECT_INFORMATION_PROVIDER = "aas.authorization.strategy.grantedAuthority.subjectInformationProvider";
  public static final String AUTHORIZATION_STRATEGY_CUSTOM_AUTHORIZERS_PROVIDER = "aas.authorization.strategy.custom.authorizersProvider";
  public static final String AUTHORIZATION_STRATEGY_CUSTOM_SUBJECT_INFORMATION_PROVIDER = "aas.authorization.strategy.custom.subjectInformationProvider";

  public enum AuthorizationStrategy {
    SimpleRbac,
    GrantedAuthority,
    Custom
  }

  // The default path for the context properties file
  public static final String DEFAULT_CONFIG_PATH = "security.properties";

  // The default key for variables pointing to the configuration file
  public static final String DEFAULT_FILE_KEY = "BASYX_SECURITY";

  public static Map<String, String> getDefaultProperties() {
    Map<String, String> defaultProps = new HashMap<>();
    defaultProps.put(AUTHORIZATION, DEFAULT_AUTHORIZATION);
    defaultProps.put(AUTHORIZATION_STRATEGY_SIMPLERBAC_RULES_FILE_PATH, DEFAULT_AUTHORIZATION_STRATEGY_SIMPLERBAC_RULES_FILE_PATH);
    return defaultProps;
  }

  /**
   * Constructor with predefined value map
   */
  public BaSyxSecurityConfiguration(Map<String, String> values) {
    super(values);
  }

  /**
   * Empty Constructor - use default values
   */
  public BaSyxSecurityConfiguration() {
    super(getDefaultProperties());
  }

  /**
   * Load all settings except of the whitelist config part
   */
  public void loadFromEnvironmentVariables() {
    String[] properties = {
        AUTHORIZATION,
        AUTHORIZATION_STRATEGY,
        AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER,
        AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER_KEYCLOAK_SERVER_URL,
        AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER_KEYCLOAK_REALM,
        AUTHORIZATION_STRATEGY_SIMPLERBAC_RULES_FILE_PATH,
        AUTHORIZATION_STRATEGY_SIMPLERBAC_ROLE_AUTHENTICATOR,
        AUTHORIZATION_STRATEGY_SIMPLERBAC_SUBJECT_INFORMATION_PROVIDER,
        AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_GRANTED_AUTHORITY_GRANTED_AUTHORITY_AUTHENTICATOR,
        AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_SUBJECT_INFORMATION_PROVIDER,
        AUTHORIZATION_STRATEGY_CUSTOM_AUTHORIZERS_PROVIDER,
        AUTHORIZATION_STRATEGY_CUSTOM_SUBJECT_INFORMATION_PROVIDER,
    };
    loadFromEnvironmentVariables(ENV_PREFIX, properties);
  }

  public void loadFromDefaultSource() {
    loadFileOrDefaultResource(DEFAULT_FILE_KEY, DEFAULT_CONFIG_PATH);
    loadFromEnvironmentVariables();
  }

  public boolean isAuthorizationEnabled() {
    return getProperty(AUTHORIZATION).equals(FEATURE_ENABLED);
  }

  public void enableAuthorization() {
    setProperty(AUTHORIZATION, FEATURE_ENABLED);
  }

  public void disableAuthorization() {
    setProperty(AUTHORIZATION, FEATURE_DISABLED);
  }

  public String getAuthorizationStrategy() {
    return getProperty(AUTHORIZATION_STRATEGY);
  }

  public void setAuthorizationStrategy(String authorizationStrategy) {
    setProperty(AUTHORIZATION_STRATEGY, authorizationStrategy);
  }

  public String getAuthorizationStrategyJwtBearerTokenAuthenticationConfigurationProvider() {
    return getProperty(AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER);
  }

  public void setAuthorizationStrategyJwtBearerTokenAuthenticationConfigurationProvider(String jwtBearerTokenAuthenticationConfigurationProvider) {
    setProperty(AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER, jwtBearerTokenAuthenticationConfigurationProvider);
  }

  public String getAuthorizationStrategyJwtBearerTokenAuthenticationConfigurationProviderKeycloakServerUrl() {
    return getProperty(AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER_KEYCLOAK_SERVER_URL);
  }

  public void setAuthorizationStrategyJwtBearerTokenAuthenticationConfigurationProviderKeycloakServerUrl(String authorizationStrategyJwtBearerTokenAuthenticationConfigurationProviderKeycloakServerUrl) {
    setProperty(AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER_KEYCLOAK_SERVER_URL, authorizationStrategyJwtBearerTokenAuthenticationConfigurationProviderKeycloakServerUrl);
  }

  public String getAuthorizationStrategyJwtBearerTokenAuthenticationConfigurationProviderKeycloakRealm() {
    return getProperty(AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER_KEYCLOAK_REALM);
  }

  public void setAuthorizationStrategyJwtBearerTokenAuthenticationConfigurationProviderKeycloakRealm(String authorizationStrategyJwtBearerTokenAuthenticationConfigurationProviderKeycloakRealm) {
    setProperty(AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER_KEYCLOAK_REALM, authorizationStrategyJwtBearerTokenAuthenticationConfigurationProviderKeycloakRealm);
  }

  public String getAuthorizationStrategySimpleRbacRulesFilePath() {
    return getProperty(AUTHORIZATION_STRATEGY_SIMPLERBAC_RULES_FILE_PATH);
  }

  public void setAuthorizationStrategySimpleRbacRulesFilePath(String authorizationStrategySimpleRbacRulesFilePath) {
    setProperty(AUTHORIZATION_STRATEGY_SIMPLERBAC_RULES_FILE_PATH, authorizationStrategySimpleRbacRulesFilePath);
  }

  public String getAuthorizationStrategySimpleRbacSubjectInformationProvider() {
    return getProperty(AUTHORIZATION_STRATEGY_SIMPLERBAC_SUBJECT_INFORMATION_PROVIDER);
  }

  public void setAuthorizationStrategySimpleRbacSubjectInformationProvider(String authorizationStrategySimpleRbacSubjectInformationProvider) {
    setProperty(AUTHORIZATION_STRATEGY_SIMPLERBAC_SUBJECT_INFORMATION_PROVIDER, authorizationStrategySimpleRbacSubjectInformationProvider);
  }

  public String getAuthorizationStrategySimpleRbacRoleAuthenticator() {
    return getProperty(AUTHORIZATION_STRATEGY_SIMPLERBAC_ROLE_AUTHENTICATOR);
  }

  public void setAuthorizationStrategySimpleRbacRoleAuthenticator(String authorizationStrategySimpleRbacRoleAuthenticator) {
    setProperty(AUTHORIZATION_STRATEGY_SIMPLERBAC_ROLE_AUTHENTICATOR, authorizationStrategySimpleRbacRoleAuthenticator);
  }

  public String getAuthorizationStrategyGrantedAuthoritySubjectInformationProvider() {
    return getProperty(AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_SUBJECT_INFORMATION_PROVIDER);
  }

  public void setAuthorizationStrategyGrantedAuthoritySubjectInformationProvider(String authorizationStrategyGrantedAuthoritySubjectInformationProvider) {
    setProperty(AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_SUBJECT_INFORMATION_PROVIDER, authorizationStrategyGrantedAuthoritySubjectInformationProvider);
  }

  public String getAuthorizationStrategyGrantedAuthorityGrantedAuthorityGrantedAuthorityAuthenticator() {
    return getProperty(AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_GRANTED_AUTHORITY_GRANTED_AUTHORITY_AUTHENTICATOR);
  }

  public void setAuthorizationStrategyGrantedAuthorityGrantedAuthorityAuthenticator(String authorizationStrategyGrantedAuthorityGrantedAuthorityAuthenticator) {
    setProperty(AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_GRANTED_AUTHORITY_GRANTED_AUTHORITY_AUTHENTICATOR, authorizationStrategyGrantedAuthorityGrantedAuthorityAuthenticator);
  }

  public String getAuthorizationStrategyCustomAuthorizersProvider() {
    return getProperty(AUTHORIZATION_STRATEGY_CUSTOM_AUTHORIZERS_PROVIDER);
  }

  public void setAuthorizationStrategyCustomAuthorizersProvider(String authorizationStrategyCustomAuthorizers) {
    setProperty(AUTHORIZATION_STRATEGY_CUSTOM_AUTHORIZERS_PROVIDER, authorizationStrategyCustomAuthorizers);
  }

  public String getAuthorizationStrategyCustomSubjectInformationProvider() {
    return getProperty(AUTHORIZATION_STRATEGY_CUSTOM_SUBJECT_INFORMATION_PROVIDER);
  }

  public void setAuthorizationStrategyCustomSubjectInformationProvider(String authorizationStrategyCustomSubjectInformationProvider) {
    setProperty(AUTHORIZATION_STRATEGY_CUSTOM_SUBJECT_INFORMATION_PROVIDER, authorizationStrategyCustomSubjectInformationProvider);
  }
}
