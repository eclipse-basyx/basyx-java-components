/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.components.registry.sql;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import org.eclipse.basyx.aas.registration.memory.AASRegistry;
import org.eclipse.basyx.aas.registration.memory.MapRegistryHandler;
import org.eclipse.basyx.components.configuration.BaSyxSQLConfiguration;
import org.eclipse.basyx.tools.sqlproxy.SQLRootElement;

/**
 * Implements a local registry based on an SQL database
 * 
 * @author espen
 *
 */
public class SQLRegistry extends AASRegistry {
	public final static String TABLE_ID = "root_registry";

	/**
	 * Constructor using default sql connection
	 */
	public SQLRegistry() {
		super(new MapRegistryHandler(new AASDescriptorMap(createRootMap(new BaSyxSQLConfiguration()))));
	}

	/**
	 * Creates a SQLRegistry from a sql configuration
	 */
	public SQLRegistry(BaSyxSQLConfiguration configuration) {
		super(new MapRegistryHandler(new AASDescriptorMap(createRootMap(configuration))));
	}

	private static Map<String, Object> createRootMap(BaSyxSQLConfiguration config) {
		SQLRootElement sqlRootElement = initSQLConnection(config);
		sqlRootElement.createRootTableIfNotExists();
		return sqlRootElement.retrieveRootMap();
	}

	/**
	 * Initialize sqlDriver
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 * @throws ServletException
	 */
	private static final SQLRootElement initSQLConnection(BaSyxSQLConfiguration config) {
		// SQL parameter
		String path = config.getPath();
		String user = config.getUser();
		String pass = config.getPass();
		String qryPfx = config.getPrefix();
		String qDrvCls = config.getDriver();

		// Create SQL driver instance
		return new SQLRootElement(user, pass, path, qDrvCls, qryPfx, TABLE_ID);
	}
}
