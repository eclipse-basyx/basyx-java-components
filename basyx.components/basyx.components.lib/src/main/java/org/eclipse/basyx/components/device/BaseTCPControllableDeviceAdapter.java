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

import org.eclipse.basyx.components.netcomm.NetworkReceiver;
import org.eclipse.basyx.components.netcomm.TCPClient;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;

/**
 * Base class for integrating controllable devices with BaSys
 * 
 * This controllable device uses a Thread for string based TCP communication. It
 * implements a string based communication protocol with the control component
 * of the device manager.
 * 
 * @author kuhn
 *
 */
public abstract class BaseTCPControllableDeviceAdapter extends BaseTCPDeviceAdapter implements IBaSysNativeDeviceStatus, NetworkReceiver {

	/**
	 * Receive thread
	 */
	protected Thread rxThread = null;

	/**
	 * Selected device operation mode
	 */
	protected String opMode = "";

	/**
	 * Device execution state
	 */
	protected ExecutionState exState = null;

	/**
	 * Constructor
	 */
	public BaseTCPControllableDeviceAdapter(int port) {
		// Invoke base constructor
		super(port);
	}

	/**
	 * Start the device
	 */
	@Override
	public void start() {
		// Invoke base implementation
		super.start();

		// Add this component as message listener
		communicationClient.addTCPMessageListener(this);
		// - Start receive thread
		rxThread = new Thread(communicationClient);
		// - Start receiving
		rxThread.start();
	}

	/**
	 * Stop the device
	 */
	@Override
	public void stop() {
		// Invoke base implementation
		super.stop();

		// End communication
		communicationClient.close();
	}

	/**
	 * Wait for end of all threads
	 */
	@Override
	public void waitFor() {
		// Invoke base implementation
		super.waitFor();

		// Wait for end of TCP thread
		try {
			rxThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Process device status changes
	 */
	@Override
	protected void statusChange(String newStatus) {
		// React to device status change
		// - Indicate running service in EXECUTE state
		if (newStatus.equals(ExecutionState.EXECUTE.getValue()))
			onServiceInvocation();

		// Set requested device execution state
		exState = ExecutionState.byValue(newStatus);
	}

	/**
	 * Changed operation mode
	 */
	protected void onOperationModeChange(String newOperationMode) {
		// Set requested device operation mode
		opMode = newOperationMode;
	}

	/**
	 * A message has been received
	 */
	@Override
	public void onReceive(byte[] message) {
		// Convert message to String
		String rxMessage = TCPClient.toString(message);

		// Process message
		if (rxMessage.startsWith("state:")) {
			statusChange(rxMessage.substring("state:".length()));
			return;
		}
		if (rxMessage.startsWith("opMode:")) {
			onOperationModeChange(rxMessage.substring("opMode:".length()));
			return;
		}

		// Indicate exception
		throw new RuntimeException("Unexpected message received:" + rxMessage);
	}

	public String getOpMode() {
		return opMode;
	}

	public ExecutionState getExState() {
		return exState;
	}

}
