/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.core.configuration.route.configuration;

import java.util.ArrayList;
import java.util.List;

import basyx.components.updater.core.configuration.route.sources.TimerConfiguration;

/**
 * A connection of a single route (source, transformer, sink)
 *
 * @author fischer
 *
 */
public class TimerRouteConfiguration implements IRouteConfiguration {
	private static final String ROUTE_TYPE = "TIMER";
	private String routeId;
	private String datasource;
	private List<String> transformers = new ArrayList<>();
	private List<String> datasinks = new ArrayList<>();
	private TimerConfiguration timerConfig;
	private String delegator;

	public TimerRouteConfiguration() {
	}

	public TimerRouteConfiguration(String datasource, List<String> transformers, List<String> datasinks, TimerConfiguration timerConfig) {
		this.datasource = datasource;
		this.transformers = transformers;
		this.datasinks = datasinks;
		this.setTimerConfig(timerConfig);
	}

	@Override
	public String getRouteId() {
		return routeId;
	}

	@Override
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	@Override
	public String getRouteType() {
		return ROUTE_TYPE;
	}

	@Override
	public List<String> getTransformers() {
		return transformers;
	}

	@Override
	public void setTransformers(List<String> transformers) {
		this.transformers = transformers;
	}

	@Override
	public String getDelegator() {
		return delegator;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
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
