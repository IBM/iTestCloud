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

import static itest.cloud.ibm.page.element.wxbi.WxbiVisualizationElement.VISUALIZATION_ELEMENT_LOCATOR;
import static itest.cloud.ibm.page.element.wxbi.conversation.WxbiQuestionContextMenuElement.QUESTION_CONTEXT_MENU_LOCATOR;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.ibm.page.element.wxbi.WxbiVisualizationElement;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class represents a message element representing a question or answer in {@link WxbiConversationEditorElement}.
 * <p>
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #getContextMenuElement()}: Return the context menu element.</li>
 * <li>{@link #getMessage()}: Return the textual message.</li>
 * <li>{@link #getTime(boolean)}: Return the time of the message.</li>
 * <li>{@link #getVisualizationElement(boolean)}: Return the chart element associated with the message.</li>
 * <li>{@link #openContextMenuElement()}: Return the visualization element associated with the message.</li>
 * <li>{@link #isContextMenuAvailable()}: Specifies whether a context menu is available for this message element.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * </p>
 */
public class WxbiMessageElement extends IbmElementWrapper {

public WxbiMessageElement(final ElementWrapper parent, final BrowserElement element) {
	super(parent, element);
}

/**
 * Return the context menu element.
 *
 * @return The context menu element as {@link WxbiQuestionContextMenuElement}.
 */
public WxbiQuestionContextMenuElement getContextMenuElement() {
	return new WxbiQuestionContextMenuElement(this);
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

/**
 * Return the textual message.
 *
 * @return The textual message as {@link String}.
 */
public String getMessage() {
	final BrowserElement messageElement = waitForElement(
		By.xpath(".//*[@class='ripasso-text-message'] | .//*[contains(@class,'ripasso-message-list-view-item-text') or contains(@class,'ripasso-message-list-view-text')]//p/.."), tinyTimeout(), false /*fail*/, false /*displayed*/);
	return (messageElement != null) ? messageElement.getText() : null;
}

/**
 * Return the time of the message.
 *
 * @return The time of the message as {@link String}.
 */
public String getTime(final boolean fail) {
	final BrowserElement timeElement =
		waitForElement(By.xpath(".//*[@class='ripasso-message-list-view-time']"), fail ? timeout() : tinyTimeout(), fail);

	return (timeElement != null) ? timeElement.getText() : null;
}

@Override
protected By getTitleElementLocator() {
	return null;
}

/**
 * Return the  visualization element associated with the message.
 *
 * @param fail Specify whether to fail if a visualization element could not be found.
 *
 * @return The visualization element as {@link WxbiVisualizationElement} or <code>null</code> if a visualization element
 * could not be found and specified not to fail in this situation.
 */
public WxbiVisualizationElement getVisualizationElement(final boolean fail) {
	final BrowserElement visualizationWebElement = waitForElement(VISUALIZATION_ELEMENT_LOCATOR, fail ? timeout() : tinyTimeout(), fail);

	if(visualizationWebElement == null) return null;

	// Wait for the visualization element to load in the message.
	final WxbiVisualizationElement visualizationElement = new WxbiVisualizationElement(this, visualizationWebElement);
	visualizationElement.waitForLoadingEnd();

	return visualizationElement;
}

/**
 * Specifies whether a context menu is available for this message element.
 *
 * @return <code>true</code> if a context menu is available for this message element or <code>false</code> otherwise.
 */
public boolean isContextMenuAvailable() {
	return waitForElement(QUESTION_CONTEXT_MENU_LOCATOR, tinyTimeout(), false /*fail*/) != null;
}

/**
 * Opens the context menu element.
 *
 * @return The opened context menu element as {@link WxbiQuestionContextMenuElement}.
 */
public WxbiQuestionContextMenuElement openContextMenuElement() {
	final WxbiQuestionContextMenuElement contextMenuElement = getContextMenuElement();
	contextMenuElement.expand();

	return contextMenuElement;
}
}