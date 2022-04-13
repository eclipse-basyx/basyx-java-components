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
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;

import org.eclipse.basyx.components.tools.propertyfile.opdef.OperationDefinition;
import org.eclipse.basyx.components.tools.propertyfile.opdef.Parameter;
import org.eclipse.basyx.components.tools.propertyfile.opdef.ResultFilter;
import org.eclipse.basyx.tools.sql.driver.ISQLDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement a generic SQL query
 * 
 * @author kuhn
 *
 */
public class DynamicSQLOperation extends DynamicSQLRunner implements Function<Object[], Object> {
	private static Logger logger = LoggerFactory.getLogger(DynamicSQLOperation.class);

	/**
	 * Store SQL query string with place holders ($x)
	 */
	protected String sqlQueryString = null;

	/**
	 * Store SQL result filter
	 */
	protected String resultFilterString = null;

	/**
	 * Constructor
	 */
	public DynamicSQLOperation(ISQLDriver driver, String query, String sqlResultFilter) {
		// Invoke base constructor
		super(driver);

		// Store parameter count and SQL query string
		sqlQueryString = query;
		resultFilterString = sqlResultFilter;
	}

	/**
	 * Constructor
	 */
	public DynamicSQLOperation(String path, String user, String pass, String qryPfx, String qDrvCls, String query, String sqlResultFilter) {
		// Invoke base constructor
		super(path, user, pass, qryPfx, qDrvCls);

		// Store parameter count and SQL query string
		sqlQueryString = query;
		resultFilterString = sqlResultFilter;
	}

	/**
	 * Execute query with given parameter
	 */
	@Override
	public Object apply(Object[] parameter) {
		// Create list of query parameter
		Collection<String> sqlQueryParameter = new LinkedList<>();
		// - Add parameter
		for (Object par : parameter)
			sqlQueryParameter.add(par.toString());

		// Apply parameter and create SQL query string
		String sqlQuery = OperationDefinition.getSQLString(sqlQueryString, sqlQueryParameter);

		logger.debug("Running SQL query:" + sqlQuery);

		// Execute SQL query
		ResultSet sqlResult = sqlDriver.sqlQuery(sqlQuery);

		// Extract input parameter definition
		Collection<Parameter> resultParameter = OperationDefinition.getParameter(resultFilterString);

		// Process result
		try {
			// Create inner parameter array for call
			Object[] callParameterInner = new Object[resultParameter.size()];
			int i = 0;
			for (String column : getColumnNames(resultParameter))
				callParameterInner[i++] = column;

			// Create parameter array for call
			Object[] callParameter = new Object[2];
			callParameter[0] = sqlResult;
			callParameter[1] = callParameterInner;

			// Invoke result filter operation using static invocation
			return ResultFilter.class.getMethod(OperationDefinition.getOperation(resultFilterString), getMethodParameter(resultParameter)).invoke(null, callParameter);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error("Could not invoke dynamic sql operation", e);
		}

		// No result
		return null;
	}
}
