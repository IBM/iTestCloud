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
package itest.cloud.ibm.topology;

import static itest.cloud.scenario.ScenarioUtil.getParameterValue;

import itest.cloud.ibm.entity.ApplicationType;
import itest.cloud.topology.Application;

/**
 * Class to manage the Cognos Analytics application.
 * <p>
 * Following public features are defined by this page:
 * <ul>
 * <li>{@link #getBaseUrl()}: Returns the base URL.</li>
 * <li>{@link #getBiSubdirectoryUrl()}: Returns the URL of the BI sub-directory.</li>
 * <li>{@link #getHomePageUrl()}: Returns the URL of the home page.</li>
 * <li>{@link #getPinboardViewUrl()}: Returns the URL of the PinboardView.</li>
 * </ul>
 * </p>
 */
public class CaApplication extends Application {

	/**
	 * The type of the application as {@link ApplicationType}.
	 */
	public static final ApplicationType APPLICATION_TYPE =
		ApplicationType.valueOf(getParameterValue("applicationType", ApplicationType.CA.name()));

public CaApplication(final String url) {
	super(url);
}

/**
 * Returns the base URL.
 *
 * @return The base URL as {@link String}.
 */
public String getBaseUrl() {
	return getLocation() + "/";
}

/**
 * Returns the URL of the BI sub-directory.
 *
 * @return The URL of the BI sub-directory as {@link String}.
 */
public String getBiSubdirectoryUrl() {
	return getBaseUrl() + "bi/";
}

/**
 * Returns the URL of the home page.
 *
 * @return The URL of the home page as {@link String}.
 */
public String getHomePageUrl() {
	return getPerspectiveQueryUrl() + "home";
}

private String getPerspectiveQueryUrl() {
	return getBiSubdirectoryUrl() + "?perspective=";
}

/**
 * Returns the URL of the PinboardView.
 *
 * @return The URL of the PinboardView {@link String}.
 */
public String getPinboardViewUrl() {
	return getPerspectiveQueryUrl() + "mobilePinboardView";
}
}