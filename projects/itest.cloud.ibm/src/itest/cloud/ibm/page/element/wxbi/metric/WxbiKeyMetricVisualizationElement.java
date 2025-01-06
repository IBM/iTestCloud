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
package itest.cloud.ibm.page.element.wxbi.metric;

import static itest.cloud.page.Page.NO_DATA;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.ibm.page.element.wxbi.conversation.WxbiConversationEditorElement;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class defines and manages a visualization element of a key metric in the {@link WxbiConversationEditorElement}.
 * <p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #remove()}: Remove the visualization element.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * </p>
 */
public class WxbiKeyMetricVisualizationElement extends IbmElementWrapper {

	public static final By VISUALIZATION_ELEMENT_LOCATOR = By.xpath(".//*[@class='ripasso-input-context-container']");
	private static final By TITLE_ELEMENT_LOCATOR = By.xpath(".//h6");

public WxbiKeyMetricVisualizationElement(final ElementWrapper parent, final BrowserElement element) {
	super(parent, element, NO_DATA);
	this.data = new String[] { waitForElement(TITLE_ELEMENT_LOCATOR).getText() };
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote(this.data[0]));
}

@Override
protected By getTitleElementLocator() {
	return TITLE_ELEMENT_LOCATOR;
}

/**
 * Remove the visualization element.
 */
public void remove() {
	waitForElement(By.xpath(".//*[contains(@class,'visualization-close')]")).click();
}
}