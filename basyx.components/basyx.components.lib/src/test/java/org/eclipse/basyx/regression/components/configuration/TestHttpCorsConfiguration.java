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

import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.SimpleVABElementServlet;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.TestHttpCors;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;

/**
 * Tests HTTP CORS configuration
 * 
 * @author danish
 *
 */
public class TestHttpCorsConfiguration extends TestHttpCors {
	
	@Override
	protected void createAndStartHttpServerWithCORS(String accessControlAllowOrigin) {	
		BaSyxContext contextConfig = createBasyxContext(accessControlAllowOrigin);

		configureAndStartServer(contextConfig);
	}

	protected BaSyxContext createBasyxContext(String accessControlAllowOrigin) {
		BaSyxContextConfiguration basyxContextConfig = createBasyxContextConfiguration(accessControlAllowOrigin);
		
		BaSyxContext context = basyxContextConfig.createBaSyxContext();
		context.addServletMapping("/shells/*", new SimpleVABElementServlet());
		
		return context;
	}

	private BaSyxContextConfiguration createBasyxContextConfiguration(String accessControlAllowOrigin) {
		BaSyxContextConfiguration basyxContextConfig = new BaSyxContextConfiguration(CONTEXT_PATH, DOCBASE_PATH, HOSTNAME, PORT);
		basyxContextConfig.setAccessControlAllowOrigin(accessControlAllowOrigin);
		return basyxContextConfig;
	}
	
}
