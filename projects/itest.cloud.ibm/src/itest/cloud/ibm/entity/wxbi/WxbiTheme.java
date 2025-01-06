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
package itest.cloud.ibm.entity.wxbi;

import itest.cloud.scenario.error.InvalidArgumentError;

/**
 * This enum represents a theme that the user can select for the WatsonX BI Assistant application.
 */
public enum WxbiTheme {
	GRAY_10("Gray 10", "g10"),
	GRAY_100("Gray 100", "g100");

	/**
	 * The textual representation of the enum constant used in the UI.
	 */
	public final String label;
	/**
	 * The id of the enum constant representing the selected theme in the class attribute of the body element of the web page.
	 */
	public final String id;

WxbiTheme(final String label, final String id) {
	this.label = label;
	this.id = id;
}

/**
 * Returns the enum constant that represents the theme applied to the web page.
 *
 * @param classAttribute The value of the class attribute of the body element of the web page as {@link String}.
 *
 * @return The enum constant that represents the theme applied to the web page as {@link WxbiTheme}.
 */
public static WxbiTheme getThemeEnum(final String classAttribute) {
	final WxbiTheme[] values = values();
	if(classAttribute == null) return values[0];

	for (WxbiTheme theme : values) {
		if(classAttribute.endsWith(theme.id)) return theme;
	}
	throw new InvalidArgumentError("A Theme enum could not be found for the class attribute '" + classAttribute + "'");
}
}