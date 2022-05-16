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
package org.eclipse.basyx.aas.registry.service.storage;

import java.util.Set;

import org.eclipse.basyx.aas.registry.events.RegistryEvent;
import org.eclipse.basyx.aas.registry.events.RegistryEvent.EventType;
import org.eclipse.basyx.aas.registry.events.RegistryEventSink;
import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.aas.registry.model.SubmodelDescriptor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class RegistrationEventSendingAasRegistryStorage implements AasRegistryStorage {

	@Delegate
	private final AasRegistryStorage storage;

	@NonNull
	private final RegistryEventSink eventSink;

	@Override
	public void addOrReplaceAasDescriptor(@NonNull AssetAdministrationShellDescriptor descriptor) {
		storage.addOrReplaceAasDescriptor(descriptor);
		RegistryEvent evt = RegistryEvent.builder().id(descriptor.getIdentification()).type(RegistryEvent.EventType.AAS_REGISTERED).aasDescriptor(descriptor).build();
		eventSink.consumeEvent(evt);
	}

	@Override
	public boolean removeAasDescriptor(@NonNull String aasDescriptorId) {
		boolean deleted = storage.removeAasDescriptor(aasDescriptorId);
		if (deleted) {
			RegistryEvent evt = RegistryEvent.builder().id(aasDescriptorId).type(RegistryEvent.EventType.AAS_UNREGISTERED).build();
			eventSink.consumeEvent(evt);
		}
		return deleted;
	}

	@Override
	public void appendOrReplaceSubmodel(@NonNull String aasDescriptorId, @NonNull SubmodelDescriptor submodel) {
		storage.appendOrReplaceSubmodel(aasDescriptorId, submodel);
		// always update for now, even if it was an override with the same value
		RegistryEvent evt = RegistryEvent.builder().id(aasDescriptorId).submodelId(submodel.getIdentification()).type(EventType.SUBMODEL_REGISTERED).submodelDescriptor(submodel).build();
		eventSink.consumeEvent(evt);
	}

	@Override
	public boolean removeSubmodel(@NonNull String aasDescrId, @NonNull String submodelId) {
		boolean success = storage.removeSubmodel(aasDescrId, submodelId);
		if (success) {
			RegistryEvent evt = RegistryEvent.builder().id(aasDescrId).submodelId(submodelId).type(EventType.SUBMODEL_UNREGISTERED).build();
			eventSink.consumeEvent(evt);
		}
		return success;
	}

	@Override
	public Set<String> clear() {
		Set<String> unregistredDescriptors = storage.clear();
		for (String eachId : unregistredDescriptors) {
			RegistryEvent evt = RegistryEvent.builder().id(eachId).type(RegistryEvent.EventType.AAS_UNREGISTERED).build();
			eventSink.consumeEvent(evt);
		}
		return unregistredDescriptors;
	}

}
