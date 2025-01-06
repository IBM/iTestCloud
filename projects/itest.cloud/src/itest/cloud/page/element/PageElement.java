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
package itest.cloud.page.element;

import itest.cloud.browser.Browser;
import itest.cloud.config.Config;
import itest.cloud.config.User;
import itest.cloud.nls.NlsMessages;
import itest.cloud.page.Page;
import itest.cloud.scenario.error.ScenarioFailedError;
import itest.cloud.scenario.error.WaitElementTimeoutError;
import itest.cloud.topology.Application;
import itest.cloud.topology.Topology;

/**
 * This class manage a web element belonging to a web page and add some actions and functionalities that anyone can use. It also add some specific operations only accessible to the class hierarchy.
 * <p>
 * There's no public actions at this root level, only common operations for subclasses usage:
 * <ul>
 * <li>{@link #switchToMainWindow()}: Selects either the first frame on the page, or the main document when a page contains iframes.</li>
 * <li>{@link #switchToParentFrame()}: Change focus to the parent context.</li>
 * </ul>
 * </p>
 */
abstract public class PageElement {

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
	protected Page page;

	/**
	 * The browser associated with the page.
	 * <p>
	 * This is a shortcut to access the browser page.
	 * </p>
	 */
	protected Browser browser;

	/**
	 * Current timeout.
	 */
	private Timeout currentTimeout;

public PageElement(final Page page) {
	this.page = page;
	this.browser = page.getBrowser();
}

/**
 * @see Page#getApplication()
 */
protected Application getApplication() {
	return this.page.getApplication();
}

/**
 * @see Page#getConfig()
 */
protected Config getConfig() {
	return this.page.getConfig();
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
 * Return the web page where the element resides.
 *
 * @return The page as a subclass of {@link Page}
 */
protected Page getPage() {
	return this.page;
}

/**
 * @see Page#getTopology()
 */
protected Topology getTopology() {
	return this.page.getTopology();
}

/**
 * @see Page#getUser()
 */
protected User getUser() {
	return this.page.getUser();
}

/**
 * @see Page#openTimeout()
 */
protected int openTimeout() {
	return this.page.openTimeout();
}

/**
 * Reset current timeout.
 */
protected void resetTimeout() {
	this.currentTimeout = null;
}

/**
 * @see Page#shortTimeout()
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
 * Selects either the first frame on the page, or the main document when a page contains iframes.
 *
 * @see Browser#switchToMainWindow()
 */
protected void switchToMainWindow() {
	this.browser.switchToMainWindow();
}

/**
 * Change focus to the parent context.
 * <p>
 * If the current context is the top level browsing context, the context remains unchanged.
 * </p>
 */
protected void switchToParentFrame() {
	this.browser.switchToParentFrame();
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
 * @see Page#timeout()
 */
protected int timeout() {
	return this.page.timeout();
}

/**
 * @see Page#tinyTimeout()
 */
protected int tinyTimeout() {
	return this.page.tinyTimeout();
}

/**
 * @see Page#workaround(String)
 */
protected void workaround(final String message) {
	this.page.workaround(message);
}
}
