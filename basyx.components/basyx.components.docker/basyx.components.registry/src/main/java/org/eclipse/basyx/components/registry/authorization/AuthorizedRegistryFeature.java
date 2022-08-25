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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration.AuthorizationStrategy;
import org.eclipse.basyx.components.registry.registrycomponent.IAASRegistryDecorator;
import org.eclipse.basyx.components.registry.registrycomponent.IAASRegistryFeature;
import org.eclipse.basyx.extensions.aas.registration.authorization.GrantedAuthorityAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.aas.registration.authorization.SimpleRbacAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.RbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.IRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.IGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.JWTAuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.KeycloakRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.PredefinedSetRbacRuleChecker;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Feature for Authorization of Submodel and Shell access
 * 
 * @author wege
 *
 */
public class AuthorizedRegistryFeature implements IAASRegistryFeature {
	private static Logger logger = LoggerFactory.getLogger(AuthorizedRegistryFeature.class);

	protected final BaSyxRegistryConfiguration registryConfig;

	public AuthorizedRegistryFeature(final BaSyxRegistryConfiguration registryConfig) {
		this.registryConfig = registryConfig;
	}

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public IAASRegistryDecorator getDecorator() {
		final String strategyString = registryConfig.getAuthorizationStrategy();

		if (strategyString == null) {
			throw new IllegalArgumentException(String.format("no authorization strategy set, please set %s in aas.properties", BaSyxRegistryConfiguration.AUTHORIZATION_STRATEGY));
		}

		final AuthorizationStrategy strategy;
		try {
			strategy = AuthorizationStrategy.valueOf(strategyString);
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(String.format("unknown authorization strategy %s set in aas.properties, available options: %s", strategyString, Arrays.toString(BaSyxRegistryConfiguration.AuthorizationStrategy.values())));
		}

		switch (strategy) {
			case SimpleRbac: {
				return getSimpleRbacDecorator();
			}
			case GrantedAuthority: {
				return getGrantedAuthorityDecorator();
			}
			case Custom: {
				return getCustomDecorator();
			}
			default:
				throw new UnsupportedOperationException("no handler for authorization strategy " + strategyString);
		}
	}

	private <SubjectInformationType> IAASRegistryDecorator getSimpleRbacDecorator() {
		logger.info("use SimpleRbac authorization strategy");
		final RbacRuleSet rbacRuleSet = RbacRuleSet.fromFile(registryConfig.getAuthorizationStrategySimpleRbacRulesFilePath());
		final IRbacRuleChecker rbacRuleChecker = new PredefinedSetRbacRuleChecker(rbacRuleSet);
		final IRoleAuthenticator<SubjectInformationType> roleAuthenticator = getSimpleRbacRoleAuthenticator();
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSimpleRbacSubjectInformationProvider();

		return new AuthorizedRegistryDecorator<>(
				new SimpleRbacAASRegistryAuthorizer<>(rbacRuleChecker, roleAuthenticator),
				subjectInformationProvider
		);
	}

	private <SubjectInformationType> IRoleAuthenticator<SubjectInformationType> getSimpleRbacRoleAuthenticator() {
		try {
			final String className = registryConfig.getAuthorizationStrategySimpleRbacRoleAuthenticator();
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!IRoleAuthenticator.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxRegistryConfiguration.AUTHORIZATION_STRATEGY_SIMPLERBAC_ROLE_AUTHENTICATOR + " does not implement the interface " + IRoleAuthenticator.class.getName());
			}

			return (IRoleAuthenticator<SubjectInformationType>) clazz.getDeclaredConstructor().newInstance();
		} catch (final ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	private <SubjectInformationType> ISubjectInformationProvider<SubjectInformationType> getSimpleRbacSubjectInformationProvider() {
		try {
			final String className = registryConfig.getAuthorizationStrategySimpleRbacSubjectInformationProvider();
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!ISubjectInformationProvider.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxRegistryConfiguration.AUTHORIZATION_STRATEGY_SIMPLERBAC_SUBJECT_INFORMATION_PROVIDER + " -> " + effectiveClassName + " does not implement the interface " + ISubjectInformationProvider.class.getName());
			}

			return (ISubjectInformationProvider<SubjectInformationType>) clazz.getDeclaredConstructor().newInstance();
		} catch (final ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	private <SubjectInformationType> IAASRegistryDecorator getGrantedAuthorityDecorator() {
		logger.info("use GrantedAuthority authorization strategy");
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getGrantedAuthoritySubjectInformationProvider();
		final IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator = getGrantedAuthorityAuthenticator();

		return new AuthorizedRegistryDecorator<>(
				new GrantedAuthorityAASRegistryAuthorizer<>(grantedAuthorityAuthenticator),
				subjectInformationProvider
		);
	}

	private <SubjectInformationType> IGrantedAuthorityAuthenticator<SubjectInformationType> getGrantedAuthorityAuthenticator() {
		try {
			final String className = registryConfig.getAuthorizationStrategyGrantedAuthorityGrantedAuthorityAuthenticator();
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!IGrantedAuthorityAuthenticator.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxRegistryConfiguration.AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_GRANTED_AUTHORITY_AUTHENTICATOR + " does not implement the interface " + IGrantedAuthorityAuthenticator.class.getName());
			}

			return (IGrantedAuthorityAuthenticator<SubjectInformationType>) clazz.getDeclaredConstructor().newInstance();
		} catch (final ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	private <SubjectInformationType> ISubjectInformationProvider<SubjectInformationType> getGrantedAuthoritySubjectInformationProvider() {
		try {
			final String className = registryConfig.getAuthorizationStrategyGrantedAuthoritySubjectInformationProvider();
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!ISubjectInformationProvider.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxRegistryConfiguration.AUTHORIZATION_STRATEGY_SIMPLERBAC_SUBJECT_INFORMATION_PROVIDER + " -> " + effectiveClassName + " does not implement the interface " + ISubjectInformationProvider.class.getName());
			}

			return (ISubjectInformationProvider<SubjectInformationType>) clazz.getDeclaredConstructor().newInstance();
		} catch (final ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	private static Map<String, String> classesBySimpleNameMap = new HashMap<>();

	static {
		classesBySimpleNameMap.put(KeycloakRoleAuthenticator.class.getSimpleName(), KeycloakRoleAuthenticator.class.getName());
		classesBySimpleNameMap.put(AuthenticationGrantedAuthorityAuthenticator.class.getSimpleName(), AuthenticationGrantedAuthorityAuthenticator.class.getName());
		classesBySimpleNameMap.put(JWTAuthenticationContextProvider.class.getSimpleName(), JWTAuthenticationContextProvider.class.getName());
		classesBySimpleNameMap.put(AuthenticationContextProvider.class.getSimpleName(), AuthenticationContextProvider.class.getName());
	}

	@Override
	public void addToContext(BaSyxContext context, BaSyxRegistryConfiguration registryConfig) {
		final IJwtBearerTokenAuthenticationConfigurationProvider jwtBearerTokenAuthenticationConfigurationProvider = getJwtBearerTokenAuthenticationConfigurationProvider();
		if (jwtBearerTokenAuthenticationConfigurationProvider != null) {
			context.setJwtBearerTokenAuthenticationConfiguration(
					jwtBearerTokenAuthenticationConfigurationProvider.get(registryConfig)
			);
		}
	}

	private IJwtBearerTokenAuthenticationConfigurationProvider getJwtBearerTokenAuthenticationConfigurationProvider() {
		try {
			final String className = registryConfig.getAuthorizationStrategyJwtBearerTokenAuthenticationConfigurationProvider();

			if (className == null) {
				return null;
			}

			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!IJwtBearerTokenAuthenticationConfigurationProvider.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxRegistryConfiguration.AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER + " -> " + effectiveClassName + " does not implement the interface " + IJwtBearerTokenAuthenticationConfigurationProvider.class.getName());
			}

			return (IJwtBearerTokenAuthenticationConfigurationProvider) clazz.getDeclaredConstructor().newInstance();
		} catch (final ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	private <SubjectInformationType> IAASRegistryDecorator getCustomDecorator() {
		logger.info("use Custom authorization strategy");
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getCustomSubjectInformationProvider();
		final IAuthorizersProvider<SubjectInformationType> authorizersProvider = getCustomAuthorizersProvider();

		final Authorizers<SubjectInformationType> authorizers = authorizersProvider.get(registryConfig);

		return new AuthorizedRegistryDecorator<>(
				authorizers.getAasRegistryAuthorizer(),
				subjectInformationProvider
		);
	}

	private <SubjectInformationType> IAuthorizersProvider<SubjectInformationType> getCustomAuthorizersProvider() {
		try {
			final String className = registryConfig.getAuthorizationStrategyCustomAuthorizersProvider();
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!IAuthorizersProvider.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxRegistryConfiguration.AUTHORIZATION_STRATEGY_CUSTOM_AUTHORIZERS_PROVIDER + " -> " + effectiveClassName + " does not implement the interface " + IAuthorizersProvider.class.getName());
			}

			return (IAuthorizersProvider<SubjectInformationType>) clazz.getDeclaredConstructor().newInstance();
		} catch (final ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	private <SubjectInformationType> ISubjectInformationProvider<SubjectInformationType> getCustomSubjectInformationProvider() {
		try {
			final String className = registryConfig.getAuthorizationStrategyCustomSubjectInformationProvider();
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!ISubjectInformationProvider.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxRegistryConfiguration.AUTHORIZATION_STRATEGY_CUSTOM_SUBJECT_INFORMATION_PROVIDER + " -> " + effectiveClassName + " does not implement the interface " + ISubjectInformationProvider.class.getName());
			}

			return (ISubjectInformationProvider<SubjectInformationType>) clazz.getDeclaredConstructor().newInstance();
		} catch (final ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}
}
