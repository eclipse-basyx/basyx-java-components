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

import basyx.components.updater.core.configuration.DataSinkConfiguration;

/**
 * A generic implementation of data sink configuration factory
 *
 * @author haque
 *
 */
public class DataSinkConfigurationFactory extends ConfigurationFactory {

	public DataSinkConfigurationFactory(String filePath, ClassLoader loader, Class<?> mapperClass) {
		super(filePath, loader, mapperClass);
	}

	/**
	 * @deprecated this method is deprecated please use {@link #create()}
	 */
	@Deprecated
	public List<DataSinkConfiguration> getDataSinkConfigurations() {
		return this.create();
	}

	@SuppressWarnings("unchecked")
	public List<DataSinkConfiguration> create() {
		return (List<DataSinkConfiguration>) getConfigurationLoader().loadListConfiguration();
	}
}
