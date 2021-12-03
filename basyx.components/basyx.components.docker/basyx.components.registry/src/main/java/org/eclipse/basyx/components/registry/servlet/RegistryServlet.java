/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.registry.servlet;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.restapi.AASRegistryModelProvider;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;

/**
 * A registry servlet based on a provided registry implementation.
 *
 * @author pneuschwander
 */
public class RegistryServlet extends VABHTTPInterface<AASRegistryModelProvider> {
	private static final long serialVersionUID = 1L;

	/**
	 * Provides registry servlet based on the provided registry implementation.
	 */
	public RegistryServlet(IAASRegistry aasRegistry) {
		super(new AASRegistryModelProvider(aasRegistry));
	}
}
