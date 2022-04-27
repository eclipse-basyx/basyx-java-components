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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.basyx.vab.protocol.basyx.CoderTools;

/**
 * Base class for NIO based TCP communication
 * 
 * Communication messages consist of a 32 Bit value that describes message
 * length (bytes), followed by message length bytes with payload.
 * 
 * @author kuhn
 *
 */
public abstract class TCPCommunicator {

	/**
	 * Communication channel
	 */
	protected SocketChannel communicationToClient = null;

	/**
	 * Add a message listener for blocking mode
	 */
	protected Collection<NetworkReceiver> messageListeners = new LinkedList<>();

	/**
	 * Convert byte array to string
	 */
	public static String toString(byte[] value) {
		// Convert message to string
		String str = null;
		// - Try to convert
		try {
			str = new String(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Return string
		return str;
	}

	/**
	 * Add communication socket (the one that transmits/receives data) to given
	 * selector
	 * 
	 * @return The selector, same as input parameter
	 */
	public Selector addCommunicationSocket(Selector selector) {
		// Only continue if communication socket is non null and connected
		if ((communicationToClient == null) || (!communicationToClient.isConnected()))
			return selector;

		// Only add non blocking channels to selector, otherwise return at this point
		if (communicationToClient.isBlocking())
			return selector;

		// Register 'accept' event
		try {
			communicationToClient.register(selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Return selector
		return selector;
	}

	/**
	 * Get a selector. This socket will not be added to the selector.
	 */
	public Selector getSelector() {
		// Try to create an empty selector
		try {
			return Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Send message to client
	 */
	public void sendMessage(byte[] message) {
		// Output buffer
		ByteBuffer buffer = ByteBuffer.allocate(message.length + 4);

		// Fill with message length first
		byte[] frameSizeBytes = new byte[4];
		CoderTools.setInt32(frameSizeBytes, 0, message.length);
		buffer.put(frameSizeBytes);

		// Add message
		buffer.put(message);

		// Place marker at buffer start
		buffer.flip();

		// Send message
		sendMessage(buffer);
	}

	/**
	 * Send message to client
	 */
	public void sendMessage(String message) {
		// Output buffer
		ByteBuffer buffer = ByteBuffer.allocate(message.length() + 4);

		// Fill with message length first
		byte[] frameSizeBytes = new byte[4];
		CoderTools.setInt32(frameSizeBytes, 0, message.length());
		buffer.put(frameSizeBytes);

		// Add message
		buffer.put(message.getBytes());

		// Place marker at buffer start
		buffer.flip();

		// Send message
		sendMessage(buffer);
	}

	/**
	 * Send message to client
	 * 
	 * Buffer must point to where transmission should start
	 */
	public void sendMessage(ByteBuffer messageBuffer) {
		// Only continue if client is connected
		if (communicationToClient == null)
			return;

		// Transmit frame
		try {
			communicationToClient.write(messageBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read message from client
	 */
	public byte[] readMessage() {
		// Process inputs
		try {
			// Read response
			// - Wait for leading 4 byte header that contains frame length
			ByteBuffer rxBuffer1 = ByteBuffer.allocate(4);
			readBytes(rxBuffer1, 4);
			int frameSize = CoderTools.getInt32(rxBuffer1.array(), 0);

			// Wait for frame to arrive
			ByteBuffer rxBuffer2 = ByteBuffer.allocate(frameSize);
			readBytes(rxBuffer2, frameSize);
			byte[] rxFrame = rxBuffer2.array();

			// Return message
			return rxFrame;
		} catch (IOException e) {
			// End when TCP socket is closed
			if (!communicationToClient.isConnected())
				return null;

			// Output error
			e.printStackTrace();
		}

		// In case of error, return null
		return null;
	}

	/**
	 * Read message from client as string
	 */
	public String readStringMessage() {
		// Get message as byte array
		byte[] msg = readMessage();
		// - Null check
		if (msg == null)
			return null;

		// Convert message to string
		return toString(msg);
	}

	/**
	 * Close communication
	 */
	public void close() {
		try {
			communicationToClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read a number of bytes
	 */
	protected void readBytes(ByteBuffer bytes, int expectedBytes) throws IOException {
		// Read bytes until buffer is full
		while (bytes.position() < expectedBytes) {
			communicationToClient.read(bytes);
		}
	}

	/**
	 * Add a TCP message listener
	 */
	public void addTCPMessageListener(NetworkReceiver addedListener) {
		messageListeners.add(addedListener);
	}

	/**
	 * Remove a TCP message listener
	 */
	public void removeTCPMessageListener(NetworkReceiver addedListener) {
		messageListeners.remove(addedListener);
	}

	/**
	 * Notify message listeners
	 */
	protected void notifyListeners(byte[] message) {
		// Notify listeners about message
		for (NetworkReceiver listener : messageListeners)
			listener.onReceive(message);
	}
}
