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
import itest.cloud.ibm.page.element.wxbi.metric.WxbiCarouselElement;

/**
 * This class represents and manages the <b>Conversations</b> page of the WatsonX BI Assistant application.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return the expected title for the current web page.</li>
 * </ul>
 * </p>
 */
public class WxbiConversationsPage extends WxbiPage {

public WxbiConversationsPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

/**
 * Returns the <b>Key Metrics</b> element.
 *
 * @return The Key Metrics element as {@link WxbiCarouselElement}.
 */
public WxbiCarouselElement getKeyMetricsElement() {
	WxbiCarouselElement keyMetricsElement = new WxbiCarouselElement(this);
	keyMetricsElement.expand();

	return keyMetricsElement;
}

@Override
protected Pattern getExpectedTitle() {
	return Pattern.compile(Pattern.quote("Key metrics"));
}

@Override
protected By getTitleElementLocator() {
	return By.xpath("//h6");
}
}