/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.core.configuration.factory;

import basyx.components.updater.core.configuration.DelegatorConfiguration;

/**
 * A default configuration factory for Delegators from a default path
 * @author haque
 *
 */
public class DefaultDelegatorsConfigurationFactory extends DelegatorsConfigurationFactory {
	private static final String FILE_PATH = "delegator.json";
	
	public DefaultDelegatorsConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, DelegatorConfiguration.class);
	}
	
	public DefaultDelegatorsConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, DelegatorConfiguration.class);
	}
}
