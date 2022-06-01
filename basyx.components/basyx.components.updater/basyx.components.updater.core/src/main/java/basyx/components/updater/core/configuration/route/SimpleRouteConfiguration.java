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
import java.util.List;

/**
 * A connection of a single route (source, transformer(s), sink)
 *
 * @author haque, fischer
 *
 */
public class SimpleRouteConfiguration implements IRouteConfiguration {
	private static final String ROUTE_TYPE = "SIMPLE";
	private String routeId;
	private String datasource;
	private String datasink;
	private List<String> transformers = new ArrayList<>();

	public SimpleRouteConfiguration() {
	}

	public SimpleRouteConfiguration(String datasource, List<String> transformers, String datasink) {
		this.datasource = datasource;
		this.transformers = transformers;
		this.datasink = datasink;
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

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getDatasink() {
		return datasink;
	}

	public void setDatasink(String datasink) {
		this.datasink = datasink;
	}
}
