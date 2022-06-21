/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.core.configuration.entity;

/**
 * A generic class of Data Sink Configuration
 * @author haque
 *
 */
public abstract class DataSinkConfiguration extends RouteEntity {

	public DataSinkConfiguration() {
		super();
	}
	
	public DataSinkConfiguration(String uniqueId) {
		super(uniqueId);
	}
}
