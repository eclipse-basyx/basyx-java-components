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

package org.eclipse.basyx.regression.components.propertymap;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.basyx.components.propertymap.PropertyMapHelper;
import org.junit.Test;

/**
 * 
 * @author schnicke
 *
 */
public class PropertyMapHelperTest {

	@Test
	public void testGetListFromStringListMultipleElementsWithSpace() {
		String testData = "a, b, c";
		List<String> expected = Arrays.asList("a", "b", "c");
		List<String> actual = PropertyMapHelper.getListFromStringList(testData);

		assertEquals(expected, actual);
	}

	@Test
	public void testGetListFromStringListMultipleElementsNoSpace() {
		String testData = "a,b, c";
		List<String> expected = Arrays.asList("a", "b", "c");
		List<String> actual = PropertyMapHelper.getListFromStringList(testData);

		assertEquals(expected, actual);
	}

	@Test
	public void testGetListFromStringListSingleElementWithSpace() {
		String testData = "a ";
		List<String> expected = Arrays.asList("a");
		List<String> actual = PropertyMapHelper.getListFromStringList(testData);

		assertEquals(expected, actual);
	}

	@Test
	public void testGetListFromStringListSingleElementNoSpace() {
		String testData = "a";
		List<String> expected = Arrays.asList("a");
		List<String> actual = PropertyMapHelper.getListFromStringList(testData);

		assertEquals(expected, actual);
	}
}
