/*********************************************************************
 * Copyright (c) 2012, 2022 IBM Corporation and others.
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

import static itest.cloud.scenario.ScenarioUtil.printException;
import static itest.cloud.scenario.ScenarioUtil.println;

import java.util.regex.Pattern;

import org.openqa.selenium.*;

import itest.cloud.ibm.entity.AlertStatus;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class represents an alert element.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #close()}: Close the alert.</li>
 * <li>{@link #close(boolean)}: Close the alert.</li>
 * <li>{@link #getAlert()}: Return the alert provided in the element.</li>
 * <li>{@link #getStatus()}: Return the status of the alert.</li>
 * <li>{@link #getSubtitle()}: Return the subtitle of the alert provided in the element.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class IbmAlertElement extends ElementWrapper {

	public static final By ALERT_ELEMENT_LOCATOR = By.xpath("//*[contains(@class,'toast-notification ')]");

public IbmAlertElement(final Page page, final BrowserElement element) {
	super(page, element);
}

public IbmAlertElement(final Page page, final By findBy) {
	super(page, findBy);
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
	String alert = getAlert();
	try {
		// A StaleElementReferenceException can be thrown if the alert is automatically closed by itself
		// while this method is trying to dismiss it. Do nothing in such a situation, but printing the
		// exception.
		BrowserElement closeButtonElement =
			waitForElement(By.xpath(".//*[contains(@class,'close-button')]"), (fail ? timeout() : tinyTimeout()), fail, false /*displayed*/, true /*single*/);

		if(closeButtonElement != null) closeButtonElement.clickViaJavaScript();
	}
	catch (StaleElementReferenceException | NoSuchElementException e) {
		// Print failure stack trace
		println("	  -> Following exception occurred while dismissing the alert: " + alert);
		printException(e);
	}
}

/**
 * Return the alert provided in the element.
 *
 * @return The alert as {@link String}.
 */
public String getAlert() {
	try {
		return this.element.getText();
	}
	catch (StaleElementReferenceException | NoSuchElementException e) {
		return null;
	}
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

/**
 * Return the status of the alert.
 *
 * @return The status of the alert as {@link AlertStatus}.
 */
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

@Override
protected By getTitleElementLocator() {
//	return By.xpath(".//*[contains(@class,'notification__title')]");
	return null;
}
}