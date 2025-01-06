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
package itest.cloud.ibm.scenario.wxbi;

import org.openqa.selenium.By;

import itest.cloud.ibm.scenario.IbmScenarioUtil;

/**
 * Utilities to perform various operations.
 * <p>
 * <li>{@link #getSuggestedQuestionElementLocator(String)}: Return the locator of a given suggested question.</li>
 * </p>
 */
public class WxbiScenarioUtil extends IbmScenarioUtil {

	public static final String SUGGESTED_QUESTION_ELEMENTS_LOCATOR_STRING = ".//button[contains(@class,'suggestion-question')]";

/**
 * Return the locator of a suggested question at a given index.
 *
 * @param index The index of the suggested question.
 *
 * @return The locator of a suggested question at the given index as {@link By}.
 */
public static By getSuggestedQuestionElementLocator(final int index) {
	return By.xpath(SUGGESTED_QUESTION_ELEMENTS_LOCATOR_STRING + "[" + (index + 1) + "]");
}

/**
 * Return the locator of a given suggested question.
 *
 * @param question The suggested question as {@link String}.
 *
 * @return The the locator of the given suggested question as {@link By}.
 */
public static By getSuggestedQuestionElementLocator(final String question) {
	return By.xpath(SUGGESTED_QUESTION_ELEMENTS_LOCATOR_STRING + ((question != null) ? ("[text()='" + question + "']") : EMPTY_STRING));
}
}
