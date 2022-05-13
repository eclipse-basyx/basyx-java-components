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

import java.util.stream.Stream;

import org.eclipse.basyx.aas.registry.service.tests.integration.BaseEventListener;
import org.eclipse.basyx.aas.registry.service.tests.integration.BaseIntegrationTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;


@TestPropertySource(properties = { "registry.type=elasticsearch", "events.sink=kafka" })
@Ignore
public class KafkaEventsElasticsearchStorageIntegrationTest extends BaseIntegrationTest {	

	private static final DockerImageName KAFKA_TEST_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:6.2.1");

	private static final DockerImageName ELASTICSEARCH_TEST_IMAGE = DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch-oss:7.10.2");
	
	public static KafkaContainer KAFKA = new KafkaContainer(KAFKA_TEST_IMAGE);

	public static ElasticsearchContainer ELASTIC_SEARCH = new ElasticsearchContainer(ELASTICSEARCH_TEST_IMAGE);
	
	@BeforeClass
	public static void startContainersInParallel() {
		Stream.of(KAFKA, ELASTIC_SEARCH).parallel().forEach(GenericContainer::start);
	}

	@AfterClass
	public static void stopContainersInParallel() {
		Stream.of(KAFKA, ELASTIC_SEARCH).parallel().forEach(GenericContainer::stop);
	}
	
	@DynamicPropertySource
	static void assignAdditionalProperties(DynamicPropertyRegistry registry) {
		registry.add("elasticsearch.url", ELASTIC_SEARCH::getHttpHostAddress);
		registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
	}

	
	@Component
	public static class KafkaEventListener extends BaseEventListener {

		@KafkaListener(topics = "aas-registry", groupId = "test")
		public void receive(String message) {			
			super.offer(message);
		}
	}
}