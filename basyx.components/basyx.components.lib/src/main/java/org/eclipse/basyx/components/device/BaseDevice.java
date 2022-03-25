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
package org.eclipse.basyx.components.device;

import org.eclipse.basyx.components.service.BaseBaSyxService;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for integrating devices with BaSys
 * 
 * This base class provides a simple framework for integrating devices with
 * BaSys/BaSyx. It defines callback functions that are invoked by native
 * devices, and that are used to communicate the device status.
 * 
 * @author kuhn
 *
 */
public abstract class BaseDevice extends BaseBaSyxService implements IBaSysNativeDeviceStatus {

	/**
	 * Initiates a logger using the current class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BaseDevice.class);

	/**
	 * Device interface function: (usually native code) indicates that device has
	 * been initialized
	 */
	@Override
	public void deviceInitialized() {
		// Indicate initialization to device
		onInitialize();

		// Change status
		statusChange(ExecutionState.IDLE.getValue());
	}

	/**
	 * Device interface function: (usually native code) indicates that device
	 * service is running
	 */
	@Override
	public void serviceRunning() {
		// Indicate service invocation to device
		onServiceInvocation();

		// Change status
		statusChange(ExecutionState.EXECUTE.getValue());
	}

	/**
	 * Device interface function: (usually native code) indicates that device
	 * service execution has completed
	 */
	@Override
	public void serviceCompleted() {
		// Indicate service invocation to device
		onServiceEnd();

		// Change status
		statusChange(ExecutionState.COMPLETE.getValue());
	}

	/**
	 * Device interface function: (usually native code) indicates that device is
	 * ready again
	 */
	@Override
	public void resetCompleted() {
		// Indicate reset to device
		onReset();

		// Change status
		statusChange(ExecutionState.IDLE.getValue());
	}

	/**
	 * Indicate device status change
	 */
	protected abstract void statusChange(String newStatus);

	/**
	 * Indicate device initialization
	 */
	protected void onInitialize() {
		// Here: Initialize device
		logger.debug("Device " + name + " status change: initialize");
	}

	/**
	 * Indicate device service invocation
	 */
	protected void onServiceInvocation() {
		// Here: Invoke device service
		logger.debug("Device " + name + " status change: invoke");
	}

	/**
	 * Indicate device service end
	 */
	protected void onServiceEnd() {
		// Here: Perform device operation after device service end (if necessary)
		logger.debug("Device " + name + " status change: end");
	}

	/**
	 * Indicate device reset
	 */
	protected void onReset() {
		// Here: Reset device
		logger.debug("Device " + name + " status change: reset");
	}
}
