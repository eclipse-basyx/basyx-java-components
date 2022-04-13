/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.regression.AASServer.configuration;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.junit.After;
import org.junit.Test;

/**
 * Tests parsing of AAS properties files containing AAS source as JSON Array
 * 
 * @author danish
 *
 */
public class TestBaSyxAASServerConfigurationPropertyFileParsing {
	private static final String MULTIPLE_DIFFERENT_AAS_SERIALIZATION = "aas_multiple_different_source.properties";
	private static final String SINGLE_JSON_AAS_SERIALIZATION = "aas_single_json_source.properties";
	private static final String SINGLE_AAS_SERIALIZATION = "aas_single_source.properties";

	private static AASServerComponent component;
	private static InMemoryRegistry registry;

	private static void setUp(String resourcePath) {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration(8080, "");
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration();
		aasConfig.loadFromResource(resourcePath);

		createAndStartAASServerComponent(contextConfig, aasConfig);
	}

	private static void createAndStartAASServerComponent(BaSyxContextConfiguration contextConfig, BaSyxAASServerConfiguration aasConfig) {
		component = new AASServerComponent(contextConfig, aasConfig);
		registry = new InMemoryRegistry();
		component.setRegistry(registry);
		component.startComponent();
	}

	@Test
	public void checkMultipleSerializedAasSourceOfDifferentTypes() {
		setUp(MULTIPLE_DIFFERENT_AAS_SERIALIZATION);

		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(4, aasDescriptors.size());
	}

	@Test
	public void checkSingleSerializedAasJsonSource() {
		setUp(SINGLE_JSON_AAS_SERIALIZATION);

		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(1, aasDescriptors.size());
	}

	@Test
	public void checkAasSerializedSouceDefinedWithoutJsonArray() {
		setUp(SINGLE_AAS_SERIALIZATION);

		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(2, aasDescriptors.size());
	}

	@After
	public void stopAASServerComponent() {
		component.stopComponent();
	}
}
