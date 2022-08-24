/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/

 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package basyx.components.databridge.camelactivemq.configuration;

import basyx.components.databridge.core.configuration.entity.DataSourceConfiguration;

/**
 * An implementation of ActiveMQ consumer configuration
 * @author haque
 *
 */
public class ActiveMQConsumerConfiguration extends DataSourceConfiguration {
	private String queue;

	public ActiveMQConsumerConfiguration() {}

	public ActiveMQConsumerConfiguration(String uniqueId, String serverUrl, int serverPort, String queue) {
		super(uniqueId, serverUrl, serverPort);
		this.queue = queue;
	}

	public String getQueue() {
		return queue;
	}

	public void setTopic(String queue) {
		this.queue = queue;
	}

	public String getConnectionURI() {
		return "activemq:" + getQueue();
	}
}
