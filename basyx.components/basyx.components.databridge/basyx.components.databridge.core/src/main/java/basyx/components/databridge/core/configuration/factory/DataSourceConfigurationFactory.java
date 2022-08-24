/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/

 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package basyx.components.databridge.core.configuration.factory;

import java.util.List;

import basyx.components.databridge.core.configuration.entity.DataSourceConfiguration;

/**
 * A generic implementation of data source configuration factory
 *
 * @author haque
 *
 */
public class DataSourceConfigurationFactory extends ConfigurationFactory {

	public DataSourceConfigurationFactory(String filePath, ClassLoader loader, Class<?> mapperClass) {
		super(filePath, loader, mapperClass);
	}

	@SuppressWarnings("unchecked")
	public List<DataSourceConfiguration> create() {
		return (List<DataSourceConfiguration>) getConfigurationLoader().loadListConfiguration();
	}
}
