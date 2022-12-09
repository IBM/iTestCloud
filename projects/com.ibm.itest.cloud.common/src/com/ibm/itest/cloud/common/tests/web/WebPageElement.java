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

import org.openqa.selenium.By;

import com.ibm.itest.cloud.common.nls.NlsMessages;
import com.ibm.itest.cloud.common.tests.config.Config;
import com.ibm.itest.cloud.common.tests.config.User;
import com.ibm.itest.cloud.common.tests.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.tests.scenario.errors.WaitElementTimeoutError;
import com.ibm.itest.cloud.common.tests.topology.Application;
import com.ibm.itest.cloud.common.tests.topology.Topology;

/**
 * This class manage a web element belonging to a web page and add some actions and functionalities that anyone can use. It also add some specific operations only accessible to the class hierarchy.
 * <p>
 * There's no public actions at this root level, only common operations for subclasses usage:
 * <ul>
 * <li>{@link #findElementInFrames(By)}: Find an element inside a frame of the window using the given mechanism.</li>
 * <li>{@link #resetFrame()}: Reset the current frame for the current window.</li>
 * <li>{@link #selectFrame()}: Select the frame in which the current window is expected to be found.</li>
 * <li>{@link #storeBrowserFrame()}: Store the browser the frame.</li>
 * <li>{@link #switchToBrowserFrame()}: Switch to initial browser frame.</li>
 * <li>{@link #switchToStoredFrame()}: Switch to stored frame.</li>
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
 * @see WebPage#workaround(String)
 */
protected void workaround(final String message) {
	this.page.workaround(message);
}
}
