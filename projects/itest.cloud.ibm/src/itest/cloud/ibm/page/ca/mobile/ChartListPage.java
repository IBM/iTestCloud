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

import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioConstants.CHART_TITLE_ELEMENT_LOCATOR;
import static itest.cloud.scenario.ScenarioUtil.println;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.page.element.ca.mobile.ChartElement;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.ScenarioFailedError;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * This class represents the Boards Page which is either opened via the Bottom Navigation Menu or opens automatically when the application is launched.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getChartElement(Pattern, Class, boolean)}: Return the chart element with the name matching a given pattern.</li>
 * <li>{@link #getChartElement(String, Class, boolean)}: Return the chart element with the name matching a given pattern.</li>
 * <li>{@link #getChartElements(Pattern, Class, boolean)}: Return the chart element with the name matching a given pattern.</li>
 * </ul>
 * </p>
 */
public abstract class ChartListPage extends CaMobilePageWithBottomTabList {

	private static final String CHARTS_GRID_ELEMENT_XPATH_STRING = "//*[(@class='camobile-swipe-view') and (@aria-hidden='false')]//*[@class='camobile-grid-cells']";
	protected static final By CHARTS_GRID_ELEMENT_LOCATOR = By.xpath(CHARTS_GRID_ELEMENT_XPATH_STRING);
	private static final By CHART_ELEMENT_LOCATOR = By.xpath(CHARTS_GRID_ELEMENT_XPATH_STRING + "/*");
	protected static final By NO_CONTENT_ELEMENT_LOCATOR = By.xpath("//*[@class='noContentPlaceholder']//h5");

public ChartListPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

public ChartListPage(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

/**
 * Return the chart element with the name matching a given pattern.
 *
 * @param pattern The pattern matching the name of the chart element as {@link Pattern}.
 * @param chartElementClass A class representing the particular tab as a {@link ChartElement}.
 * @param fail Specify whether to fail if a matching chart element could not be found.
 *
 * @return The the desired chart element as {link ChartElement} or
 * <code>null</code> if a matching chart element could not be found and specified not to fail in such a situation.
 *
 * @throws WaitElementTimeoutError If a matching chart element could not be found and specified to fail in such a situation.
 */
protected <T extends ChartElement> T getChartElement(final Pattern pattern, final Class<T> chartElementClass, final boolean fail) {
	final List<T> chartElements = getChartElements(pattern, chartElementClass, fail);

	return (!chartElements.isEmpty()) ? chartElements.get(0 /*index*/) : null;
}

/**
 * Return the chart element with a given name.
 *
 * @param name The name of the chart element as {@link String}.
 * @param chartElementClass A class representing the particular tab as a {@link ChartElement}.
 * @param fail Specify whether to fail if a matching chart element could not be found.
 *
 * @return The the desired chart element as {link ChartElement} or
 * <code>null</code> if a matching chart element could not be found and specified not to fail in such a situation.
 *
 * @throws WaitElementTimeoutError If a matching chart element could not be found and specified to fail in such a situation.
 */
protected <T extends ChartElement> T getChartElement(final String name, final Class<T> chartElementClass, final boolean fail) {
	return getChartElement(compile(quote(name)), chartElementClass, fail);
}

/**
 * Return the chart elements with the name matching a given pattern.
 *
 * @param pattern The pattern matching the name of the chart elements as {@link Pattern}.
 * @param chartElementClass A class representing the particular tab as a {@link ChartElement}.
 * @param fail Specify whether to fail if a matching chart element could not be found.
 *
 * @return The the desired chart elements as a @ {@link List} of {link ChartElement} or
 * <code>null</code> if a matching chart element could not be found and specified not to fail in such a situation.
 *
 * @throws WaitElementTimeoutError If a matching chart element could not be found and specified to fail in such a situation.
 */
protected <T extends ChartElement> List<T> getChartElements(final Pattern pattern, final Class<T> chartElementClass, final boolean fail) {
	final int timeout = fail ? timeout() : tinyTimeout();
	final long timeoutMillis = timeout * 1000 + System.currentTimeMillis();

	while (true) {
		final List<T> chartElements = new ArrayList<T>();
		final List<BrowserElement> chartWebElements = waitForElements(CHART_ELEMENT_LOCATOR, timeout, fail);

		for (int i = 0; i < chartWebElements.size(); i++) {
			final BrowserElement chartWebElement = chartWebElements.get(i /*index*/);
			final BrowserElement chartTitleElement = chartWebElement.waitForElement(CHART_TITLE_ELEMENT_LOCATOR);
			final String chartName = chartTitleElement.getText();

			if((pattern == null) || pattern.matcher(chartName).matches()) {
				T chartElement;

				try {
					chartElement = chartElementClass.getConstructor(Page.class, BrowserElement.class, String[].class).newInstance(this, chartWebElement, new String[] {chartName});
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

				chartElements.add(chartElement);
			}
		}

		if(!chartElements.isEmpty()) {
			return chartElements;
		}
		else if (System.currentTimeMillis() > timeoutMillis) {
			if(fail) throw new WaitElementTimeoutError("A chart element with name matching pattern '" + pattern + "' could not be found before timeout '" + timeout + "'s had reached.");
			return chartElements;
		}
	}
}
}