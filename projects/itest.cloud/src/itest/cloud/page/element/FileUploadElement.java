/*********************************************************************
 * Copyright (c) 2016, 2022 IBM Corporation and others.
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
package itest.cloud.page.element;

import static itest.cloud.scenario.ScenarioUtil.EMPTY_STRING;
import static itest.cloud.scenario.ScenarioUtil.PERIOD_STRING;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import itest.cloud.page.Page;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * Manage a basic file upload field consisting of nothing but a file input element.
 * <p>
 * The following actions are available on this element:
 * <ul>
 * <li>{@link #clear()}: Clear the existing file selection from the file upload element.</li>
 * <li>{@link #upload(File[])}: Upload a given array of files from the local file system.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * </p>
 */
public class FileUploadElement extends ElementWrapper {

private static By getInputElementLocator(final boolean relative) {
	return By.xpath((relative ? PERIOD_STRING : EMPTY_STRING) + "//input[@type='file']");
}

public FileUploadElement(final ElementWrapper parent) {
	super(parent);
	this.element = parent.waitForElement(getInputElementLocator(true /*relative*/), timeout(), true /*fail*/, false /*displayed*/);
}

public FileUploadElement(final Page page) {
	super(page);
	this.element = page.waitForElement(getInputElementLocator(false /*relative*/), timeout(), true /*fail*/, false /*displayed*/);
}

public FileUploadElement(final Page page, final BrowserElement fileUploadElement) {
	super(page, fileUploadElement);
}

/**
 * Clear the existing file selection from the file upload element.
 */
public void clear() {
	try {
		this.element.clear();
	}
	catch (WebDriverException e) {
		// Do nothing since a WebDriverException is expected to be raised after clearing the file
		// upload element.
	}
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

@Override
protected By getTitleElementLocator() {
	return null;
}

/**
 * Upload a given array of files from the local file system.
 *
 * @param files The array of files to upload as {@link File}[].
 */
public void upload(final File... files) {
	// Remove the existing file selection from the file input (<input type="file">) element if one exists.
	// If a new file upload is initiated while the path to an uploaded file is in the input element, either
	// the new file upload will be failed or both the new and the old files will be uploaded simultaneously.
	clear();
	// Compose the file paths string.
	StringBuffer filePaths = new StringBuffer();
	for (int i = 0; i < files.length; i++) {
		final String canonicalPath;

		try {
			canonicalPath = files[i].getCanonicalPath();
		}
		catch (IOException e) {throw new ScenarioFailedError(e);}

		filePaths.append(canonicalPath);
		if(i < files.length - 1) filePaths.append("\n");
	}
	// Upload the given files.
	this.element.sendKeys(filePaths.toString());
}
}
