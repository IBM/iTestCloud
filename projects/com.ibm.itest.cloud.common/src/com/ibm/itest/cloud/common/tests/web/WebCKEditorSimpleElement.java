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
package com.ibm.itest.cloud.common.tests.web;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.ibm.itest.cloud.common.tests.scenario.errors.ScenarioFailedError;

/**
 * Class to handle a CKEditor simple element.
 * <p>
 * Following features are accessible in this page:
 * <ul>
 * <li>{@link #setContent(String)}: Set the editor content with the given text.</li>
 * </ul>
 * </p><p>
 * Following internal features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class WebCKEditorSimpleElement extends WebElementWrapper {

public static final By CKEDITOR_LOCATOR = By.xpath(".//div[starts-with(@class,'RichTextEditorWidget')]");

public WebCKEditorSimpleElement(final WebPage page, final WebBrowserElement element) {
	super(page, element);
}

public WebCKEditorSimpleElement(final WebPage page, final WebBrowserElement element, final boolean parent) {
	super(page, parent ? element.findElement(CKEDITOR_LOCATOR) : element);
}

/**
 * Set the editor content with the given text.
 *
 * @param text The text to set the editor content with
 */
public void setContent(final String text) {

	// Type text in the editor
	typeText(this.element, text);

	// Check that the text was well entered
	if (!this.element.getText().equals(text)) {

		// Hit ENTER key to enter in edit mode
		this.element.sendKeys(Keys.ENTER);

		// Enter text again
		typeText(this.element, text);

		// Give up if the text was still not set
		if (!this.element.getText().equals(text)) {
			throw new ScenarioFailedError("Cannot set text for CKEditor element '"+this.element+"' in page '"+getPage().getTitle()+"'");
		}
	}
}
}
