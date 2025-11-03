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

import static itest.cloud.ibm.page.ca.mobile.FolderPage.FOLDER_SCREEN_ELEMENT_LOCATOR;
import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioConstants.BACK_BUTTON_LOCATOR;
import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioUtil.TEAM_CONTENT_BUTTON_LOCATOR;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;
import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.page.element.ca.mobile.DashboardChartElement;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * This class represents a Dashboard Page, which is opened via {@link FolderPage}.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #back()}: Navigate back to the Folder Page containing the dashboard.</li>
 * <li>{@link #getName()}: Return the name of the dashboard.</li>
 * <li>{@link #pinChartElement(String)}: Pin a chart in a board.</li>
 * <li>{@link #waitForLoadingPageEnd()}: Wait for the page loading to be finished.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Returns a pattern matching the expected title for the current web page.</li>
 * </ul>
 * </p>
 */
public class DashboardPage extends ChartListPage implements AssetPage {

	public static final By DASHBOARD_PAGE_ELEMENT_LOCATOR = AppiumBy.accessibilityId("Dashboard page");

public DashboardPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
	this.data = new String [] {getTitle()};
}

public DashboardPage(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

/**
 * Navigate back to the previously opened page.
 *
 * @return The previously opened page as {@link FolderPage} or {@link ContentNavigationPage}.
 */
@Override
public CaMobilePage back() {
	// Click the Back Button.
	click(BACK_BUTTON_LOCATOR);
	// Clicking the Back Button would open the Content Page if the currently opened is a top level folder or
	// a different Folder Page otherwise. Therefore, figure out what type of page has just opened.
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

/**
 * Return the chart element with a given name.
 *
 * @param name The name of the chart element as {@link String}.
 * @param fail Specify whether to fail if a matching chart element could not be found.
 *
 * @return The the desired chart element as {link DashboardChartElement} or
 * <code>null</code> if a matching chart element could not be found and specified not to fail in such a situation.
 *
 * @throws WaitElementTimeoutError If a matching chart element could not be found and specified to fail in such a situation.
 */
private DashboardChartElement getChartElement(final String name, final boolean fail) {
	return getChartElement(compile(quote(name)), DashboardChartElement.class, fail);
}

/**
 * Return the chart elements with the name matching a given pattern.
 *
 * @param pattern The pattern matching the name of the chart elements as {@link Pattern}.
 * @param fail Specify whether to fail if a matching chart element could not be found.
 *
 * @return The the desired chart elements as a @ {@link List} of {link DashboardChartElement} or
 * <code>null</code> if a matching chart element could not be found and specified not to fail in such a situation.
 *
 * @throws WaitElementTimeoutError If a matching chart element could not be found and specified to fail in such a situation.
 */
private List<DashboardChartElement> getChartElements(final Pattern pattern, final boolean fail) {
	return getChartElements(pattern, DashboardChartElement.class, fail);
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote(getName()));
}

/**
 * Return the name of the dashboard.
 *
 * @return The name of the dashboard as {@link String}.
 */
@Override
public String getName() {
	return this.data[0];
}

/**
 * Pin a chart in a board.
 *
 * @param name The name of the chart {@link String}.
 *
 * @return The opened Dashboard Page after pinning the chart in the board.
 */
public BoardsSelectionPage pinChartElement(final String name) {
	try {
		// The content of the board is presented in a WebView. Therefore, switch to the appropriate window.
		switchToMobileDashboardWindow();

		final DashboardChartElement chartElement = getChartElement(name, true /*fail*/);
		chartElement.pin();
	}
	finally {
		// Switch back to the native application context.
		switchToNativeAppContext();
	}
	return getPageUsingBrowser(getConfig(), getUser(), BoardsSelectionPage.class, getName());
}

/**
 * Switch to the Dashboard Window in the WEBVIEW_com.ibm.ba.camobile context.
 */
private void switchToMobileDashboardWindow() {
	switchToCaMobileWebViewContext();
	switchToWindow(compile(quote(getCaApplication().getDashboardViewUrl())));
}

@Override
public void waitForLoadingPageEnd() {
	super.waitForLoadingPageEnd();

	// Wait for the content of the page to load.
	try {
		// The content of the board is presented in a WebView. Therefore, switch to the appropriate window.
		switchToMobileDashboardWindow();

		// Wait for the chart elements grid or no charts message to appear in the currently opened board.
		BrowserElement[] loadingCompletionIndicatorElements =
			waitForMultipleElements(CHARTS_GRID_ELEMENT_LOCATOR, NO_CONTENT_ELEMENT_LOCATOR);
		// If the chart elements grid exists, wait for all its charts elements to load.
		if(loadingCompletionIndicatorElements[0] != null) {
			final List<DashboardChartElement> chartElements = getChartElements(null /*pattern*/, true /*fail*/);
			for (DashboardChartElement chartElement : chartElements) {
				chartElement.waitForLoadingEnd();
			}
		}
	}
	finally {
		// Switch back to the native application context.
		switchToNativeAppContext();
	}
}
}