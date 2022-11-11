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
package org.eclipse.basyx.regression.support.processengine.submodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.eclipse.basyx.regression.support.processengine.stubs.ICoilcar;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;

public class DeviceSubmodelFactory {
	public Submodel create(String id, ICoilcar coilcar) {
		// create a single value property
		Property property1 = new Property(0);
		property1.setIdShort("currentPosition");

		Property property2 = new Property(0);
		property2.setIdShort("lifterPosition");

		Property property3 = new Property(false);
		property3.setIdShort("physicalSpeed");

		// create 2 opertations
		Operation op1 = new Operation((Function<Object[], Object>) obj -> {
			return coilcar.liftTo((int) obj[0]);
		});
		op1.setInputVariables(Collections.singletonList(new OperationVariable(new Property("position", 0))));
		op1.setOutputVariables(Collections.singletonList(new OperationVariable(new Property("result", 0))));
		op1.setIdShort("liftTo");

		Operation op2 = new Operation((Function<Object[], Object>) obj -> {
			coilcar.moveTo((int) obj[0]);
			return true;
		});
		op2.setInputVariables(Collections.singletonList(new OperationVariable(new Property("position", 0))));
		op2.setOutputVariables(Collections.singletonList(new OperationVariable(new Property("result", false))));
		op2.setIdShort("moveTo");

		// create a list for defined operations
		List<Operation> opList = new ArrayList<>();
		opList.add(op1);
		opList.add(op2);
		// create a list for defined properties
		List<Property> propList = new ArrayList<>();
		propList.add(property1);
		propList.add(property2);
		propList.add(property3);
		// create the sub-model and add the property and operations to the sub-model
		Submodel sm = new Submodel(id, new Identifier(IdentifierType.CUSTOM, id + "Custom"));
		propList.forEach(sm::addSubmodelElement);
		opList.forEach(sm::addSubmodelElement);
		return sm;
	}
}
