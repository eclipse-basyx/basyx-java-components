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
 * Execution state enum
 * 
 * @author kuhn
 *
 */
public enum ExecutionState {
	// Enumeration constants
	IDLE("IDLE"), STARTING("STARTING"), EXECUTE("EXECUTE"), COMPLETING("COMPLETING"), COMPLETE("COMPLETE"), RESETTING("RESETTING"), HOLDING("HOLDING"), HELD("HELD"), UNHOLDING("UNHOLDING"), SUSPENDING("SUSPENDING"), SUSPENDED(
			"SUSPENDED"), UNSUSPENDING("UNSUSPENDING"), STOPPING("STOPPING"), STOPPED("STOPPED"), ABORTING("ABORTING"), ABORTED("ABORTED"), CLEARING("CLEARING");

	/**
	 * Get execution order by its value
	 */
	public static ExecutionState byValue(String value) {
		// Switch by requested value
		switch (value.toLowerCase()) {
		case "idle":
			return IDLE;
		case "starting":
			return STARTING;
		case "execute":
			return EXECUTE;
		case "completing":
			return COMPLETING;
		case "complete":
			return COMPLETE;
		case "resetting":
			return RESETTING;
		case "holding":
			return HOLDING;
		case "held":
			return HELD;
		case "unholding":
			return UNHOLDING;
		case "suspending":
			return SUSPENDING;
		case "suspended":
			return SUSPENDED;
		case "unsuspending":
			return UNSUSPENDING;
		case "stopping":
			return STOPPING;
		case "stopped":
			return STOPPED;
		case "aborting":
			return ABORTING;
		case "aborted":
			return ABORTED;
		case "clearing":
			return CLEARING;
		}

		// Indicate error
		throw new RuntimeException("Unknown value requested");
	}

	/**
	 * Enumeration item value
	 */
	protected String value = null;

	/**
	 * Constructor
	 */
	private ExecutionState(String val) {
		this.value = val;
	}

	/**
	 * Get enumeration value
	 */
	public String getValue() {
		return value;
	}
}
