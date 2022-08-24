/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.databridge.hono;

import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UriEndpoint(firstVersion = "1.0.0-SNAPSHOT", scheme = "hono", title = "HONO", syntax = "hono:name",
category = {Category.JAVA})
public class HonoEndpoint extends DefaultEndpoint {
	private static final Logger logger = LoggerFactory.getLogger(HonoEndpoint.class);
	
    @UriPath @Metadata(required = true)
    private String name;

	@UriParam(defaultValue = "15671")
    private String port = "15671";

	@UriParam(defaultValue = "consumer@HONO")
	private String userName = "consumer@HONO";

	@UriParam(defaultValue = "")
	private String tenantId = "";
	
	@UriParam(defaultValue = "")
	private String deviceId = "";
	
	@UriParam(defaultValue = "verysecret")
	private String password = "verysecret";

	public HonoEndpoint() {
    }

	public HonoEndpoint(String uri, HonoComponent component) {
        super(uri, component);
        logger.info("Hono URI: " + uri);
    }

    @Override
	public Producer createProducer() throws Exception {
		return null;
    }

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		return new HonoConsumer(this, processor);
	}

    /**
     * Sets the name of the hono tenant
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the hono tenant
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets connection port
     * @return
     */
	public String getPort() {
		return port;
	}

	/**
	 * Sets connection port
	 * @param port
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * Gets user name for the connection
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets user name for the connection
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Gets the password to connect to the device
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password to connect to the device
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets tenant id
	 * @return
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * Gets tenant id
	 * @param tenantId
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	/**
	 * Gets device id
	 * @return
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * Sets device id
	 * @param deviceId
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	/**
	 * Gets the Hono host for connection
	 * @return
	 */
	public String getHonoHost() {
		String onlyEndpoint = this.getEndpointBaseUri().substring(7); 
    	logger.info("only url " + onlyEndpoint);
		return onlyEndpoint;
	}
	
}
