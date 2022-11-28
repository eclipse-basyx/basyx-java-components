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

import java.util.Arrays;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerFeature;
import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration.AuthorizationStrategy;
import org.eclipse.basyx.components.security.authorization.IJwtBearerTokenAuthenticationConfigurationProvider;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;

/**
 * 
 * Feature for Authorization of Submodel and Shell access
 * 
 * @author fischer, fried, wege
 *
 */
public class AuthorizedAASServerFeature implements IAASServerFeature {
	protected final BaSyxSecurityConfiguration securityConfig;

	public AuthorizedAASServerFeature(final BaSyxSecurityConfiguration securityConfig) {
		this.securityConfig = securityConfig;
	}

	@Override
	public void initialize() {
		// nothing to do regarding initialization for this feature at the moment
	}

	@Override
	public void cleanUp() {
		// nothing to do regarding cleanup for this feature at the moment
	}

	@Override
	public IAASServerDecorator getDecorator() {
		final SecurityFeature securityFeature = getSecurityFeature();

		return securityFeature.getDecorator();
	}

	@Override
	public void addToContext(BaSyxContext context) {
		final IJwtBearerTokenAuthenticationConfigurationProvider jwtBearerTokenAuthenticationConfigurationProvider = getJwtBearerTokenAuthenticationConfigurationProvider();
		if (jwtBearerTokenAuthenticationConfigurationProvider != null) {
			context.setJwtBearerTokenAuthenticationConfiguration(
					jwtBearerTokenAuthenticationConfigurationProvider.get(securityConfig)
			);
		}
	}

	public <SubjectInformationType> AuthorizedDefaultServletParams<SubjectInformationType> getFilesAuthorizerParams() {
		return getSecurityFeature().getFilesAuthorizerParams();
	}

	private SecurityFeature getSecurityFeature() {
		final String strategyString = securityConfig.getAuthorizationStrategy();

		if (strategyString == null) {
			throw new IllegalArgumentException(String.format("no authorization strategy set, please set %s in aas.properties", BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY));
		}

		AuthorizationStrategy strategy;
		try {
			strategy = AuthorizationStrategy.valueOf(strategyString);
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(String.format("unknown authorization strategy %s set in aas.properties, available options: %s", strategyString, Arrays.toString(BaSyxSecurityConfiguration.AuthorizationStrategy.values())));
		}

		switch (strategy) {
			case SimpleRbac: {
				return new SimpleRbacSecurityFeature(securityConfig);
			}
			case GrantedAuthority: {
				return new GrantedAuthoritySecurityFeature(securityConfig);
			}
			case Custom: {
				return new SecurityFeature(securityConfig);
			}
			default:
				throw new UnsupportedOperationException("no handler for authorization strategy " + strategyString);
		}
	}

	private IJwtBearerTokenAuthenticationConfigurationProvider getJwtBearerTokenAuthenticationConfigurationProvider() {
		return securityConfig.loadInstanceDynamically(BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER, IJwtBearerTokenAuthenticationConfigurationProvider.class);
	}
}
