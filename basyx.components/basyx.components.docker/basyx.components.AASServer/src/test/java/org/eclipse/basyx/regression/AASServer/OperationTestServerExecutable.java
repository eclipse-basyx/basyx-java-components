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
package org.eclipse.basyx.regression.AASServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.submodel.restapi.OperationProvider;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxHTTPServer;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;

/**
 * This class provides an operation on a test server that can be delegated to.
 * The operation takes an integer and returns a boolean that indicates whether
 * the input value is odd (false) or even (true).
 * 
 * In order to test the delegation operation externally, this class has a main
 * method that executes this test server.
 * 
 * @author jungjan
 *
 */
public class OperationTestServerExecutable {
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 4002;
	private static final String DEFAULT_OPERATION_ID = "test_operation";
	private BaSyxHTTPServer operationTestServer;

	public OperationTestServerExecutable() {
		operationTestServer = new BaSyxHTTPServer(makeDefaultContext(DEFAULT_HOST, DEFAULT_PORT));
	}

	public OperationTestServerExecutable(String host, int port) {
		operationTestServer = new BaSyxHTTPServer(makeDefaultContext(host, port));
	}

	private static BaSyxContext makeDefaultContext(String host, int port) {
		Collection<OperationVariable> in = makeInputInteger(0);
		Collection<OperationVariable> out = makeOutputBoolean(false);
		Function<Object[], Object> isEven = (Function<Object[], Object>) v -> {
			return (Integer) v[0] % 2 == 0;
		};

		Operation operation = makeOperation(in, out, isEven);
		OperationProvider operationProvider = new OperationProvider(new VABLambdaProvider(operation));
		BaSyxContext context = new BaSyxContext("/" + "operation", "", host, port);
		return context.addServletMapping("/" + DEFAULT_OPERATION_ID + "/*", new VABHTTPInterface<IModelProvider>(operationProvider));
	}

	private static Collection<OperationVariable> makeInputInteger(int i) {
		Collection<OperationVariable> in = new ArrayList<>();

		Property inProp = new Property("in", i);
		inProp.setKind(ModelingKind.TEMPLATE);
		in.add(new OperationVariable(inProp));
		return in;
	}

	private static Collection<OperationVariable> makeOutputBoolean(boolean bool) {
		Collection<OperationVariable> out = new ArrayList<>();

		Property outProp = new Property("out", bool);
		outProp.setKind(ModelingKind.TEMPLATE);
		out.add(new OperationVariable(outProp));
		return out;
	}

	private static Operation makeOperation(Collection<OperationVariable> in, Collection<OperationVariable> out, Function<Object[], Object> function) {
		Operation operation = new Operation(DEFAULT_OPERATION_ID);
		operation.setInputVariables(in);
		operation.setOutputVariables(out);
		operation.setInvokable(function);
		return operation;
	}

	/**
	 * Execute this, to externally test the operation delegation functionality.
	 * 
	 * To customize the host and port, pass these values as args. Else the sever
	 * will run on local host at port 4002.
	 * 
	 * @param args
	 *            (optionally) custom host and port. Expected values: args[0]: host
	 *            args[1]: port
	 */
	public static void main(String[] args) {
		OperationTestServerExecutable server;
		if (args.length > 1) {
			server = new OperationTestServerExecutable(args[0], Integer.parseInt(args[1]));
		} else {
			server = new OperationTestServerExecutable();
		}

		server.start();
	}

	private void start() {
		operationTestServer.start();
	}

}
