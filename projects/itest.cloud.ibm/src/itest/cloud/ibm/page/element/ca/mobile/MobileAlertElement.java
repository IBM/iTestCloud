/*********************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
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
package itest.cloud.ibm.page.element.ca.mobile;

import static itest.cloud.scenario.ScenarioUtil.SPACE_STRING;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;
import itest.cloud.page.Page;
import itest.cloud.page.element.AlertElement;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents an alert element in the Cognos Analytics Mobile application.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getMessage(BrowserElement)}: Return the message of the alert.</li>
 * <li>{@link #getStatus(BrowserElement)}: Return the status of the alert.</li>
 * </ul>
 * </p>
 */
public class MobileAlertElement extends AlertElement {

	public static final By NATIVE_ALERT_ELEMENT_LOCATOR = AppiumBy.accessibilityId("Toast message");
	private static final By MESSAGE_ELEMENT_LOCATOR = By.xpath(".//*[@*='Toast message content']");
	private static final By STATUS_ELEMENT_LOCATOR = By.xpath(".//*[starts-with(@content-desc,'Status ')]");
	private static final By CLOSE_BUTTON_LOCATOR = By.xpath(".//*[@*='Close']");

public MobileAlertElement(final Page page, final BrowserElement element) {
	super(page, element, MESSAGE_ELEMENT_LOCATOR, STATUS_ELEMENT_LOCATOR, CLOSE_BUTTON_LOCATOR);
}

public MobileAlertElement(final Page page, final By findBy) {
	super(page, findBy, MESSAGE_ELEMENT_LOCATOR, STATUS_ELEMENT_LOCATOR, CLOSE_BUTTON_LOCATOR);
}

@Override
protected String getMessage(final BrowserElement messageButtonElement) {
	return messageButtonElement.getTextAttribute();
}

@Override
protected String getStatus(final BrowserElement statusElement) {
	return statusElement.getAttributeValue("content-desc").split(SPACE_STRING)[1];
}
}