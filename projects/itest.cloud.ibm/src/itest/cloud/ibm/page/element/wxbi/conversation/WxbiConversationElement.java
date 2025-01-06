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
package itest.cloud.ibm.page.element.wxbi.conversation;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.dialog.IbmDeleteDialog;
import itest.cloud.ibm.page.dialog.wxbi.conversation.WxbiRenameConversationDialog;
import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.ibm.page.element.wxbi.WxbiContextMenuElement;
import itest.cloud.ibm.page.wxbia.WxbiHomePage;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * This class defines and manages a conversation element in {@link WxbiConversationsMenuElement}.
 * <p>
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #delete()}: Delete the conversation.</li>
 * <li>{@link #getName()}: Return the name of the conversation.</li>
 * <li>{@link #isSelected()}: Specify whether this conversation element is selected.</li>
 * <li>{@link #open()}: .</li>
 * <li>{@link #rename(String)}: Rename the conversation.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * </p>
 */
public class WxbiConversationElement extends IbmElementWrapper {

	public static final String CONVERSATION_ELEMENT_TITLE_XPATH = ".//*[contains(@class,'text')]";

public WxbiConversationElement(final ElementWrapper parent, final BrowserElement element) {
	super(parent, element);
}

/**
 * Delete the conversation.
 */
public void delete() {
	final WxbiContextMenuElement contextMenuElement = getContextMenuElement();
	final IbmDeleteDialog deleteDialog = contextMenuElement.selectByOpeningDialog("Delete", IbmDeleteDialog.class, getName());
	deleteDialog.close();

}

private WxbiContextMenuElement getContextMenuElement() {
	return new WxbiContextMenuElement(getPage(),
		By.xpath("//ul[contains(@class,'cds--menu')]"), getElement(), null /*selectionLocator*/,
		By.xpath(".//*[contains(@class,'menu-item__label')]"));
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

/**
 * Return the name of the conversation.
 *
 * @return The name of the conversation as {@link String}.
 */
public String getName() {
	return waitForElement(By.xpath(CONVERSATION_ELEMENT_TITLE_XPATH)).getText();
}

@Override
protected WxbiHomePage getPage() {
	return (WxbiHomePage) super.getPage();
}

@Override
protected By getTitleElementLocator() {
	return null;
}

/**
 * Specify whether this conversation element is selected.
 *
 * @return <code>true</code> if this conversation element is selected or <code>false</code> otherwise.
 */
public boolean isSelected() {
	return this.element.getClassAttribute().contains("selected");
}

/**
 * Open the conversation in the conversation editor element.
 *
 * @return The opened conversation in the conversation editor element as {@link WxbiConversationEditorElement}.
 */
public WxbiConversationEditorElement open() {
	return openElementUsingLink(this.element, WxbiConversationEditorElement.class, getName());
}

/**
 * Rename the conversation.
 *
 * @param newName The new name of the conversation.
 */
public void rename(final String newName) {
	final WxbiContextMenuElement contextMenuElement = getContextMenuElement();
	final WxbiRenameConversationDialog renameConversationDialog =
		contextMenuElement.selectByOpeningDialog("Rename", WxbiRenameConversationDialog.class, getName());
	renameConversationDialog.rename(newName);

	// Verify whether the renaming is successful.
	String currentName;
	long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
	while (!(currentName = getName()).equals(newName)) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The name of the conversation is expected to be '" + newName + "' after it's been renamed, but specified as '" + currentName + "' in the conversation element instead.");
		}
	}
}
}