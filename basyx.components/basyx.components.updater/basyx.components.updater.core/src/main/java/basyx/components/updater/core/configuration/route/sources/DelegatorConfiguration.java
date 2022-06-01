/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.core.configuration.route.sources;

import basyx.components.updater.core.delegator.servlet.DelegatorServlet;

/**
 * Configuration properties of a delegator with poll timer 
 * @author haque
 *
 */
public class DelegatorConfiguration extends RouteEntity {
	private String host;
	private int port;
	private String path;
	private String timerName = "pollTimer";
	private int delay = -1;
	private int repeatCount = 1;
	private DelegatorServlet delegatorServlet;
	
	public DelegatorConfiguration() {}

	public DelegatorConfiguration(String uniqueId, String host, int port, String userName, String password, String path) {
		super(uniqueId);
		this.host = host;
		this.port = port;
		this.path = path;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public DelegatorServlet getDelegatorServlet() {
		return delegatorServlet;
	}

	public void setDelegatorServlet(DelegatorServlet delegatorServlet) {
		this.delegatorServlet = delegatorServlet;
	}

	public String getTimerName() {
		return timerName;
	}

	public void setTimerName(String timerName) {
		this.timerName = timerName;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	@Override
	public String getConnectionURI() {
		return "timer://" + getTimerName() + "?delay=" + getDelay() + "&repeatCount=" + getRepeatCount();
	}
}
