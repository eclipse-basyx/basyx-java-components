/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.databridge.core.configuration.parser;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * A generic core implementation of Json message parser-
 * Parses the json message according to the given mapper Class
 * 
 * @author haque
 *
 */
public class JsonParser {
	private Gson gson;
	Class<?> mapperClass;
	
	/**
	 * Builds a JsonParser with a class in which the json will 
	 * be mapped into
	 * @param mapperClass
	 */
	public JsonParser(Class<?> mapperClass) {
		gson = new Gson();
		this.mapperClass = mapperClass;
	}
	
	/**
	 * Gets a list of mapper class objects from the json stream reader
	 * @param reader
	 * @return
	 */
	public Object getListConfiguration(Reader reader) {
		Type listType = TypeToken.getParameterized(ArrayList.class, mapperClass).getType(); 
		return gson.fromJson(reader, listType); 
	}
	
	/**
	 * Gets a mapper class object from the json stream reader
	 * @param reader
	 * @return
	 */
	public Object getConfiguration(Reader reader) {
		Type type = TypeToken.getParameterized(mapperClass).getType();
		return gson.fromJson(reader, type); 
	}
}
