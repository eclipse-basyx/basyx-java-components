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
import java.util.Collection;

import org.eclipse.basyx.tools.sqlproxy.SQLRootElement;
import org.junit.Test;

/**
 * Test SQL collection element implementation (set operations)
 * 
 * @author kuhn
 *
 */
public class SQLProxyTestCollectionSetOperations {

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

		// Create collection
		Collection<Object> sqlColl = sqlRootElement.createCollection(1);

		// Test simple collection functions
		assertTrue(sqlColl.size() == 0);
		// - Add single element
		sqlColl.add("A");
		assertTrue(sqlColl.size() == 1);
		sqlColl.add("B");
		assertTrue(sqlColl.size() == 2);
		sqlColl.add("A");
		assertTrue(sqlColl.size() == 3);
		// - Check element presence
		assertTrue(sqlColl.containsAll(Arrays.asList("A", "B", "A")));

		// Add more elements
		sqlColl.addAll(Arrays.asList("D", "E", "F"));
		// - Size check
		assertTrue(sqlColl.size() == 6);
		// - Check contained elements
		assertTrue(sqlColl.containsAll(Arrays.asList("A", "B", "A", "D", "E", "F")));

		// Remove elements
		sqlColl.removeAll(Arrays.asList("D", "F"));
		// - Size check
		assertTrue(sqlColl.size() == 4);
		// - Check contained elements
		assertTrue(sqlColl.containsAll(Arrays.asList("A", "B", "A", "E")));

		// Now add some array types
		sqlColl.addAll(Arrays.asList((Object) new int[] { 1, 2, 3 }, new float[] { 1.2f, 4.5f }));
		// - Size check
		assertTrue(sqlColl.size() == 6);
		// - Check contained elements
		assertTrue(sqlColl.containsAll(Arrays.asList((Object) new int[] { 1, 2, 3 }, new float[] { 1.2f, 4.5f })));
		assertTrue(!sqlColl.containsAll(Arrays.asList((Object) new int[] { 1, 2, 3 }, new float[] { 1.2f, 4.5f }, new int[] { 8, 0 })));
		// - Check element presence
		assertTrue(sqlColl.contains(new int[] { 1, 2, 3 }) == true);
		assertTrue(sqlColl.contains(new float[] { 1.2f, 4.5f }) == true);

		// Get all elements from collection
		Object[] arr = sqlColl.toArray();
		// - Check array contents
		assertTrue(arr.length == 6);
		assertTrue(arr[0].equals("A"));
		assertTrue(arr[1].equals("B"));
		assertTrue(arr[2].equals("A"));
		assertTrue(arr[3].equals("E"));
		assertTrue(Arrays.equals((int[]) arr[4], new int[] { 1, 2, 3 }));
		assertTrue(Arrays.equals((float[]) arr[5], new float[] { 1.2f, 4.5f }));

		// Delete most elements
		sqlColl.retainAll(Arrays.asList(new int[] { 1, 2, 3 }, "A"));
		// - Size check
		assertTrue(sqlColl.size() == 3);
		// - Check element presence
		assertTrue(sqlColl.contains(new int[] { 1, 2, 3 }) == true);
		assertTrue(sqlColl.contains("A") == true);

		// Drop tables
		sqlRootElement.dropTable(1);

		// Drop table for root element (= delete it)
		sqlRootElement.drop();
	}
}
