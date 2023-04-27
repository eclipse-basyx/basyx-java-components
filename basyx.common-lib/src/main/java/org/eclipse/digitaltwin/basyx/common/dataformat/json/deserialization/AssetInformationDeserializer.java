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


package org.eclipse.digitaltwin.basyx.common.dataformat.json.deserialization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;


/**
 * 
 * Deserializer Interface for deserialization of assetInformations
 *
 * @author jungjan
 *
 */
public interface AssetInformationDeserializer {
	/**
     * Default charset that will be used when no charset is specified
     */
    Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * Deserializes a given string into an instance of the given AssetInformation
     *
     * @param src a string representation of the AssetInformation
     * @param outputClass most specific class of the given AssetInformation
     * @param <T> type of the returned element
     * @return an instance of the assetInformation
     * @throws DeserializationException if deserialization fails
     */
    <T extends AssetInformation> T readAssetInformation(String src, Class<T> outputClass) throws DeserializationException;

    /**
     * Deserializes a given input stream into an instance of the given AssetInformation using DEFAULT_CHARSET
     *
     * @param src a input stream representing a AssetInformation
     * @param outputClass most specific class of the given AssetInformation
     * @param <T> type of the returned element
     * @return an instance of the assetInformation
     * @throws DeserializationException if deserialization fails
     */
    default <T extends AssetInformation> T readAssetInformation(InputStream src, Class<T> outputClass) throws DeserializationException {
        return readAssetInformation(src, DEFAULT_CHARSET, outputClass);
    }

    /**
     * Deserializes a given input stream into an instance of the given AssetInformation
     *
     * @param src a input stream representing a AssetInformation
     * @param charset the charset to use
     * @param outputClass most specific class of the given AssetInformation
     * @param <T> type of the returned element
     * @return an instance of the assetInformation
     * @throws DeserializationException if deserialization fails
     */
    default <T extends AssetInformation> T readAssetInformation(InputStream src, Charset charset, Class<T> outputClass) throws DeserializationException {
        return readAssetInformation(new BufferedReader(
                new InputStreamReader(src, charset))
                .lines()
                .collect(Collectors.joining(System.lineSeparator())),
                outputClass);
    }

    /**
     * Deserializes a given file into an instance of the given AssetInformation using DEFAULT_CHARSET
     *
     * @param src a file containing string representation of a AssetInformation
     * @param outputClass most specific class of the given AssetInformation
     * @param <T> type of the returned element
     * @return an instance of the assetInformation
     * @throws DeserializationException if deserialization fails
     * @throws java.io.FileNotFoundException if file is not found
     */
    default <T extends AssetInformation> T readAssetInformation(File src, Class<T> outputClass) throws DeserializationException, FileNotFoundException {
        return readAssetInformation(src, DEFAULT_CHARSET, outputClass);
    }

    /**
     * Deserializes a given file into an instance of the given AssetInformation
     *
     * @param src a file containing string representation of a AssetInformation
     * @param charset the charset to use
     * @param outputClass most specific class of the given AssetInformation
     * @param <T> type of the returned element
     * @return an instance of the assetInformation
     * @throws DeserializationException if deserialization fails
     * @throws java.io.FileNotFoundException if file is not found
     */
    default <T extends AssetInformation> T readAssetInformation(File src, Charset charset, Class<T> outputClass) throws DeserializationException, FileNotFoundException {
        return readAssetInformation(new FileInputStream(src), charset, outputClass);
    }

    /**
     * Deserializes a given string into an instance of a list of the given AssetInformations
     *
     * @param assetInformations a string representation of an array of AssetInformations
     * @param outputClass most specific class of the given AssetInformation
     * @param <T> type of the returned element
     * @return an instance of a list of the assetInformations
     * @throws DeserializationException
     */
    <T extends AssetInformation> List<T> readAssetInformations(String assetInformations, Class<T> outputClass) throws DeserializationException;

    /**
     * Deserializes a given input stream into an instance of a list of the given AssetInformation using DEFAULT_CHARSET
     *
     * @param src a input stream representing a AssetInformation
     * @param outputClass most specific class of the given AssetInformation
     * @param <T> type of the returned element
     * @return an instance of the assetInformation
     * @throws DeserializationException if deserialization fails
     */
    default <T extends AssetInformation> List<T> readAssetInformations(InputStream src, Class<T> outputClass) throws DeserializationException {
        return readAssetInformations(src, DEFAULT_CHARSET, outputClass);
    }

    /**
     * Deserializes a given input stream into an instance of a list of the given AssetInformation
     *
     * @param src a input stream representing a AssetInformation
     * @param charset the charset to use
     * @param outputClass most specific class of the given AssetInformation
     * @param <T> type of the returned element
     * @return an instance of the assetInformation
     * @throws DeserializationException if deserialization fails
     */
    default <T extends AssetInformation> List<T> readAssetInformations(InputStream src, Charset charset, Class<T> outputClass) throws DeserializationException {
        return readAssetInformations(new BufferedReader(
                new InputStreamReader(src, charset))
                .lines()
                .collect(Collectors.joining(System.lineSeparator())),
                outputClass);
    }

    /**
     * Deserializes a given file into an instance of a list of the given AssetInformation using DEFAULT_CHARSET
     *
     * @param src a file containing string representation of a AssetInformation
     * @param outputClass most specific class of the given AssetInformation
     * @param <T> type of the returned element
     * @return an instance of the assetInformation
     * @throws DeserializationException if deserialization fails
     * @throws java.io.FileNotFoundException if file is not found
     */
    default <T extends AssetInformation> List<T> readAssetInformations(File src, Class<T> outputClass) throws DeserializationException, FileNotFoundException {
        return readAssetInformations(src, DEFAULT_CHARSET, outputClass);
    }

    /**
     * Deserializes a given file into an instance of a list of the given AssetInformation
     *
     * @param src a file containing string representation of a AssetInformation
     * @param charset the charset to use
     * @param outputClass most specific class of the given AssetInformation
     * @param <T> type of the returned element
     * @return an instance of the assetInformation
     * @throws DeserializationException if deserialization fails
     * @throws java.io.FileNotFoundException if file is not found
     */
    default <T extends AssetInformation> List<T> readAssetInformations(File src, Charset charset, Class<T> outputClass) throws DeserializationException, FileNotFoundException {
        return readAssetInformations(new FileInputStream(src), charset, outputClass);
    }
}
