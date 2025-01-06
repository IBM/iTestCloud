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
package itest.cloud.ibm.topology;

import static itest.cloud.ibm.entity.ApplicationType.CPD;
import static itest.cloud.ibm.entity.ApplicationType.WXBI;
import static itest.cloud.scenario.ScenarioUtil.getParameterValue;

import itest.cloud.ibm.entity.ApplicationType;
import itest.cloud.topology.Application;

/**
 * Class to manage the WatsonX BI Assistant application.
 * <p>
 * Following public features are defined by this page:
 * <ul>
 * <li>{@link #getHomePageUrl()}: Returns the URL of the home page.</li>
 * <li>{@link #getModelingPageUrl()}: Returns the URL of the modeling page.</li>
 * <li>{@link #isCloudApplication()}: Specifies whether the test environment is the cloud offering of the WXBI application.</li>
 * </ul>
 * </p>
 */
public class WxbiApplication extends Application {

	/**
	 * The type of the application as {@link ApplicationType}.
	 */
	public static final ApplicationType APPLICATION_TYPE =
		ApplicationType.valueOf(getParameterValue("applicationType", WXBI.name()));

public WxbiApplication(final String url) {
	super(url);
}

/**
 * Returns the URL of the home page.
 *
 * @return The URL of the home page as {@link String}.
 */
public String getHomePageUrl() {
	return getWxbiUrlPath() + "?context=wxbi";
}

/**
 * Returns the URL of the modeling page.
 *
 * @return The URL of the modeling page as {@link String}.
 */
public String getModelingPageUrl() {
	return getWxbiUrlPath() + "/modelling/4f10503a-8914-4dd1-a8b1-f09b26ed49af@99d4383e-81b5-4bca-8d08-e8c759a7676d@project_id";
}

private String getWxbiUrlPath() {
	return getLocation() + "/wxbi";
}

/**
 * Specifies whether the test environment is the cloud offering of the WXBI application.
 *
 * @return <code>true</code> if the test environment is the cloud offering of the WXBI application or <code>false</code> otherwise.
 */
public boolean isCloudApplication() {
	return !getLocation().toLowerCase().contains(CPD.name().toLowerCase());
}
}