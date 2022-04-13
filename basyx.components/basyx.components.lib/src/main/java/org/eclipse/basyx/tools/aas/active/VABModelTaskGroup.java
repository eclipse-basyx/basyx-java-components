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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Provides management for execution of multiple {@link VABModelTask}s.
 * 
 * @author espen, schnicke
 *
 */
public class VABModelTaskGroup {
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> currentSchedule;
	private int updateInterval = 1000;

	private List<VABModelTask> tasks = new LinkedList<>();
	private IModelProvider modelProvider;

	public VABModelTaskGroup(IModelProvider modelProvider) {
		this.modelProvider = modelProvider;
	}

	/**
	 * Changes the associated model provider. The contained tasks refer to this new
	 * provider from now on
	 * 
	 * @param newProvider
	 *            A reference to a model provider the scheduled task shall refer to
	 * @return A reference to this task group
	 */
	public synchronized VABModelTaskGroup setModelProvider(IModelProvider newProvider) {
		this.modelProvider = newProvider;
		return this;
	}

	/**
	 * 
	 * @param task
	 * @return A reference to this task group
	 */
	public synchronized VABModelTaskGroup addTask(VABModelTask task) {
		tasks.add(task);
		return this;
	}

	/**
	 * 
	 * @param newInterval
	 * @return A reference to this task group
	 */
	public synchronized VABModelTaskGroup setUpdateInterval(int newInterval) {
		this.updateInterval = newInterval;
		if (isRunning()) {
			stop();
			start();
		}
		return this;
	}

	/**
	 * Starts scheduling the contained tasks at the configured update rate. If no
	 * update rate has been set before, it assumes a default rate of one execution
	 * per second. Does nothing if already started.
	 */
	public synchronized void start() {
		if (!isRunning()) {
			currentSchedule = executor.scheduleAtFixedRate(this::update, updateInterval, updateInterval, TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * Stops the groups' current task schedules. Does nothing if the group has been
	 * stopped before or has not been started yet.
	 */
	public synchronized void stop() {
		if (isRunning()) {
			currentSchedule.cancel(false);
			currentSchedule = null;
		}
	}

	/**
	 * Checks, whether this group is currently running or not.
	 * 
	 * @return True, if the tasks of this group are currently scheduled. False, if
	 *         not.
	 */
	public synchronized boolean isRunning() {
		return currentSchedule != null;
	}

	protected synchronized void update() {
		for (VABModelTask task : tasks) {
			try {
				task.execute(modelProvider);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Stops the groups' tasks and frees its resources, so that no tasks can be
	 * scheduled after calling this method
	 */
	public synchronized void clear() {
		executor.shutdown();
		currentSchedule = null;
	}
}
