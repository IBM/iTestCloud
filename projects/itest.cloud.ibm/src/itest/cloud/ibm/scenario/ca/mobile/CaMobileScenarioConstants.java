/*********************************************************************
 * Copyright (c) 2018, 2024 IBM Corporation and others.
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

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;
import itest.cloud.ibm.scenario.IbmScenarioConstants;

/**
 * Common constants for the Cognos Analytics Mobile Application.
 */
public interface CaMobileScenarioConstants extends IbmScenarioConstants {

	public static final By CONTEXT_MENU_EXPANSION_LOCATOR = By.xpath(".//*[contains(@class,'overflow')]");
	public static final By BACK_BUTTON_LOCATOR = AppiumBy.accessibilityId("Back");
	public static final By CLOSE_BUTTON_LOCATOR = AppiumBy.accessibilityId("Close");
	public static final By SEARCH_BAR_LOCATOR = AppiumBy.accessibilityId("ca-textinput-search-bar");
	public static final By CHART_LOADING_CONFIRMATION_ELEMENT_LOCATOR = By.xpath(".//*[starts-with(@class,'ariaLabelNode')]");
	// Title element of a graph and KPI widget are given below respectively.
	public static final By CHART_TITLE_ELEMENT_LOCATOR = By.xpath(
		// The following is the locator for a chat with a visualization such as a pie chart.
		".//*[contains(@class,'textArea') and not(contains(@class,'hidden'))]//*[contains(@id,'Title') and .//*] | " +
		// The following is the locator for a chat with a KPI information.
		".//*[contains(@class,'kpi-widget-base-value')]//*[contains(@class,'value-label')]//*[not(child::*)] | " +
		// The following is the locator for a chat with a list of texts.
		".//*[contains(@class, 'datawidget')]");
}