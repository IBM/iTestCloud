/*********************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
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
package itest.cloud.ibm.page.element.wxbi.modeling;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.wxbia.modeling.WxbiModelingPage;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents a tab element in {@link WxbiModelingPage} and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getTableElement()}: Return the table element.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #waitForLoadingEnd()}: Wait for loading of the wrapped element to complete.</li>
 * </ul>
 * </p>
 */
public class WxbiGridTabElement extends WxbiModelingPageTabElement {

	private static final By TABLE_ELEMENT_LOCATOR = By.xpath(".//*[contains(@class,'arrow-key-stepper')]");

public WxbiGridTabElement(final Page page) {
	super(page);
}

/**
 * Return the table element.
 *
 * @return The table element as {@link BrowserElement}.
 */
public BrowserElement getTableElement() {
	return waitForElement(By.xpath(""));
}

@Override
public void waitForLoadingEnd() {
	super.waitForLoadingEnd();
	// Wait for the selected table to load or a message to appear asking to select a table.
	waitForMultipleElements(TABLE_ELEMENT_LOCATOR, By.xpath(".//*[contains(text(),'To preview data, select a table')]"));
}
}