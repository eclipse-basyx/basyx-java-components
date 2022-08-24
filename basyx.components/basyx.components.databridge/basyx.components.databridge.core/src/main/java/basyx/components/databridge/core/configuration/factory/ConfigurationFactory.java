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

import basyx.components.databridge.core.configuration.loader.FileConfigurationLoader;

/**
 * A core generic implementation of configuration factory a which will load the
 * configurations by loading it from given file
 *
 * @author haque
 *
 */
public abstract class ConfigurationFactory {
	private FileConfigurationLoader configLoader;

	protected ConfigurationFactory(String filePath, ClassLoader loader, Class<?> mapperClass) {
		this.configLoader = new FileConfigurationLoader(filePath, loader, mapperClass);
	}

	protected FileConfigurationLoader getConfigurationLoader() {
		return this.configLoader;
	}
}
