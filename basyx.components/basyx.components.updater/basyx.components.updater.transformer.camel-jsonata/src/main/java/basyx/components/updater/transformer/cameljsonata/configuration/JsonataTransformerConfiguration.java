/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.transformer.cameljsonata.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import basyx.components.updater.core.configuration.entity.DataTransformerConfiguration;

import java.io.File;

/**
 * An implementation of Jsonata transformer configuration
 * using camel jsonata component
 * 
 * @author haque
 *
 */
public class JsonataTransformerConfiguration extends DataTransformerConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(JsonataTransformerConfiguration.class);
	private String queryPath;
	private String inputType;
	private String outputType;
	
	public JsonataTransformerConfiguration() {}
	
	public JsonataTransformerConfiguration(String uniqueId, String queryPath, String inputType, String outputType) {
		super(uniqueId);
		this.queryPath = queryPath;
		this.inputType = inputType;
		this.outputType = outputType;
	}

	public String getQueryPath() {
		return queryPath;
	}

	public void setQueryPath(String queryPath) {
		this.queryPath = queryPath;
	}

	public String getConnectionURI() {
		String url = "";
		File jsonataFile = new File(getQueryPath());

		if (jsonataFile.exists()) {
			logger.info("Looking for jsonata config as configured in jsonatatransformer.json...");
			url = "jsonata:" + "file:./" + getQueryPath() + "?inputType=" + getInputType() + "&outputType=" + getOutputType();
		} else {
			logger.info("Couldn't find jsonata config as configured in jsonatatransformer.json. Looking in classpath...");
			url = "jsonata:" + getQueryPath() + "?inputType=" + getInputType() + "&outputType=" + getOutputType();
		}
		return url;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}
}
