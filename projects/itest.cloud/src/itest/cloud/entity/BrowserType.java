/*********************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
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
package itest.cloud.entity;

import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This enum represents the types of browsers that can be used to run a test scenario.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public enum BrowserType {

	ANDROID("Android"),
	CHROME("Chrome"),
	CHROMIUM("Chromium"),
	EDGE("Edge"),
	FIREFOX("Firefox"),
	INTERNET_EXPLORER("Internet Explorer"),
	IOS("iOS"),
	SAFARI("Safari");

	/**
	 * Return the browser type representing a given name.
	 *
	 * @param name The name of the browser type.
	 *
	 * @return The browser type representing a given name as {@link BrowserType}.
	 */
	public static final BrowserType toEnum(final String name) {
		for (BrowserType browserTypes : values()) {
			if (name.equalsIgnoreCase(browserTypes.getName())) {
				return browserTypes;
			}
		}

		throw new ScenarioFailedError("Browser type '" + name + "' is unrecognized by this method");
	}

	final String name;

BrowserType(final String label) {
	this.name = label;
}

/**
 * Return the name of this browser type.
 *
 * @return the name of this browser type.
 */
public String getName() {
	return this.name;
}
}