/*******************************************************************************
 * Copyright (C) 2022 DFKI GmbH
 * Author: Gerhard Sonnenberg (gerhard.sonnenberg@dfki.de)
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
package org.eclipse.basyx.aas.registry.service.tests.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.eclipse.basyx.aas.registry.client.api.AasRegistryPaths;
import org.eclipse.basyx.aas.registry.events.RegistryEventSink;
import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorQuery;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorSearchRequest;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorSearchResponse;
import org.eclipse.basyx.aas.registry.model.SubmodelDescriptor;
import org.eclipse.basyx.aas.registry.service.api.BasyxRegistryApiDelegate;
import org.eclipse.basyx.aas.registry.service.api.RegistryApiController;
import org.eclipse.basyx.aas.registry.service.configuration.InMemoryAasStorageConfiguration;
import org.eclipse.basyx.aas.registry.service.storage.AasDescriptorNotFoundException;
import org.eclipse.basyx.aas.registry.service.storage.AasRegistryStorage;
import org.eclipse.basyx.aas.registry.service.tests.TestResourcesLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { BasyxRegistryApiDelegate.class, RegistryApiController.class, InMemoryAasStorageConfiguration.class })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = { "registry.type=inMemory" })
public class BasyxRegistryApiDelegateTest {

	private static final String ID_3 = "identification_3";

	private static final String ID_2_1 = "identification_2.1";

	private static final String ID_1 = "identification_1";

	private static final String ID_2_4 = "2.4";

	private static final String ID_UNKNOWN = "unknown";

	private static final String ID_2 = "identification_2";

	private static final String ID_2_3 = "identification_2.3";

	@MockBean
	private RegistryEventSink listener;
	
	@Autowired
	private AasRegistryStorage storage;

	@Autowired
	private RegistryApiController controller;

	@Rule
	public TestResourcesLoader testResourcesLoader = new TestResourcesLoader();

	@Before
	public void initStorage() throws IOException {		
		testResourcesLoader.loadRepositoryDefinition().forEach(storage::addOrReplaceAasDescriptor);
	}
	
	@After
	public void clearStorage() {
		storage.clear();
	}

	@Test
	public void whenDeleteAssetAdministrationShellDescriptorByIdNullArg_thenNoContent() {
		ResponseEntity<Void> response = controller.deleteAssetAdministrationShellDescriptorById(null);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void whenDeleteAssetAdministrationShellDescriptorById_thenNoContent() {
		ResponseEntity<Void> response = controller.deleteAssetAdministrationShellDescriptorById(encode(ID_1));
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void whenDeleteSubmodelDescriptorById_thenNoContent() {
		ResponseEntity<Void> response = controller.deleteSubmodelDescriptorById(encode(ID_2), encode(ID_2_1));
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void whenDeleteSubmodelDescriptorByIdUnknownAasId_thenNoContent() {
		ResponseEntity<Void> response = controller.deleteSubmodelDescriptorById(encode(ID_UNKNOWN), encode(ID_2_1));
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}


	@Test
	public void whenGetAllAssetAdministrationShellDescriptors_thenRepoContent() throws IOException {
		List<AssetAdministrationShellDescriptor> repoContent = testResourcesLoader.loadRepositoryDefinition();
		ResponseEntity<List<AssetAdministrationShellDescriptor>> response = controller.getAllAssetAdministrationShellDescriptors();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).asList().containsExactlyInAnyOrderElementsOf(repoContent);
	}

	@Test
	public void whenGetAllSubmodelDescriptorsNullArgs_thenThrowNullPointer() {
		assertThrows(NullPointerException.class, () -> controller.getAllSubmodelDescriptors(null));
	}

	@Test
	public void whenGetAllSubmodelDescriptorsUnknownDescriptor_thenNotFound() {
		assertThrows(AasDescriptorNotFoundException.class, () -> controller.getAllSubmodelDescriptors(encode(ID_UNKNOWN)));
	}

	@Test
	public void whenGetAllSubmodelDescriptorsKnownDescriptor_thenOk() throws IOException {
		List<SubmodelDescriptor> expected = testResourcesLoader.loadSubmodelList();
		ResponseEntity<List<SubmodelDescriptor>> response = controller.getAllSubmodelDescriptors(encode(ID_2));
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).containsExactlyInAnyOrderElementsOf(expected);
	}

	@Test
	public void whenGetAssetAdministrationShellDescriptorByIdNullArg_thenNullPointer() {
		assertThrows(NullPointerException.class, () -> controller.getAssetAdministrationShellDescriptorById(null));
	}

	@Test
	public void whenGetAssetAdministrationShellDescriptorByIdUnknown_thenNotFound() {
		assertThrows(AasDescriptorNotFoundException.class, ()-> controller.getAssetAdministrationShellDescriptorById(encode(ID_UNKNOWN)));
	}

	@Test
	public void whenGetAssetAdministrationShellDescriptorById_thenOk() throws IOException {
		AssetAdministrationShellDescriptor expected = testResourcesLoader.loadAssetAdminShellDescriptor();
		ResponseEntity<AssetAdministrationShellDescriptor> response = controller.getAssetAdministrationShellDescriptorById(encode(ID_2));
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(expected);
	}

	@Test
	public void whenGetSubmodelDescriptorByIdNullArgs_thenNullPointer() {
		assertThrows(NullPointerException.class, () -> controller.getSubmodelDescriptorById(null, null));
	}

	@Test
	public void whenGetSubmodelDescriptorById_thenOk() throws IOException {
		SubmodelDescriptor expected = testResourcesLoader.loadSubmodel();
		ResponseEntity<SubmodelDescriptor> response = controller.getSubmodelDescriptorById(encode(ID_2), encode(ID_2_1));
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(expected);
	}

	@Test
	public void whenGetSubmodelDescriptorByIdUnknown_thenNotFound() {
		assertThrows(AasDescriptorNotFoundException.class, ()-> controller.getSubmodelDescriptorById(encode(ID_UNKNOWN), encode(ID_UNKNOWN)));
	}

	@Test
	public void whenPostSubmodelDescriptorNullArgs_thenNullPointer() {
		assertThrows(NullPointerException.class, () -> controller.postSubmodelDescriptor(null, null));
	}

	@Test
	public void whenPostSubmodelDescriptor_thenCreated() throws IOException {
		SubmodelDescriptor input = testResourcesLoader.loadSubmodel("input");
		ResponseEntity<SubmodelDescriptor> response = controller.postSubmodelDescriptor(encode(ID_2), input);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(input);
		ResponseEntity<List<SubmodelDescriptor>> all = controller.getAllSubmodelDescriptors(encode(ID_2));
		List<SubmodelDescriptor> expected = testResourcesLoader.loadSubmodelList();
		assertThat(all.getBody()).isEqualTo(expected);
	}

	@Test
	public void whenPostSubmodelDescriptorUnknownAasId_thenNotFound() throws IOException {
		SubmodelDescriptor input = new SubmodelDescriptor();
		input.setIdentification("4.3");
		assertThrows(AasDescriptorNotFoundException.class, ()-> controller.postSubmodelDescriptor(encode(ID_UNKNOWN), input));
	}

	@Test
	public void whenPutAssetAdministrationShellDescriptorById_thenNoContent() throws IOException {
		AssetAdministrationShellDescriptor descriptor = testResourcesLoader.loadAssetAdminShellDescriptor();
		ResponseEntity<Void> response = controller.putAssetAdministrationShellDescriptorById(encode(ID_3), descriptor);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<AssetAdministrationShellDescriptor> stored = controller.getAssetAdministrationShellDescriptorById(encode(ID_3));
		assertThat(descriptor).isEqualTo(stored.getBody());
	}

	@Test
	public void whenPutSubmodelDescriptorDescriptorById_thenNoContent() throws IOException {
		SubmodelDescriptor input = new SubmodelDescriptor();
		input.setIdentification(ID_2_3);
		ResponseEntity<Void> response = controller.putSubmodelDescriptorById(encode(ID_2), encode(ID_2_3), input);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<SubmodelDescriptor> stored = controller.getSubmodelDescriptorById(encode(ID_2), encode(ID_2_3));
		assertThat(input).isEqualTo(stored.getBody());
	}

	@Test
	public void whenPutSubmodelDescriptorDescriptorByIdUnknownParent_thenNotFound() throws IOException {
		SubmodelDescriptor input = new SubmodelDescriptor();
		input.setIdentification(ID_2_3);
		assertThrows(AasDescriptorNotFoundException.class, ()-> controller.putSubmodelDescriptorById(encode(ID_UNKNOWN), encode(ID_2_3), input));
	}

	@Test
	public void whenPutSubmodelDescriptorDescriptorByIdDifferentIds_thenBadRequest() throws IOException {
		SubmodelDescriptor input = new SubmodelDescriptor();
		input.setIdentification(ID_2_3);
		ResponseEntity<Void> response = controller.putSubmodelDescriptorById(encode(ID_2), encode(ID_2_4), input);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void whenPutAssetAdministrationShellDescriptorByIdDifferentIds_thenBadRequest() throws IOException {
		AssetAdministrationShellDescriptor input = new AssetAdministrationShellDescriptor();
		input.setIdentification(ID_2);
		ResponseEntity<Void> response = controller.putAssetAdministrationShellDescriptorById(encode(ID_3), input);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void whenPostAssetAdministrationShellDescriptor_thenApplied() throws IOException {
		AssetAdministrationShellDescriptor input = new AssetAdministrationShellDescriptor();
		input.setIdentification(ID_3);
		ResponseEntity<AssetAdministrationShellDescriptor> response = controller.postAssetAdministrationShellDescriptor(input);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(input);

		ResponseEntity<List<AssetAdministrationShellDescriptor>> all = controller.getAllAssetAdministrationShellDescriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadShellDescriptorList();
		assertThat(all.getBody()).asList().containsExactlyInAnyOrderElementsOf(expected);
	}

	@Test
	public void whenPostAssetAdministrationShellDescriptor_thenOverridden() throws IOException {
		AssetAdministrationShellDescriptor input = new AssetAdministrationShellDescriptor();
		input.setIdentification(ID_2);
		ResponseEntity<AssetAdministrationShellDescriptor> response = controller.postAssetAdministrationShellDescriptor(input);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(input);

		ResponseEntity<List<AssetAdministrationShellDescriptor>> all = controller.getAllAssetAdministrationShellDescriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadShellDescriptorList();
		assertThat(all.getBody()).asList().containsExactlyInAnyOrderElementsOf(expected);
	}

	@Test
	public void whenSearchForUnknownAasDescriptor_thenReturnEmptyList() {
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery().queryType(QueryTypeEnum.MATCH).path(AasRegistryPaths.submodelDescriptors().identification()).value("unknown"));
		ResponseEntity<ShellDescriptorSearchResponse> entry = controller.searchShellDescriptors(request);
		assertThat(entry.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(entry.getBody().getHits()).isEmpty();
	}

	@Test
	public void whenDeleteAllShellDescritors_thenReturnNoContent() {
		ResponseEntity<Void> entry = controller.deleteAllShellDescriptors();
		assertThat(entry.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(entry.getBody()).isNull();
	}

	@Test
	public void whenMatchSearchForAasDescriptor_thenReturnResult() {
		AssetAdministrationShellDescriptor input = new AssetAdministrationShellDescriptor();
		input.setIdentification(ID_2);
		input.submodelDescriptors(List.of(new SubmodelDescriptor().identification(ID_2_1)));
		ResponseEntity<AssetAdministrationShellDescriptor> response = controller.postAssetAdministrationShellDescriptor(input);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(input);

		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery().queryType(QueryTypeEnum.MATCH).path(AasRegistryPaths.submodelDescriptors().identification()).value(ID_2_1));
		ResponseEntity<ShellDescriptorSearchResponse> entry = controller.searchShellDescriptors(request);
		assertThat(entry.getStatusCode()).isEqualTo(HttpStatus.OK);
		List<AssetAdministrationShellDescriptor> result = entry.getBody().getHits();
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.get(0)).isEqualTo(input);
	}

	@Test
	public void whenRegexSearchForAasDescriptor_thenReturnResult() {
		AssetAdministrationShellDescriptor input = new AssetAdministrationShellDescriptor();
		input.setIdentification(ID_2);
		input.submodelDescriptors(List.of(new SubmodelDescriptor().identification(ID_2_1).idShort(ID_2_1)));
		ResponseEntity<AssetAdministrationShellDescriptor> response = controller.postAssetAdministrationShellDescriptor(input);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(input);

		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery().queryType(QueryTypeEnum.REGEX).path(AasRegistryPaths.submodelDescriptors().idShort()).value(".*_2.1"));
		ResponseEntity<ShellDescriptorSearchResponse> entry = controller.searchShellDescriptors(request);
		assertThat(entry.getStatusCode()).isEqualTo(HttpStatus.OK);
		List<AssetAdministrationShellDescriptor> result = entry.getBody().getHits();
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.get(0)).isEqualTo(input);
	}

	private String encode(String id) {
		return Base64.getUrlEncoder().encodeToString(id.getBytes(StandardCharsets.UTF_8));
	}
}