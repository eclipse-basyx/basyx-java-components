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

import basyx.components.updater.core.configuration.DataTransformerConfiguration;

/**
 * An implementation of Jsonata transformer configuration
 * using camel jsonata component
 * 
 * @author haque
 *
 */
public class JsonataTransformerConfiguration extends DataTransformerConfiguration {
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
		String url = "jsonata:" + getQueryPath() + "?inputType=" + getInputType() + "&outputType=" + getOutputType();
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
