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
package com.ibm.itest.cloud.common.tests.web;

import org.openqa.selenium.WebDriver;

/**
 * Abstract class for frame used in browser page.
 * <p>
 * {@link WebDriver} API allow to select a browser frame either using an index,
 * a name or a web element. This abstract class allow user to switch to a frame
 * without having to know how to it's accessible through the driver (see {@link #switchTo()}).
 * </p><p>
 * It also allow to get the web element, the index or the name of the current frame.
 * </p>
 */
abstract public class WebBrowserFrame {

	/**
	 * The browser associated with the page.
	 * <p>
	 * It's necessary to provide it to children classes in case they want to perform
	 * some specific operation while the dialog is opened.
	 * </p>
	 */
	WebDriver driver;

WebBrowserFrame(final WebBrowser browser) {
    this.driver = browser.driver;
}

/**
 * Select current frame.
 */
abstract void switchTo();

/**
 * Return the frame element.
 *
 * @return The frame element or <code>null</code> if the frame is not identified
 * with web element.
 */
WebBrowserElement getElement() {
	return null;
}

/**
 * Return the frame index.
 *
 * @return The frame index or <code>-1</code> if the frame is not identified
 * with an index.
 */
int getIndex() {
	return -1;
}

/**
 * Return the frame name.
 *
 * @return The frame name or <code>null</code> if the frame is not identified
 * with a name.
 */
String getName() {
	return null;
}
}