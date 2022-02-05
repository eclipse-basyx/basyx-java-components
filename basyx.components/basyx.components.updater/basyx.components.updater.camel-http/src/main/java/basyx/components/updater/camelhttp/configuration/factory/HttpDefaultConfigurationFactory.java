/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.camelhttp.configuration.factory;

import basyx.components.updater.camelhttp.configuration.HttpConsumerConfiguration;
import basyx.components.updater.core.configuration.factory.DataSourceConfigurationFactory;

/**
 * A default configuration factory for http from a default file location
 * @author haque
 *
 */
public class HttpDefaultConfigurationFactory extends DataSourceConfigurationFactory {
	private static final String FILE_PATH = "httpconsumer.json";
	
	public HttpDefaultConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, HttpConsumerConfiguration.class);
	}
	
	public HttpDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, HttpConsumerConfiguration.class);
	}
}
