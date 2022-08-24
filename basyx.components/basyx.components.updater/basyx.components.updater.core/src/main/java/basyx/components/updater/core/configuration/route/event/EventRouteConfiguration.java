/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.core.configuration.route.event;

import java.util.List;

import basyx.components.updater.core.configuration.route.core.RouteConfiguration;

/**
 * A connection of a single route (source, transformer(s), sink(s))
 *
 * @author haque, fischer
 *
 */
public class EventRouteConfiguration extends RouteConfiguration {
	private static final String ROUTE_TYPE = "event";
	private String datasink;

	public EventRouteConfiguration() {
		this.routeType = ROUTE_TYPE;
	}

	public EventRouteConfiguration(String datasource, List<String> transformers, String datasink) {
		this();
		this.datasource = datasource;
		this.transformers = transformers;
		this.datasink = datasink;
	}

	public String getDatasink() {
		return datasink;
	}

	public void setDatasink(String datasink) {
		this.datasink = datasink;
	}

}
