/*********************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
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

import com.ibm.itest.cloud.common.pages.Page;
import com.ibm.itest.cloud.common.scenario.errors.ScenarioFailedError;

/**
 * Abstract class to handle a dropdown list web element in a web page where the web element is only
 * made available in the page after clicking on the expansion element.
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
public class DynamicDropdownlistElement extends DropdownlistElement {

	/**
	 * The locator of the wrapped expandable web element.
	 */
	protected final By locator;

public DynamicDropdownlistElement(final ElementWrapper parent, final By locator, final By expansionLocator, final By selectionLocator) {
	this(parent, locator, (BrowserElement) null /*expansionElement*/, selectionLocator);
	this.expansionElement = parent.getElement().waitForElement(expansionLocator);
}

public DynamicDropdownlistElement(final ElementWrapper parent, final By locator, final BrowserElement expansionElement, final By selectionLocator) {
	super(parent);
	this.locator = locator;
	this.expansionElement = expansionElement;
	this.selectionLocator = selectionLocator;
}

public DynamicDropdownlistElement(final Page page, final By locator, final By expansionLocator, final By selectionLocator) {
	this(page, locator, (BrowserElement) null /*expansionElement*/, selectionLocator);
	this.expansionElement = waitForElement(expansionLocator);
}

public DynamicDropdownlistElement(final Page page, final By locator, final BrowserElement expansionElement, final By selectionLocator) {
	super(page);
	this.locator = locator;
	this.expansionElement = expansionElement;
	this.selectionLocator = selectionLocator;
}

@Override
public void collapse() throws ScenarioFailedError {
	super.collapse();
	this.element = null;
}

/**
 * Find the the wrapped expandable web element.
 *
 * @param fail Specify whether to fail if none of the locators is find before timeout.
 */
protected void findElement(final boolean fail) {
	if(this.parent != null) {
		this.element = this.browser.waitForElement(this.parent.getElement(), this.locator, (fail ? timeout() : tinyTimeout()), fail);
	}
	else {
		this.element = waitForElement(this.locator, (fail ? timeout() : tinyTimeout()), fail);
	}
}

@Override
protected String getElementInfo() {
	return this.locator.toString();
}

@Override
public boolean isExpanded() throws ScenarioFailedError {
	findElement(false /*fail*/);
	return (this.element != null) && super.isExpanded();
}
}
