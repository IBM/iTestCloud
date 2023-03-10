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
package itest.cloud.scenario.errors;

import static itest.cloud.scenario.ScenarioUtils.LINE_SEPARATOR;

import java.util.List;

import itest.cloud.pages.elements.BrowserElement;

public class MultipleVisibleElementsError extends ScenarioFailedError {

/**
 * Create a multiple visible elements error, including information about the locator being used
 * and how many elements have been found.
 *
 * @param elements List of elements found
 */
public MultipleVisibleElementsError(final List<BrowserElement> elements) {
	this("Unexpected multiple elements found.", elements);
}

/**
 * Create a multiple visible elements error, including a specific message, information about the locator being
 * used and how many elements have been found.
 *
 * @param message Specific message
 * @param elements List of elements found
 */
public MultipleVisibleElementsError(final String message, final List<BrowserElement> elements) {
	super(message + LINE_SEPARATOR
		+ "			-> element: " + elements.get(0).getBy() + LINE_SEPARATOR
		+ "			-> # found: " + elements.size());
}
}