/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.regression.components.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.junit.Test;

/**
 * Tests parsing of context property files
 * 
 * @author danish
 *
 */
public class TestBaSyxContextConfigurationPropertyFileParsing {
	
	@Test
	public void withCORSConfiguration() {
		BaSyxContext context = createBasyxContext("context_with_cors_config.properties");
		
		String allowOrigin = "http://www.example.com";

		assertEquals(allowOrigin, context.getAccessControlAllowOrigin());
	}
	
	@Test
	public void withoutCORSConfiguration() {
		BaSyxContext context = createBasyxContext("context_without_cors_config.properties");
		
		assertNull(context.getAccessControlAllowOrigin());
	}
	
	private BaSyxContext createBasyxContext(String resourceFile) {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration(4001, "");
		contextConfig.loadFromResource(resourceFile);

		return contextConfig.createBaSyxContext();
	}
}
