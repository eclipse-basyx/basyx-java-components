/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.core.delegator.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.CamelContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import basyx.components.updater.core.delegator.response.DelegatorResponse;

/**
 * An Http servlet for a delegator.
 * Describes the functionality after receiving an get request
 * @author haque
 *
 */
public class DelegatorServlet extends HttpServlet {
	private static final long serialVersionUID = 4918478763760299634L;
	private DelegatorResponse response;
	private CamelContext context;
	private Gson gson;
	
	public DelegatorServlet(CamelContext context) {
		this.context = context;
		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	}

	@Override
    protected void doGet(
      HttpServletRequest req, 
      HttpServletResponse resp) throws IOException {
		this.response = new DelegatorResponse();
		
		context.start();
		waitForMessageToBeReceived();
		context.stop();
		
		setAPIResponseProperty(resp);
		setMessageToResponse(response, resp);
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
	private void setMessageToResponse(DelegatorResponse response, HttpServletResponse resp) throws IOException {
		String jsonString = gson.toJson(response);
        PrintWriter out = resp.getWriter();
        out.print(jsonString);
        out.flush();
        out.close();
	}
	
	/**
	 * Waits for message to be received
	 */
	private void waitForMessageToBeReceived() {
		while (!response.isMessageReceived()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void processMessage(String msg) {
		if (!response.isMessageReceived()) {
			this.response.setValue(msg);
			this.response.setMessageReceived(true);	
		}
	}
}
