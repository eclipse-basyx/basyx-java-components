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
package org.eclipse.basyx.components.security.authorization;

import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration;
import org.eclipse.basyx.vab.protocol.http.server.JwtBearerTokenAuthenticationConfiguration;

/**
 *
 * Provider for {@link JwtBearerTokenAuthenticationConfiguration}, which will be passed
 * into the BaSyx server context to be used as a security filter and set up the security context
 * for incoming requests. Uses the aas server configuration.
 *
 * @author wege
 *
 */
public interface IJwtBearerTokenAuthenticationConfigurationProvider {
  /**
   * Provides the {@link JwtBearerTokenAuthenticationConfiguration} that can be passed to the
   * BaSyx server context to install a security filter and validate and set up the security context
   * from access tokens included in incoming requests.
   *
   * @param securityConfig
   *                  the configuration of the aas server which should have information on how to determine the {@link JwtBearerTokenAuthenticationConfiguration}.
   *
   * @return the {@link JwtBearerTokenAuthenticationConfiguration} object
   */
  public JwtBearerTokenAuthenticationConfiguration get(BaSyxSecurityConfiguration securityConfig);
}
