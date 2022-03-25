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
 * A simplified implementation of a control component for devices that offer
 * only basic services
 * 
 * @author kuhn
 *
 */
public class SimpleControlComponent extends ControlComponent {

	/**
	 * Version information for serialized instances
	 */
	private static final long serialVersionUID = 1L;

	private boolean explicitResetFinished;

	/**
	 * Constructor
	 */
	public SimpleControlComponent() {
		this(false);
	}

	/**
	 * Constructs this control component so that it can be configured if the
	 * resetting stage should finalize on its own or if it needs another
	 * confirmation through {@link ControlComponent#finishState}
	 * 
	 * @param explicitResetFinished
	 */
	public SimpleControlComponent(boolean explicitResetFinished) {
		this.explicitResetFinished = explicitResetFinished;

		// Initial execution state
		setExecutionState("idle");
	}

	/**
	 * Indicate an execution state change
	 */
	@Override
	protected String filterExecutionState(String newExecutionState) {
		// Implement a simplified model that only consists of states
		// idle/execute/complete/aborted/stopped
		switch (newExecutionState.toLowerCase()) {
		// Move from starting state directly to execute state after notifying the device
		case "starting":
			return ExecutionState.EXECUTE.getValue();

		// Move from completing state directly to complete state
		case "completing":
			return ExecutionState.COMPLETE.getValue();

		// Move from resetting state directly to idle state
		case "resetting":
			if (explicitResetFinished) {
				break;
			} else {
				return ExecutionState.IDLE.getValue();
			}

			// Move from aborting state directly to aborted state
		case "aborting":
			return ExecutionState.ABORTED.getValue();

		// Move from clearing state directly to stopped state
		case "clearing":
			return ExecutionState.STOPPED.getValue();
		}

		// Default behavior - leave execution state unchanged
		return newExecutionState;
	}

}
