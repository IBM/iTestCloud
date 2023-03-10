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
package itest.cloud.performance;

import static itest.cloud.scenario.ScenarioUtils.LINE_SEPARATOR;
import static itest.cloud.scenario.ScenarioUtils.QUOTE;
import static itest.cloud.utils.CollectionsUtil.getListFromArray;
import static java.util.Collections.unmodifiableList;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Csv Writer class, for writing csv results to disk
 * <p>
 * The Csv writer class is designed to provide a simple way to write Csv files and provide
 * input methods for common array types.
 * <ul>
 * <li>{@link #writeNext(ArrayList)}: Write next Array List of strings to file.</li>
 * <li>{@link #writeNext(String[])}: Write next string array to file.</li>
 * </ul>
 * </p>
 */

public class CsvWriter extends LogWriter {

//Global Variables
final char breakCharacter;

public CsvWriter (final String filePathName, final char breakCharacter){
	super(filePathName);
	this.breakCharacter = breakCharacter;
}

/**
 * Write next string array to file.
 *
 * @param csvArray The array of string to write on the line
 */
public void writeNext(final String[] csvArray) {
	writeNext(new ArrayList<>(unmodifiableList(getListFromArray(csvArray))));
}

/**
 * Write next string array to file.
 *
 * @param csvArray The array of string to write on the line
 */
public void writeNext(final ArrayList<String> csvArray) {
	try {
		for (int i = 0; i < csvArray.size(); i++) {
			if (i > 0) this.writer.append(this.breakCharacter);
			this.writer.append(QUOTE).append(csvArray.get(i)).append(QUOTE);
		}
		this.writer.append(LINE_SEPARATOR);
		this.writer.flush();
	} catch (IOException e) {
		System.out.println("Error occured while writing to csv file.");
		e.printStackTrace();
	}
}
}
