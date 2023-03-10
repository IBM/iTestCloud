/*********************************************************************
 * Copyright (c) 2014, 2022 IBM Corporation and others.
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
package itest.cloud;

import org.openqa.selenium.By;

/**
 * Usual constants used by web objects.
 */
public interface Constants {

	// Common strings
	public static final String OK = "OK";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String ENABLED = "enabled";
	public static final String DISABLED = "disabled";
	public static final String NAME = "Name";
	public static final String TITLE = "title";

	// Common locators
	public static final By TAG_NAME_H1 = By.tagName("h1");
}
