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
import static itest.cloud.scenario.ScenarioUtil.println;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;
import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.page.element.ca.mobile.BoardContextMenuElement;
import itest.cloud.page.element.AlertElement;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * This class represents a page with functionality to manage various aspects of boards.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #boardExists(String)}: Specifies whether a given board exists.</li>
 * <li>{@link #deleteBoard(String)}: Delete a given board.</li>
 * <li>{@link #openBoard(String)}: Open a given board.</li>
 * <li>{@link #renameBoard(String, String)}: Rename a specific board to a given.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getTitleElementLocator()}: Return the title element locator.</li>
 * </ul>
 * </p>
 */
public class BoardsManagementPage extends BoardsListPage {

public BoardsManagementPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

/**
 * Specifies whether a given board exists.
 *
 * @param name The name of the board as {@link String}.
 *
 * @return <code>true</code> if the given board exists or <code>false</code> otherwise.
 */
public boolean boardExists(final String name) {
	final BrowserElement boardElement = getBoardElement(name, false /*fail*/);

	return (boardElement != null);
}

/**
 * Delete a given board.
 *
 * @param name The name of the board as {@link String}.
 */
public void deleteBoard(final String name) {
	// Look for the desired board among the existing ones.
	// If the given board does not exists, simply return after displaying an appropriate message.
	if(!boardExists(name)) {
		println("	  -> A board named '" + name + "' did not exist and therefore, no attempt was made to delete it.");
		return;
	}
	// Delete the board via the context menu of the board element.
	final BoardContextMenuElement contextMenuElement = getContextMenuElementOfBoard(name);
	contextMenuElement.deleteBoard();

	// Wait for an alert to appear indicating that the deletion was successful or not.
	final AlertElement alertElement = getAlertElement(getBoardsDeletionAlertPattern(name), true /*fail*/);
	// Make sure the alert indicates that the deletion was successful.
	if(alertElement.getStatus() != Success) throw new WaitElementTimeoutError("Deleting board '" + name + "' failed with the following message: " + alertElement.getMessage());
	// Dismiss the alert.
	alertElement.close();

	// Wait for the deleted board to disappear from the Boards Management Page.
	final long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
	while (boardExists(name)) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The deleted board '" + name + "' remained in the Boards Management Page until timeout '" + timeout() + "'s had reached.");
		}
	}
}

private BoardContextMenuElement getContextMenuElementOfBoard(final String name) {
	return new BoardContextMenuElement(this, getContextMenuExpansionElementOfBoard(name, true /*fail*/));
}

private BrowserElement getContextMenuExpansionElementOfBoard(final String name, final boolean fail) {
	search(name);
	return waitForElement(AppiumBy.accessibilityId("icon-button-menu-" + name), fail ? timeout() : tinyTimeout(), fail, true /*displayed*/, false /*single*/);
}

@Override
protected By getTitleElementLocator() {
	// TODO Find a more appropriate xpath since the following duplicates the xpath of the close button.
	return By.xpath("//*[@*='icon-button-boards-header-close']/../*[1]");
}

/**
 * Open a given board.
 *
 * @param name The name of the board as {@link String}.
 *
 * @return The opened Boards Page as {@link BoardsPage}.
 */
public BoardsPage openBoard(final String name) {
	final BrowserElement boardElement = getBoardElement(name, true /*fail*/);

	return openMobilePageUsingLink(boardElement, BoardsPage.class);
}

/**
 * Rename a specific board to a given.
 *
 * @param currentName The current name of the board as {@link String}.
 * @param newName The new name to be assigned to the board.
 */
public void renameBoard(final String currentName, final String newName) {
	// Rename the board via the context menu of the board element.
	final BoardContextMenuElement contextMenuElement = getContextMenuElementOfBoard(currentName);
	contextMenuElement.renameBoard(newName);

	// Wait for the new name of the board to appear in the Boards Management Page.
	final long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
	while (!boardExists(newName)) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The new name '" + newName + "' of the board did not appear in the Boards Management Page until timeout '" + timeout() + "'s had reached.");
		}
	}
}
}