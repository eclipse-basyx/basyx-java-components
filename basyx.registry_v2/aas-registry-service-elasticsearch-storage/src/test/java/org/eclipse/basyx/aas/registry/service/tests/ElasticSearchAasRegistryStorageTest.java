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
package org.eclipse.basyx.aas.registry.service.tests;

import org.eclipse.basyx.aas.registry.service.ElasticSearchConfiguration;
import org.junit.ClassRule;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

@TestPropertySource(properties = { "registry.type=elasticsearch"})
@ContextConfiguration(classes = { ElasticSearchConfiguration.class })
public class ElasticSearchAasRegistryStorageTest extends AasRegistryStorageTest {

	private static final DockerImageName ELASTICSEARCH_TEST_IMAGE = DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch-oss:7.10.2");

	@ClassRule
	public static ElasticsearchContainer ELASTICSEARCH_CONTAINER = new ElasticsearchContainer(ELASTICSEARCH_TEST_IMAGE);

	@DynamicPropertySource
	static void assignAdditionalProperties(DynamicPropertyRegistry registry) {
		registry.add("elasticsearch.url", ELASTICSEARCH_CONTAINER::getHttpHostAddress);
	}
}