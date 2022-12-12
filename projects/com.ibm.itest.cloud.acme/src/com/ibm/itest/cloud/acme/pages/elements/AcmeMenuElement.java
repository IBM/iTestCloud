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

import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.DEBUG;
import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.debugPrintln;

import org.openqa.selenium.By;

import com.ibm.itest.cloud.acme.pages.AcmeAbstractWebPage;
import com.ibm.itest.cloud.common.pages.WebPage;
import com.ibm.itest.cloud.common.pages.elements.WebBrowserElement;
import com.ibm.itest.cloud.common.scenario.errors.ScenarioFailedError;

/**
 * This class represents a generic menu element and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #isExpandable()}:
 * Returns whether the current wrapped web element is expandable or not.</li>
 * <li>{@link #isExpanded()}: Returns whether the current wrapped web element is expanded or not.</li>
 * <li>{@link #select(String)}: Select a specific item (option) from the menu element.</li>
 * <li>{@link #select(String, Class)}:
 * Select a specific item (option) from the menu element by opening a {@link AcmeAbstractWebPage}.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpandableAttribute()}: Return the expandable attribute.</li>
 * </ul>
 * </p>
 */
public class AcmeMenuElement extends AcmeAbstractExpandableElement {

public AcmeMenuElement(final WebPage page, final By findBy, final By expansionLocator) {
	super(page, findBy, expansionLocator);
}

public AcmeMenuElement(final WebPage page, final WebBrowserElement webElement, final By expansionLocator) {
	super(page, webElement, expansionLocator);
}

@Override
protected String getExpandableAttribute() {
	throw new ScenarioFailedError("This method should never be called.");
}

@Override
public boolean isExpandable() throws ScenarioFailedError {
	return true;
}

@Override
public boolean isExpanded() throws ScenarioFailedError {
	WebBrowserElement sidebarElement =
		this.browser.waitForElement(this.element, By.xpath(".//ul[contains(@class,'dropdown-menu')]"), true /*fail*/, timeout(), false /*displayed*/, true /*single*/);
	return sidebarElement.isDisplayed() && sidebarElement.getAttributeValue("class").contains("open");
}

/**
 * Select a specific item (option) from the menu element.
 *
 * @param item The item to select from the menu.
 */
public void select(final String item) {
	select(item, null /* pageClass */);
}

public <T extends AcmeAbstractWebPage> T select(final String item, final Class<T> pageClass) {
	return select(item, pageClass, false /*force*/);
}

/**
 * Select a specific item (option) from the menu element by opening
 * a {@link AcmeAbstractWebPage}.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param pageClass A class representing the web page opened
 * after clicking on the particular menu item.
 * @return The opened confirmation dialog as a subclass of {@link AcmeAbstractWebPage}.
 */
@SuppressWarnings("unchecked")
public <T extends AcmeAbstractWebPage> T select(final String item, final Class<T> pageClass, final boolean force) {
	if (DEBUG) debugPrintln("		+ Select '"+ item + "' from the drop down list");

	WebBrowserElement itemElement =
		this.browser.waitForElement(this.element,
			By.xpath(".//li[contains(@class,'dropdown-link') and .//a[text()='" + item + "']]"), true /*fail*/, timeout(), false /*displayed*/, true /*single*/);

	if(!force && itemElement.getAttributeValue("class").contains("selected")) {
		debugPrintln("		  -> No need to select '" + item + "' because it is already the currently selected item.");
		return (T) getPage();
	}

	expand();

	WebBrowserElement anchorElement = itemElement.waitForElement(By.xpath(".//a"));

	if(pageClass == null){
		anchorElement.click();
		return null;
	}

	return openPageUsingLink(anchorElement, pageClass);
}
}
