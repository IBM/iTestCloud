/*********************************************************************
 * Copyright (c) 2013, 2022 IBM Corporation and others.
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
package itest.cloud.util;

import static itest.cloud.scenario.ScenarioUtil.*;

import java.util.StringTokenizer;

import org.openqa.selenium.By;
import org.openqa.selenium.By.*;

import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * Utility class to create {@link By} locator mechanism.
 * <p>
 * This class contains following API methods:
 * <ul>
 * <li>{@link #fixLocator(By)}: Check whether the locator need to be fixed.</li>
 * <li>{@link #getCombinedLocator(boolean, String...)}: Return the combined locator from the given array of locators.</li>
 * <li>{@link #getCombinedLocatorString(boolean, String...)}: Return the combined locator string from the given array of locators.</li>
 * <li>{@link #getLocatorString(By)}: Return the string content for the given locator.</li>
 * <li>{@link #getNormalizedLocatorString(By)}: Returns the XPath string for the given locator supported in By.</li>
 * <li>{@link #isRelativeLocator(By)}: Check whether the given is a relative locator.</li>
 * <li>{@link #isRelativeLocator(String)}: Check whether a given locator string is relative.</li>
 * <li>{@link #toRelativeLocator(By)}: Convert a given locator to its relative counterpart.</li>
 * <li>{@link #toRelativeLocator(String)}: Convert a given locator string to its relative counterpart.</li>
 * <li>{@link #toRelativeLocatorString(String)}: Convert a given locator string to its relative counterpart string.</li>
 * </ul>
 * </p>
 */
public class ByUtils {

	public static final String OR = " | ";
	public static final String RELATIVITY_STRING = PERIOD_STRING;

/**
 * Check whether the locator need to be fixed.
 *
 * @param locator The locator to be fixed if necessary
 */
public static By fixLocator(final By locator) {
	if (locator instanceof By.ById) {
		String locatorString = getLocatorString(locator);
		if (locatorString.indexOf(SPACE_CHAR) >= 0) {
			StringTokenizer tokenizer = new StringTokenizer(locatorString, SPACE_STRING);
			StringBuilder xpath = new StringBuilder("//*[");
			while (tokenizer.hasMoreTokens()) {
				xpath.append("contains(@id, '");
				xpath.append(tokenizer.nextToken());
				xpath.append("')");
				if (tokenizer.hasMoreTokens()) {
					xpath.append(" and ");
				}
			}
			xpath.append("]");
			if (DEBUG) debugPrintln("			-> locator '"+locator+"' had spaces, hence replacing it with '"+xpath+"'");
			return By.xpath(xpath.toString());
		}
	}
	return locator;
}

/**
 * Return the combined locator from the given array of locators.
 *
 * @param relative Specify whether the combined locator should be relative or absolute.
 * @param locators An array of locators to be combined into one.
 *
 * @return The combined locator as {@link By}.
 */
public static By getCombinedLocator(final boolean relative, final String... locators) {
	return By.xpath(getCombinedLocatorString(relative, locators));
}

/**
 * Return the combined locator string from the given array of locators.
 *
 * @param relative Specify whether the combined locator should be relative or absolute.
 * @param locators An array of locators to be combined into one.
 *
 * @return The combined locator string as {@link String}.
 */
public static String getCombinedLocatorString(final boolean relative, final String... locators) {
	StringBuffer combinedLocator = new StringBuffer();

	for (int i = 0; i < locators.length; i++) {
		combinedLocator.append((relative ? RELATIVITY_STRING : EMPTY_STRING) + locators[i]);
		if(i < locators.length - 1) combinedLocator.append(OR);
	}

	return combinedLocator.toString();
}

/**
 * Returns the XPath string for a given locator.
 *
 * @param locator The locator to obtain the XPath string as {@link By}.
 *
 * @return the XPath string as {@link String}.
 */
public static String getLocatorString(final By locator) {
	String locatorString = locator.toString();
	return locatorString.substring(locatorString.indexOf(": ") + 2);
}

/**
 * Returns a generalized the XPath string for a given locator supported in <code>By</code>.
 *
 * @param locator The locator to obtain the XPath string as {@link By}.
 *
 * @return the generalized XPath string as {@link String}.
 */
public static String getNormalizedLocatorString(final By locator) {
	String locatorString = getLocatorString(locator);

	if (locator instanceof ByXPath) return locatorString;
	if (locator instanceof ByClassName) return "//*[contains(@class,'" + locatorString + "')]";
	if (locator instanceof ById) return "//*[@id='" + locatorString+ "']";
	if (locator instanceof ByName) return "//*[@name='" + locatorString+ "']";
	if (locator instanceof ByTagName) return "//" + locatorString+ "]";
	if (locator instanceof ByLinkText) return "//a[text()='" + locatorString+ "']";
	if (locator instanceof ByPartialLinkText) return "//a[contains[text(),'"+locatorString+"')]";
	if (locator instanceof ByCssSelector) return "css=" + locator.toString();

	throw new ScenarioFailedError("Locator type '" + locator.getClass().getSimpleName() + "' is not supported by this method.");
}

/**
 * Check whether a given locator is relative.
 *
 * @param locator The locator to be checked.
 *
 * @return <code>true</code> If the given is a relative locator or <code>false</code> otherwise.
 */
public static boolean isRelativeLocator(final By locator) {
	return isRelativeLocator(getLocatorString(locator));
}

/**
 * Check whether a given locator string is relative.
 *
 * @param locator The locator string to be checked.
 *
 * @return <code>true</code> If the given is a relative locator string or <code>false</code> otherwise.
 */
public static boolean isRelativeLocator(final String locator) {
	return locator.trim().startsWith(RELATIVITY_STRING);
}

/**
 * Convert a given locator to its relative counterpart.
 * <p>
 * If the given locator is already relative, the original locator will be returned.
 * </p>
 *
 * @param locator The locator to be made relative.
 *
 * @return The relative locator.
 */
public static By toRelativeLocator(final By locator) {
	return toRelativeLocator(getLocatorString(locator));
}

/**
 * Convert a given locator string to its relative counterpart.
 * <p>
 * If the given locator string is already relative, the original locator string will be returned.
 * </p>
 *
 * @param locator The locator string to be made relative.
 *
 * @return The relative locator.
 */
public static By toRelativeLocator(final String locator) {
	return By.xpath(toRelativeLocatorString(locator));
}

/**
 * Convert a given locator string to its relative counterpart string.
 * <p>
 * If the given locator string is already relative, the original locator string will be returned.
 * </p>
 *
 * @param locator The locator string to be made relative.
 *
 * @return The relative locator.
 */
public static String toRelativeLocatorString(final String locator) {
	return isRelativeLocator(locator) ? locator : RELATIVITY_STRING + locator;
}
}