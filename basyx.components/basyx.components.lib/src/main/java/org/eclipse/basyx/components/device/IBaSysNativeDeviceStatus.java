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

/**
 * Base interface for integrating devices with BaSys
 * 
 * This base class interface defines a simple framework for integrating devices
 * with BaSys/BaSyx. If the device has no control component, it defines
 * interface methods that need to be called from sub classes or native code in
 * order to communicate the device status to BaSys. If the device has a control
 * component, the interface methods communicate the device status as response to
 * device status requests from the control component.
 * 
 * Depending on the device, the operations may be called asynchronously, e.g. if
 * the device is triggered by environment sensors, or they may be called
 * synchronously as response to signals to the device.
 * 
 * It resembles the following basic state machine OFF
 * --(deviceInitialized){@literal -->} IDLE --(serviceRunning){@literal -->}
 * RUNNING --(serviceCompleted){@literal -->} COMPLETE
 * --(resetCompleted){@literal -->} IDLE
 * 
 * @author kuhn
 *
 */
public interface IBaSysNativeDeviceStatus {

	/**
	 * Device interface function: (usually native code) indicates that device has
	 * been initialized and is ready now
	 */
	public void deviceInitialized();

	/**
	 * Device interface function: (usually native code) indicates that device
	 * service has started and is running
	 */
	public void serviceRunning();

	/**
	 * Device interface function: (usually native code) indicates that device
	 * service execution has completed
	 */
	public void serviceCompleted();

	/**
	 * Device interface function: (usually native code) indicates that device is
	 * ready again
	 */
	public void resetCompleted();
}
