/*********************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
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
package itest.cloud.ibm.page.element;

import org.openqa.selenium.By;

import itest.cloud.ibm.scenario.IbmScenarioUtil;
import itest.cloud.page.Page;
import itest.cloud.page.element.*;

/**
 * This class represents a generic dropdown list element in the WatsonX BI Assistant application and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getBusyIndicatorElementLocator()}: Return the xpaths of element indicating that the element is undergoing an operation (busy).</li>
 * </ul>
 * </p>
 */
public class IbmDropdownlistElement extends DropdownlistElement {

	private static final By EXPANSION_LOCATOR = By.xpath(".//*[contains(@class,'menu-icon')]");
	private static final By SELECTION_LOCATOR = By.xpath(".//*[contains(@class,'list-box__label')]");
	private static final By OPTION_LOCATOR = By.xpath(".//*[contains(@class,'option')]");

public IbmDropdownlistElement(final ElementWrapper parent, final BrowserElement webElement) {
	super(parent, webElement, EXPANSION_LOCATOR, SELECTION_LOCATOR, OPTION_LOCATOR);
}

public IbmDropdownlistElement(final ElementWrapper parent, final BrowserElement webElement, final By expansionLocator, final By selectionLocator, final By optionLocator) {
	super(parent, webElement, expansionLocator, selectionLocator, optionLocator);
}

public IbmDropdownlistElement(final ElementWrapper parent, final By locator) {
	super(parent, locator, EXPANSION_LOCATOR, SELECTION_LOCATOR, OPTION_LOCATOR);
}

public IbmDropdownlistElement(final ElementWrapper parent, final By locator, final By expansionLocator, final By selectionLocator, final By optionLocator) {
	super(parent, locator, expansionLocator, selectionLocator, optionLocator);
}

public IbmDropdownlistElement(final Page page, final BrowserElement webElement) {
	super(page, webElement, EXPANSION_LOCATOR, SELECTION_LOCATOR, OPTION_LOCATOR);
}

public IbmDropdownlistElement(final Page page, final BrowserElement webElement, final By expansionLocator, final By selectionLocator, final By optionLocator) {
	super(page, webElement, expansionLocator, selectionLocator, optionLocator);
}

public IbmDropdownlistElement(final Page page, final By locator) {
	super(page, locator, EXPANSION_LOCATOR, SELECTION_LOCATOR, OPTION_LOCATOR);
}

public IbmDropdownlistElement(final Page page, final By findBy, final By expansionLocator, final By selectionLocator, final By optionLocator) {
	super(page, findBy, expansionLocator, selectionLocator, optionLocator);
}

@Override
protected By getBusyIndicatorElementLocator() {
	return IbmScenarioUtil.getBusyIndicatorElementLocator(true /*relative*/);
}
}
