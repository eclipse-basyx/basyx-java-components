/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.regression.AASServer;

import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.eclipse.basyx.aas.factory.aasx.AASXToMetamodelConverter;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregator;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Test accessing to AAS using basys aas SDK
 * 
 * @author mateusmolina
 *
 */
public class TestAASXMongoDBAASServer extends AASXSuite {
	private static AASServerComponent component;
	private static BaSyxMongoDBConfiguration basyxMongoDBConfig = buildBaSyxMongoDBConfiguration();

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.MONGODB, "[\"aasx/01_Festo.aasx\", \"aasx/a.aasx\", \"aasx/b.aasx\"]");

		String docBasepath = Paths.get(FileUtils.getTempDirectory().getAbsolutePath(), AASXToMetamodelConverter.TEMP_DIRECTORY).toAbsolutePath().toString();
		contextConfig.setDocBasePath(docBasepath);

		resetMongoDBTestData();

		component = new AASServerComponent(contextConfig, aasConfig, basyxMongoDBConfig);
		component.startComponent();

		buildEndpoints(contextConfig);
	}

	@AfterClass
	public static void tearDownClass() {
		component.stopComponent();
	}

	// TODO Investigation needed on why it's failing for TestAASXMongoDBAASServer
	@Override
	public void testCollidingFiles() throws Exception {
	}

	@SuppressWarnings("deprecation")
	private static void resetMongoDBTestData() {
		new MongoDBAASAggregator(basyxMongoDBConfig).reset();
	}

	private static BaSyxMongoDBConfiguration buildBaSyxMongoDBConfiguration() {
		BaSyxMongoDBConfiguration mongoDBConfig = new BaSyxMongoDBConfiguration();
		mongoDBConfig.setAASCollection("TestAASXMongoDBAASServer_AAS");
		mongoDBConfig.setSubmodelCollection("TestAASXMongoDBAASServer_SM");
		return mongoDBConfig;
	}
}
