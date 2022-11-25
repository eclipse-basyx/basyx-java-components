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

import org.eclipse.basyx.extensions.aas.aggregator.authorization.IAASAggregatorAuthorizer;
import org.eclipse.basyx.extensions.aas.api.authorization.IAASAPIAuthorizer;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.ISubmodelAggregatorAuthorizer;
import org.eclipse.basyx.extensions.submodel.authorization.ISubmodelAPIAuthorizer;

/**
 *
 * The different authorizers for the aas server to use when calling BaSyx objects like
 * the aas aggregator or the file download functionality of the aas server.
 *
 * @author wege
 *
 */
public class Authorizers<SubjectInformationType> {
  private final IAASAggregatorAuthorizer<SubjectInformationType> aasAggregatorAuthorizer;

  public IAASAggregatorAuthorizer<SubjectInformationType> getAasAggregatorAuthorizer() {
    return aasAggregatorAuthorizer;
  }

  private final IAASAPIAuthorizer<SubjectInformationType> aasApiAuthorizer;

  public IAASAPIAuthorizer<SubjectInformationType> getAasApiAuthorizer() {
    return aasApiAuthorizer;
  }

  private final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer;

  public ISubmodelAggregatorAuthorizer<SubjectInformationType> getSubmodelAggregatorAuthorizer() {
    return submodelAggregatorAuthorizer;
  }

  private final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer;

  public ISubmodelAPIAuthorizer<SubjectInformationType> getSubmodelAPIAuthorizer() {
    return submodelAPIAuthorizer;
  }

  private final IFilesAuthorizer<SubjectInformationType> filesAuthorizer;

  public IFilesAuthorizer<SubjectInformationType> getFilesAuthorizer() {
    return filesAuthorizer;
  }

  public Authorizers(
      final IAASAggregatorAuthorizer<SubjectInformationType> aasAggregatorAuthorizer,
      final IAASAPIAuthorizer<SubjectInformationType> aasApiAuthorizer,
      final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer,
      final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer,
      final IFilesAuthorizer<SubjectInformationType> filesAuthorizer
  ) {
    this.aasAggregatorAuthorizer = aasAggregatorAuthorizer;
    this.aasApiAuthorizer = aasApiAuthorizer;
    this.submodelAggregatorAuthorizer = submodelAggregatorAuthorizer;
    this.submodelAPIAuthorizer = submodelAPIAuthorizer;
    this.filesAuthorizer = filesAuthorizer;
  }
}
