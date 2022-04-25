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
package org.eclipse.basyx.regression.sql;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.regression.support.directory.ComponentsTestsuiteDirectory;
import org.eclipse.basyx.regression.support.server.context.ComponentsRegressionContext;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.AASHTTPServerResource;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test SQL queries
 * 
 * @author kuhn
 *
 */
public class SQLQueriesTest {

	/**
	 * Store HTTP asset administration shell manager backend
	 */
	protected VABConnectionManager connManager = new VABConnectionManager(new ComponentsTestsuiteDirectory(), new HTTPConnectorFactory());

	/**
	 * Makes sure Tomcat Server is started
	 */
	@ClassRule
	public static AASHTTPServerResource res = new AASHTTPServerResource(new ComponentsRegressionContext());

	/**
	 * Test basic queries
	 */
	@Test
	@Ignore
	public void test() throws Exception {

		// Connect to sub model "CfgFileTestAAS"
		VABElementProxy connSubmodel = this.connManager.connectToVABElement("SQLTestSubmodel");

		// Get sub model
		Object value0A = connSubmodel.getValue("/aas/submodels/SQLTestSubmodel");

		// Get properties
		Object value0B = connSubmodel.getValue("/aas/submodels/SQLTestSubmodel/dataElements");

		// Get property value
		Object value1 = connSubmodel.getValue("/aas/submodels/SQLTestSubmodel/dataElements/sensorNames/value");

		// Get property value with meta data
		Object value1a = connSubmodel.getValue("/aas/submodels/SQLTestSubmodel/dataElements/sensorNames");

		// Create a new property
		// - HashMap that contains new table line
		Map<String, Object> newTableLine = new HashMap<>();
		newTableLine.put("sensorname", "VS_0003");
		newTableLine.put("sensorid", "033542");
		Property p = new Property(newTableLine);
		p.setIdShort("sensorNames");
		// - Insert line into table
		connSubmodel.createValue("/aas/submodels/SQLTestSubmodel/dataElements", p);

		// Get property value again
		Object value2 = connSubmodel.getValue("/aas/submodels/SQLTestSubmodel/dataElements/sensorNames/value");

		Object value2a = connSubmodel.getValue("/aas/submodels/SQLTestSubmodel/dataElements/sensorNames");

		// Update property value
		// - Here this adds a new value into the table
		// - Collection that contains call values
		Map<String, Object> updatedTableLine = new HashMap<>();
		updatedTableLine.put("sensorname", "VS_0004");
		updatedTableLine.put("sensorid", "033542");
		// - Update table line
		connSubmodel.setValue("/aas/submodels/SQLTestSubmodel/dataElements/sensorNames/value", updatedTableLine);

		// Get property value again
		Object value3 = connSubmodel.getValue("/aas/submodels/SQLTestSubmodel/dataElements/sensorNames/value");

		// Delete property with ID 033542
		// - Map that contains call values
		Map<String, Object> removedTableLine = new HashMap<>();
		removedTableLine.put("sensorid", "033542");
		// - Delete sensor from table
		connSubmodel.deleteValue("/aas/submodels/SQLTestSubmodel/dataElements/sensorNames/value", removedTableLine);

		// Get property value again
		Object value4 = connSubmodel.getValue("/aas/submodels/SQLTestSubmodel/dataElements/sensorNames/value");

		// Get property meta data value
		Object value5 = connSubmodel.getValue("/aas/submodels/SQLTestSubmodel/dataElements/sensorNames/category");
	}
}
