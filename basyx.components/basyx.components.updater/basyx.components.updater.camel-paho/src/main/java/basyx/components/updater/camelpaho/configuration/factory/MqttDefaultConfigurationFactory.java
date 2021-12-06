/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.camelpaho.configuration.factory;

import basyx.components.updater.camelpaho.configuration.MqttConsumerConfiguration;
import basyx.components.updater.core.configuration.factory.DataSourceConfigurationFactory;

/**
 * A default configuration factory for MQTT from a default file path
 * @author haque
 *
 */
public class MqttDefaultConfigurationFactory extends DataSourceConfigurationFactory {
	private static final String FILE_PATH = "mqttconsumer.json";
	
	public MqttDefaultConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, MqttConsumerConfiguration.class);
	}
	
	public MqttDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, MqttConsumerConfiguration.class);
	}
}
