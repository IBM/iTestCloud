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

import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioConstants.CLOSE_BUTTON_LOCATOR;
import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioConstants.SEARCH_BAR_LOCATOR;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import io.appium.java_client.AppiumBy;
import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents a page with a list of boards and functionality to manage them.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #close()}: Close the Non Navigable Page by opening a Navigable Page.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getBoardElement(String, boolean)}: Return a given board element.</li>
 * <li>{@link #getExpectedTitle()}: Returns a pattern matching the expected title for the current web page.</li>
 * <li>{@link #search(String)}: Search a given text.</li>
 * </ul>
 * </p>
 */
public abstract class BoardsListPage extends CaMobilePage implements CaMobilePageWithoutBottomTabList {

public BoardsListPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

public BoardsListPage(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

@Override
public BoardsPage close() {
	return openMobilePageUsingLink(CLOSE_BUTTON_LOCATOR, BoardsPage.class);
}

/**
 * Return a given board element.
 *
 * @param name The name of the board as {@link String}.
 * @param fail Specify whether to fail if a matching board element can not be found.
 *
 * @return The given board element as {@link BrowserElement} or <code>null</code> if
 * a matching board element can not be found and asked not to fail under the circumstances.
 */
protected BrowserElement getBoardElement(final String name, final boolean fail) {
	search(name);
	return waitForElement(AppiumBy.accessibilityId("listitem-" + name), fail ? timeout() : tinyTimeout(), fail, true /*displayed*/, false /*single*/);
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote("Boards"));
}

/**
 * Search a given text.
 *
 * @param text The text to be searched as {@link String}.
 */
protected void search(final String text) {
	typeText(SEARCH_BAR_LOCATOR, text);
}
}