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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.aas.registry.model.SubmodelDescriptor;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

public class SearchResultMapper {

	public List<AssetAdministrationShellDescriptor> shrinkHits(SearchHits<AssetAdministrationShellDescriptor> hits) {
		List<SearchHit<AssetAdministrationShellDescriptor>> hitList = hits.getSearchHits();
		List<AssetAdministrationShellDescriptor> descriptors = new ArrayList<>(hitList.size());
		for (SearchHit<AssetAdministrationShellDescriptor> eachHit : hitList) {
			AssetAdministrationShellDescriptor descriptor = eachHit.getContent();
			Map<String, SearchHits<?>> innerHits = eachHit.getInnerHits();
			if (!innerHits.isEmpty()) {
				// we just had one innerHit query
				Entry<String, SearchHits<?>> innerEntry = innerHits.entrySet().iterator().next();
				shrinkDescriptor(descriptor, innerEntry.getValue());
			}
			descriptors.add(descriptor);
		}
		return descriptors;
	}

	private void shrinkDescriptor(AssetAdministrationShellDescriptor descriptor, SearchHits<?> hits) {
		List<SubmodelDescriptor> matchingSubModels = new LinkedList<>();
		for (SearchHit<?> eachHit : hits.getSearchHits()) {
			SubmodelDescriptor content = (SubmodelDescriptor) eachHit.getContent();
			matchingSubModels.add(content);
		}
		descriptor.setSubmodelDescriptors(matchingSubModels);
	}

}
