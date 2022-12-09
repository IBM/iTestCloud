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
package com.ibm.itest.cloud.acme.pages.dialogs;

import org.openqa.selenium.By;

import com.ibm.itest.cloud.common.pages.WebPage;
import com.ibm.itest.cloud.common.tests.web.WebBrowserFrame;

/**
 * This class represents a generic confirmation dialog and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getCloseButton(boolean)}: Return the xpath of the button to close the window.</li>
 * <li>{@link #getContentElementLocator()}:
 * Return the locator for the content element of the current dialog.</li>
 * <li>{@link #getPrimaryButtonText()}: Return the text of the primary button.</li>
 * <li>{@link #getTitleElementLocator()}:
 * Return the locator for the title element of the current dialog.</li>
 * </ul>
 * </p>
 */
public abstract class AcmeConfirmationDialog extends AcmeWebPageDialog {

	private static final By DIALOG_LOCATOR = By.xpath("//div[(@class='modal-inner') or (@class='modal-dialog') or contains(@class,'modal-container') or contains(@class,'vex-message-dlg')]");
	protected static final String CANCEL_BUTTON_XPATH = ".//*[text()='Cancel']";

public AcmeConfirmationDialog(final WebPage page) {
	super(page, DIALOG_LOCATOR);
}

public AcmeConfirmationDialog(final WebPage page, final String... data) {
	super(page, DIALOG_LOCATOR, data);
}

public AcmeConfirmationDialog(final WebPage page, final WebBrowserFrame frame) {
	super(page, DIALOG_LOCATOR, frame);
}

public AcmeConfirmationDialog(final WebPage page, final WebBrowserFrame frame, final String... data) {
	super(page, DIALOG_LOCATOR, frame, data);
}

@Override
protected String getCloseButton(final boolean validate) {
	return validate ? ".//button[.='" + getPrimaryButtonText() + "']" : CANCEL_BUTTON_XPATH;
}

@Override
protected By getContentElementLocator(){
	return By.xpath(".//div[contains(@class,'modal-content')]");
}

/**
 * Return the text of the primary button.
 *
 * @return The text of the primary button.
 */
protected abstract String getPrimaryButtonText();

@Override
protected By getTitleElementLocator() {
	return By.xpath(".//h3");
}
}
