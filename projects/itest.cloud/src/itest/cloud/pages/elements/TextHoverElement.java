/*********************************************************************
 * Copyright (c) 2012, 2022 IBM Corporation and others.
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

import org.openqa.selenium.*;

import itest.cloud.pages.Page;
import itest.cloud.pages.dialogs.AbstractWindow;
import itest.cloud.scenario.errors.WaitElementTimeoutError;

/**
 * Abstract class for any window opened as a rich hover in a browser page.
 * <p>
 * Following functionalities are specialized by the rich hover:
 * <ul>
 * <li>{@link #getText()}: Return the text content of the hover.</li>
 * <li>{@link #open(BrowserElement)}: open the window by clicking on the
 * given web element.</li>
 * </ul>
* </p><p>
 * Following operations are also defined or specialized for rich hovers:
 * <ul>
 * <li>{@link #closeAction(boolean)}: The action to perform to close the window.</li>
 * <li>{@link #getCloseButton(boolean)}: The button to close the hover.</li>
 * </ul>
  * </p>
 */
abstract public class TextHoverElement extends AbstractWindow {

	/**
	 * Default locator for the hover.
	 */
	private static final By DEFAULT_TEXT_HOVER_LOCATOR = By.xpath("//div[starts-with(@id,'jazz_ui_internal__MasterPopup')]");

	// The link element on which the hover is created
	protected BrowserElement linkElement;

public TextHoverElement(final Page page) {
	super(page, DEFAULT_TEXT_HOVER_LOCATOR);
}

/**
 * Wait that a text hover gets opened and cancel it.
 * <p>
 * This is a convenient method to close any kind of hover while moving to an
 * element to ensure it's visible in the page.
 * </p><p>
 * Of course, this is a no-op if there's no hover currently opened.
 * </p>
 * @param page The page on which the hover occurs.
 */
public static void waitAndCancel(final Page page) {
	BrowserElement dialogElement = page.waitForElement(DEFAULT_TEXT_HOVER_LOCATOR, 1 /*sec*/, false /*fail*/);
	if (dialogElement != null) {
		pause(250);
		dialogElement.sendKeys(Keys.ESCAPE);
		pause(250);
	}
}

/**
 * The action to perform to close the window.
 * <p>
 * There's no close button for this dialog, the only way to close it is to click
 * somewhere else in the page.
 * </p><p>
 * First attempt to do this is to scroll to the top.
 * Second attempt to do hit the Escape key on the link element
 * </p>
 */
@Override
protected void closeAction(final boolean cancel) {
	this.linkElement.sendKeys(Keys.ESCAPE);
}

/**
 * {@inheritDoc}
 * <p>
 * There's no button close this kind of rich hover.
 * </p>
 */
@Override
protected String getCloseButton(final boolean validate) {
	return null;
}

/**
 * Return the link element on which the hover is opened.
 * <p>
 * <b>Warning</b>: This method should not be used from scenarios.
 * </p>
 * @return The link element as a {@link BrowserElement}.
 */
public BrowserElement getLinkElement() {
	return this.linkElement;
}

/**
 * Return the text content of the hover.
 *
 * @return The content as a {@link String}.
 */
@Override
public String getText() {
	if (DEBUG) debugPrintln("		+ Get text for hover "+this.element);

	// Get content element
	BrowserElement contentElement = waitForElement(By.xpath(".//div[@dojoattachpoint='content']"), tinyTimeout(), false /*fail*/);

	// Return text if content is found
	if (contentElement != null) {
		return contentElement.getText();
	}

	// Invalid content, return empty text
	debugPrintln("		  -> no web element matching expected xpath, return empty string");
	return EMPTY_STRING;
}

/**
 * {@inheritDoc}
 * <p>
 * The rich hover is opened by hovering the mouse over the given element.
 * </p>
 */
@Override
public BrowserElement open(final BrowserElement webElement) {

	// Store the link element
	this.linkElement = webElement;

	// Wait for the element to be displayed (allow recovering if element has become stale)
	long timeout = openTimeout() * 1000 + System.currentTimeMillis();	 // Timeout currentTimeMilliseconds
	while (!webElement.isDisplayed()) {
		if (System.currentTimeMillis() > timeout) {
			throw new WaitElementTimeoutError("Cannot get the link element '"+webElement+"' on which rich hover should be opened.");
		}
	}

	// Move the mouse to the link element in order to trigger the hover
	while (true) {
		if (System.currentTimeMillis() > timeout) {
			throw new WaitElementTimeoutError("Cannot open the rich hover over "+webElement);
		}
		try {
//			this.browser.moveToElement(this.linkElement);
			this.linkElement.mouseOver();
			break;
		}
		catch (StaleElementReferenceException sere) {
			debugPrintException(sere);
			webElement.isDisplayed(); // allow recovery
		}
	}

	// Store the hover element
	this.element = waitForElement(null, this.findBy, (int) ((timeout - System.currentTimeMillis())/1000));

	waitForLoadingEnd();

	// Return the opened hover
	return this.element;
}
}
