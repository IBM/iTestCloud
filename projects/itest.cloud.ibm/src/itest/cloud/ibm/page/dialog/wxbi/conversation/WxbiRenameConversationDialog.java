/*********************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
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
package itest.cloud.ibm.page.dialog.wxbi.conversation;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.dialog.IbmConfirmationDialog;
import itest.cloud.page.Page;
import itest.cloud.scenario.error.InvalidOutcomeError;

/**
 * This class represents the dialog presented when attempt to rename a conversation and manages its actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #rename(String)}: Rename the conversation.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getPrimaryButtonText()}: Return the text of the primary button.</li>
 * <li>{@link #waitForLoadingEnd()}: Wait for loading of the element to complete.</li>
 * </ul>
 * </p>
 */
public class WxbiRenameConversationDialog extends IbmConfirmationDialog {

	private static final By RENAME_INPUT_LOCATOR = By.xpath(".//*[@id='edit-conversation-title']");

public WxbiRenameConversationDialog(final Page page, final String... data) {
	super(page, data);
}

private String getCurrentName() {
	return waitForElement(RENAME_INPUT_LOCATOR).getAttributeValue("value");
}

private String getExpectedName() {
	return this.data[0];
}

@Override
protected Pattern getExpectedTitle() {
	return Pattern.compile(Pattern.quote("Rename conversation"));
}

@Override
protected String getPrimaryButtonText() {
	return "Save";
}

/**
 * Rename the conversation.
 * <p>
 * This dialog will be suppressed automatically at the conclusion of this method.
 * </p>
 *
 * @param newName The new name of the conversation.
 */
public void rename(final String newName) {
	typeText(RENAME_INPUT_LOCATOR, newName);
	close();
}

@Override
public void waitForLoadingEnd() {
	super.waitForLoadingEnd();
	// Validate the existing name of the conversation specified in the dialog.
	final String currentName = getCurrentName();
	final String expectedName = getExpectedName();
	if(!currentName.equals(expectedName)) throw new InvalidOutcomeError("The name of the conversation is expected to be '" + expectedName + "'. but specified as '" + currentName + "' in this dialog instead.");
}
}