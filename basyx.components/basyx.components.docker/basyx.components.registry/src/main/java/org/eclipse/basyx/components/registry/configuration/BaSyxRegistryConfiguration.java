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
package org.eclipse.basyx.components.registry.configuration;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.components.configuration.BaSyxConfiguration;

/**
 * Represents a BaSyx registry configuration for a BaSyx Registry with any
 * backend, that can be loaded from a properties file.
 * 
 * @author espen, wege
 *
 */
public class BaSyxRegistryConfiguration extends BaSyxConfiguration {
	// Prefix for environment variables
	public static final String ENV_PREFIX = "BaSyxRegistry_";

	// Feature enabling options
	private static final String FEATURE_ENABLED = "Enabled";
	private static final String FEATURE_DISABLED = "Disabled";

	// Default BaSyx Context configuration
	public static final String DEFAULT_BACKEND = RegistryBackend.INMEMORY.toString();
	public static final String DEFAULT_EVENTS = RegistryEventBackend.NONE.toString();
	public static final String DEFAULT_AUTHORIZATION = FEATURE_DISABLED;
	public static final String DEFAULT_TAGGED_DIRECTORY = FEATURE_DISABLED;

	// Configuration keys
	public static final String BACKEND = "registry.backend";
	public static final String EVENTS = "registry.events";
	public static final String AUTHORIZATION = "registry.authorization";
	public static final String AUTHORIZATION_STRATEGY = "registry.authorization.strategy";
	public static final String AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER = "registry.authorization.strategy.jwtBearerTokenAuthenticationConfigurationProvider";
	public static final String AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER_KEYCLOAK_SERVER_URL = "registry.authorization.strategy.jwtBearerTokenAuthenticationConfigurationProvider.keycloak.serverUrl";
	public static final String AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER_KEYCLOAK_REALM = "registry.authorization.strategy.jwtBearerTokenAuthenticationConfigurationProvider.keycloak.realm";
	public static final String AUTHORIZATION_STRATEGY_SIMPLEABAC_RULES_FILE_PATH = "registry.authorization.strategy.simpleAbac.rulesFilePath";
	public static final String AUTHORIZATION_STRATEGY_SIMPLEABAC_ROLE_AUTHENTICATOR = "registry.authorization.strategy.simpleAbac.roleAuthenticator";
	public static final String AUTHORIZATION_STRATEGY_SIMPLEABAC_SUBJECT_INFORMATION_PROVIDER = "registry.authorization.strategy.simpleAbac.subjectInformationProvider";
	public static final String AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_GRANTED_AUTHORITY_AUTHENTICATOR = "registry.authorization.strategy.grantedAuthority.grantedAuthorityAuthenticator";
	public static final String AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_SUBJECT_INFORMATION_PROVIDER = "registry.authorization.strategy.grantedAuthority.subjectInformationProvider";

	public enum AuthorizationStrategy {
		SimpleAbac,
		GrantedAuthority
	}

	private static final String TAGGED_DIRECTORY = "registry.taggedDirectory";

	// The default path for the context properties file
	public static final String DEFAULT_CONFIG_PATH = "registry.properties";

	// The default key for variables pointing to the configuration file
	public static final String DEFAULT_FILE_KEY = "BASYX_REGISTRY";

	public static Map<String, String> getDefaultProperties() {
		Map<String, String> defaultProps = new HashMap<>();
		defaultProps.put(BACKEND, DEFAULT_BACKEND);
		defaultProps.put(EVENTS, DEFAULT_EVENTS);
		defaultProps.put(AUTHORIZATION, DEFAULT_AUTHORIZATION);
		defaultProps.put(TAGGED_DIRECTORY, DEFAULT_TAGGED_DIRECTORY);
		return defaultProps;
	}

	public BaSyxRegistryConfiguration() {
		super(getDefaultProperties());
	}

	public BaSyxRegistryConfiguration(RegistryBackend backend) {
		super(getDefaultProperties());
		setRegistryBackend(backend);
	}

	public BaSyxRegistryConfiguration(Map<String, String> values) {
		super(values);
	}

	public void loadFromEnvironmentVariables() {
		loadFromEnvironmentVariables(ENV_PREFIX, BACKEND, EVENTS, AUTHORIZATION, TAGGED_DIRECTORY);
	}

	public void loadFromDefaultSource() {
		loadFileOrDefaultResource(DEFAULT_FILE_KEY, DEFAULT_CONFIG_PATH);
		loadFromEnvironmentVariables();
	}

	public RegistryBackend getRegistryBackend() {
		return RegistryBackend.fromString(getProperty(BACKEND));
	}

	public void setRegistryBackend(RegistryBackend backend) {
		setProperty(BACKEND, backend.toString());
	}

	public RegistryEventBackend getRegistryEvents() {
		return RegistryEventBackend.fromString(getProperty(EVENTS));
	}

	public void setRegistryEvents(RegistryEventBackend events) {
		setProperty(EVENTS, events.toString());
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

	public String getAuthorizationStrategySimpleAbacRulesFilePath() {
		return getProperty(AUTHORIZATION_STRATEGY_SIMPLEABAC_RULES_FILE_PATH);
	}

	public void setAuthorizationStrategySimpleAbacRulesFilePath(String authorizationStrategySimpleAbacRulesFilePath) {
		setProperty(AUTHORIZATION_STRATEGY_SIMPLEABAC_RULES_FILE_PATH, authorizationStrategySimpleAbacRulesFilePath);
	}

	public String getAuthorizationStrategySimpleAbacSubjectInformationProvider() {
		return getProperty(AUTHORIZATION_STRATEGY_SIMPLEABAC_SUBJECT_INFORMATION_PROVIDER);
	}

	public void setAuthorizationStrategySimpleAbacSubjectInformationProvider(String authorizationStrategySimpleAbacSubjectInformationProvider) {
		setProperty(AUTHORIZATION_STRATEGY_SIMPLEABAC_SUBJECT_INFORMATION_PROVIDER, authorizationStrategySimpleAbacSubjectInformationProvider);
	}

	public String getAuthorizationStrategySimpleAbacRoleAuthenticator() {
		return getProperty(AUTHORIZATION_STRATEGY_SIMPLEABAC_ROLE_AUTHENTICATOR);
	}

	public void setAuthorizationStrategySimpleabacRoleAuthenticator(String authorizationStrategySimpleAbacRoleAuthenticator) {
		setProperty(AUTHORIZATION_STRATEGY_SIMPLEABAC_ROLE_AUTHENTICATOR, authorizationStrategySimpleAbacRoleAuthenticator);
	}

	public String getAuthorizationStrategyGrantedAuthoritySubjectInformationProvider() {
		return getProperty(AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_SUBJECT_INFORMATION_PROVIDER);
	}

	public void setAuthorizationStrategyGrantedAuthoritySubjectInformationProvider(String authorizationStrategyGrantedAuthoritySubjectInformationProvider) {
		setProperty(AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_SUBJECT_INFORMATION_PROVIDER, authorizationStrategyGrantedAuthoritySubjectInformationProvider);
	}

	public String getAuthorizationStrategyGrantedAuthorityGrantedAuthorityAuthenticator() {
		return getProperty(AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_GRANTED_AUTHORITY_AUTHENTICATOR);
	}

	public void setAuthorizationStrategyGrantedAuthorityGrantedAuthorityAuthenticator(String authorizationStrategyGrantedAuthorityGrantedAuthorityAuthenticator) {
		setProperty(AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_GRANTED_AUTHORITY_AUTHENTICATOR, authorizationStrategyGrantedAuthorityGrantedAuthorityAuthenticator);
	}

	public boolean isTaggedDirectoryEnabled() {
		return getProperty(TAGGED_DIRECTORY).equals(FEATURE_ENABLED);
	}

	public void enableTaggedDirectory() {
		setProperty(TAGGED_DIRECTORY, FEATURE_ENABLED);
	}

	public void disableTaggedDirectory() {
		setProperty(TAGGED_DIRECTORY, FEATURE_DISABLED);
	}
}
