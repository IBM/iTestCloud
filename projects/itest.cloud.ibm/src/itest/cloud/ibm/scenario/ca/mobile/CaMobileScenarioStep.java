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

import static itest.cloud.entity.AlertStatus.Success;
import static itest.cloud.page.Page.getPageUsingBrowser;
import static itest.cloud.scenario.ScenarioUtil.println;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import itest.cloud.config.IUser;
import itest.cloud.ibm.config.IbmUser;
import itest.cloud.ibm.entity.mobile.AssetContext;
import itest.cloud.ibm.page.ca.mobile.*;
import itest.cloud.ibm.page.element.ca.mobile.CaContentTabElement;
import itest.cloud.ibm.scenario.IbmScenarioStep;
import itest.cloud.ibm.topology.CaMobileApplication;
import itest.cloud.ibm.topology.IbmTopology;
import itest.cloud.page.element.AlertElement;
import itest.cloud.scenario.error.InvalidOutcomeError;
import itest.cloud.scenario.error.WaitElementTimeoutError;
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
		if(!boardsPage.boardsExist()) {
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
			return boardsPage;
		}

		// If the currently opened board in the Boards Page is not the desired one,
		// open the Boards Management Page to look for it among the existing boards.
		boardsManagementPage = boardsPage.openBoardsManagementPage();
	}

	// Look for the desired board among the existing ones in the Boards Management Page.
	// If the given board already exists in the Boards Management Page.
	if(boardsManagementPage.boardExists(name)) {
		if(!force) {
			// Simply open and return it if the caller requested to do so via the force parameter in this situation.
			println("	  -> A board named '" + name + "' already existed and therefore, reused for this scenario.");
			return boardsManagementPage.openBoard(name);
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
		final BoardsPage boardsPage = (BoardsPage) getCurrentPage();
		// Check if the currently opened board is the desired one.
		if(boardsPage.getBoardName().equals(name)) {
			// If so, simply return it.
			return boardsPage;
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

	return ((CaMobilePageWithBottomTabList) page).openBoards();
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
		if(currentPage instanceof CaMobilePageWithoutBottomTabList) {
			// If so, navigate away from such a page to another allowing navigation via the Bottom Menu.
			currentPage = ((CaMobilePageWithoutBottomTabList) currentPage).close();

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
protected ChartPage openChartInBoard(final String board, final String chart, final IUser user) {
	final CaMobilePage page = openCaMobilePage(ChartPage.class, user);

	// If the current page is null, it implies that the particular page is a Chart Page.
	if (page == null) {
		final ChartPage chartPage = (ChartPage) getCurrentPage();
		// Check if the corresponding chart has the correct name and belong to the desired board.
		if(chartPage.getBoardName().equals(board) && chartPage.getChartName().equals(chart)) {
			// If reached here, it implies that the currently opened Chart Page belong to the desired board.
			// Therefore, simply return it.
			return chartPage;
		}
	}

	final BoardsPage boardsPage = openBoard(board, user);
	return boardsPage.openChart(chart);
}

private ContentNavigationPage openContentNavigationPage(final IUser user) {
	CaMobilePage page = openContent(user);

	// Looping should continue as long as the current page is an Asset Page and stop after reaching the Content Navigation Page.
	while (page instanceof AssetPage) {
		final AssetPage assetPage = (AssetPage) page;
		page = assetPage.back();
	}

	// Once reached here, it implies that the current page is the Content Navigation Page.
	return (ContentNavigationPage) page;
}

private CaMobilePage openContent(final IUser user) {
	// Get the currently opened page.
	final CaMobilePage currentPage = openCaMobilePage(null /*webPageClass*/, user);

	// Check if the currently opened page is a content page.
	if(currentPage instanceof ContentPage) {
		// If so, return the currently opened page.
		return currentPage;
	}

	// Otherwise, open the Content context via the bottom tab list.
	return ((CaMobilePageWithBottomTabList) currentPage).openContent();
}

/**
 * Open a dashboard in a context.
 *
 * @param context The context of the dashboard as {@link AssetContext} where it should be searched in.
 * @param dashboard The name of the dashboard as {@link String}.
 * @param force Specifies whether to reopen the dashboard if the desired dashboard is currently open.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The opened Dashboard Page as {@link BoardsPage}.
 */
protected DashboardPage openDashboard(final AssetContext context, final String dashboard, final boolean force, final IUser user) {
	// Get the currently opened page.
	CaMobilePage currentPage = openCaMobilePage(null /*webPageClass*/, user);

	// If the currently open page is not a Dashboard Page, open the Content context via the bottom tab list.
	if (!(currentPage instanceof DashboardPage)) {
		currentPage = ((CaMobilePageWithBottomTabList) currentPage).openContent();
	}

	// Check if the currently opened page is a Dashboard Page.
	if (currentPage instanceof DashboardPage) {
		final DashboardPage dashboardPage = (DashboardPage) currentPage;

		// Check if the current page is the desired one.
		if(!force && dashboardPage.getName().equals(dashboard)) {
			// If the currently opened page represents the desired dashboard and its opening is not forced, then simply return it's page.
			return dashboardPage;
		}
	}

	final ContentNavigationPage contentPage = openContentNavigationPage(user);
	final CaContentTabElement contentTabElement = contentPage.openTab(context);
	contentPage.search(dashboard);

	return contentTabElement.openAsset(dashboard, DashboardPage.class);
}

///**
// * Open a given dashboard.
// *
// * @param path The path of the dashboard as {@link String}.
// * @param user The user to perform this operation.
// * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
// * <li>Samples/* Get started/dashboards/My first dashboard</li>
// *
// * @return The opened Dashboard Page as {@link BoardsPage}.
// */
//protected DashboardPage openDashboard(final ContentTab contentTab, final String path, final boolean force, final IUser user) {
//	final String[] pathItems = splitPath(path);
//	final String dashboard = pathItems[pathItems.length -1];
//	CaMobilePage page = openCaMobilePage(DashboardPage.class, user);
//
//	if (page == null) {
//		// If the page variable is null, it implies that the current page is a Dashboard Page.
//		final DashboardPage dashboardPage = (DashboardPage) getCurrentPage();
//
//		// Check if the current page is the desired one.
//		if(!force && dashboardPage.getName().equals(dashboard)) {
//			// If the current page represents the desired dashboard and its opening is not forced, then simply return it's page.
//			return dashboardPage;
//		}
//
//		page = dashboardPage.back();
//	}
//
//	// The first path item is selected in the Content Page. The subsequent path items are selected in the Folder Pages.
//	// The startIndexForFolderNavigation variable is used to start path items (navigate in) the Folder Pages.
//	// Therefore, initialize the startIndexForFolderNavigation variable to 1 as the index 0 is consumed in the Content Page.
//	// The startIndexForFolderNavigation variable can then be appropriately updated if the currently opened page is
//	// a Folder Page and the corresponding folder is in the path.
//	int startIndexForFolderNavigation = 1;
//	if(page instanceof FolderPage) {
//		// If reached here, it implies that the current page is a Folder Page representing an opened folder.
//		final List<String> pathItemsList = asList(pathItems);
//
//		do {
//			// Check if the currently opened folder is in the given path.
//			FolderPage folderPage = (FolderPage) page;
//			String folderName = folderPage.getName();
//			final int index = pathItemsList.lastIndexOf(folderName);
//
//			if(index >= 0) {
//				// If reached here, it implies that the currently opened folder is in the path.
//				// Therefore, set the startIndexForFolderNavigation variable appropriately to start navigating on
//				// the path from the currently open folder.
//				startIndexForFolderNavigation = index + 1;
//				break;
//			}
//
//			// If reached here, it implies that the currently opened folder is not in the path. Therefore, navigate
//			// back to the previous page and check if the previous page is a Folder Page and the corresponding folder in in the path.
//			page = folderPage.back();
//			// Looping should continue as long as the current page is a Folder Page and stop after reaching the Content Page.
//		} while (page instanceof FolderPage);
//
//	}
//	else {
//		page = openContentPage(user);
//	}
//
//	// If reached here, it implies that the current page is the Content Page or a Folder Page.
//	if(page instanceof ContentPage) {
//		// If the current page is the Content Page, open the desired tab in it.
//		final ContentPage contentPage = (ContentPage) page;
//		final CaContentTabElement contentTabElement = contentPage.openTab(contentTab);
//		// Check if the desired dashboard is to be found in a folder path or in the tab itself.
//		if(pathItems.length > 1) {
//			// If the desired dashboard is to be found in a folder path, open its parent folder in a Folder Page.
//			page = contentTabElement.openAsset(pathItems[0], FolderPage.class);
//		}
//		else {
//			// If the desired dashboard is to be found in the Recent or My Content tabs,
//			// open it via the tab itself and return the opened page.
//			return contentTabElement.openAsset(dashboard, DashboardPage.class);
//		}
//	}
//
//	FolderPage folderPage = (FolderPage) page;
//
//	// Navigate through the remaining sub-folder path path via appropriate Folder Pages.
//	for (int i = startIndexForFolderNavigation; i < pathItems.length - 1; i++) {
//		folderPage = folderPage.openAsset(pathItems[i], FolderPage.class);
//	}
//
//	// Finally open the dashboard.
//	return folderPage.openAsset(dashboard, DashboardPage.class);
//}

/**
 * Pin a chart from a dashboard in a board.
 *
 * @param dashboard The name of the dashboard containing the chart to pin as {@link String}.
 * @param chart The name of the chart to pin {@link String}.
 * @param board The name of the board to pin the chart in as {@link String}.
 * @param force Specifies whether to unpin and pin the chart again if a matching chart already exists in the board.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The Boards Page as {@link BoardsPage} containing the pinned chart in the currently opened board.
 */
protected BoardsPage pinChartFromDashboardInBoard(final AssetContext context, final String dashboard, final String chart, final String board, final boolean force, final IUser user) {
	BoardsPage boardsPage = openBoard(board, user);

	// Check if the chart already exists in the board.
	if(boardsPage.chartExist(chart)) {
		// If the chart exists in the board and the caller requested not to forcefully pin the chart in the board in this situation,
		// simply return the currently opened Boards Page.
		if(!force) {
			return boardsPage;
		}
		// If the chart exists in the board, but the caller requested to forcefully pin the chart in the board in this situation,
		// simply unpin the existing chart from the board.
		boardsPage.unpinChart(chart);
	}

	// Open the dashboard and pin the chart in the board.
	DashboardPage dashboardPage = openDashboard(context, dashboard, false /*force*/, user);
	final BoardsSelectionPage boardsSelectionPage = dashboardPage.pinChartElement(chart);
	dashboardPage = boardsSelectionPage.selectBoard(board);

	// Wait for an alert to appear indicating that the pinning was successful or not.
	final AlertElement alertElement = dashboardPage.getAlertElement(
		compile(quote("Pin was successfully added to \"" + board + "\" board")), true /*fail*/);
	// Make sure the alert indicates that the pinning was successful.
	if(alertElement.getStatus() != Success) throw new WaitElementTimeoutError("Pinning the chart '" + chart + "' from the dashboard '" + dashboard + "' in the board '" + board + "' failed with the following message: " + alertElement.getMessage());
	// Dismiss the alert.
	alertElement.close();

	// Validate if the pinned chart now appears in the  board.
	boardsPage = openBoard(board, user);
	// Wait for the pinned chart to appear in the board.
	final int timeout = boardsPage.timeout();
	final long timeoutMillis = timeout * 1000 + System.currentTimeMillis();
	while (!boardsPage.chartExist(chart)) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The newly pinned chart '" + chart + "' had not appeared in the board '" + board + "' when the timeout '" + timeout + "'s was reached.");
		}
	}

	return boardsPage;
}

/**
 * Rename a specific board to a given.
 *
 * @param currentName The current name of the board as {@link String}.
 * @param newName The new name to be assigned to the board.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 */
protected void renameBoard(final String currentName, final String newName, final IUser user) {
	final CaMobilePage currentPage = openCaMobilePage(null /*webPageClass*/, user);
	// Check if the current page is the Boards Page.
	if(currentPage instanceof BoardsPage) {
		// If so, check if the currently opened board is the desired one.
		final BoardsPage boardPage = (BoardsPage) getCurrentPage();
		if(boardPage.getBoardName().equals(currentName)) {
			// If so, rename the board via the context menu of the Board Page.
			boardPage.renameBoard(newName);
			return;
		}
	}

	// Otherwise, rename the board via the context menu of the Board Management Page.
	final BoardsManagementPage boardsManagementPage = openBoardsManagementPage(user);
	boardsManagementPage.renameBoard(currentName, newName);
}

/**
 * Unpin a chart from a board.
 *
 * @param board The name of the board to unpin the chart from as {@link String}.
 * @param chart The name of the chart to unpin {@link String}.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 */
protected void unpinChartFromBoard(final String board, final String chart, final IUser user) {
	BoardsPage boardsPage = openBoard(board, user);

	if(!boardsPage.chartExist(chart)) {
		println("	  -> A chart named '" + chart + "' did not exist in the board '" + board + "'. Therefore, no attempt was made to unpin it from the board.");
		return;
	}

	// If the chart exists in the board, unpin the chart from the board.
	boardsPage.unpinChart(chart);
}
}