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
package org.eclipse.basyx.regression.models.controlcomponent;

import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.models.controlcomponent.ControlComponent;
import org.eclipse.basyx.models.controlcomponent.ExecutionMode;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;
import org.eclipse.basyx.models.controlcomponent.OccupationState;
import org.eclipse.basyx.models.controlcomponent.SimpleControlComponent;
import org.junit.Test;

/**
 * Test cases for basic control component testing
 * 
 * @author kuhn
 *
 */
public class TestSimpleControlComponent {

	/**
	 * Execution state assignment tests
	 */
	@Test
	public void executionStateAssignmentTests() {
		// Instantiate simple control component
		SimpleControlComponent ctrlComponent = new SimpleControlComponent();

		// Change state, read state back
		ctrlComponent.setExecutionState("idle");
		assertTrue(ctrlComponent.getExecutionState().equals("idle"));
	}

	/**
	 * Test simple proxy control component
	 */
	@Test
	public void testSimpleProxyControlComponent() {
		// Instantiate simple proxy control component
		SimpleControlComponent ctrlComponent = new SimpleControlComponent();

		// Check component initial state
		// - Initial state
		assertTrue(ctrlComponent.getExecutionState().equals("idle"));
		// - Occupation state
		assertTrue(ctrlComponent.getOccupationState().equals(OccupationState.FREE));
		// - Execution mode
		assertTrue(ctrlComponent.getExecutionMode().equals(ExecutionMode.AUTO));
		// - Error state
		assertTrue(ctrlComponent.getErrorState().equals(""));
	}

	/**
	 * Run a normal sequence
	 */
	@Test
	public void testSimpleProxyControlComponentSequence() {
		// Instantiate simple proxy control component
		SimpleControlComponent ctrlComponent = new SimpleControlComponent(true);

		// Check component initial state
		assertTrue(ctrlComponent.getExecutionState().equals("idle"));

		// Change operation mode
		ctrlComponent.setOperationMode("DefaultService");
		// - Read operation mode back
		assertTrue(ctrlComponent.getOperationMode().equals("DefaultService"));

		// Issue start command
		ctrlComponent.setCommand(ControlComponent.OPERATION_START);
		// - Check execution state
		assertTrue(ctrlComponent.getExecutionState().equals(ExecutionState.EXECUTE.getValue()));

		// Machine finishes service
		ctrlComponent.finishState();
		// - Check execution state
		assertTrue(ctrlComponent.getExecutionState().equals(ExecutionState.COMPLETE.getValue()));

		// Reset device
		ctrlComponent.setCommand(ControlComponent.OPERATION_RESET);
		// - Check execution state
		assertTrue(ctrlComponent.getExecutionState().equals(ExecutionState.RESETTING.getValue()));
		// - Indicate end of reset
		ctrlComponent.finishState();
		// - Check execution state
		assertTrue(ctrlComponent.getExecutionState().equals(ExecutionState.IDLE.getValue()));
	}

	/**
	 * Run a sequence that is aborted by device
	 */
	@Test
	public void testSimpleProxyControlComponentSequenceAbort() {
		// Instantiate simple proxy control component
		SimpleControlComponent ctrlComponent = new SimpleControlComponent(true);

		// Check component initial state
		assertTrue(ctrlComponent.getExecutionState().equals("idle"));

		// Change operation mode
		ctrlComponent.setOperationMode("DefaultService");
		// - Read operation mode back
		assertTrue(ctrlComponent.getOperationMode().equals("DefaultService"));

		// Issue start command
		ctrlComponent.setCommand(ControlComponent.OPERATION_START);
		// - Check execution state
		assertTrue(ctrlComponent.getExecutionState().equals(ExecutionState.EXECUTE.getValue()));

		// Machine aborts service
		ctrlComponent.setCommand(ControlComponent.OPERATION_ABORT);
		// - Check execution state
		assertTrue(ctrlComponent.getExecutionState().equals(ExecutionState.ABORTED.getValue()));

		// Operator clears machine state
		ctrlComponent.setCommand(ControlComponent.OPERATION_CLEAR);
		// - Check execution state
		assertTrue(ctrlComponent.getExecutionState().equals(ExecutionState.STOPPED.getValue()));

		// Operator restarts machine
		ctrlComponent.setCommand(ControlComponent.OPERATION_RESET);
		// - Check execution state
		assertTrue(ctrlComponent.getExecutionState().equals(ExecutionState.RESETTING.getValue()));
		// - Indicate end of reset
		ctrlComponent.finishState();
		// - Check execution state
		assertTrue(ctrlComponent.getExecutionState().equals(ExecutionState.IDLE.getValue()));
	}
}
