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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import basyx.components.updater.core.configuration.route.RoutesConfiguration;
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
	private RoutesConfiguration configuration;
	
	protected CamelContext camelContext;

	public UpdaterComponent(RoutesConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Starts the Camel component
	 */
	public void startComponent() {
		camelContext = new DefaultCamelContext();
		try {
			camelContext.addRoutes(new UpdaterRouteBuilder(configuration));
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
	public void stopComponent() {
		if (camelContext != null && !camelContext.isStopped()) {
			camelContext.stop();
			logger.info("Updater stopped");
		}
	}
}
