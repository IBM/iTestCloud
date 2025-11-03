/*********************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
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

import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioConstants.CHART_LOADING_CONFIRMATION_ELEMENT_LOCATOR;
import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioConstants.CHART_TITLE_ELEMENT_LOCATOR;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents a page with a chart and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #back()}: Navigate back to the Boards Page.</li>
 * <li>{@link #close()}: Close the Non Navigable Page by opening a Navigable Page.</li>
 * <li>{@link #getBoardName()}: Return the name of the board where the chart resides.</li>
 * <li>{@link #getChartName()}: Return the name of the chart.</li>
 * <li>{@link #getTitle()}: Return the title of the page.</li>
 * <li>{@link #waitForLoadingPageEnd()}: Wait for the page loading to be finished.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Returns a pattern matching the expected title for the current web page.</li>
 * <li>{@link #getTitle(BrowserElement)}: Return the title from a given title element.</li>
 * <li>{@link #getTitleElement()}: Return the title element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the title element locator.</li>
 * </ul>
 * </p>
 */
public class ChartPage extends CaMobilePage implements CaMobilePageWithoutBottomTabList {

public ChartPage(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

/**
 * Navigate back to the Boards Page.
 *
 * @return The opened Boards Page as {@link BoardsPage}.
 */
public BoardsPage back() {
	return openMobilePageUsingLink(BoardsPage.class, new MobilePinboardWindowAction() {
		@Override
		public void performActionInMobilePinboardWindow(final Object... actionData) {
			// Place a click on the back icon element.
			click(By.xpath("//*[contains(@class,'navvizback')]//*[name()='svg']")).click();
		}
	});
}

@Override
public CaMobilePageWithBottomTabList close() {
	return back();
}

/**
 * Return the name of the board where the chart resides.
 *
 * @return The name of the board where the chart resides as {@link String}.
 */
public String getBoardName() {
	return this.data[0];
}

/**
 * Return the name of the chart.
 *
 * @return The name of the chart as {@link String}.
 */
public String getChartName() {
	return this.data[1];
}

/**
 * Return the chart web element.
 *
 * @return The chart web element as {@link BrowserElement}.
 */
private BrowserElement getChartWebElement() {
	return waitForElement(By.xpath("//*[@class='camobile-chart max']"));
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote(getChartName()));
}

@Override
public String getTitle() {
	try {
		// The title element is presented in a WebView. Therefore, switch to the appropriate window.
		switchToMobilePinboardWindow();

		return super.getTitle();
	}
	finally {
		// Switch back to the native application context.
		switchToNativeAppContext();
	}
}

@Override
protected String getTitle(final BrowserElement titleElement) {
	final String titleAttribute = titleElement.getAttribute("title");
	return (titleAttribute != null) ? titleAttribute : titleElement.getText();
}

@Override
protected BrowserElement getTitleElement() {
	return waitForElement(getChartWebElement(), getTitleElementLocator());
}

@Override
protected By getTitleElementLocator() {
	return CHART_TITLE_ELEMENT_LOCATOR;
}

@Override
public void waitForLoadingPageEnd() {
	super.waitForLoadingPageEnd();

	// Wait for the content of the chart to load.
	try {
		// The chart element is presented in a WebView. Therefore, switch to the appropriate window.
		switchToMobilePinboardWindow();

		// Wait for the content of the chart to load.
		waitForElement(getChartWebElement(), CHART_LOADING_CONFIRMATION_ELEMENT_LOCATOR, timeout(), true /*fail*/, false /*displayed*/);
	}
	finally {
		// Switch back to the native application context.
		switchToNativeAppContext();
	}
}
}