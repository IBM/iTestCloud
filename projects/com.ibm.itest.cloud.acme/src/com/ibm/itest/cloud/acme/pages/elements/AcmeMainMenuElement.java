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
import com.ibm.itest.cloud.common.pages.Page;
import com.ibm.itest.cloud.common.scenario.errors.ScenarioFailedError;

/**
 * This class represents and manages the main menu element of a web page
 * {@link AcmeAbstractWebPage}.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #collapse()}: Collapse the current web element.</li>
 * <li>{@link #expand()}: Expand the current web element.</li>
 * <li>{@link #isExpandable()}: Returns whether the current wrapped web element is expandable or not.</li>
 * <li>{@link #isExpanded()}: Returns whether the current wrapped web element is expanded or not.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #select(String, Class)}: Open a specific web page by selecting an appropriate item at a given path.</li>
 * </ul>
 * </p>
 */
public class AcmeMainMenuElement extends AcmeMainMenuNodeElement {

public AcmeMainMenuElement(final Page page) {
	super(page, By.xpath("//*[@class='dap-section-link-container']"));
}

@Override
public void collapse() throws ScenarioFailedError {
	// Do nothing.
}

@Override
public void expand() throws ScenarioFailedError {
	// Do nothing.
}

@Override
public boolean isExpandable() throws ScenarioFailedError {
	return true;
}

@Override
public boolean isExpanded() throws ScenarioFailedError {
	return true;
}

/**
 * Open a specific web page by selecting an appropriate item at a given path.
 * <p>
 * If the path contains multiple sections, they must be separated by the character '/'.
 * The following are some example paths.
 * <ul>
 * <li>Catalog</li>
 * <li>Data Services/Connections</li>
 * </ul>
 * </p>
 *
 * @param path The path to the menu item to be clicked as {@link String}.
 * @param pageClass A class representing the web page to be opened.
 *
 * @return The opened web page as a {@link AcmeAbstractWebPage}.
 */
protected <T extends AcmeAbstractWebPage> T select(final String path, final Class<T> pageClass) {
	if (DEBUG) debugPrintln("		+ Select item at path '" + path + "' from main menu");

	AcmeMainMenuNodeElement childElement = getChildElement(path, true /*fail*/);
	return openPageUsingLink(childElement.getLabelElement(), pageClass);
}
}