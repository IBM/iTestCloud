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
package itest.cloud.ibm.page.element.ca.mobile;

import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.ca.mobile.BoardsManagementPage;
import itest.cloud.ibm.page.ca.mobile.BoardsPage;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents a board element in {@link BoardsManagementPage} and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getContextMenuElement()}: Return the content menu element.</li>
 * <li>{@link #openBoard()}: Open the board.</li>
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
public class BoardElement extends CaMobileElementWrapper {

	public static final By BOARD_TITLE_ELEMENT_LOCATOR = By.xpath(".//*[contains(@content-desc,'listitem')]/*[2]");
	public static final String CONTEXT_MENU_EXPANSION_ELEMENT_LOCATOR_STRING = ".//*[starts-with(@content-desc,'icon-button')]";
	private static final By CONTEXT_MENU_EXPANSION_ELEMENT_LOCATOR = By.xpath(CONTEXT_MENU_EXPANSION_ELEMENT_LOCATOR_STRING);

public BoardElement(final Page page, final BrowserElement element, final String... data) {
	super(page, element, data);
}

private String getBoardName() {
	return this.data[0];
}

/**
 * Return the content menu element.
 *
 * @return The context menu element as {@link BoardContextMenuElement}.
 */
public BoardContextMenuElement getContextMenuElement() {
	return new BoardContextMenuElement(getPage(), waitForElement(CONTEXT_MENU_EXPANSION_ELEMENT_LOCATOR), getBoardName());
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote(getBoardName()));
}

@Override
protected String getTitle(final BrowserElement titleElement) {
	return titleElement.getTextAttribute();
}

@Override
protected By getTitleElementLocator() {
	return BOARD_TITLE_ELEMENT_LOCATOR;
}

/**
 * Open the board.
 *
 * @return The opened Boards Page as {@link BoardsPage}.
 */
public BoardsPage openBoard() {
	return openMobilePageUsingLink(this.element, BoardsPage.class);
}
}