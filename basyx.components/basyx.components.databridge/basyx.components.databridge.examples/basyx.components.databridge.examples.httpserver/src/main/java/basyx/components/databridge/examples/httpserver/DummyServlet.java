/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.databridge.examples.httpserver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DummyServlet extends HttpServlet {
	private static final long serialVersionUID = 4918478763760299634L;

	@Override
    protected void doGet(
      HttpServletRequest req, 
      HttpServletResponse resp) throws IOException {
		setAPIResponseProperty(resp);
		setMessageToResponse(resp);
    }
	
	/**
	 * Sets {@link HttpServletResponse} properties
	 * @param resp
	 */
	private void setAPIResponseProperty(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
	}
	
	/**
	 * Sets retrieved message as a response
	 * @param response
	 * @param resp
	 * @throws IOException
	 */
	private void setMessageToResponse(HttpServletResponse resp) throws IOException {
		String jsonString = "{\"Account\":{\"Account Name\":\"Firefly\",\"Order\":[{\"OrderID\":\"order103\",\"Product\":[{\"Product Name\":\"Bowler Hat\",\"ProductID\":858383,\"SKU\":\"0406654608\",\"Description\":{\"Colour\":\"Purple\",\"Width\":300,\"Height\":200,\"Depth\":210,\"Weight\":0.75},\"Price\":34.45,\"Quantity\":2},{\"Product Name\":\"Trilby hat\",\"ProductID\":858236,\"SKU\":\"0406634348\",\"Description\":{\"Colour\":\"Orange\",\"Width\":300,\"Height\":200,\"Depth\":210,\"Weight\":0.6},\"Price\":21.67,\"Quantity\":1}]},{\"OrderID\":\"order104\",\"Product\":[{\"Product Name\":\"Bowler Hat\",\"ProductID\":858383,\"SKU\":\"040657863\",\"Description\":{\"Colour\":\"Purple\",\"Width\":300,\"Height\":200,\"Depth\":210,\"Weight\":0.75},\"Price\":34.45,\"Quantity\":4},{\"ProductID\":345664,\"SKU\":\"0406654603\",\"Product Name\":\"Cloak\",\"Description\":{\"Colour\":\"Black\",\"Width\":30,\"Height\":20,\"Depth\":210,\"Weight\":2},\"Price\":107.99,\"Quantity\":1}]}]}}";
        PrintWriter out = resp.getWriter();
        out.print(jsonString);
        out.flush();
        out.close();
	}
}
