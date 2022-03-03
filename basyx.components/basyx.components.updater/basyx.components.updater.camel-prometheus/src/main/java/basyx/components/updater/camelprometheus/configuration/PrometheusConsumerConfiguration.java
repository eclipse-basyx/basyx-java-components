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

import basyx.components.updater.camelhttp.configuration.HttpConsumerConfiguration;
import com.bdwise.prometheus.client.builder.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


/**
 * An implementation of prometheus consumer configuration
 * @author n14s - Niklas Mertens
 *
 */
public class PrometheusConsumerConfiguration extends HttpConsumerConfiguration {
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

		queryMap.put("cpu-arch", "node_uname_info");
		queryMap.put("cpu-cores", "count(count(node_cpu_seconds_total) by (cpu))");
		queryMap.put("cpu-usage", "(((count(count(node_cpu_seconds_total) by (cpu))) - avg(sum by (mode)(irate(node_cpu_seconds_total{mode='idle'}[1m])))) * 100) / count(count(node_cpu_seconds_total) by (cpu))");
		queryMap.put("system-uptime", "node_time_seconds - node_boot_time_seconds");
		queryMap.put("ram-installed", "node_memory_MemTotal_bytes");
		queryMap.put("ram-usage", "100 - ((node_memory_MemAvailable_bytes * 100) / node_memory_MemTotal_bytes)");
		queryMap.put("rootfs-type", "node_filesystem_size_bytes{mountpoint=\"/\"}");
		queryMap.put("rootfs-usage", "100 - ((node_filesystem_avail_bytes{mountpoint=\"/\",fstype!=\"rootfs\"} * 100) / node_filesystem_size_bytes{mountpoint=\"/\",fstype!=\"rootfs\"})");
		queryMap.put("docker-version", "cadvisor_version_info");
		queryMap.put("docker-runningContainers", "irate(container_network_transmit_bytes_total{image!=\"\"}[1m])");
	}
}
