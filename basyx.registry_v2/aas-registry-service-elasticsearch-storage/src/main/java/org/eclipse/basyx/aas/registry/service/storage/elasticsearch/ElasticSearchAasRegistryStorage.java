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
package org.eclipse.basyx.aas.registry.service.storage.elasticsearch;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorSearchRequest;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorSearchResponse;
import org.eclipse.basyx.aas.registry.model.SubmodelDescriptor;
import org.eclipse.basyx.aas.registry.service.storage.AasDescriptorNotFoundException;
import org.eclipse.basyx.aas.registry.service.storage.AasRegistryStorage;
import org.eclipse.basyx.aas.registry.service.storage.SubmodelNotFoundException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.UpdateResponse.Result;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ElasticSearchAasRegistryStorage implements AasRegistryStorage {

	private static final int MAX_RESULTS = 30;

	private static final String SUBMODEL_ID_IS_NULL = "Submodel id is null!";

	@Autowired
	private RestHighLevelClient client;

	@Autowired
	private AasDescriptorRepository aasDescriptorRepository;

	@Autowired
	private AtomicElasticSearchRepoAccess atomicRepoAccess;

	@Autowired
	private ElasticsearchOperations ops;

	@Override
	public boolean containsSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId) {
		AssetAdministrationShellDescriptor descriptor = ops.get(aasDescriptorId, AssetAdministrationShellDescriptor.class);
		if (descriptor == null) {
			return false;
		}
		List<SubmodelDescriptor> submodels = descriptor.getSubmodelDescriptors();
		if (submodels == null) {
			return false;
		}
		return submodels.stream().map(SubmodelDescriptor::getIdentification).anyMatch(Predicate.isEqual(submodelId));
	}

	@Override
	public boolean removeAasDescriptor(@NonNull String aasDescriptorId) {
		try {
			// can't use the repository object here because we need a return value
			IndexCoordinates coordinates = ops.getIndexCoordinatesFor(AssetAdministrationShellDescriptor.class);
			DeleteRequest request = new DeleteRequest(coordinates.getIndexName(), aasDescriptorId);
			request.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
			DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
			return response.getResult() == DocWriteResponse.Result.DELETED;
		} catch (IOException e) {
			log.catching(e);
			return false;
		}
	}

	@Override
	public boolean removeSubmodel(@NonNull String aasDescrId, @NonNull String submodelId) {
		Result result = atomicRepoAccess.removeAssetAdministrationSubmodel(aasDescrId, submodelId);
		return result == Result.UPDATED;
	}

	@Override
	public Set<String> clear() {
		List<String> descriptors;
		Set<String> allDeleted = new LinkedHashSet<>();
		do {
			descriptors = atomicRepoAccess.getAllIds(MAX_RESULTS);
			// we can not delete all descriptors at once
			// another client could have deleted them while after read and before delete
			// and we do not want to have multiple unregister events fired
			for (String eachId : descriptors) {
				if (removeAasDescriptor(eachId)) {
					allDeleted.add(eachId);
				}
			}
		} while (descriptors.size() == MAX_RESULTS);
		return Collections.unmodifiableSet(allDeleted);
	}

	@Override
	public List<AssetAdministrationShellDescriptor> getAllAasDesriptors() {
		Iterable<AssetAdministrationShellDescriptor> iterable = aasDescriptorRepository.findAll();
		return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toUnmodifiableList());
	}

	@Override
	public AssetAdministrationShellDescriptor getAasDescriptor(@NonNull String aasId) {
		AssetAdministrationShellDescriptor descriptor = ops.get(aasId, AssetAdministrationShellDescriptor.class);
		if (descriptor == null) {
			throw new AasDescriptorNotFoundException(aasId);
		}
		return descriptor;
	}

	@Override
	public void addOrReplaceAasDescriptor(@NonNull AssetAdministrationShellDescriptor descriptor) {
		aasDescriptorRepository.save(descriptor);
	}

	@Override
	public List<SubmodelDescriptor> getAllSubmodels(@NonNull String aasDescriptorId) {
		AssetAdministrationShellDescriptor descriptor = getAasDescriptor(aasDescriptorId);
		List<SubmodelDescriptor> descriptorList = descriptor.getSubmodelDescriptors();
		if (descriptorList == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(descriptorList);
	}

	@Override
	public SubmodelDescriptor getSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId) {
		AssetAdministrationShellDescriptor descriptor = getAasDescriptor(aasDescriptorId);
		List<SubmodelDescriptor> submodels = descriptor.getSubmodelDescriptors();
		if (submodels != null) {
			for (SubmodelDescriptor eachSubmodel : submodels) {
				if (Objects.equals(eachSubmodel.getIdentification(), submodelId)) {
					return eachSubmodel;
				}
			}
		}
		throw new SubmodelNotFoundException(aasDescriptorId, submodelId);
	}

	@Override
	public void appendOrReplaceSubmodel(@NonNull String aasDescriptorId, SubmodelDescriptor submodel) {
		Objects.requireNonNull(submodel.getIdentification(), SUBMODEL_ID_IS_NULL);
		Result result = atomicRepoAccess.storeAssetAdministrationSubmodel(aasDescriptorId, submodel);
		if (result == Result.NOT_FOUND) {
			throw new AasDescriptorNotFoundException(aasDescriptorId);
		}
	}

	@Override
	public ShellDescriptorSearchResponse searchAasDescriptors(ShellDescriptorSearchRequest request) {
		NativeSearchQuery nQuery = SearchRequestMapper.mapSearchQuery(request);
		SearchHits<AssetAdministrationShellDescriptor> hits = ops.search(nQuery, AssetAdministrationShellDescriptor.class);
		SearchResultMapper cutter = new SearchResultMapper();
		List<AssetAdministrationShellDescriptor> transformed = cutter.shrinkHits(hits);
		return new ShellDescriptorSearchResponse().total(hits.getTotalHits()).hits(transformed);
	}

}