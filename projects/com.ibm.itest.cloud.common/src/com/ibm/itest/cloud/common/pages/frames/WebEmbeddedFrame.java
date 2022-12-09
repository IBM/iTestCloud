/*********************************************************************
 * Copyright (c) 2014, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.common.pages.frames;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.DEBUG;
import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.debugPrintln;

import com.ibm.itest.cloud.common.pages.elements.WebBrowserElement;
import com.ibm.itest.cloud.common.tests.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.tests.web.browsers.WebBrowser;

/**
 * Class to manage browser frame embedded in another frame.
 */
public class WebEmbeddedFrame extends WebElementFrame {

	private WebElementFrame parentFrame;

public WebEmbeddedFrame(final WebBrowser browser, final WebBrowserFrame frame, final WebBrowserElement element) {
	super(browser, element);
	WebBrowserFrame browserFrame = frame;
	if (browserFrame == null) {
		throw new ScenarioFailedError("An embedded frame must have a parent.");
	}
	if (browserFrame instanceof WebElementFrame) {
		this.parentFrame = (WebElementFrame) browserFrame;
	} else {
		throw new ScenarioFailedError("Invalid class for parent frame: "+browserFrame.getClass());
	}
}

@Override
public void switchTo() {
	switchToParent();
	this.driver.switchTo().frame(getElement().getWebElement());
}

/**
 * Switch to parent frame.
 *
 * @return The selected parent frame as a {@link WebElementFrame}.
 */
public WebElementFrame switchToParent() {
	if (DEBUG) debugPrintln("		+ Switch to "+this);
	this.parentFrame.switchTo();
	return this.parentFrame;
}

}
