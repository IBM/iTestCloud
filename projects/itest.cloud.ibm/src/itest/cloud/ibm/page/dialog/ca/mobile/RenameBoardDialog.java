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
package itest.cloud.ibm.page.dialog.ca.mobile;

import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import itest.cloud.ibm.page.ca.mobile.BoardsPage;
import itest.cloud.page.Page;

/**
 * This class represents the New Board dialog opened from {@link BoardsPage} all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #renameBoard(String)}: Rename the board to a given.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getPrimaryButtonText()}: Return the text of the primary button.</li>
 * </ul>
 * </p>
 */
public class RenameBoardDialog extends CaMobileNativeConfirmationInputDialog {

public RenameBoardDialog(final Page page) {
	super(page);
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote("Rename"));
}

@Override
protected String getPrimaryButtonText() {
	return "Done";
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
	setName(newName);
	// Choose to create the board by suppressing the dialog.
	close();
}

private void setName(final String name) {
	setInput(name);
}
}