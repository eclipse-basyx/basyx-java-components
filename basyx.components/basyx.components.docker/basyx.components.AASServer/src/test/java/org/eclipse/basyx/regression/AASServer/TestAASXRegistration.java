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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.executable.AASServerExecutable;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Test the AASComponent registration process
 * 
 * @author espen
 *
 */
public class TestAASXRegistration {
	protected static final String AAS_SHORTID = "Festo_3S7PM0CP4BD";
	protected static final ModelUrn AAS_ID = new ModelUrn("smart.festo.com/demo/aas/1/1/454576463545648365874");
	protected static final ModelUrn SM_ID = new ModelUrn("www.company.com/ids/sm/4343_5072_7091_3242");
	protected static final String SM_SHORTID = "Nameplate";
	protected static final String AASXPATH = "aasx/01_Festo.aasx";

	protected InMemoryRegistry registry = new InMemoryRegistry();
	protected String deployedEndpoint = "https://www.eclipse.org/basyx/test";
	protected AASServerComponent component;

	@Before
	public void setUp() throws ParserConfigurationException, SAXException, IOException, URISyntaxException, ServletException {
		BaSyxContextConfiguration contextConfig = createContextConfig();
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.INMEMORY, AASXPATH, "", deployedEndpoint);
		startAASServerComponent(contextConfig, aasConfig);
	}

	private void startAASServerComponent(BaSyxContextConfiguration contextConfig, BaSyxAASServerConfiguration aasConfig) {
		component = new AASServerComponent(contextConfig, aasConfig);
		component.setRegistry(registry);
		component.startComponent();
	}

	private BaSyxContextConfiguration createContextConfig() throws URISyntaxException {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		// Load the additional file path relative to the executed jar file
		String rootPath = new File(AASServerExecutable.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
		contextConfig.setDocBasePath(rootPath);
		return contextConfig;
	}

	@Test
	public void testAASHasBeenRegistered() {
		AASDescriptor aasDescriptor = registry.lookupAAS(AAS_ID);
		String descriptorAASShortId = aasDescriptor.getIdShort();
		assertEquals(AAS_SHORTID, descriptorAASShortId);
	}

	@Test
	public void testAASEndpointCorrect() {
		AASDescriptor aasDescriptor = registry.lookupAAS(AAS_ID);
		String descAASEndpoint = aasDescriptor.getFirstEndpoint();

		String expectedEndpoint = VABPathTools.concatenatePaths(deployedEndpoint, AASAggregatorProvider.PREFIX, AAS_ID.getEncodedURN(), MultiSubmodelProvider.AAS);
		System.out.println(expectedEndpoint);
		System.out.println(descAASEndpoint);
		assertEquals(expectedEndpoint, descAASEndpoint);
	}

	@After
	public void tearDown() {
		component.stopComponent();
	}
}
