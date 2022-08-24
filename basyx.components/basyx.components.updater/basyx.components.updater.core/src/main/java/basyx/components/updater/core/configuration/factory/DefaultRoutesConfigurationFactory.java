/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.core.configuration.factory;

import basyx.components.updater.core.configuration.route.event.EventRouteConfiguration;

/**
 * A default implementation of Routes configuration factory from a default file
 * path
 *
 * @deprecated Use the {@link RoutesConfigurationFactory} instead
 * @author haque
 *
 */
@Deprecated
public class DefaultRoutesConfigurationFactory extends RoutesConfigurationFactory {
	private static final String FILE_PATH = "routes.json";

	/**
	 * @deprecated use the
	 *             {@link RoutesConfigurationFactory#RoutesConfigurationFactory(ClassLoader)}
	 *             instead
	 *
	 * @param loader
	 */
	@Deprecated
	public DefaultRoutesConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, EventRouteConfiguration.class);
	}

	/**
	 * @deprecated use the
	 *             {@link RoutesConfigurationFactory#RoutesConfigurationFactory(String, ClassLoader)}
	 *             instead
	 *
	 * @param filePath
	 * @param loader
	 */
	@Deprecated
	public DefaultRoutesConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, EventRouteConfiguration.class);
	}
}
