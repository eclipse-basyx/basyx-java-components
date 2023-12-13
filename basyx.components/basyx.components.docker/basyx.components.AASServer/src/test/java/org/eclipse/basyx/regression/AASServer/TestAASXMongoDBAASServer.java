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
package org.eclipse.basyx.regression.AASServer;

import java.net.URLEncoder;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.factory.aasx.AASXToMetamodelConverter;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregator;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test accessing to AAS using basys aas SDK
 * 
 * @author mateusmolina
 *
 */
public class TestAASXMongoDBAASServer extends AASXSuite {
	private static Logger logger = LoggerFactory.getLogger(TestAASXMongoDBAASServer.class);
	private static AASServerComponent component;
	private static BaSyxMongoDBConfiguration basyxMongoDBConfig = buildBaSyxMongoDBConfiguration();

	@BeforeClass
	public static void setUpClass() throws Exception {
		// Setup component's test configuration
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.MONGODB, "[\"aasx/01_Festo.aasx\", \"aasx/a.aasx\", \"aasx/b.aasx\"]");

		String docBasepath = Paths.get(FileUtils.getTempDirectory().getAbsolutePath(), AASXToMetamodelConverter.TEMP_DIRECTORY).toAbsolutePath().toString();
		contextConfig.setDocBasePath(docBasepath);

		resetMongoDBTestData();

		// Start the component
		component = new AASServerComponent(contextConfig, aasConfig, basyxMongoDBConfig);
		component.startComponent();

		rootEndpoint = contextConfig.getUrl() + "/";
		aasEndpoint = rootEndpoint + "/" + AASAggregatorProvider.PREFIX + "/" + aasId.getEncodedURN() + "/aas";
		smEndpoint = aasEndpoint + "/submodels/" + smIdShort + "/submodel";
		String encodedAasAId = URLEncoder.encode(aasAId.getId(), "UTF-8");
		aasAEndpoint = rootEndpoint + "/" + AASAggregatorProvider.PREFIX + "/" + encodedAasAId + "/aas";
		smAEndpoint = aasAEndpoint + "/submodels/" + smAIdShort + "/submodel";
		String encodedAasBId = URLEncoder.encode(aasBId.getId(), "UTF-8");
		aasBEndpoint = rootEndpoint + "/" + AASAggregatorProvider.PREFIX + "/" + encodedAasBId + "/aas";
		smBEndpoint = aasBEndpoint + "/submodels/" + smBIdShort + "/submodel";
		logger.info("AAS URL for servlet test: " + aasEndpoint);
	}

	@AfterClass
	public static void tearDownClass() {
		component.stopComponent();
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
