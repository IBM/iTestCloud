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

import itest.cloud.ibm.page.element.IbmExpandableElement;
import itest.cloud.page.element.ElementWrapper;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This class represents a question element in {@link WxbiConversationEditorElement}.
 * <p>
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #getContext()}: Returns the context of the question.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * <li>{@link #isExpandable()}: Returns whether the current wrapped web element is expandable or not.</li>
 * <li>{@link #isExpanded()}: Returns whether the current wrapped web element is expanded or not.</li>
 * </ul>
 * </p>
 */
public class WxbiQuestionContextMenuElement extends IbmExpandableElement {

	public static final By QUESTION_CONTEXT_MENU_LOCATOR = By.xpath(".//*[@id='ripasso-smart-steps-info']");

public WxbiQuestionContextMenuElement(final ElementWrapper parent) {
	super(parent, parent.waitForElement(QUESTION_CONTEXT_MENU_LOCATOR), By.xpath(".//*[name()='svg']"));
}

/**
 * Returns the context of the question.
 *
 * @return The context of the question as {@link String}.
 */
public String getContext() {
	return waitForElement(By.xpath(".//*[contains(@class,'steps-content')]"), timeout(), true /*fail*/, false /*displayed*/).getText();
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

@Override
protected By getTitleElementLocator() {
	return null;
}

@Override
public boolean isExpandable() throws ScenarioFailedError {
	return true;
}

@Override
public boolean isExpanded() throws ScenarioFailedError {
	return this.element.getClassAttribute().contains("open");
}
}