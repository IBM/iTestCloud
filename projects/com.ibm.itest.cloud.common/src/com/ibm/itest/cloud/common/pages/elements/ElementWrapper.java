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

import com.ibm.itest.cloud.common.browsers.Browser;
import com.ibm.itest.cloud.common.config.Config;
import com.ibm.itest.cloud.common.config.IUser;
import com.ibm.itest.cloud.common.pages.Page;
import com.ibm.itest.cloud.common.pages.Page.ClickType;
import com.ibm.itest.cloud.common.scenario.errors.*;

/**
 * This class wraps a web element and add some actions and functionalities
 * that anyone can use. It also add some specific operations only accessible to
 * the class hierarchy.
 * <p>
 * There's still no public action or functionalities at this level, only common
 * operations for subclasses usage:
 * <ul>
 * <li>{@link #waitWhileDisplayed(int)}: Wait until the current window is closed.</li>
 * </ul>
 * </p>
 */
public abstract class ElementWrapper extends PageElement {

	/**
	 * The wrapped web element.
	 */
	protected BrowserElement element;

	/**
	 * The parent of current wrapper.
	 * <p>
	 * Children web elements should be looked from the web element of this
	 * wrapper if not <code>null</code>. If <code>null</code>, then they have
	 * to be looked for in the entire page.
	 * </p>
	 */
	protected ElementWrapper parent;

public ElementWrapper(final ElementWrapper parent) {
	this(parent.getPage());
	this.parent = parent;
}

public ElementWrapper(final ElementWrapper parent, final BrowserElement element) {
	this(parent.getPage(), element);
	this.parent = parent;
}

public ElementWrapper(final ElementWrapper parent, final By findBy) {
	this(parent.getPage(), parent.element.waitForElement(findBy));
	this.parent = parent;
}

public ElementWrapper(final Page page) {
	super(page);
}

public ElementWrapper(final Page page, final BrowserElement element) {
	super(page);
	this.element = element;
}

public ElementWrapper(final Page page, final By findBy) {
	super(page);
	this.element = page.waitForElement(findBy);
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
 * @return The web element (as a {@link BrowserElement}) found in the page
 *
 * @see Browser#clickButton(BrowserElement, int, boolean)
 */
public BrowserElement clickButton(final BrowserElement parentElement, final By buttonBy) {
	return this.page.clickButton((parentElement != null) ? parentElement : this.element, buttonBy);
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
 * @return The web element (as a {@link BrowserElement}) found in the page
 *
 * @see Browser#clickButton(BrowserElement, int, boolean)
 */
public BrowserElement clickButton(final By buttonBy) {
	return this.page.clickButton(this.element, buttonBy);
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
protected BrowserElement[] getBusyIndicatorElements() {
	return this.browser.waitForMultipleElements(this.element, tinyTimeout(), false /*fail*/, getBusyIndicatorElementLocators());
}

/**
 * Return the wrapped web element.
 *
 * @return The wrapped web element as {@link BrowserElement}.
 */
public BrowserElement getElement() {
	return this.element;
}

/**
 * Returns the parent element.
 *
 * @return The parent element of <code>null</code> if there's no parent.
 */
public ElementWrapper getParent() {
	return this.parent;
}

/**
 * Returns the parent element.
 *
 * @return The parent element of <code>null</code> if there's no parent.
 */
public BrowserElement getParentElement() {
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
 * @param linkElement The link on which to click.
 * @param findBy The locator of the element opened after clicking on the link element.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link ElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends ElementWrapper> P openElementUsingLink(final BrowserElement linkElement, final By findBy, final Class<P> elementClass, final String... elementData) {
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
 * @return The element (as a subclass of {@link ElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends ElementWrapper> P openElementUsingLink(final BrowserElement linkElement, final Class<P> elementClass, final ClickType clickType, final String... elementData) {
	return getPage().openElementUsingLink(linkElement, elementClass, clickType, elementData);
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkElement The link on which to click.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link ElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends ElementWrapper> P openElementUsingLink(final BrowserElement linkElement, final Class<P> elementClass, final String... elementData) {
	return getPage().openElementUsingLink(linkElement, elementClass, elementData);
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkBy The link locator on which to click.
 * @param findBy The locator of the element opened after clicking on the link element.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link ElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends ElementWrapper> P openElementUsingLink(final By linkBy, final By findBy, final Class<P> elementClass, final String... elementData) {
	return getPage().openElementUsingLink(this.element.waitForElement(linkBy), findBy, elementClass, elementData);
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkBy The link locator on which to click.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link ElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends ElementWrapper> P openElementUsingLink(final By linkBy, final Class<P> elementClass, final String... elementData) {
	return getPage().openElementUsingLink(this.element.waitForElement(linkBy), elementClass, elementData);
}

/**
 * Retrieve the existing page for the browser current URL. Create it if it's the first
 * time the page is requested.
 *
 * @param pageClass The class associated with the page to open
 * @param data Additional CLM information to be stored in the page
 * @return The instance of the class associate with the page.
 */
public <P extends Page> P openPageUsingBrowser(final Class<P> pageClass, final String... data) {
	return getPage().openPageUsingBrowser(pageClass, data);
}

/**
 * Click on the given link assuming that it will open the given page.
 *
 * @param linkElement The link on which to click
 * @param openedPageClass The class associated with the opened page
 * @param postLinkClickAction The action to perform after clicking the link as {@link Action}.
 * @param pageData Provide additional information to store in the page when opening it
 * @return The web page (as a subclass of {@link Page}) opened after
 * @see Page#openPageUsingLink(BrowserElement, Class, String...)
 */
public <P extends Page> P openPageUsingLink(final BrowserElement linkElement, final Class<P> openedPageClass, final Action postLinkClickAction, final String... pageData) {
	return getPage().openPageUsingLink(linkElement, openedPageClass, postLinkClickAction, pageData);
}

/**
 * Click on the given link assuming that it will open the given page.
 *
 * @param linkElement The link on which to click
 * @param openedPageClass The class associated with the opened page
 * @param pageData Provide additional information to store in the page when opening it
 * @return The web page (as a subclass of {@link Page}) opened after
 * @see Page#openPageUsingLink(BrowserElement, Class, String...)
 */
public <P extends Page> P openPageUsingLink(final BrowserElement linkElement, final Class<P> openedPageClass, final String... pageData) {
	return getPage().openPageUsingLink(linkElement, openedPageClass, pageData);
}

/**
 * Select the given item in the given list element found.
 * <p>
 * The items of the selection list are supposed to be found using
 * <code>by.xpath("./option")</code> search mechanism.
 * </p>
 * @param listElement The list element in which perform the selection.
 * @param pattern A pattern matching the item to select in the list, assuming that text matches
 * @return The selected element as {@link BrowserElement}.
 * @throws ScenarioFailedError if no item matches the expected selection.
 */
public BrowserElement select(final BrowserElement listElement, final Pattern pattern) {
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
 * @return The selected element as {@link BrowserElement}.
 * @throws ScenarioFailedError if no item matches the expected selection.
 */
public BrowserElement select(final BrowserElement listElement, final String selection) {
	return this.page.select((listElement != null) ? listElement : this.element, selection);
}

/**
 * Select the given item in the given list element found.
 * <p>
 * The items of the selection list are supposed to be found using
 * <code>by.xpath("./option")</code> search mechanism.
 * </p>
 * @param locator The locator of the list element in which perform the selection.
 * @param pattern A pattern matching the item to select in the list, assuming that text matches
 * @return The selected element as {@link BrowserElement}.
 * @throws ScenarioFailedError if no item matches the expected selection.
 */
public BrowserElement select(final By locator, final Pattern pattern) {
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
 * @return The selected element as {@link BrowserElement}.
 * @throws ScenarioFailedError if no item matches the expected selection.
 */
public BrowserElement select(final By locator, final String selection) {
	return this.page.select(this.element.waitForElement(locator), selection);
}

/**
 * @see Page#typePassword(BrowserElement, IUser)
 */
public void typePassword(final BrowserElement inputElement, final IUser user) {
	this.page.typePassword(inputElement, user);
}

/**
 * @see Page#typeText(BrowserElement, By, String)
 */
public BrowserElement typeText(final BrowserElement parentElement, final By locator, final String text) {
	return this.page.typeText((parentElement != null) ? parentElement : this.element, locator, text);
}

/**
 * @see Page#typeText(BrowserElement, String)
 */
public void typeText(final BrowserElement inputElement, final String text) {
	this.page.typeText(inputElement, text);
}

/**
 * @see Page#typeText(BrowserElement, String, Keys)
 */
public void typeText(final BrowserElement inputElement, final String text, final Keys key) {
	this.page.typeText(inputElement, text, key);
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
 * @return The text web element (as a {@link BrowserElement}) found
 * in the page
 *
 * @see Browser#typeText(BrowserElement, String, Keys, boolean, int)
 */
public BrowserElement typeText(final By locator, final String text) {
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
 * @return The text web element (as a {@link BrowserElement}) found
 * in the page
 *
 * @see Browser#typeText(BrowserElement, String, Keys, boolean, int)
 */
public BrowserElement typeText(final By locator, final String text, final Keys key) {
	BrowserElement inputElement = this.element.waitForElement(locator);
	this.page.typeText(inputElement, text, key);
	return inputElement;
}

/**
 * Waits until have found the element using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 *
 * @return The web element as {@link BrowserElement}.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 * @throws MultipleVisibleElementsError if several elements are found.
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By locator) {
	return this.browser.waitForElement((parentElement != null) ? parentElement : this.element, locator, timeout(), true /*fail*/, true /*displayed*/, true /*single*/);
}

/**
 * Waits until have found one of element using the given search locators.
 * <p>
 * Fails only if specified and after having waited the given timeout.
 * </p>
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locators Search locators of the expected elements.
 *
 * @return The web element as {@link BrowserElement}.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By... locators) {
	return this.browser.waitForElement((parentElement != null) ? parentElement : this.element, timeout(), true /*fail*/, locators);
}

/**
 * Waits until have found the element using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research.
 *
 * @return The web element as {@link BrowserElement}.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 * @throws MultipleVisibleElementsError if several elements are found.
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By locator, final int timeout) {
	return this.browser.waitForElement((parentElement != null) ? parentElement : this.element, locator, timeout, true /*fail*/, true /*displayed*/, true /*single*/);
}

/**
 * Waits until have found the element using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research.
 * @param fail Tells whether to fail if none of the locators is find before timeout.
 *
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail.
 * @throws MultipleVisibleElementsError if several elements are found.
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By locator, final int timeout, final boolean fail) {
	return this.browser.waitForElement((parentElement != null) ? parentElement : this.element, locator, timeout, fail, true /*displayed*/, true /*single*/);
}

/**
 * Waits until have found the element using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 *
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail.
 * @throws MultipleVisibleElementsError if several elements are found.
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By locator, final int timeout, final boolean fail, final boolean displayed) {
	return this.browser.waitForElement((parentElement != null) ? parentElement : this.element, locator, timeout, fail, displayed, true /*single*/);
}

/**
 * Waits until have found the element using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @param single Tells whether a single element is expected.
 *
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail.
 * @throws MultipleVisibleElementsError if several elements are found and only single one was expected.
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By locator, final int timeout, final boolean fail, final boolean displayed, final boolean single) {
	return this.browser.waitForElement((parentElement != null) ? parentElement : this.element, locator, timeout, fail, displayed, single);
}

/**
 * Waits until have found one of element using the given search locators.
 * <p>
 * Fails only if specified and after having waited the given timeout.
 * </p>
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param timeout The time to wait before giving up the research.
 * @param fail Tells whether to fail if none of the locators is find before timeout.
 * @param locators Search locators of the expected elements.
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail.
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final int timeout, final boolean fail, final By... locators) {
	return this.browser.waitForElement((parentElement != null) ? parentElement : this.element, timeout, fail, locators);
}

/**
 * Waits until have found one of element using the given search locators.
 * <p>
 * Fails only if specified and after having waited the given timeout.
 * </p>
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param timeout The time to wait before giving up the research.
 * @param locators Search locators of the expected elements.
 *
 * @return The web element as {@link BrowserElement}.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final int timeout, final By... locators) {
	return this.browser.waitForElement((parentElement != null) ? parentElement : this.element, timeout, true /*fail*/, locators);
}

/**
 * Waits until have found the element using given locator.
 * <p>
 * Note that:
 * <ul>
 * <li>hidden element will be ignored</li>
 * <li>it will fail if:
 * <ol>
 * <li>the element is not found before timeout seconds</li>
 * <li>there's more than one element found</li>
 * </ol></li>
 * </ul>
 * </p>
 * </p>
 *
 * @param locator Locator to find the element in the current page.
 *
 * @return The web element as {@link BrowserElement}.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 * @throws MultipleVisibleElementsError if several elements are found.
 */
public BrowserElement waitForElement(final By locator) {
	return this.browser.waitForElement(this.element, locator, timeout(), true /*fail*/, true /*displayed*/, true /*single*/);
}

/**
 * Waits until have found one of element using the given search locators.
 * <p>
 * Fails only if specified and after having waited the given timeout.
 * </p>
 *
 * @param locators Search locators of the expected elements.
 *
 * @return The web element as {@link BrowserElement}.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 */
public BrowserElement waitForElement(final By... locators) {
	return this.browser.waitForElement(this.element, timeout(), true /*fail*/, locators);
}

/**
 * Waits until have found the element using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param locator Locator to find the element in the current page.
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 *
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 * @throws MultipleVisibleElementsError if several elements are found.
 */
public BrowserElement waitForElement(final By locator, final boolean displayed) {
	return this.browser.waitForElement(this.element, locator, timeout(), true /*fail*/, displayed, true /*single*/);
}

/**
 * Waits until have found the web element relatively to a parent element using
 * the respective given mechanisms.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if:
 * <ol>
 * <li>the element is not found before {@link #timeout()} seconds</li>
 * <li>there's more than one element found</li>
 * </ol></li>
 * <li>hidden element will be ignored</li>
 * </p>
 * @param parentLocator The locator to find the parent element in the current
 * element, if <code>null</code>, the element will be searched in the current element.
 * @param locator The locator to find the element in the current page or
 * from the given parent element if not <code>null</code>
 * @return The web element as {@link BrowserElement}
 * @throws ScenarioFailedError if no element was found before the {@link #timeout()}.
 *
 * @see #waitForElement(By, By, boolean, int)
 */
public BrowserElement waitForElement(final By parentLocator, final By locator) {
	return waitForElement(parentLocator, locator, true/*fail*/, timeout());
}

/**
 * Waits until have found the web element relatively to a parent element using
 * the respective given mechanisms.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if there's more than one element found</li>
 * <li>hidden element will be ignored</li>
 * </p>
 * @param parentLocator The locator to find the parent element in the current
 * element, if <code>null</code>, the element will be searched in the current element.
 * @param locator The locator to find the element in the current page or
 * from the given parent element if not <code>null</code>.
 * @param fail Tells whether to fail if none of the elements is find before timeout.
 * @param time_out The time to wait before giving up the research.
 *
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail.
 *
 * @throws ScenarioFailedError if no element was found before the timeout and
 * asked to fail.
 *
 * @see Browser#waitForElement(BrowserElement, By, int, boolean, boolean, boolean)
 */
public BrowserElement waitForElement(final By parentLocator, final By locator, final boolean fail, final int time_out) {
	BrowserElement parentElement = (parentLocator != null) ? waitForElement(parentLocator) : this.element;
	return this.browser.waitForElement(parentElement, locator, time_out, fail, true /*displayed*/, true /*single*/);
}

/**
 * Waits until have found the element using given locator.
 * <p>
 * Note that:
 * <ul>
 * <li>hidden element will be ignored</li>
 * <li>it will fail if:
 * <ol>
 * <li>the element is not found before timeout seconds</li>
 * <li>there's more than one element found</li>
 * </ol></li>
 * </ul>
 * </p>
 * </p>
 *
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research.
 *
 * @return The web element as {@link BrowserElement}.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 * @throws MultipleVisibleElementsError if several elements are found.
 */
public BrowserElement waitForElement(final By locator, final int timeout) {
	return this.browser.waitForElement(this.element, locator, timeout, true /*fail*/, true /*displayed*/, true /*single*/);
}

/**
 * Waits until have found the element using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research.
 * @param fail Tells whether to fail if none of the locators is find before timeout.
 *
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail.
 * @throws MultipleVisibleElementsError if several elements are found.
 */
public BrowserElement waitForElement(final By locator, final int timeout, final boolean fail) {
	return this.browser.waitForElement(this.element, locator, timeout, fail, true /*displayed*/, true /*single*/);
}

/**
 * Waits until have found the element using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 *
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail.
 * @throws MultipleVisibleElementsError if several elements are found.
 */
public BrowserElement waitForElement(final By locator, final int timeout, final boolean fail, final boolean displayed) {
	return this.browser.waitForElement(this.element, locator, timeout, fail, displayed, true /*single*/);
}

/**
 * Waits until have found the element using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @param single Tells whether a single element is expected.
 *
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail.
 * @throws MultipleVisibleElementsError if several elements are found and only single one was expected.
 */
public BrowserElement waitForElement(final By locator, final int timeout, final boolean fail, final boolean displayed, final boolean single) {
	return this.browser.waitForElement(this.element, locator, timeout, fail, displayed, true /*single*/);
}

/**
 * Waits until have found one of element using the given search locators.
 * <p>
 * Fails only if specified and after having waited the given timeout.
 * </p>
 *
 * @param timeout The time to wait before giving up the research.
 * @param fail Tells whether to fail if none of the locators is find before timeout.
 * @param locators Search locators of the expected elements.
 *
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail.
 */
public BrowserElement waitForElement(final int timeout, final boolean fail, final By... locators) {
	return this.browser.waitForElement(this.element, timeout, fail, locators);
}

/**
 * Waits until have found one of element using the given search locators.
 * <p>
 * Fails only if specified and after having waited the given timeout.
 * </p>
 *
 * @param timeout The time to wait before giving up the research.
 * @param locators Search locators of the expected elements.
 *
 * @return The web element as {@link BrowserElement}.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 */
public BrowserElement waitForElement(final int timeout, final By... locators) {
	return this.browser.waitForElement(this.element, timeout, true /*fail*/, locators);
}

/**
 * Waits until have found one or several elements using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 *
 * @return A {@link List} of web element as {@link BrowserElement}.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 */
public List<BrowserElement> waitForElements(final BrowserElement parentElement, final By locator) {
	return this.browser.waitForElements((parentElement != null) ? parentElement : this.element, locator, timeout(), true /*fail*/, true /*displayed*/);
}

/**
 * Waits until have found one or several elements using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 *
 * @return A {@link List} of web element as {@link BrowserElement}.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 */
public List<BrowserElement> waitForElements(final BrowserElement parentElement, final By locator, final boolean displayed) {
	return this.browser.waitForElements((parentElement != null) ? parentElement : this.element, locator, timeout(), true /*fail*/, displayed);
}

/**
 * Waits until have found one or several elements using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research.
 *
 * @return A {@link List} of web element as {@link BrowserElement}.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 */
public List<BrowserElement> waitForElements(final BrowserElement parentElement, final By locator, final int timeout) {
	return this.browser.waitForElements((parentElement != null) ? parentElement : this.element, locator, timeout, true /*fail*/, true /*displayed*/);
}

/**
 * Waits until have found one or several elements using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research.
 * @param fail Tells whether to fail if none of the locators is find before timeout.
 *
 * @return A {@link List} of web element as {@link BrowserElement}. Might
 * be empty if no element was found before the timeout and asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout and
 * asked to fail.
 */
public List<BrowserElement> waitForElements(final BrowserElement parentElement, final By locator, final int timeout, final boolean fail) {
	return this.browser.waitForElements((parentElement != null) ? parentElement : this.element, locator, timeout, fail, true /*displayed*/);
}

/**
 * Waits until have found one or several elements using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research.
 * @param fail Tells whether to fail if none of the locators is find before timeout.
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 *
 * @return A {@link List} of web element as {@link BrowserElement}. Might
 * be empty if no element was found before the timeout and asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout and
 * asked to fail.
 */
public List<BrowserElement> waitForElements(final BrowserElement parentElement, final By locator, final int timeout, final boolean fail, final boolean displayed) {
	return this.browser.waitForElements((parentElement != null) ? parentElement : this.element, locator, timeout, fail, displayed);
}

/**
 * Waits until have found one or several elements using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param locator Locator to find the element in the current page.
 *
 * @return A {@link List} of web element as {@link BrowserElement}.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 */
public List<BrowserElement> waitForElements(final By locator) {
	return this.browser.waitForElements(this.element, locator, timeout(), true /*fail*/, true /*displayed*/);
}

/**
 * Waits until have found one or several elements using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research.
 *
 * @return A {@link List} of web element as {@link BrowserElement}.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout.
 */
public List<BrowserElement> waitForElements(final By locator, final int timeout) {
	return this.browser.waitForElements(this.element, locator, timeout, true /*fail*/, true /*displayed*/);
}

/**
 * Waits until have found one or several elements using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research.
 * @param fail Tells whether to fail if none of the locators is find before timeout.
 *
 * @return A {@link List} of web element as {@link BrowserElement}. Might
 * be empty if no element was found before the timeout and asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout and
 * asked to fail.
 */
public List<BrowserElement> waitForElements(final By locator, final int timeout, final boolean fail) {
	return this.browser.waitForElements(this.element, locator, timeout, fail, true /*displayed*/);
}

/**
 * Waits until have found one or several elements using given locator.
 * <p>
 * Only fails if specified and after having waited the given timeout.
 * </p>
 *
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research.
 * @param fail Tells whether to fail if none of the locators is find before timeout.
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 *
 * @return A {@link List} of web element as {@link BrowserElement}. Might
 * be empty if no element was found before the timeout and asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element was found before the timeout and
 * asked to fail.
 */
public List<BrowserElement> waitForElements(final By locator, final int timeout, final boolean fail, final boolean displayed) {
	return this.browser.waitForElements(this.element, locator, timeout, fail, displayed);
}

/**
 * Wait for loading of the wrapped element to complete.
 */
public void waitForLoadingEnd() {
	waitWhileBusy();
}

/**
 * Waits until at least one element is found using each of the given locator.
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
 *
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content.
 * @param locators List of locators to use to find the elements in the current page.
 *
 * @return An array with one non-null slot per element found before timeout occurs.
 *
 * @throws WaitElementTimeoutError if no element is found before the timeout occurs.
 */
public BrowserElement[] waitForMultipleElements(final BrowserElement parentElement, final By... locators) {
	return this.browser.waitForMultipleElements((parentElement != null) ? parentElement : this.element, locators, timeout(), true /*fail*/, null /*displayFlags*/);
}

/**
 * Waits until at least one element is found using each of the given locator.
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
 *
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content.
 * @param locators List of locators to use to find the elements in the current page.
 * @param timeout The time to wait before giving up the research.
 * @param fail Tells whether to fail if none of the locators is find before timeout.
 * @param displayFlags List of flag telling whether the corresponding element should
 * be displayed or not. If <code>null</code>, then it's assumed that all elements
 * have to be displayed.
 *
 * @return An array with one non-null slot per element found before timeout
 * occurs or <code>null</code> if none was found and it has been asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element is found before the timeout occurs
 * and it has been asked to fail.
 */
public BrowserElement[] waitForMultipleElements(final BrowserElement parentElement, final By[] locators, final int timeout, final boolean fail, final boolean[] displayFlags) {
	return this.browser.waitForMultipleElements((parentElement != null) ? parentElement : this.element, locators, timeout, fail, displayFlags);
}

/**
 * Waits until at least one element is found using each of the given locator.
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
 *
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content.
 * @param timeout The time to wait before giving up the research.
 * @param fail Tells whether to fail if none of the locators is find before timeout.
 * @param locators List of locators to use to find the elements in the current page.
 *
 * @return An array with one non-null slot per element found before timeout
 * occurs or <code>null</code> if none was found and it has been asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element is found before the timeout occurs
 * and it has been asked to fail.
 */
public BrowserElement[] waitForMultipleElements(final BrowserElement parentElement, final int timeout, final boolean fail, final By... locators) {
	return this.browser.waitForMultipleElements((parentElement != null) ? parentElement : this.element, locators, timeout, fail, null /*displayFlags*/);
}

/**
 * Waits until at least one element is found using each of the given locator.
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
 *
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content.
 * @param timeout The time to wait before giving up the research.
 * @param locators List of locators to use to find the elements in the current page.
 *
 * @return An array with one non-null slot per element found before timeout occurs.
 *
 * @throws WaitElementTimeoutError if no element is found before the timeout occurs.
 */
public BrowserElement[] waitForMultipleElements(final BrowserElement parentElement, final int timeout, final By... locators) {
	return this.browser.waitForMultipleElements((parentElement != null) ? parentElement : this.element, locators, timeout, true /*fail*/, null /*displayFlags*/);
}

/**
 * Waits until at least one element is found using each of the given locator.
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
 *
 * @param locators List of locators to use to find the elements in the current page.
 *
 * @return An array with one non-null slot per element found before timeout.
 *
 * @throws WaitElementTimeoutError if no element is found before the timeout occurs.
 */
public BrowserElement[] waitForMultipleElements(final By... locators) {
	return this.browser.waitForMultipleElements(this.element, locators, timeout(), true /*fail*/, null /*displayFlags*/);
}

/**
 * Waits until at least one element is found using each of the given locator.
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
 *
 * @param locators List of locators to use to find the elements in the current page.
 * @param timeout The time to wait before giving up the research.
 * @param fail Tells whether to fail if none of the locators is find before timeout.
 * @param displayFlags List of flag telling whether the corresponding element should
 * be displayed or not. If <code>null</code>, then it's assumed that all elements
 * have to be displayed.
 *
 * @return An array with one non-null slot per element found before timeout
 * occurs or <code>null</code> if none was found and it has been asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element is found before the timeout occurs
 * and it has been asked to fail.
 */
public BrowserElement[] waitForMultipleElements(final By[] locators, final int timeout, final boolean fail, final boolean[] displayFlags) {
	return this.browser.waitForMultipleElements(this.element, locators, timeout, fail, displayFlags);
}

/**
 * Waits until at least one element is found using each of the given locator.
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
 *
 * @param timeout The time to wait before giving up the research.
 * @param fail Tells whether to fail if none of the locators is find before timeout.
 * @param locators List of locators to use to find the elements in the current page.
 *
 * @return An array with one non-null slot per element found before timeout
 * occurs or <code>null</code> if none was found and it has been asked not to fail.
 *
 * @throws WaitElementTimeoutError if no element is found before the timeout occurs
 * and it has been asked to fail.
 */
public BrowserElement[] waitForMultipleElements(final int timeout, final boolean fail, final By... locators) {
	return this.browser.waitForMultipleElements(this.element, locators, timeout, fail, null /*displayFlags*/);
}

/**
 * Waits until at least one element is found using each of the given locator.
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
 *
 * @param timeout The time to wait before giving up the research.
 * @param locators List of locators to use to find the elements in the current page.
 *
 * @return An array with one non-null slot per element found before timeout.
 *
 * @throws WaitElementTimeoutError if no element is found before the timeout occurs.
 */
public BrowserElement[] waitForMultipleElements(final int timeout, final By... locators) {
	return this.browser.waitForMultipleElements(this.element, locators, timeout, true /*fail*/, null /*displayFlags*/);
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
