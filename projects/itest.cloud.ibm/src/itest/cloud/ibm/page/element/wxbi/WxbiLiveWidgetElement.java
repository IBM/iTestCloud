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
package itest.cloud.ibm.page.element.wxbi;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.ibm.page.element.wxbi.metric.WxbiCarouselElement;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class defines and manages a live widget element in {@link WxbiCarouselElement} or {@link WxbiVisualizationElement}.
 * <p>
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #getParent()}: Returns the parent element.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #waitForLoadingEnd()}: Wait for loading of the wrapped element to complete.</li>
 * </ul>
 * </p>
 */
public class WxbiLiveWidgetElement extends IbmElementWrapper {

	public static final By LIVE_WIDGET_ELEMENT_LOCATOR = By.xpath(".//*[contains(@class,'vizWidget ripasso-visualization')]/* | .//*[contains(@class,'liveWidget ')]");
//	private static final By LOADED_KEY_METRIC_CONTENT_ELEMENT_LOCATOR =
//		// Loaded content element locator for a chat and a textual representation are given below respectively.
//		By.xpath(".//*[contains(@class,'Legend') and @aria-hidden='true'] | .//*[contains(@class,'base-value labeled-value')]");

public WxbiLiveWidgetElement(final ElementWrapper parent) {
	super(parent, parent.waitForElement(LIVE_WIDGET_ELEMENT_LOCATOR));
}

public WxbiLiveWidgetElement(final ElementWrapper parent, final BrowserElement element) {
	super(parent, element);
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

@Override
public WxbiVisualizationElement getParent() {
	return (WxbiVisualizationElement) super.getParent();
}

@Override
protected By getTitleElementLocator() {
	return null;
}

@Override
public void waitForLoadingEnd() {
	super.waitForLoadingEnd();
	// Wait for the content of the visualization element to load.
	waitForElement(By.xpath(".//*[name()='canvas'] | .//*[contains(@class,'ariaLabelNode') and (text()!='')]"), timeout(), true /*fail*/, false /*displayed*/);
}
}