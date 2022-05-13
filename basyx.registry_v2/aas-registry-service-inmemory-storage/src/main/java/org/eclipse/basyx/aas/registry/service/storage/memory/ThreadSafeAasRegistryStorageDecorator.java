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
package org.eclipse.basyx.aas.registry.service.storage.memory;

import java.util.List;
import java.util.Set;

import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorSearchRequest;
import org.eclipse.basyx.aas.registry.model.ShellDescriptorSearchResponse;
import org.eclipse.basyx.aas.registry.model.SubmodelDescriptor;
import org.eclipse.basyx.aas.registry.service.storage.AasRegistryStorage;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ThreadSafeAasRegistryStorageDecorator implements AasRegistryStorage {

	private final AasRegistryStorage storage;

	private final ThreadSafeAccess access = new ThreadSafeAccess();

	@Override
	public boolean containsSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId) {
		return access.read(storage::containsSubmodel, aasDescriptorId, submodelId);
	}

	@Override
	public List<AssetAdministrationShellDescriptor> getAllAasDesriptors() {
		return access.read(storage::getAllAasDesriptors);
	}

	@Override
	public AssetAdministrationShellDescriptor getAasDescriptor(@NonNull String aasId) {
		return access.read(storage::getAasDescriptor, aasId);
	}

	@Override
	public void addOrReplaceAasDescriptor(@NonNull AssetAdministrationShellDescriptor descriptor) {
		access.write(storage::addOrReplaceAasDescriptor, descriptor);
	}

	@Override
	public boolean removeAasDescriptor(@NonNull String aasDescriptorId) {
		return access.write(storage::removeAasDescriptor, aasDescriptorId);
	}

	@Override
	public List<SubmodelDescriptor> getAllSubmodels(@NonNull String aasDescriptorId) {
		return access.read(storage::getAllSubmodels, aasDescriptorId);
	}

	@Override
	public SubmodelDescriptor getSubmodel(@NonNull String aasDescriptorId, @NonNull String submodelId) {
		return access.read(storage::getSubmodel, aasDescriptorId, submodelId);
	}

	@Override
	public void appendOrReplaceSubmodel(@NonNull String aasDescriptorId, @NonNull SubmodelDescriptor submodel) {
		access.write(storage::appendOrReplaceSubmodel, aasDescriptorId, submodel);
	}

	@Override
	public boolean removeSubmodel(@NonNull String aasDescrId, @NonNull String submodelId) {
		return access.write(storage::removeSubmodel, aasDescrId, submodelId);
	}

	@Override
	public Set<String> clear() {
		return access.write(storage::clear);
	}

	@Override
	public ShellDescriptorSearchResponse searchAasDescriptors(ShellDescriptorSearchRequest request) {
		return access.read(storage::searchAasDescriptors, request);
	}
}