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
package org.eclipse.basyx.regression.support.directory;

import org.eclipse.basyx.vab.registry.memory.VABInMemoryRegistry;

/**
 * Implement the test suite directory service with pre-configured directory
 * entries
 * 
 * @author kuhn
 *
 */
public class ComponentsTestsuiteDirectory extends VABInMemoryRegistry {

	/**
	 * Constructor - load all directory entries
	 */
	public ComponentsTestsuiteDirectory() {
		// Populate with entries from base implementation
		super();

		// Define mappings
		// - SQL provider mappings
		addMapping("SQLTestSubmodel", "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/sqlsm/");
		addMapping("sampleDB.SQLTestAAS", "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/sqlsm/");
		// - CFG provider mappings
		addMapping("CfgFileTestAAS", "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/cfgsm/");
		addMapping("sampleCFG.CfgFileTestAAS", "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/cfgsm/");
		// - Raw CFG provider mappings
		addMapping("AASProvider", "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/rawcfgsm/");
		addMapping("RawCfgFileTestAAS", "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/rawcfgsm/");
		addMapping("sampleRawCFG.RawCfgFileTestAAS", "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/rawcfgsm/");
		// - XQuery provider mappings
		addMapping("XMLXQueryFileTestAAS", "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/xmlxquery/");
		// - Processengine mappings
		addMapping("coilcar", "http://localhost:8080/basys.components/Testsuite/Processengine/coilcar/");
		addMapping("submodel1", "http://localhost:8080/basys.components/Testsuite/Processengine/coilcar/");
	}
}
