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
package org.eclipse.basyx.models.controlcomponent;

/**
 * Execution order enum
 * 
 * @author kuhn
 *
 */
public enum ExecutionOrder {
	// Enumeration constants
	START("START"), COMPLETE("COMPLETE"), RESET("RESET"), HOLD("HOLD"), UNHOLD("UNHOLD"), SUSPEND("SUSPEND"), UNSUSPEND("UNSUSPEND"), CLEAR("CLEAR"), STOP("STOP"), ABORT("ABORT");

	/**
	 * Get execution order by its value
	 */
	public static ExecutionOrder byValue(String value) {
		// Switch by requested value
		switch (value.toLowerCase()) {
		case "start":
			return START;
		case "complete":
			return COMPLETE;
		case "reset":
			return RESET;
		case "hold":
			return HOLD;
		case "unhold":
			return UNHOLD;
		case "suspend":
			return SUSPEND;
		case "unsuspend":
			return UNSUSPEND;
		case "clear":
			return CLEAR;
		case "stop":
			return STOP;
		case "abort":
			return ABORT;
		}

		// Indicate error
		throw new RuntimeException("Unknown value requested:" + value.toLowerCase());
	}

	/**
	 * Enumeration item value
	 */
	protected String value = null;

	/**
	 * Constructor
	 */
	private ExecutionOrder(String val) {
		this.value = val;
	}

	/**
	 * Get enumeration value
	 */
	public String getValue() {
		return value;
	}
}
