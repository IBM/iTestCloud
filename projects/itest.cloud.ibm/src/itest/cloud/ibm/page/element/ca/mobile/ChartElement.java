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
package itest.cloud.ibm.page.element.ca.mobile;

import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.ca.mobile.BoardsPage;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents a chart element in {@link BoardsPage} and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getChartName()}: Return the name of the chart..</li>
 * <li>{@link #open()}: Open the chart.</li>
 * <li>{@link #openContextMenu()}: Open the context menu.</li>
 * <li>{@link #waitForLoadingEnd()}: Wait for loading of the wrapped element to complete.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitle(BrowserElement)}: Return the title from a given title element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * </p>
 */
public class ChartElement extends CaMobileElementWrapper {

	public static final By CHART_ELEMENT_LOCATOR_STRING = By.xpath("//*[@class='camobile-chart-container']");
	public static final By CHART_LOADING_CONFIRMATION_ELEMENT_LOCATOR = By.xpath(".//*[starts-with(@class,'ariaLabelNode')]");
	// Title element of a graph and KPI widget are given below respectively.
	public static final By CHART_TITLE_ELEMENT_LOCATOR = By.xpath(
		".//*[contains(@id,'Title') and .//*] | " +
		".//*[contains(@class,'kpi-widget-base-value')]//*[contains(@class,'value-label')]//*[not(child::*)]");

public ChartElement(final Page page, final BrowserElement element, final String... data) {
	super(page, element, data);
}

/**
 * Return the name of the chart.
 *
 * @return The name of the chart as {@link String}.
 */
public String getChartName() {
	return this.data[0];
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote(getChartName()));
}

@Override
protected By getTitleElementLocator() {
	return CHART_TITLE_ELEMENT_LOCATOR;
}

/**
 * Open the chart.
 */
public void open() {
	this.element.click();
}

/**
 * Open the context menu.
 */
public void openContextMenu() {
	waitForElement(By.xpath(".//*[contains(@class,'overflow')]")).click();
}

@Override
public void waitForLoadingEnd() {
	super.waitForLoadingEnd();
	// Wait for the content of the chart to load.
	waitForElement(CHART_LOADING_CONFIRMATION_ELEMENT_LOCATOR, timeout(), true /*fail*/, false /*displayed*/);
}
}