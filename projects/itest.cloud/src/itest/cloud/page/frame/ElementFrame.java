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
package itest.cloud.page.frame;

import static itest.cloud.scenario.ScenarioUtil.DEBUG;
import static itest.cloud.scenario.ScenarioUtil.debugPrintln;

import org.openqa.selenium.By;

import itest.cloud.browser.Browser;
import itest.cloud.config.Timeouts;
import itest.cloud.page.element.BrowserElement;

/**
 * Class to manage browser frame identified with a web element.
 * <p>
 * TODO Only use this kind of frame, the two other ones should become
 * obsolete
 */
public class ElementFrame extends BrowserFrame {

	/**
	 * The web element of the current frame.
	 */
	private final BrowserElement element;

public ElementFrame(final Browser browser, final By locator) {
	this(browser, browser.waitForElement(locator, Timeouts.DEFAULT_TIMEOUT));
}

public ElementFrame(final Browser browser, final BrowserElement element) {
	super(browser);
	this.element = element;
}

@Override
public boolean equals(final Object obj) {
	if (obj instanceof ElementFrame) {
		ElementFrame frame = (ElementFrame) obj;
		return this.element.equals(frame.element);
	}
	if (obj instanceof BrowserElement) {
		return this.element.equals(obj);
	}
	return super.equals(obj);
}

@Override
public BrowserElement getElement() {
	return this.element;
}

@Override
public int hashCode() {
	return this.element.hashCode();
}

/**
 * Return whether the current frame is still displayed or not.
 *
 * @return <code>true</code> if the frame element is still displayed,
 * <code>false>/code> otherwise.
 */
public boolean isDisplayed() {
	return this.element.isDisplayed(false/*recovery*/);
}

/**
 * Select current frame.
 */
@Override
public void switchTo() {
	if (DEBUG) debugPrintln("		+ Switch to "+this);
	this.driver.switchTo().defaultContent();
	this.driver.switchTo().frame(this.element.getWebElement());
}

@Override
public String toString() {
	return "Frame element "+this.element;
}

}
