/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.transformer.cameljsonjackson.configuration.factory;

import basyx.components.updater.core.configuration.factory.DataTransformerConfigurationFactory;
import basyx.components.updater.transformer.cameljsonjackson.configuration.JsonjacksonTransformerConfiguration;

/**
 * JsonJackson default configuration factory from default path
 * 
 * @author Daniele Rossi
 *
 */
public class JsonjacksonDefaultConfigurationFactory extends DataTransformerConfigurationFactory {
	private static final String FILE_PATH = "jsonjacksontransformer.json";

	public JsonjacksonDefaultConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, JsonjacksonTransformerConfiguration.class);
	}

	public JsonjacksonDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, JsonjacksonTransformerConfiguration.class);
	}
}
