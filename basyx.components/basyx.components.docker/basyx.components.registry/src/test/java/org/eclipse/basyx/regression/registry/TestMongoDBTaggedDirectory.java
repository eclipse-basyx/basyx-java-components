package org.eclipse.basyx.regression.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
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
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class TestMongoDBTaggedDirectory extends TestTaggedDirectorySuite {

	@Override
	protected IAASTaggedDirectory getDirectory() {
		return new MongoDBTaggedDirectory(new BaSyxMongoDBConfiguration(), new HashMap<>());
	}

	@Override
	protected IAASRegistry getRegistryService() {
		return getDirectory();
	}

	/**
	 * Test tags are store persistenly in MongoDB
	 */
	@Test
	public void testTagPersistency() {
		super.init();

		List<TaggedAASDescriptor> result = getTaggedAASDescriptorWithNewMongoDBConnection();

		Set<String> allTags = extractAllTagsFromAASDescriptors(result);

		assertEquals(9, allTags.size());
		assertTrue(allTags.contains(DEVICE));
		assertTrue(allTags.contains(SUPPLIER_A));
		assertTrue(allTags.contains(SUPPLIER_B));
		assertTrue(allTags.contains(MILL));
		assertTrue(allTags.contains(PACKAGER));
		assertTrue(allTags.contains(BASYS_READY));
		assertTrue(allTags.contains(INTERNAL));
	}

	@Test
	public void testUpdateTag() {
		TaggedAASDescriptor desc5 = createTaggedAAS();
		TaggedSubmodelDescriptor taggedSmDesc2 = createTaggedSubmodel();

		TaggedSubmodelDescriptor submodelDesc = directory.lookupSubmodelTag(INTEGRATOR).iterator().next();
		TaggedAASDescriptor aasDesc = directory.lookupTag(INTERNAL).iterator().next();
		assertEquals(taggedSmIdShort2, submodelDesc.getIdShort());
		assertEquals(taggedAasIdShort5, aasDesc.getIdShort());

		// Update tags of submodel and aas
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
		BaSyxMongoDBConfiguration mongoDBConfig = new BaSyxMongoDBConfiguration();
		MongoClient client = MongoClients.create(mongoDBConfig.getConnectionUrl());
		MongoOperations mongoOps = new MongoTemplate(client, mongoDBConfig.getDatabase());
		String collection = mongoDBConfig.getRegistryCollection();
		return mongoOps.findAll(TaggedAASDescriptor.class, collection);
	}

}
