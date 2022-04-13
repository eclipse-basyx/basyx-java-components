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
package org.eclipse.basyx.tools.sql.query;

import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.basyx.components.tools.propertyfile.opdef.OperationDefinition;
import org.eclipse.basyx.tools.sql.driver.ISQLDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement a generic SQL query
 * 
 * @author kuhn
 *
 */
public class DynamicSQLUpdate extends DynamicSQLRunner implements Consumer<Map<String, Object>> {
	private static Logger logger = LoggerFactory.getLogger(DynamicSQLUpdate.class);

	/**
	 * Store SQL query string with place holders ($x)
	 */
	protected String sqlQueryString = null;

	/**
	 * Constructor
	 */
	public DynamicSQLUpdate(ISQLDriver driver, String query) {
		// Invoke base constructor
		super(driver);

		// Store parameter count and SQL query string
		sqlQueryString = query;
	}

	/**
	 * Constructor
	 */
	public DynamicSQLUpdate(String path, String user, String pass, String qryPfx, String qDrvCls, String query) {
		// Invoke base constructor
		super(path, user, pass, qryPfx, qDrvCls);

		// Store parameter count and SQL query string
		sqlQueryString = query;
	}

	/**
	 * Execute update with given parameter
	 */
	@Override
	public void accept(Map<String, Object> parameter) {
		logger.debug("(Parameters) Running SQL update: " + parameter);

		// Apply parameter and create SQL query string
		String sqlQuery = OperationDefinition.getSQLString(sqlQueryString, parameter);

		logger.debug("(Query) Running SQL update:" + sqlQuery);

		// Execute SQL query
		sqlDriver.sqlUpdate(sqlQuery);
	}
}
