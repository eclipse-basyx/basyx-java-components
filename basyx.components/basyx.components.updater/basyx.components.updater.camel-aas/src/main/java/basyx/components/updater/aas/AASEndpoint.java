/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.aas;

import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AAS component which can connect to Asset Administration Shells via a given
 * registry
 */
@UriEndpoint(firstVersion = "1.0.0-SNAPSHOT", scheme = "aas", title = "AAS", syntax = "aas:name",
             category = {Category.JAVA})
public class AASEndpoint extends DefaultEndpoint {
	private static final Logger logger = LoggerFactory.getLogger(AASEndpoint.class);
	
	@UriPath
	@Metadata(required = true)
	private String name;

	@UriParam(defaultValue = "")
	private String propertyPath;

	public AASEndpoint() {
    }

	public AASEndpoint(String uri, AASComponent component) {
        super(uri, component);
    }

    @Override
	public Producer createProducer() throws Exception {
		return new AASProducer(this);
    }

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		return null;
	}

	/**
	 * Sets the name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * The path to the property relative to the target AAS
	 */
	public String getPropertyPath() {
		return propertyPath;
	}

	public void setPropertyPath(String propertyPath) {
		this.propertyPath = propertyPath;
	}
	
	public String getFullProxyUrl() {
		String elemUrl = String.format("%s/submodels/%s/submodel/submodelElements/%s", this.getAASEndpoint(), this.getSubmodelId(), this.getSubmodelElementId());
		logger.info("Proxy URL: " + elemUrl);
		return elemUrl;
	}
	
	/**
	 * Gets the AAS URL for connection
	 * @return
	 */
	private String getAASEndpoint() {
		String onlyEndpoint = this.getEndpointBaseUri().substring(6); 
    	logger.info("only url " + onlyEndpoint);
		return onlyEndpoint;
	}
	
	/**
	 * Gets the Submodel ID for data dump
	 * @return
	 */
	private String getSubmodelId() {
		String submodelId = VABPathTools.getEntry(getPropertyPath(), 0);
    	logger.info("Submodel ID: " + submodelId);
		return submodelId;
	}
	
	/**
	 * Gets the submodel element id for data dump
	 * @return 
	 */
	private String getSubmodelElementId() {
		String submodelElementId = VABPathTools.skipEntries(getPropertyPath(), 1);
    	logger.info("Submodel Element ID: " + submodelElementId);
		return submodelElementId;
	}
	
}
