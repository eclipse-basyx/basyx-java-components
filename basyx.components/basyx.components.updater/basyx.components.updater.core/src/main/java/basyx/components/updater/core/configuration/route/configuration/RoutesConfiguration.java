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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basyx.components.updater.core.configuration.route.sources.DataSinkConfiguration;
import basyx.components.updater.core.configuration.route.sources.DataSourceConfiguration;
import basyx.components.updater.core.configuration.route.sources.DataTransformerConfiguration;
import basyx.components.updater.core.configuration.route.sources.DelegatorConfiguration;

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
	private Map<String, DelegatorConfiguration> delegators = new HashMap<>();
	private List<IRouteConfiguration> routes = new ArrayList<>();

	public RoutesConfiguration() {
	}

	public RoutesConfiguration(List<DataSourceConfiguration> datasources, List<DataTransformerConfiguration> transformers, List<DataSinkConfiguration> datasinks, List<DelegatorConfiguration> delegators, List<IRouteConfiguration> routes) {
		addDatasources(datasources);
		addTransformers(transformers);
		addDatasinks(datasinks);
		addRoutes(routes);
		addDelegators(delegators);
	}

	public RoutesConfiguration(Map<String, DataSourceConfiguration> datasources, Map<String, DataTransformerConfiguration> transformers, Map<String, DataSinkConfiguration> datasinks, Map<String, DelegatorConfiguration> delegators,
			List<IRouteConfiguration> routes) {
		setDatasources(datasources);
		setTransformers(transformers);
		setDatasinks(datasinks);
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

	public List<IRouteConfiguration> getRoutes() {
		return routes;
	}

	public void setRoutes(List<IRouteConfiguration> routes) {
		this.routes = routes;
	}

	public void addRoutes(List<IRouteConfiguration> routes) {
		this.routes.addAll(routes);
	}

	public void addRoute(IRouteConfiguration route) {
		this.routes.add(route);
	}
}
