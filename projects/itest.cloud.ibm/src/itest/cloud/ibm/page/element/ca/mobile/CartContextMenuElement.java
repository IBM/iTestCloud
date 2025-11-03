/*********************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
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
 * This class to represents the context menu element of a chart where the element is only made available after clicking on the expansion element.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #unpinFromBoard()}: Unpin the chart from the board.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class CartContextMenuElement extends CaMobileContextMenuElement {

public CartContextMenuElement(final Page page, final BrowserElement expansionElement, final String... data) {
	super(page, expansionElement);
	this.data = data;
}

/**
 * Unpin the chart from the board.
 */
public void unpinFromBoard() {
	// Select the 'Unpin from board' option from the context menu.
	select("Unpin from board");
	// Confirm the deletion by accessing the appropriate alert.
	getPage().acceptAlert("Unpin Visualization", true /*fail*/);
	// TODO: Accept the alert confirming the unpinning.
}

//private String getChartName() {
//	return this.data[0];
//}
}