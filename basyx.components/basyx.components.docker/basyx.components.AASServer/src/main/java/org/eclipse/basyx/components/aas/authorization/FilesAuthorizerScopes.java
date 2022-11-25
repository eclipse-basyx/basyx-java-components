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

/**
 *
 * Constants for the permission scopes to the
 * {@link GrantedAuthorityFilesAuthorizer} and {@link SimpleRbacFilesAuthorizer}.
 *
 * @author wege
 * @see <a href=
 *      "https://tools.ietf.org/html/rfc6749#section-3.3">https://tools.ietf.org/html/rfc6749#section-3.3</a>
 */
public class FilesAuthorizerScopes {
  private static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
  public static final String READ_SCOPE = "urn:org.eclipse.basyx:scope:files:read";
  public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + READ_SCOPE;

  private FilesAuthorizerScopes() {
    // This class should not be instantiated as it serves as a holder for constants
    // only
  }
}
