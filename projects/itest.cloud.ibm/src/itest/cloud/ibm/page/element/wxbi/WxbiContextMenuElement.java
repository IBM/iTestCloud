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
package itest.cloud.ibm.page.element.wxbi;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.element.IbmDynamicDropdownlistElement;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class to represents a generic context menu element where the element is only made available after clicking on the expansion element.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #clickOnExpansionElement(boolean)}: Click on the expansion element in order to toggle the expandable element.</li>
 * </ul>
 * </p>
 */
public class WxbiContextMenuElement extends IbmDynamicDropdownlistElement {

public WxbiContextMenuElement(final ElementWrapper parent, final By locator, final BrowserElement expansionElement, final By selectionLocator, final By optionLocator) {
	super(parent, locator, expansionElement, selectionLocator, optionLocator);
}

public WxbiContextMenuElement(final Page page, final By locator, final BrowserElement expansionElement, final By selectionLocator, final By optionLocator) {
	super(page, locator, expansionElement, selectionLocator, optionLocator);
}

@Override
protected void clickOnExpansionElement(final boolean expand) {
	this.expansionElement.rightClick();
}
}