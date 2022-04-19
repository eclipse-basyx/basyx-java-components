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
package org.eclipse.basyx.components.netcomm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * Implements an NIO TCP server
 * 
 * This server supports both blocking and non-blocking operation. It manages a
 * single communication stream to a client. Communication messages consist of a
 * 32 Bit value that describes message length (bytes), followed by message
 * length bytes with payload.
 * 
 * @author kuhn
 *
 */
public class TCPServer extends TCPCommunicator implements Runnable {

	/**
	 * Port number
	 */
	protected int port = -1;

	/**
	 * Implements non-blocking semantics
	 */
	protected boolean isBlocking = true;

	/**
	 * Server socket
	 */
	protected ServerSocketChannel serverSocket = null;

	/**
	 * Create a connection server on given server port
	 */
	public TCPServer(int portNo) {
		// Store port number
		port = portNo;

		// Catch communication errors
		try {
			// Resolve address of this host
			InetAddress hostIPAddress = InetAddress.getByName("localhost");

			// Server socket channel
			serverSocket = ServerSocketChannel.open();
			serverSocket.configureBlocking(true);
			serverSocket.socket().bind(new InetSocketAddress(hostIPAddress, port));
		} catch (IOException e) {
			// Output exception
			e.printStackTrace();
		}
	}

	/**
	 * Make socket non blocking
	 */
	public void makeNonBlocking() {
		// Set my own blocking flag
		isBlocking = false;

		// Change server socket
		try {
			serverSocket.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add server socket (the one that receives 'accept' notifications) to given
	 * selector
	 * 
	 * @return The selector, same as input parameter
	 */
	public Selector addServerSocket(Selector selector) {
		// Register 'accept' event
		try {
			serverSocket.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Return selector
		return selector;
	}

	/**
	 * Accept an incoming connection
	 */
	public void acceptIncomingConnection() throws IOException {
		// Only accept connection if no connection is waiting
		if (!((communicationToClient == null) || (!communicationToClient.isConnected())))
			return;

		// Try to accept connection. Return communication socket if successful
		communicationToClient = serverSocket.accept();

		// Make communication nonblocking if blocking flag is not set
		if (!isBlocking)
			communicationToClient.configureBlocking(false);
	}

	/**
	 * Close server communication
	 */
	public void closeServer() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Server main loop (for non-blocking communication)
	 */
	@Override
	public void run() {
		// Check if we are still using blocking communication
		if (!isBlocking)
			throw new RuntimeException("TCP Server communication thread may only be used on blocking sockets.");

		// Wait for a connection
		while (true) {
			// Exception handling
			try {
				// Wait for new connection if necessary
				while (((communicationToClient == null) || (!communicationToClient.isConnected())))
					acceptIncomingConnection();

				// Stop if server channel is closed
				if (!serverSocket.isOpen())
					break;

				// Read data
				byte[] message = readMessage();

				// Notify listeners
				if (message != null)
					notifyListeners(message);
			} catch (ClosedChannelException e) {
				// Server socket has been closed
				return;
			} catch (IOException e) {
				// Print exception, continue
				e.printStackTrace();
			}
		}
	}
}
