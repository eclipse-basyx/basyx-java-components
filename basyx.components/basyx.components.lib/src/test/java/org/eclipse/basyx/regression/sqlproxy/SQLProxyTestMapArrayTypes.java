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
package org.eclipse.basyx.regression.sqlproxy;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.basyx.tools.sqlproxy.SQLRootElement;
import org.junit.Test;

/**
 * Test SQL map implementation with array types
 * 
 * @author kuhn
 *
 */
public class SQLProxyTestMapArrayTypes {

	/**
	 * Store SQL root element reference - An SQL root element is the main gateway to
	 * a SQL database
	 */
	protected SQLRootElement sqlRootElement = null;

	/**
	 * Test basic operations
	 */
	@Test
	public void test() throws Exception {
		// Create SQL root element
		sqlRootElement = new SQLRootElement(SQLConfig.SQLUSER, SQLConfig.SQLPW, "//localhost/basyx-map?", "org.postgresql.Driver", "jdbc:postgresql:", "root_el_01");

		// Drop tables to make sure we start with a fresh database
		sqlRootElement.dropTable(1);
		sqlRootElement.drop();

		// Create new table in database for root element
		sqlRootElement.createRootTableIfNotExists();

		// Create new SQL map
		Map<String, Object> sqlMap = sqlRootElement.createMap(1);

		// Clear map to make sure that no old data from previous test runs is stored in
		// it
		sqlMap.clear();

		// Test simple types
		sqlMap.put("intArray", new int[] { 1, 2, 3 });
		sqlMap.put("floatArray", new float[] { 1.2f, 2.2f, 3.4f });
		sqlMap.put("doubleArray", new double[] { 3.2, -2.7 });
		sqlMap.put("boolArray", new boolean[] { true, true, false });
		sqlMap.put("StringArray", new String[] { "x", "y", "z" });
		sqlMap.put("CharArray", new char[] { 'x', 'y', 'z' });

		// Check size
		assertTrue(sqlMap.size() == 6);

		// Test retrieving of values
		assertTrue(Arrays.equals((int[]) sqlMap.get("intArray"), new int[] { 1, 2, 3 }));
		assertTrue(Arrays.equals((float[]) sqlMap.get("floatArray"), new float[] { 1.2f, 2.2f, 3.4f }));
		assertTrue(Arrays.equals((double[]) sqlMap.get("doubleArray"), new double[] { 3.2, -2.7 }));
		assertTrue(Arrays.equals((boolean[]) sqlMap.get("boolArray"), new boolean[] { true, true, false }));
		assertTrue(Arrays.equals((String[]) sqlMap.get("StringArray"), new String[] { "x", "y", "z" }));
		assertTrue(Arrays.equals((char[]) sqlMap.get("CharArray"), new char[] { 'x', 'y', 'z' }));

		// Test value presence
		assertTrue(sqlMap.containsValue(new int[] { 1, 2, 3 }));

		// Clear map, check size
		sqlMap.clear();
		assertTrue(sqlMap.size() == 0);

		// Drop tables
		sqlRootElement.dropTable(1);

		// Drop table for root element (= delete it)
		sqlRootElement.drop();
	}
}
