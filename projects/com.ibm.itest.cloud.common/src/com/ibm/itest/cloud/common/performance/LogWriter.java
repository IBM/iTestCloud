/*********************************************************************
 * Copyright (c) 2013, 2022 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *********************************************************************/
package com.ibm.itest.cloud.common.performance;

import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.LINE_SEPARATOR;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Log Writer class, for writing performance debug messages to disk
 * <p>
 * The Log writer class is designed to be used by a PerfManager to write debug messages out to disk
 * <ul>
 * <li>{@link #close()}: Close the file.</li>
 * <li>{@link #open}: Open the file.</li>
 * <li>{@link #writeNext(String)}: Write next string to file.</li>
 * </ul>
 * </p>
 */

public class LogWriter {

//Global Variables
FileWriter writer;
final String filePathName;

public LogWriter (final String filePathName){
	this.filePathName = filePathName;
	try {
		this.writer = new FileWriter(filePathName,true);
	}
	catch (IOException e) {
		System.out.println("Performance file location not found, please create folders appropropriately.");
		e.printStackTrace();
	}
}

/**
 * Close the file
 */
public void close() {
	try {
	    this.writer.close();
    } catch (IOException e) {
    	System.out.println("Error occured while closing csv file.");
	    e.printStackTrace();
    }
}

/**
 * Flush the file
 */
public void flush() {
	try {
	    this.writer.flush();
    } catch (IOException e) {
    	System.out.println("Error occured while closing csv file.");
	    e.printStackTrace();
    }
}

/**
 * Open the file.
 */
public void open() {
	try {
		this.writer = new FileWriter(this.filePathName,true);
	} catch (IOException e) {
		System.out.println("Error occured while openning csv file.");
		e.printStackTrace();
	}
}

/**
 * Write next string array to file.
 *
 * @param content The string to write on the line
 */
public void writeNext(final String content) {
	try {
		this.writer.append(content);
		this.writer.append(LINE_SEPARATOR);
		this.writer.flush();
	} catch (IOException e) {
		System.out.println("Error occured while writing to log file.");
		e.printStackTrace();
	}
}

}
