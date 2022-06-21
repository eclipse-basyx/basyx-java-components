/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.aas.configuration;

import basyx.components.updater.core.configuration.entity.DataSourceConfiguration;

/**
 * An implementation of Basyx internal data source configuration
 * @author haque
 *
 */
public class BasyxInternalDatasourceConfiguration extends DataSourceConfiguration {
	private String type;
	
	public BasyxInternalDatasourceConfiguration() {}
	
	public BasyxInternalDatasourceConfiguration(String uniqueId, String serverUrl, int serverPort) {
		super(uniqueId, serverUrl, 8080);
	}
	
	@Override
	public String getConnectionURI() {
		return null;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
