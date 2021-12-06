/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.core.configuration;

/**
 * An generic class of Route Entity. parent of all configuration class
 * @author haque
 *
 */
public abstract class RouteEntity {
	private String uniqueId;
	
	public RouteEntity() {
		this.uniqueId = null;
	}
	
	public RouteEntity(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	/**
	 * Retrieves the connection URI of the configuration
	 * @return
	 */
	public abstract String getConnectionURI();
}
