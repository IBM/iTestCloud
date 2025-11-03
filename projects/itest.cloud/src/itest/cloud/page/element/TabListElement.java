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
package itest.cloud.page.element;

import static itest.cloud.scenario.ScenarioUtil.*;

import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import itest.cloud.page.Page;
import itest.cloud.scenario.error.ScenarioFailedError;
import itest.cloud.scenario.error.WaitElementTimeoutError;

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
 * Following private features are also defined or specialized by this element:
 * <ul>
 * <li>{@link #clickOnOpenLinkElement(Pattern)}: Click on the open link element in order to open the tab element.</li>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getLabelOfOpenLinkElement(BrowserElement)}: Return the label of a given open link element.</li>
 * <li>{@link #getOpenLinkElement(Pattern)}: Return the link element to be used to open a given tab.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * <li>{@link #isOpen(Pattern)}: Specifies whether a given tab is the currently opened tab.</li>
 * </ul>
 * </p>
 */
public abstract class TabListElement extends ElementWrapper {

	protected final By openLinkLocator;

public TabListElement(final ElementWrapper parent, final BrowserElement element, final By openLinkLocator) {
	super(parent, element);
	this.openLinkLocator = openLinkLocator;
}

public TabListElement(final ElementWrapper parent, final By locator, final By openLinkLocator) {
	super(parent, locator);
	this.openLinkLocator = openLinkLocator;
}

public TabListElement(final Page page, final BrowserElement element, final By openLinkLocator) {
	super(page, element);
	this.openLinkLocator = openLinkLocator;
}

public TabListElement(final Page page, final By locator, final By openLinkLocator) {
	super(page, locator);
	this.openLinkLocator = openLinkLocator;
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
	final BrowserElement openLinkElement = getOpenLinkElement(pattern);
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

@Override
protected Pattern getExpectedTitle() {
	return null;
}

/**
 * Return the label of a given open link element.
 *
 * @param openLinkElement The open link element as {@link BrowserElement}.
 *
 * @return The label of the given open link element as {@link String}.
 */
protected String getLabelOfOpenLinkElement(final BrowserElement openLinkElement) {
	return openLinkElement.getText();
}

/**
 * Return the link element to be used to open a given tab.
 *
 * @param pattern A pattern matching the name or label of the tab as {@link Pattern}.
 *
 * @return The link element to be used to open the given tab as {@link BrowserElement}.
 */
protected BrowserElement getOpenLinkElement(final Pattern pattern) {
	final List<BrowserElement> openLinkElements = waitForElements(this.openLinkLocator);

	for (BrowserElement openLinkElement : openLinkElements) {
		if(pattern.matcher(getLabelOfOpenLinkElement(openLinkElement)).matches()) return openLinkElement;
	}

	throw new ScenarioFailedError("An open link element matching '" + pattern + "' could not be found.");
}

@Override
protected By getTitleElementLocator() {
	return null;
}

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
 * @param tabClass A class representing the particular tab as a {@link TabElement}.
 * @param force Specifies whether to reopen the given tab if it is the currently opened tab.
 * If <code>true</code> is provided as the value of this parameter, the given tab will
 * be reopened if it is the currently opened tab.
 * If <code>false</code> is provided as the value of this parameter, no action will be
 * performed if the given tab is the currently opened tab.
 * @param tabData Additional information to store in the tab when it is opened.
 *
 * @return The opened tab as a subclass of {@link TabElement}
 */
public <T extends TabElement> T openTab(final Pattern pattern, final Class<T> tabClass, final boolean force, final String... tabData) {
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
		if((tabData == null) || (tabData.length == 0)) {
			if(this.parent != null) {
				tab = tabClass.getConstructor(ElementWrapper.class).newInstance(getParent());
			}
			else {
				tab = tabClass.getConstructor(Page.class).newInstance(getPage());
			}
		}
		else {
			if(this.parent != null) {
				tab = tabClass.getConstructor(ElementWrapper.class, String[].class).newInstance(getParent(), tabData);
			}
			else {
				tab = tabClass.getConstructor(Page.class, String[].class).newInstance(getPage(), tabData);
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
 * @param tabData Additional information to store in the tab when it is opened.
 *
 * @return The opened tab as a subclass of {@link TabElement}
 */
public <T extends TabElement> T openTab(final Pattern pattern, final Class<T> tabClass, final String... tabData) {
	return openTab(pattern, tabClass, false /*force*/, tabData);
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
 * @param tabData Additional information to store in the tab when it is opened.
 *
 * @return The opened tab as a subclass of {@link TabElement}
 */
public <T extends TabElement> T openTab(final String name, final Class<T> tabClass, final boolean force, final String... tabData) {
	return openTab(Pattern.compile(Pattern.quote(name)), tabClass, force, tabData);
}

/**
 * Open a specific tab.
 * <p>
 * No action will be performed if the given is the currently opened tab.
 * </p>
 * @param name The name of the tab.
 * @param tabClass A class representing the particular tab.
 * @param tabData Additional information to store in the tab when it is opened.
 *
 * @return The opened tab as a subclass of {@link TabElement}
 */
public <T extends TabElement> T openTab(final String name, final Class<T> tabClass, final String... tabData) {
	return openTab(name, tabClass, false /*force*/, tabData);
}
}
