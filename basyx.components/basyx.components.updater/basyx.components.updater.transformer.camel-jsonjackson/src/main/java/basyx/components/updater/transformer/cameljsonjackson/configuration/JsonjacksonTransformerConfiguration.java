/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.transformer.cameljsonjackson.configuration;

import basyx.components.updater.core.configuration.DataTransformerConfiguration;

/**
 * An implementation of JsonJackson transformer configuration
 * using camel jsonjackson component
 * 
 * @author Daniele Rossi
 *
 */
public class JsonjacksonTransformerConfiguration extends DataTransformerConfiguration {
	private String operation;
	private String jacksonModules;

	public JsonjacksonTransformerConfiguration() {}
	
	public JsonjacksonTransformerConfiguration(String uniqueId, String operation) {
		super(uniqueId);
		this.operation = operation;
	}
	
	public String getOperation() {
		return operation;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getJacksonModules() {
		return jacksonModules;
	}

	public void setJacksonModules(String jacksonModules) {
		this.jacksonModules = jacksonModules;
	}
	
	public String getConnectionURI() {
		String url = "dataformat:jackson:" + getOperation()+"?moduleClassNames="+getJacksonModules();
		return url;
	}
	
}
