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

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.eclipse.basyx.components.IComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import basyx.components.updater.core.configuration.route.RoutesConfiguration;
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

	protected CamelContext camelContext;

	public UpdaterComponent(RoutesConfiguration configuration) {
		this.routeConfiguration = configuration;
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
			DataBridgeRouteFactory orchestrator = new DataBridgeRouteFactory(routeConfiguration);
			camelContext.addRoutes(orchestrator);
			camelContext.start();
			logger.info("Updater started");
		} catch (Exception e) {
			e.printStackTrace();
			camelContext = null;
		}
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
