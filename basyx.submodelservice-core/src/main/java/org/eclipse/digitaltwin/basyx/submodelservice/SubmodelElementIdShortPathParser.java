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

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class containing Methods to split a idShortPath
 * 
 * @author fried
 *
 */
public class SubmodelElementIdShortPathParser {

	public static String[] parsePath(String idShortPath) {
		String parsed[] = idShortPath.split("\\.");
		return parsed;
	}

	/**
	 * Returns all Indices of a idShort
	 * 
	 * @param idShort the idShort
	 * @return List of indices
	 */

	public static List<Integer> getAllIndices(String idShort) {
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < idShort.length(); i++) {
			if (idShort.charAt(i) == '[') {
				boolean ended = false;
				int ix = i;
				String index = "";
				while (!ended) {
					ix++;
					if (idShort.charAt(ix) == ']') {
						ended = true;
					} else {
						index = index + idShort.charAt(ix);
					}
				}
				indices.add(Integer.parseInt(index));
			}
		}
		return indices;
	}

	/**
	 * Removes the indices of the idShort
	 * 
	 * @param idShort the idShort
	 * @return
	 */
	public static String getIdShortWithoutIndices(String idShort) {
		return idShort.split("\\[")[0];
	}

	/**
	 * Checks if a idShort has indices
	 * 
	 * @param idShort the idShort
	 * @return
	 */
	public static boolean hasIdShortIncides(String idShort) {
		return idShort.contains("[");
	}

	/**
	 * Checks if a idShort is a idShortPath
	 * 
	 * @param idShort the idShort
	 * @return
	 */

	public static boolean isPath(String idShort) {
		return idShort.contains(".") || idShort.contains("[");
	}

}
