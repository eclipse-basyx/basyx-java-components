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

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.tools.sqlproxy.SQLMap;
import org.eclipse.basyx.tools.sqlproxy.SQLRootElement;
import org.junit.Test;

/**
 * Test SQL root element implementation, its creation, and dropping
 * 
 * @author kuhn
 *
 */
public class SQLProxyTestRootElementSQLElements {

	/**
	 * Store SQL root element reference - An SQL root element is the main gateway to
	 * a SQL database
	 */
	protected SQLRootElement sqlRootElement = null;

	/**
	 * Test basic operations
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void test() throws Exception {
		// Create SQL root element
		sqlRootElement = new SQLRootElement(SQLConfig.SQLUSER, SQLConfig.SQLPW, "//localhost/basyx-map?", "org.postgresql.Driver", "jdbc:postgresql:", "root_el_01");
		// - Create new table in database for root element
		sqlRootElement.drop();
		sqlRootElement.createRootTableIfNotExists();

		// Create map
		Map<String, Object> rootMap = sqlRootElement.createMap(0);
		// - Create contained collection
		Collection<Object> contCol = sqlRootElement.createCollection(sqlRootElement.getNextIdentifier());
		// - Create contained map
		Map<String, Object> contMap = sqlRootElement.createMap(sqlRootElement.getNextIdentifier());
		// - Add elements to root map
		rootMap.put("a", 13);
		rootMap.put("b", contCol);
		rootMap.put("c", contMap);
		// - Add elements to contained elements
		contCol.add('a');
		contCol.add('b');
		contCol.add(7);
		contMap.put("1", true);
		contMap.put("2", false);

		// Check map contents
		assertTrue((int) rootMap.get("a") == 13);
		assertTrue(rootMap.get("b") instanceof Collection);
		assertTrue(rootMap.get("c") instanceof Map);
		Collection<Object> contCol1 = (Collection<Object>) rootMap.get("b");
		Map<String, Object> contMap1 = (Map<String, Object>) rootMap.get("c");
		assertTrue(contCol1.size() == 3);
		assertTrue(contMap1.size() == 2);

		// Access root map via new reference
		Map<String, Object> rootMap2 = new SQLMap(sqlRootElement, 0);
		// - Access contained elements
		Collection<Object> contCol2 = (Collection<Object>) rootMap2.get("b");
		Map<String, Object> contMap2 = (Map<String, Object>) rootMap2.get("c");
		// Check sizes
		assertTrue(contCol2.size() == 3);
		assertTrue(contMap2.size() == 2);

		// Add another contained collection
		contMap.put("3", sqlRootElement.createCollection(sqlRootElement.getNextIdentifier()));
		// - Get contained elements
		Collection<Object> contCol3 = (Collection<Object>) contMap.get("3");
		// - Add elements
		contCol3.add("x");

		// Access contained elements
		Collection<Object> contCol4 = (Collection<Object>) contMap.get("3");
		// Check sizes
		assertTrue(contCol4.size() == 1);

		// Drop tables
		sqlRootElement.dropTable(0);
		sqlRootElement.dropTable(1);
		sqlRootElement.dropTable(2);
		sqlRootElement.dropTable(3);

		// Drop table for root element (= delete it)
		sqlRootElement.drop();
	}
}
