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
package org.eclipse.basyx.components.servlet.aas;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;

/**
 * AAS servlet class that exports a given Asset Administration Shell
 * 
 * @author kuhn
 *
 */
public class AASServlet extends VABHTTPInterface<MultiSubmodelProvider> {

	/**
	 * ID of serialized instances
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor - based on a VABMultiSubmodelProvider
	 */
	public AASServlet() {
		super(new MultiSubmodelProvider());
	}

	/**
	 * Constructor for directly creating the AAS in the provider
	 */
	public AASServlet(AssetAdministrationShell exportedAAS) {
		this();
		getModelProvider().setAssetAdministrationShell(new AASModelProvider(exportedAAS));
	}
}
