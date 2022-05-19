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
package org.eclipse.basyx.aas.registry.service.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.eclipse.basyx.aas.registry.client.api.AasRegistryPaths;
import org.eclipse.basyx.aas.registry.events.RegistryEvent;
import org.eclipse.basyx.aas.registry.events.RegistryEventSink;
import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.aas.registry.model.Page;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorQuery;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorSearchRequest;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorSearchResponse;
import org.eclipse.basyx.aas.registry.model.SortDirection;
import org.eclipse.basyx.aas.registry.model.Sorting;
import org.eclipse.basyx.aas.registry.model.SortingPath;
import org.eclipse.basyx.aas.registry.model.SubmodelDescriptor;
import org.eclipse.basyx.aas.registry.service.storage.AasDescriptorNotFoundException;
import org.eclipse.basyx.aas.registry.service.storage.AasRegistryStorage;
import org.eclipse.basyx.aas.registry.service.storage.RegistrationEventSendingAasRegistryStorage;
import org.eclipse.basyx.aas.registry.service.storage.SubmodelNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class AasRegistryStorageTest {

	private static final String IDENTIFICATION_2_3 = "identification_2.3";

	private static final String IDENTIFICATION_2_2 = "identification_2.2";

	private static final String IDENTIFICATION_NEW = "identification_new";

	private static final String IDENTIFICATION_NEW_1 = "identification_new.1";

	private static final String IDENTIFICATION_2_1 = "identification_2.1";

	private static final String _2_UNKNOWN = "2.unknown";

	private static final String UNKNOWN_1 = "unknown.1";

	private static final String UNKNOWN = "unknown";

	private static final String IDENTIFICATION_1 = "identification_1";

	private static final String IDENTIFICATION_2 = "identification_2";

	private RegistryEventSink eventSink = Mockito.mock(RegistryEventSink.class);

	@Autowired
	private AasRegistryStorage baseStorage;

	private RegistrationEventSendingAasRegistryStorage storage;

	@Rule
	public TestResourcesLoader testResourcesLoader = new TestResourcesLoader();

	@Before
	public void setUp() throws IOException {
		storage = new RegistrationEventSendingAasRegistryStorage(baseStorage, eventSink);
		List<AssetAdministrationShellDescriptor> descriptors = testResourcesLoader.loadRepositoryDefinition();
		descriptors.forEach(baseStorage::addOrReplaceAasDescriptor);
	}

	@After
	public void tearDown() {
		baseStorage.clear();
	}

	@Test
	public void whenSearchWithSorting_thenSorted() {
		ShellDescriptorSearchResponse response = storage.searchAasDescriptors(new ShellDescriptorSearchRequest().sortBy(new Sorting().addPathItem(SortingPath.IDENTIFICATION)));
		String[] ids = response.getHits().stream().map(AssetAdministrationShellDescriptor::getIdentification).toArray(String[]::new);
		assertThat(ids[0]).isEqualTo(IDENTIFICATION_1);
		assertThat(ids[1]).isEqualTo(IDENTIFICATION_2);

		response = storage.searchAasDescriptors(new ShellDescriptorSearchRequest().sortBy(new Sorting().addPathItem(SortingPath.IDENTIFICATION).direction(SortDirection.DESC)));
		ids = response.getHits().stream().map(AssetAdministrationShellDescriptor::getIdentification).toArray(String[]::new);
		assertThat(ids[0]).isEqualTo(IDENTIFICATION_2);
		assertThat(ids[1]).isEqualTo(IDENTIFICATION_1);
	}

	@Test
	public void whenSearchOutsideSubmodel_thenGetUnshrinkedDescriptor() {
		AssetAdministrationShellDescriptor expected = storage.getAasDescriptor(IDENTIFICATION_1);
		ShellDescriptorSearchResponse response = storage.searchAasDescriptors(new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery().path(AasRegistryPaths.identification()).value(IDENTIFICATION_1)));
		assertThat(response.getTotal()).isEqualTo(1);
		assertThat(response.getHits().iterator().next()).isEqualTo(expected);
	}

	@Test
	public void whenSearchWithPagination_thenReturnStepwise() throws IOException {
		List<AssetAdministrationShellDescriptor> expectedFirstPage = testResourcesLoader.loadShellDescriptorList("0");
		List<AssetAdministrationShellDescriptor> expectedSecondPage = testResourcesLoader.loadShellDescriptorList("1");
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery().path(AasRegistryPaths.submodelDescriptors().description().text()).value(".*[R|r]obot.*").queryType(QueryTypeEnum.REGEX))
				.sortBy(new Sorting().addPathItem(SortingPath.IDENTIFICATION).direction(SortDirection.DESC)).page(new Page().index(0).size(2));
		ShellDescriptorSearchResponse response1 = storage.searchAasDescriptors(request);
		request.setPage(new Page().index(1).size(2));
		ShellDescriptorSearchResponse response2 = storage.searchAasDescriptors(request);
		assertThat(response1.getTotal()).isEqualTo(3);
		assertThat(response2.getTotal()).isEqualTo(3);
		assertThat(response1.getHits().size()).isEqualTo(2);
		assertThat(response2.getHits().size()).isEqualTo(1);
		assertThat(response1.getHits()).isEqualTo(expectedFirstPage);
		assertThat(response2.getHits()).isEqualTo(expectedSecondPage);
	}

	@Test
	public void whenSearchWithSortingAndNullValueAdSearchPath_thenNotSorted() {
		Collection<AssetAdministrationShellDescriptor> initial = storage.getAllAasDesriptors();
		ShellDescriptorSearchResponse response = storage.searchAasDescriptors(new ShellDescriptorSearchRequest().sortBy(new Sorting().addPathItem(SortingPath.ADMINISTRATION_VERSION)));
		assertThat(response.getHits()).isEqualTo(initial);
	}

	@Test
	public void whenSearchWithTwoSortingPaths_thenSorted() throws IOException {
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadShellDescriptorList();
		ShellDescriptorSearchResponse response = storage.searchAasDescriptors(new ShellDescriptorSearchRequest().sortBy(new Sorting().addPathItem(SortingPath.ADMINISTRATION_VERSION).addPathItem(SortingPath.ADMINISTRATION_REVISION)));
		assertThat(response.getHits()).isEqualTo(expected);
	}

	@Test
	public void whenSearchWithSortingButNoSortPath_thenNotSorted() {
		Collection<AssetAdministrationShellDescriptor> initial = storage.getAllAasDesriptors();
		ShellDescriptorSearchResponse response = storage.searchAasDescriptors(new ShellDescriptorSearchRequest().sortBy(new Sorting()));
		assertThat(response.getHits()).isEqualTo(initial);
	}

	@Test
	public void whenGetAllAssetAdministrationShellDescriptors_thenAll() throws IOException {
		Collection<AssetAdministrationShellDescriptor> found = storage.getAllAasDesriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadShellDescriptorList();
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
		verifyNoEventSend();
	}

	@Test
	public void whenGetAllAssetAdministrationShellDescriptorsAndEmptyRepo_thenEmptyList() {
		baseStorage.clear();
		Collection<AssetAdministrationShellDescriptor> found = storage.getAllAasDesriptors();
		assertThat(found).isEmpty();
		verifyNoEventSend();
	}

	@Test
	public void whenGetAllSubmodelDescriptorsAndNotSet_thenEmptyList() {
		List<SubmodelDescriptor> submodels = storage.getAllSubmodels(IDENTIFICATION_1);
		assertThat(submodels).isEmpty();
		verifyNoEventSend();
	}

	@Test
	public void whenGetAllSubmodelDescriptorsAndNotPresent_throwNotFound() {
		assertThrows(AasDescriptorNotFoundException.class, () -> storage.getAllSubmodels(UNKNOWN));
		verifyNoEventSend();
	}

	@Test
	public void whenGetAllSubmodelDescriptors_thenGot2Elements() throws IOException {
		List<SubmodelDescriptor> found = storage.getAllSubmodels(IDENTIFICATION_2);
		List<SubmodelDescriptor> expected = testResourcesLoader.loadSubmodelList();
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
		verifyNoEventSend();
	}

	@Test
	public void whenExistsSubmodelDescriptorByIdAndBothArgsNull_thenNullPointer() {
		assertNullPointerThrown(() -> storage.containsSubmodel(null, null));
	}

	@Test
	public void whenExistsSubmodelDescriptorByIdAndSubmodelIdIsNull_thenNullPointer() {
		assertNullPointerThrown(() -> storage.containsSubmodel(IDENTIFICATION_2, null));
	}

	@Test
	public void whenExistsSubmodelDescriptorByIdAndDescriptorIdIsNotAvailable_thenFalse() {
		boolean result = storage.containsSubmodel(UNKNOWN, UNKNOWN_1);
		assertThat(result).isFalse();
		verifyNoEventSend();
	}

	@Test
	public void whenExistsSubmodelDescriptorByIdAndSubmodelIdIsNotAvailable_thenFalse() {
		boolean result = storage.containsSubmodel(IDENTIFICATION_2, _2_UNKNOWN);
		assertThat(result).isFalse();
		verifyNoEventSend();
	}

	@Test
	public void whenExistsSubmodelDescriptorByIdAndAvailable_thenTrue() {
		boolean result = storage.containsSubmodel(IDENTIFICATION_2, IDENTIFICATION_2_1);
		assertThat(result).isTrue();
		verifyNoEventSend();
	}

	@Test
	public void whenGetSubmodelDescriptorByIdAndBothArgsNull_thenNullPointer() {
		assertNullPointerThrown(() -> storage.getSubmodel(null, null));
	}

	@Test
	public void whenGetSubmodelDescriptorByIdAndSubmodelIdIsNull_thenNullPointer() {
		assertNullPointerThrown(() -> storage.getSubmodel(IDENTIFICATION_2, null));
	}

	@Test
	public void whenGetSubmodelDescriptorByIdAndDescriptorIdIsNotAvailable_thenThrowNotFound() {
		assertThrows(AasDescriptorNotFoundException.class, () -> storage.getSubmodel(UNKNOWN, UNKNOWN_1));
		verifyNoEventSend();
	}

	@Test
	public void whenGetSubmodelDescriptorByIdAndSubmodelIdIsNotAvailable_thenThrowNotFound() {
		assertThrows(SubmodelNotFoundException.class, () -> storage.getSubmodel(IDENTIFICATION_2, _2_UNKNOWN));
		verifyNoEventSend();
	}

	@Test
	public void whenGetSubmodelDescriptorByIdAndSubmodelIdIsAvailable_thenGotResult() throws IOException {
		SubmodelDescriptor result = storage.getSubmodel(IDENTIFICATION_2, IDENTIFICATION_2_1);
		SubmodelDescriptor expected = testResourcesLoader.loadSubmodel();
		assertThat(result).isEqualTo(expected);
		verifyNoEventSend();
	}

	@Test
	public void whenGetAssetAdminstrationShellDescritorByIdAndIdIsNull_thenNullPointer() {
		assertNullPointerThrown(() -> storage.getAasDescriptor(null));
	}

	@Test
	public void whenGetAssetAdminstrationShellDescritorByIdAndUnknown_thenThrowNotFound() {
		assertThrows(AasDescriptorNotFoundException.class, () -> storage.getAasDescriptor(UNKNOWN));
		verifyNoEventSend();
	}

	@Test
	public void whenGetAssetAdminstrationShellDescritorByIdAndAvailable_thenGotResult() throws IOException {
		AssetAdministrationShellDescriptor result = storage.getAasDescriptor(IDENTIFICATION_1);
		AssetAdministrationShellDescriptor expected = testResourcesLoader.loadAssetAdminShellDescriptor();
		assertThat(result).isEqualTo(expected);
		verifyNoEventSend();
	}

	@Test
	public void whenRegisterAssetAdministrationShellDescriptorNullArg_thenNullPointer() {
		assertNullPointerThrown(() -> storage.addOrReplaceAasDescriptor(null));
	}

	@Test
	public void whenRegisterAssetAdministrationShellDescriptor_thenStored() throws IOException {
		List<AssetAdministrationShellDescriptor> initialState = storage.getAllAasDesriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadShellDescriptorList();
		assertThat(initialState).isNotEqualTo(expected);
		AssetAdministrationShellDescriptor testResource = RegistryTestObjects.newAssetAdministrationShellDescriptor(IDENTIFICATION_NEW);
		SubmodelDescriptor subModel = RegistryTestObjects.newSubmodelDescriptor(IDENTIFICATION_NEW_1);
		testResource.setSubmodelDescriptors(Collections.singletonList(subModel));
		storage.addOrReplaceAasDescriptor(testResource);
		List<AssetAdministrationShellDescriptor> newState = storage.getAllAasDesriptors();
		assertThat(newState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventSend();
	}

	@Test
	public void whenUnregisterAssetAdministrationShellDescriptorByIdAndNullId_thenThrowNotFoundAndNoChanges() {
		List<AssetAdministrationShellDescriptor> initialState = storage.getAllAasDesriptors();
		assertThrows(NullPointerException.class, () -> storage.removeAasDescriptor(null));
		List<AssetAdministrationShellDescriptor> currentState = storage.getAllAasDesriptors();
		assertThat(currentState).asList().containsExactlyInAnyOrderElementsOf(initialState);
		verifyNoEventSend();
	}

	@Test
	public void whenUnregisterAssetAdministrationShellDescriptorById_thenReturnTrueAndEntryRemoved() throws IOException {
		List<AssetAdministrationShellDescriptor> initialState = storage.getAllAasDesriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadShellDescriptorList();
		boolean success = storage.removeAasDescriptor(IDENTIFICATION_2);
		assertThat(success).isTrue();
		List<AssetAdministrationShellDescriptor> currentState = storage.getAllAasDesriptors();
		assertThat(currentState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventSend();
	}

	@Test
	public void whenUnregisterAssetAdministrationShellDescriptorByIdAndIdUnknon_thenReturnFalsAndNoChanges() throws IOException {
		List<AssetAdministrationShellDescriptor> initialState = storage.getAllAasDesriptors();
		boolean success = storage.removeAasDescriptor(UNKNOWN);
		assertThat(success).isFalse();
		List<AssetAdministrationShellDescriptor> currentState = storage.getAllAasDesriptors();
		assertThat(currentState).asList().containsExactlyInAnyOrderElementsOf(initialState);
		verifyNoEventSend();
	}

	@Test
	public void whenRegisterSubmodelDescriptorNullAasId_thenNullPointer() {
		assertNullPointerThrown(() -> storage.appendOrReplaceSubmodel(null, null));
	}

	@Test
	public void whenRegisterSubmodelDescriptorNullModel_thenNullPointer() {
		assertNullPointerThrown(() -> storage.appendOrReplaceSubmodel(IDENTIFICATION_1, null));
	}

	@Test
	public void whenRegisterSubmodelDescriptorNullId_thenNullPointer() {
		SubmodelDescriptor descriptor = RegistryTestObjects.newSubmodelDescriptor(null);
		assertNullPointerThrown(() -> storage.appendOrReplaceSubmodel(IDENTIFICATION_1, descriptor));
	}

	@Test
	public void whenRegisterSubmodelDescriptorUnknownId_thenThrowNotFound() {
		List<AssetAdministrationShellDescriptor> initialState = storage.getAllAasDesriptors();
		SubmodelDescriptor ignored = RegistryTestObjects.newSubmodelDescriptor("ignored");
		assertThrows(AasDescriptorNotFoundException.class, () -> storage.appendOrReplaceSubmodel(UNKNOWN, ignored));
		List<AssetAdministrationShellDescriptor> currentState = storage.getAllAasDesriptors();
		assertThat(currentState).isEqualTo(initialState);
		verifyNoEventSend();
	}

	@Test
	public void whenRegisterSubmodelDescriptorAndWasAlreadyPresent_thenElementIsOverridden() throws IOException {
		List<AssetAdministrationShellDescriptor> initialState = storage.getAllAasDesriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadShellDescriptorList();
		assertThat(initialState).isNotEqualTo(expected);
		SubmodelDescriptor toAdd = RegistryTestObjects.newSubmodelDescriptorWithDescription(IDENTIFICATION_2_2, "Overridden");
		storage.appendOrReplaceSubmodel(IDENTIFICATION_2, toAdd);
		List<AssetAdministrationShellDescriptor> newState = storage.getAllAasDesriptors();
		assertThat(newState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventSend();
	}

	@Test
	public void whenRegisterSubmodelDescriptorAndWasNotAlreadyPresent_thenElementIsAdded() throws IOException {
		List<AssetAdministrationShellDescriptor> initialState = storage.getAllAasDesriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadShellDescriptorList();
		assertThat(initialState).isNotEqualTo(expected);
		SubmodelDescriptor toAdd = RegistryTestObjects.newSubmodelDescriptor(IDENTIFICATION_2_3);
		storage.appendOrReplaceSubmodel(IDENTIFICATION_2, toAdd);
		List<AssetAdministrationShellDescriptor> newState = storage.getAllAasDesriptors();
		assertThat(newState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventSend();
	}

	@Test
	public void whenUnregisterSubmodelDescriptorNullAdminShell_thenNullPointer() {
		assertNullPointerThrown(() -> storage.removeSubmodel(null, IDENTIFICATION_2_1));
	}

	@Test
	public void whenUnregisterSubmodelDescriptorNullSubmodelId_thenNullPointer() {
		assertNullPointerThrown(() -> storage.removeSubmodel(IDENTIFICATION_2, null));
	}

	@Test
	public void whenUnregisterSubmodelDescriptorAndWasPresent_thenElementIsRemoved() throws IOException {
		List<AssetAdministrationShellDescriptor> initialState = storage.getAllAasDesriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadShellDescriptorList();
		assertThat(initialState).isNotEqualTo(expected);
		boolean success = storage.removeSubmodel(IDENTIFICATION_2, IDENTIFICATION_2_2);
		assertThat(success).isTrue();
		List<AssetAdministrationShellDescriptor> newState = storage.getAllAasDesriptors();
		assertThat(newState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventSend();

		success = storage.removeSubmodel(IDENTIFICATION_2, IDENTIFICATION_2_1);
		assertThat(success).isTrue();
		assertThat(storage.getAasDescriptor(IDENTIFICATION_2).getSubmodelDescriptors()).isNullOrEmpty();

		success = storage.removeSubmodel(IDENTIFICATION_2, IDENTIFICATION_2_1);
		assertThat(success).isFalse();
	}

	@Test
	public void whenUnregisterSubmodelDescriptorAndShellWasNotPresent_thenThrowNotFound() {
		List<AssetAdministrationShellDescriptor> initialState = storage.getAllAasDesriptors();
		boolean removed = storage.removeSubmodel(UNKNOWN, UNKNOWN_1);
		assertThat(removed).isFalse();
		List<AssetAdministrationShellDescriptor> newState = storage.getAllAasDesriptors();
		assertThat(newState).asList().containsExactlyInAnyOrderElementsOf(initialState);
		verifyNoEventSend();
	}

	@Test
	public void whenUnregisterSubmodelDescriptorAndSubmodelWasNotPresent_thenReturnFalse() {
		List<AssetAdministrationShellDescriptor> initialState = storage.getAllAasDesriptors();
		boolean success = storage.removeSubmodel(IDENTIFICATION_2, _2_UNKNOWN);
		assertThat(success).isFalse();
		List<AssetAdministrationShellDescriptor> newState = storage.getAllAasDesriptors();
		assertThat(newState).asList().containsExactlyInAnyOrderElementsOf(initialState);
		verifyNoEventSend();
	}

	@Test
	public void whenMatchSearchBySubModel_thenReturnDescriptorList() throws IOException {
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery().queryType(QueryTypeEnum.MATCH).path(AasRegistryPaths.submodelDescriptors().identification()).value(IDENTIFICATION_2_1));
		ShellDescriptorSearchResponse result = storage.searchAasDescriptors(request);
		AssetAdministrationShellDescriptor descriptor = testResourcesLoader.loadAssetAdminShellDescriptor();

		assertThat(result.getTotal()).isEqualTo(1);
		assertThat(result.getHits().get(0)).isEqualTo(descriptor);
	}

	@Test
	public void whenMatchSearchBySubModelAndNotFound_thenReturnEmptyList() {
		ShellDescriptorSearchRequest query = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery().queryType(QueryTypeEnum.MATCH).path(AasRegistryPaths.submodelDescriptors().identification()).value(UNKNOWN));
		ShellDescriptorSearchResponse result = storage.searchAasDescriptors(query);
		assertThat(result.getTotal()).isZero();
		assertThat(result.getHits().size()).isZero();
	}

	@Test
	public void whenRegexSearchBySubModel_thenReturnDescriptorList() throws IOException {
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery().queryType(QueryTypeEnum.REGEX).path(AasRegistryPaths.submodelDescriptors().idShort()).value(".*_24"));
		ShellDescriptorSearchResponse result = storage.searchAasDescriptors(request);
		AssetAdministrationShellDescriptor descriptor = testResourcesLoader.loadAssetAdminShellDescriptor();
		assertThat(result.getTotal()).isEqualTo(1);
		assertThat(result.getHits().get(0)).isEqualTo(descriptor);
	}

	@Test
	public void whenRegexSearchBySubModelAndNotFound_thenReturnEmptyList() {
		ShellDescriptorSearchRequest query = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery().queryType(QueryTypeEnum.REGEX).path(AasRegistryPaths.submodelDescriptors().idShort()).value(".*_333_.*"));
		ShellDescriptorSearchResponse result = storage.searchAasDescriptors(query);
		assertThat(result.getTotal()).isZero();
		assertThat(result.getHits().size()).isZero();
	}

	@Test
	public void whenDeleteAllShellDescritors_thenEventsAreSendAndDescriptorsRemoved() {
		List<AssetAdministrationShellDescriptor> oldState = storage.getAllAasDesriptors();
		assertThat(oldState).isNotEmpty();
		Set<String> aasIdsOfRemovedDescriptors = storage.clear();
		// listener is invoked for each removal
		Mockito.verify(eventSink, Mockito.times(aasIdsOfRemovedDescriptors.size())).consumeEvent(Mockito.any(RegistryEvent.class));
		List<AssetAdministrationShellDescriptor> newState = storage.getAllAasDesriptors();
		assertThat(newState).isEmpty();
	}

	private void assertNullPointerThrown(ThrowingCallable callable) {
		Throwable th = Assertions.catchThrowable(callable);
		assertThat(th).isInstanceOf(NullPointerException.class);
		verifyNoEventSend();
	}

	private void verifyEventSend() throws IOException {
		RegistryEvent evt = testResourcesLoader.loadEvent();
		Mockito.verify(eventSink, Mockito.times(1)).consumeEvent(Mockito.any(RegistryEvent.class));
		Mockito.verify(eventSink, Mockito.only()).consumeEvent(evt);
	}

	private void verifyNoEventSend() {
		Mockito.verify(eventSink, Mockito.never()).consumeEvent(Mockito.any(RegistryEvent.class));
	}

}