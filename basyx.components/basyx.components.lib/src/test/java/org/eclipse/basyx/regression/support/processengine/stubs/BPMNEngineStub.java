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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.eclipse.basyx.components.processengine.connector.DeviceServiceDelegate;

/**
 * BPMNStub that invokes the JavaDelegate through the reflection A test that
 * ensures the right parameters are given to the DeviceServiceExecutor
 * 
 * @author Zhang, Zai
 */
public class BPMNEngineStub {

	private Map<String, String> fieldInjections = new HashMap<>();

	private String classPath = "org.eclipse.basyx.components.processengine.connector.DeviceServiceDelegate";

	/**
	 * Constructor parameters are value of the injected fields in String
	 * 
	 * @param serviceParameter
	 *            all requested parameters in a serialized Json-String
	 */
	public BPMNEngineStub(String serviceName, String serviceProvider, String serviceParameter, String submodelid) {
		fieldInjections.put("serviceName", serviceName);
		fieldInjections.put("serviceProvider", serviceProvider);
		fieldInjections.put("serviceParameter", serviceParameter);
		fieldInjections.put("submodelId", submodelid);
	}

	public void callJavaDelegate() throws Exception {
		// create the class of the java-delegate
		@SuppressWarnings("rawtypes")
		Class clazz = Class.forName(classPath);
		DeviceServiceDelegate delegate = (DeviceServiceDelegate) clazz.newInstance();

		// get all declare field of the class, including private fields
		Field fields[] = clazz.getDeclaredFields();

		// set expected value defined in the hashMap to each fields
		for (Field f : fields) {
			if (fieldInjections.containsKey(f.getName())) {

				// get expected value
				String value = fieldInjections.get(f.getName());

				// create expression for this field
				Expression serviceName_expression = createExpressionObejct(value);

				// set the private field accesable
				f.setAccessible(true);

				// set value to the field
				f.set(delegate, serviceName_expression);
			}
		}

		// create execution-stub
		// DelegateExecution execution = new ExecutionImpl();
		DelegateExecution execution = new ProcessInstanceStub();

		// call the delegate function
		delegate.execute(execution);
	}

	/**
	 * create the Expression-instance for the delegate
	 */
	private Expression createExpressionObejct(String value) {
		Expression ep = new FixedValue(value);
		return ep;
	}

}
