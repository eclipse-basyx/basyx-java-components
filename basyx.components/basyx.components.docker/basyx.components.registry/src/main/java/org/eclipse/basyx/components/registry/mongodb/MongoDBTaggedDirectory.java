package org.eclipse.basyx.components.registry.mongodb;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedSubmodelDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.map.MapTaggedDirectory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * A tagged Directory with MongoDB backend
 * 
 * @author zhangzai
 *
 */
public class MongoDBTaggedDirectory extends MapTaggedDirectory {

	public MongoDBTaggedDirectory(BaSyxMongoDBConfiguration mongoDBConfig, Map<String, Set<TaggedAASDescriptor>> tagMap) {
		super(new MongoDBRegistryHandler(mongoDBConfig), tagMap);
		this.tagMap = tagMap;
		initializeTagMap();
	}

	private void initializeTagMap() {
		List<AASDescriptor> aasDescriptors = super.handler.getAll();
		for (AASDescriptor aasDes : aasDescriptors) {
			if (!(aasDes instanceof TaggedAASDescriptor))
				continue;
			super.addTags((TaggedAASDescriptor) aasDes);
		}
	}

	@Override
	public void registerSubmodel(IIdentifier aas, TaggedSubmodelDescriptor descriptor) {
		super.register(aas, descriptor);
		addSubmodelTags(descriptor);
		updateTagMap(aas, descriptor);
	}

	private void updateTagMap(IIdentifier aas, TaggedSubmodelDescriptor descriptor) {
		tagMap.values().forEach(tagSet -> {
			tagSet.forEach(tagDesc -> {
				if (descriptorEqualsToGivenAASId(aas, tagDesc)) {
					if (!containsSubmodelDescriptor(descriptor, tagDesc))
						tagDesc.addSubmodelDescriptor(descriptor);
				}
			});
		});
	}

	private boolean containsSubmodelDescriptor(TaggedSubmodelDescriptor descriptor, TaggedAASDescriptor tagDesc) {
		return tagDesc.getSubmodelDescriptorFromIdShort(descriptor.getIdShort()) != null;
	}

	private boolean descriptorEqualsToGivenAASId(IIdentifier aas, TaggedAASDescriptor tagDesc) {
		return tagDesc.getIdentifier().getId().equals(aas.getId());
	}
}
