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

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.lucene.search.join.ScoreMode;
import org.eclipse.basyx.aas.registry.client.api.AasRegistryPaths;
import org.eclipse.basyx.aas.registry.model.Page;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorQuery;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorSearchRequest;
import org.eclipse.basyx.aas.registry.model.SortDirection;
import org.eclipse.basyx.aas.registry.model.Sorting;
import org.eclipse.basyx.aas.registry.model.SortingPath;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SearchRequestMapper {

	private static final int MAX_INNER_HITS = 100;

	public static NativeSearchQuery mapSearchQuery(ShellDescriptorSearchRequest request) {
		NativeSearchQuery nQuery = createSearchQuery(request.getQuery());
		Sort sort = getSort(request);
		if (sort != null) {
			nQuery.addSort(sort);
		}
		applyPageable(request, nQuery, sort);
		return nQuery;
	}

	private static NativeSearchQuery createSearchQuery(ShellDescriptorQuery query) {
		if (query == null) {
			return createMatchAllQuery();
		} else if (QueryTypeEnum.REGEX.equals(query.getQueryType())) {
			return createRegExp(query.getPath(), query.getValue());
		} else {
			return createMatchByFilter(query.getPath(), query.getValue());
		}
	}

	private static NativeSearchQuery createMatchAllQuery() {
		MatchAllQueryBuilder matchAllBuilder = QueryBuilders.matchAllQuery();
		return new NativeSearchQuery(matchAllBuilder);
	}

	private static NativeSearchQuery createRegExp(@NotNull String path, @NotNull String value) {
		BoolQueryBuilder bqBuilder = QueryBuilders.boolQuery();
		RegexpQueryBuilder regexpBuilder = QueryBuilders.regexpQuery(path, value);

		bqBuilder = bqBuilder.must(regexpBuilder);
		if (doBuildSubmodelNestedQuery(path)) {
			NestedQueryBuilder nestedBuilder = QueryBuilders.nestedQuery(AasRegistryPaths.submodelDescriptors().toString(), bqBuilder, ScoreMode.None);
			InnerHitBuilder innerHitBuilder = new InnerHitBuilder();
			innerHitBuilder.setSize(MAX_INNER_HITS);
			nestedBuilder.innerHit(innerHitBuilder);
			return new NativeSearchQuery(nestedBuilder);
		} else {
			return new NativeSearchQuery(bqBuilder);
		}
	}

	private static NativeSearchQuery createMatchByFilter(@NotNull String path, @NotNull String value) {
		BoolQueryBuilder bqBuilder = QueryBuilders.boolQuery();
		MatchQueryBuilder matchBuilder = QueryBuilders.matchQuery(path, value);
		matchBuilder.operator(Operator.AND);
		bqBuilder.must(matchBuilder);
		if (doBuildSubmodelNestedQuery(path)) {
			NestedQueryBuilder nestedBuilder = QueryBuilders.nestedQuery(AasRegistryPaths.submodelDescriptors().toString(), bqBuilder, ScoreMode.None);
			InnerHitBuilder innerHitBuilder = new InnerHitBuilder();
			innerHitBuilder.setSize(MAX_INNER_HITS);
			nestedBuilder.innerHit(innerHitBuilder);
			return new NativeSearchQuery(nestedBuilder);
		} else {
			return new NativeSearchQuery(bqBuilder);
		}
	}

	private static Sort getSort(ShellDescriptorSearchRequest query) {
		Sorting sorting = query.getSortBy();
		if (sorting != null) {
			SortDirection sortDirection = sorting.getDirection();
			Direction direction;
			if (sortDirection == null) {
				direction = Direction.ASC;
			} else {
				direction = Direction.fromString(sortDirection.name());
			}
			List<SortingPath> sPaths = sorting.getPath();
			if (sPaths != null && !sPaths.isEmpty()) {
				String[] paths = sorting.getPath().stream().map(SortingPath::toString).toArray(String[]::new);
				return Sort.by(direction, paths);
			}
		}
		return null;
	}

	private static void applyPageable(ShellDescriptorSearchRequest query, NativeSearchQuery nQuery, Sort sort) {
		Page page = query.getPage();
		if (page != null) {
			int idx = page.getIndex();
			int size = page.getSize();
			if (sort == null) {
				nQuery.setPageable(PageRequest.of(idx, size));
			} else {
				nQuery.setPageable(PageRequest.of(idx, size, sort));
			}

		}
	}

	private static boolean doBuildSubmodelNestedQuery(String key) {
		return key.startsWith(AasRegistryPaths.submodelDescriptors().toString() + ".");
	}

}