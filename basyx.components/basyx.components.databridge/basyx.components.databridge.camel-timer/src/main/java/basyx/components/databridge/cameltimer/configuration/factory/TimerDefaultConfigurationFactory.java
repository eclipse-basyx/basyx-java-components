/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.databridge.cameltimer.configuration.factory;

import basyx.components.databridge.cameltimer.configuration.TimerConsumerConfiguration;
import basyx.components.databridge.core.configuration.factory.DataSourceConfigurationFactory;

/**
 * A default configuration factory for Camels Timer from a default path
 * @author n14s 
 *
 */
public class TimerDefaultConfigurationFactory extends DataSourceConfigurationFactory {
	private static final String FILE_PATH = "timerconsumer.json";
	
	public TimerDefaultConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, TimerConsumerConfiguration.class);
	}
	
	public TimerDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, TimerConsumerConfiguration.class);
	}
}
