/*********************************************************************
 * Copyright (c) 2024, 2025 IBM Corporation and others.
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

import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioConstants.BACK_BUTTON_LOCATOR;
import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioConstants.SEARCH_BAR_LOCATOR;
import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioUtil.TEAM_CONTENT_BUTTON_LOCATOR;
import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioUtil.getListItemLocator;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;
import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents the Boards Page which is either opened via the Bottom Navigation Menu or opens automatically when the application is launched.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #back()}: Navigate back to the previously opened page.</li>
 * <li>{@link #getName()}: Return the name of the folder.</li>
 * <li>{@link #openAsset(String, Class)}: Open a given asset in the folder.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Returns a pattern matching the expected title for the current web page.</li>
 * </ul>
 * </p>
 */
public class FolderPage extends CaMobilePageWithBottomTabList implements AssetPage {

	public static final By FOLDER_SCREEN_ELEMENT_LOCATOR = AppiumBy.accessibilityId("folder-screen");

public FolderPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
	this.data = new String [] {getTitle()};
}

public FolderPage(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

/**
 * Navigate back to the previously opened page.
 *
 * @return The previously opened page as a {@link CaMobilePage}.
 */
@Override
public CaMobilePage back() {
	// Click the Back Button.
	click(BACK_BUTTON_LOCATOR);
	// Clicking the Back Button would open the Content Page if the currently opened is a top level folder or
	// a different Folder Page otherwise. Therefore, figure out what type of page has opened.
	// Having the Team Content Button in the currently opened page implies that the particular page is the Content Page.
	// Otherwise, having the Folder Screen Element in the currently opened page implies that the particular page is a Folder Page.
	final BrowserElement[] pageIndicationElements = waitForMultipleElements(TEAM_CONTENT_BUTTON_LOCATOR, FOLDER_SCREEN_ELEMENT_LOCATOR);

	if(pageIndicationElements[0] != null) {
		// If reached here, it implies that the currently opened is the Content Page.
		return getPageUsingBrowser(getConfig(), getUser(), ContentNavigationPage.class);
	}
	// If reached here, it implies that the currently opened is a Folder Page.
	return getPageUsingBrowser(getConfig(), getUser(), FolderPage.class);
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote(getName()));
}

/**
 * Return the name of the folder.
 *
 * @return The name of the folder as {@link String}.
 */
@Override
public String getName() {
	return this.data[0];
}

/**
 * Open a given asset in the folder.
 *
 * @param name The name of the asset as {@link String}.
 * @param openedPageClass The class associated with the opened page as a {@link CaMobilePage}.
 *
 * @return The web page opened after clicking on the link as a {@link CaMobilePage}.
 */
public <P extends CaMobilePage> P openAsset(final String name, final Class<P> openedPageClass) {
	search(name);
	return openMobilePageUsingLink(waitForElement(getListItemLocator(name)), openedPageClass, name);
}

private void search(final String text) {
	typeText(SEARCH_BAR_LOCATOR, text);
}
}