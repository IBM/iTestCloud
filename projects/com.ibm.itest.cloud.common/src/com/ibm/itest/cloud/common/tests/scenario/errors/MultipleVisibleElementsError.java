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
package com.ibm.itest.cloud.common.tests.scenario.errors;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.LINE_SEPARATOR;

import java.util.List;

import com.ibm.itest.cloud.common.pages.elements.WebBrowserElement;

public class MultipleVisibleElementsError extends ScenarioFailedError {

/**
 * Create a multiple visible elements error with a generic message.
 * @deprecated as of 6.0.0; use {@link #MultipleVisibleElementsError(List)}
 * to provide more debugging information.
 */
@Deprecated
public MultipleVisibleElementsError() {
	this("Found several visible elements.");
}

/**
 * Create a multiple visible elements error with a specific message.
 * @deprecated as of 6.0.0; use {@link #MultipleVisibleElementsError(List)}
 * or {@link #MultipleVisibleElementsError(String, List)} to provide more
 * debugging information.
 */
@Deprecated
public MultipleVisibleElementsError(final String message) {
	super(message);
}

/**
 * Create a multiple visible elements error, including information about the locator being used
 * and how many elements have been found.
 *
 * @param elements List of elements found
 */
public MultipleVisibleElementsError(final List<WebBrowserElement> elements) {
	this("Unexpected multiple elements found.", elements);
}

/**
 * Create a multiple visible elements error, including a specific message, information about the locator being
 * used and how many elements have been found.
 *
 * @param message Specific message
 * @param elements List of elements found
 */
public MultipleVisibleElementsError(final String message, final List<WebBrowserElement> elements) {
	super(message + LINE_SEPARATOR
		+ "			-> element: " + elements.get(0).getBy() + LINE_SEPARATOR
		+ "			-> # found: " + elements.size());
}

/**
 * Create a multiple visible elements error with a specific message.
 * @deprecated as of 6.0.0; use {@link #MultipleVisibleElementsError(List)}
 * or {@link #MultipleVisibleElementsError(String, List)} to provide more
 * debugging information.
 * TODO If necessary, create a constructor that takes a throwable and additional
 * debugging information
 */
@Deprecated
public MultipleVisibleElementsError(final Throwable ex) {
	super(ex);
}
}
