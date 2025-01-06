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
package itest.cloud.ibm.scenario.ca.mobile;

import static itest.cloud.page.Page.getPageUsingBrowser;
import static itest.cloud.scenario.ScenarioUtil.println;

import itest.cloud.config.IUser;
import itest.cloud.ibm.config.IbmUser;
import itest.cloud.ibm.page.ca.mobile.*;
import itest.cloud.ibm.page.element.ca.mobile.BoardElement;
import itest.cloud.ibm.scenario.IbmScenarioStep;
import itest.cloud.ibm.topology.CaMobileApplication;
import itest.cloud.ibm.topology.IbmTopology;
import itest.cloud.scenario.error.InvalidOutcomeError;
/**
 * Manage a scenario step of the Cognos Analytics Mobile application.
 * <p>
 * The step gives access to the current location which is either the page location or the current browser URL
 * in case no page was already stored.
 * </p>
 * <p>
 * Following actions are accessible in this page:
 * <ul>
 * </ul>
 * </p>
 * <p>
 * It also contains some other useful helper methods:
 * <ul>
 * <li>{@link #changeUser(IUser)}: Login as a new user.</li>
 * </ul>
 * </p>
 */
public class CaMobileScenarioStep extends IbmScenarioStep {

/**
 * Create a new board with a given name.
 *
 * @param name The name of the board. If <code>null</code> is provided as the value of this parameter,
 * a default name will be assigned to the newly created board.
 * @param force Specifies whether to delete and recreate the given board is matching board already exists.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The opened Boards Page with the newly created board as {@link BoardsPage}.
 */
protected BoardsPage createBoard(final String name, final boolean force, final IUser user) {
	final CaMobilePage currentPage = openCaMobilePage(null /*webPageClass*/, user);
	final BoardsManagementPage boardsManagementPage;

	// Check if the current page is the Boards Management Page.
	if(currentPage instanceof BoardsManagementPage) {
		// If so, the existence of this page implies that at least one board is available.
		boardsManagementPage = (BoardsManagementPage) currentPage;
	}
	else {
		final BoardsPage boardsPage;
		// Check if the current page is the Boards Page.
		if(currentPage instanceof BoardsPage) {
			boardsPage = (BoardsPage) currentPage;
		}
		// If the current page is not the Boards Page, open the Boards Page.
		else {
			boardsPage = openBoardsPage(user);
		}

		// If reached here, it implies that the currently opened is the Boards Page.
		// Check if no boards exist in it.
		if(!boardsPage.doBoardsExist()) {
			// if So, create a new board.
			boardsPage.createBoard(name);
			return boardsPage;
		}

		// If boards exist, check if the currently opened board in the Boards Page is the desired one.
		if(boardsPage.getBoardName().equals(name)) {
			// If so, simply return the currently opened board if the caller requested to do so via the force parameter in this situation.
			if(!force) return boardsPage;
			// Otherwise, delete the currently opened page and create a new board with the same name and return it to the caller.
			boardsPage.deleteBoard();
			boardsPage.createBoard(name);
		}

		// If the currently opened board in the Boards Page is not the desired one,
		// open the Boards Management Page to look for it among the existing boards.
		boardsManagementPage = boardsPage.openBoardsManagementPage();
	}

	// Look for the desired board among the existing ones in the Boards Management Page.
	final BoardElement boardElement = boardsManagementPage.getBoardElement(name, false /*fail*/);
	// If the given board already exists in the Boards Management Page.
	if(boardElement != null) {
		if(!force) {
			// Simply open and return it if the caller requested to do so via the force parameter in this situation.
			println("	  -> A board named '" + name + "' already existed and therefore, reused for this scenario.");
			return boardElement.openBoard();
		}
		// Otherwise, delete the currently opened page and create a new board with the same name and return it to the caller in the next step.
		boardsManagementPage.deleteBoard(name);
	}
	// If the given board does not exist in the Boards Management Page or the existed board has been deleted in the previous step,
	// close the Boards Management Page and navigate back to the Boards Page.
	final BoardsPage boardsPage = boardsManagementPage.close();
	// Create a new board from the Boards Page.
	boardsPage.createBoard(name);

	return boardsPage;
}

/**
 * Delete a given board.
 *
 * @param name The name of the board as {@link String}.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 */
protected void deleteBoard(final String name, final IUser user) {
	final CaMobilePage currentPage = openCaMobilePage(null /*webPageClass*/, user);
	// Check if the current page is the Boards Page.
	if(currentPage instanceof BoardsPage) {
		// If so, check if the currently opened board is the desired one.
		final BoardsPage boardPage = (BoardsPage) getCurrentPage();
		if(boardPage.getBoardName().equals(name)) {
			// If so, delete the board via the context menu of the Board Page.
			boardPage.deleteBoard();
			return;
		}
	}

	// Otherwise, delete the board via the context menu of the Board Management Page.
	final BoardsManagementPage boardsManagementPage = openBoardsManagementPage(user);
	boardsManagementPage.deleteBoard(name);
}

/**
 * @see IbmTopology#getCaMobileApplication()
 */
protected CaMobileApplication getCaApplication() {
	return getTopology().getCaMobileApplication();
}

/**
 * {@inheritDoc}
 *
 * @return The page as a subclass of {@link CaMobilePage}.
 * May be null if no page was stored neither in current test nor in previous one.
 */
@Override
protected CaMobilePage getCurrentPage() {
	return (CaMobilePage) super.getCurrentPage();
}

/**
 * Open a given board.
 *
 * @param name The name of the board as {@link String}.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The opened Boards Page as {@link BoardsPage}.
 */
protected BoardsPage openBoard(final String name, final IUser user) {
	final CaMobilePage page = openCaMobilePage(BoardsPage.class, user);

	// If the current page is null, it implies that the particular page is the Boards Page.
	if (page == null) {
		final BoardsPage boardPage = (BoardsPage) getCurrentPage();
		// Check if the currently opened board is the desired one.
		if(boardPage.getBoardName().equals(name)) {
			// If so, simply return it.
			return boardPage;
		}
	}

	final BoardsManagementPage boardsManagementPage = openBoardsManagementPage(user);
	final BoardsPage boardsPage = boardsManagementPage.openBoard(name);

	// Check if the correct board has been opened in the Boards Page.
	final String boardName = boardsPage.getBoardName();
	if(!boardName.equals(name)) {
		throw new InvalidOutcomeError("Board named '" + name + "' was expected to open, but one with name '" + boardName + "' opened instead.");
	}

	return boardsPage;
}

private BoardsManagementPage openBoardsManagementPage(final IUser user) {
	final CaMobilePage page = openCaMobilePage(BoardsManagementPage.class, user);

	// If the current page is null, it implies that the particular page is the desired one.
	if (page == null) {
		return (BoardsManagementPage) getCurrentPage();
	}

	final BoardsPage boardsPage = openBoardsPage(user);
	return boardsPage.openBoardsManagementPage();
}

/**
 * Open the Boards Page.
 *
 * @param user
 *
 * @return The opened Boards Page as {@link BoardsPage}.
 */
protected BoardsPage openBoardsPage(final IUser user) {
	final CaMobilePage page = openCaMobilePage(BoardsPage.class, user);

	// If the current page is null, it implies that the particular page is the desired one.
	if (page == null) {
		return (BoardsPage) getCurrentPage();
	}

	return ((CaMobileNavigablePage) page).openBoardsPage();
}

private CaMobilePage openCaMobilePage(final Class<? extends CaMobilePage> webPageClass, final IUser user) {
	// Change the user to the given
	changeUser(user);

	CaMobilePage currentPage = getCurrentPage();

	// Check if the application has just been started.
	if (currentPage == null) {
		// If so, the home page is opened by default in the freshly started application.
		// Therefore, create the page instance and return.
		currentPage = getPageUsingBrowser(getConfig(), (IbmUser) user, BoardsPage.class);
	}

	// If a desired page is specified, perform the following operations.
	if (webPageClass != null) {
		// Check whether the current page matches the given one.
		if(webPageClass.isInstance(currentPage) && currentPage.matchPage()) {
			// If so, return null to indicate to the caller that no further
			// actions are required to open the particular web page since
			// the user is already in the correct web page.
			return null;
		}

		// Check whether the current page would block navigation via the Bottom Menu.
		if(currentPage instanceof CaMobileNonNavigablePage) {
			// If so, navigate away from such a page to another allowing navigation via the Bottom Menu.
			currentPage = ((CaMobileNonNavigablePage) currentPage).close();

			// Check whether the new current page matches the given one.
			if(webPageClass.isInstance(currentPage) && currentPage.matchPage()) {
				// If so, return null to indicate to the caller that no further
				// actions are required to open the particular web page since
				// the user is already in the correct web page.
				return null;
			}
		}
	}

	return currentPage;
}

/**
 * Open a chart in a board.
 *
 * @param board The name of the board containing the desired chart as {@link String}.
 * @param chart The name of the chart as {@link String}.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The opened Chart Page as {@link ChartPage}.
 */
protected ChartPage openChart(final String board, final String chart, final IUser user) {
	final CaMobilePage page = openCaMobilePage(ChartPage.class, user);

	// If the current page is null, it implies that the particular page is a Chart Page.
	if (page == null) {
		final ChartPage chartPage = (ChartPage) getCurrentPage();
		// Check if the corresponding chart has the correct name and belong to the desired board.
		if(chartPage.getBoardName().equals(board) && chartPage.getChartName().equals(chart)) {
			// If reached here, it implies that the currently opened Chart Page belong to the desired chart.
			// Therefore, simply return it.
			return chartPage;
		}
	}

	final BoardsPage boardsPage = openBoard(board, user);
	return boardsPage.openChart(board, chart);
}
}