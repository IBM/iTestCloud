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

import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;

/**
 * This class to represents the context menu element of a board where the element is only made available after clicking on the expansion element.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getOptionElementLabel(BrowserElement)}: Return the label associated with a given option element.</li>
 * <li>{@link #selectOptionElement(BrowserElement)}: Select a given option element.</li>
 * </ul>
 * </p>
 */
public class BoardContextMenuElement extends CaMobileContextMenuElement {

public BoardContextMenuElement(final Page page, final BrowserElement expansionElement, final String... data) {
	super(page, expansionElement);
	this.data = data;
}

/**
 * Delete the board.
 */
public void deleteBoard() {
	// Select the 'Delete' option from the context menu.
	select("Delete");
	// Confirm the deletion by accessing the appropriate alert.
	getPage().acceptAlert("Confirm Deletion", true /*fail*/);
	// Accept the alert confirming the deletion.
}

//private String getBoardName() {
//	return this.data[0];
//}
}