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

import java.util.List;

import basyx.components.updater.core.configuration.DelegatorConfiguration;

/**
 * A generic implementation of delegators configuration factory
 * @author haque
 *
 */
public class DelegatorsConfigurationFactory extends ConfigurationFactory {

	public DelegatorsConfigurationFactory(String filePath, ClassLoader loader,
			Class<?> mapperClass) {
		super(filePath, loader, mapperClass);
	}

	@SuppressWarnings("unchecked")
	public List<DelegatorConfiguration> getDelegatorConfigurations() {
		return (List<DelegatorConfiguration>) getConfigurationLoader().loadListConfiguration();
	}
}
