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

import org.eclipse.basyx.components.netcomm.TCPClient;

/**
 * Base class for integrating devices with BaSys
 * 
 * This base class provides a simple framework for integrating devices with
 * BaSys/BaSyx. It implements a string based communication and connects to a
 * device manager. The class defines interface methods that need to be called
 * from sub classes or native code.
 * 
 * The device has no control component; the device decides itself when its
 * service is executed. This happens e.g. due to sensor inputs or MES system
 * request
 * 
 * @author kuhn
 *
 */
public abstract class BaseTCPDeviceAdapter extends BaseDevice implements IBaSysNativeDeviceStatus {

	/**
	 * Communication client
	 */
	protected TCPClient communicationClient = null;

	/**
	 * Store server port
	 */
	protected int serverPort;

	/**
	 * Constructor
	 */
	public BaseTCPDeviceAdapter(int port) {
		// Store server port
		serverPort = port;
	}

	/**
	 * Indicate device service invocation to device manager
	 */
	@Override
	protected void onServiceInvocation() {
		// Invoke base implementation
		super.onServiceInvocation();

		// Write bytes to device manager
		communicationClient.sendMessage("invocation:start\n");
	}

	/**
	 * Indicate device service end
	 */
	protected void onServiceEnd() {
		// Invoke base implementation
		super.onServiceEnd();

		// Write bytes to device manager
		communicationClient.sendMessage("invocation:end\n");
	}

	/**
	 * Indicate device status change to device manager
	 */
	@Override
	protected void statusChange(String newStatus) {
		// Write bytes to device manager
		communicationClient.sendMessage("status:" + newStatus + "\n");
	}

	/**
	 * Close device communication socket
	 */
	public void closeSocket() {
		// Close socket
		communicationClient.close();
	}

	/**
	 * Start the device
	 */
	@Override
	public void start() {
		// Invoke base implementation
		super.start();

		// Create connection
		communicationClient = new TCPClient("localhost", serverPort);
	}

	/**
	 * Stop the device
	 */
	@Override
	public void stop() {
		// Invoke base implementation
		super.stop();

		// Close communication socket
		closeSocket();
	}

	/**
	 * Wait for completion of server
	 */
	@Override
	public void waitFor() {
		// Do nothing
	}
}
