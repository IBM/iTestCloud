/*********************************************************************
 * Copyright (c) 2018, 2024 IBM Corporation and others.
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
package itest.cloud.ibm.page.element;

import static itest.cloud.scenario.ScenarioUtil.DEBUG;
import static itest.cloud.scenario.ScenarioUtil.debugPrintln;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.IbmPage;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.ScenarioFailedError;

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
 * Select a specific item (option) from the menu element by opening a {@link IbmPage}.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpandableAttribute()}: Return the expandable attribute.</li>
 * </ul>
 * </p>
 */
public abstract class IbmMenuElement extends IbmExpandableElement {

public IbmMenuElement(final Page page, final By findBy, final By expansionLocator) {
	super(page, findBy, expansionLocator);
}

public IbmMenuElement(final Page page, final BrowserElement webElement, final By expansionLocator) {
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
	BrowserElement sidebarElement =
		waitForElement(By.xpath(".//ul[contains(@class,'dropdown-menu')]"), timeout(), true /*fail*/, false /*displayed*/);
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

public <T extends IbmPage> T select(final String item, final Class<T> pageClass) {
	return select(item, pageClass, false /*force*/);
}

/**
 * Select a specific item (option) from the menu element by opening
 * a {@link IbmPage}.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param pageClass A class representing the web page opened
 * after clicking on the particular menu item.
 * @return The opened confirmation dialog as a subclass of {@link IbmPage}.
 */
@SuppressWarnings("unchecked")
public <T extends IbmPage> T select(final String item, final Class<T> pageClass, final boolean force) {
	if (DEBUG) debugPrintln("		+ Select '"+ item + "' from the drop down list");

	BrowserElement itemElement =
		this.browser.waitForElement(this.element,
			By.xpath(".//a[text()='" + item + "']"), timeout(), true /*fail*/, false /*displayed*/, true /*single*/);

	if(!force && itemElement.getAttributeValue("class").contains("selected")) {
		debugPrintln("		  -> No need to select '" + item + "' because it is already the currently selected item.");
		return (T) getPage();
	}

	expand();

	BrowserElement anchorElement = itemElement.waitForElement(By.xpath(".//a"));

	if(pageClass == null){
		anchorElement.click();
		return null;
	}

	return openPageUsingLink(anchorElement, pageClass);
}
}
