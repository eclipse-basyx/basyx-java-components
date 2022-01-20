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
 * A connection of a single route (source, transformer, sink)
 * @author haque
 *
 */
public class RouteConfiguration {
	private String datasource;
	private List<String> transformers = new ArrayList<>();
	private List<String> datasinks = new ArrayList<>();
	private String delegator;
	
	public RouteConfiguration() {}
	
	public RouteConfiguration(String datasource, List<String> transformers, List<String> datasinks) {
		this.datasource = datasource;
		this.transformers = transformers;
		this.datasinks = datasinks;
	}
	
	public RouteConfiguration(String datasource, List<String> transformers, List<String> datasinks, String delegator) {
		this.datasource = datasource;
		this.transformers = transformers;
		this.datasinks = datasinks;
		this.delegator = delegator;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public List<String> getTransformers() {
		return transformers;
	}

	public void setTransformers(List<String> transformers) {
		this.transformers = transformers;
	}

	public List<String> getDatasinks() {
		return datasinks;
	}

	public void setDatasinks(List<String> datasinks) {
		this.datasinks = datasinks;
	}
	
	public String getDelegator() {
		return delegator;
	}

	public void setDelegator(String delegator) {
		this.delegator = delegator;
	}
}
