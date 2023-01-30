/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.basyx.regression.AASServer.mongodb;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.components.aas.mongodb.MongoDBSubmodelAPI;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.junit.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Tests the ISubmodelAPI implementation of the MongoDB backend
 * 
 * @author schnicke
 */
public class TestMongoDBSubmodelAPI {

	@Test
	public void writeAndReadMultiLanguageProperty() {
		MongoDBSubmodelAPI submodelAPI = createAPIWithPreconfiguredSubmodel();

		MultiLanguageProperty mlprop = new MultiLanguageProperty("myMLP");
		submodelAPI.addSubmodelElement(mlprop);

		LangStrings expected = new LangStrings("de", "Hallo!");
		submodelAPI.updateSubmodelElement(mlprop.getIdShort(), expected);

		Object value = submodelAPI.getSubmodelElementValue(mlprop.getIdShort());

		assertEquals(expected, value);
	}

	private MongoDBSubmodelAPI createAPIWithPreconfiguredSubmodel() {
		MongoClient client = MongoClients.create(new BaSyxMongoDBConfiguration().getConnectionUrl());
		MongoDBSubmodelAPI submodelAPI = new MongoDBSubmodelAPI("", client);

		Submodel mySM = new Submodel("mySubmodel", new CustomId("mySubmodelId"));

		submodelAPI.setSubmodel(mySM);
		return submodelAPI;
	}
}
