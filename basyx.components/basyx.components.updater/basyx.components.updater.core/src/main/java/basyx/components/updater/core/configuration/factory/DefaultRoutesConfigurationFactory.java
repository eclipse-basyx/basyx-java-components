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

import basyx.components.updater.core.configuration.route.RouteConfiguration;

/**
 * A default implementation of Routes configuration factory 
 * from a default file path
 *
 * @author haque
 *
 */
public class DefaultRoutesConfigurationFactory extends RoutesConfigurationFactory {
	private static final String FILE_PATH = "routes.json";
	
	public DefaultRoutesConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, RouteConfiguration.class);
	}
	
	public DefaultRoutesConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, RouteConfiguration.class);
	}
}
