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

import org.openqa.selenium.By;

import itest.cloud.ibm.page.ca.mobile.BoardsPage;
import itest.cloud.page.Page;

/**
 * This class represents the New Board dialog opened from {@link BoardsPage} all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #setInput(String)}: Enter a give text as the input of the dialog.</li>
 * </ul>
 * </p>
 */
public abstract class CaMobileNativeConfirmationInputDialog extends CaMobileNativeConfirmationDialog {

public CaMobileNativeConfirmationInputDialog(final Page page) {
	super(page);
}

/**
 * Enter a give text as the input of the dialog.
 *
 * @param input The text to be entered as the input for the dialog as {@link String}.
 */
protected void setInput(final String input) {
	typeText(By.xpath(".//*[@*='ca-textinput-modal-input']"), input);
}
}