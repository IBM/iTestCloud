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

import static itest.cloud.entity.AlertStatus.Success;
import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioUtil.getBoardsDeletionAlertPattern;
import static itest.cloud.util.ByUtils.OR;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.page.dialog.ca.mobile.NewBoardDialog;
import itest.cloud.ibm.page.element.ca.mobile.*;
import itest.cloud.page.element.AlertElement;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.InvalidOutcomeError;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * This class represents the Boards Page which is either opened via the Bottom Navigation Menu or opens automatically when the application is launched.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #boardsExist()}: Specifies whether no boards exist.</li>
 * <li>{@link #chartExist(String)}: Specifies whether a given chart exist in the currently opened board.</li>
 * <li>{@link #createBoard(String)}: Create a new board with a given name.</li>
 * <li>{@link #deleteBoard()}: Delete the currently opened board.</li>
 * <li>{@link #getBoardName()}: Return the name of the currently open board.</li>
 * <li>{@link #openBoardsManagementPage()}: Open the Boards Management Page.</li>
 * <li>{@link #openChart(String)}: Open a given chart from the currently opened board.</li>
 * <li>{@link #renameBoard(String)}: Rename the currently opened board to a given.</li>
 * <li>{@link #unpinChart(String)}: Unpin the a given chart from the currently opened board.</li>
 * <li>{@link #waitForLoadingPageEnd()}: Wait for the page loading to be finished.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Returns a pattern matching the expected title for the current web page.</li>
 * <li>{@link #getTitleElementLocator()}: Return the title element locator.</li>
 * </ul>
 * </p>
 */
public class BoardsPage extends ChartListPage {

	private static final By NO_CHARTS_ELEMENT_LOCATOR = By.xpath("//*[@class='noPinsPlaceholder']//h5");
	private static final By TOOLBAR_TITLE_ELEMENT_LOCAOR = By.xpath("//*[@class='camobile-toolbar-title']");

public BoardsPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

/**
 * Specifies whether boards exist.
 *
 * @return <code>true</code> if at least one board exists or <code>false</code> otherwise.
 */
public boolean boardsExist() {
	try {
		// The no boards message is presented in a WebView. Therefore, switch to the appropriate window.
		switchToMobilePinboardWindow();

		// Check if the tool bar title element or the no boards message is present.
		BrowserElement[] existenceIndicatorElements =
			waitForMultipleElements(TOOLBAR_TITLE_ELEMENT_LOCAOR, NO_CONTENT_ELEMENT_LOCATOR);
		// Check if the no boards message is present.
		return existenceIndicatorElements[0] != null;
	}
	finally {
		// Switch back to the native application context.
		switchToNativeAppContext();
	}
}

/**
 * Specifies whether a given chart exist in the currently opened board.
 *
 * @return <code>true</code> if the given chart exists in the currently opened board or <code>false</code> otherwise.
 */
public boolean chartExist(final String name) {
	try {
		// The no boards message is presented in a WebView. Therefore, switch to the appropriate window. existence
		switchToMobilePinboardWindow();

		return getChartElement(name, false /*fail*/) != null;
	}
	finally {
		// Switch back to the native application context.
		switchToNativeAppContext();
	}
}

/**
 * Specifies whether charts exist in the currently opened board.
 *
 * @return <code>true</code> if at least one chart exists in the currently opened board or <code>false</code> otherwise.
 */
private boolean chartsExist() {
	try {
		// The no boards message is presented in a WebView. Therefore, switch to the appropriate window. existence
		switchToMobilePinboardWindow();

		// Check if the charts grid element or the no charts message is present.
		final BrowserElement[] existenceIndicatorElements =
			waitForMultipleElements(CHARTS_GRID_ELEMENT_LOCATOR, NO_CHARTS_ELEMENT_LOCATOR);
		return existenceIndicatorElements[0] != null;
	}
	finally {
		// Switch back to the native application context.
		switchToNativeAppContext();
	}
}

/**
 * Create a new board with a given name.
 *
 * @param name The name of the board. If <code>null</code> is provided as the value of this parameter,
 * a default name will be assigned to the newly created board.
 */
public void createBoard(final String name) {
	try {
		// The new board button is presented in a WebView. Therefore, switch to the appropriate window.
		switchToMobilePinboardWindow();

		// Click the new board button or the add button in the tool bar.
		final BrowserElement buttonElement = waitForElement(
			By.xpath("//add-new-board-button" + OR + "//*[@data-ftu-id='camobile-toolbar-add']//*[name()='svg']"), timeout(), true /*fail*/, true /*displayed*/, false /*single*/);
		buttonElement.click();
	}
	finally {
		// Switch back to the native application context.
		switchToNativeAppContext();
	}

	final NewBoardDialog newBoardDialog = new NewBoardDialog(this);
	newBoardDialog.opened();
	newBoardDialog.createBoard(name);

	// Wait for the newly created board to open in the Boards Page.
	final long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
	String boardName;
	while (!(boardName = getBoardName()).equals(name)) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The newly created board '" + name + "' did not open in the Boards Page before timeout '" + timeout() + "'s had reached. A board named '" + boardName + "' remained opened in the Boards Page instead.");
		}
	}

	// Ensure there are no charts exist in the newly created board.
	if(chartsExist()) {
		throw new InvalidOutcomeError("Charts were present in the newly created board with name '" + name + "'. No charts were expected in a newly created board.");
	}
}

/**
 * Delete the currently opened board.
 */
public void deleteBoard() {
	// Record the board name for validating its removal later on.
	final String boardName = getBoardName();

	// Delete the board via the context menu of the board element.
	final BoardContextMenuElement contextMenuElement = getContextMenuElement();
	contextMenuElement.deleteBoard();

	// Wait for an alert to appear indicating that the deletion was successful or not.
	final AlertElement alertElement = getAlertElement(getBoardsDeletionAlertPattern(boardName), true /*fail*/);
	// Make sure the alert indicates that the deletion was successful.
	if(alertElement.getStatus() != Success) throw new WaitElementTimeoutError("Deleting board '" + boardName + "' failed with the following message: " + alertElement.getMessage());
	// Dismiss the alert.
	alertElement.close();

	// Wait for the deleted board to disappear from the Boards Page.
	// TODO: What if the deleted was the only board in the application? Need to consider this situation in the following logic.
	final long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
	while (getBoardName().equals(boardName)) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The deleted board '" + boardName + "' remained in the Boards Page until timeout '" + timeout() + "'s had reached.");
		}
	}
}

/**
 * Return the name of the currently opened board.
 *
 * @return The name of the currently opened board as {@link String}.
 */
public String getBoardName() {
	try {
		// The toolbar title element is presented in a WebView. Therefore, switch to the appropriate window.
		switchToMobilePinboardWindow();

		return waitForElement(TOOLBAR_TITLE_ELEMENT_LOCAOR).getText();
	}
	finally {
		// Switch back to the native application context.
		switchToNativeAppContext();
	}
}

/**
 * Return the chart element with a given name.
 *
 * @param name The name of the chart element as {@link String}.
 * @param fail Specify whether to fail if a matching chart element could not be found.
 *
 * @return The the desired chart element as {link BoardChartElement} or
 * <code>null</code> if a matching chart element could not be found and specified not to fail in such a situation.
 *
 * @throws WaitElementTimeoutError If a matching chart element could not be found and specified to fail in such a situation.
 */
private BoardChartElement getChartElement(final String name, final boolean fail) {
	return getChartElement(compile(quote(name)), BoardChartElement.class, fail);
}

/**
 * Return the chart elements with the name matching a given pattern.
 *
 * @param pattern The pattern matching the name of the chart elements as {@link Pattern}.
 * @param fail Specify whether to fail if a matching chart element could not be found.
 *
 * @return The the desired chart elements as a @ {@link List} of {link BoardChartElement} or
 * <code>null</code> if a matching chart element could not be found and specified not to fail in such a situation.
 *
 * @throws WaitElementTimeoutError If a matching chart element could not be found and specified to fail in such a situation.
 */
private List<BoardChartElement> getChartElements(final Pattern pattern, final boolean fail) {
	return getChartElements(pattern, BoardChartElement.class, fail);
}

/**
 * Return the content menu element.
 *
 * @return The context menu element as {@link BoardContextMenuElement}.
 */
private BoardContextMenuElement getContextMenuElement() {
	return new BoardContextMenuElement(this, null /*expansionElement*/) {
		@Override
		protected void clickOnExpansionElement(final boolean expand) {
			MobilePinboardWindowAction action = new MobilePinboardWindowAction() {
				@Override
				public void performActionInMobilePinboardWindow(final Object... actionData) {
					click(By.xpath("//*[contains(@class,'toolbar-overflow')]/*[name()='svg']"));
				}
			};
			action.perform();
		}
	};
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

@Override
protected By getTitleElementLocator() {
	return null;
}

/**
 * Open the Boards Management Page.
 *
 * @return The opened Boards Page as {@link BoardsManagementPage}.
 */
public BoardsManagementPage openBoardsManagementPage() {
	return openMobilePageUsingLink(BoardsManagementPage.class, new MobilePinboardWindowAction() {
		@Override
		public void performActionInMobilePinboardWindow(final Object... actionData) {
			// Place a click on the appropriate icon element.
			click(By.xpath("//*[contains(@class,'toolbar-chevron')]//*[name()='svg']"));
		}
	});
}

/**
 * Open a given chart in the currently opened board.
 *
 * @param name The name of the chart as {@link String}.
 *
 * @return The opened Chart Page as {@link ChartPage}.
 */
public ChartPage openChart(final String name) {
	return openMobilePageUsingLink(ChartPage.class, new MobilePinboardWindowAction() {
		@Override
		public void performActionInMobilePinboardWindow(final Object... actionData) {
			// Find the chart element.
			final BoardChartElement chartElement = getChartElement(name, true /*fail*/);
			// Trigger the maximize action on the chart element.
			chartElement.open();
		}
	}, getBoardName(), name);
}

/**
 * Return the content menu element of a given chart.
 *
 * @return The context menu element of the chart as {@link CartContextMenuElement}.
 */
private CartContextMenuElement openContextMenuElementOfChart(final String chart) {
	return new CartContextMenuElement(this, null /*expansionElement*/, chart) {
		@Override
		protected void clickOnExpansionElement(final boolean expand) {
			MobilePinboardWindowAction action = new MobilePinboardWindowAction() {
				@Override
				public void performActionInMobilePinboardWindow(final Object... actionData) {
					// Find the chart element.
					final BoardChartElement chartElement = getChartElement(chart, true /*fail*/);
					// Trigger the maximize action on the chart element.
					chartElement.openContextMenu();
				}
			};
			action.perform();
		}
	};
}

/**
 * Rename the currently opened board to a given.
 *
 * @param newName The new name to be assigned to the board.
 */
public void renameBoard(final String newName) {
	// Rename the board via the context menu of the board element.
	final BoardContextMenuElement contextMenuElement = getContextMenuElement();
	contextMenuElement.renameBoard(newName);

	// Wait for the new name of the board to appear in the Boards Page.
	final long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
	while (!getBoardName().equals(newName)) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The new name '" + newName + "' of the board had not appeared in the Boards Page when timeout '" + timeout() + "'s was reached.");
		}
	}
}

/**
 * Unpin the a given chart from the currently opened board.
 *
 * @param name The name of the chart as {@link String}.
 */
public void unpinChart(final String name) {
	// Rename the board via the context menu of the board element.
	final CartContextMenuElement contextMenuElement = openContextMenuElementOfChart(name);
	contextMenuElement.unpinFromBoard();

	// Wait for an alert to appear indicating that the unpinning was successful or not.
	final AlertElement alertElement = getAlertElement(compile(quote("Pin was removed")), true /*fail*/);
	// Make sure the alert indicates that the unpinning was successful.
	if(alertElement.getStatus() != Success) throw new WaitElementTimeoutError("Unpinning chart '" + name + " from board '" + getBoardName() + "' failed with the following message: " + alertElement.getMessage());
	// Dismiss the alert.
	alertElement.close();

	// Wait for the chart to disappear from the board.
	final long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
	while (chartExist(name)) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The unpinned chart '" + name + "' remained in the board until the timeout '" + timeout() + "'s had reached.");
		}
	}
}

@Override
public void waitForLoadingPageEnd() {
	super.waitForLoadingPageEnd();

	// Wait for the content of the open board to load.
	try {
		// The content of the board is presented in a WebView. Therefore, switch to the appropriate window.
		switchToMobilePinboardWindow();

		// Wait for the tool bar title element to load or the no boards message to appear.
		BrowserElement[] loadingCompletionIndicatorElements =
			waitForMultipleElements(TOOLBAR_TITLE_ELEMENT_LOCAOR, NO_CONTENT_ELEMENT_LOCATOR);
		// If the tool bar title exists, it implies that a board is currently present in the Boards Page.
		// Therefore, wait for the chart elements grid or no charts message to appear in the currently opened board.
		if(loadingCompletionIndicatorElements[0] != null) {
			loadingCompletionIndicatorElements =
				waitForMultipleElements(CHARTS_GRID_ELEMENT_LOCATOR, NO_CHARTS_ELEMENT_LOCATOR);
			// If the chart elements grid exists, wait for all its charts elements to load.
			if(loadingCompletionIndicatorElements[0] != null) {
				final List<BoardChartElement> chartElements = getChartElements(null /*pattern*/, true /*fail*/);
				for (BoardChartElement chartElement : chartElements) {
					chartElement.waitForLoadingEnd();
				}
			}
		}
	}
	finally {
		// Switch back to the native application context.
		switchToNativeAppContext();
	}
}
}