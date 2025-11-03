/*********************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
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
package itest.cloud.ibm.page.ca.mobile;

import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioConstants.SEARCH_BAR_LOCATOR;
import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioUtil.getContentTabButtonLocator;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.entity.mobile.AssetContext;
import itest.cloud.ibm.page.element.ca.mobile.CaContentTabElement;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents the Content Navigation Page.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #openTab(AssetContext)}: Open a given tab.</li>
 * <li>{@link #search(String)}: Search a given text.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Returns a pattern matching the expected title for the current web page.</li>
 * <li>{@link #getTitleElementLocator()}: Return the title element locator.</li>
 * </ul>
 * </p>
 */
public class ContentNavigationPage extends CaMobilePageWithBottomTabList implements ContentPage {

public ContentNavigationPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

@Override
protected By getTitleElementLocator() {
	return null;
}

/**
 * Open a given tab.
 *
 * @param tab The tab to open as {@link AssetContext}.
 *
 * @return The opened tab as {@link CaContentTabElement}.
 */
public CaContentTabElement openTab(final AssetContext tab) {
	return openElementUsingLink(getContentTabButtonLocator(tab), CaContentTabElement.class, tab.getId());
}

/**
 * Search a given text.
 *
 * @param text The text to be searched as {@link String}.
 */
public void search(final String text) {
	final BrowserElement searchBarElement = waitForElement(SEARCH_BAR_LOCATOR);
	typeText(searchBarElement, text);
	// Need to place a click to trigger the filtering.
	searchBarElement.click();
}
}