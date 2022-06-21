/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.aas.configuration;

import basyx.components.updater.core.configuration.entity.DataSinkConfiguration;

/**
 * An implementation of AAS data sink configuration
 * @author haque
 *
 */
public class AASDatasinkConfiguration extends DataSinkConfiguration {
	private static final String PROPERTY_TYPE = "PROPERTY";

	private String type;
	private String endpoint;
	private String path;

	public AASDatasinkConfiguration() {}
	
	public AASDatasinkConfiguration(String aasEndpoint, String propertyPath, String uniqueId) {
		super(uniqueId);
		this.type = PROPERTY_TYPE;
		this.endpoint = aasEndpoint;
		this.path = propertyPath;
	}
	
	public AASDatasinkConfiguration(String aasEndpoint, String propertyPath) {
		this(aasEndpoint, propertyPath, null);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String getConnectionURI() {
		String endpointDefinition = "aas:";
		endpointDefinition += this.endpoint;
		endpointDefinition += "?propertyPath=" + this.path;
		return endpointDefinition;
	}
}
