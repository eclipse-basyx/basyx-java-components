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
package org.eclipse.basyx.components.devicemanager;

import org.eclipse.basyx.components.netcomm.NetworkReceiver;
import org.eclipse.basyx.components.netcomm.TCPServer;

/**
 * Base class for device managers that communicate via TCP with the connected
 * device
 * 
 * @author kuhn
 *
 */
public abstract class TCPDeviceManagerComponent extends DeviceManagerComponent implements NetworkReceiver {

	/**
	 * TCP port number
	 */
	protected int tcpPortNumber = -1;

	/**
	 * TCP server reference
	 */
	protected TCPServer tcpServer = null;

	/**
	 * TCP server thread
	 */
	protected Thread tcpServerThread = null;

	/**
	 * Constructor
	 */
	public TCPDeviceManagerComponent(int portNumber) {
		// Store port number
		tcpPortNumber = portNumber;
	}

	/**
	 * Run this service
	 */
	@Override
	public void start() {
		// Base implementation
		super.start();

		// Create TCP thread (or any other connection) to legacy device
		tcpServer = new TCPServer(tcpPortNumber);

		// Register this component as network receiver
		tcpServer.addTCPMessageListener(this);

		// Start TCP server
		tcpServerThread = new Thread(tcpServer);
		tcpServerThread.start();
	}

	/**
	 * Stop this service
	 */
	@Override
	public void stop() {
		// Base implementation
		super.stop();

		// End server
		tcpServer.closeServer();
		tcpServer.close();
	}

	/**
	 * Wait for completion of all servers
	 */
	@Override
	public void waitFor() {
		// Base implementation
		super.waitFor();

		// Wait for server
		try {
			tcpServerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
