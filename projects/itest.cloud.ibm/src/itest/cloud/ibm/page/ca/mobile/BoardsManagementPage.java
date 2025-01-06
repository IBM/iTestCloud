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

import static itest.cloud.ibm.page.element.ca.mobile.BoardElement.BOARD_TITLE_ELEMENT_LOCATOR;
import static itest.cloud.ibm.page.element.ca.mobile.BoardElement.CONTEXT_MENU_EXPANSION_ELEMENT_LOCATOR_STRING;
import static itest.cloud.scenario.ScenarioUtil.println;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.page.element.ca.mobile.BoardContextMenuElement;
import itest.cloud.ibm.page.element.ca.mobile.BoardElement;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * This class represents a page with functionality to manage boards.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #close()}: Close the Non Navigable Page by opening a Navigable Page.</li>
 * <li>{@link #deleteBoard(String)}: Delete a given board.</li>
 * <li>{@link #openBoard(String)}: Open a given board.</li>
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
public class BoardsManagementPage extends CaMobilePage implements CaMobileNonNavigablePage {

	private static final String CLOSE_ICON_BUTTON_LOCATOR_STRING = "//*[@*='icon-button-boards-header-close']";
	private static final By BOARD_ELEMENTS_LOCATOR = By.xpath("//*[@*='boards-list']/*/*" + "[" + CONTEXT_MENU_EXPANSION_ELEMENT_LOCATOR_STRING + "]");

public BoardsManagementPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

@Override
public BoardsPage close() {
	return openMobilePageUsingLink(By.xpath(CLOSE_ICON_BUTTON_LOCATOR_STRING), BoardsPage.class);
}

/**
 * Delete a given board.
 *
 * @param name The name of the board as {@link String}.
 */
public void deleteBoard(final String name) {
	// Look for the desired board among the existing ones.
	final BoardElement boardElement = getBoardElement(name, false /*fail*/);
	// If the given board does not exists, simply return after displaying an appropriate message.
	if(boardElement == null) {
		println("	  -> A board named '" + name + "' did not exist and therefore, no attempt was made to delete it.");
		return;
	}
	// Delete the board via the context menu of the board element.
	final BoardContextMenuElement contextMenuElement = boardElement.getContextMenuElement();
	contextMenuElement.deleteBoard();

	// Wait for the deleted board to disappear from the Boards Management Page.
	final long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
	while (getBoardElement(name, false /*fail*/) != null) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The deleted board '" + name + "' remained in the Boards Management Page until timeout '" + timeout() + "'s had reached.");
		}
	}
}

private BoardElement getBoardElement(final Pattern pattern, final boolean fail) {
	final List<BoardElement> boardElements = getBoardElements(pattern, fail);

	return (!boardElements.isEmpty()) ? boardElements.get(0 /*index*/) : null;
}

public BoardElement getBoardElement(final String name, final boolean fail) {
	search(name);

	return getBoardElement(compile(quote(name)), fail);
}

private List<BoardElement> getBoardElements(final Pattern pattern, final boolean fail) {
	final int timeout = fail ? timeout() : tinyTimeout();
	final long timeoutMillis = timeout * 1000 + System.currentTimeMillis();

	while (true) {
		final List<BoardElement> boardElements = new ArrayList<BoardElement>();
		final List<BrowserElement> boardWebElements = waitForElements(BOARD_ELEMENTS_LOCATOR, timeout, fail);

		for (BrowserElement boardWebElement : boardWebElements) {
			final BrowserElement chartTitleElement = boardWebElement.waitForElement(BOARD_TITLE_ELEMENT_LOCATOR);
			final String chartName = chartTitleElement.getText();

			if((pattern == null) || pattern.matcher(chartName).matches()) {
				boardElements.add(new BoardElement(this, boardWebElement, chartName));
			}
		}

		if(!boardElements.isEmpty()) {
			return boardElements;
		}
		else if (System.currentTimeMillis() > timeoutMillis) {
			if(fail) throw new WaitElementTimeoutError("A board element with name matching pattern '" + pattern + "' could not be found before timeout '" + timeout + "'s had reached.");
			return boardElements;
		}
	}
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote("Boards"));
}

@Override
protected String getTitle(final BrowserElement titleElement) {
	return titleElement.getTextAttribute();
}

@Override
protected By getTitleElementLocator() {
	return By.xpath("//*[(@*='ca-text-undefined') and .." + CLOSE_ICON_BUTTON_LOCATOR_STRING + "]");
}

/**
 * Open a given board.
 *
 * @param name The name of the board as {@link String}.
 *
 * @return The opened Boards Page as {@link BoardsPage}.
 */
public BoardsPage openBoard(final String name) {
	final BoardElement boardElement = getBoardElement(name, true /*fail*/);

	return boardElement.openBoard();
}

private void search(final String text) {
	typeText(By.xpath("//*[@*='ca-textinput-search-bar']"), text);
	// Wait for the filtered boards elements or no search results message to appear.
	waitForMultipleElements(BOARD_ELEMENTS_LOCATOR, By.xpath("//*[contains(@text,'no search results')]"));
}
}