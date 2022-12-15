/*******************************************************************************
 * Copyright (C) 2021-2022 the Eclipse BaSyx Authors
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.registry.mongodb.MongoDBTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedSubmodelDescriptor;
import org.eclipse.basyx.testsuite.regression.extensions.aas.directory.tagged.TestTaggedDirectorySuite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class TestMongoDBTaggedDirectory extends TestTaggedDirectorySuite {
	private static BaSyxMongoDBConfiguration mongoDBConfig = new BaSyxMongoDBConfiguration();
	private static MongoOperations mongoOps;
	private static MongoClient client;

	@Override
	protected IAASTaggedDirectory getDirectory() {
		return new MongoDBTaggedDirectory(new BaSyxMongoDBConfiguration());
	}

	@Override
	protected IAASRegistry getRegistryService() {
		return getDirectory();
	}

	@BeforeClass
	public static void cleanupMongoDB() {
		client = MongoClients.create(mongoDBConfig.getConnectionUrl());
		mongoOps = new MongoTemplate(client, mongoDBConfig.getDatabase());
		mongoOps.dropCollection(mongoDBConfig.getRegistryCollection());
	}

	/**
	 * Test tags are stored persistently in MongoDB
	 */
	@Test
	public void testTagPersistency() {
		int expectedNumberOfTags = 9;
		
		super.init();
		
		Set<TaggedAASDescriptor> deviceDescriptors = getDirectory().lookupTag(DEVICE);
		List<TaggedAASDescriptor> taggedDescriptors = getTaggedAASDescriptorWithNewMongoDBConnection();
		Set<String> allTags = extractAllTagsFromAASDescriptors(taggedDescriptors);	

		assertFalse(deviceDescriptors.isEmpty());
		assertTrue(allTags.contains(DEVICE));
		assertEquals(expectedNumberOfTags, allTags.size());
	}

	@Test
	public void testUpdateTag() {
		TaggedAASDescriptor desc5 = createTaggedAAS();
		TaggedSubmodelDescriptor taggedSmDesc2 = createTaggedSubmodel();

		TaggedSubmodelDescriptor submodelDesc = directory.lookupSubmodelTag(INTEGRATOR).iterator().next();
		TaggedAASDescriptor aasDesc = directory.lookupTag(INTERNAL).iterator().next();
		assertEquals(taggedSmIdShort2, submodelDesc.getIdShort());
		assertEquals(taggedAasIdShort5, aasDesc.getIdShort());

		updateSubmodelTag(taggedSmDesc2);
		updateAASTag(desc5);

		aasDesc = directory.lookupTag(MACHINE).iterator().next();
		assertEquals(1, aasDesc.getTags().size());
		assertEquals(taggedAasIdShort5, aasDesc.getIdShort());
		submodelDesc = directory.lookupSubmodelTag(KEY).iterator().next();
		assertEquals(taggedSmIdShort2, submodelDesc.getIdShort());
		assertEquals(1, submodelDesc.getTags().size());
	}

	private void updateAASTag(TaggedAASDescriptor desc5) {
		desc5.addTag(MACHINE);
		desc5.getTags().remove(INTERNAL);
		directory.register(desc5);
	}

	private void updateSubmodelTag(TaggedSubmodelDescriptor taggedSmDesc2) {
		taggedSmDesc2.addTag(KEY);
		taggedSmDesc2.getTags().remove(INTEGRATOR);
		directory.registerSubmodel(taggedAAS5, taggedSmDesc2);
	}

	private TaggedSubmodelDescriptor createTaggedSubmodel() {
		TaggedSubmodelDescriptor taggedSmDesc2 = new TaggedSubmodelDescriptor(taggedSmIdShort2, taggedSmId2, taggedSmEndpoint2);
		taggedSmDesc2.addTag(INTEGRATOR);
		directory.registerSubmodel(taggedAAS5, taggedSmDesc2);
		return taggedSmDesc2;
	}

	private TaggedAASDescriptor createTaggedAAS() {
		TaggedAASDescriptor desc5 = new TaggedAASDescriptor(taggedAasIdShort5, taggedAAS5, taggedAasEndpoint5);
		desc5.addTag(INTERNAL);
		directory.register(desc5);
		return desc5;
	}

	private Set<String> extractAllTagsFromAASDescriptors(List<TaggedAASDescriptor> result) {
		Set<String> allTags = new HashSet<>();
		for (TaggedAASDescriptor aasDesc : result) {
			Collection<String> tags = aasDesc.getTags();
			if (tags == null)
				continue;

			tags.forEach(tag -> {
				if (!allTags.contains(tag))
					allTags.add(tag);
			});
		}
		return allTags;
	}

	private List<TaggedAASDescriptor> getTaggedAASDescriptorWithNewMongoDBConnection() {
		return mongoOps.findAll(TaggedAASDescriptor.class, mongoDBConfig.getRegistryCollection());
	}

}
