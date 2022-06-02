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

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.eclipse.basyx.components.IComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import basyx.components.updater.core.configuration.route.configuration.RoutesConfiguration;
import basyx.components.updater.core.configuration.route.configuration.SimpleRouteConfiguration;
import basyx.components.updater.core.configuration.route.configuration.TimerRouteConfiguration;
import basyx.components.updater.core.configuration.route.creatorfactory.IRouteCreatorFactory;
import basyx.components.updater.core.configuration.route.creatorfactory.SimpleRouteCreatorFactory;
import basyx.components.updater.core.configuration.route.creatorfactory.TimerRouteCreatorFactory;
import basyx.components.updater.core.routebuilder.DataBridgeRouteFactory;

/**
 * Core Updater component which can run the updater if routes configuration is
 * provided
 *
 * @author haque, fischer
 *
 */
public class UpdaterComponent implements IComponent {
	private static Logger logger = LoggerFactory.getLogger(UpdaterComponent.class);
	private RoutesConfiguration routeConfiguration;
	private Map<String, IRouteCreatorFactory> routeCreatorFactoryMap = new HashMap<>();
	private DataBridgeRouteFactory orchestrator;

	protected CamelContext camelContext;

	public UpdaterComponent(RoutesConfiguration configuration) {
		this.routeConfiguration = configuration;
		this.routeCreatorFactoryMap = getRouteCreatorFactoryMapDefault();
	}

	private Map<String, IRouteCreatorFactory> getRouteCreatorFactoryMapDefault() {
		Map<String, IRouteCreatorFactory> defaultRouteCreatorFactoryMap = new HashMap<>();
		defaultRouteCreatorFactoryMap.put(new SimpleRouteConfiguration().getRouteType(), new SimpleRouteCreatorFactory());
		defaultRouteCreatorFactoryMap.put(new TimerRouteConfiguration().getRouteType(), new TimerRouteCreatorFactory());

		return defaultRouteCreatorFactoryMap;
	}

	/**
	 * Starts the Camel component
	 */
	@Override
	public void startComponent() {
		startRoutesWithoutDelegator();
	}

	public void startRoutesWithoutDelegator() {
		camelContext = new DefaultCamelContext();
		try {
			orchestrator = new DataBridgeRouteFactory(routeConfiguration, routeCreatorFactoryMap);
			camelContext.addRoutes(orchestrator);
			camelContext.start();
			logger.info("Updater started");
		} catch (Exception e) {
			e.printStackTrace();
			camelContext = null;
		}
	}

	public void addRouteCreatorFactory(String key, IRouteCreatorFactory routeCreatorFactory) {
		orchestrator.addRouteCreatorFactory(key, routeCreatorFactory);
	}

	/**
	 * Stops the Camel component
	 */
	@Override
	public void stopComponent() {
		if (camelContext != null && !camelContext.isStopped()) {
			camelContext.stop();
			logger.info("Updater stopped");
		}
	}
}
