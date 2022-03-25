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

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.basyx.components.tools.propertyfile.opdef.OperationDefinition;
import org.eclipse.basyx.components.tools.propertyfile.opdef.Parameter;
import org.eclipse.basyx.components.tools.propertyfile.opdef.ResultFilter;
import org.eclipse.basyx.tools.sql.driver.ISQLDriver;

/**
 * Implement a generic SQL query
 * 
 * @author kuhn
 *
 */
public class DynamicSQLQuery extends DynamicSQLRunner implements Supplier<Object> {

	/**
	 * Store SQL query string with place holders ($x)
	 */
	protected String sqlQueryString = null;

	/**
	 * Store SQL result filter
	 */
	protected String resultFilterString = null;

	/**
	 * Constructor that accepts a driver
	 */
	public DynamicSQLQuery(ISQLDriver driver, String query, String sqlResultFilter) {
		// Invoke base constructor
		super(driver);

		// Store SQL query string and result filter
		sqlQueryString = query;
		resultFilterString = sqlResultFilter;
	}

	/**
	 * Constructor
	 */
	public DynamicSQLQuery(String path, String user, String pass, String qryPfx, String qDrvCls, String query, String sqlResultFilter) {
		// Invoke base constructor
		super(path, user, pass, qryPfx, qDrvCls);

		// Store SQL query string and result filter
		sqlQueryString = query;
		resultFilterString = sqlResultFilter;
	}

	/**
	 * Execute query without parameter
	 */
	@Override
	public Object get() {
		// Execute SQL query
		ResultSet sqlResult = sqlDriver.sqlQuery(sqlQueryString);

		// Process result
		return processResult(sqlResult);
	}

	/**
	 * Execute query without parameter, do not post process result
	 */
	public ResultSet getRaw() {
		// Execute SQL query
		return sqlDriver.sqlQuery(sqlQueryString);
	}

	/**
	 * Execute query without parameter
	 */
	public Object get(Map<String, Object> param) {
		// Apply parameter and create SQL query string
		String sqlQuery = OperationDefinition.getSQLString(sqlQueryString, param);

		// Execute SQL query
		ResultSet sqlResult = sqlDriver.sqlQuery(sqlQuery);

		// Process result
		return processResult(sqlResult);
	}

	/**
	 * Execute query without parameter, do not post process result
	 */
	public ResultSet getRaw(Map<String, Object> param) {
		// Apply parameter and create SQL query string
		String sqlQuery = OperationDefinition.getSQLString(sqlQueryString, param);

		// Execute SQL query
		return sqlDriver.sqlQuery(sqlQuery);
	}

	/**
	 * Process result parameter
	 */
	protected Object processResult(ResultSet sqlResult) {
		// Extract input parameter definition
		Collection<Parameter> parameter = OperationDefinition.getParameter(resultFilterString);

		// Process result
		try {
			// Create inner parameter array for call
			Object[] callParameterInner = new Object[parameter.size()];
			int i = 0;
			for (String column : getColumnNames(parameter))
				callParameterInner[i++] = column;

			// Create parameter array for call
			Object[] callParameter = new Object[2];
			callParameter[0] = sqlResult;
			callParameter[1] = callParameterInner;

			// Invoke result filter operation using static invocation
			Object result = ResultFilter.class.getMethod(OperationDefinition.getOperation(resultFilterString), getMethodParameter(parameter)).invoke(null, callParameter);

			// Close result set
			sqlResult.close();

			// Return result
			return result;

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SQLException e) {
			// Print exception to console
			e.printStackTrace();
		}

		// No result
		return null;
	}
}
