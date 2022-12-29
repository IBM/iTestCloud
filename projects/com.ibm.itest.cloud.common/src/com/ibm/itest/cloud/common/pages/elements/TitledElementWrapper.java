/*********************************************************************
 * Copyright (c) 2016, 2022 IBM Corporation and others.
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

import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.debugPrintln;
import static com.ibm.itest.cloud.common.utils.ObjectUtils.matches;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import com.ibm.itest.cloud.common.pages.Page;
import com.ibm.itest.cloud.common.scenario.errors.IncorrectTitleError;
import com.ibm.itest.cloud.common.scenario.errors.ScenarioFailedError;

/**
 * This class wraps a titled web element and add some actions and functionalities
 * that anyone can use. It also add some specific operations only accessible to
 * the class hierarchy.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getTitle()}: Return the title of the element.</li>
 * <li>{@link #waitForLoadingEnd()}: Wait for loading of the tab to complete.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitle(boolean)}: Return the title of the element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * <li>{@link #isTitleExpected()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * </p>
 */
public abstract class TitledElementWrapper extends ElementWrapper {

	protected String[] data;

public TitledElementWrapper(final ElementWrapper parent, final By findBy) {
	this(parent, findBy, (String[]) null);
}

public TitledElementWrapper(final ElementWrapper parent, final By findBy, final String... data) {
	super(parent, findBy);
	this.data = data;
}

public TitledElementWrapper(final Page page) {
	this(page, (String[]) null);
}

public TitledElementWrapper(final Page page, final By findBy) {
	this(page, findBy, (String[]) null);
}

public TitledElementWrapper(final Page page, final By findBy, final String... data) {
	super(page, findBy);
	this.data = data;
}

public TitledElementWrapper(final Page page, final String... data) {
	super(page);
	this.data = data;
}

public TitledElementWrapper(final Page page, final BrowserElement element) {
	this(page, element, (String[]) null);
}

public TitledElementWrapper(final Page page, final BrowserElement element, final String... data) {
	super(page, element);
	this.data = data;
}

/**
 * Return a pattern matching the expected title for the current element.
 *
 * @return The title of the element as a {@link String}
 */
protected abstract Pattern getExpectedTitle();

/**
 * Return the title of the element.
 *
 * @return The title of the element as {@link String} or <code>null</code>
 * if a title should not be expected in this element.
 */
public String getTitle() {
	return getTitle(false /*fail*/);
}

/**
 * Return the title of the element.
 *
 * @param fail Specifies whether to fail if a title is not found.
 *
 * @return The title of the element as {@link String}.
 */
protected String getTitle(final boolean fail) {
	if(!fail && !isTitleExpected()) {
		return null;
	}
	return this.element.waitForElement(getTitleElementLocator()).getText();
}

/**
 * Return the locator for the title element of the current element.
 *
 * @return The title element locator as a {@link By}.
 */
protected abstract By getTitleElementLocator();

/**
 * Specifies if a title is expected for the element.
 *
 * @return <code>true</code> if a title is expected or <code>false</code> otherwise.
 */
protected boolean isTitleExpected() {
	return (getExpectedTitle() != null) && (getTitleElementLocator() != null);
}

/**
 * Returns whether the element title matches the expected one.
 *
 * @return <code>true</code> if the title is part of the expected element title
 * or vice-versa, <code>false</code> otherwise.
 */
public boolean matchTitle() {
	return matches(getExpectedTitle(), getTitle());
}

/**
 * Wait for the expected title to appear.
 *
 * @throws ScenarioFailedError if the current element title does not match the expected one.
 */
protected void waitForExpectedTitle() {
	if (isTitleExpected()) {
		long timeoutMillis = openTimeout() * 1000 + System.currentTimeMillis();
		if (!matchTitle()) {
			debugPrintln("		+ Wait for expected title '"+getExpectedTitle()+"' (current is '"+getTitle()+"')");
			while (!matchTitle()) {
				if (System.currentTimeMillis() > timeoutMillis) {
					throw new IncorrectTitleError("Current element title '" + getTitle() + "' does not match the expected one: '" + getExpectedTitle() + "' before timeout '" + openTimeout() + "' seconds");
				}
			}
		}
	}
}

/**
 * Wait for loading of the element to complete.
 */
@Override
public void waitForLoadingEnd() {
	super.waitForLoadingEnd();

	// Check the title.
	waitForExpectedTitle();
}
}