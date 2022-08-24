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

import java.util.ArrayList;
import java.util.List;

import basyx.components.updater.core.configuration.route.core.RouteConfiguration;
import basyx.components.updater.core.configuration.route.event.EventRouteConfiguration;
import basyx.components.updater.core.configuration.route.timer.TimerRouteConfiguration;

/**
 * A generic implementation of routes configuration factory
 *
 * @author haque
 *
 */
public class RoutesConfigurationFactory extends ConfigurationFactory {
	private static final String DEFAULT_FILE_PATH = "routes.json";

	/**
	 * This constructor uses the {@link EventRouteConfiguration} as the default
	 * configuration
	 *
	 * @param filePath
	 * @param loader
	 */
	public RoutesConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, RouteConfiguration.class);
	}

	/**
	 * This constructor uses the default path {@link #DEFAULT_FILE_PATH} and the
	 * {@link EventRouteConfiguration} as the default configuration
	 *
	 * @param loader
	 */
	public RoutesConfigurationFactory(ClassLoader loader) {
		super(DEFAULT_FILE_PATH, loader, RouteConfiguration.class);
	}

	@SuppressWarnings("unchecked")
	public List<RouteConfiguration> create() {
		List<RouteConfiguration> configurations = (List<RouteConfiguration>) getConfigurationLoader().loadListConfiguration();

		return mapToSpecificRouteConfigurations(configurations);
	}

	private List<RouteConfiguration> mapToSpecificRouteConfigurations(List<RouteConfiguration> configurations) {
		List<RouteConfiguration> mapped = new ArrayList<>();

		for (RouteConfiguration configuration : configurations) {
			if (isEventConfiguration(configuration)) {
				mapped.add(new EventRouteConfiguration(configuration));
			} else if (isTimerConfiguration(configuration)) {
				mapped.add(new TimerRouteConfiguration(configuration));
			}
		}

		return mapped;
	}

	private boolean isTimerConfiguration(RouteConfiguration configuration) {
		return configuration.getRouteTrigger().equals(TimerRouteConfiguration.ROUTE_TRIGGER);
	}

	private boolean isEventConfiguration(RouteConfiguration configuration) {
		return configuration.getRouteTrigger().equals(EventRouteConfiguration.ROUTE_TRIGGER);
	}
}
