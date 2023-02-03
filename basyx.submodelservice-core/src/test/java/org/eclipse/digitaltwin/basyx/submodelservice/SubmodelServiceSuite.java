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

package org.eclipse.digitaltwin.basyx.submodelservice;

import static org.junit.Assert.assertEquals;

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.junit.Test;

/**
 * Testsuite for implementations of the SubmodelService interface
 * 
 * @author schnicke
 *
 */
public abstract class SubmodelServiceSuite {

	protected abstract SubmodelService getSubmodelService(Submodel submodel);
	
	@Test
	public void getSubmodel() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		SubmodelService smService = getSubmodelService(technicalData);

		assertEquals(technicalData, smService.getSubmodel());
	}

	@Test
	public void getSubmodelElements() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		SubmodelService smService = getSubmodelService(technicalData);
		
		assertEquals(technicalData.getSubmodelElements(), smService.getSubmodelElements());
	}
	
	@Test
	public void getSubmodelElement() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		SubmodelElement smElement = getSubmodelService(technicalData).getSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);
		
		assertEquals(getDummySubmodelElement(technicalData), smElement);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getNonExistingSubmodelElement() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();

		getSubmodelService(technicalData).getSubmodelElement("nonExisting");
	}

	@Test
	public void getDeepNestedSubmodelElement() {
		Submodel operationalData = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();
		String idShortPath = generateNestedIdShortPath();
		SubmodelElement submodelElement = getSubmodelService(operationalData).getSubmodelElement(idShortPath);
		assertEquals(DummySubmodelFactory.SUBMODEL_ELEMENT_FIRST_ID_SHORT, submodelElement.getIdShort());
	}

	@Test
	public void getHierachicalSubmodelElementValue() {
		Submodel operationalData = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();
		String idShortPath = generateIdShortPath();
		Object submodelElementValue = getSubmodelService(operationalData).getSubmodelElementValue(idShortPath);
		assertEquals(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_PROPERTY_VALUE, submodelElementValue);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getNonExistentHierachicalSubmodelElementValueThrowsException() {
		Submodel operationalData = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();
		String idShortPath = generateNonExistentIdShortPath();
		getSubmodelService(operationalData).getSubmodelElementValue(idShortPath);
	}

	@Test
	public void setHierachicalSubmodelElementValue() {
		String expected = "205";
		Submodel operationalData = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();
		String idShortPath = generateIdShortPath();
		Property submodelElement = (Property) getSubmodelService(operationalData).getSubmodelElement(idShortPath);
		submodelElement.setValue(expected);
		submodelElement = (Property) getSubmodelService(operationalData).getSubmodelElement(idShortPath);
		assertEquals(expected, submodelElement.getValue());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void setNonExistentHierachicalSubmodelElementValueThrowsException() {
		String expected = "205";
		Submodel operationalData = DummySubmodelFactory.createOperationalDataSubmodelWithHierarchicalSubmodelElements();
		String idShortPath = generateNonExistentIdShortPath();
		Property submodelElement = (Property) getSubmodelService(operationalData).getSubmodelElement(idShortPath);
		submodelElement.setValue(expected);
	}

	@Test
	public void getPropertyValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		String expected = getDummySubmodelElement(technicalData).getValue();
		
		Object value = getSubmodelService(technicalData).getSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);
		
		assertEquals(expected, value);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getNonExistingSubmodelElementValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();

		getSubmodelService(technicalData).getSubmodelElementValue("nonExisting");
	}

	@Test
	public void setPropertyValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		SubmodelService smService = getSubmodelService(technicalData);

		String expected = "200";
		smService.setSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT, expected);
		Object value = smService.getSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(expected, value);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void setNonExistingSubmodelElementValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();

		getSubmodelService(technicalData).setSubmodelElementValue("nonExisting", "doesNotMatter");
	}

	private Property getDummySubmodelElement(Submodel technicalData) {
		return (Property) technicalData.getSubmodelElements().stream()
				.filter(sme -> sme.getIdShort().equals(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT))
				.findAny().get();
	}

	private String generateIdShortPath() {
		return DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_COLLECTION_ID_SHORT + "."
				+ DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_LIST_ID_SHORT + "[0]";
	}

	private String generateNonExistentIdShortPath() {
		return DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_COLLECTION_ID_SHORT + "."
				+ DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_ELEMENT_LIST_ID_SHORT + "[1]";
	}

	private String generateNestedIdShortPath() {
		String idShortPath = DummySubmodelFactory.SUBMODEL_ELEMENT_COLLECTION_TOP + "."
				+ DummySubmodelFactory.SUBMODEL_ELEMENT_FIRST_LIST + "[0][0]."
				+ DummySubmodelFactory.SUBMODEL_ELEMENT_FIRST_ID_SHORT;
		return idShortPath;
	}
}
