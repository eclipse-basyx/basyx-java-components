/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.databridge.aas.configuration.factory;

import basyx.components.databridge.aas.configuration.AASDatasinkConfiguration;
import basyx.components.databridge.core.configuration.factory.DataSinkConfigurationFactory;

/**
 * A default configuration factory for AAS producer
 * @author haque
 *
 */
public class AASProducerDefaultConfigurationFactory extends DataSinkConfigurationFactory {
	private static final String FILE_PATH = "aasserver.json";
	
	public AASProducerDefaultConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, AASDatasinkConfiguration.class);
	}
	
	public AASProducerDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, AASDatasinkConfiguration.class);
	}
}
