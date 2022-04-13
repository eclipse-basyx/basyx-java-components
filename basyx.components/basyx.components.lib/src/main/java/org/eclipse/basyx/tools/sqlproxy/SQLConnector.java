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
package org.eclipse.basyx.tools.sqlproxy;

import org.eclipse.basyx.tools.sql.driver.ISQLDriver;
import org.eclipse.basyx.tools.sql.driver.SQLDriver;

/**
 * Base class for classes that connect to SQL databases
 * 
 * @author kuhn
 *
 */
public abstract class SQLConnector {

	/**
	 * ID of table for this element object
	 */
	private String sqlTableID = null;

	/**
	 * SQL Driver for the connector
	 */
	private ISQLDriver driver;

	/**
	 * Constructor
	 * 
	 * @param user
	 *            SQL user name
	 * @param pass
	 *            SQL password
	 * @param url
	 *            SQL server URL
	 * @param driver
	 *            SQL driver
	 * @param prefix
	 *            JDBC SQL driver prefix
	 * @param tableID
	 *            ID of table for this element in database
	 */
	public SQLConnector(String user, String pass, String url, String driver, String prefix, String tableID) {
		// ID of table hat contains elements of this element
		sqlTableID = tableID;

		// Instantiate a driver for the SQL Connector
		this.driver = new SQLDriver(url, user, pass, prefix, driver);
	}

	/**
	 * Constructor
	 *
	 * @param driver
	 *            SQL Driver to connect with the database
	 * @param tableID
	 *            ID of table for this element in database
	 */
	public SQLConnector(ISQLDriver driver, String tableID) {
		// Store variables
		this.driver = driver;

		// ID of table hat contains elements of this element
		sqlTableID = tableID;
	}

	public String getSqlTableID() {
		return sqlTableID;
	}

	public ISQLDriver getDriver() {
		return driver;
	}

}
