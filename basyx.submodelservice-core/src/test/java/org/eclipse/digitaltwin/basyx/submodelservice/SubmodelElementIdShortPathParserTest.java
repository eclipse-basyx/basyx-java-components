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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * 
 * Test for the SubmodelElementIdShortPathParser
 * 
 * @author fried
 *
 */
public class SubmodelElementIdShortPathParserTest {

	private static final String ID_SHORT_PATH_FIRST_PART = "SubmodelElement";
	private static final int INDEX_ONE = 100;
	private static final int INDEX_TWO = 23;
	private static final String ID_SHORT_PATH_SECOND_PART = "SubmodelElementList[" + INDEX_ONE + "][" + INDEX_TWO + "]";
	private static final String ID_SHORT_PATH_THIRD_PART = "SubmodelElementProperty";
	private static final String ID_SHORT_PATH = ID_SHORT_PATH_FIRST_PART + "." + ID_SHORT_PATH_SECOND_PART + "."
			+ ID_SHORT_PATH_THIRD_PART;
	private static final String ID_SHORT_WITH_SPECIAL_CHARACTERS = "doesNotMatter-,;_'*+~?=)({}&%$§!]";

	@Test
	public void idShortParsedCorrectly() {
		String[] parsed = SubmodelElementIdShortPathParser.parsePath(ID_SHORT_PATH);
		assertEquals(ID_SHORT_PATH_FIRST_PART, parsed[0]);
		assertEquals(ID_SHORT_PATH_SECOND_PART, parsed[1]);
		assertEquals(ID_SHORT_PATH_THIRD_PART, parsed[2]);
	}

	@Test
	public void indizesExtractedCorrectly() {
		List<Integer> indices = SubmodelElementIdShortPathParser.getAllIndices(ID_SHORT_PATH_SECOND_PART);
		assertEquals((int) indices.get(0), INDEX_ONE);
		assertEquals((int) indices.get(1), INDEX_TWO);
	}

	@Test
	public void getAllIndicesOnIdShortWithoutIndicesReturnsEmptyList() {
		List<Integer> indices = SubmodelElementIdShortPathParser.getAllIndices(ID_SHORT_PATH_FIRST_PART);
		assertTrue(indices.size() == 0);
	}

	@Test
	public void idShortPathCheckReturnsTrue() {
		assertTrue(SubmodelElementIdShortPathParser.isPath(ID_SHORT_PATH));
	}

	@Test
	public void normalIdShortCheckReturnsFalse() {
		assertFalse(SubmodelElementIdShortPathParser.isPath(ID_SHORT_WITH_SPECIAL_CHARACTERS));
	}
}
