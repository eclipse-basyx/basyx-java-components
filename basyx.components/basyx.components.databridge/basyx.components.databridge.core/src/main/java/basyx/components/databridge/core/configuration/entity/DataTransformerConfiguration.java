/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.databridge.core.configuration.entity;

/**
 * A generic class of Data transformer configuration
 * @author haque
 *
 */
public abstract class DataTransformerConfiguration extends RouteEntity {

	public DataTransformerConfiguration() {
		super();
	}
	
	public DataTransformerConfiguration(String uniqueId) {
		super(uniqueId);
	}
}
