/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/

 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package basyx.components.updater.core.routebuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.tooling.model.Strings;

import basyx.components.updater.core.configuration.DataSinkConfiguration;
import basyx.components.updater.core.configuration.DataSourceConfiguration;
import basyx.components.updater.core.configuration.DataTransformerConfiguration;
import basyx.components.updater.core.configuration.DelegatorConfiguration;
import basyx.components.updater.core.configuration.route.RouteConfiguration;
import basyx.components.updater.core.configuration.route.RoutesConfiguration;
import basyx.components.updater.core.delegator.servlet.DelegatorServlet;

/**
 * updater route builder inherits from {@link RouteBuilder} to 
 * create the routes  
 * 
 * @author haque
 *
 */
public class UpdaterRouteBuilder extends RouteBuilder {
	private RoutesConfiguration configuration;

	public UpdaterRouteBuilder(RoutesConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public void configure() throws Exception {
		createRoutes();
	}

	/**
	 * Creates the routes for the camel context based on configurations
	 * @throws Exception
	 */
	public void createRoutes() throws Exception {
		// Here, it would be possible to optimize routes
		// optimizeRoutes(configuration.routes);
		// Here, a more complex route builder could generate a routes from a route graph
		// instead of building one separate route per property
		for (RouteConfiguration routeConfig : configuration.getRoutes()) {
			createRoute(configuration, routeConfig);
		}	

	}

	/**
	 * Creates a single route
	 * @param configuration
	 * @param routeConfig
	 * @throws Exception 
	 */
	private void createRoute(RoutesConfiguration configuration, RouteConfiguration routeConfig) throws Exception {
		String dataSourceEndpoint = createDatasourceEndpoint(configuration, routeConfig.getDatasource());
		String[] dataSinkEndpoints = createDatasinkEndpoint(configuration, routeConfig.getDatasinks());
		String[] dataTransformerEndpoints = createDataTransformerEndpoint(configuration, routeConfig.getTransformers());
		
		if (Strings.isNullOrEmpty(routeConfig.getDelegator())) {
			if (dataTransformerEndpoints == null || dataTransformerEndpoints.length == 0) {
				from(dataSourceEndpoint).to("log:updater").to(dataSinkEndpoints).to("log:updater");
			} else {
				from(dataSourceEndpoint).to("log:updater").to(dataTransformerEndpoints).to("log:updater").to(dataSinkEndpoints).to("log:updater");
			}
		} else {
			DelegatorServlet delegatorServlet = getDelegatorServlet(configuration, routeConfig.getDelegator());
			if (dataTransformerEndpoints == null || dataTransformerEndpoints.length == 0) {
				from(dataSourceEndpoint).to("log:updater").to(dataSinkEndpoints).to("log:updater").bean(delegatorServlet, "processMessage");
			} else {
				from(dataSourceEndpoint).to("log:updater").to(dataTransformerEndpoints).to("log:updater").to(dataSinkEndpoints).to("log:updater").bean(delegatorServlet, "processMessage");
			}	
		}
	}
	
	/**
	 * Creates data sink endpoint for each sink
	 * @param routesConfig
	 * @param dataSinks
	 * @return
	 */
	private String[] createDatasinkEndpoint(RoutesConfiguration routesConfig, List<String> dataSinks) {
		List<String> ret = new ArrayList<String>();
		for (String sinkId : dataSinks) {
			ret.add(createDatasinkEndpoint(routesConfig, sinkId));
		}
		return ret.toArray(new String[0]);
	}

	/**
	 * Creates data sink end point for a single sink
	 * @param routesConfig
	 * @param dataSinkId
	 * @return
	 */
	private String createDatasinkEndpoint(RoutesConfiguration routesConfig, String dataSinkId) {
		DataSinkConfiguration dsConfig = routesConfig.getDatasinks().get(dataSinkId);
		if (dsConfig != null) {
			return dsConfig.getConnectionURI();	
		} else {
			return null;
		}
	}

	/**
	 * Creates a data source endpoint from {@link DataSourceConfiguration}
	 * @param routesConfig
	 * @param dataSourceId
	 * @return
	 */
	private String createDatasourceEndpoint(RoutesConfiguration routesConfig, String dataSourceId) {
		DataSourceConfiguration dsConfig = routesConfig.getDatasources().get(dataSourceId);
		if (dsConfig != null) {
			return dsConfig.getConnectionURI();	
		} else {
			return null;
		}
	}

	/**
	 * Creates data transformer endpoint for multiple transformers
	 * @param routesConfig
	 * @param dataTransformers
	 * @return
	 */
	private String[] createDataTransformerEndpoint(RoutesConfiguration routesConfig, List<String> dataTransformers) {
		List<String> ret = new ArrayList<String>();
		for (String transformerId : dataTransformers) {
			ret.add(createDataTransformerEndpoint(routesConfig, transformerId));
		}
		
		if (ret == null || ret.size() == 0) {
			return null;	
		} else {
			return ret.toArray(new String[0]);	
		}	
	}

	/**
	 * Creates a data transformer endpoint for single transformer
	 * @param routesConfig
	 * @param dataTransformerId
	 * @return
	 */
	private String createDataTransformerEndpoint(RoutesConfiguration routesConfig, String dataTransformerId) {
		DataTransformerConfiguration dtConfig = routesConfig.getTransformers().get(dataTransformerId);
		if (dtConfig != null) {
			return dtConfig.getConnectionURI();	
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the delegator servlet of the {@link DataSourceConfiguration}
	 * @param routesConfig
	 * @param dataSourceId
	 * @return
	 */
	private DelegatorServlet getDelegatorServlet(RoutesConfiguration routesConfig, String delegatorId) {
		DelegatorConfiguration delegatorConfig = routesConfig.getDelegators().get(delegatorId);
		if (delegatorConfig != null) {
			return delegatorConfig.getDelegatorServlet();	
		} else {
			return null;
		}
	}
}
