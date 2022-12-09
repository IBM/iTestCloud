/*********************************************************************
 * Copyright (c) 2019, 2022 IBM Corporation and others.
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

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.*;

import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import com.ibm.itest.cloud.common.tests.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.tests.scenario.errors.WaitElementTimeoutError;
import com.ibm.itest.cloud.common.tests.web.WebPage;

/**
 * This class represents a generic list of tabs and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #openTab(Pattern, Class, String[])}: Open a specific tab.</li>
 * <li>{@link #openTab(Pattern, Class, boolean, String[])}: Open a specific tab.</li>
 * <li>{@link #openTab(String, Class, String[])}: Open a specific tab.</li>
 * <li>{@link #openTab(String, Class, boolean, String[])}: Open a specific tab.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #clickOnOpenLinkElement(Pattern)}: Click on the open link element in order to open the tab element.</li>
 * <li>{@link #getOpenLinkElement(Pattern)}: Return the link element to be used to open a given tab.</li>
 * <li>{@link #isOpen(Pattern)}: Specifies whether a given tab is the currently opened tab.</li>
 * </ul>
 * </p>
 */
public abstract class WebTabListElement extends WebElementWrapper {

public WebTabListElement(final WebElementWrapper parent, final By findBy) {
	super(parent, findBy);
}

public WebTabListElement(final WebElementWrapper parent, final WebBrowserElement element) {
	super(parent, element);
}

public WebTabListElement(final WebPage page, final By findBy) {
	super(page, findBy);
}

public WebTabListElement(final WebPage page, final WebBrowserElement element) {
	super(page, element);
}

/**
 * Click on the open link element in order to open the tab element.
 * <p>
 * The default behavior is to click on the open link element after having made it
 * visible. If this default behavior is not true, then a peculiar subclass might want
 * to override this method and perform different actions while expanding or collapsing
 * the current object.
 * </p>
 * @param pattern A pattern matching the name or label of the tab as {@link Pattern}.
 */
protected void clickOnOpenLinkElement(final Pattern pattern) {
	WebBrowserElement openLinkElement = getOpenLinkElement(pattern);
	// Click on the open link element.
	// At times, the open link element may be obscured by another element and therefore, not be clickable.
	// As a result, a WebDriverException can occur.
	try {
		openLinkElement.click();
	}
	catch (WebDriverException e) {
		// If the linkElement.click() method causes a WebDriverException, use JavaScript to perform the
		// click on the open link element in this case.
		debugPrintln("Clicking on link element (WebBrowserElement.click()) caused following error. Therefore, try JavaScript (WebBrowserElement.clickViaJavaScript()) to perform click as a workaround.");
		debugPrintln(e.toString());
		debugPrintStackTrace(e.getStackTrace(), 1 /*tabs*/);
		openLinkElement.clickViaJavaScript();
	}
}

/**
 * Return the link element to be used to open a given tab.
 *
 * @param pattern A pattern matching the name or label of the tab as {@link Pattern}.
 *
 * @return The link element to be used to open the given tab as {@link WebBrowserElement}.
 */
protected abstract WebBrowserElement getOpenLinkElement(final Pattern pattern);

/**
 * Specifies whether a given tab is the currently opened tab.
 *
 * @param pattern A pattern matching the name or label of the tab as {@link Pattern}.
 *
 * @return <code>true</code> if the given is the currently opened tab or
 * <code>false</code> otherwise.
 */
protected abstract boolean isOpen(final Pattern pattern);

/**
 * Open a specific tab.
 *
 * @param pattern A pattern matching the name or label of the tab as {@link Pattern}.
 * @param tabClass A class representing the particular tab.
 * @param force Specifies whether to reopen the given tab if it is the currently opened tab.
 * If <code>true</code> is provided as the value of this parameter, the given tab will
 * be reopened if it is the currently opened tab.
 * If <code>false</code> is provided as the value of this parameter, no action will be
 * performed if the given tab is the currently opened tab.
 * @param data Additional information to store in the tab when it is opened.
 *
 * @return The opened tab as a subclass of {@link WebTab}
 */
public <T extends WebTab> T openTab(final Pattern pattern, final Class<T> tabClass, final boolean force, final String... data) {
	if (DEBUG) debugPrintln("		+ Open tab '"+ tabClass.getSimpleName() + "' from '" + getClass().getSimpleName() + "'");

	// Check if the given is the currently opened tab.
	if(!force && isOpen(pattern)) {
		debugPrintln("		  -> No need to open '" + tabClass.getSimpleName() + "' because it is already the currently opened tab.");
	}
	else {
		clickOnOpenLinkElement(pattern);
	}

	T tab;
	try {
		if((data == null) || (data.length == 0)) {
			if(this.parent != null) {
				tab = tabClass.getConstructor(WebElementWrapper.class).newInstance(getParent());
			}
			else {
				tab = tabClass.getConstructor(WebPage.class).newInstance(getPage());
			}
		}
		else {
			if(this.parent != null) {
				tab = tabClass.getConstructor(WebElementWrapper.class, String[].class).newInstance(getParent(), data);
			}
			else {
				tab = tabClass.getConstructor(WebPage.class, String[].class).newInstance(getPage(), data);
			}
		}
	}
	catch (WebDriverException e) {
		throw e;
	}
	catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
		println("Exception cause: " + e.getCause());
		throw new ScenarioFailedError(e);
	}
	catch (Throwable e) {
		println("Exception cause: " + e.getCause());
		throw new WaitElementTimeoutError(e);
	}

	// Wait for loading of tab to finish.
	tab.waitForLoadingEnd();
	return tab;
}

/**
 * Open a specific tab.
 * <p>
 * No action will be performed if the given is the currently opened tab.
 * </p>
 * @param pattern A pattern matching the name or label of the tab as {@link Pattern}.
 * @param tabClass A class representing the particular tab.
 * @param data Additional information to store in the tab when it is opened.
 *
 * @return The opened tab as a subclass of {@link WebTab}
 */
public <T extends WebTab> T openTab(final Pattern pattern, final Class<T> tabClass, final String... data) {
	return openTab(pattern, tabClass, false /*force*/, data);
}

/**
 * Open a specific tab.
 *
 * @param name The name of the tab.
 * @param tabClass A class representing the particular tab.
 * @param force Specifies whether to reopen the given tab if it is the currently opened tab.
 * If <code>true</code> is provided as the value of this parameter, the given tab will
 * be reopened if it is the currently opened tab.
 * If <code>false</code> is provided as the value of this parameter, no action will be
 * performed if the given tab is the currently opened tab.
 * @param data Additional information to store in the tab when it is opened.
 *
 * @return The opened tab as a subclass of {@link WebTab}
 */
public <T extends WebTab> T openTab(final String name, final Class<T> tabClass, final boolean force, final String... data) {
	return openTab(Pattern.compile(Pattern.quote(name)), tabClass, force, data);
}

/**
 * Open a specific tab.
 * <p>
 * No action will be performed if the given is the currently opened tab.
 * </p>
 * @param name The name of the tab.
 * @param tabClass A class representing the particular tab.
 * @param data Additional information to store in the tab when it is opened.
 *
 * @return The opened tab as a subclass of {@link WebTab}
 */
public <T extends WebTab> T openTab(final String name, final Class<T> tabClass, final String... data) {
	return openTab(name, tabClass, false /*force*/, data);
}
}
