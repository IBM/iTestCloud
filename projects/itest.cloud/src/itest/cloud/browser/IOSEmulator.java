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
package itest.cloud.browser;

import static itest.cloud.entity.BrowserType.IOS;
import static itest.cloud.scenario.ScenarioUtil.getParameterValue;

import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.ios.IOSDriver;

/**
 * The browser class to use when running tests on a mobile application using an IOS or IPadOS emulator.
 */
public class IOSEmulator extends MobileEmulator {

public IOSEmulator() {
	super(IOS);
}

@Override
public IOSDriver getDriver() {
	return (IOSDriver) super.getDriver();
}

@Override
void initDriver() {
	super.initDriver();

	// Specify the desired capabilities.
	final DesiredCapabilities capabilities = getCapabilities(
		"iPhone 16" /*deviceName*/, "iOS" /*platformName*/, "XCUITest" /*automationName*/);
	// Specify the bundle id of the application-under-test once it has installed on the emulator.
	final String applicationBundleId = getParameterValue("applicationBundleId");
	if(applicationBundleId != null) capabilities.setCapability("appium:bundleId", applicationBundleId);

	// Create driver.
	this.driver = new IOSDriver(this.service, capabilities);
}
}