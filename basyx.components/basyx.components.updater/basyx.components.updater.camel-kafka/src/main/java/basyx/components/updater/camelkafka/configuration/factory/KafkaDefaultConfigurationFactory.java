/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.camelkafka.configuration.factory;

import basyx.components.updater.camelkafka.configuration.KafkaConsumerConfiguration;
import basyx.components.updater.core.configuration.factory.DataSourceConfigurationFactory;

/**
 * A default configuration factory for kafka from a default file location
 * @author haque
 *
 */
public class KafkaDefaultConfigurationFactory extends DataSourceConfigurationFactory {
	private static final String FILE_PATH = "kafkaconsumer.json";
	
	public KafkaDefaultConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, KafkaConsumerConfiguration.class);
	}
	
	public KafkaDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, KafkaConsumerConfiguration.class);
	}
}
