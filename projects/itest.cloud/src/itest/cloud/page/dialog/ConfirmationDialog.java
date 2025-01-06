/*********************************************************************
 * Copyright (c) 2019, 2024 IBM Corporation and others.
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
package itest.cloud.page.dialog;

import org.openqa.selenium.By;

import itest.cloud.page.Page;

/**
 * This class represents a generic confirmation dialog and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getCloseButton(boolean)}: Return the xpath of the button to close the window.</li>
 * <li>{@link #getPrimaryButtonText()}: Return the text of the primary button.</li>
 * <li>{@link #getSecondaryButtonText()}: Return the text of the secondary button.</li>
 * </ul>
 * </p>
 */
public abstract class ConfirmationDialog extends Dialog {

public ConfirmationDialog(final Page page,final By findBy) {
	super(page, findBy);
}

public ConfirmationDialog(final Page page, final By findBy, final String... data) {
	super(page, findBy, data);
}

@Override
protected String getCloseButton(final boolean validate) {
	return ".//button[text()='" + (validate ? getPrimaryButtonText() : getSecondaryButtonText()) + "']";
}

/**
 * Return the text of the primary button.
 *
 * @return The text of the primary button.
 */
protected String getPrimaryButtonText() {
	return "Ok";
}

/**
 * Return the text of the secondary button.
 *
 * @return The text of the secondary button.
 */
protected String getSecondaryButtonText() {
	return "Cancel";
}
}