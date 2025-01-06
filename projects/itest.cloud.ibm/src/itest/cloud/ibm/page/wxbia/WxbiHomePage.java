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
package itest.cloud.ibm.page.wxbia;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.page.element.wxbi.conversation.WxbiConversationEditorElement;
import itest.cloud.ibm.page.element.wxbi.conversation.WxbiConversationsMenuElement;
import itest.cloud.ibm.page.element.wxbi.metric.WxbiCarouselElement;

/**
 * This class represents and manages the <b>Home</b> page of the WatsonX BI Assistant application.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getCarouselElement()}: Return the carousel element.</li>
 * <li>{@link #getConversationEditorElement()}: Return the Conversation Editor element</li>
 * <li>{@link #getConversationsMenuElement()}: Return the conversation menu element.</li>
 * <li>{@link #openCarouselElement()}: Open the Key Metrics menu element.</li>
 * <li>{@link #openConversationsMenuElement()}: Open the Conversations Menu element.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return the expected title for the current web page.</li>
 * <li>{@link #getTitleElementLocator()}: Return the title element locator.</li>
 * </ul>
 * </p>
 */
public class WxbiHomePage extends WxbiPage {

public WxbiHomePage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

/**
 * Return the carousel element.
 *
 * @return The carousel element as {@link WxbiCarouselElement}.
 */
public WxbiCarouselElement getCarouselElement() {
	final WxbiCarouselElement carouselElement = new WxbiCarouselElement(this);
	// Wait for the contents to load if it is expanded.
	if(carouselElement.isExpanded()) carouselElement.waitForLoadingEnd();
	return carouselElement;
}

/**
 * Return the <b>Conversation Editor</b> element.
 *
 * @return The the <b>Conversation Editor</b> element as {@link WxbiConversationEditorElement}.
 */
public WxbiConversationEditorElement getConversationEditorElement() {
	final WxbiConversationEditorElement conversationEditorElement = new WxbiConversationEditorElement(this);
	// Wait for its contents to load.
	conversationEditorElement.waitForLoadingEnd();
	return conversationEditorElement;
}

/**
 * Return the conversation menu element.
 *
 * @return The conversation menu element as {@link WxbiConversationsMenuElement}.
 */
public WxbiConversationsMenuElement getConversationsMenuElement() {
	final WxbiConversationsMenuElement conversationsMenuElement = new WxbiConversationsMenuElement(this);
	// Wait for the contents to load if it is expanded.
	if(conversationsMenuElement.isExpanded()) conversationsMenuElement.waitForLoadingEnd();
	return conversationsMenuElement;
}

@Override
protected Pattern getExpectedTitle() {
//	return Pattern.compile(Pattern.quote("Real time metrics"));
	return null;
}

@Override
protected By getTitleElementLocator() {
//	return By.xpath("//*[contains(@class,'main-header-title')]//h6");
	return null;
}

/**
 * Open the carousel element.
 *
 * @return The opened carousel element as {@link WxbiCarouselElement}.
 */
public WxbiCarouselElement openCarouselElement() {
	WxbiCarouselElement carouselElement = getCarouselElement();
	carouselElement.expand();

	return carouselElement;
}

/**
 * Open the <b>Conversations Menu</b> element.
 *
 * @return The opened Conversations Menu element as {@link WxbiConversationsMenuElement}.
 */
public WxbiConversationsMenuElement openConversationsMenuElement() {
	WxbiConversationsMenuElement conversationsMenuElement = getConversationsMenuElement();
	conversationsMenuElement.expand();

	return conversationsMenuElement;
}

@Override
public void waitForLoadingPageEnd() {
	super.waitForLoadingPageEnd();

	// Wait for the carousel element to load.
	getCarouselElement();

	// Wait for conversation editor element to load.
	getConversationEditorElement();

	// Wait for conversations menu element to load.
	getConversationsMenuElement();
}
}