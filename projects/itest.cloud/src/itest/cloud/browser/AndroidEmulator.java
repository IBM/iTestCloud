/*********************************************************************
 * Copyright (c) 2024, 2025 IBM Corporation and others.
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

import static itest.cloud.entity.BrowserType.ANDROID;
import static itest.cloud.scenario.ScenarioUtil.getParameterValue;

import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumServiceBuilder;

/**
 * The browser class to use when running tests on a mobile application using an Android emulator.
 */
public class AndroidEmulator extends MobileEmulator {

public AndroidEmulator() {
	super(ANDROID);
}

@Override
public AndroidDriver getDriver() {
	return (AndroidDriver) super.getDriver();
}

@Override
AppiumServiceBuilder getServiceBuilder() {
	// Initialize the Appium service builder with arguments specific to an Android Emulator.
	return super.getServiceBuilder()
		// Automatically download an appropriate version of chromedriver when dealing with a WebView.
		.withArgument(() -> "--allow-insecure", "chromedriver_autodownload");
}

@Override
void initDriver() {
	super.initDriver();

	// Specify the desired capabilities.
	final DesiredCapabilities capabilities = getCapabilities(
		"emulator-5554" /*deviceName*/, "ANDROID" /*platformName*/, "uiautomator2" /*automationName*/);
	// Specify the Android version used on the emulator. E.g. 15.
	capabilities.setCapability("appium:platformversion", getParameterValue("platformVersion"));
//	// Specify the path to the Chrome driver, which is used when dealing with a WebView.
//	capabilities.setCapability("appium:chromedriverExecutable", this.driverPath);

	// Create driver.
	this.driver = new AndroidDriver(this.service, capabilities);
}
}