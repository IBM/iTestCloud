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
package itest.cloud.ibm.scenario;

import static itest.cloud.util.ByUtils.toRelativeLocatorString;

import org.openqa.selenium.By;

import itest.cloud.scenario.ScenarioUtil;

/**
 * Utilities to perform various operations.
 * <p>
 * <li>{@link #getBusyIndicatorElementLocator(boolean)}: Return the busy indicator element locator.</li>
 * <li>{@link #getBusyIndicatorElementLocator(boolean, String...)}: Return the busy indicator element locator.</li>
 * </p>
 */
public class IbmScenarioUtil extends ScenarioUtil {

	private static final String BUSY_INDICATOR_ELEMENT_LOCATOR_STRING = "//*[contains(@class,'skeleton')]";

/**
 * Return the busy indicator element locator.
 *
 * @param relative Specify whether the locator should be relative or absolute.
 *
 * @return The busy indicator element locator as {@link By}.
 */
public static By getBusyIndicatorElementLocator(final boolean relative) {
	return getBusyIndicatorElementLocator(relative, EMPTY_STRING_ARRAY);
}

/**
 * Return the busy indicator element locator.
 *
 * @param relative Specify whether the locator should be relative or absolute.
 * @param additionalLocators An array of additional locators to consider as busy indicator elements in addition to the default.
 *
 * @return The busy indicator element locator as {@link By}.
 */
public static By getBusyIndicatorElementLocator(final boolean relative, final String... additionalLocators) {
	final StringBuffer locatorBuffer = new StringBuffer(
		relative ? toRelativeLocatorString(BUSY_INDICATOR_ELEMENT_LOCATOR_STRING) : BUSY_INDICATOR_ELEMENT_LOCATOR_STRING);

	for (String additionalLocator : additionalLocators) {
		locatorBuffer.append(" | " + (relative ? toRelativeLocatorString(additionalLocator) : additionalLocator));
	}

	return By.xpath(locatorBuffer.toString());
}
}