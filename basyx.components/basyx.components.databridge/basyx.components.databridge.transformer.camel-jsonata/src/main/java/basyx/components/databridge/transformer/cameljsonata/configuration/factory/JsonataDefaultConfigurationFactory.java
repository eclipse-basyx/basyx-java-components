/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.databridge.transformer.cameljsonata.configuration.factory;

import basyx.components.databridge.core.configuration.factory.DataTransformerConfigurationFactory;
import basyx.components.databridge.transformer.cameljsonata.configuration.JsonataTransformerConfiguration;

/**
 * Jsonata default configuration factory from default path
 * @author haque
 *
 */
public class JsonataDefaultConfigurationFactory extends DataTransformerConfigurationFactory {
	private static final String FILE_PATH = "jsonatatransformer.json";
	
	public JsonataDefaultConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, JsonataTransformerConfiguration.class);
	}
	
	public JsonataDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, JsonataTransformerConfiguration.class);
	}
}
