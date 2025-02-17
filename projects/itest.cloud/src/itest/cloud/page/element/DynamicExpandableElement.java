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

import static itest.cloud.scenario.ScenarioUtil.debugPrintln;

import org.openqa.selenium.By;

import itest.cloud.page.Page;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * Abstract class to handle an expandable web element in a web page where the web element is only
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
public abstract class DynamicExpandableElement extends ExpandableElement {

	/**
	 * The locator of the wrapped expandable web element.
	 */
	protected final By locator;

public DynamicExpandableElement(final ElementWrapper parent, final By locator, final BrowserElement expansionElement) {
	super(parent);
	this.locator = locator;
	this.expansionElement = expansionElement;
}

public DynamicExpandableElement(final ElementWrapper parent, final By locator, final By expansionLocator) {
	this(parent, locator, (BrowserElement) null /*expansionElement*/);
	this.expansionElement = this.page.waitForElement(expansionLocator);
}

public DynamicExpandableElement(final Page page, final By locator, final BrowserElement expansionElement) {
	super(page);
	this.locator = locator;
	this.expansionElement = expansionElement;
}

public DynamicExpandableElement(final Page page, final By locator, final By expansionLocator) {
	this(page, locator, (BrowserElement) null /*expansionElement*/);
	this.expansionElement = this.page.waitForElement(expansionLocator);
}

@Override
public void expand() throws ScenarioFailedError {
	debugPrintln("		+ Expand expandable web element " + getElementInfo());
	performAction(true /*expand*/);
	findElement(true /*fail*/);
	waitForLoadingEnd();
}

private void findElement(final boolean fail) {
	if(this.parent != null) {
		this.element = this.parent.waitForElement(this.locator, (fail ? timeout() : tinyTimeout()), fail);
	}
	else {
		this.element = this.page.waitForElement(this.locator, (fail ? timeout() : tinyTimeout()), fail);
	}
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
	findElement(false /*fail*/);
	return (this.element != null);
}
}
