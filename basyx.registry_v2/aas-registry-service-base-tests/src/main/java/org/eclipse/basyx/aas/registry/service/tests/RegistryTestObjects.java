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

import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.aas.registry.model.LangString;
import org.eclipse.basyx.aas.registry.model.SubmodelDescriptor;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RegistryTestObjects {

	public AssetAdministrationShellDescriptor newAssetAdministrationShellDescriptor(String id) {
		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
		descriptor.setIdentification(id);
		return descriptor;
	}

	public SubmodelDescriptor newSubmodelDescriptor(String id) {
		return newSubmodelDescriptorWithIdShort(id, null);
	}

	public SubmodelDescriptor newSubmodelDescriptorWithDescription(String id, String description) {
		SubmodelDescriptor descriptor = new SubmodelDescriptor();
		descriptor.setIdentification(id);
		addDescription(descriptor, description);
		return descriptor;
	}

	private void addDescription(SubmodelDescriptor descriptor, String description) {
		if (description != null) {
			LangString lString = newDescription(description);
			descriptor.addDescriptionItem(lString);
		}
	}

	private LangString newDescription(String sDescr) {
		LangString descr = new LangString();
		descr.setLanguage("de-DE");
		descr.setText(sDescr);
		return descr;
	}

	public AssetAdministrationShellDescriptor newDescriptor(String id) {
		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
		descriptor.setIdentification(id);
		return descriptor;
	}

	public SubmodelDescriptor newSubmodelDescriptorWithIdShort(String id, String idShort) {
		SubmodelDescriptor descriptor = new SubmodelDescriptor();
		descriptor.setIdentification(id);
		descriptor.setIdShort(idShort);
		return descriptor;
	}
}