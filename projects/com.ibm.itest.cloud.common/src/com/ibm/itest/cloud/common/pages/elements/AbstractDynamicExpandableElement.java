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

import org.openqa.selenium.By;

import com.ibm.itest.cloud.common.tests.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.tests.web.WebPage;

/**
 * Abstract class to handle an expandable web element in a web page where the web element is only
 * made available in the page after clicking on the expansion element.
 * <p>
 * By default the expansion mechanism of the web element is managed by its
 * <code>aria-expanded</code> attribute. If the wrapped web element does
 * not have this attribute, then {@link #isExpandable()} and {@link #isExpanded()}
 * methods must be overridden in the corresponding subclass.
 * </p><p>
 * Public API for this class is available in {@link ExpandableElement} interface.
 * </p><p>
 * Following internal API method are defined in this abstract class and
 * might be overridden by subclasses:
 * <ul>
 * <li>{@link #getExpandableAttribute()}:  Return the expandable attribute.</li>
 * </ul>
 * </p>
 */
public abstract class AbstractDynamicExpandableElement extends AbstractExpandableElement {

	/**
	 * The locator of the wrapped expandable web element.
	 */
	protected final By locator;

public AbstractDynamicExpandableElement(final WebElementWrapper parent, final By locator, final By expansionLocator) {
	this(parent, locator, (WebBrowserElement) null /*expansionElement*/);
	this.expansionElement = parent.element.waitForElement(expansionLocator);
}

public AbstractDynamicExpandableElement(final WebElementWrapper parent, final By locator, final WebBrowserElement expansionElement) {
	super(parent);
	this.locator = locator;
	this.expansionElement = expansionElement;
}

public AbstractDynamicExpandableElement(final WebPage page, final By locator, final By expansionLocator) {
	this(page, locator, (WebBrowserElement) null /*expansionElement*/);
	this.expansionElement = waitForElement(expansionLocator);
}

public AbstractDynamicExpandableElement(final WebPage page, final By locator, final WebBrowserElement expansionElement) {
	super(page);
	this.locator = locator;
	this.expansionElement = expansionElement;
}

@Override
public void expand() throws ScenarioFailedError {
	super.expand();
	this.element = waitForElement(this.locator);
}

@Override
protected String getElementInfo() {
	return this.locator.toString();
}

@Override
protected String getExpandableAttribute() {
	throw new ScenarioFailedError("This method should never be called");
}

@Override
public boolean isExpandable() throws ScenarioFailedError {
	return true;
}

@Override
public boolean isExpanded() throws ScenarioFailedError {
	return waitForElement(this.locator, false /*fail*/, tinyTimeout()) != null;
}
}
