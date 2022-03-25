/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.regression.support.processengine.stubs;

import java.util.List;

import org.eclipse.basyx.components.processengine.connector.IDeviceServiceExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceServiceExecutorStub implements IDeviceServiceExecutor {

	/**
	 * Initiates a logger using the current class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DeviceServiceExecutorStub.class);

	private String serviceName;
	private String serviceProvider;
	@SuppressWarnings("unused")
	private String serviceSubmodelid;
	private List<Object> params;

	// whether the right service is invoked
	@Override
	public Object executeService(String servicename, String serviceProvider, String submodelid, List<Object> params) {
		this.serviceName = servicename;
		this.serviceProvider = serviceProvider;
		this.serviceSubmodelid = submodelid;
		this.params = params;
		logger.debug("service: %s, executed by device: %s , parameters: ", servicename, serviceProvider);
		if (params.size() == 0) {
			logger.debug("[]");
		} else {
			for (Object p : params) {
				logger.debug("%s, ", p);
			}
		}

		return 1;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getServiceProvider() {
		return serviceProvider;
	}

	public List<Object> getParams() {
		return params;
	}

}
