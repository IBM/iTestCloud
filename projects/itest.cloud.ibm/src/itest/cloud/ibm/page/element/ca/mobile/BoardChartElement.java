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

import itest.cloud.ibm.page.ca.mobile.BoardsPage;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents a chart element in {@link BoardsPage} and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #openContextMenu()}: Open the context menu of the chart element.</li>
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
public class BoardChartElement extends ChartElement {

public BoardChartElement(final Page page, final BrowserElement element, final String... data) {
	super(page, element, data);
}

/**
 * Open the context menu of the chart element.
 */
public void openContextMenu() {
	waitForElement(By.xpath(".//*[contains(@class,'overflow')]")).click();
}
}