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

import org.eclipse.basyx.models.controlcomponent.ControlComponent;
import org.eclipse.basyx.models.controlcomponent.ControlComponentChangeListener;
import org.eclipse.basyx.models.controlcomponent.ExecutionMode;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;
import org.eclipse.basyx.models.controlcomponent.OccupationState;
import org.eclipse.basyx.models.controlcomponent.SimpleControlComponent;

/**
 * Base class for BaSys smart devices
 * 
 * This base class implements a control component for a smart device with a
 * SimpleControlComponent instance
 * 
 * @author kuhn
 *
 */
public abstract class BaseSmartDevice extends BaseDevice implements ControlComponentChangeListener, IBaSysNativeDeviceStatus {

	/**
	 * Device control component
	 */
	protected ControlComponent controlComponent = null;

	/**
	 * Initializes BaseSmartDevice with a SimpleControlComponent
	 */
	public BaseSmartDevice() {
		// Create control component
		controlComponent = new SimpleControlComponent(true);
		// - Register this component as event listener
		controlComponent.addControlComponentChangeListener(this);
	}

	/**
	 * Initializes BaseSmartDevice with an arbitary {@link ControlComponent}
	 * 
	 * @param component
	 */
	public BaseSmartDevice(ControlComponent component) {
		controlComponent = component;
		component.addControlComponentChangeListener(this);
	}

	/**
	 * Start smart device
	 */
	@Override
	public void start() {
	}

	/**
	 * Get control component instance
	 */
	public ControlComponent getControlComponent() {
		// Return control component instance
		return controlComponent;
	}

	/**
	 * Indicate device status change
	 */
	@Override
	public void statusChange(String newStatus) {
		// Change control component execution status
		controlComponent.setExecutionState(newStatus);
	}

	/**
	 * Smart device control component indicates a variable change
	 */
	@Override
	public void onVariableChange(String varName, Object newValue) {
	}

	/**
	 * Smart device control component indicates an occupier change
	 */
	@Override
	public void onNewOccupier(String occupierId) {
	}

	/**
	 * Smart device control component indicates an occupation state change
	 */
	@Override
	public void onNewOccupationState(OccupationState state) {
	}

	/**
	 * Smart device control component indicates an execution mode change
	 */
	@Override
	public void onChangedExecutionMode(ExecutionMode newExecutionMode) {
	}

	/**
	 * Smart device control component indicates an execution state change
	 */
	@Override
	public void onChangedExecutionState(ExecutionState newExecutionState) {
		// Indicate service start in "Executing" state
		if (newExecutionState == ExecutionState.EXECUTE)
			this.onServiceInvocation();
	}

	/**
	 * Smart device control component indicates an operation mode change
	 */
	@Override
	public void onChangedOperationMode(String newOperationMode) {
	}

	/**
	 * Smart device control component indicates a work state change
	 */
	@Override
	public void onChangedWorkState(String newWorkState) {
	}

	/**
	 * Smart device control component indicates an error state change
	 */
	@Override
	public void onChangedErrorState(String newWorkState) {
	}
}
