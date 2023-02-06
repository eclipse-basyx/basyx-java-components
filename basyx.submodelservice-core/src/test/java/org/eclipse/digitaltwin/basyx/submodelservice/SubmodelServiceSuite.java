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

import java.util.Arrays;
import java.util.List;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Range;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangString;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.MultiLanguagePropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RangeValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.LangStringMapper;
import org.junit.Test;

/**
 * Testsuite for implementations of the SubmodelService interface
 * 
 * @author schnicke, danish
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
		SubmodelElement smElement = getSubmodelService(technicalData)
				.getSubmodelElement(SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(SubmodelServiceUtil.getDummySubmodelElement(technicalData,
				SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT), smElement);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getNonExistingSubmodelElement() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();

		getSubmodelService(technicalData).getSubmodelElement("nonExisting");
	}

	@Test
	public void getPropertyValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		String expected = ((Property) SubmodelServiceUtil.getDummySubmodelElement(technicalData,
				SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT)).getValue();

		Object submodelElementValue = getSubmodelService(technicalData)
				.getSubmodelElementValue(SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(expected, ((PropertyValue) submodelElementValue).getValue());
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
		smService.setSubmodelElementValue(SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT, expected);
		Object submodelElementValue = smService
				.getSubmodelElementValue(SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(expected, ((PropertyValue) submodelElementValue).getValue());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void setNonExistingSubmodelElementValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();

		getSubmodelService(technicalData).setSubmodelElementValue("nonExisting", "doesNotMatter");
	}

	@Test
	public void getRangeValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		
		String expectedMin = ((Range) SubmodelServiceUtil.getDummySubmodelElement(technicalData,
				SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT)).getMin();
		String expectedMax = ((Range) SubmodelServiceUtil.getDummySubmodelElement(technicalData,
				SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT)).getMax();

		Object submodelElementValue = getSubmodelService(technicalData)
				.getSubmodelElementValue(SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT);

		assertEquals(expectedMin, String.valueOf(((RangeValue) submodelElementValue).getMin()));

		assertEquals(expectedMax, String.valueOf(((RangeValue) submodelElementValue).getMax()));
	}

	@Test
	public void getMultiLanguagePropertyValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		
		List<LangStringMapper> expectedValue = Arrays.asList(new LangStringMapper(new DefaultLangString("Hello", "en")),
				new LangStringMapper(new DefaultLangString("Hallo", "de")));

		Object submodelElementValue = getSubmodelService(technicalData)
				.getSubmodelElementValue(SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT);

		assertEquals(expectedValue.get(0).getLanguage(),
				((MultiLanguagePropertyValue) submodelElementValue).getValue().get(0).getLanguage());
	}

	@Test
	public void getFileValue() {
		Submodel technicalData = DummySubmodelFactory.createTechnicalDataSubmodel();
		String expectedValue = ((File) SubmodelServiceUtil.getDummySubmodelElement(technicalData,
				SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT)).getValue();

		Object submodelElementValue = getSubmodelService(technicalData)
				.getSubmodelElementValue(SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);

		assertEquals(expectedValue, ((FileValue) submodelElementValue).getValue());
	}
}
