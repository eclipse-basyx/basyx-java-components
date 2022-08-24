/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.core.configuration.route.timer;

import java.util.List;

import basyx.components.updater.core.configuration.route.core.RouteConfiguration;

/**
 * A connection of a single route (source, transformer(s), sink(s))
 *
 * @author fischer
 *
 */
public class TimerRouteConfiguration extends RouteConfiguration {
	public static final String ROUTE_TRIGGER = "timer";

	public TimerRouteConfiguration(String datasource, List<String> transformers, List<String> datasinks) {
		super(ROUTE_TRIGGER, datasource, transformers, datasinks);
	}

	public TimerRouteConfiguration(RouteConfiguration configuration) {
		super(configuration);
	}

	@Override
	public String getRouteTrigger() {
		return ROUTE_TRIGGER;
	}
}
