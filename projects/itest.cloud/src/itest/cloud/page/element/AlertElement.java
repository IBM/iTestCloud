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
package itest.cloud.page.element;

import static itest.cloud.scenario.ScenarioUtil.printException;
import static itest.cloud.scenario.ScenarioUtil.println;

import java.util.regex.Pattern;

import org.openqa.selenium.*;

import itest.cloud.entity.AlertStatus;
import itest.cloud.page.Page;

/**
 * This class represents an alert element.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #close()}: Close the alert.</li>
 * <li>{@link #close(boolean)}: Close the alert.</li>
 * <li>{@link #getMessage()}: Return the message of the alert.</li>
 * <li>{@link #getStatus()}: Return the status of the alert.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getMessage(BrowserElement)}: Return the message of the alert.</li>
 * <li>{@link #getStatus(BrowserElement)}: Return the status of the alert.</li>
 * </ul>
 * </p>
 */
public class AlertElement extends ElementWrapper {

	private final By messageElementLocator, statusElementLocator, closeButtonLocator;

public AlertElement(final Page page, final BrowserElement element, final By messageElementLocator, final By statusElementLocator, final By closeButtonLocator) {
	super(page, element);

	this.messageElementLocator = messageElementLocator;
	this.statusElementLocator = statusElementLocator;
	this.closeButtonLocator = closeButtonLocator;
}

public AlertElement(final Page page, final By findBy, final By messageElementLocator, final By statusElementLocator, final By closeButtonLocator) {
	super(page, findBy);

	this.messageElementLocator = messageElementLocator;
	this.statusElementLocator = statusElementLocator;
	this.closeButtonLocator = closeButtonLocator;
}

/**
 * Close the alert.
 */
public void close() {
	close(true /*fail*/);
}

/**
 * Close the alert.
 *
 * @param fail Specify whether to fail if the alert can not be closed.
 */
public void close(final boolean fail) {
	String alert = getMessage();
	try {
		// A StaleElementReferenceException can be thrown if the alert is automatically closed by itself
		// while this method is trying to dismiss it. Do nothing in such a situation, but printing the
		// exception.
		final BrowserElement closeButtonElement =
			waitForElement(this.closeButtonLocator, (fail ? timeout() : tinyTimeout()), fail, false /*displayed*/, true /*single*/);

		if(closeButtonElement != null) closeButtonElement.clickViaJavaScript();
	}
	catch (StaleElementReferenceException | NoSuchElementException e) {
		// Print failure stack trace
		println("	  -> Following exception occurred while dismissing the alert: " + alert);
		printException(e);
	}
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

/**
 * Return the message of the alert.
 *
 * @return The message as {@link String}.
 */
public String getMessage() {
	try {
		// A StaleElementReferenceException can be thrown if the alert is automatically closed by itself
		// while this method is trying to access its message element. Do nothing in such a situation, but printing the
		// exception.
		final BrowserElement messageButtonElement = waitForElement(this.messageElementLocator, timeout(), true /*fail*/, false /*displayed*/);
		return getMessage(messageButtonElement);
	}
	catch (StaleElementReferenceException | NoSuchElementException e) {
		return null;
	}
}

/**
 * Return the message of the alert.
 *
 * @param messageButtonElement The close button element as {@link BrowserElement}.
 *
 * @return The message as {@link String}.
 */
protected String getMessage(final BrowserElement messageButtonElement) {
	return messageButtonElement.getText();
}

/**
 * Return the status of the alert.
 *
 * @return The status of the alert as {@link AlertStatus}.
 */
public AlertStatus getStatus() {
	try {
		// A StaleElementReferenceException can be thrown if the alert is automatically closed by itself
		// while this method is trying to access its status element. Do nothing in such a situation, but printing the
		// exception.
		final BrowserElement statusElement = waitForElement(this.statusElementLocator, timeout(), true /*fail*/, false /*displayed*/);
		return AlertStatus.toEnum(getStatus(statusElement));
	}
	catch (StaleElementReferenceException | NoSuchElementException e) {
		return null;
	}
}

/**
 * Return the status of the alert.
 *
 * @param statusElement The status element as {@link BrowserElement}.
 *
 * @return The status of the alert as {@link AlertStatus}.
 */
protected String getStatus(final BrowserElement statusElement) {
	return statusElement.getClassAttribute();
}

@Override
protected By getTitleElementLocator() {
//	return By.xpath(".//*[contains(@class,'notification__title')]");
	return null;
}
}