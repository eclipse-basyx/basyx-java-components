/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.camelprometheus.configuration;

import basyx.components.updater.camelhttppolling.configuration.HttpPollingConsumerConfiguration;
import com.bdwise.prometheus.client.builder.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


/**
 * An implementation of prometheus consumer configuration
 * @author n14s - Niklas Mertens
 *
 */
public class PrometheusConsumerConfiguration extends HttpPollingConsumerConfiguration {
	Map<String, String> queryMap = new HashMap<>();



	public PrometheusConsumerConfiguration() {
		addQueryMapEntries();
	}
	
	public PrometheusConsumerConfiguration(String uniqueId, String serverUrl, int serverPort, String query) {
		super(uniqueId, serverUrl, serverPort, query);
		addQueryMapEntries();
	}

	public String getConnectionURI() {
		InstantQueryBuilder iqb = QueryBuilderType.InstantQuery.newInstance(getServerUrl() + ":" + getServerPort());
		URI targetUri = iqb.withQuery(queryMap.get(getQuery())).build();
		System.out.println(targetUri.toString());
		return targetUri.toString();
	}

	private void addQueryMapEntries(){
		queryMap.put("cpu-usage", "avg(rate(node_cpu_seconds_total{mode=\"idle\"}[3m])) * 100");
		queryMap.put("memory-usage", "node_memory_Active_bytes/node_memory_MemTotal_bytes*100");
	}
}
