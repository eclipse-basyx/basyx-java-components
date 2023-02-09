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

package org.eclipse.digitaltwin.basyx.submodelrepository.http;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;

/**
 * Tests the SubmodelElement specific parts of the SubmodelRepository HTTP/REST
 * API
 * 
 * @author schnicke, danish
 *
 */
public class TestSubmodelRepositorySubmodelElementsHTTP {
	private ConfigurableApplicationContext appContext;

	@Before
	public void startAASRepo() throws Exception {
		appContext = new SpringApplication(DummySubmodelRepositoryComponent.class).run(new String[] {});
	}

	@After
	public void shutdownAASRepo() {
		appContext.close();
	}

	@Test
	public void getSubmodelElements() throws FileNotFoundException, IOException, ParseException {
		String id = DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID;
		String requestedSubmodelElements = requestSubmodelElementsJSON(id);

		String submodelElementJSON = getSubmodelElementsJSON();
		BaSyxHttpTestUtils.assertSameJSONContent(submodelElementJSON, requestedSubmodelElements);
	}

	@Test
	public void getSubmodelElementsOfNonExistingSubmodel() throws ParseException, IOException {
		CloseableHttpResponse response = requestSubmodelElements("nonExisting");
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void getSubmodelElement() throws FileNotFoundException, IOException, ParseException {
		String expectedElement = getSubmodelElementJSON();
		CloseableHttpResponse response = requestSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());
		BaSyxHttpTestUtils.assertSameJSONContent(expectedElement, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getSubmodelElementOfNonExistingSubmodel() throws FileNotFoundException, IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElement("nonExisting", "doesNotMatter");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void getNonExistingSubmodelElement() throws FileNotFoundException, IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElement(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, "nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void getPropertyValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = FileUtils.readFileToString(new File(getClass().getClassLoader().getResource("value/expectedPropertyValue.json").getFile()), StandardCharsets.UTF_8);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void getNonExistingSubmodelElementValue() throws IOException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, "nonExisting");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void getSubmodelElementValueOfNonExistingSubmodel() throws IOException {
		CloseableHttpResponse response = requestSubmodelElementValue("nonExisting", "doesNotMatter");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void setPropertyValue() throws IOException, ParseException {
		String valueToWrite = "200";
		String expectedValue = FileUtils.readFileToString(new File(getClass().getClassLoader().getResource("value/expectedPropertySetValue.json").getFile()), StandardCharsets.UTF_8);

		CloseableHttpResponse writeResponse = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT, valueToWrite);
		assertEquals(HttpStatus.OK.value(), writeResponse.getCode());

		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_PROPERTY_ID_SHORT);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	@Test
	public void setNonExistingSubmodelElementValue() throws IOException {
		CloseableHttpResponse response = writeSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, "nonExisting", "doesNotMatter");
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}

	@Test
	public void setSubmodelElementValueOfNonExistingSubmodel() throws IOException {
		CloseableHttpResponse response = writeSubmodelElementValue("nonExisting", "doesNotMatter", "doesNotMatter");

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
	}
	
	@Test
	public void getRangeValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_RANGE_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = FileUtils.readFileToString(new File(getClass().getClassLoader().getResource("value/expectedRangeValue.json").getFile()), StandardCharsets.UTF_8);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}
	
	@Test
	public void getMultiLanguagePropertyValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_MULTI_LANG_PROP_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = FileUtils.readFileToString(new File(getClass().getClassLoader().getResource("value/expectedMultiLanguagePropertyValue.json").getFile()), StandardCharsets.UTF_8);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}
	
	@Test
	public void getFileValue() throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElementValue(DummySubmodelFactory.SUBMODEL_TECHNICAL_DATA_ID, SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT);

		assertEquals(HttpStatus.OK.value(), response.getCode());

		String expectedValue = FileUtils.readFileToString(new File(getClass().getClassLoader().getResource("value/expectedFileValue.json").getFile()), StandardCharsets.UTF_8);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedValue, BaSyxHttpTestUtils.getResponseAsString(response));
	}

	private CloseableHttpResponse writeSubmodelElementValue(String submodelId, String smeIdShort, String value) throws IOException {
		String wrappedValue = wrapStringValue(value);

		return BaSyxHttpTestUtils.executePutOnURL(createSubmodelElementValueURL(submodelId, smeIdShort), wrappedValue);
	}

	private String wrapStringValue(String value) {
		// The value needs to be wrapped to ensure that it is correctly identified as
		// string and not parsed to another primitive, e.g., int
		return "\"" + value + "\"";
	}


	private CloseableHttpResponse requestSubmodelElementValue(String submodelId, String smeIdShort) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createSubmodelElementValueURL(submodelId, smeIdShort));

	}

	private String requestSubmodelElementsJSON(String id) throws IOException, ParseException {
		CloseableHttpResponse response = requestSubmodelElements(id);

		return BaSyxHttpTestUtils.getResponseAsString(response);
	}

	private CloseableHttpResponse requestSubmodelElement(String submodelId, String smeIdShort) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createSpecificSubmodelElementURL(submodelId, smeIdShort));
	}

	private String createSubmodelElementValueURL(String submodelId, String smeIdShort) {
		return createSpecificSubmodelElementURL(submodelId, smeIdShort) + "?content=value";
	}

	private String createSpecificSubmodelElementURL(String submodelId, String smeIdShort) {
		return createSubmodelElementsURL(submodelId) + "/" + smeIdShort;
	}

	private String createSubmodelElementsURL(String submodelId) {
		return BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(submodelId) + "/submodel-elements";
	}

	private CloseableHttpResponse requestSubmodelElements(String submodelId) throws IOException {
		return BaSyxHttpTestUtils.executeGetOnURL(createSubmodelElementsURL(submodelId));
	}

	private String getSubmodelElementsJSON() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:SubmodelElements.json");
	}

	private String getSubmodelElementJSON() throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromFile("classpath:SubmodelElement.json");
	}
}
