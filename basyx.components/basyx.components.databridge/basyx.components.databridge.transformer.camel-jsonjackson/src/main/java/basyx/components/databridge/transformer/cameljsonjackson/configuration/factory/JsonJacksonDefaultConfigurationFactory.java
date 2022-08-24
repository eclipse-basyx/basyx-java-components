/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.databridge.transformer.cameljsonjackson.configuration.factory;

import basyx.components.databridge.core.configuration.factory.DataTransformerConfigurationFactory;
import basyx.components.databridge.transformer.cameljsonjackson.configuration.JsonJacksonTransformerConfiguration;

/**
 * JsonJackson default configuration factory from default path
 * 
 * @author Daniele Rossi
 *
 */
public class JsonJacksonDefaultConfigurationFactory extends DataTransformerConfigurationFactory {
	private static final String FILE_PATH = "jsonjacksontransformer.json";

	public JsonJacksonDefaultConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, JsonJacksonTransformerConfiguration.class);
	}

	public JsonJacksonDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, JsonJacksonTransformerConfiguration.class);
	}
}
