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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IFile;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedFile;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Suite for testing that the XMLAAS servlet is set up correctly. The tests here
 * can be used by the servlet test itself and the integration test
 * 
 * @author schnicke, espen
 *
 */
public abstract class AASXSuite {
	private static Logger logger = LoggerFactory.getLogger(AASXSuite.class);

	protected IAASRegistry aasRegistry;

	protected static final String aasShortId = "Festo_3S7PM0CP4BD";
	protected static final ModelUrn aasId = new ModelUrn("smart.festo.com/demo/aas/1/1/454576463545648365874");
	protected static final ModelUrn smId = new ModelUrn("www.company.com/ids/sm/4343_5072_7091_3242");
	protected static final String smIdShort = "Nameplate";

	protected static final String aasAIdShort = "aasA";
	protected static final String smAIdShort = "a";
	protected static final CustomId aasAId = new CustomId("AssetAdministrationShell---51A6D8AE");
	protected static final CustomId smAId = new CustomId("fileTestA");
	protected static final String aasBIdShort = "aasB";
	protected static final String smBIdShort = "b";
	protected static final CustomId aasBId = new CustomId("AssetAdministrationShell---51A6D8AF");
	protected static final CustomId smBId = new CustomId("fileTestB");
	protected static final String fileShortIdPath = "file";

	// Has to be individualized by each test inheriting from this suite
	// Default configuration is provided by buildEnpoints method
	protected static String aasEndpoint;
	protected static String smEndpoint;
	protected static String aasAEndpoint;
	protected static String smAEndpoint;
	protected static String aasBEndpoint;
	protected static String smBEndpoint;
	protected static String rootEndpoint;

	private ConnectedAssetAdministrationShellManager manager;

	// create a REST client
	private Client client = new JerseyClientBuilder().build();

	/**
	 * Before each test, a dummy registry is created and an AAS is added in the
	 * registry
	 */
	@Before
	public void setUp() {
		// Create a dummy registry to test integration of XML AAS
		aasRegistry = new InMemoryRegistry();
		AASDescriptor descriptor = new AASDescriptor(aasShortId, aasId, aasEndpoint);
		descriptor.addSubmodelDescriptor(new SubmodelDescriptor(smIdShort, smId, smEndpoint));
		aasRegistry.register(descriptor);
		AASDescriptor descriptorA = new AASDescriptor(aasAIdShort, aasAId, aasAEndpoint);
		descriptorA.addSubmodelDescriptor(new SubmodelDescriptor(smAIdShort, smAId, smAEndpoint));
		aasRegistry.register(descriptorA);
		AASDescriptor descriptorB = new AASDescriptor(aasBIdShort, aasBId, aasBEndpoint);
		descriptorB.addSubmodelDescriptor(new SubmodelDescriptor(smBIdShort, smBId, smBEndpoint));
		aasRegistry.register(descriptorB);

		// Create a ConnectedAssetAdministrationShell using a
		// ConnectedAssetAdministrationShellManager
		IConnectorFactory connectorFactory = new HTTPConnectorFactory();
		manager = new ConnectedAssetAdministrationShellManager(aasRegistry, connectorFactory);
	}

	@Test
	public void testGetSingleAAS() throws Exception {
		ConnectedAssetAdministrationShell connectedAssetAdministrationShell = getConnectedAssetAdministrationShell();
		assertEquals(aasShortId, connectedAssetAdministrationShell.getIdShort());
	}

	@Test
	public void testGetSingleSubmodel() throws Exception {
		ISubmodel subModel = manager.retrieveSubmodel(aasId, smId);
		assertEquals(smIdShort, subModel.getIdShort());
	}

	@Test
	public void testGetSingleModule() throws Exception {
		final String FILE_ENDING = VABPathTools.buildPath(new String[] { "basyx-temp", "aasx0", "files", "aasx", "Nameplate", "marking_rcm.jpg" }, 0);
		checkFile(VABPathTools.concatenatePaths(rootEndpoint, FILE_ENDING));

		// Get the submdoel nameplate
		ISubmodel nameplate = manager.retrieveSubmodel(aasId, smId);
		// Get the submodel element collection marking_rcm
		ConnectedSubmodelElementCollection marking_rcm = (ConnectedSubmodelElementCollection) nameplate.getSubmodelElements().get("Marking_RCM");
		Collection<ISubmodelElement> values = marking_rcm.getValue();

		// navigate to the File element
		Iterator<ISubmodelElement> iter = values.iterator();
		while (iter.hasNext()) {
			ISubmodelElement element = iter.next();
			if (element instanceof ConnectedFile) {
				ConnectedFile connectedFile = (ConnectedFile) element;
				// get value of the file element

				String fileurl = connectedFile.getValue();
				assertTrue(fileurl.endsWith(FILE_ENDING));
			}
		}
	}
	
	@Test
	public void testCollidingFiles() throws Exception {
		final String FILE_ENDING_A = VABPathTools.buildPath(new String[] { "basyx-temp", "aasx1", "files", "aasx", "files", "text.txt" }, 0);
		final String FILE_ENDING_B = VABPathTools.buildPath(new String[] { "basyx-temp", "aasx2", "files", "aasx", "files", "text.txt" }, 0);

		checkFile(VABPathTools.concatenatePaths(rootEndpoint, FILE_ENDING_A));
		checkFile(VABPathTools.concatenatePaths(rootEndpoint, FILE_ENDING_B));

		ISubmodel smA = manager.retrieveSubmodel(aasAId, smAId);
		ISubmodel smB = manager.retrieveSubmodel(aasBId, smBId);

		String fileAValue = (String) smA.getSubmodelElement("file").getValue();
		String fileBValue = (String) smB.getSubmodelElement("file").getValue();

		assertTrue(fileAValue.endsWith(FILE_ENDING_A));
		assertTrue(fileBValue.endsWith(FILE_ENDING_B));
	}

	@Test
	public void testAllFiles() throws Exception {
		logger.info("Checking all files");
		ConnectedAssetAdministrationShell aas = getConnectedAssetAdministrationShell();
		logger.info("AAS idShort: " + aas.getIdShort());
		logger.info("AAS identifier: " + aas.getIdentification().getId());
		Map<String, ISubmodel> submodels = aas.getSubmodels();
		logger.info("# Submodels: " + submodels.size());
		for (ISubmodel sm : submodels.values()) {
			logger.info("Checking submodel: " + sm.getIdShort());
			checkElementCollectionFiles(sm.getSubmodelElements().values());
		}

	}

	protected static void buildEndpoints(BaSyxContextConfiguration contextConfig) {
		rootEndpoint = VABPathTools.stripSlashes(contextConfig.getUrl());

		aasEndpoint = VABPathTools.concatenatePaths(rootEndpoint, AASAggregatorProvider.PREFIX, aasId.getEncodedURN(), "aas");
		smEndpoint = VABPathTools.concatenatePaths(aasEndpoint, "submodels", smIdShort, "submodel");

		String encodedAasAId = VABPathTools.encodePathElement(aasAId.getId());
		aasAEndpoint = VABPathTools.concatenatePaths(rootEndpoint, AASAggregatorProvider.PREFIX, encodedAasAId, "aas");
		smAEndpoint = VABPathTools.concatenatePaths(aasAEndpoint, "submodels", smAIdShort, "submodel");

		String encodedAasBId = VABPathTools.encodePathElement(aasBId.getId());
		aasBEndpoint = VABPathTools.concatenatePaths(rootEndpoint, AASAggregatorProvider.PREFIX, encodedAasBId, "aas");
		smBEndpoint = VABPathTools.concatenatePaths(aasBEndpoint, "submodels", smBIdShort, "submodel");

		logger.info("AAS URL for servlet test: " + aasEndpoint);
	}

	private void checkElementCollectionFiles(Collection<ISubmodelElement> elements) {
		for (ISubmodelElement element : elements) {
			if (element instanceof IFile) {
				String fileUrl = ((IFile) element).getValue();
				checkFile(fileUrl);
			} else if (element instanceof ISubmodelElementCollection) {
				ISubmodelElementCollection col = (ISubmodelElementCollection) element;
				checkElementCollectionFiles(col.getSubmodelElements().values());
			}
		}
	}

	private void checkFile(String absolutePath) {
		// connect to the url of the aas
		WebTarget webTarget = client.target(absolutePath);
		logger.info("Checking file: " + absolutePath);
		Invocation.Builder invocationBuilder = webTarget.request();
		Response response = invocationBuilder.get();

		// validate the response
		assertEquals("Path check failed: " + absolutePath, 200, response.getStatus());
	}

	/**
	 * Gets the connected Asset Administration Shell
	 * 
	 * @return connected AAS
	 * @throws Exception
	 */
	private ConnectedAssetAdministrationShell getConnectedAssetAdministrationShell() throws Exception {
		return manager.retrieveAAS(aasId);
	}
}
