/*********************************************************************
 * Copyright (c) 2019, 2024 IBM Corporation and others.
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

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.IbmPage;
import itest.cloud.page.Page;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This class represents and manages the side menu element of a web page {@link IbmPage}.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #isExpanded()}: Returns whether the current wrapped web element is expanded or not.</li>
 * <li>{@link #isMenuItemAvailable(String, boolean)}: Specifies whether the given option is available in the menu tree.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class IbmSideMenuElement extends IbmExpandableElement {

	private IbmSideMenuTreeElement sideMenuTreeElement;

public IbmSideMenuElement(final Page page) {
	super(page, By.id("dap-side-menu"), By.id("sidemenu-toggle-button"));
	this.sideMenuTreeElement = new IbmSideMenuTreeElement(this);
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
	String classAttribute = this.element.getAttribute("class");

	return this.element.isDisplayed() && (classAttribute != null) && classAttribute.contains("open");
}

/**
 * Specifies whether a menu item is available at a given path.
 *
 * @param path The path to the menu item to be checked as {@link String}.
 * @param fail Specifies whether to fail if a matching menu item is not found.
 *
 * @return <code>true</code> if the given option is available in the menu tree or
 * <code>false</code> otherwise.
 */
public boolean isMenuItemAvailable(final String path, final boolean fail) {
	return this.sideMenuTreeElement.isMenuItemAvailable(path, fail);
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

@Override
protected By getTitleElementLocator() {
	return null;
}
}