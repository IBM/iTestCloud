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

public InFrameDialog(final Page page, final By findBy, final BrowserFrame frame) {
	super(page, findBy, frame);
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
 * Select the frame before waiting for the element.
 * </p>
 * @throws ScenarioFailedError If the frame argument is true as the frame is
 * already known.
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By locator, final int timeout, final boolean frame) {
	if (frame) {
		throw new ScenarioFailedError("Should not find element in frames in framed dialog.");
	}

	// Select frame
	selectFrame();

	// Wait for the element
	return waitForElement(parentElement, locator, timeout);
}
}