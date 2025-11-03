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

import itest.cloud.ibm.page.dialog.ca.mobile.RenameBoardDialog;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;

/**
 * This class to represents the context menu element of a board where the element is only made available after clicking on the expansion element.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #deleteBoard()}: Delete the board.</li>
 * <li>{@link #renameBoard(String)}: Rename the board to a given.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class BoardContextMenuElement extends CaMobileContextMenuElement {

public BoardContextMenuElement(final Page page, final BrowserElement expansionElement) {
	super(page, expansionElement);
}

/**
 * Delete the board.
 */
public void deleteBoard() {
	// Select the 'Delete' option from the context menu.
	select("Delete");
	// Confirm the deletion by accessing the appropriate alert.
	getPage().acceptAlert("Confirm Deletion", true /*fail*/);
	// TODO: Accept the alert confirming the deletion.
}

/**
 * Rename the board to a given.
 * <p>
 * This dialog will be automatically closed after renaming the board.
 * </p>
 *
 * @param newName The new name to be assigned to the board.
 */
public void renameBoard(final String newName) {
	// Select the 'Rename' option from the context menu by opening the 'Rename Board Dialog'.
	final RenameBoardDialog renameBoardDialog = selectByOpeningDialog("Delete", RenameBoardDialog.class);
	// Rename the board to the given.
	renameBoardDialog.renameBoard(newName);
}
}