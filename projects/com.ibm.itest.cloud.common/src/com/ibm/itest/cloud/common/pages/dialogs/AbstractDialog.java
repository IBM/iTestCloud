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
package com.ibm.itest.cloud.common.pages.dialogs;

import static com.ibm.itest.cloud.common.performance.PerfManager.PERFORMANCE_ENABLED;
import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.*;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;

import com.ibm.itest.cloud.common.pages.Page;
import com.ibm.itest.cloud.common.pages.elements.BrowserElement;
import com.ibm.itest.cloud.common.performance.PerfManager.RegressionType;
import com.ibm.itest.cloud.common.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.scenario.errors.WaitElementTimeoutError;

/**
 * Abstract class for any window opened as a dialog in a browser page.
 * <p>
 * Following functionalities are specialized by the dialog:
 * <ul>
 * <li>{@link #open(BrowserElement)}: open the window by clicking on the
 * given web element.</li>
 * </ul>
* </p><p>
 * Following operations are also specialized for dialogs:
 * <ul>
 * <li>{@link #closeAction(boolean)}: The action to perform to close the window.</li>
 * </ul>
 * </p><p>
 * <b>There's no public API for this class</b>.
 * </p><p>
 * Internal API methods accessible in the framework are:
 * <ul>
 * <li>{@link #cancelAll()}: Close all possible opened dialogs by clicking on Cancel button or equivalent.</li>
 * <li>{@link #cancelByOpeningPage(Class, String...)}:
 * Close the dialog by opening a web page via clicking on the cancel button (usually the 'Cancel' button).</li>
 * <li>{@link #closeAll()}: Close all possible opened dialogs by clicking on Ok button or equivalent.</li>
 * <li>{@link #closeByOpeningDialog(Class, String...)}:
 * Close the dialog by opening another dialog via clicking on the close button (usually the 'OK' button).</li>
 * <li>{@link #closeByOpeningDialog(boolean, Class, String...)}: Close the dialog by opening another dialog.</li>
 * <li>{@link #closeByOpeningPage(boolean, Class, Action, String...)}:
 * Close the dialog by opening a web page via clicking on the close button (usually the 'OK' button).</li>
 * <li>{@link #closeByOpeningPage(Class, String...)}:
 * Close the dialog by opening a web page via clicking on the close button (usually the 'OK' button).</li>
 * <li>{@link #getTitle()}: Return the title of the dialog.</li>
 * <li>{@link #isOpened()}: Check whether a dialog is opened or not.</li>
 * <li>{@link #open(BrowserElement)}: open the dialog by clicking on the given web element.</li>
 * <li>{@link #opened()}: Get the element on an already opened dialog.</li>
 * </ul>
 * </p><p>
 * Internal API methods accessible from subclasses are:
 * <ul>
 * <li>{@link #clickOnOpenElement(BrowserElement, Action)}: Click on open element in order to open the dialog.</li>
 * <li>{@link #closeAction(boolean)}: The action to perform to close the dialog.</li>
 * <li>{@link #getContentElementLocator()}:
 * Return the locator for the content element of the current dialog.</li>
 * <li>{@link #getExpectedTitle()}: Return the expected title for the current dialog.</li>
 * <li>{@link #getPage()}: Return the web page from which the window has been opened.</li>
 * <li>{@link #getTitleElementLocator()}:
 * Return the locator for the title element of the current dialog.</li>
 * <li>{@link #handleConfirmationPopup()}: Handle possible confirmation popup dialog.</li>
 * <li>{@link #waitForLoadingEnd()}: Wait for the window content to be loaded.</li>
 * </ul>
  * </p>
 */
abstract public class AbstractDialog extends AbstractWindow {

	private List<BrowserElement> alreadyOpenedDialogElements;

public AbstractDialog(final Page page, final By findBy) {
	super(page, findBy);
}

public AbstractDialog(final Page page, final By findBy, final String... data) {
	super(page, findBy, data);
}

public AbstractDialog(final Page page, final By findBy, final String frame) {
	super(page, findBy, frame);
}

/**
 * Close all possible opened dialogs by clicking on Cancel button or equivalent.
 * <p>
 * This is a no-op if there's no dialog opened.
 * </p>
 */
public void cancelAll() {
	closeAll(false /*validate*/);
}

/**
 * Close the dialog by opening a web page via clicking on the cancel button
 * (usually the 'Cancel' button).
 *
 * @param pageClass A class representing the web page opened after closing the dialog.
 * @param pageData Additional information to store in the page when opening it.
 *
 * @return The opened web page as a subclass of {@link Page}.
 */
public <T extends Page> T cancelByOpeningPage(final Class<T> pageClass, final String... pageData) {
	return closeByOpeningPage(false /*validate*/, pageClass, pageData);
}

/**
 * Click on open element in order to open the dialog.
 * <p>
 * Default is to click on the given element and put back the frame if it has been
 * reset or changed to be able to reach it.
 * </p>
 * @param openElement The element on which to click to open the dialog.
 * If <code>null</code>, then do nothing as the dialog is supposed to be already
 * opened.
 * @param postElementClickAction The action to perform after clicking the link as {@link Action}.
 */
protected void clickOnOpenElement(final BrowserElement openElement, final Action postElementClickAction) {
	if (DEBUG) debugPrintln("		+ Click on '"+openElement+"' to open the dialog");

	// If no element was given then do nothing
	if (openElement == null) {
		if (DEBUG) debugPrintln("		  -> do nothing as no element was given");
		return;
	}

	// Select the open element frame if necessary
//	WebBrowserFrame openElementFrame = openElement.getFrame();
//	if (openElementFrame != this.frames[1]) {
//		resetFrame();
//		if (openElementFrame != null) {
//			this.browser.selectFrame(openElementFrame);
//		}
//	}

	// Check that the element is enabled (necessary for button)
	if (!openElement.isEnabled()) {
		throw new WaitElementTimeoutError("Cannot open dialog "+getClassSimpleName(getClass())+" as the open element "+openElement+" is disabled.");
	}

	// Click on the open element.
	// At times, the open element may be obscured by another element and therefore, not be clickable.
	// As a result, a WebDriverException can occur.
	try {
		openElement.click();
	}
	catch (WebDriverException e) {
		// If the openElement.click() method causes a WebDriverException, use JavaScript to perform the
		// click on the open element in this case.
		debugPrintln("Clicking on open element (WebBrowserElement.click()) caused following error. Therefore, try JavaScript (WebBrowserElement.clickViaJavaScript()) to perform click as a workaround.");
		debugPrintln(e.toString());
		debugPrintStackTrace(e.getStackTrace(), 1 /*tabs*/);
		openElement.clickViaJavaScript();
	}

	// Wait one second
	sleep(1);

	// Perform the given action after clicking the link.
	if(postElementClickAction != null) postElementClickAction.perform();

	// Handle confirmation dialogs that might pop-up. New since 5.0.2, only implemented where required.
	handleConfirmationPopup();

//	// Reset frame if necessary
//	if (openElementFrame != this.frames[1] && openElementFrame != null) {
//		resetFrame();
//	}
}

/**
 * {@inheritDoc}
 * <p>
 * Initialize the dialog element in case it was never done before. This is necessary
 * to check that the dialog has finally vanished.
 * </p><p>
 * Such use case might happen when a dialog is opened by another kind of action
 * (e.g. save operation). Then, callers just want to close the dialog in case it has
 * been opened during the action...
 * </p>
 */
@Override
protected void close(final boolean validate) {

	// Initialize the element if it has not been done yet
	if (this.element == null) {
		setElement();
	}

	// Now we can close safely
	super.close(validate);
}

/**
 * {@inheritDoc}
 * <p>
 * A dialog is closed by clicking on the "Close" button
 * </p>
 */
@Override
protected void closeAction(final boolean validate) {
	BrowserElement buttonElement =
		waitForElement(By.xpath(getCloseButton(validate)), timeout(), true /*fail*/, false /*displayed*/);
	this.browser.clickButton(buttonElement, timeout(), false /*validate*/);
}

/**
 * Close all possible opened dialogs by clicking on Ok button or equivalent.
 * <p>
 * This is a no-op if there's no dialog opened.
 * </p>
 */
public void closeAll() {
	closeAll(true /*validate*/);
}

/**
 * Close all possible opened dialogs by clicking on an appropriate button.
 * <p>
 * This is a no-op if there's no dialog opened.
 * </p>
 */
private void closeAll(final boolean validate) {
	if (DEBUG) debugPrintln("		+ Close all possible opened dialogs.");

	// Get all opened dialogs
	List<BrowserElement> openedDialogElements = getOpenedDialogElements(tinyTimeout());

	// Close them all by accepting each dialog from the last to the first (descending order).
	// A dialogs appeared later may cover a former. Therefore, interacting with a former
	// dialog before suppressing the latter may not be possible.
	for (int i = 0; i < openedDialogElements.size(); i++) {
		BrowserElement dialogElement = openedDialogElements.get(openedDialogElements.size()-1-i);
		String buttonXpath = getCloseButton(validate);
		BrowserElement buttonElement = dialogElement.waitForElement(By.xpath(buttonXpath), 1/*sec*/);
		if (buttonElement == null) {
			throw new WaitElementTimeoutError("Cannot close dialog '" + this.findBy +"' as close button '"+buttonXpath+"' was not found.");
		}
		buttonElement.click();
		dialogElement.waitWhileDisplayed(closeTimeout());
	}
}

/**
 * Close the dialog by opening another dialog.
 * @param validate Specifies whether the close action is to validate or to cancel.
 * @param dialogClass A class representing the new dialog opened after closing this dialog.
 * @param dialogData Additional information to store in the dialog when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link ConfirmationDialog}.
 */
public <P extends ConfirmationDialog> P closeByOpeningDialog(final boolean validate, final Class<P> dialogClass, final String... dialogData) {
	// Perform any actions prior to closing the window.
	if(validate) preCloseActions();

	P confirmationDialog;

	try {
		if((dialogData == null) || (dialogData.length == 0)) {
			confirmationDialog = dialogClass.getConstructor(Page.class).newInstance(getPage());
		}
		else {
			confirmationDialog = dialogClass.getConstructor(Page.class, String[].class).newInstance(getPage(), dialogData);
		}

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

	confirmationDialog.open(waitForElement(By.xpath(getCloseButton(validate))));
	return confirmationDialog;
}

/**
 * Close the dialog by opening another dialog via clicking on the close button
 * (usually the 'OK' button).
 *
 * @param dialogClass A class representing the new dialog opened after closing this dialog.
 * @param dialogData Additional information to store in the dialog when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link ConfirmationDialog}.
 */
public <P extends ConfirmationDialog> P closeByOpeningDialog(final Class<P> dialogClass, final String... dialogData) {
	return closeByOpeningDialog(true /*validate*/, dialogClass, dialogData);
}

/**
 * Close the dialog by opening a web page.
 *
 * @param validate Specifies whether the close action is to validate or to cancel.
 * @param pageClass A class representing the web page opened after closing the dialog.
 * @param postLinkClickAction The action to perform after clicking the link as {@link Action}.
 * @param pageData Additional information to store in the page when opening it.
 *
 * @return The opened web page as a subclass of {@link Page}.
 */
<T extends Page> T closeByOpeningPage(final boolean validate, final Class<T> pageClass, final Action postLinkClickAction, final String... pageData) {
	// Perform any actions prior to closing the window.
	if(validate) preCloseActions();

	return openPageUsingLink(waitForElement(By.xpath(getCloseButton(validate))), pageClass, postLinkClickAction, pageData);
}

/**
 * Close the dialog by opening a web page.
 *
 * @param validate Specifies whether the close action is to validate or to cancel.
 * @param pageClass A class representing the web page opened after closing the dialog.
 * @param pageData Additional information to store in the page when opening it.
 *
 * @return The opened web page as a subclass of {@link Page}.
 */
<T extends Page> T closeByOpeningPage(final boolean validate, final Class<T> pageClass, final String... pageData) {
	return closeByOpeningPage(validate, pageClass, null /*postLinkClickAction*/, pageData);
}

/**
 * Close the dialog by opening a web page.
 *
 * @param pageClass A class representing the web page opened after closing the dialog.
 * @param postLinkClickAction The action to perform after clicking the link as {@link Action}.
 * @param pageData Additional information to store in the page when opening it.
 *
 * @return The opened web page as a subclass of {@link Page}.
 */
public <T extends Page> T closeByOpeningPage(final Class<T> pageClass, final Action postLinkClickAction, final String... pageData) {
	return closeByOpeningPage(true /*validate*/, pageClass, postLinkClickAction, pageData);
}

/**
 * Close the dialog by opening a web page via clicking on the close button
 * (usually the 'OK' button).
 *
 * @param pageClass A class representing the web page opened after closing the dialog.
 * @param pageData Additional information to store in the page when opening it.
 *
 * @return The opened web page as a subclass of {@link Page}.
 */
public <T extends Page> T closeByOpeningPage(final Class<T> pageClass, final String... pageData) {
	return closeByOpeningPage(true /*validate*/, pageClass, pageData);
}

/**
 * Return the locator for the content element of the current dialog.
 *
 * @return The content element locator as a {@link By}.
 */
protected abstract By getContentElementLocator();

private List<BrowserElement> getMatchingDialogElements(final int timeout) {
	List<BrowserElement> matchingDialogElements;
	long timeoutMillis = timeout * 1000 + System.currentTimeMillis();

	do {
		matchingDialogElements = getMatchingDialogElements(getOpenedDialogElements(tinyTimeout()));
	} while(matchingDialogElements.isEmpty() && (System.currentTimeMillis() <= timeoutMillis));

	return matchingDialogElements;
}

private List<BrowserElement> getMatchingDialogElements(final List<BrowserElement> openedDialogElements) {
	// If a title is not expected for the dialog, no further checking is need.
	if(!isTitleExpected()) {
		return openedDialogElements;
	}

	// If reached here, a title is expected for the dialog. Therefore, check the title.
	List<BrowserElement> matchingDialogElements = new ArrayList<BrowserElement>();

	for (BrowserElement openedDialogElement : openedDialogElements) {
		BrowserElement titleElement =
			openedDialogElement.waitForElement(getTitleElementLocator(), tinyTimeout(), false /*fail*/);

		if((titleElement != null) && getExpectedTitle().matcher(titleElement.getText()).matches()) {
			matchingDialogElements.add(openedDialogElement);
		}
	}

	return matchingDialogElements;
}

/**
 * Get the opened dialog elements.
 *
 * @param seconds Time to wait for the elements
 * @return The list of opened dialog elements as a {@link List} of {@link BrowserElement}
 */
protected List<BrowserElement> getOpenedDialogElements(final int seconds) {
	return waitForElements(getParentElement(), this.findBy, seconds, false /*fail*/);
}

/**
 * Handle possible confirmation popup dialog.
 * <p>
 * Method designed to be overridden by subclasses. Sometimes during opening a
 * dialog, another pop-up dialog appears, e.g., to confirm before an action is
 * finished. In order to keep the opening of general dialogs at this class
 * level, need to push the ability to handle miscellaneous pop-ups down to the
 * sub-classes. Those sub-class implementations should be limited to specific
 * cases, e.g., a confirmation dialog that appears (as of 5.0.2) when adding
 * test cases to a test suite.
 * </p>
 * @since 5.0.2
 */
protected void handleConfirmationPopup() {
	// do nothing;
}

/**
 * Check whether a dialog is opened or not.
 * <p>
 * Note that this method will also return true in case several dialogs are opened.
 * </p><p>
 * Searching for opened dialogs lasts one second.
 * </p>
 * @return <code>true</code> if at least one dialog is opened, <code>false</code>
 * otherwise.
 */
public boolean isOpened() {
	return isOpened(1 /*second*/);
}

/**
 * Check whether a dialog is opened or not during the given amount of seconds.
 * <p>
 * Note that this method will also return true in case several dialogs are opened.
 * </p>
 * @param timeout The timeout in seconds for the dialog to be opened.
 * @return <code>true</code> if at least one dialog is opened, <code>false</code>
 * otherwise.
 */
public boolean isOpened(final int timeout) {
	if (DEBUG) debugPrintln("		+ Check whether a dialog is opened or not");

	try {
		return !getMatchingDialogElements(timeout).isEmpty();
	}
	catch (NoSuchWindowException e) {
		return false;
	}
}

@Override
public BrowserElement open(final BrowserElement openElement) {
	return open(openElement, null /*postElementClickAction*/);
}

/**
 * Open the window by clicking on the given web element.
 *
 * @param openElement The element on which to perform the open action.
 * @param postElementClickAction The action to perform after clicking the element as {@link Action}.
 *
 * @return The web element matching the opened window as a {@link BrowserElement}.
 */
public BrowserElement open(final BrowserElement openElement, final Action postElementClickAction) {
	if (DEBUG) debugPrintln("		+ Open "+getClassSimpleName(getClass())+" dialog");

	// Get list of already opened dialog IDs
	this.alreadyOpenedDialogElements = getOpenedDialogElements(tinyTimeout());

	// Start performance timer
   	if (PERFORMANCE_ENABLED) {
		this.browser.getPerfManager().startServerTimer();
   	}

	// Click on element which opens the dialog
	clickOnOpenElement(openElement, postElementClickAction);

	// Wait for dialog web element
	setElement();

	// Loop until having got the web element
	if (DEBUG) debugPrintln("		  -> timeout=" + (this.max * timeout()) + " seconds");
	int count = 0;
	while (this.element == null) {
		if (count++ > this.max) {
			throw new WaitElementTimeoutError("Failing to open the dialog "+this);
		}

	    setElement();
	}

	// Purge alerts if any
	if (this.browser.purgeAlerts("Open dialog "+this.findBy+"from "+this.page) > 0) {
		if (!this.element.isDisplayed(false)) {
			// Workaround
			debugPrintln("Workaround: The dialog was closed while purging alerts, try to open it again...");
			return open(openElement);
		}
	}

	// Add performance result
	if (PERFORMANCE_ENABLED) {
		this.page.addPerfResult(RegressionType.CLIENT, this.page.getTitle()+": Action: "+this.findBy);
	}
	// Wait for the dialog to load.
	waitForLoadingEnd();
	// Return the opened dialog
	return this.element;
}

/**
 * Get the element on an already opened dialog.
 *
 * @return The dialog as a {@link AbstractDialog} subclass.
 */
public BrowserElement opened() {
	if (DEBUG) debugPrintln("		+ Initialize the opened "+getClassSimpleName(getClass())+" dialog");
	setElement();
	if (this.element == null) {
		throw new WaitElementTimeoutError("Cannot find any dialog with corresponding xpath: "+this.findBy);
	}
	// Wait for the loading to finish.
	waitForLoadingEnd();

	return this.element;
}

/**
 * Initialize the dialog element.
 */
protected void setElement() {
	// Get list of opened dialog elements
   	List<BrowserElement> openedDialogElements = getOpenedDialogElements(2/*sec*/);
   	// Remove the dialogs, which already existed, from the consideration.
   	if(this.alreadyOpenedDialogElements != null) openedDialogElements.removeAll(this.alreadyOpenedDialogElements);
   	// Remove the dialogs, which do not have a matching title, from the consideration.
   	List<BrowserElement> matchingDialogElements = getMatchingDialogElements(openedDialogElements);

   	// Go through the map to see if there's any doubled opened dialog
   	final int size = matchingDialogElements.size();
   	switch (size) {
   		case 1:
   			// We got it, store in window and leave the loop
   			this.element = matchingDialogElements.get(0);
   			break;
   		case 0:
   			// No opened dialog was found
   			debugPrintln("WARNING: No opened dialog was found after having clicked on open element.");
   			break;
   		default:
   			// Apparently, there are several dialogs opened, hence close all but the last one
   			debugPrintln("WARNING: "+size+" dialogs have been found after having clicked on open element, keep the last one and close all others");
   			for (int i=0; i<size-1; i++) {
	   			BrowserElement windowElement = matchingDialogElements.get(i);
   				debugPrintln("	-> close dialog '"+windowElement+"' by clicking on "+getCloseButton(false)+" button.");
	   			windowElement.findElement(By.xpath(getCloseButton(false))).click();
   			}
   			this.element = matchingDialogElements.get(size-1);
			debugPrintln("	-> keep dialog '"+this.element+"'.");
   			sleep(2);
   			break;
   	}
}

/**
 * Wait for the content element to be loaded.
 *
 * @return The content element as {@link BrowserElement}.
 */
protected BrowserElement waitForContentElement() {
	return waitForElement(getContentElementLocator());
}

@Override
public void waitForLoadingEnd() {
	// Wait for loading to finish.
	super.waitForLoadingEnd();

	// Wait for the content element to be loaded.
	waitForContentElement();
}

/**
 * {@inheritDoc}
 * <p>
 * Overridden to set the element in case it's <code>null</code>
 * </p>
 */
@Override
public void waitWhileDisplayed(final int seconds) throws ScenarioFailedError {
	if (this.element == null) {
		setElement();
	}
	super.waitWhileDisplayed(seconds);
}
}