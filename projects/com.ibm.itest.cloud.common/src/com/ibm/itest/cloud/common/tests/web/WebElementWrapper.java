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

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.LINE_SEPARATOR;

import java.util.List;

import org.openqa.selenium.*;

import com.ibm.itest.cloud.common.tests.config.Config;
import com.ibm.itest.cloud.common.tests.scenario.errors.*;

/**
 * This class wraps a web element and add some actions and functionalities
 * that anyone can use. It also add some specific operations only accessible to
 * the class hierarchy.
 * <p>
 * There's still no public action or functionalities at this level, only common
 * operations for subclasses usage:
 * <ul>
 * <li>{@link #clickButton(String)}: Click on the button found using the given xpath.</li>
 * <li>{@link #clickButton(String, int)}: Click on the button found using the given xpath.</li>
 * <li>{@link #findElement(By, boolean)}: Find an element using the given search
 * mechanism relatively to the wrapped element.</li>
 * <li>{@link #findElement(String, boolean)}: Find an element using the given
 * xpath relatively to the wrapped element.</li>
 * <li>{@link #waitWhileDisplayed(int)}: Wait until the current window is closed.</li>
 * <li>{@link #waitForElement(By)}: Wait until having found an element
 * searched using the given mechanism.</li>
 * </ul>
 * </p>
 */
abstract public class WebElementWrapper extends WebPageElement {

	/**
	 * Locator for title element.
	 * <p>
	 * Design Backward compatibility with 4.0.0.1 version
	 * </p>
	 */
	protected static final By[] TITLE_POSSIBLE_BYS = new By[] {
		By.xpath(".//*[@dojoattachpoint='_headerPrimary']"),
		By.xpath(".//*[@dojoattachpoint='_primaryHeaderText']"),
	};

	/**
	 * The wrapped web element.
	 */
	protected WebBrowserElement element;

	/**
	 * The parent of current wrapper.
	 * <p>
	 * Children web elements should be looked from the web element of this
	 * wrapper if not <code>null</code>. If <code>null</code>, then they have
	 * to be looked for in the entire page.
	 * </p>
	 */
	protected WebElementWrapper parent;

public WebElementWrapper(final WebElementWrapper parent) {
	this(parent.getPage());
	this.parent = parent;
}

public WebElementWrapper(final WebElementWrapper parent, final By selectBy) {
	this(parent.getPage(), parent.element.waitForElement(selectBy));
	this.parent = parent;
}

public WebElementWrapper(final WebElementWrapper parent, final By selectBy, final WebBrowserFrame frame) {
	this(parent.getPage(), parent.element.waitForElement(selectBy), frame);
	this.parent = parent;
}

public WebElementWrapper(final WebElementWrapper parent, final WebBrowserElement element) {
	this(parent.getPage(), element);
	this.parent = parent;
}

public WebElementWrapper(final WebElementWrapper parent, final WebBrowserElement element, final WebBrowserFrame frame) {
	this(parent.getPage(), element, frame);
	this.parent = parent;
}

public WebElementWrapper(final WebPage page) {
	super(page);
}

public WebElementWrapper(final WebPage page, final By findBy) {
	super(page);
	//waitForElement() in this class can't filter out the hidden element, use the one in super class instead
	this.element = page.waitForElement(findBy, true, openTimeout());
}

public WebElementWrapper(final WebPage page, final By findBy, final WebBrowserFrame frame) {
	super(page, frame);
	this.element = page.waitForElement(findBy, true, openTimeout());
}

public WebElementWrapper(final WebPage page, final WebBrowserElement element) {
	super(page);
	this.element = element;
}

public WebElementWrapper(final WebPage page, final WebBrowserElement element, final WebBrowserFrame frame) {
	super(page, frame);
	this.element = element;
}

public WebElementWrapper(final WebPage page, final WebBrowserFrame frame) {
	super(page, frame);
}

/**
 * Click on the button found using the given xpath.
 * <p>
 * TODO Remove the {@link #findElementInFrames(By)} call
 * </p>
 * @param xpath The Xpath to find the button
 * @see #findElement(String, boolean)
 * @see WebBrowser#clickButton(WebBrowserElement, int, boolean)
 */
protected void clickButton(final String xpath) {
	clickButton(xpath, timeout());
}

/**
 * Click on the button found using the given xpath.
 * <p>
 * TODO Remove the {@link #findElementInFrames(By)} call
 * </p>
 * @param xpath The Xpath to find the button
 * @param timeout Timeout while waiting for the button to become enabled
 * @see #findElement(String, boolean)
 * @see WebBrowser#clickButton(WebBrowserElement, int, boolean)
 */
protected void clickButton(final String xpath, final int timeout) {

	// Get button element
	WebBrowserElement buttonElement = deepFindElement(xpath);

	// Throw an error if the button is unavailable.
	if(buttonElement == null) {
		throw new WaitElementTimeoutError("Button '" + xpath + "' could not be found");
	}

	// Click on button
	this.browser.clickButton(buttonElement, timeout, false /*validate*/);
}

/**
 * Perform a deep search to find the element from the given xpath.
 * <p>
 * Initially searching for the web element by using {@link #findElement(By, boolean)}.
 * If it's not found using this method, then try to look for the element in possible
 * frames of the wrapped element.
 * </p>
 * @param xpath The xpath of the web element to find
 * @return The web element as a {@link WebBrowserElement} or <code>null</code>
 * if it has not been found.
 */
protected WebBrowserElement deepFindElement(final String xpath) {

	// Look for the element using regular method
	WebBrowserElement buttonElement = findElement(xpath, false/*no recovery*/);

	// If not found , then try to find it in another frame
	if (buttonElement == null) {
		buttonElement = findElementInFrames(By.xpath(xpath));
	}

	// Return the found element
	return buttonElement;
}

/**
 * Find an element using the given mechanism relatively to the wrapped element.
 * <p>
 * Note that if a frame is selected, then the element is searched relatively to
 * this frame instead.
 * </p>
 * @param by The mechanism to find the window's element
 * @param recovery Tells whether recovery is allowed when searching the element.
 * @return The found element as a {@link WebBrowserElement} or <code>null</code>
 * if the element was not found and recovery was not allowed.
 * @throws NoSuchElementException If the element is not found at the given
 * xpath and recovery was allowed.
 * @see WebBrowser#findElement(By)
 */
protected WebBrowserElement findElement(final By by, final boolean recovery) {
	return findElement(by, true/*displayed*/, recovery);
}

/**
 * Find an element using the given mechanism relatively to the wrapped element.
 * <p>
 * Note that if a frame is selected, then the element is searched relatively to
 * this frame instead.
 * </p>
 * @param by The mechanism to find the window's element
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @param recovery Tells whether recovery is allowed when searching the element.
 * @return The found element as a {@link WebBrowserElement} or <code>null</code>
 * if the element was not found and recovery was not allowed.
 * @throws NoSuchElementException If the element is not found at the given
 * xpath and recovery was allowed.
 * @see WebBrowser#findElement(By)
 */
protected WebBrowserElement findElement(final By by, final boolean displayed, final boolean recovery) {

	// Find elements
	List<WebElement> elements;
	if (this.frames[2] != null) {
		elements = this.browser.findElements(by, displayed, recovery);
	} else if (this.element == null) {
		elements = this.browser.findElements(by, displayed, recovery);
	} else {
		elements = this.element.findElements(by, displayed, recovery);
	}

	// Nothing was found, check frame
	if (elements == null || elements.size() == 0) {
		if (this.frames[2] == null && this.frames[1] != null) {
			selectFrame();
			elements = this.browser.findElements(by, displayed, recovery);
		}
		if (elements == null || elements.size() == 0) return null;
	}

	// Check element uniqueness
	if (elements.size() > 1) {
		if (this.element.isDisplayed(false/*recovery*/)) {
			throw new ScenarioFailedError("Unexpected multiple elements found." + LINE_SEPARATOR
					+ "			-> " + by + LINE_SEPARATOR
					+ "			-> # found: " + elements.size());
		}
		return null;
	}

	// Return the found element
	return (WebBrowserElement) elements.get(0);
}

/**
 * Find an element using the given xpath relatively to the wrapped element.
 * <p>
 * Note that if a frame is selected, then the element is searched relatively to
 * this frame instead.
 * </p>
 * @param xpath The xpath to find the window's element
 * @param recovery Tells whether recovery is allowed when searching the element.
 * @return The found element as a {@link WebBrowserElement} or <code>null</code>
 * if the element was not found and recovery was not allowed.
 * @throws NoSuchElementException If the element is not found at the given
 * xpath and recovery was allowed.
 * @see #findElement(By, boolean)
 */
protected WebBrowserElement findElement(final String xpath, final boolean recovery) {

	// Use given path if there's an active frame
	if (this.frames[2] != null) {
		return findElement(By.xpath(xpath), recovery);
	}

	// Make xpath relative
	String relativeXpath = xpath;
	if (relativeXpath.startsWith("/")) {
		relativeXpath = "." + xpath;
	}

	// Find element using xpath mechanism
	return findElement(By.xpath(relativeXpath), recovery);
}

/**
 * Return the xpaths of elements indicating that the wrapped element is undergoing an operation (busy).
 * <p>
 * The availability of such an element implies that at least a part of the
 * web page is still loading.
 * </p>
 *
 * @return Return the xpaths of elements indicating that the wrapped element is undergoing an operation.
 *
 */
protected By[] getBusyIndicatorElementLocators() {
	// No busy element defined at this level.
	return new By[] {};
}

/**
 * Return the elements indicating that the wrapped element is undergoing an operation (busy).
 * <p>
 * The availability of such an element implies that at least a part of the
 * web page is still loading.
 * </p>
 *
 * @return Return the elements indicating that the wrapped element is undergoing an operation or
 * <code>null</code> if no such element was found.
 *
 */
protected WebBrowserElement[] getBusyIndicatorElements() {
	return this.browser.waitForMultipleElements(this.element, getBusyIndicatorElementLocators(), false /*fail*/, tinyTimeout());
}

/**
 * Return the wrapped web element.
 *
 * @return The wrapped web element as {@link WebBrowserElement}.
 */
public WebBrowserElement getElement() {
	return this.element;
}

/**
 * Returns the parent element.
 *
 * @return The parent element of <code>null</code> if there's no parent.
 */
public WebElementWrapper getParent() {
	return this.parent;
}

/**
 * Returns the parent element.
 *
 * @return The parent element of <code>null</code> if there's no parent.
 */
public WebBrowserElement getParentElement() {
	if (this.parent == null) {
		return null;
	}
	return this.parent.element;
}

/**
 * Return the text of the expandable element.
 *
 * @return The text as a {@link String}.
 */
public String getText() {
	return this.element.getText();
}

/**
 * Return whether the current wrapped element is displayed or not.
 *
 * @param recovery Tells whether to use recovery or not to get the info
 * @return <code>true</code> if the wrapped element is still valid and displayed,
 * <code>false</code> otherwise.
 */
public boolean isDisplayed(final boolean recovery) {
	return this.element.isDisplayed(recovery);
}

/**
 * Wait until have found the element using given locator.
 * <p>
 * The desired element is searched in the wrapped element and with no frame.
 * </p>
 * @param locator The locator to find the element in the wrapped element.
 * @return The web element as {@link WebBrowserElement}.
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail.
 * @throws MultipleVisibleElementsError if several elements are found.
 */
public WebBrowserElement waitForElement(final By locator) {
	return waitForElement(locator, true /*displayed*/);
}

/**
 * Wait until have found the element using given locator.
 * <p>
 * The desired element is searched in the wrapped element and with no frame.
 * </p>
 * @param locator The locator to find the element in the wrapped element.
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @return The web element as {@link WebBrowserElement}.
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail.
 * @throws MultipleVisibleElementsError if several elements are found.
 */
public WebBrowserElement waitForElement(final By locator, final boolean displayed) {
	return waitForElement(locator, true /*fail*/, timeout(), displayed);
}

/**
 * Wait until have found the element using given locator.
 * <p>
 * The desired element is searched in the wrapped element and with no frame.
 * </p>
 * @param locator The locator to find the element in the wrapped element.
 * @param fail Specify whether to fail if none of the locators is find before timeout.
 * @param timeout The time to wait before giving up the research.
 * @return The web element as {@link WebBrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail.
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail.
 * @throws MultipleVisibleElementsError if several elements are found.
 */
public WebBrowserElement waitForElement(final By locator, final boolean fail, final int timeout) {
	return waitForElement(locator, fail, timeout, true /*displayed*/);
}

/**
 * Wait until have found the element using given locator.
 * <p>
 * The desired element is searched in the wrapped element and with no frame.
 * </p>
 * @param locator The locator to find the element in the wrapped element.
 * @param fail Specify whether to fail if none of the locators is find before timeout.
 * @param timeout The time to wait before giving up the research.
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @return The web element as {@link WebBrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail.
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail.
 * @throws MultipleVisibleElementsError if several elements are found.
 */
public WebBrowserElement waitForElement(final By locator, final boolean fail, final int timeout, final boolean displayed) {
	return waitForElement(locator, fail, timeout, displayed, true /*single*/);
}

/**
 * Wait until have found the element using given locator.
 * <p>
 * The desired element is searched in the wrapped element and with no frame.
 * </p>
 * @param locator The locator to find the element in the wrapped element.
 * @param fail Specify whether to fail if none of the locators is find before timeout.
 * @param timeout The time to wait before giving up the research.
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @param single Specify whether a single element is expected.
 * @return The web element as {@link WebBrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail.
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail.
 * @throws MultipleVisibleElementsError if several elements are found and only single one was expected.
 */
public WebBrowserElement waitForElement(final By locator, final boolean fail, final int timeout, final boolean displayed, final boolean single) {
	return this.browser.waitForElement(this.element, locator, fail, timeout, displayed, single);
}

/**
 * Wait until having found an element searched using the given mechanism.
 * <p>
 * The desired element is searched in the wrapped element and with no frame.
 * </p>
 * @param locator The locator to use for the search
 * @param timeout Time to wait until giving up if the element is not found
 * @return The found web element as a {@link WebBrowserElement}.
 * @throws ScenarioFailedError If the element is not found before the given
 * timeout is reached.
 * TODO Try to get rid off this method by selecting the frame explicitly before
 * waiting for an element. Hence, {@link WebPageElement} waitForElement*
 * methods could be used instead.
 */
public WebBrowserElement waitForElement(final By locator, final int timeout) {
	return waitForElement(locator, true /*fail*/, timeout);
}

/**
 * Wait until have found the web element using the given mechanism relatively
 * to the given parent element.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if:
 * <ol>
 * <li>the element is not found before {@link #timeout()} seconds</li>
 * <li>there's more than one element found</li>
 * </ol></li>
 * <li>hidden element will be ignored</li>
 * </ul>
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the wrapped element.
 * @param locator The locator to find the element in the current page.
 * @return The web element as {@link WebBrowserElement}
 * @throws ScenarioFailedError if no element was found before the timeout.
 *
 * @see WebBrowser#waitForElement(WebBrowserElement, By, boolean, int, boolean, boolean)
 * TODO Try to get rid off this method by selecting the frame explicitly before
 * waiting for an element. Hence, {@link WebPageElement} waitForElement*
 * methods could be used instead.
 */
public WebBrowserElement waitForElement(final WebBrowserElement parentElement, final By locator) {
	return waitForElement(parentElement, locator, timeout());
}

/**
 * Wait until having found an element searched using the given mechanism.
 * <p>
 * The element is searched with no frame.
 * </p>
 * @param parentElement The element from which the search has to be started.
 * if <code>null</code>, then search in the wrapped element.
 * @param locator The locator to use for the search
 * @param timeout Time to wait until giving up if the element is not found
 * @return The found web element as a {@link WebBrowserElement}.
 * @throws ScenarioFailedError If the element is not found before the given
 * timeout is reached.
 * TODO Try to get rid off this method by selecting the frame explicitly before
 * waiting for an element. Hence, {@link WebPageElement} waitForElement*
 * methods could be used instead.
 */
public WebBrowserElement waitForElement(final WebBrowserElement parentElement, final By locator, final int timeout) {
	return this.browser.waitForElement(parentElement != null ? parentElement : this.element, locator, true /*fail*/, timeout);
}

/**
 * Wait until have found one or several elements using given locator.
 * <p>
 * Only fail if specified and after having waited the given timeout.
 * </p>
 * @param locator Locator to find the element in the current page.
 * @return A {@link List} of web element as {@link WebBrowserElement}.
 * @throws WaitElementTimeoutError if no element was found before the timeout and
 * asked to fail.
 */
public List<WebBrowserElement> waitForElements(final By locator) {
	return waitForElements(locator, true /*fail*/);
}

/**
 * Wait until have found one or several elements using given locator.
 * <p>
 * Only fail if specified and after having waited the given timeout.
 * </p>
 * @param locator Locator to find the element in the current page.
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @return A {@link List} of web element as {@link WebBrowserElement}. Might
 * be empty if no element was found before the timeout and asked not to fail.
 * @throws WaitElementTimeoutError if no element was found before the timeout and
 * asked to fail.
 */
public List<WebBrowserElement> waitForElements(final By locator, final boolean fail) {
	return waitForElements(locator, fail, timeout());
}

/**
 * Wait until have found one or several elements using given locator.
 * <p>
 * Only fail if specified and after having waited the given timeout.
 * </p>
 * @param locator Locator to find the element in the current page.
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param timeout The time to wait before giving up the research
 * @return A {@link List} of web element as {@link WebBrowserElement}. Might
 * be empty if no element was found before the timeout and asked not to fail.
 * @throws WaitElementTimeoutError if no element was found before the timeout and
 * asked to fail.
 */
public List<WebBrowserElement> waitForElements(final By locator, final boolean fail, final int timeout) {
	return waitForElements(locator, fail, timeout, true /*displayed*/);
}

/**
 * Wait until have found one or several elements using given locator.
 * <p>
 * Only fail if specified and after having waited the given timeout.
 * </p>
 * @param locator Locator to find the element in the current page.
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param timeout The time to wait before giving up the research
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @return A {@link List} of web element as {@link WebBrowserElement}. Might
 * be empty if no element was found before the timeout and asked not to fail.
 * @throws WaitElementTimeoutError if no element was found before the timeout and
 * asked to fail.
 */
public List<WebBrowserElement> waitForElements(final By locator, final boolean fail, final int timeout, final boolean displayed) {
	return this.browser.waitForElements(this.element, locator, fail, timeout, displayed);
}

/**
 * Wait until have found one or several elements using given locator.
 * <p>
 * Only fail if specified and after having waited the given timeout.
 * </p>
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research
 * @return A {@link List} of web element as {@link WebBrowserElement}.
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 */
public List<WebBrowserElement> waitForElements(final By locator, final int timeout) {
	return waitForElements(locator, true /*fail*/, timeout);
}

/**
 * Wait until have found one or several elements using given locator.
 * <p>
 * Only fail if specified and after having waited the given timeout.
 * </p>
 * @param locator Locator to find the element in the current page.
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param timeout The time to wait before giving up the research
 * @return A {@link List} of web element as {@link WebBrowserElement}. Might
 * be empty if no element was found before the timeout and asked not to fail.
 * @throws WaitElementTimeoutError if no element was found before the timeout and
 * asked to fail.
 */
public List<WebBrowserElement> waitForElements(final By locator, final int timeout, final boolean fail) {
	return waitForElements(locator, fail, timeout);
}

///**
// * Wait until having found an element searched using the given mechanism.
// * <p>
// * The element is searched in the entire document. If the element has to be
// * searched in a frame and it's found, then the matching frame is selected after
// * the method execution. That may impact further element researches...
// * </p>
// * @param locator The locator to use for the search
// * @param timeout Time to wait until giving up if the element is not found
// * @param frame Tells whether the element should be searched in a frame or
// * not.
// * @return The found web element as a {@link WebBrowserElement}.
// * @throws ScenarioFailedError If the element is not found before the given
// * timeout is reached.
// * TODO Try to get rid off this method by selecting the frame explicitly before
// * waiting for an element. Hence, {@link WebPageElement} waitForElement*
// * methods could be used instead.
// */
//public WebBrowserElement waitForElement(final By locator, final int timeout, final boolean frame) {
//	return waitForElement(this.element, locator, timeout, frame);
//}

/**
 * Wait for loading of the wrapped element to complete.
 */
public void waitForLoadingEnd() {
	waitWhileBusy();
}

/**
 * Wait until at least one element is found using each of the given locator.
 * <p>
 * That method stores each found element using the given locators in the
 * the returned array, hence it may have more than one non-null slot.
 * </p><p>
 * Note that the method stop to search as soon as at least one element is found.
 * Hence, when several elements are found and returned in the array, that means
 * they have been found in the same loop. The timeout is only reached when
 * <b>no</b> element is found...
 * </p><p>
 * Note also that only displayed elements are returned.
 * </p>
 * @param locators List of locators to use to find the elements in the current page.
 * @param fail Specify whether to fail if none of the locators is find before timeout
 * @param timeout The time to wait before giving up the research
 * @return An array with one non-null slot per element found before timeout
 * occurs or <code>null</code> if none was found and it has been asked not to fail.
 * @throws WaitElementTimeoutError if no element is found before the timeout occurs
 * and it has been asked to fail.
 */
public WebBrowserElement[] waitForMultipleElements(final boolean fail, final int timeout, final By... locators) {
	return this.browser.waitForMultipleElements(this.element, locators, fail, timeout);
}

///**
// * Wait until having found an element searched using the given mechanism.
// * <p>
// * If the element has to be searched in a frame and it's found, then the matching
// * frame is selected after the method execution. That may impact further element
// * researches...
// * </p>
// * @param parentElement The element from which the search has to be started.
// * If <code>null</code>, then search in the entire page.
// * @param locator The locator to use for the search
// * @param timeout Time to wait until giving up if the element is not found
// * @param frame Tells whether the element should be searched in a frame or
// * not.
// * @return The found web element as a {@link WebBrowserElement}.
// * @throws ScenarioFailedError If the element is not found before the given
// * timeout is reached.
// * TODO Try to get rid off this method by selecting the frame explicitly before
// * waiting for an element. Hence, {@link WebPageElement} waitForElement*
// * methods could be used instead.
// */
//public WebBrowserElement waitForElement(final WebBrowserElement parentElement, final By locator, final int timeout, final boolean frame) {
//	return this.browser.waitForElement(parentElement, locator, true /*fail*/, timeout);
//}

/**
 * Wait until at least one element is found using each of the given locator.
 * <p>
 * That method stores each found element using the given locators in the
 * the returned array, hence it may have more than one non-null slot.
 * </p><p>
 * Note that the method stop to search as soon as at least one element is found.
 * Hence, when several elements are found and returned in the array, that means
 * they have been found in the same loop. The timeout is only reached when
 * <b>no</b> element is found...
 * </p><p>
 * Note also that only displayed elements are returned.
 * </p>
 * @param locators List of locators to use to find the elements in the current page.
 * @return An array with one non-null slot per element found before timeout occurs.
 * @throws WaitElementTimeoutError if no element is found before the timeout occurs.
 */
public WebBrowserElement[] waitForMultipleElements(final By... locators) {
	return waitForMultipleElements(true /*fail*/, timeout(), locators);
}

/**
 * Wait default timeout while the wrapped element is busy.
 * <p>
 * By default the wrapped element is busy if the status message is displayed and the timeout
 * is {@link #openTimeout()} seconds.
 * </p>
 * @throws ScenarioFailedError If the timeout is reached while the wrapped element is still
 * busy.
 */
public void waitWhileBusy() {
	waitWhileBusy(openTimeout());
}

/**
 * Wait given timeout while the wrapped element is busy.
 * <p>
 * By default the wrapped element is busy if the status message is displayed.
 * </p>
 * @param busyTimeout The number of seconds to wait while the wrapped element is busy
 * @throws WaitElementTimeoutError If the timeout is reached while the wrapped element is still
 * busy.
 */
public void waitWhileBusy(final int busyTimeout) {
	long timeoutInMillis = busyTimeout * 1000 + System.currentTimeMillis();

	while (getBusyIndicatorElements() != null) {
		if (System.currentTimeMillis() > timeoutInMillis) {
			throw new PageBuysTimeoutError("Wrapped element was undergoing an operation which did not finish before timeout '" + busyTimeout + "s'");
		}
	}
}

/**
 * Wait until the wrapped element is no longer displayed.
 *
 * @param seconds The timeout before giving up if the element is still displayed
 * @throws ScenarioFailedError If the wrapped element is still displayed after
 * the {@link Config#closeDialogTimeout()}.
 */
public void waitWhileDisplayed(final int seconds) throws ScenarioFailedError {
	if (this.element != null) {
		try {
			this.element.waitWhileDisplayed(seconds);
		} catch (WebDriverException t) {}
	}
}
}
