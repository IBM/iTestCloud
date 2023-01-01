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

import java.util.List;

import org.openqa.selenium.By;

import com.ibm.itest.cloud.common.pages.Page;
import com.ibm.itest.cloud.common.pages.elements.BrowserElement;
import com.ibm.itest.cloud.common.pages.frames.BrowserFrame;
import com.ibm.itest.cloud.common.scenario.errors.ScenarioFailedError;

/**
 * Abstract class for any dialog opened in a frame.
 * <p>
 * Basically all superclass method which needs to access a web element are
 * overridden to select the browser frame before performing the corresponding action.
 */
abstract public class InFrameDialog extends AbstractDialog {

	/**
	 * The frames that the windows has to deal with:
	 * <ul>
	 * <li>slot 0: The browser frame when the dialog was opened.
	 * <p>
	 * It's important to store this piece of information to be able to restore it
	 * when closing the dialog.
	 * </p>
	 * </li>
	 * <li>slot 1: The frame used by the dialog
	 * <p>
	 * Can be <code>null</code> if no frame is used by the window
	 * </p><p>
	 * Note that not all the window elements are supposed to be in this frame,
	 * typically window title is not in this frame
	 * </p>
	 * </li>
	 * <li>slot 2: The current used frame.
	 * <p>
	 * If slot 1 is null, then this slot is always <code>null</code>, otherwise
	 * it can be either equals to slot 1 if frame elements want to be found or
	 * <code>null</code>  if other elements are searched.
	 * </p>
	 * </li>
	 * </ul>
	 */
	protected BrowserFrame[] frames;

public InFrameDialog(final Page page, final By findBy, final BrowserFrame frame) {
	super(page, findBy);
	this.frames = new BrowserFrame[3];
	this.frames[0] = this.browser.getCurrentFrame();
	this.frames[1] = frame;
}

/**
 * {@inheritDoc}
 * <p>
 * Select the frame before performing the close action.
 * </p>
 */
@Override
protected void closeAction(final boolean cancel) {
	selectFrame();
	super.closeAction(cancel);
}

/**
 * Return the frame used inside the wrapped element.
 *
 * @return The frame as a {@link BrowserFrame} or <code>null</code> if
 * no frame is used.
 */
protected BrowserFrame getFrame() {
	return this.frames[1];
}

/**
 * {@inheritDoc}
 * <p>
 * Select the frame before looking for opened dialogs.
 * </p>
 */
@Override
protected List<BrowserElement> getOpenedDialogElements(final int seconds) {
	selectFrame();
	return super.getOpenedDialogElements(seconds);
}

/**
 * {@inheritDoc}
 * <p>
 * Select the frame before opening the dialog.
 * </p>
 */
@Override
public BrowserElement open(final BrowserElement openElement) {
	selectFrame();
	return super.open(openElement);
}

/**
 * Select the frame in which the current window is expected to be found.
 */
protected void selectFrame() {
	if (this.frames[2] != this.frames[1]) { // == is intentional
		this.frames[2] = this.frames[1];
		this.frames[2].switchTo();
	}
}

/**
 * Store the browser the frame.
 * <p>
 * As frame might not be set when building the wrapper, this method allows
 * subclasses to store the current browser frame when they know that it matches
 * the one displayed inside by the wrapped element.
 * </p>
 */
protected void storeBrowserFrame() {
	this.frames[1] = this.browser.getCurrentFrame();
}

/**
 * Select the frame before waiting for the element.
 * </p>
 * @throws ScenarioFailedError If the frame argument is true as the frame is
 * already known.
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By locator, final int timeout, final boolean isInFrame) {
	if (isInFrame) {
		throw new ScenarioFailedError("Should not find element in frames in framed dialog.");
	}

	// Select frame
	selectFrame();

	// Wait for the element
	return waitForElement(parentElement, locator, timeout);
}
}