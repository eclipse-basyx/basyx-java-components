/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.components.aas.aasx;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.factory.aasx.AASXToMetamodelConverter;
import org.xml.sax.SAXException;

/**
 * @deprecated Renamed and moved to SDK. Please use AASXToMetamodelConverter
 * @author schnicke
 *
 */
@Deprecated
public class AASXPackageManager extends AASXToMetamodelConverter {

	public AASXPackageManager(String path) {
		super(path);
	}

	@Override
	protected Path getRootFolder() throws IOException, URISyntaxException {
		URI uri = AASXPackageManager.class.getProtectionDomain().getCodeSource().getLocation().toURI();
		URI parent = new File(uri).getParentFile().toURI();
		return Paths.get(parent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<org.eclipse.basyx.support.bundle.AASBundle> retrieveAASBundles() throws IOException, ParserConfigurationException, SAXException, InvalidFormatException {
		Set<? extends AASBundle> bundles = super.retrieveAASBundles();
		return repackAASBundle(bundles);
	}

	/**
	 * @param bundles
	 * @return
	 */
	private Set<org.eclipse.basyx.support.bundle.AASBundle> repackAASBundle(Set<? extends AASBundle> bundles) {
		return bundles.stream().map(b -> new org.eclipse.basyx.support.bundle.AASBundle(b.getAAS(), b.getSubmodels())).collect(Collectors.toSet());
	}
}
