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

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents a page presented to select a board when a pinning a chart in the particular board.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #selectBoard(String)}: Select a given board.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class BoardsSelectionPage extends BoardsListPage {

public BoardsSelectionPage(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

private String getDashboardName() {
	return this.data[0];
}

/**
 * Select a given board.
 *
 * @param name The name of the board as {@link String}.
 *
 * @return The opened Dashboard Page after selecting the given board.
 */
public DashboardPage selectBoard(final String name) {
	final BrowserElement boardElement = getBoardElement(name, true /*fail*/);
	return openMobilePageUsingLink(boardElement, DashboardPage.class, getDashboardName());
}
}