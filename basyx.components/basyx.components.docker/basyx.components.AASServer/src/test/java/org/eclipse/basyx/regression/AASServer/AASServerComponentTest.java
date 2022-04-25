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
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Test;

/**
 * Tests if AASServerComponent correctly deregisteres automatically registered
 * AASs/SMs
 * 
 * @author conradi
 *
 */
public class AASServerComponentTest {
	private static AASServerComponent component;
	private static InMemoryRegistry registry;

	private static final String XML_SOURCE = "xml/aas.xml";

	private static final String MULTIPLE_DIFFERENT_AAS_SERIALIZATION = "[\"json/aas.json\",\"aasx/01_Festo.aasx\",\"xml/aas.xml\"]";
	private static final String SINGLE_JSON_AAS_SERIALIZATION = "[\"json/aas.json\"]";
	private static final String EMPTY_JSON_ARRAY = "[]";

	public static void setUp(String source) {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration(8080, "");
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration(AASServerBackend.INMEMORY, source);

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

		stopAASServerComponent();
	}

	@Test
	public void checkSingleSerializedAasJsonSource() {
		setUp(SINGLE_JSON_AAS_SERIALIZATION);

		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(1, aasDescriptors.size());

		stopAASServerComponent();
	}

	@Test
	public void checkBehaviorWithEmptyJsonArray() {
		setUp(EMPTY_JSON_ARRAY);

		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		System.out.println(aasDescriptors.size());
		assertEquals(0, aasDescriptors.size());

		stopAASServerComponent();
	}

	/**
	 * Tests if AASServerComponent deregisters all AASs/SMs that it registered
	 * automatically on startup
	 */
	@Test
	public void testServerCleanup() {
		setUp(XML_SOURCE);

		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(2, aasDescriptors.size());

		stopAASServerComponent();

		// Try to lookup all previously registered AASs
		for (AASDescriptor aasDescriptor : aasDescriptors) {
			try {
				registry.lookupAAS(aasDescriptor.getIdentifier());
				fail();
			} catch (ResourceNotFoundException e) {
			}

			// Try to lookup all previously registered SMs
			for (SubmodelDescriptor smDescriptor : aasDescriptor.getSubmodelDescriptors()) {
				try {
					registry.lookupSubmodel(aasDescriptor.getIdentifier(), smDescriptor.getIdentifier());
					fail();
				} catch (ResourceNotFoundException e) {
				}
			}
		}
	}

	public void stopAASServerComponent() {
		component.stopComponent();
	}
}