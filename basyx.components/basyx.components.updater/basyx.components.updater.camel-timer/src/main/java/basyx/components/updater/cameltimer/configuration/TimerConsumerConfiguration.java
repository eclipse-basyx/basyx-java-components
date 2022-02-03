/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/

 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package basyx.components.updater.cameltimer.configuration;

import basyx.components.updater.core.configuration.DataSourceConfiguration;

/**
 * An implementation of Timer consumer configuration
 * @author n14s
 *
 */
public class TimerConsumerConfiguration extends DataSourceConfiguration {
	private boolean fixedRate;
	private int delay;
	private int period;

	public TimerConsumerConfiguration() {}

	public TimerConsumerConfiguration(String uniqueId, String serverUrl,int serverPort, boolean fixedRate, int delay, int period) {
		super(uniqueId, serverUrl, serverPort);
		this.fixedRate = fixedRate;
		this.delay = delay;
		this.period = period;
	}

	public String getConnectionURI() {
		return "timer://foo?fixedRate=" + getFixedRate()
				+ "&delay=" + getDelay()
				+ "&period=" + getPeriod();
	}

	public boolean getFixedRate() {
		return fixedRate;
	}

	public void setFixedRate(boolean fixedRate) {
		this.fixedRate = fixedRate;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}
}
