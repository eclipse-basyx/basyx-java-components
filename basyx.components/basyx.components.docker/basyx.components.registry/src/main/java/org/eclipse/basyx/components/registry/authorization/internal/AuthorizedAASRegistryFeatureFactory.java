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
package org.eclipse.basyx.components.registry.authorization.internal;

import java.util.Arrays;
import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration.AuthorizationStrategy;

/**
 * Creates {@link AuthorizedAASRegistryFeature} according to a
 * {@link org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration}.
 *
 * @author wege
 */
public class AuthorizedAASRegistryFeatureFactory {
	private final BaSyxSecurityConfiguration securityConfig;

	public AuthorizedAASRegistryFeatureFactory(final BaSyxSecurityConfiguration securityConfig) {
		this.securityConfig = securityConfig;
	}

	public AuthorizedAASRegistryFeature create() {
		final String strategyString = securityConfig.getAuthorizationStrategy();

		if (strategyString == null) {
			throw new IllegalArgumentException(String.format("no authorization strategy set, please set %s in security.properties", BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY));
		}

		AuthorizationStrategy strategy;
		try {
			strategy = AuthorizationStrategy.valueOf(strategyString);
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					String.format("unknown authorization strategy %s set in security.properties, available options: %s", strategyString, Arrays.toString(BaSyxSecurityConfiguration.AuthorizationStrategy.values())));
		}

		switch (strategy) {
		case SimpleRbac: {
			return new SimpleRbacAuthorizedAASRegistryFeature<>(securityConfig);
		}
		case GrantedAuthority: {
			return new GrantedAuthorityAuthorizedAASRegistryFeature<>(securityConfig);
		}
		case Custom: {
			return new CustomAuthorizedAASRegistryFeature<>(securityConfig);
		}
		default:
			throw new UnsupportedOperationException("no handler for authorization strategy " + strategyString);
		}
	}
}
