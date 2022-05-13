/*******************************************************************************
 * Copyright (C) 2022 DFKI GmbH
 * Author: Gerhard Sonnenberg (gerhard.sonnenberg@dfki.de)
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
package org.eclipse.basyx.aas.registry.service.storage.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorSearchRequest;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorSearchResponse;
import org.eclipse.basyx.aas.registry.model.SubmodelDescriptor;
import org.eclipse.basyx.aas.registry.service.storage.AasDescriptorNotFoundException;
import org.eclipse.basyx.aas.registry.service.storage.AasRegistryStorage;
import org.eclipse.basyx.aas.registry.service.storage.SubmodelNotFoundException;

import lombok.NonNull;

public class InMemoryAasRegistryStorage implements AasRegistryStorage {

	private final Map<String, AssetAdministrationShellDescriptor> aasDescriptorLookupMap = new LinkedHashMap<>();
	private final Map<String, Map<String, SubmodelDescriptor>> submodelLookupMap = new HashMap<>();

	@Override
	public boolean containsSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId) {
		Map<String, SubmodelDescriptor> submodels = submodelLookupMap.get(aasDescriptorId);
		return submodels != null && submodels.containsKey(submodelId);
	}

	@Override
	public List<AssetAdministrationShellDescriptor> getAllAasDesriptors() {
		return new ArrayList<>(aasDescriptorLookupMap.values());
	}

	@Override
	public AssetAdministrationShellDescriptor getAasDescriptor(@NonNull String aasId) {
		AssetAdministrationShellDescriptor descriptor = aasDescriptorLookupMap.get(aasId);
		if (descriptor == null) {
			throw new AasDescriptorNotFoundException(aasId);
		}
		return descriptor;
	}

	@Override
	public void addOrReplaceAasDescriptor(@NonNull AssetAdministrationShellDescriptor descriptor) {
		Map<String, SubmodelDescriptor> submodelMap = toSubmodelLookupMap(descriptor.getSubmodelDescriptors());
		String aasDescrId = descriptor.getIdentification();
		aasDescriptorLookupMap.put(aasDescrId, descriptor);
		submodelLookupMap.put(aasDescrId, submodelMap);
	}

	private LinkedHashMap<String, SubmodelDescriptor> toSubmodelLookupMap(List<SubmodelDescriptor> submodelDescriptors) {
		return Optional.ofNullable(submodelDescriptors).orElseGet(List::of).stream().collect(Collectors.toMap(SubmodelDescriptor::getIdentification, Function.identity(), this::mergeSubmodels, LinkedHashMap::new));
	}

	private SubmodelDescriptor mergeSubmodels(SubmodelDescriptor descr1, SubmodelDescriptor descr2) {
		throw new DuplicateSubmodelIds(descr1.getIdentification());
	}

	@Override
	public boolean removeAasDescriptor(String aasDescriptorId) {
		return aasDescriptorLookupMap.remove(aasDescriptorId) != null && submodelLookupMap.remove(aasDescriptorId) != null;
	}

	@Override
	public List<SubmodelDescriptor> getAllSubmodels(@NonNull String aasDescriptorId) {
		AssetAdministrationShellDescriptor descriptor = getAasDescriptor(aasDescriptorId);
		List<SubmodelDescriptor> submodels = descriptor.getSubmodelDescriptors();
		if (submodels == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(submodels);
	}

	@Override
	public SubmodelDescriptor getSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId) {
		Map<String, SubmodelDescriptor> descriptorModels = submodelLookupMap.get(aasDescriptorId);
		if (descriptorModels == null) {
			throw new AasDescriptorNotFoundException(aasDescriptorId);
		}
		SubmodelDescriptor submodel = descriptorModels.get(submodelId);
		if (submodel == null) {
			throw new SubmodelNotFoundException(aasDescriptorId, submodelId);
		}
		return submodel;
	}

	@Override
	public void appendOrReplaceSubmodel(@NonNull String aasDescriptorId, @NonNull SubmodelDescriptor submodel) {
		AssetAdministrationShellDescriptor aasDescriptor = getAasDescriptor(aasDescriptorId);
		replaceOrAppendSubmodel(aasDescriptorId, aasDescriptor, submodel);
	}

	private void replaceOrAppendSubmodel(String aasDescriptorId, AssetAdministrationShellDescriptor aasDescriptor, SubmodelDescriptor submodel) {
		String submodelId = submodel.getIdentification();
		if (containsSubmodel(aasDescriptorId, submodelId)) { // replace
			replaceSubmodelInAasDescriptor(aasDescriptor, submodel, submodelId);
		} else { // just append
			aasDescriptor.addSubmodelDescriptorsItem(submodel);
		}
		// update map
		submodelLookupMap.get(aasDescriptorId).put(submodelId, submodel);
	}

	private void replaceSubmodelInAasDescriptor(AssetAdministrationShellDescriptor aasDescriptor, SubmodelDescriptor submodel, String submodelId) {
		ListIterator<SubmodelDescriptor> submodels = aasDescriptor.getSubmodelDescriptors().listIterator();
		while (submodels.hasNext()) {
			SubmodelDescriptor eachItem = submodels.next();
			if (Objects.equals(eachItem.getIdentification(), submodelId)) {
				submodels.set(submodel);
				break;
			}
		}
	}

	@Override
	public boolean removeSubmodel(String aasDescrId, String submodelId) {
		AssetAdministrationShellDescriptor descriptor = aasDescriptorLookupMap.get(aasDescrId);
		if (descriptor == null) {
			return false;
		}
		return removeStoredSubmodel(aasDescrId, descriptor, submodelId);
	}

	private boolean removeStoredSubmodel(String aasDescriptorId, AssetAdministrationShellDescriptor aasDescriptor, String submodelId) {
		if (submodelLookupMap.get(aasDescriptorId).remove(submodelId) == null) {
			return false;
		} else { // found submodel so also remove it from the aasDescriptor object
			removeSubmodelFromDescriptor(aasDescriptor, submodelId);
			return true;
		}
	}

	private void removeSubmodelFromDescriptor(AssetAdministrationShellDescriptor aasDescriptor, String submodelId) {
		Iterator<SubmodelDescriptor> submodelIter = aasDescriptor.getSubmodelDescriptors().iterator();
		while (submodelIter.hasNext()) {
			SubmodelDescriptor eachItem = submodelIter.next();
			if (Objects.equals(eachItem.getIdentification(), submodelId)) {
				submodelIter.remove();
				return; // we assume that there is always just one submodel with this id
			}
		}
	}

	@Override
	public Set<String> clear() {
		Set<String> keys = new HashSet<>(aasDescriptorLookupMap.keySet());
		aasDescriptorLookupMap.clear();
		submodelLookupMap.clear();
		return keys;
	}

	@Override
	public ShellDescriptorSearchResponse searchAasDescriptors(ShellDescriptorSearchRequest request) {
		Collection<AssetAdministrationShellDescriptor> descriptors = getAllAasDesriptors();
		InMemoryStorageSearch search = new InMemoryStorageSearch(descriptors);
		return search.performSearch(request);
	}

	private static final class DuplicateSubmodelIds extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public DuplicateSubmodelIds(String id) {
			super("The submodel id '" + id + "' is stored mulitple times in the descriptor");
		}
	}
}