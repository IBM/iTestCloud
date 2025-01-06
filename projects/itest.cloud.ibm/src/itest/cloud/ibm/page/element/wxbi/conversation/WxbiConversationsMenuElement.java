/*********************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
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

import static itest.cloud.util.ObjectUtil.matches;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.element.IbmExpandableElement;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.ScenarioFailedError;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * This class represents a generic menu element and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getConversationElement(String, boolean)}: Return the a given conversation element.</li>
 * <li>{@link #isExpandable()}: Returns whether the current wrapped web element is expandable or not.</li>
 * <li>{@link #isExpanded()}: Returns whether the current wrapped web element is expanded or not.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpandableAttribute()}: Return the expandable attribute.</li>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * </p>
 */
public class WxbiConversationsMenuElement extends IbmExpandableElement {

public WxbiConversationsMenuElement(final Page page) {
	super(page, By.xpath("//aside[.//*[contains(@class,'conversations')]]"), By.xpath("//*[contains(@class,'sidebar-toolbar')]//button"));
}

/**
 * Create a new conversation.
 *
 * @return The newly created conversation element as {@link WxbiConversationElement}.
 */
public WxbiConversationElement createConversation() {
	// Record info of the existing conversation elements before creating a new one.
	List<WxbiConversationElement> existingConversationElements = getConversationElements();

	// Create a new conversation element.
	clickButton(By.xpath(".//button[text()='New Conversation']"));

	// Wait for the new conversation element to appear.
	final int existingConversationsCount = existingConversationElements.size();
	List<WxbiConversationElement> currentConversations;
	long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
	while ((currentConversations = getConversationElements()).size() <= existingConversationsCount) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("A new conversation did not appear in the conversation menu before the '" + timeout() + "' seconds has reached");
		}
	}

	// Return the newly created conversation element, which should be the first element in the conversation list.
	return currentConversations.get(0);
}

//private SearchBarElement getSearchBarElement() {
//	return new SearchBarElement(this);
//}

/**
 * Return the a given conversation element.
 *
 * @param name The name of the conversation as {@link String}.
 * If <code>null</code> is provided as the value of this parameter, the most recent conversation element will be returned instead.
 * @param fail Specifies whether to fail if a matching conversation element could not be found.
 *
 * @return The conversation element with the given name as {@link WxbiConversationElement} or
 * <code>null</code> if the given conversation could not be found and instructed not to fail in such a situation.
 */
public WxbiConversationElement getConversationElement(final String name, final boolean fail) {
//	// Search for the given conversation element if a name is specified.
//	if(name != null) {
//		final SearchBarElement searchBarElement = getSearchBarElement();
//		searchBarElement.search(name);
//		waitWhileBusy();
//	}

	return getConversationElement(compile(quote(name)), fail);
}

/**
 * Return the conversation element matching a given pattern.
 *
 * @param pattern The pattern of the name of the conversation as {@link Pattern}.
 * If <code>null</code> is provided as the value of this parameter, the most recent conversation element will be returned instead.
 * @param fail Specifies whether to fail if a matching conversation element could not be found.
 *
 * @return The conversation element matching the given pattern as {@link WxbiConversationElement}.
 */
public WxbiConversationElement getConversationElement(final Pattern pattern, final boolean fail) {
	final List<WxbiConversationElement> conversationElements = getConversationElements(pattern, fail);

	return conversationElements.isEmpty() ? null : conversationElements.get(conversationElements.size()-1);
}

/**
 * Return the conversation elements.
 *
 * @return The conversation elements as {@link List} of {@link WxbiConversationElement}.
 */
public List<WxbiConversationElement> getConversationElements() {
	return getConversationElements(null /*pattern*/, false /*fail*/);
}

/**
 * Return the conversation elements matching a given pattern.
 *
 * @param pattern The pattern of the name of the conversation as {@link Pattern}.
 * If <code>null</code> is provided as the value of this parameter, all the conversation elements will be returned instead.
 * @param fail Specifies whether to fail if a matching conversation element could not be found.
 *
 * @return The conversation elements matching the given pattern as {@link List} of {@link WxbiConversationElement}.
 */
private List<WxbiConversationElement> getConversationElements(final Pattern pattern, final boolean fail) {
//	// Clear the search filter if a pattern is not specified.
//	if(pattern == null) {
//		final SearchBarElement searchBarElement = getSearchBarElement();
//		searchBarElement.resetFilter();
//		waitWhileBusy();
//	}

	long timeoutMillis = (fail ? timeout() : tinyTimeout()) * 1000 + System.currentTimeMillis();
	while (true) {
		final List<BrowserElement> conversationWebElements =
			waitForElements(By.xpath(".//*[contains(@class,'list-item')]"), tinyTimeout(), false /*fail*/);
		final List<WxbiConversationElement> conversationElements = new ArrayList<WxbiConversationElement>(conversationWebElements.size());

		for (BrowserElement conversationWebElement : conversationWebElements) {
			if(pattern != null) {
				final BrowserElement textElement = conversationWebElement.waitForElement(By.xpath(".//*[contains(@class,'text')]"));

				if(matches(pattern, textElement.getText())) {
					conversationElements.add(new WxbiConversationElement(this, conversationWebElement));
				}
			}
			else {
				conversationElements.add(new WxbiConversationElement(this, conversationWebElement));
			}
		}

		if(!conversationElements.isEmpty()) return conversationElements;

		if (System.currentTimeMillis() > timeoutMillis) {
			if(fail) throw new WaitElementTimeoutError("A new message did not appear in the conversation editor before the '" + timeout() + "' seconds has reached");
			return conversationElements;
		}
	}
}

@Override
protected String getExpandableAttribute() {
	throw new ScenarioFailedError("This method should never be called.");
}

@Override
protected Pattern getExpectedTitle() {
	return Pattern.compile(Pattern.quote("Conversations"));
}

@Override
protected By getTitleElementLocator() {
	return By.xpath(".//*[contains(@class,'content-title')]");
}

@Override
public boolean isExpandable() throws ScenarioFailedError {
	return true;
}

@Override
public boolean isExpanded() throws ScenarioFailedError {
	return !this.element.getClassAttribute().contains("close");
}
}