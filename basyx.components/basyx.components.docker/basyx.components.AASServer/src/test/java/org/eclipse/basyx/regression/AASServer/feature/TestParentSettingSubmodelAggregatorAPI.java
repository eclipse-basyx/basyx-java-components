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


package org.eclipse.basyx.regression.AASServer.feature;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.components.aas.fileadaptation.ParentSettingSubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Test;

/**
 * 
 * @author schnicke
 *
 */
public class TestParentSettingSubmodelAggregatorAPI {

	@Test
	public void parentAddedIfNoParent() {
		IIdentifier aasIdentifier = new CustomId("aasId");
		ParentSettingSubmodelAggregator aggregator = new ParentSettingSubmodelAggregator(new SubmodelAggregator(), aasIdentifier);

		Submodel sm = createSubmodelWithNoParent();
		aggregator.createSubmodel(sm);

		ISubmodel retrieved = aggregator.getSubmodel(sm.getIdentification());
		assertEquals(getAasReference(aasIdentifier), retrieved.getParent());
	}

	private Submodel createSubmodelWithNoParent() {
		return new Submodel("sm", new CustomId("smId"));
	}

	private IReference getAasReference(IIdentifier aasIdentifier) {
		return new Reference(new Key(KeyElements.ASSETADMINISTRATIONSHELL, false, aasIdentifier.getId(), aasIdentifier.getIdType()));
	}

}
