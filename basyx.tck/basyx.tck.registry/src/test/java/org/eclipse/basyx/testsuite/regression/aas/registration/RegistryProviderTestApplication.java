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
package org.eclipse.basyx.testsuite.regression.aas.registration;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * The main class that executes the concrete test suite of the registry provider
 * 
 * @author zhangzai
 *
 */
public class RegistryProviderTestApplication {


	/**
	 * Run the test suite of the registry provider with inserted url
	 * 
	 * @param args - URL inserted by the user
	 */
	public static void main(String[] args) {
		// First argument is the inserted url
		String url = args[0];

		// Run the junit tests
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));

		RegistryProviderSuiteWithDefinedURL.url = url;
		Result result = junit.run(RegistryProviderSuiteWithDefinedURL.class);

		System.out.println("Finished. Result: Failures: " +
				result.getFailureCount() + ". Ignored: " +
				result.getIgnoreCount() + ". Tests run: " +
				result.getRunCount() + ". Time: " +
				result.getRunTime() + "ms.");
	}
}
