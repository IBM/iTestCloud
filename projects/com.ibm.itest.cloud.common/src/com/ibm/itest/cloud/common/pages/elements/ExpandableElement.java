/*********************************************************************
 * Copyright (c) 2013, 2022 IBM Corporation and others.
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

import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.debugPrintStackTrace;
import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.debugPrintln;
import static com.ibm.itest.cloud.common.utils.ByUtils.isRelativeLocator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import com.ibm.itest.cloud.common.pages.Page;
import com.ibm.itest.cloud.common.pages.frames.BrowserFrame;
import com.ibm.itest.cloud.common.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.scenario.errors.WaitElementTimeoutError;

/**
 * Abstract class to handle an expandable web element in a web page.
 * <p>
 * By default the expansion mechanism of the web element is managed by its
 * <code>aria-expanded</code> attribute. If the wrapped web element does
 * not have this attribute, then {@link #isExpandable()} and {@link #isExpanded()}
 * methods must be overridden in the corresponding subclass.
 * </p><p>
 * Public API for this class is available in {@link IExpandableElement} interface.
 * </p><p>
 * Following internal API method are defined in this abstract class and
 * might be overridden by subclasses:
 * <ul>
 * <li>{@link #getExpandableAttribute()}:  Return the expandable attribute.</li>
 * </ul>
 * </p>
 */
public class ExpandableElement extends ElementWrapper implements IExpandableElement {

	/**
	 * The web element on which to click in order to perform expanse operations.
	 * <p>
	 * When <code>null, the it's assumed that the expansion element is the wrapped element itself.
	 * </p>
	 */
	protected BrowserElement expansionElement;

/**
 * Create an expandable element child of the given parent using the wrapped and
 * expansion web elements found by the given locators.
 * <p>
 * Important: This constructor is only used for initializing {@link DynamicExpandableElement} and
 * should not be used for any other purposes.
 * </p>
 * @param parent The wrapped web element parent
 */
protected ExpandableElement(final ElementWrapper parent) {
	super(parent);
}

/**
 * Create an expandable element child of the given parent using the wrapped and
 * expansion web elements found by the given locators.
 * <p>
 * When using this constructor, it's assumed that the wrapped web element is also
 * used for the expanse and collapse operations (ie. by simply clicking on it).
 * </p>
 * @param parent The wrapped web element parent
 * @param locator The locator to find the wrapped web element
 */
public ExpandableElement(final ElementWrapper parent, final By locator) {
	this(parent, locator, null /*expansionLocator*/);
}

/**
 * Create an expandable element child of the given parent using the wrapped and
 * expansion web elements found by the given locators.
 * <p>
 * <b>Important</b>: The expansion web element locator is assumed to be relative
 * to the wrapped web element.
 * </p>
 * @param parent The wrapped web element parent
 * @param locator The locator to find the wrapped web element
 * @param expansionLocator The locator to find the expansion web element
 */
public ExpandableElement(final ElementWrapper parent, final By locator, final By expansionLocator) {
	super(parent, locator);
	this.expansionElement = getExpansionElement(expansionLocator);
}

/**
 * Create an expandable element child of the given parent using the wrapped and
 * expansion web elements found by the given locators.
 * <p>
 * <b>Important</b>: The expansion web element locator is assumed to be relative
 * to the wrapped web element.
 * </p>
 * @param parent The wrapped web element parent
 * @param locator The locator to find the wrapped web element
 * @param expansionLocator The locator to find the expansion web element
 */
public ExpandableElement(final ElementWrapper parent, final By locator, final BrowserFrame frame, final By expansionLocator) {
	super(parent, locator, frame);
	this.expansionElement = getExpansionElement(expansionLocator);
}

/**
 * Create an expandable element child of the given parent using the wrapped and
 * expansion web elements found by the given locators.
 * <p>
 * When using this constructor, it's assumed that the wrapped web element is also
 * used for the expanse and collapse operations (ie. by simply clicking on it).
 * </p>
 * @param parent The wrapped web element parent
 * @param webElement The wrapped web element
 */
public ExpandableElement(final ElementWrapper parent, final BrowserElement webElement) {
	this(parent, webElement, (By) null /*expansionLocator*/);
}

/**
 * Create an expandable element child of the given parent using the given wrapped
 * web element and the expansion web element found using the given locator.
 * <p>
 * <b>Important</b>: The expansion web element locator is assumed to be relative
 * to the wrapped web element.
 * </p>
 * @param parent The wrapped web element parent
 * @param webElement The wrapped web element
 * @param expansionLocator The locator to find the expansion web element
 */
public ExpandableElement(final ElementWrapper parent, final BrowserElement webElement, final By expansionLocator) {
	this(parent, webElement, (BrowserElement) null /*expansionElement*/);
	this.expansionElement = getExpansionElement(expansionLocator);
}

/**
 * Create an expandable element child of the given parent using the wrapped and
 * expansion web elements found by the given locators.
 * <p>
 * When using this constructor, it's assumed that the wrapped web element is also
 * used for the expanse and collapse operations (ie. by simply clicking on it).
 * </p>
 * @param parent The wrapped web element parent
 * @param webElement The wrapped web element
 * @param expansionElement The expansion web element
 */
public ExpandableElement(final ElementWrapper parent, final BrowserElement webElement, final BrowserElement expansionElement) {
	super(parent, webElement);
	this.expansionElement = expansionElement;
}

/**
 * Create an expandable element child of the given parent using the given wrapped
 * web element and the expansion web element found using the given locator.
 * <p>
 * <b>Important</b>: The expansion web element locator is assumed to be relative
 * to the wrapped web element.
 * </p>
 * @param parent The wrapped web element parent
 * @param webElement The wrapped web element
 * @param expansionLocator The locator to find the expansion web element
 */
public ExpandableElement(final ElementWrapper parent, final BrowserElement webElement, final BrowserFrame frame, final By expansionLocator) {
	super(parent, webElement, frame);
	this.expansionElement = getExpansionElement(expansionLocator);
}

/**
 * Create an expandable element in the given page using the wrapped web element
 * found by the given locator.
 * <p>
 * Important: This constructor is only used for initializing {@link DynamicExpandableElement} and
 * should not be used for any other purposes.
 * </p>
 * @param page The page in which the expandable element is located
 */
protected ExpandableElement(final Page page) {
	super(page);
}

/**
 * Create an expandable element in the given page using the wrapped web element
 * found by the given locator.
 * <p>
 * When using this constructor, it's assumed that the wrapped web element is also
 * used for the expanse and collapse operations (ie. by simply clicking on it).
 * </p>
 * @param page The page in which the expandable element is located
 * @param locator The locator to find the wrapped web element
 */
public ExpandableElement(final Page page, final By locator) {
	this(page, locator, null);
}

/**
 * Create an expandable element in the given page using the wrapped and expansion
 * web elements found by the given locators.
 * <p>
 * <b>Important</b>: The expansion web element locator is assumed to be relative
 * to the wrapped web element.
 * </p>
 * @param page The page in which the expandable element is located
 * @param locator The locator to find the wrapped web element
 * @param expansionLocator The locator to find the expansion web element
 */
public ExpandableElement(final Page page, final By locator, final By expansionLocator) {
	super(page, locator);
	this.expansionElement = getExpansionElement(expansionLocator);
}

/**
 * Create an expandable element in the given page using the wrapped and expansion
 * web elements found by the given locators.
 * <p>
 * <b>Important</b>: The expansion web element locator is assumed to be relative
 * to the wrapped web element.
 * </p>
 * @param page The page in which the expandable element is located
 * @param locator The locator to find the wrapped web element
 * @param expansionLocator The locator to find the expansion web element
 */
public ExpandableElement(final Page page, final By locator, final BrowserFrame frame, final By expansionLocator) {
	super(page, locator, frame);
	this.expansionElement = getExpansionElement(expansionLocator);
}

/**
 * Create an expandable element in the given page using the given wrapped web
 * element.
 * <p>
 * When using this constructor, it's assumed that the wrapped web element is also
 * used for the expanse and collapse operation (ie. by simply clicking on it).
 * </p>
 * @param page The page in which the expandable element is located
 * @param webElement The wrapped web element
 */
public ExpandableElement(final Page page, final BrowserElement webElement) {
	this(page, webElement, null);
}

/**
 * Create an expandable element in the given page using the given wrapped web
 * element and the expansion web element found using the given locator.
 * <p>
 * <b>Important</b>: The expansion web element locator is assumed to be relative
 * to the wrapped web element.
 * </p>
 * @param page The page in which the expandable element is located
 * @param webElement The wrapped web element
 * @param expansionLocator The locator to find the expansion web element
 */
public ExpandableElement(final Page page, final BrowserElement webElement, final By expansionLocator) {
	this(page, webElement, null, expansionLocator);
}

/**
 * Create an expandable element in the given page and frame using the given wrapped web
 * element and the expansion web element found using the given locator.
 * <p>
 * <b>Important</b>: The expansion web element locator is assumed to be relative
 * to the wrapped web element.
 * </p>
 * @param page The page in which the expandable element is located
 * @param webElement The wrapped web element
 * @param frame The frame that the element belongs
 * @param expansionLocator The locator to find the expansion web element
 */
public ExpandableElement(final Page page, final BrowserElement webElement, final BrowserFrame frame, final By expansionLocator) {
	super(page, webElement, frame);
	this.expansionElement = getExpansionElement(expansionLocator);
}

/**
 * Click on the expansion element in order to toggle the expandable element.
 * <p>
 * The default behavior is to click on the expansion element after having made it
 * visible assuming that the same web element is used for expansion and collapsing
 * actions. If this default behavior is not true, then a peculiar subclass might want
 * to override this method and perform different actions while expanding or collapsing
 * the current object.
 * </p>
 * @param expand Tells whether the click is for an expansion or a collapse
 */
protected void clickOnExpansionElement(final boolean expand) {
	// Click on the expansion element.
	// At times, the expansion element may be obscured by another element and therefore, not be clickable.
	// As a result, a WebDriverException can occur.
	try {
		this.expansionElement.click();
	}
	catch (WebDriverException e) {
		// If the expansionElement.click() method causes a WebDriverException, use JavaScript to perform the
		// click on the open element in this case.
		debugPrintln("Clicking on expansion element (WebBrowserElement.click()) caused following error. Therefore, try JavaScript (WebBrowserElement.clickViaJavaScript()) to perform click as a workaround.");
		debugPrintln(e.toString());
		debugPrintStackTrace(e.getStackTrace(), 1 /*tabs*/);
		this.expansionElement.clickViaJavaScript();
	}
}

/**
 * Collapse the current web element.
 * <p>
 * If the web element is already collapsed, then nothing happens.
 * </p>
 * @throws ScenarioFailedError If the wrapped web element does not have
 * the <code>aria-expanded</code> attribute.
 */
@Override
public void collapse() throws ScenarioFailedError {
	debugPrintln("		+ Collapse expandable web element " + getElementInfo());
	performAction(false);
}

/**
 * Expand the current web element.
 * <p>
 * If the web element is already expanded, then nothing happens.
 * </p>
 * @throws ScenarioFailedError If the wrapped web element does not have
 * the <code>aria-expanded</code> attribute.
 */
@Override
public void expand() throws ScenarioFailedError {
	debugPrintln("		+ Expand expandable web element " + getElementInfo());
	performAction(true);
}

/**
 * Return information about the expandable element to be displaced in messages.
 *
 * @return Information about the expandable element to be displaced in messages as {@link String}.
 */
protected String getElementInfo() {
	return this.element.toString();
}

/**
 * Return the expandable attribute.
 * <p>
 * If the web element has no specific attribute for the expansion status, then
 * subclass must ignore this method and override both {@link #isExpandable()}
 * and {@link #isExpanded()}.
 * </p>
 * @return The attribute value as a {@link String}.
 */
protected String getExpandableAttribute() {
	return this.element.getAttribute("aria-expanded");
}

/**
 * Return the expansion element.
 *
 * @param expansionLocator The locator of the expansion element as {@link By}.
 *
 * @return The expansion element as {@link BrowserElement}.
 */
protected BrowserElement getExpansionElement(final By expansionLocator) {
	if(expansionLocator == null) return this.element;

	if(isRelativeLocator(expansionLocator)) return this.element.waitForElement(expansionLocator);

	return this.browser.waitForElement(expansionLocator, timeout());
}

/**
 * Returns whether the current wrapped web element is expandable or not.
 * <p>
 * Subclass must override this method if the web element has no specific
 * expandable attribute.
 * </p>
 * @return <code>true</code> if the current node is expanda, <code>false>/code>
 * otherwise.
 * @throws ScenarioFailedError If the wrapped web element does not have
 * the <code>aria-expanded</code> attribute.
 */
@Override
public boolean isExpandable() throws ScenarioFailedError {
	return getExpandableAttribute() != null;
}

/**
 * Returns whether the current wrapped web element is expanded or not.
 * <p>
 * Subclass must override this method if the web element has no specific
 * expandable attribute.
 * </p>
 * @return <code>true</code> if the current node is expanded, <code>false>/code>
 * otherwise.
 * @throws ScenarioFailedError If the wrapped web element does not have
 * the <code>aria-expanded</code> attribute.
 */
@Override
public boolean isExpanded() throws ScenarioFailedError {
	String expandAttribute = getExpandableAttribute();
	if (expandAttribute == null) {
		throw new ScenarioFailedError("Web element " + getElementInfo() + " has no 'aria-expanded' attribute");
	}
	return "true".equals(expandAttribute);
}

private void performAction(final boolean expand) throws ScenarioFailedError {
	// Do nothing if it's already in the desired expansion state
	if (isExpanded() == expand) {
		return;
	}
	// Perform action
	clickOnExpansionElement(expand);
	// Check the expansion state and raise an error if the expansion is not in the desired state.
	long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
	while (isExpanded() != expand) {
		if (System.currentTimeMillis() > timeoutMillis) {
			StringBuilder messageBuilder = new StringBuilder("Cannot ")
				.append(expand ? "expand" : "collapse")
				.append(" web element: ")
				.append(getElementInfo());
			if (this.element != this.expansionElement) { // != is intentional
				messageBuilder.append(" using expand element: ").append(this.expansionElement);
			}
			throw new WaitElementTimeoutError(messageBuilder.toString());
		}
	}
}

/**
 * Expand the current web element.
 * <p>
 * If the web element is already expanded, then nothing happens.
 * </p>
 * @throws ScenarioFailedError If the wrapped web element does not have
 * the <code>aria-expanded</code> attribute.
 */
@Override
public final void toggle() throws ScenarioFailedError {
	debugPrintln("		+ Toggle expandable web element " + getElementInfo());
	if (isExpanded()) {
		collapse();
	} else {
		expand();
	}
}
}
