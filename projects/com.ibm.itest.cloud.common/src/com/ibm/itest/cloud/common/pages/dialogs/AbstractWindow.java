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

import org.openqa.selenium.By;

import com.ibm.itest.cloud.common.Constants;
import com.ibm.itest.cloud.common.config.Config;
import com.ibm.itest.cloud.common.pages.Page;
import com.ibm.itest.cloud.common.pages.elements.*;
import com.ibm.itest.cloud.common.pages.frames.BrowserFrame;
import com.ibm.itest.cloud.common.pages.frames.NamedFrame;
import com.ibm.itest.cloud.common.performance.PerfManager.RegressionType;
import com.ibm.itest.cloud.common.scenario.errors.ScenarioFailedError;

/**
 * Abstract class for any window opened in a browser page.
 * <p>
 * All necessary information to find the window in the page has to be given when
 * creating an instance of this class. Then, it will be possible to open it at any time,
 * but also to recover it if troubles occur during the opening operation (typically
 * if the window does not show up after having performed the expected operation...).
 * </p><p>
 * Public API for this class is defined in {@link Window} interface.
 * </p><p>
 * Internal API methods accessible in the framework are:
 * <ul>
 * <li>{@link #open(BrowserElement)}: open the window by clicking on the given web element.</li>
 * </ul>
 * </p><p>
 * Internal API methods accessible from subclasses are:
 * <ul>
 * <li>{@link #checkErrorMessage()}: Check whether an error message is displayed or not.</li>
 * <li>{@link #closeAction(boolean)}: The action to perform to close the window.</li>
 * <li>{@link #closeTimeout()}: Time allowed to close the window.</li>
 * <li>{@link #getCloseButton(boolean)}: Return the xpath of the button to close the window.</li>
 * <li>{@link #open(BrowserElement)}: open the window by clicking on the given web element.</li>
 * <li>{@link #waitForLoadingEnd()}: Wait for the window content to be loaded.</li>
 * <li>{@link #preCloseActions()}: Perform any actions prior to closing the window.</li>
 * </ul>
 * </p>
 */
abstract public class AbstractWindow extends TitledElementWrapper implements Window, Constants {

	/**
	 *  The mechanism to find the opened window in the web page.
	 */
	protected final By findBy;

	/**
	 * The maximum number of attempts while searching the window.
	 */
	final int max;

public AbstractWindow(final Page page, final By findBy) {
	this(page, findBy, (String[]) null);
}

public AbstractWindow(final Page page, final By findBy, final String... data) {
	this(page, findBy, (BrowserFrame) null, data);
}

public AbstractWindow(final Page page, final By findBy, final String frame) {
	this(page, findBy, new NamedFrame(page.getBrowser(), frame));
}

public AbstractWindow(final Page page, final By findBy, final BrowserFrame frame, final String... data) {
	super(page, frame, data);
	this.findBy = findBy;
	this.max = BrowserElement.MAX_RECOVERY_ATTEMPTS;
}

@Override
public final void cancel() {
	close(false/*cancel*/);
}

/**
 * Check whether an error message is displayed or not.
 * <p>
 * Default is to do nothing. Subclasses have to implement specific code
 * to get the error message displayed in the window.
 * </p>
 * @throws ScenarioFailedError With displayed message if any.
 */
protected void checkErrorMessage() throws ScenarioFailedError {
	// Do nothing by default
}

@Override
public final void close() {
	close(true/*validate*/);
}

protected void close(final boolean validate) {
	// Start server time if performances are managed
	if (PERFORMANCE_ENABLED) {
		this.page.startPerfManagerServerTimer();
	}

	// Perform any actions prior to closing the window
	if(validate) preCloseActions();

	// Perform the close action
	closeAction(validate);

	// Wait for the window to vanish
	waitWhileDisplayed(closeTimeout());

	// Add performance result
	if (PERFORMANCE_ENABLED) {
		this.page.addPerfResult(RegressionType.SERVER, this.page.getTitle());
	}
}

/**
 * The action to perform to close the window.
 *
 * @param validate Tells whether the close action is to validate or to cancel.
 */
protected abstract void closeAction(boolean validate);

/**
 * Time allowed to close the window.
 * <p>
 * Default value is got from {@link Config#closeDialogTimeout()}.
 * </p>
 * @return The timeout in seconds as an <code>int</code>.
 */
protected int closeTimeout() {
	return getConfig().closeDialogTimeout();
}

/**
 * Return the xpath of the button to close the window.
 *
 * @param validate Tells whether the close action is to validate or to cancel.
 * @return The xpath of the button as a {@link String}
 */
protected abstract String getCloseButton(boolean validate);

@Override
public boolean isCloseable() {
	// Get web element for close button
	BrowserElement closeButtonElement =
		this.element.waitForElement(By.xpath(getCloseButton(true/*validate*/)), tinyTimeout());

	// If button is found then return whether it's enabled or not
	if (closeButtonElement != null) {
		return closeButtonElement.isEnabled();
	}

	// No button found, hence it's not closeable
	return false;
}

/**
 * Open the window by clicking on the given web element.
 *
 * @param openElement The element on which to perform the open action.
 * @return The web element matching the opened window as a {@link BrowserElement}.
 */
abstract public BrowserElement open(final BrowserElement openElement);

/**
 * Perform any actions prior to closing the window.
 */
protected void preCloseActions() {
	// Do nothing by default.
}
}
