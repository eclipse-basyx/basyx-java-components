/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.databridge.examples.debsdatagenerator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class DataGenerator {
	public static void main(String[] args) throws IOException {
		FileInputStream inputStream = null;
		Scanner sc = null;
		try {
		    inputStream = new FileInputStream("C:/Users/ashha/Desktop/Development/Thesis/allData.txt");
		    sc = new Scanner(inputStream, "UTF-8");
		    int i = 0;
		    while (sc.hasNextLine()) {
		        String line = sc.nextLine();
		        System.out.println(line);
		        i++;
		        if (i == 10) break;
		    }
		    // note that Scanner suppresses exceptions
		    if (sc.ioException() != null) {
		        throw sc.ioException();
		    }
		} finally {
		    if (inputStream != null) {
		        inputStream.close();
		    }
		    if (sc != null) {
		        sc.close();
		    }
		}
	}
}
