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

import java.util.List;

import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.aas.registry.model.SubmodelDescriptor;
import org.eclipse.basyx.aas.registry.service.storage.AasRegistryStorage;
import org.eclipse.basyx.aas.registry.service.storage.DescriptorCopies;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

//performs additional cloning for in memory tests
//altering the objects during tests will then not affect the storage
@RequiredArgsConstructor
public class CloningAasRegistryStorageDecorator implements AasRegistryStorage {

	@Delegate
	private final AasRegistryStorage storage;

	@Override
	public List<AssetAdministrationShellDescriptor> getAllAasDesriptors() {
		return DescriptorCopies.deepCloneCollection(storage.getAllAasDesriptors());
	}

	@Override
	public AssetAdministrationShellDescriptor getAasDescriptor(String aasId) {
		return DescriptorCopies.deepClone(storage.getAasDescriptor(aasId));
	}

	@Override
	public void addOrReplaceAasDescriptor(AssetAdministrationShellDescriptor descriptor) {
		storage.addOrReplaceAasDescriptor(DescriptorCopies.deepClone(descriptor));
	}

	@Override
	public List<SubmodelDescriptor> getAllSubmodels(String aasDescriptorId) {
		return DescriptorCopies.deepCloneCollection(storage.getAllSubmodels(aasDescriptorId));
	}

	@Override
	public SubmodelDescriptor getSubmodel(String aasDescriptorId, String submodelId) {
		return DescriptorCopies.deepClone(storage.getSubmodel(aasDescriptorId, submodelId));
	}

	@Override
	public void appendOrReplaceSubmodel(String aasDescriptorId, SubmodelDescriptor submodel) {
		storage.appendOrReplaceSubmodel(aasDescriptorId, DescriptorCopies.deepClone(submodel));
	}
}
