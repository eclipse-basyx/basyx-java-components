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

import java.util.List;

import basyx.components.updater.core.configuration.route.RouteConfiguration;

/**
 * A generic implementation of routes configuration factory
 *
 * @author haque
 *
 */
public class RoutesConfigurationFactory extends ConfigurationFactory {
	private static final String DEFAULT_FILE_PATH = "routes.json";

	public RoutesConfigurationFactory(String filePath, ClassLoader loader, Class<?> mapperClass) {
		super(filePath, loader, mapperClass);
	}

	public RoutesConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, RouteConfiguration.class);
	}

	/**
	 * This constructor uses the default path {@link #DEFAULT_FILE_PATH}
	 *
	 * @param loader
	 */
	public RoutesConfigurationFactory(ClassLoader loader) {
		super(DEFAULT_FILE_PATH, loader, RouteConfiguration.class);
	}

	/**
	 * @deprecated this method is deprecated please use {@link #create()}
	 */
	@Deprecated
	public List<RouteConfiguration> getRouteConfigurations() {
		return this.create();
	}

	@SuppressWarnings("unchecked")
	public List<RouteConfiguration> create() {
		return (List<RouteConfiguration>) getConfigurationLoader().loadListConfiguration();
	}
}
