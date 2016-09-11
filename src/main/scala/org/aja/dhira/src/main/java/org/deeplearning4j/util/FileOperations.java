/*
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package org.deeplearning4j.util;

import java.io.*;

public class FileOperations {

	private FileOperations() {}
	
	
	
	public static OutputStream createAppendingOutputStream(File to) {
		try {
			return new BufferedOutputStream(new FileOutputStream(to,true));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void appendTo(String data,File append) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(append,true));
			bos.write(data.getBytes());
			bos.flush();
			bos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

}
