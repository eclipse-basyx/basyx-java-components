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
package org.eclipse.basyx.aas.registry.compatibility;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.aas.registry.model.Descriptor;
import org.eclipse.basyx.aas.registry.model.Endpoint;
import org.eclipse.basyx.aas.registry.model.GlobalReference;
import org.eclipse.basyx.aas.registry.model.ModelReference;
import org.eclipse.basyx.aas.registry.model.ProtocolInformation;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;

public class DescriptorConversions {

	private DescriptorConversions() {
	}

	public static AssetAdministrationShellDescriptor toDotaasAASDescriptor(AASDescriptor basyxDescriptor) {
		AssetAdministrationShellDescriptor result = new AssetAdministrationShellDescriptor();

		Optional.ofNullable(basyxDescriptor.getIdShort()).ifPresent(result::setIdShort);
		Optional.ofNullable(basyxDescriptor.getIdentifier()).map(IIdentifier::getId).ifPresent(result::setIdentification);
		assignEndpoints(basyxDescriptor.getEndpoints(), InterfaceType.AAS, result);
		assignGlobalReference(basyxDescriptor, result);
		assignSubmodelDescriptors(basyxDescriptor.getSubmodelDescriptors(), result);

		return result;
	}

	private static void assignEndpoints(Collection<Map<String, Object>> endpoints, InterfaceType iType, Descriptor target) {
		for (Map<String, Object> eachEndpoint : endpoints) {
			String address = (String) eachEndpoint.get(AssetAdministrationShell.ADDRESS);
			String type = (String) eachEndpoint.get(AssetAdministrationShell.TYPE);
			if (address != null && type != null) {
				ProtocolInformation protocolInfo = new ProtocolInformation().endpointAddress(address).endpointProtocol(type);
				Endpoint endpoint = new Endpoint()._interface(iType.getInterfaceName()).protocolInformation(protocolInfo);
				target.addEndpointsItem(endpoint);
			}
		}
	}

	private static void assignSubmodelDescriptors(Collection<SubmodelDescriptor> submodelDescriptors, AssetAdministrationShellDescriptor result) {
		Comparator<SubmodelDescriptor> comparator = Comparator.nullsFirst(Comparator.comparing(SubmodelDescriptor::getIdShort));
		submodelDescriptors.stream().sorted(comparator).map(DescriptorConversions::toDotaasSubmodelDescriptor).forEach(result::addSubmodelDescriptorsItem);
	}

	public static org.eclipse.basyx.aas.registry.model.SubmodelDescriptor toDotaasSubmodelDescriptor(SubmodelDescriptor basyxDescriptor) {
		org.eclipse.basyx.aas.registry.model.SubmodelDescriptor result = new org.eclipse.basyx.aas.registry.model.SubmodelDescriptor();

		Optional.ofNullable(basyxDescriptor.getIdShort()).ifPresent(result::setIdShort);
		Optional.ofNullable(basyxDescriptor.getIdentifier()).map(IIdentifier::getId).ifPresent(result::setIdentification);

		assignEndpoints(basyxDescriptor.getEndpoints(), InterfaceType.SUBMODEL, result);
		assignSemanticId(basyxDescriptor.getSemanticId(), result);

		return result;
	}

	private static void assignSemanticId(IReference semanticId, org.eclipse.basyx.aas.registry.model.SubmodelDescriptor target) {
		ModelReference semanticRef = new ModelReference();
		target.setSemanticId(semanticRef);
		List<IKey> keys = semanticId.getKeys();
		if (keys != null) {
			keys.stream().map(DescriptorConversions::toDotaasKey).forEach(semanticRef::addKeysItem);
		}
	}

	private static org.eclipse.basyx.aas.registry.model.Key toDotaasKey(IKey key) {
		org.eclipse.basyx.aas.registry.model.Key dotaasKey = new org.eclipse.basyx.aas.registry.model.Key();
		dotaasKey.setType(toDotaasKeyElement(key.getType()));
		dotaasKey.setValue(key.getValue());
		return dotaasKey;
	}

	private static org.eclipse.basyx.aas.registry.model.KeyElements toDotaasKeyElement(KeyElements key) {
		switch (key) {
		// case SUBMODEL:
		// return org.eclipse.basyx.aas.registry.model.KeyElements.SUBMODEL;
		default:
			return org.eclipse.basyx.aas.registry.model.KeyElements.CONCEPTDESCRIPTION;
		}
	}

	public static AASDescriptor toBasyxAASDescriptor(AssetAdministrationShellDescriptor dotaasDescriptor) {
		String idShort = dotaasDescriptor.getIdShort();
		Identifier identifier = new Identifier(IdentifierType.CUSTOM, dotaasDescriptor.getIdentification());

		String address = getEndpointAddress(dotaasDescriptor.getEndpoints());

		AASDescriptor result = new AASDescriptor(idShort, identifier, new Asset(), address);

		GlobalReference ref = (GlobalReference) dotaasDescriptor.getGlobalAssetId();
		Asset resultAsset = (Asset) result.getAsset();
		resultAsset.setIdentification(IdentifierType.CUSTOM, ref.getValue().get(0));

		Optional.ofNullable(dotaasDescriptor.getSubmodelDescriptors()).map(List::stream).orElseGet(Stream::empty).map(DescriptorConversions::toBasyxSubmodelDescriptor).forEach(result::addSubmodelDescriptor);

		return result;
	}

	private static String getEndpointAddress(List<Endpoint> endpoints) {
		if (endpoints == null || endpoints.isEmpty()) {
			return ""; // fallback -> see ModelDescriptor.getFirstEndpoint();
		}
		ProtocolInformation info = endpoints.get(0).getProtocolInformation();
		if (info == null) {
			return "";
		}
		return info.getEndpointAddress();
	}

	public static SubmodelDescriptor toBasyxSubmodelDescriptor(org.eclipse.basyx.aas.registry.model.SubmodelDescriptor dotaasDescriptor) {

		String address = getEndpointAddress(dotaasDescriptor.getEndpoints());
		SubmodelDescriptor result = new SubmodelDescriptor(dotaasDescriptor.getIdShort(), new Identifier(IdentifierType.CUSTOM, dotaasDescriptor.getIdentification()), address);

		Optional.ofNullable(dotaasDescriptor.getSemanticId()).filter(ModelReference.class::isInstance).map(ModelReference.class::cast).map(ModelReference::getKeys).map(List::iterator).filter(Iterator::hasNext).map(Iterator::next)
				.map(org.eclipse.basyx.aas.registry.model.Key::getValue).map(DescriptorConversions::toCustomKey).filter(Objects::nonNull).map(Reference::new).ifPresent(result::setSemanticId);

		return result;
	}

	private static Key toCustomKey(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return new Key(KeyElements.SUBMODEL, false, value, IdentifierType.CUSTOM);
	}

	private static void assignGlobalReference(AASDescriptor basyxDescriptor, AssetAdministrationShellDescriptor result) {
		GlobalReference assetRef = new GlobalReference();
		Optional.of(basyxDescriptor.getAsset()).map(IAsset::getIdentification).map(IIdentifier::getId).ifPresent(assetRef::addValueItem);
		result.setGlobalAssetId(assetRef);
	}

	private enum InterfaceType {

		AAS, SUBMODEL;

		private static final String CURRENT_VERSION = "1.0";

		public String getInterfaceName() {
			return name() + "-" + CURRENT_VERSION;
		}
	}
}