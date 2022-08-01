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

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerFeature;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration.AuthorizationStrategy;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.GrantedAuthorityAASAggregatorAuthorizer;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.SimpleAbacAASAggregatorAuthorizer;
import org.eclipse.basyx.extensions.aas.api.authorization.GrantedAuthorityAASAPIAuthorizer;
import org.eclipse.basyx.extensions.aas.api.authorization.SimpleAbacAASAPIAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.AbacRule;
import org.eclipse.basyx.extensions.shared.authorization.AbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.IAbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.IGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.JWTAuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.KeycloakRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.PredefinedSetAbacRuleChecker;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.GrantedAuthoritySubmodelAggregatorAuthorizer;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.SimpleAbacSubmodelAggregatorAuthorizer;
import org.eclipse.basyx.extensions.submodel.authorization.GrantedAuthoritySubmodelAPIAuthorizer;
import org.eclipse.basyx.extensions.submodel.authorization.SimpleAbacSubmodelAPIAuthorizer;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Feature for Authorization of Submodel and Shell access
 * 
 * @author fischer, fried, wege
 *
 */
public class AuthorizedAASServerFeature implements IAASServerFeature {
	private static Logger logger = LoggerFactory.getLogger(AuthorizedAASServerFeature.class);

	protected final BaSyxAASServerConfiguration aasConfig;

	public AuthorizedAASServerFeature(final BaSyxAASServerConfiguration aasConfig) {
		this.aasConfig = aasConfig;
	}

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public IAASServerDecorator getDecorator() {
		final String strategyString = aasConfig.getAuthorizationStrategy();

		if (strategyString == null) {
			throw new IllegalArgumentException(String.format("no authorization strategy set, please set %s in aas.properties", BaSyxAASServerConfiguration.AUTHORIZATION_STRATEGY));
		}

		final AuthorizationStrategy strategy;
		try {
			strategy = AuthorizationStrategy.valueOf(strategyString);
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(String.format("unknown authorization strategy %s set in aas.properties, available options: %s", strategyString, Arrays.toString(BaSyxAASServerConfiguration.AuthorizationStrategy.values())));
		}

		switch (strategy) {
			case SimpleAbac: {
				return getSimpleAbacDecorator();
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

	private <SubjectInformationType> IAASServerDecorator getSimpleAbacDecorator() {
		logger.info("use SimpleAbac authorization strategy");
		final AbacRuleSet abacRuleSet = readAbacRulesFile();
		final IAbacRuleChecker abacRuleChecker = new PredefinedSetAbacRuleChecker(abacRuleSet);
		final IRoleAuthenticator<SubjectInformationType> roleAuthenticator = getSimpleAbacRoleAuthenticator();
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSimpleAbacSubjectInformationProvider();

		return new AuthorizedAASServerDecorator<>(
				new SimpleAbacSubmodelAPIAuthorizer<>(abacRuleChecker, roleAuthenticator),
				new SimpleAbacSubmodelAggregatorAuthorizer<>(abacRuleChecker, roleAuthenticator),
				new SimpleAbacAASAPIAuthorizer<>(abacRuleChecker, roleAuthenticator),
				new SimpleAbacAASAggregatorAuthorizer<>(abacRuleChecker, roleAuthenticator),
				subjectInformationProvider
		);
	}

	private <SubjectInformationType> IRoleAuthenticator<SubjectInformationType> getSimpleAbacRoleAuthenticator() {
		try {
			final String className = aasConfig.getAuthorizationStrategySimpleAbacRoleAuthenticator();
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!IRoleAuthenticator.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxAASServerConfiguration.AUTHORIZATION_STRATEGY_SIMPLEABAC_ROLE_AUTHENTICATOR + " does not implement the interface " + IRoleAuthenticator.class.getName());
			}

			return (IRoleAuthenticator<SubjectInformationType>) clazz.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	private <SubjectInformationType> ISubjectInformationProvider<SubjectInformationType> getSimpleAbacSubjectInformationProvider() {
		try {
			final String className = aasConfig.getAuthorizationStrategySimpleAbacSubjectInformationProvider();
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!ISubjectInformationProvider.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxAASServerConfiguration.AUTHORIZATION_STRATEGY_SIMPLEABAC_SUBJECT_INFORMATION_PROVIDER + " -> " + effectiveClassName + " does not implement the interface " + ISubjectInformationProvider.class.getName());
			}

			return (ISubjectInformationProvider<SubjectInformationType>) clazz.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	private final String ABAC_RULES_PATH = "/abac_rules.json";

	private AbacRuleSet readAbacRulesFile() {
		logger.info("loading abac rules...");
		try (final InputStream inputStream = getClass().getResourceAsStream(ABAC_RULES_PATH)) {
			if (inputStream == null) {
				throw new FileNotFoundException("could not find " + ABAC_RULES_PATH);
			}
			final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			final JsonReader jsonReader = new JsonReader(inputStreamReader);
			final AbacRule[] abacRules = new Gson().fromJson(jsonReader, AbacRule[].class);
			logger.info("Read abac rules: " + Arrays.toString(abacRules));
			final AbacRuleSet abacRuleSet = new AbacRuleSet();
			Arrays.stream(abacRules).forEach(abacRuleSet::addRule);
			return abacRuleSet;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return new AbacRuleSet();
	}

	public  <SubjectInformationType> IAASServerDecorator getGrantedAuthorityDecorator() {
		logger.info("use GrantedAuthority authorization strategy");
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getGrantedAuthoritySubjectInformationProvider();
		final IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator = getGrantedAuthorityAuthenticator();

		return new AuthorizedAASServerDecorator<>(
				new GrantedAuthoritySubmodelAPIAuthorizer<>(grantedAuthorityAuthenticator),
				new GrantedAuthoritySubmodelAggregatorAuthorizer<>(grantedAuthorityAuthenticator),
				new GrantedAuthorityAASAPIAuthorizer<>(grantedAuthorityAuthenticator),
				new GrantedAuthorityAASAggregatorAuthorizer<>(grantedAuthorityAuthenticator),
				subjectInformationProvider
		);
	}

	private <SubjectInformationType> IGrantedAuthorityAuthenticator<SubjectInformationType> getGrantedAuthorityAuthenticator() {
		try {
			final String className = aasConfig.getAuthorizationStrategyGrantedAuthorityGrantedAuthorityAuthenticator();
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!IGrantedAuthorityAuthenticator.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxAASServerConfiguration.AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_GRANTED_AUTHORITY_AUTHENTICATOR + " does not implement the interface " + IGrantedAuthorityAuthenticator.class.getName());
			}

			return (IGrantedAuthorityAuthenticator<SubjectInformationType>) clazz.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	private <SubjectInformationType> ISubjectInformationProvider<SubjectInformationType> getGrantedAuthoritySubjectInformationProvider() {
		try {
			final String className = aasConfig.getAuthorizationStrategyGrantedAuthoritySubjectInformationProvider();
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!ISubjectInformationProvider.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxAASServerConfiguration.AUTHORIZATION_STRATEGY_GRANTEDAUTHORITY_SUBJECT_INFORMATION_PROVIDER + " -> " + effectiveClassName + " does not implement the interface " + ISubjectInformationProvider.class.getName());
			}

			return (ISubjectInformationProvider<SubjectInformationType>) clazz.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
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
	public void addToContext(BaSyxContext context, BaSyxAASServerConfiguration aasConfig) {
		final IJwtBearerTokenAuthenticationConfigurationProvider jwtBearerTokenAuthenticationConfigurationProvider = getJwtBearerTokenAuthenticationConfigurationProvider();
		if (jwtBearerTokenAuthenticationConfigurationProvider != null) {
			context.setJwtBearerTokenAuthenticationConfiguration(
					jwtBearerTokenAuthenticationConfigurationProvider.get(aasConfig)
			);
		}
	}

	private IJwtBearerTokenAuthenticationConfigurationProvider getJwtBearerTokenAuthenticationConfigurationProvider() {
		try {
			final String className = aasConfig.getAuthorizationStrategyJwtBearerTokenAuthenticationConfigurationProvider();

			if (className == null) {
				return null;
			}

			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!IJwtBearerTokenAuthenticationConfigurationProvider.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxAASServerConfiguration.AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER + " -> " + effectiveClassName + " does not implement the interface " + IJwtBearerTokenAuthenticationConfigurationProvider.class.getName());
			}

			return (IJwtBearerTokenAuthenticationConfigurationProvider) clazz.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	private <SubjectInformationType> IAASServerDecorator getCustomDecorator() {
		logger.info("use Custom authorization strategy");
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getCustomSubjectInformationProvider();
		final IAuthorizersProvider<SubjectInformationType> authorizersProvider = getCustomAuthorizersProvider();

		final Authorizers<SubjectInformationType> authorizers = authorizersProvider.get(aasConfig);

		return new AuthorizedAASServerDecorator<>(
				authorizers.getSubmodelAPIAuthorizer(),
				authorizers.getSubmodelAggregatorAuthorizer(),
				authorizers.getAasApiAuthorizer(),
				authorizers.getAasAggregatorAuthorizer(),
				subjectInformationProvider
		);
	}

	private <SubjectInformationType> IAuthorizersProvider<SubjectInformationType> getCustomAuthorizersProvider() {
		try {
			final String className = aasConfig.getAuthorizationStrategyCustomAuthorizersProvider();
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!IAuthorizersProvider.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxAASServerConfiguration.AUTHORIZATION_STRATEGY_CUSTOM_AUTHORIZERS_PROVIDER + " -> " + effectiveClassName + " does not implement the interface " + IAuthorizersProvider.class.getName());
			}

			return (IAuthorizersProvider<SubjectInformationType>) clazz.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	private <SubjectInformationType> ISubjectInformationProvider<SubjectInformationType> getCustomSubjectInformationProvider() {
		try {
			final String className = aasConfig.getAuthorizationStrategyCustomSubjectInformationProvider();
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);
			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!ISubjectInformationProvider.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("given " + BaSyxAASServerConfiguration.AUTHORIZATION_STRATEGY_CUSTOM_SUBJECT_INFORMATION_PROVIDER + " -> " + effectiveClassName + " does not implement the interface " + ISubjectInformationProvider.class.getName());
			}

			return (ISubjectInformationProvider<SubjectInformationType>) clazz.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}
}
