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
package org.eclipse.basyx.regression.registry;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.components.configuration.BaSyxSQLConfiguration;
import org.eclipse.basyx.components.registry.sql.SQLRegistry;
import org.eclipse.basyx.testsuite.regression.aas.registration.TestRegistryProviderSuite;
import org.eclipse.basyx.tools.sqlproxy.SQLRootElement;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for a local registry provider based on SQL tables
 * 
 * @author espen
 *
 */
public class TestSQLRegistryProvider extends TestRegistryProviderSuite {

	@BeforeClass
	public static void setUpClass() {
		SQLRootElement root = getSQLRootElement();
		root.drop();
	}

	@AfterClass
	public static void tearDownClass() {
		SQLRootElement root = getSQLRootElement();
		root.drop();
	}

	protected static SQLRootElement getSQLRootElement() {
		// Load config
		BaSyxSQLConfiguration config = new BaSyxSQLConfiguration();
		config.loadFromResource("sql.properties");

		// Create SQL driver instance
		String path = config.getPath();
		String user = config.getUser();
		String pass = config.getPass();
		String qryPfx = config.getPrefix();
		String qDrvCls = config.getDriver();
		return new SQLRootElement(user, pass, path, qDrvCls, qryPfx, SQLRegistry.TABLE_ID);
	}

	@Override
	protected IAASRegistry getRegistryService() {
		BaSyxSQLConfiguration sqlConfig = new BaSyxSQLConfiguration();
		sqlConfig.loadFromResource("sql.properties");
		return new SQLRegistry(sqlConfig);
	}

	/**
	 * Tests, if the data has been persisted by creating a new registry with the
	 * same settings
	 */
	@Test
	public void testPersistency() {
		// Create new SQLRegistry with same configuration
		BaSyxSQLConfiguration sqlConfig = new BaSyxSQLConfiguration();
		sqlConfig.loadFromResource("sql.properties");
		IAASRegistry registry = new SQLRegistry(sqlConfig);

		// Try to "overwrite" data
		AASDescriptor aasDesc2 = new AASDescriptor(aasIdShort2, aasId2, asset2, aasEndpoint2);
		proxy.register(aasDesc2);

		// Retrieve and check the first AAS
		AASDescriptor descriptor = registry.lookupAAS(aasId1);
		super.validateDescriptor1(descriptor);

		// Retrieve and check the second AAS
		AASDescriptor descriptor2 = registry.lookupAAS(aasId2);
		super.validateDescriptor2(descriptor2);
	}
}
