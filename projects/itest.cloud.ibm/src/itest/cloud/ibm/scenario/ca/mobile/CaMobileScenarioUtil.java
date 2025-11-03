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
package itest.cloud.ibm.scenario.ca.mobile;
import static itest.cloud.ibm.entity.mobile.AssetContext.TEAM_CONTENT;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;
import itest.cloud.ibm.entity.mobile.AssetContext;
import itest.cloud.ibm.scenario.IbmScenarioUtil;

/**
 * Utilities to perform various operations.
 * <p>
 * <li>{@link #getBoardsDeletionAlertPattern(String)}: Return the pattern of the alert provided once a board has been deleted.</li>
 * <li>{@link #getContentTabButtonLocator(AssetContext)}: Return the locator of a given tab button in the Content Page.</li>
 * <li>{@link #getListItemLocator(String)}: Return the locator of a list item with a given name.</li>
 * </p>
 */
public class CaMobileScenarioUtil extends IbmScenarioUtil {

	public static final By TEAM_CONTENT_BUTTON_LOCATOR = getContentTabButtonLocator(TEAM_CONTENT);

/**
 * Return the pattern of the alert provided once a board has been deleted.
 *
 * @param board The name of the board as {@link String}.
 *
 * @return The pattern of the alert provided once a board has been deleted as {@link Pattern}.
 */
public static Pattern getBoardsDeletionAlertPattern(final String board) {
	return compile(quote("\"" + board + "\" was deleted"));
}

/**
 * Return the locator of a given tab button in the Content Page.
 *
 * @param tab The tab as {@link AssetContext}.
 *
 * @return The locator of the given tab button as {@link By}.
 */
public static By getContentTabButtonLocator(final AssetContext tab) {
	return AppiumBy.accessibilityId("ca-text-button-" + tab.getId());
}

/**
 * Return the locator of a list item with a given name.
 *
 * @param name The name of the list item as {@link String}.
 *
 * @return The locator of the given list item as {@link By}.
 */
public static By getListItemLocator(final String name) {
	return AppiumBy.accessibilityId("listitem-" + name);
}
}