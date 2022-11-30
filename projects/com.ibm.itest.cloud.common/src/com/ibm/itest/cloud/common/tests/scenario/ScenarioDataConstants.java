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
package com.ibm.itest.cloud.common.tests.scenario;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.EMPTY_STRING;
import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.getParameterValue;
import static com.ibm.itest.cloud.common.tests.web.WebBrowser.BROWSER_DOWNLOAD_DIR_ID;
import static com.ibm.itest.cloud.common.tests.web.WebBrowser.BROWSER_DOWNLOAD_DIR_VALUE;

import java.io.File;

public interface ScenarioDataConstants {

	// Test prefix
	String TEST_PREFIX_PARAM_DEFAULT_VALUE = EMPTY_STRING;
	String TEST_PREFIX_PARAM_ID = "testPrefix";
	String RANDOM_PREFIX_PARAM_ID = "randomPrefix";

	// Default Test User
	String TEST_USER_ID = "test";
	String TEST_USERID = "CDUser";

	/**
	 * Define root directory containing the artifacts of a test scenario.
	 */
	File ARTIFACTS_DIR = new File(getParameterValue("artifacts.dir", "artifacts"));

	/**
	 * Define the directory containing the downloaded artifacts of a test scenario.
	 */
	File DOWNLOAD_DIR = new File(getParameterValue(BROWSER_DOWNLOAD_DIR_ID, BROWSER_DOWNLOAD_DIR_VALUE));

	// Indicators of a dependency of a test
	String CLASS_INDICATOR_OF_DEPENDENCY = "${class}";
	String PACKAGE_INDICATOR_OF_DEPENDENCY = "${package}";
}
