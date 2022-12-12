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
package com.ibm.itest.cloud.acme.pages.elements;

import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.println;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import com.ibm.itest.cloud.acme.pages.dialogs.AcmeConfirmationDialog;
import com.ibm.itest.cloud.common.pages.WebPage;
import com.ibm.itest.cloud.common.pages.elements.WebBrowserElement;
import com.ibm.itest.cloud.common.pages.elements.WebElementWrapper;
import com.ibm.itest.cloud.common.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.scenario.errors.WaitElementTimeoutError;

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
public class AcmeSelectionElement extends AcmeWebElementWrapper {

	private static final By INPUT_ELEMENT_LOCATOR = By.xpath("./../input");

/**
 * Return the locator of a given label element of the selection element.
 *
 * @param label The text of the label element.
 * @param isRelative Specifies whether the locator (xpath) should be relative.
 *
 * @return The locator of the given label element of the selection element
 * as {@link By}.
 */
public static By getLabelElementLocator(final String label, final boolean isRelative) {
	return By.xpath((isRelative? "." : "") + "//label[.='" + label + "']");
}

	private final WebBrowserElement labelElement;

public AcmeSelectionElement(final WebElementWrapper parent, final By labelElementLocator) {
	super(parent);
	this.labelElement = parent.getElement().waitForElement(labelElementLocator);
	this.element = this.labelElement.waitForElement(INPUT_ELEMENT_LOCATOR, false /*displayed*/);
}

public AcmeSelectionElement(final WebElementWrapper parent, final String label) {
	this(parent, getLabelElementLocator(label, true /*isRelative*/));
}

public AcmeSelectionElement(final WebElementWrapper parent, final WebBrowserElement labelElement) {
	this(parent, labelElement.waitForElement(INPUT_ELEMENT_LOCATOR, false /*displayed*/), labelElement);
}

public AcmeSelectionElement(final WebElementWrapper parent, final WebBrowserElement element, final WebBrowserElement labelElement) {
	super(parent, element);
	this.labelElement = labelElement;
}

public AcmeSelectionElement(final WebPage page, final By labelElementLocator) {
	super(page);
	this.labelElement = waitForElement(labelElementLocator);
	this.element = this.labelElement.waitForElement(INPUT_ELEMENT_LOCATOR, false /*displayed*/);
}

public AcmeSelectionElement(final WebPage page, final String label) {
	this(page, getLabelElementLocator(label, false /*isRelative*/));
}

public AcmeSelectionElement(final WebPage page, final WebBrowserElement labelElement) {
	this(page, labelElement.waitForElement(INPUT_ELEMENT_LOCATOR, false /*displayed*/), labelElement);
}

public AcmeSelectionElement(final WebPage page, final WebBrowserElement element, final WebBrowserElement labelElement) {
	super(page, element);
	this.labelElement = labelElement;
}

/**
 * Alter the selection status of the element.
 * <p>
 * This operation only applies to input elements such as checkboxes, options in a
 * select and radio buttons. The element status will only be altered if the current
 * status is different from the provided.
 * </p>
 * @param select Specifies whether to select or clear the element. The value <b>true</b>
 * or <b>false</b> implies that the element should be selected or cleared respectively.
 *
 * @return <code>true</code> if an alteration is needed or <code>false</code> otherwise.
 */
public boolean alter(final boolean select) {
	boolean selectionNeeded = (select && !this.element.isSelected()) || (!select && this.element.isSelected());

	if (selectionNeeded) {
		this.labelElement.click();
	}

	return selectionNeeded;
}

/**
 * Determine whether or not this element is selected.
 * <p>
 * This operation only applies to input elements such as checkboxes,
 * options in a select and radio buttons.
 * </p>
 * @return True if the element is currently selected or checked, false otherwise.
 */
public boolean isSelected(){
	return this.element.isSelected();
}

/**
 * Select or check the element.
 * <p>
 * This operation only applies to input elements such as checkboxes,
 * options in a select and radio buttons. The element will only be
 * selected/checked if it has not been selected/checked already.
 * </p>
 *
 * @return <code>true</code> if a selection is needed or <code>false</code> otherwise.
 */
public boolean select() {
	return alter(true /* select */);
}

/**
 * Select or check the element by opening a {@link AcmeConfirmationDialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param dialogClass A class representing the confirmation dialog opened
 * after selecting the element.
 *
 * @return The opened confirmation dialog as a subclass of {@link AcmeConfirmationDialog}.
 */
public <P extends AcmeConfirmationDialog> P selectByOpeningDialog(final Class<P> dialogClass) {
	// Throw an exception if the selection element has already been selected
	// since the dialog can not be opened and returned back to caller.
	if (isSelected()) throw new ScenarioFailedError("The element had already been selected. Therefore, the associated dialog can not be opened");
	P confirmationDialog;

	try {
		confirmationDialog = dialogClass.getConstructor(WebPage.class).newInstance(getPage());
	}
	catch (WebDriverException e) {
		throw e;
	}
	catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
		println("Exception cause: " + e.getCause());
		throw new ScenarioFailedError(e);
	}
	catch (Throwable e) {
		println("Exception cause: " + e.getCause());
		throw new WaitElementTimeoutError(e);
	}

	confirmationDialog.open(this.labelElement);
	return confirmationDialog;
}
}
