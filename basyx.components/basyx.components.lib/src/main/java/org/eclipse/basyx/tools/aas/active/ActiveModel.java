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
package org.eclipse.basyx.tools.aas.active;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

public class ActiveModel {
	private Set<VABModelTaskGroup> groups = new HashSet<>();
	private IModelProvider modelProvider;

	public ActiveModel(IModelProvider modelProvider) {
		this.modelProvider = modelProvider;
	}

	/**
	 * Runs a task with a specific update rate in a new task group.
	 * 
	 * @param updateInterval
	 *            The interval in which the task is updated
	 * @param task
	 *            The task that is scheduled
	 * @return The resulting task group in which the task is contained
	 */
	public VABModelTaskGroup runTask(int updateInterval, VABModelTask task) {
		VABModelTaskGroup group = new VABModelTaskGroup(modelProvider);
		groups.add(group);
		group.setUpdateInterval(updateInterval).addTask(task).start();
		return group;
	}

	/**
	 * Creates a new task group and associated it with this active model.
	 * 
	 * @return The created task group
	 */
	public VABModelTaskGroup createTaskGroup() {
		VABModelTaskGroup group = new VABModelTaskGroup(modelProvider);
		groups.add(group);
		return group;
	}

	/**
	 * Shuts all running threads down and removes all groups.
	 */
	public void clear() {
		for (VABModelTaskGroup group : groups) {
			group.clear();
		}
		groups.clear();
	}

	/**
	 * Getter for all containing task groups.
	 * 
	 * @return A set of all task groups that have been created in this model
	 */
	public Set<VABModelTaskGroup> getTaskGroups() {
		return groups;
	}

	/**
	 * Stops all running tasks in the contained groups.
	 */
	public void stopAll() {
		for (VABModelTaskGroup group : groups) {
			group.stop();
		}
	}

	/**
	 * Starts all running tasks in the contained groups.
	 */
	public void startAll() {
		for (VABModelTaskGroup group : groups) {
			group.start();
		}
	}
}
