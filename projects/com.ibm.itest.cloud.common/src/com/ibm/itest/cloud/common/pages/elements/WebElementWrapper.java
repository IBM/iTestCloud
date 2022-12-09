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
package com.ibm.itest.cloud.common.pages.elements;

import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;

import com.ibm.itest.cloud.common.config.IUser;
import com.ibm.itest.cloud.common.tests.config.Config;
import com.ibm.itest.cloud.common.tests.scenario.errors.*;
import com.ibm.itest.cloud.common.tests.web.WebBrowserFrame;
import com.ibm.itest.cloud.common.tests.web.WebPage;
import com.ibm.itest.cloud.common.tests.web.WebPage.ClickType;
import com.ibm.itest.cloud.common.tests.web.browsers.WebBrowser;

/**
 * This class wraps a web element and add some actions and functionalities
 * that anyone can use. It also add some specific operations only accessible to
 * the class hierarchy.
 * <p>
 * There's still no public action or functionalities at this level, only common
 * operations for subclasses usage:
 * <ul>
 * <li>{@link #waitWhileDisplayed(int)}: Wait until the current window is closed.</li>
 * <li>{@link #waitForElement(By)}: Wait until having found an element
 * searched using the given mechanism.</li>
 * </ul>
 * </p>
 */
public abstract class WebElementWrapper extends WebPageElement {

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
 * Click on the button found relatively to the wrapped web element.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * <li>there's no verification that the button turns to enable after having clicked
 * on it</li>
 * </p>
 * @param buttonBy The mechanism to find the button in the current page
 * @return The web element (as a {@link WebBrowserElement}) found in the page
 *
 * @see #waitForElement(WebBrowserElement, By)
 * @see WebBrowser#clickButton(WebBrowserElement, int, boolean)
 */
public WebBrowserElement clickButton(final By buttonBy) {
	return this.page.clickButton(this.element, buttonBy);
}

/**
 * Click on the button found relatively to the given parent web element.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * <li>there's no verification that the button turns to enable after having clicked
 * on it</li>
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param buttonBy The mechanism to find the button in the current page
 * @return The web element (as a {@link WebBrowserElement}) found in the page
 *
 * @see #waitForElement(WebBrowserElement, By)
 * @see WebBrowser#clickButton(WebBrowserElement, int, boolean)
 */
public WebBrowserElement clickButton(final WebBrowserElement parentElement, final By buttonBy) {
	return this.page.clickButton((parentElement != null) ? parentElement : this.element, buttonBy);
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
	return getPage().openElementUsingLink(this.element.waitForElement(linkBy), findBy, elementClass, elementData);
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
	return getPage().openElementUsingLink(this.element.waitForElement(linkBy), elementClass, elementData);
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
public <P extends WebElementWrapper> P openElementUsingLink(final WebBrowserElement linkElement, final By findBy, final Class<P> elementClass, final String... elementData) {
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
public <P extends WebPage> P openPageUsingBrowser(final Class<P> pageClass, final String... data) {
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
public <P extends WebPage> P openPageUsingLink(final WebBrowserElement linkElement, final Class<P> openedPageClass, final Action postLinkClickAction, final String... pageData) {
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
public WebBrowserElement select(final By locator, final Pattern pattern) {
	return this.page.select(this.element.waitForElement(locator), pattern);
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
public WebBrowserElement select(final By locator, final String selection) {
	return this.page.select(this.element.waitForElement(locator), selection);
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
public WebBrowserElement select(final WebBrowserElement listElement, final Pattern pattern) {
	return this.page.select((listElement != null) ? listElement : this.element, pattern);
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
public WebBrowserElement select(final WebBrowserElement listElement, final String selection) {
	return this.page.select((listElement != null) ? listElement : this.element, selection);
}

/**
 * @see WebPage#typePassword(WebBrowserElement, IUser)
 */
public void typePassword(final WebBrowserElement inputElement, final IUser user) {
	this.page.typePassword(inputElement, user);
}

/**
 * Type a text into an input web element found inside the wrapped web element
 * using the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the input field is not found before {@link #timeout()} seconds</li>
 * <li>if will fail if the input field does not turn enabled before {@link #timeout()}
 * seconds</li>
 * <li>the input element will be cleared prior entering the given text</li>
 * </ul>
 * </p><p>
 * Note also that a {@link Keys#TAB} is hit after having entered the text in the
 * input field in order to trigger the 'keyEvent' and makes the javascript associated
 * with the filed working properly.
 * </p>
 * @param locator The mechanism to find the input web element in the current
 * page
 * @param text The text to type in the input element
 * @return The text web element (as a {@link WebBrowserElement}) found
 * in the page
 *
 * @see #waitForElement(WebBrowserElement, By)
 * @see WebBrowser#typeText(WebBrowserElement, String, Keys, boolean, int)
 */
public WebBrowserElement typeText(final By locator, final String text) {
	return this.page.typeText(this.element, locator, text);
}

/**
 * Type a text into an input web element found inside the wrapped web element
 * using the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the input field is not found before {@link #timeout()} seconds</li>
 * <li>if will fail if the input field does not turn enabled before {@link #timeout()}
 * seconds</li>
 * <li>the input element will be cleared prior entering the given text</li>
 * </ul>
 * </p><p>
 * Note also that a {@link Keys#TAB} is hit after having entered the text in the
 * input field in order to trigger the 'keyEvent' and makes the javascript associated
 * with the filed working properly.
 * </p>
 * @param locator The mechanism to find the input web element in the current
 * page
 * @param text The text to type in the input element
 * @param key The key to hit after having entered the text in the input field.
 * If <code>null</code> is provided as the value of this parameter, a key will not
 * be hit after having entered the text in the input field.
 * @return The text web element (as a {@link WebBrowserElement}) found
 * in the page
 *
 * @see #waitForElement(WebBrowserElement, By)
 * @see WebBrowser#typeText(WebBrowserElement, String, Keys, boolean, int)
 */
public WebBrowserElement typeText(final By locator, final String text, final Keys key) {
	WebBrowserElement inputElement = this.element.waitForElement(locator);
	this.page.typeText(inputElement, text, key);
	return inputElement;
}

/**
 * @see WebPage#typeText(WebBrowserElement, By, String)
 */
public WebBrowserElement typeText(final WebBrowserElement parentElement, final By locator, final String text) {
	return this.page.typeText((parentElement != null) ? parentElement : this.element, locator, text);
}

/**
 * @see WebPage#typeText(WebBrowserElement, String)
 */
public void typeText(final WebBrowserElement inputElement, final String text) {
	this.page.typeText(inputElement, text);
}

/**
 * @see WebPage#typeText(WebBrowserElement, String, Keys)
 */
public void typeText(final WebBrowserElement inputElement, final String text, final Keys key) {
	this.page.typeText(inputElement, text, key);
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
