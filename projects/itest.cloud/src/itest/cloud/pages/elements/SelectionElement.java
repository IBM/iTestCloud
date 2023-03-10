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
package itest.cloud.pages.elements;

import static itest.cloud.scenario.ScenarioUtils.*;

import org.openqa.selenium.WebDriverException;

import itest.cloud.pages.Page;
import itest.cloud.pages.dialogs.ConfirmationDialog;
import itest.cloud.scenario.errors.ScenarioFailedError;
import itest.cloud.scenario.errors.WaitElementTimeoutError;

/**
 * This class represents a complex selection element such as a check-box and radio-button
 * which is composed of an input and a label element.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #alter(boolean)}: Alter the selection status of the element.</li>
 * <li>{@link #isSelected()}: Determine whether or not this element is selected or not.</li>
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
public class SelectionElement extends ElementWrapper {

	protected BrowserElement labelElement;

protected SelectionElement(final ElementWrapper parent, final BrowserElement element) {
	super(parent, element);
	this.labelElement = null;
}

protected SelectionElement(final Page page, final BrowserElement element) {
	super(page, element);
	this.labelElement = null;
}

public SelectionElement(final ElementWrapper parent, final BrowserElement element, final BrowserElement labelElement) {
	super(parent, element);
	this.labelElement = labelElement;
}

public SelectionElement(final Page page, final BrowserElement element, final BrowserElement labelElement) {
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
 * @param selection Specifies whether to select or clear the element. The value <b>true</b>
 * or <b>false</b> implies that the element should be selected or cleared respectively.
 *
 * @return <code>true</code> if an alteration is needed or <code>false</code> otherwise.
 */
public boolean alter(final boolean selection) {
	boolean selectionNeeded = (selection != isSelected());

	if (selectionNeeded) {
		// Perform the action.
		performAction();

		// Wait for the action to be reflected in the UI.
		int timeout = 1;
		long timeoutMillis = timeout * 60 * 1000 + System.currentTimeMillis();

		while(selection != isSelected()) {
			if (System.currentTimeMillis() > timeoutMillis) {
				throw new WaitElementTimeoutError("State of selection element remained unchanged at '" + isSelected() + "' even after attempting to change it state to '" + selection + "' before timeout '" + timeout + "' minute.");
			}
		}
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
 * Perform the selection action.
 */
protected void performAction() {
	// Click on the label element.
	// At times, the label element may be obscured by another element and therefore, not be clickable.
	// As a result, a WebDriverException can occur.
	try {
		this.labelElement.click();
	}
	catch (WebDriverException e) {
		// If the labelElement.click() method causes a WebDriverException, use JavaScript to perform the
		// click on the label element in this case.
		debugPrintln("Clicking on label element (BrowserElement.click()) caused following error. Therefore, try JavaScript (BrowserElement.clickViaJavaScript()) to perform click as a workaround.");
		debugPrintln(e.toString());
		debugPrintStackTrace(e.getStackTrace(), 1 /*tabs*/);
		this.labelElement.clickViaJavaScript();
	}
}

/**
 * Scroll the page to the element.
 * <p>
 * This is a no-op if the web element is already visible in the browser view.
 * </p>
 */
public void scrollIntoView() {
	this.labelElement.scrollIntoView();
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
 * Select or check the element by opening a {@link ConfirmationDialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param dialogClass A class representing the confirmation dialog opened
 * after selecting the element.
 *
 * @return The opened confirmation dialog as a subclass of {@link ConfirmationDialog}.
 */
public <P extends ConfirmationDialog> P selectByOpeningDialog(final Class<P> dialogClass) {
	// Throw an exception if the selection element has already been selected
	// since the dialog can not be opened and returned back to caller.
	if (isSelected()) throw new ScenarioFailedError("The element had already been selected. Therefore, the associated dialog can not be opened");
	P confirmationDialog;

	try {
		confirmationDialog = dialogClass.getConstructor(Page.class).newInstance(getPage());
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
