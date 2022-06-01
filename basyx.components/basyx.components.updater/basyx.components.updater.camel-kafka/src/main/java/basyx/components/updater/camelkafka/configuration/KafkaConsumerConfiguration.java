/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.camelkafka.configuration;

import basyx.components.updater.core.configuration.route.sources.DataSourceConfiguration;

/**
 * An implementation of kafka consumer configuration
 * @author haque
 *
 */
public class KafkaConsumerConfiguration extends DataSourceConfiguration {
	private String topic;
	private int maxPollRecords;
	private String groupId;
	private int consumersCount;
	private String seekTo;
	
	public KafkaConsumerConfiguration() {}
	
	public KafkaConsumerConfiguration(String uniqueId, String serverUrl, int serverPort, String topic,
			int maxPollRecords, String groupId, int consumersCount, String seekTo) {
		super(uniqueId, serverUrl, serverPort);
		this.topic = topic;
		this.maxPollRecords = maxPollRecords;
		this.groupId = groupId;
		this.consumersCount = consumersCount;
		this.seekTo = seekTo;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getMaxPollRecords() {
		return maxPollRecords;
	}

	public void setMaxPollRecords(int maxPollRecords) {
		this.maxPollRecords = maxPollRecords;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getConsumersCount() {
		return consumersCount;
	}

	public void setConsumersCount(int consumersCount) {
		this.consumersCount = consumersCount;
	}

	public String getSeekTo() {
		return seekTo;
	}

	public void setSeekTo(String seekTo) {
		this.seekTo = seekTo;
	}

	public String getConnectionURI() {
		return 
		"kafka:" + getTopic() + "?brokers=" + getServerUrl() + ":" + getServerPort()
		+ "&maxPollRecords=" + getMaxPollRecords()
        + "&consumersCount=" + getConsumersCount()
        + "&seekTo=" + getSeekTo()
        + "&groupId="  + getGroupId();
	}
}
