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

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.pause;

import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Action;

import com.ibm.itest.cloud.common.config.IUser;
import com.ibm.itest.cloud.common.nls.NlsMessages;
import com.ibm.itest.cloud.common.tests.config.Config;
import com.ibm.itest.cloud.common.tests.config.User;
import com.ibm.itest.cloud.common.tests.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.tests.scenario.errors.WaitElementTimeoutError;
import com.ibm.itest.cloud.common.tests.topology.Application;
import com.ibm.itest.cloud.common.tests.topology.Topology;
import com.ibm.itest.cloud.common.tests.web.WebPage.ClickType;

/**
 * This class manage a web element belonging to a web page and add some actions and functionalities that anyone can use. It also add some specific operations only accessible to the class hierarchy.
 * <p>
 * There's no public actions at this root level, only common operations for subclasses usage:
 * <ul>
 * <li>{@link #check(By, boolean)}: Set or unset the check-box found inside the current page using the given search mechanism.</li>
 * <li>{@link #check(WebBrowserElement, boolean)}: Set/Unset the given check-box web element.</li>
 * <li>{@link #findElementInFrames(By)}: Find an element inside a frame of the window using the given mechanism.</li>
 * <li>{@link #resetFrame()}: Reset the current frame for the current window.</li>
 * <li>{@link #select(By, String)}: Select the given item in the given list element found.</li>
 * <li>{@link #select(WebBrowserElement, String)}: Select the given item in the given list element found.</li>
 * <li>{@link #selectFrame()}: Select the frame in which the current window is expected to be found.</li>
 * <li>{@link #storeBrowserFrame()}: Store the browser the frame.</li>
 * <li>{@link #switchToBrowserFrame()}: Switch to initial browser frame.</li>
 * <li>{@link #switchToStoredFrame()}: Switch to stored frame.</li>
 * <li>{@link #typeText(WebBrowserElement, By, String)}: Type a text into an input web element found inside the given parent web element using the given mechanism.</li>
 * </ul>
 * </p>
 */
abstract public class WebPageElement {

	//	/**
    //	 * Number of tries to workaround any problem while performing action on
    //	 * current element.
    //	 */
    //	protected int workarounds;

    class Timeout {
    	final int sec;
    	final long start, end;
    	String msg;
    	Timeout(final int seconds, final String message) {
    		this.sec = seconds;
    		this.start = System.currentTimeMillis();
    		this.end = this.start + seconds * 1000;
    		this.msg = message;
    	}
    	void test() {
    		if (System.currentTimeMillis() > this.end) {
    			throw new WaitElementTimeoutError(this.msg);
    		}
    	}
    }

	/**
	 * The CLM web page from which the element is found.
	 */
	protected WebPage page;

	/**
	 * The browser associated with the page.
	 * <p>
	 * This is a shortcut to access the browser page.
	 * </p>
	 */
	protected WebBrowser browser;

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
	protected WebBrowserFrame[] frames;

	/**
	 * Current timeout.
	 */
	private Timeout currentTimeout;

public WebPageElement(final WebPage page) {
	this(page, null);
}

public WebPageElement(final WebPage page, final WebBrowserFrame frame) {
	this.page = page;
	this.browser = page.getBrowser();
	this.frames = new WebBrowserFrame[3];
	this.frames[0] = this.browser.getCurrentFrame();
	this.frames[1] = frame;
}

/**
 * @see WebPage#check(By, boolean)
 */
protected WebBrowserElement check(final By locator, final boolean on) {
	return this.page.check(locator, on);
}

/**
 * @see WebPage#check(WebBrowserElement, boolean)
 */
protected boolean check(final WebBrowserElement element, final boolean on) {
	return this.page.check(element, on);
}

/**
 * @see WebPage#check(WebBrowserElement, By, int, boolean)
 */
protected WebBrowserElement check(final WebBrowserElement parentElement, final By locator, final boolean on) {
	return this.page.check(parentElement, locator, on ? 1 : -1, true/*validate*/);
}

/**
 * @see WebPage#clickButton(By)
 */
protected WebBrowserElement clickButton(final By buttonBy) {
	return this.page.clickButton(buttonBy);
}

/**
 * @see WebPage#clickButton(WebBrowserElement, By)
 */
protected WebBrowserElement clickButton(final WebBrowserElement parentElement, final By buttonBy) {
	return this.page.clickButton(parentElement, buttonBy);
}

/**
 * Find an element inside a frame of the window using the given mechanism.
 * <p>
 * TODO Try to get rid off this method by selecting the frame explicitly before
 * looking for an element.
 * </p>
 * @param by The mechanism use to find the element
 * @return The found web element as {@link WebBrowserElement} or
 * <code>null</code> if the element is not found.
 */
protected WebBrowserElement findElementInFrames(final By by) {

	// Get the element in the appropriate frame
	WebBrowserElement foundElement = this.browser.findElementInFrames(by);

	// Assign the found frame to current window
	if (foundElement != null) {
		this.frames[2] = this.browser.getCurrentFrame();
	}

	// Store the browser frame
	return foundElement;
}

/**
 * @see WebPage#getApplication()
 */
protected Application getApplication() {
	return this.page.getApplication();
}

/**
 * @see WebPage#getConfig()
 */
protected Config getConfig() {
	return this.page.getConfig();
}

/**
 * Return the frame used inside the wrapped element.
 *
 * @return The frame as a {@link WebBrowserFrame} or <code>null</code> if
 * no frame is used.
 */
protected WebBrowserFrame getFrame() {
	return this.frames[1];
}

/**
 * Return the NLS messages associated with the web page.
 *
 * @return The messages as {@link NlsMessages}.
 */
protected NlsMessages getNlsMessages() {
	return this.page.nlsMessages;
}

/**
 * Return the web page from which the window has been opened.
 *
 * @return The page as a subclass of {@link WebPage}
 */
protected WebPage getPage() {
	return this.page;
}

/**
 * @see WebPage#getTopology()
 */
protected Topology getTopology() {
	return this.page.getTopology();
}

/**
 * @see WebPage#getUser()
 */
protected User getUser() {
	return this.page.getUser();
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkBy The link locator on which to click.
 * @param findBy The locator of the element opened after clicking on the link element.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link WebElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends WebElementWrapper> P openElementUsingLink(final By linkBy, final By findBy, final Class<P> elementClass, final String... elementData) {
	return getPage().openElementUsingLink(linkBy, findBy, elementClass, elementData);
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkBy The link locator on which to click.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link WebElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends WebElementWrapper> P openElementUsingLink(final By linkBy, final Class<P> elementClass, final String... elementData) {
	return getPage().openElementUsingLink(linkBy, elementClass, elementData);
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkElement The link on which to click.
 * @param findBy The locator of the element opened after clicking on the link element.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link WebElementWrapper}) opened after
 * having clicked on the link.
 */
protected <P extends WebElementWrapper> P openElementUsingLink(final WebBrowserElement linkElement, final By findBy, final Class<P> elementClass, final String... elementData) {
	return getPage().openElementUsingLink(linkElement, findBy, elementClass, elementData);
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkElement The link on which to click.
 * @param elementClass The class associated with the opened element.
 * @param clickType The type of click to make on the link element to open the new element as {@link ClickType}
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link WebElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends WebElementWrapper> P openElementUsingLink(final WebBrowserElement linkElement, final Class<P> elementClass, final ClickType clickType, final String... elementData) {
	return getPage().openElementUsingLink(linkElement, elementClass, clickType, elementData);
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkElement The link on which to click.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link WebElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends WebElementWrapper> P openElementUsingLink(final WebBrowserElement linkElement, final Class<P> elementClass, final String... elementData) {
	return getPage().openElementUsingLink(linkElement, elementClass, elementData);
}

/**
 * Retrieve the existing page for the browser current URL. Create it if it's the first
 * time the page is requested.
 *
 * @param pageClass The class associated with the page to open
 * @param data Additional CLM information to be stored in the page
 * @return The instance of the class associate with the page.
 */
protected <P extends WebPage> P openPageUsingBrowser(final Class<P> pageClass, final String... data) {
	return getPage().openPageUsingBrowser(pageClass, data);
}

/**
 * Click on the given link assuming that it will open the given page.
 *
 * @param linkElement The link on which to click
 * @param openedPageClass The class associated with the opened page
 * @param postLinkClickAction The action to perform after clicking the link as {@link Action}.
 * @param pageData Provide additional information to store in the page when opening it
 * @return The web page (as a subclass of {@link WebPage}) opened after
 * @see WebPage#openPageUsingLink(WebBrowserElement, Class, String...)
 */
protected <P extends WebPage> P openPageUsingLink(final WebBrowserElement linkElement, final Class<P> openedPageClass, final Action postLinkClickAction, final String... pageData) {
	return getPage().openPageUsingLink(linkElement, openedPageClass, postLinkClickAction, pageData);
}

/**
 * Click on the given link assuming that it will open the given page.
 *
 * @param linkElement The link on which to click
 * @param openedPageClass The class associated with the opened page
 * @param pageData Provide additional information to store in the page when opening it
 * @return The web page (as a subclass of {@link WebPage}) opened after
 * @see WebPage#openPageUsingLink(WebBrowserElement, Class, String...)
 */
public <P extends WebPage> P openPageUsingLink(final WebBrowserElement linkElement, final Class<P> openedPageClass, final String... pageData) {
	return getPage().openPageUsingLink(linkElement, openedPageClass, pageData);
}

/**
 * @see WebPage#openTimeout()
 */
protected int openTimeout() {
	return this.page.openTimeout();
}

/**
 * Reset the current frame for the current window.
 *
 * @see WebBrowser#resetFrame()
 */
protected void resetFrame() {
	this.frames[2] = null;
	this.browser.resetFrame();
	pause(100);
}

/**
 * Reset current timeout.
 */
protected void resetTimeout() {
	this.currentTimeout = null;
}

/**
 * Select the given item in the given list element found.
 * <p>
 * The items of the selection list are supposed to be found using
 * <code>by.xpath("./option")</code> search mechanism.
 * </p>
 * @param locator The locator of the list element in which perform the selection.
 * @param pattern A pattern matching the item to select in the list, assuming that text matches
 * @return The selected element as {@link WebBrowserElement}.
 * @throws ScenarioFailedError if no item matches the expected selection.
 */
protected WebBrowserElement select(final By locator, final Pattern pattern) {
	return this.page.select(locator, pattern);
}

/**
 * Select the given item in the given list element found.
 * <p>
 * The items of the selection list are supposed to be found using
 * <code>by.xpath("./option")</code> search mechanism.
 * </p>
 * @param locator The locator of the list element in which perform the selection.
 * @param selection The item to select in the list, assuming that text matches
 * @return The selected element as {@link WebBrowserElement}.
 * @throws ScenarioFailedError if no item matches the expected selection.
 */
protected WebBrowserElement select(final By locator, final String selection) {
	return this.page.select(locator, selection);
}

/**
 * Select the given item in the given list element found.
 * <p>
 * The items of the selection list are supposed to be found using
 * <code>by.xpath("./option")</code> search mechanism.
 * </p>
 * @param listElement The list element in which perform the selection.
 * @param pattern A pattern matching the item to select in the list, assuming that text matches
 * @return The selected element as {@link WebBrowserElement}.
 * @throws ScenarioFailedError if no item matches the expected selection.
 */
protected WebBrowserElement select(final WebBrowserElement listElement, final Pattern pattern) {
	return this.page.select(listElement, pattern);
}

/**
 * Select the given item in the given list element found.
 * <p>
 * The items of the selection list are supposed to be found using
 * <code>by.xpath("./option")</code> search mechanism.
 * </p>
 * @param listElement The list element in which perform the selection.
 * @param selection The item to select in the list, assuming that text matches
 * @return The selected element as {@link WebBrowserElement}.
 * @throws ScenarioFailedError if no item matches the expected selection.
 */
protected WebBrowserElement select(final WebBrowserElement listElement, final String selection) {
	return this.page.select(listElement, selection);
}

/**
 * Select the frame in which the current window is expected to be found.
 *
 * @see WebBrowser#selectFrame(int)
 */
protected void selectFrame() {
	if (this.frames[2] != this.frames[1]) { // == is intentional
		this.frames[2] = this.frames[1];
		this.browser.selectFrame(this.frames[2]);
		pause(250);
	}
}

/**
 * @see WebPage#shortTimeout()
 */
protected int shortTimeout() {
	return this.page.shortTimeout();
}

/**
 * Start a timeout of the given seconds;
 *
 * @param timeout The timeout to wait in seconds
 * @param message Error message to display if the timeout is reached
 * @throws ScenarioFailedError If there was already a started timeout and it has
 * expired.
 */
protected void startTimeout(final int timeout, final String message) {
	if (this.currentTimeout == null) {
		this.currentTimeout = new Timeout(timeout, message);
	}
	this.currentTimeout.test();
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
 * Switch to initial browser frame.
 */
protected void switchToBrowserFrame() {
	switchToFrame(0);
}

private void switchToFrame(final int idx) {
	this.browser.selectFrame(this.frames[idx]);
	this.frames[2] = this.frames[idx];
}

/**
 * Switch to element frame.
 */
protected void switchToStoredFrame() {
	switchToFrame(1);
}

/**
 * Test whether the current timeout has been reached or not.
 *
 * @throws ScenarioFailedError If the timeout has been reached or if no timeout
 * was set.
 */
protected void testTimeout() {
	if (this.currentTimeout == null) {
		throw new ScenarioFailedError("Programmation error, no timeout has been set, hence it cannot be tested!");
	}
	this.currentTimeout.test();
}

/**
 * @see WebPage#timeout()
 */
protected int timeout() {
	return this.page.timeout();
}

/**
 * @see WebPage#tinyTimeout()
 */
protected int tinyTimeout() {
	return this.page.tinyTimeout();
}

/**
 * @see WebPage#typePassword(WebBrowserElement, IUser)
 * @since 6.0
 */
protected void typePassword(final WebBrowserElement element, final IUser user) {
	this.page.typePassword(element, user);
}

/**
 * @see WebPage#typeText(By, String)
 */
protected WebBrowserElement typeText(final By locator, final String text) {
	return this.page.typeText(locator, text);
}

/**
 * @see WebPage#typeText(By, String, Keys)
 */
protected WebBrowserElement typeText(final By locator, final String text, final Keys key) {
	return this.page.typeText(locator, text, key);
}

/**
 * @see WebPage#typeText(WebBrowserElement, By, String)
 */
protected WebBrowserElement typeText(final WebBrowserElement parentElement, final By locator, final String text) {
	return this.page.typeText(parentElement, locator, text);
}

/**
 * @see WebPage#typeText(WebBrowserElement, String)
 */
protected void typeText(final WebBrowserElement inputElement, final String text) {
	this.page.typeText(inputElement, text);
}

/**
 * @see WebPage#typeText(WebBrowserElement, String, Keys)
 */
protected void typeText(final WebBrowserElement inputElement, final String text, final Keys key) {
	this.page.typeText(inputElement, text, key);
}

///**
// * @see WebPage#waitForElement(By)
// */
//public WebBrowserElement waitForElement(final By locator) {
//	return this.page.waitForElement(locator);
//}
//
///**
// * @see WebPage#waitForElement(By, boolean)
// */
//protected WebBrowserElement waitForElement(final By locator, final boolean displayed) {
//	return this.page.waitForElement(locator, displayed);
//}
//
///**
// * @see WebPage#waitForElement(By, boolean, int)
// */
//protected WebBrowserElement waitForElement(final By locator, final boolean fail, final int timeout) {
//	return this.page.waitForElement(locator, fail, timeout);
//}
//
///**
// * @see WebPage#waitForElement(By, boolean, int, boolean)
// */
//protected WebBrowserElement waitForElement(final By locator, final boolean fail, final int timeout, final boolean displayed) {
//	return this.page.waitForElement(locator, fail, timeout, displayed);
//}
//
///**
// * @see WebPage#waitForElement(By, boolean, int, boolean, boolean)
// */
//public WebBrowserElement waitForElement(final By locator, final boolean fail, final int timeout, final boolean displayed, final boolean single) {
//	return this.page.waitForElement(locator, fail, timeout, displayed, single);
//}
//
///**
// * @see WebPage#waitForElements(By)
// */
//protected List<WebBrowserElement> waitForElements(final By locator) {
//	return this.page.waitForElements(locator);
//}
//
///**
// * @see WebPage#waitForElements(By, boolean)
// */
//protected List<WebBrowserElement> waitForElements(final By locator, final boolean fail) {
//	return this.page.waitForElements(locator, fail);
//}
//
///**
// * @see WebPage#waitForElements(By, int)
// */
//protected List<WebBrowserElement> waitForElements(final By locator, final boolean fail, final int timeout) {
//	return this.page.waitForElements(locator, fail, timeout);
//}
//
///**
// * @see WebBrowser#waitForElements(WebBrowserElement, By, boolean, int, boolean)
// */
//protected List<WebBrowserElement> waitForElements(final By locator, final boolean fail, final int time_out, final boolean displayed) {
//	return this.browser.waitForElements(null, locator, fail, time_out, displayed);
//}
//
///**
// * @see WebPage#waitForElements(By, int)
// */
//protected List<WebBrowserElement> waitForElements(final By locator, final int timeout) {
//	return this.page.waitForElements(locator, timeout);
//}
//
///**
// * @see WebPage#waitForElements(By, int, boolean)
// */
//protected List<WebBrowserElement> waitForElements(final By locator, final int timeout, final boolean fail) {
//	return this.page.waitForElements(locator, timeout, fail);
//}
//
///**
// * @see WebPage#waitForMultipleElements(boolean, int, By...)
// */
//protected WebBrowserElement[] waitForMultipleElements(final boolean fail, final int time_out, final By... findBys) {
//	return this.page.waitForMultipleElements(fail, time_out, findBys);
//}
//
///**
// * @see WebPage#waitForMultipleElements(By...)
// */
//protected WebBrowserElement[] waitForMultipleElements(final By... locators) {
//	return this.page.waitForMultipleElements(locators);
//}

/**
 * @see WebPage#workaround(String)
 */
protected void workaround(final String message) {
	this.page.workaround(message);
}
}
