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
package itest.cloud.ibm.page.element.ca.mobile;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.ca.mobile.DashboardPage;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents a chart element in a {@link DashboardPage} and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #pin()}: Pin the chart in a board.</li>
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
public class DashboardChartElement extends ChartElement {

public DashboardChartElement(final Page page, final BrowserElement element, final String... data) {
	super(page, element, data);
}

/**
 * Pin the chart in a board.
 */
public void pin() {
	waitForElement(By.xpath(".//*[contains(@class, 'chart-pin')]")).click();
}
}