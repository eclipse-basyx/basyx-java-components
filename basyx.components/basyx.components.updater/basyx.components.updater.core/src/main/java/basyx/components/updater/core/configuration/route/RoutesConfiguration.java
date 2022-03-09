/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/

 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package basyx.components.updater.core.configuration.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basyx.components.updater.core.configuration.*;

/**
 * An implementation of configurations of all the routes 
 * 
 * @author haque
 *
 */
public class RoutesConfiguration {
	private Map<String, DataSourceConfiguration> datasources = new HashMap<>();
	private Map<String, DataTransformerConfiguration> transformers = new HashMap<>();
	private Map<String, DataSinkConfiguration> datasinks = new HashMap<>();
	private Map<String, TimerConfiguration> timers = new HashMap<>();
	private Map<String, DelegatorConfiguration> delegators = new HashMap<>();
	private List<RouteConfiguration> routes = new ArrayList<>();

	public RoutesConfiguration() {}

	public RoutesConfiguration(
			List<DataSourceConfiguration> datasources,
			List<DataTransformerConfiguration> transformers,
			List<DataSinkConfiguration> datasinks,
			List<TimerConfiguration> timers,
			List<DelegatorConfiguration> delegators,
			List<RouteConfiguration> routes) {
		addDatasources(datasources);
		addTransformers(transformers);
		addDatasinks(datasinks);
		addTimers(timers);
		addRoutes(routes);
		addDelegators(delegators);
	}
	
	public RoutesConfiguration(
			Map<String, DataSourceConfiguration> datasources,
			Map<String, DataTransformerConfiguration> transformers,
			Map<String, DataSinkConfiguration> datasinks,
			Map<String, TimerConfiguration> timers,
			Map<String, DelegatorConfiguration> delegators,
			List<RouteConfiguration> routes) {
		setDatasources(datasources);
		setTransformers(transformers);
		setDatasinks(datasinks);
		setTimers(timers);
		addRoutes(routes);
		setDelegators(delegators);
	}

	public Map<String, DataSourceConfiguration> getDatasources() {
		return datasources;
	}

	public void setDatasources(Map<String, DataSourceConfiguration> datasources) {
		this.datasources = datasources;
	}

	public void addDatasources(List<DataSourceConfiguration> datasources) {
		for (DataSourceConfiguration datasource : datasources) {
			addDatasource(datasource);
		}
	}

	public void addDatasource(DataSourceConfiguration datasource) {
		this.datasources.put(datasource.getUniqueId(), datasource);
	}

	public Map<String, DataTransformerConfiguration> getTransformers() {
		return transformers;
	}

	public void setTransformers(Map<String, DataTransformerConfiguration> transformers) {
		this.transformers = transformers;
	}

	public void addTransformers(List<DataTransformerConfiguration> datatransformers) {
		for (DataTransformerConfiguration datatransformer : datatransformers) {
			addTransformer(datatransformer);
		}
	}

	public void addTransformer(DataTransformerConfiguration datatransformer) {
		this.transformers.put(datatransformer.getUniqueId(), datatransformer);
	}

	public Map<String, DataSinkConfiguration> getDatasinks() {
		return datasinks;
	}

	public void setDatasinks(Map<String, DataSinkConfiguration> datasinks) {
		this.datasinks = datasinks;
	}

	public void addDatasinks(List<DataSinkConfiguration> datasinks) {
		for (DataSinkConfiguration datasink : datasinks) {
			addDatasink(datasink);
		}
	}

	public void addDatasink(DataSinkConfiguration datasink) {
		this.datasinks.put(datasink.getUniqueId(), datasink);
	}

	public Map<String, TimerConfiguration>	getTimers() {
		return timers;
	}

	public void setTimers(Map<String, TimerConfiguration> timer) {
		this.timers = timers;
	}

	public void addTimers(List<TimerConfiguration> timers) {
		for (TimerConfiguration timer : timers) {
			addTimer(timer);
		}
	}

	public void addTimer(TimerConfiguration timer) {
		this.timers.put(timer.getUniqueId(), timer);
	}

	public Map<String, DelegatorConfiguration> getDelegators() {
		return delegators;
	}

	public void setDelegators(Map<String, DelegatorConfiguration> delegators) {
		this.delegators = delegators;
	}

	public void addDelegators(List<DelegatorConfiguration> delegators) {
		for (DelegatorConfiguration delegator : delegators) {
			addDelegator(delegator);
		}
	}

	public void addDelegator(DelegatorConfiguration delegator) {
		this.delegators.put(delegator.getUniqueId(), delegator);
	}

	public List<RouteConfiguration> getRoutes() {
		return routes;
	}

	public void setRoutes(List<RouteConfiguration> routes) {
		this.routes = routes;
	}

	public void addRoutes(List<RouteConfiguration> routes) {
		this.routes.addAll(routes);
	}

	public void addRoute(RouteConfiguration route) {
		this.routes.add(route);
	}
}
