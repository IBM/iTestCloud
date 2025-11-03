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
 * This class represents the Rename Board dialog opened from {@link BoardsPage}.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #createBoard(String)}: Create a new board with a given name.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getPrimaryButtonText()}: Return the text of the primary button.</li>
 * </ul>
 * </p>
 */
public class NewBoardDialog extends CaMobileNativeConfirmationInputDialog {

public NewBoardDialog(final Page page) {
	super(page);
}

/**
 * Create a new board with a given name.
 * <p>
 * This dialog will be automatically closed after creating the board.
 * </p>
 *
 * @param name The name of the board. If <code>null</code> is provided as the value of this parameter,
 * a default name will be assigned to the newly created board.
 */
public void createBoard(final String name) {
	if(name != null) setName(name);
	// Choose to create the board by suppressing the dialog.
	close();
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote("New board"));
}

@Override
protected String getPrimaryButtonText() {
	return "Create";
}

private void setName(final String name) {
	setInput(name);
}
}