/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.core.delegator.server;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import basyx.components.updater.core.delegator.servlet.DelegatorServlet;

/**
 * This class exposes an Http API endpoint according to given
 * host, port, path.
 * @author haque
 *
 */
public class HttpServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
	
    private Tomcat tomcat;
	
    public HttpServer(int port, String host, String path, DelegatorServlet servlet) {
    	initializeServer(port, host, path, servlet);
	}
    
    /**
     * Initializes a tomcat server based on given properties
     * @param port
     * @param host
     * @param path
     * @param servlet
     */
    public void initializeServer(int port, String host, String path, DelegatorServlet servlet) {
    	tomcat = new Tomcat();
		tomcat.setPort(port);
		tomcat.setHostname(host);
		String appBase = ".";
		tomcat.getHost().setAppBase(appBase);
		File docBase = new File(System.getProperty("java.io.tmpdir"));
		Context context = tomcat.addContext("", docBase.getAbsolutePath());

		Tomcat.addServlet(
		  context, Integer.toString(servlet.hashCode()), servlet);
		context.addServletMappingDecoded(
		  path + "/*", Integer.toString(servlet.hashCode()));
    }
    
    /**
     * Starts the tomcat server
     */
    public void start() {
    	try {
    		tomcat.start();
    	} catch (Exception e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
    }
}
