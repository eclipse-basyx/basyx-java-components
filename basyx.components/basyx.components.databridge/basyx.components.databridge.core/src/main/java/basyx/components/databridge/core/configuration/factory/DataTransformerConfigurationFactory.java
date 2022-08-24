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

import basyx.components.databridge.core.configuration.entity.DataTransformerConfiguration;

/**
 * A generic implementation of data transformer configuration factory
 *
 * @author haque
 *
 */
public class DataTransformerConfigurationFactory extends ConfigurationFactory {

	public DataTransformerConfigurationFactory(String filePath, ClassLoader loader, Class<?> mapperClass) {
		super(filePath, loader, mapperClass);
	}

	@SuppressWarnings("unchecked")
	public List<DataTransformerConfiguration> create() {
		return (List<DataTransformerConfiguration>) getConfigurationLoader().loadListConfiguration();
	}
}
