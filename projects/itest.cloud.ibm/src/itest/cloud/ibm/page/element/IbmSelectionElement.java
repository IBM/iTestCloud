/*********************************************************************
 * Copyright (c) 2018, 2023 IBM Corporation and others.
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

import itest.cloud.page.Page;
import itest.cloud.page.element.*;

/**
 * This class represents a generic selection element such as a check-box and radio-button
 * and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #alter(boolean)}: Alter the selection status of the element.</li>
 * <li>{@link #isSelected()}: Determine whether or not this element is selected or not.</li>
 * <li>{@link #getLabelElementLocator(String, boolean)}:
 * Return the locator of a given label element of the selection element.</li>
 * <li>{@link #select()}: Select or check the given element.</li>
 * <li>{@link #selectByOpeningDialog(Class)}:
 * Select or check the element by opening a ApsPortalConfirmationDialog.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class IbmSelectionElement extends SelectionElement {

	private static final By INPUT_ELEMENT_LOCATOR = By.xpath("./../input");

/**
 * Return the locator of a given label element of the selection element.
 *
 * @param forAttributeValue The value of the `for` attribute of the label element.
 * @param isRelative Specifies whether the locator (xpath) should be relative.
 *
 * @return The locator of the given label element of the selection element
 * as {@link By}.
 */
public static By getLabelElementLocator(final String forAttributeValue, final boolean isRelative) {
	return By.xpath((isRelative? "." : "") + "//label[@for='" + forAttributeValue + "']");
}

public IbmSelectionElement(final ElementWrapper parent, final By labelElementLocator) {
	super(parent, null /*element*/);
	this.labelElement = parent.waitForElement(labelElementLocator);
	this.element = this.labelElement.waitForElement(INPUT_ELEMENT_LOCATOR, false /*displayed*/);
}

public IbmSelectionElement(final ElementWrapper parent, final String forAttributeValue) {
	this(parent, getLabelElementLocator(forAttributeValue, true /*isRelative*/));
}

public IbmSelectionElement(final ElementWrapper parent, final BrowserElement labelElement) {
	this(parent, labelElement.waitForElement(INPUT_ELEMENT_LOCATOR, false /*displayed*/), labelElement);
}

public IbmSelectionElement(final ElementWrapper parent, final BrowserElement element, final BrowserElement labelElement) {
	super(parent, element, labelElement);
}

public IbmSelectionElement(final Page page, final By labelElementLocator) {
	super(page, null /*element*/);
	this.labelElement = page.waitForElement(labelElementLocator);
	this.element = this.labelElement.waitForElement(INPUT_ELEMENT_LOCATOR, false /*displayed*/);
}

public IbmSelectionElement(final Page page, final String forAttributeValue) {
	this(page, getLabelElementLocator(forAttributeValue, false /*isRelative*/));
}

public IbmSelectionElement(final Page page, final BrowserElement labelElement) {
	this(page, labelElement.waitForElement(INPUT_ELEMENT_LOCATOR, false /*displayed*/), labelElement);
}

public IbmSelectionElement(final Page page, final BrowserElement element, final BrowserElement labelElement) {
	super(page, element, labelElement);
}
}