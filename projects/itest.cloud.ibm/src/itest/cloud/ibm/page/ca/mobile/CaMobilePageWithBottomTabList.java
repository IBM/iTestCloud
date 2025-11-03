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

import static itest.cloud.ibm.page.ca.mobile.DashboardPage.DASHBOARD_PAGE_ELEMENT_LOCATOR;
import static itest.cloud.ibm.page.ca.mobile.FolderPage.FOLDER_SCREEN_ELEMENT_LOCATOR;
import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioUtil.TEAM_CONTENT_BUTTON_LOCATOR;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;
import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.PageBuysTimeoutError;

/**
 * This class represents a generic page with the ability to navigate to other pages via the Bottom Tab List in the Cognos Analytics Mobile application and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #openBoards()}: Open the Board Page.</li>
 * <li>{@link #openContent()}: Open the Content Page via its Bottom Navigation Menu.</li>
 * <li>{@link #waitForLoadingPageEnd()}: Wait for the page loading to be finished.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public abstract class CaMobilePageWithBottomTabList extends CaMobilePage {

	protected static final By BOARDS_TAB_OPEN_ELEMENT_LOCATOR = AppiumBy.accessibilityId("bottom-tab-Boards");

public CaMobilePageWithBottomTabList(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

public CaMobilePageWithBottomTabList(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

protected void dismissTour() {
	final BrowserElement tourElement = getTourElement();
	if(tourElement != null) {
		// Click the close button.
		click(AppiumBy.accessibilityId("Tour Close"));

		// Wait for the tour element to disappear.
		long timeoutInMillis = timeout() * 1000 + System.currentTimeMillis();

		while (getTourElement() != null) {
			if (System.currentTimeMillis() > timeoutInMillis) {
				throw new PageBuysTimeoutError("The tour element had remained in the application after suppressing it when the timeout '" + timeout() + "s' was reached.");
			}
		}
	}
}

private BrowserElement getTourElement() {
	final BrowserElement[] relatedElements = waitForMultipleElements(
		AppiumBy.accessibilityId("tooltip overlay"), BOARDS_TAB_OPEN_ELEMENT_LOCATOR);
	return (relatedElements[0] != null) ? relatedElements[0] : null;
}

/**
 * Open the Boards via its Bottom Tab List.
 *
 * @return the opened Boards Page as {@link BoardsPage}.
 */
public BoardsPage openBoards() {
	return openMobilePageUsingLink(BOARDS_TAB_OPEN_ELEMENT_LOCATOR, BoardsPage.class);
}

/**
 * Open the Content via its Bottom Tab List.
 *
 * @return the opened page as {@link ContentNavigationPage}, {@link FolderPage}, or {@link DashboardPage}.
 */
public CaMobilePage openContent() {
	// Click the Content Tab Button.
	click(AppiumBy.accessibilityId("bottom-tab-Content"));

	// Clicking the Content Tab Button would open the Content Page, a Folder Page, or a Dashboard Page depending on what page
	// was most recently open in this context. Therefore, figure out what type of page has just opened.
	// Having the Team Content Tab Button in the currently opened page implies that the particular page is the Content Page.
	// Having the Folder Screen Element in the currently opened page implies that the particular page is a Folder Page.
	// Otherwise, having the Dashboard Title Element in the page implies that the particular page is a Dashboard Page.
	final BrowserElement[] pageIndicationElements =
		waitForMultipleElements(TEAM_CONTENT_BUTTON_LOCATOR, FOLDER_SCREEN_ELEMENT_LOCATOR, DASHBOARD_PAGE_ELEMENT_LOCATOR);

	if(pageIndicationElements[0] != null) {
		// If reached here, it implies that the currently opened is the Content Page.
		return getPageUsingBrowser(getConfig(), getUser(), ContentNavigationPage.class);
	}

	if(pageIndicationElements[1] != null) {
		// If reached here, it implies that the currently opened is a Folder Page.
		return getPageUsingBrowser(getConfig(), getUser(), FolderPage.class);
	}

	// If reached here, it implies that the currently opened is a Dashboard Page.
	return getPageUsingBrowser(getConfig(), getUser(), DashboardPage.class);
}

@Override
public void waitForLoadingPageEnd() {
	super.waitForLoadingPageEnd();

	// Dismiss a tour if one is presented.
	//dismissTour();
}
}