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
package com.ibm.itest.cloud.acme.pages.elements;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import com.ibm.itest.cloud.common.pages.WebPage;
import com.ibm.itest.cloud.common.pages.elements.WebBrowserElement;
import com.ibm.itest.cloud.common.pages.elements.WebElementWrapper;
import com.ibm.itest.cloud.common.tests.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.tests.scenario.errors.WaitElementTimeoutError;
import com.ibm.itest.cloud.common.tests.web.*;

/**
 * This class represents a generic list of tabs and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #openTab(String, Class, String[])}: Open a specific tab.</li>
 * <li>{@link #openTab(String, Class, boolean, String[])}: Open a specific tab.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #clickOnOpenLinkElement(String)}: Click on the open link element in order to open the tab element.</li>
 * <li>{@link #getOpenLinkElement(String)}: Return the link element to be used to open a given tab.</li>
 * <li>{@link #isOpen(String)}: Specifies whether a given tab is the currently opened tab.</li>
 * </ul>
 * </p>
 */
public class AcmeTabListElement extends AcmeWebElementWrapper {

private static By getTabListElementLocator(final boolean isRelative) {
	return By.xpath((isRelative ? "." : EMPTY_STRING) + "//*[contains(@class,'subHeader_') or contains(@class,'tabs__list') or (@class='bx--tabs')]");
}

public AcmeTabListElement(final WebElementWrapper parent) {
	super(parent, getTabListElementLocator(true /*isRelative*/));
}

public AcmeTabListElement(final WebPage page) {
	this(page, getTabListElementLocator(false /*isRelative*/));
}

public AcmeTabListElement(final WebPage page, final By findBy) {
	super(page, findBy);
}

public AcmeTabListElement(final WebPage page, final WebBrowserElement element) {
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
 * @param name The name of the tab.
 */
protected void clickOnOpenLinkElement(final String name) {
	getOpenLinkElement(name).makeVisible().click();
}

/**
 * Return the link element to be used to open a given tab.
 *
 * @param tabName The name or label of the tab.
 *
 * @return The link element to be used to open the given tab as {@link WebBrowserElement}.
 */
protected WebBrowserElement getOpenLinkElement(final String tabName) {
	return this.element.waitForElement(By.xpath(".//*[(contains(@class,'subHeaderLink') or contains(@class,'tabs__tab') or contains(@class,'tabs__nav-link')) and contains(text(),'" + tabName + "')]"));
}

/**
 * Specifies whether a given tab is the currently opened tab.
 *
 * @param tabName The name or label of the tab.
 *
 * @return <code>true</code> if the given is the currently opened tab or
 * <code>false</code> otherwise.
 */
protected boolean isOpen(final String tabName) {
	WebBrowserElement tabOpenLinkElement = getOpenLinkElement(tabName);
	String classValue = tabOpenLinkElement.getAttribute("class");
	String ariaSelectedValue = tabOpenLinkElement.getAttribute("aria-selected");
	return ((classValue != null) && (classValue.toLowerCase().contains("selected") || classValue.toLowerCase().contains("active"))) ||
		   ((ariaSelectedValue != null) && Boolean.parseBoolean(ariaSelectedValue));
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
 * @return The opened tab as a subclass of {@link AcmeTab}
 */
public <T extends AcmeTab> T openTab(final String name, final Class<T> tabClass, final boolean force, final String... data) {
	if (DEBUG) debugPrintln("		+ Open tab '"+ tabClass.getSimpleName() + "' from '" + getClass().getSimpleName() + "'");

	// Check if the given is the currently opened tab.
	if(!force && isOpen(name)) {
		debugPrintln("		  -> No need to open '" + tabClass.getSimpleName() + "' because it is already the currently opened tab.");
	}
	else {
		clickOnOpenLinkElement(name);
	}

	T tab;
	try {
		if((data == null) || (data.length == 0)) {
			tab = tabClass.getConstructor(WebPage.class).newInstance(getPage());
		}
		else {
			tab = tabClass.getConstructor(WebPage.class, String[].class).newInstance(getPage(), data);
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
 * @param name The name of the tab.
 * @param tabClass A class representing the particular tab.
 * @param data Additional information to store in the tab when it is opened.
 *
 * @return The opened tab as a subclass of {@link AcmeTab}
 */
public <T extends AcmeTab> T openTab(final String name, final Class<T> tabClass, final String... data) {
	return openTab(name, tabClass, false /*force*/, data);
}
}
