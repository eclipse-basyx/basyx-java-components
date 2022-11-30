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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.components.registry.configuration.RegistryEventBackend;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.proxy.TaggedDirectoryProxy;
import org.junit.Test;

public class TestTaggedDirectoryMqtt extends TestMqttRegistryBackend {
	@Override
	public RegistryComponent createRegistryComponent() {
		BaSyxRegistryConfiguration registryConfig = new BaSyxRegistryConfiguration();
		registryConfig.enableTaggedDirectory();
		registryConfig.setRegistryEvents(RegistryEventBackend.MQTT);

		return new RegistryComponent(new BaSyxContextConfiguration(), registryConfig);
	}

	@Test
	public void testEventsWithTaggedDirectory() {
		String testTag = "TestTag";
		TaggedDirectoryProxy taggedDirectoryProxy = new TaggedDirectoryProxy(registryUrl);
		registerTaggedDescriptor(taggedDirectoryProxy, testTag);
		assertFindingTaggedDescriptor(taggedDirectoryProxy, testTag);
	}

	private void registerTaggedDescriptor(TaggedDirectoryProxy taggedDirectoryProxy, String testTag) {
		TaggedAASDescriptor taggedDescriptor = new TaggedAASDescriptor("TaggedAAS", new CustomId("TaggedAASId"), "");
		taggedDescriptor.addTag(testTag);
		taggedDirectoryProxy.register(taggedDescriptor);
	}

	private void assertFindingTaggedDescriptor(TaggedDirectoryProxy taggedDirectoryProxy, String testTag) {
		Set<TaggedAASDescriptor> foundDescriptors = taggedDirectoryProxy.lookupTag("TestTag");
		assertEquals(1, foundDescriptors.size());
		TaggedAASDescriptor foundDescriptor = foundDescriptors.iterator().next();
		assertTrue(foundDescriptor.getTags().contains(testTag));
	}
}
