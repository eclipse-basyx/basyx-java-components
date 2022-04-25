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
package org.eclipse.basyx.components.tools.propertyfile.opdef;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that provides result filters for operation definitions
 * 
 * @author kuhn
 *
 */
public class ResultFilter {
	private static Logger logger = LoggerFactory.getLogger(ResultFilter.class);

	/**
	 * Extract a column from a SQL result set and return this column as set
	 * 
	 * @param sqlResult
	 *            SQL result
	 * @param columnName
	 *            Name of column to extract
	 */
	public static Set<String> stringSet(ResultSet sqlResult, Object... columnName) {
		// Create result
		Set<String> result = new HashSet<>();

		// Process all SQL results
		try {
			while (sqlResult.next()) {
				result.add(sqlResult.getString((String) columnName[0]));
			}
		} catch (SQLException e) {
			logger.error("Could not get string set from sqlResult", e);
		}

		// Return result
		return result;
	}

	/**
	 * Extract a column from a SQL result set and return this column as collection
	 * 
	 * @param sqlResult
	 *            SQL result
	 * @param columnName
	 *            Name of column to extract
	 */
	public static Object stringArray(ResultSet sqlResult, Object... columnName) {
		// Create result
		Collection<String> result = new LinkedList<>();

		// Process all SQL results
		try {
			while (sqlResult.next()) {
				result.add(sqlResult.getString((String) columnName[0]));
			}
		} catch (SQLException e) {
			logger.error("Could not get string collection from sqlResult", e);
		}

		// Return result
		return result;
	}

	/**
	 * Return SQL result set as Map
	 * 
	 * @param sqlResult
	 *            SQL result
	 * @param columnNames
	 *            Name of column to extract
	 */
	public static Object mapArray(ResultSet sqlResult, Object... columnNames) {
		// Create result
		Map<String, Object> result = new HashMap<>();

		// Process all SQL results
		try {
			while (sqlResult.next()) {
				// Process columns
				for (Object columnName : columnNames) {
					result.put((String) columnName, sqlResult.getObject((String) columnName));
				}
			}
		} catch (SQLException e) {
			logger.error("Could not get map from sqlResult", e);
		}

		// Return result
		return result;
	}

	/**
	 * Return SQL result set as List of Maps
	 * 
	 * @param sqlResult
	 *            SQL result
	 * @param columnNames
	 *            Name of column to extract
	 */
	public static Object listOfMaps(ResultSet sqlResult, Object... columnNames) {
		// Create result
		List<Object> result = new LinkedList<>();

		// Process all SQL results
		try {
			while (sqlResult.next()) {
				// List element
				Map<String, Object> listElement = new HashMap<>();

				// Process columns
				for (Object columnName : columnNames) {
					listElement.put((String) columnName, sqlResult.getObject((String) columnName));
				}

				// Add list element to result
				result.add(listElement);
			}
		} catch (SQLException e) {
			logger.error("Could not get list from sqlResult", e);
		}

		// Return result
		return result;
	}
}
