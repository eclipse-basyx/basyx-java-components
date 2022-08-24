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

import java.util.ArrayList;
import java.util.List;

import basyx.components.updater.core.configuration.route.core.RouteConfiguration;

/**
 * A connection of a single route (source, transformer, sink)
 *
 * @author fischer
 *
 */
public class TimerRouteConfiguration extends RouteConfiguration {
	private static final String ROUTE_TYPE = "timer";
	private List<String> datasinks = new ArrayList<>();
	private TimerConfiguration timerConfig;

	public TimerRouteConfiguration() {
		this.routeType = ROUTE_TYPE;
	}

	public TimerRouteConfiguration(String datasource, List<String> transformers, List<String> datasinks, TimerConfiguration timerConfig) {
		this();
		this.datasource = datasource;
		this.transformers = transformers;
		this.datasinks = datasinks;
		this.setTimerConfig(timerConfig);
	}

	public List<String> getDatasinks() {
		return datasinks;
	}

	public void setDatasinks(List<String> datasinks) {
		this.datasinks = datasinks;
	}

	public TimerConfiguration getTimerConfig() {
		return timerConfig;
	}

	public void setTimerConfig(TimerConfiguration timerConfig) {
		this.timerConfig = timerConfig;
	}
}
