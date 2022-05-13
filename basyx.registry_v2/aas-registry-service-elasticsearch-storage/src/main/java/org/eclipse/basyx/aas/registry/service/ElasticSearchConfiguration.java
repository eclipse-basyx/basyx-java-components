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
package org.eclipse.basyx.aas.registry.service;

import java.time.Duration;

import org.eclipse.basyx.aas.registry.service.storage.AasRegistryStorage;
import org.eclipse.basyx.aas.registry.service.storage.elasticsearch.AtomicElasticSearchRepoAccess;
import org.eclipse.basyx.aas.registry.service.storage.elasticsearch.ElasticSearchAasRegistryStorage;
import org.eclipse.basyx.aas.registry.service.storage.elasticsearch.PainlessAtomicElasticSearchRepoAccess;
import org.eclipse.basyx.aas.registry.service.storage.elasticsearch.PainlessElasticSearchScripts;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import lombok.extern.log4j.Log4j2;

@Configuration
@EnableElasticsearchRepositories(basePackages = "org.eclipse.basyx.aas.registry.service.storage.elasticsearch")
@Log4j2
@ConditionalOnProperty(prefix = "registry", name = "type", havingValue = "elasticsearch")
public class ElasticSearchConfiguration extends AbstractElasticsearchConfiguration {

	@Value("${elasticsearch.url}")
	private String elasticsearchUrl;

	@Override
	public RestHighLevelClient elasticsearchClient() {
		log.info("Connecting to elasticsearch server '" + elasticsearchUrl + "' ...");
		ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo(elasticsearchUrl).withSocketTimeout(Duration.ofSeconds(300)).build();
		return RestClients.create(clientConfiguration).rest();
	}

	@Bean
	public AtomicElasticSearchRepoAccess extension(ApplicationContext context, ElasticsearchOperations ops, ElasticsearchConverter converter) {
		return new PainlessAtomicElasticSearchRepoAccess(ops, new PainlessElasticSearchScripts(), converter);
	}

	@Bean
	public AasRegistryStorage storage() {
		return new ElasticSearchAasRegistryStorage();
	}
}
