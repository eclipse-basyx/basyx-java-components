/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.databridge.camelactivemq.configuration.factory;

import basyx.components.databridge.camelactivemq.configuration.ActiveMQConsumerConfiguration;
import basyx.components.databridge.core.configuration.factory.DataSourceConfigurationFactory;

/**
 * A default configuration factory for ActiveMQ from a default path
 * @author haque
 *
 */
public class ActiveMQDefaultConfigurationFactory extends DataSourceConfigurationFactory {
	private static final String FILE_PATH = "activemqconsumer.json";
	
	public ActiveMQDefaultConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, ActiveMQConsumerConfiguration.class);
	}
	
	public ActiveMQDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, ActiveMQConsumerConfiguration.class);
	}
}
