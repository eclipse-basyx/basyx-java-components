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

import basyx.components.updater.core.configuration.entity.DataTransformerConfiguration;

/**
 * An implementation of Basyx internal transformer configuration
 * @author haque
 *
 */
public class BasyxInternalTransformerConfiguration extends DataTransformerConfiguration {

	public BasyxInternalTransformerConfiguration() {}
	
	public BasyxInternalTransformerConfiguration(String uniqueId) {
		super(uniqueId);
	}

	@Override
	public String getConnectionURI() {
		return null;
	}
}
