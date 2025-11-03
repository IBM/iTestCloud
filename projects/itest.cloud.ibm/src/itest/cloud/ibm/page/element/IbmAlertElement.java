/*********************************************************************
 * Copyright (c) 2012, 2025 IBM Corporation and others.
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
package itest.cloud.ibm.page.element;

import org.openqa.selenium.*;

import itest.cloud.entity.AlertStatus;
import itest.cloud.page.Page;
import itest.cloud.page.element.AlertElement;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents an alert element in an IBM application.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getMessage()}: Return the message of the alert.</li>
 * <li>{@link #getStatus()}: Return the status of the alert.</li>
 * <li>{@link #getSubtitle()}: Return the subtitle of the alert provided in the element.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class IbmAlertElement extends AlertElement {

	public static final By WEB_ALERT_ELEMENT_LOCATOR = By.xpath("//*[contains(@class,'toast-notification ')]");
	private static final By CLOSE_BUTTON_LOCATOR = By.xpath(".//*[contains(@class,'close-button')]");

public IbmAlertElement(final Page page, final BrowserElement element) {
	super(page, element, null /*messageElementLocator*/, null /*statusElementLocator*/, CLOSE_BUTTON_LOCATOR);
}

public IbmAlertElement(final Page page, final By findBy) {
	super(page, findBy, null /*messageElementLocator*/, null /*statusElementLocator*/, CLOSE_BUTTON_LOCATOR);
}

/**
 * Return the message of the alert.
 *
 * @return The message as {@link String}.
 */
@Override
public String getMessage() {
	try {
		return this.element.getText();
	}
	catch (StaleElementReferenceException | NoSuchElementException e) {
		return null;
	}
}

/**
 * Return the status of the alert.
 *
 * @return The status of the alert as {@link AlertStatus}.
 */
@Override
public AlertStatus getStatus() {
	try {
		String status = this.element.getClassAttribute();
		return AlertStatus.toEnum(status);
	}
	catch (StaleElementReferenceException | NoSuchElementException e) {
		return null;
	}
}

/**
 * Return the subtitle of the alert provided in the element.
 *
 * @return The subtitle of the alert as {@link String}.
 */
public String getSubtitle() {
	try {
		return waitForElement(By.xpath(".//*[contains(@class,'subtitle')]")).getText();
	}
	catch (StaleElementReferenceException | NoSuchElementException e) {
		return null;
	}
}
}