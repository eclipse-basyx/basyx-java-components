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
package org.eclipse.basyx.regression.support.processengine.stubs;

import org.camunda.bpm.engine.impl.pvm.runtime.ExecutionImpl;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

/**
 * A process instance stub which is BPMNEngineStub for test purpose
 * 
 * @author zhangzai
 *
 */
public class ProcessInstanceStub extends ExecutionImpl {

	private static final long serialVersionUID = 1L;
	/**
	 * Id of the process instance
	 */
	private String processInstanceId = "process-instance-stub-01";

	/**
	 * process definition id
	 */
	private String getProcessDefinitionId = "process-definition-stub-01";

	/**
	 * Bpmn model instance
	 */
	private BpmnModelInstance modelInstance;

	/**
	 * Get process instance Id
	 */
	@Override
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	/**
	 * Get Process definition Id
	 */
	@Override
	public String getProcessDefinitionId() {
		return getProcessDefinitionId;
	}

	/**
	 * get Bpmn Model instance
	 */
	@Override
	public BpmnModelInstance getBpmnModelInstance() {
		modelInstance = new BPMNModelFactory().create(getProcessDefinitionId);
		return modelInstance;
	}

	@Override
	public String getProcessBusinessKey() {
		return "Process Business Key 01";
	}

	@Override
	public String getCurrentActivityName() {
		return "test-activity-01";
	}

	/**
	 * Unused functions
	 */
	@Override
	public FlowElement getBpmnModelElementInstance() {
		ModelElementInstance element = modelInstance.getModelElementById("t1");
		return (FlowElement) element;
	}

}
