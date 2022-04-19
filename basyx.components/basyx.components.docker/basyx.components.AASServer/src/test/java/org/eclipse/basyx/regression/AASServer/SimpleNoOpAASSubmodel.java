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

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.testsuite.regression.submodel.restapi.SimpleAASSubmodel;

public class SimpleNoOpAASSubmodel extends SimpleAASSubmodel {

	public SimpleNoOpAASSubmodel() {
		this("SimpleAASSubmodel");
	}

	public SimpleNoOpAASSubmodel(String idShort) {
		super(idShort);

		// Remove operations
		deleteSubmodelElement("complex");
		deleteSubmodelElement("simple");
		deleteSubmodelElement("exception1");
		deleteSubmodelElement("exception2");

		Map<String, ISubmodelElement> elems = this.getSubmodelElements();
		SubmodelElementCollection root = (SubmodelElementCollection) elems.get("containerRoot");
		SubmodelElementCollection opContainer = (SubmodelElementCollection) root.getSubmodelElement("container");
		opContainer.deleteSubmodelElement("operationId");
		Operation opReplacement = new Operation("operationId");
		opContainer.addSubmodelElement(opReplacement);
	}

}
