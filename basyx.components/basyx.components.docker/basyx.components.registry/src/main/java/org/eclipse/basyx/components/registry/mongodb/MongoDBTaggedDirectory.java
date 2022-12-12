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
package org.eclipse.basyx.components.registry.mongodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelDescriptor;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedSubmodelDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.map.MapTaggedDirectory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * A tagged Directory with MongoDB backend
 * 
 * @author zhangzai, jungjan
 *
 */
public class MongoDBTaggedDirectory extends MapTaggedDirectory {

	public MongoDBTaggedDirectory(BaSyxMongoDBConfiguration mongoDBConfig, Map<String, Set<TaggedAASDescriptor>> tagMap) {
		super(new MongoDBRegistryHandler(mongoDBConfig), tagMap);
		initializeTagMap();
	}
	
	public MongoDBTaggedDirectory(BaSyxMongoDBConfiguration mongoDBConfig) {
		this(mongoDBConfig, new HashMap<>());
	}

	private void initializeTagMap() {
		List<AASDescriptor> shellDescriptors = super.handler.getAll();	
		shellDescriptors.stream().filter(this::isTaggedDescriptor).map(TaggedAASDescriptor::createAsFacade).forEach(super::addTags);
	}
	
	private boolean isTaggedDescriptor(ModelDescriptor descriptor) {
		return descriptor.get(TaggedAASDescriptor.TAGS) != null;
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
