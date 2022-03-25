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
 * Receive change events from control components
 * 
 * @author kuhn
 *
 */
public interface ControlComponentChangeListener {

	/**
	 * Indicate change of a variable
	 */
	public void onVariableChange(String varName, Object newValue);

	/**
	 * Indicate new occupier
	 */
	public void onNewOccupier(String occupierId);

	/**
	 * Indicate new occupation state
	 */
	public void onNewOccupationState(OccupationState state);

	/**
	 * Indicate an execution mode change
	 */
	public void onChangedExecutionMode(ExecutionMode newExecutionMode);

	/**
	 * Indicate an execution state change
	 */
	public void onChangedExecutionState(ExecutionState newExecutionState);

	/**
	 * Indicate an operation mode change
	 */
	public void onChangedOperationMode(String newOperationMode);

	/**
	 * Indicate an work state change
	 */
	public void onChangedWorkState(String newWorkState);

	/**
	 * Indicate an error state change
	 */
	public void onChangedErrorState(String newWorkState);
}
