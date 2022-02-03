/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.core.component;

import java.util.ArrayList;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.tooling.model.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import basyx.components.updater.core.configuration.DelegatorConfiguration;
import basyx.components.updater.core.configuration.route.RouteConfiguration;
import basyx.components.updater.core.configuration.route.RoutesConfiguration;
import basyx.components.updater.core.delegator.server.HttpServer;
import basyx.components.updater.core.delegator.servlet.DelegatorServlet;
import basyx.components.updater.core.routebuilder.UpdaterRouteBuilder;

/**
 * Core Updater component which can run the updater if 
 * routes configuration is provided
 * 
 * @author haque
 *
 */
public class UpdaterComponent {
	private static Logger logger = LoggerFactory.getLogger(UpdaterComponent.class);
	private RoutesConfiguration routesWithoutDelegator;
	private RoutesConfiguration delegatorRoutes;
	
	protected CamelContext camelContext;
	
	public UpdaterComponent(RoutesConfiguration configuration) {
		setRoutesWithoutDelegator(configuration);
		setDelegatorRoutes(configuration);
	}

	/**
	 * Starts the Camel component
	 */
	public void startComponent() {
		startRoutesWithoutDelegator();
		startDelegatoRoutes();
	}
	
	public void startRoutesWithoutDelegator() {
		camelContext = new DefaultCamelContext();
		try {
			camelContext.addRoutes(new UpdaterRouteBuilder(routesWithoutDelegator));
			camelContext.start();
			logger.info("Updater started");
		} catch (Exception e) {
			e.printStackTrace();
			camelContext = null;
		}
	}
	
	public void startDelegatoRoutes() {
		try {
			for (RouteConfiguration route : delegatorRoutes.getRoutes()) {
				CamelContext camelContext = new DefaultCamelContext();
				RoutesConfiguration singleConfiguration = copyRoutesConfiguration(delegatorRoutes);
				singleConfiguration.addRoute(route);
				
				UpdaterRouteBuilder builder = new UpdaterRouteBuilder(singleConfiguration);

				DelegatorConfiguration delegator = singleConfiguration.getDelegators().get(route.getDelegator());
				DelegatorServlet servlet = new DelegatorServlet(camelContext);
				delegator.setDelegatorServlet(servlet);
				startDelegatorServer(delegator);
				camelContext.addRoutes(builder);
			}
			
			logger.info("Updater Delegator started");
		} catch (Exception e) {
			e.printStackTrace();
			camelContext = null;
		}
	}
	
	private void startDelegatorServer(DelegatorConfiguration configuration) {	
		HttpServer server = new HttpServer(configuration.getPort(), configuration.getHost(), configuration.getPath(), configuration.getDelegatorServlet());
		server.start();
	}

	/**
	 * Stops the Camel component
	 */
	public void stopComponent() {
		if (camelContext != null && !camelContext.isStopped()) {
			camelContext.stop();
			logger.info("Updater stopped");
		}
	}
	
	/**
	 * Retrieves the routes without delegator
	 * @param configuration
	 * @return
	 */
	private void setRoutesWithoutDelegator(RoutesConfiguration configuration) {
		RoutesConfiguration routesWithoutDelegator = copyRoutesConfiguration(configuration);
		for (RouteConfiguration route : configuration.getRoutes()) {
			if (Strings.isNullOrEmpty(route.getDelegator())) {
				routesWithoutDelegator.addRoute(route);
			}
		}
		this.routesWithoutDelegator = routesWithoutDelegator;
	}
	
	/**
	 * Retrieves the routes with delegator
	 * @param configuration
	 * @return
	 */
	private void setDelegatorRoutes(RoutesConfiguration configuration) {
		RoutesConfiguration delegatorRoutes = copyRoutesConfiguration(configuration);
		for (RouteConfiguration route : configuration.getRoutes()) {
			if (!Strings.isNullOrEmpty(route.getDelegator())) {
				delegatorRoutes.addRoute(route);
			}
		}
		this.delegatorRoutes = delegatorRoutes;
	}
	
	/**
	 * Copies route configuration and return another instance without the route definition
	 * @param configuration
	 * @return
	 */
	private RoutesConfiguration copyRoutesConfiguration(RoutesConfiguration configuration) {
		return new RoutesConfiguration(
				configuration.getDatasources(), 
				configuration.getTransformers(), 
				configuration.getDatasinks(),
				configuration.getTimers(),
				configuration.getDelegators(),
				new ArrayList<RouteConfiguration>()
			);
	}
}
