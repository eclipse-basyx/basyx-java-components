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

import java.sql.ResultSet;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.basyx.components.tools.propertyfile.opdef.Parameter;
import org.eclipse.basyx.tools.sql.driver.ISQLDriver;
import org.eclipse.basyx.tools.sql.driver.SQLDriver;

/**
 * SQL query operation
 * 
 * @author kuhn
 *
 */
public class DynamicSQLRunner {

	/**
	 * Store SQL driver instance
	 */
	protected ISQLDriver sqlDriver = null;

	/**
	 * Constructor that accepts a driver
	 */
	public DynamicSQLRunner(ISQLDriver driver) {
		// Store SQL driver instance
		sqlDriver = driver;
	}

	/**
	 * Constructor
	 */
	public DynamicSQLRunner(String path, String user, String pass, String qryPfx, String qDrvCls) {
		// Create SQL driver instance
		sqlDriver = new SQLDriver(path, user, pass, qryPfx, qDrvCls);
	}

	/**
	 * Get method parameter definition
	 */
	protected Class<?>[] getMethodParameter(Collection<Parameter> parameter) {
		// Store operation signature
		Class<?>[] result = new Class<?>[2];

		// Operation signature is ResultSet and a list of string parameter that define
		// column names
		result[0] = ResultSet.class;
		result[1] = Object[].class;

		// Return signature
		return result;
	}

	/**
	 * Get column names
	 */
	protected Collection<String> getColumnNames(Collection<Parameter> parameter) {
		// Return value
		Collection<String> result = new LinkedList<>();

		for (Parameter par : parameter)
			result.add(par.getName());

		return result;
	}
}
