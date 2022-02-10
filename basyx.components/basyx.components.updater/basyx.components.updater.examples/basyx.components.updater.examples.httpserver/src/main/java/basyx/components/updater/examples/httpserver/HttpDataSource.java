/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.examples.httpserver;

public class HttpDataSource {
	private HttpServer server;
	
	public void runHttpServer() throws InterruptedException {
		DummyServlet servlet = new DummyServlet();
		server = new HttpServer(8091, "localhost", "", servlet);
		server.start();
	}
	
	public void stopHttpServer() {
		server.stop();
	}
}
