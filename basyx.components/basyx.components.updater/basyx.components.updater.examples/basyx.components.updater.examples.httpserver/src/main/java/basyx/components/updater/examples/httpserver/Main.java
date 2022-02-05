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

public class Main {
	public static void main(String[] args) throws InterruptedException {
		DummyServlet servlet = new DummyServlet();
		HttpServer server = new HttpServer(1111, "localhost", "", servlet);
		server.start();
		Thread.sleep(1000000);
	}
}
