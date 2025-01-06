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

import static io.appium.java_client.service.local.AppiumDriverLocalService.buildService;
import static io.appium.java_client.service.local.flags.GeneralServerFlag.LOG_LEVEL;
import static itest.cloud.scenario.ScenarioUtil.*;

import java.util.Set;
import java.util.regex.Pattern;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.appium.java_client.NoSuchContextException;
import io.appium.java_client.remote.SupportsContextSwitching;
import io.appium.java_client.service.local.*;
import itest.cloud.entity.BrowserType;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.ScenarioUtil;
import itest.cloud.scenario.error.ScenarioFailedError;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * The abstract browser class to use when running tests on a mobile application using an emulator.
 */
public abstract class MobileEmulator extends Browser {

	protected AppiumDriverLocalService service;

public MobileEmulator(final BrowserType browserType) {
	super(browserType);
}

@Override
public void back() {
	// Back functionality is unsupported on a mobile emulator.
}

@Override
public void close() {
	super.close();
	// Stop the Appium service.
    this.service.stop();
}

/**
 * Returns the capabilities for driver initialization.
 *
 * @param deviceName The name of the device as {@link String}.
 * @param platformName The name of the platform as {@link String}.
 * @param automationName The name of the automation as {@link String}.
 *
 * @return The capabilities for driver initialization as {@link DesiredCapabilities}.
 */
DesiredCapabilities getCapabilities(final String deviceName, final String platformName, final String automationName) {
	// Specify the desired capabilities.
	final DesiredCapabilities capabilities =  new DesiredCapabilities();
	capabilities.setCapability("appium:deviceName", deviceName);
	// The 'platformName' is a standard capability and do not require the 'appium:' prefix.
	capabilities.setCapability("platformName", platformName);
	capabilities.setCapability("appium:automationName", automationName);
	// Specify where the APK file of the application-under-test is located if one provided.
	// This application will be automatically uploaded and installed on the emulator.
	final String applicationPath = getParameterValue("applicationPath");
	if(applicationPath != null) capabilities.setCapability("appium:app", applicationPath);
//	// Specify the maximum depth for traversing elements source tree when an element is searched.
//	capabilities.setCapability("snapshotMaxDepth", Integer.valueOf(getParameterIntValue("snapshotMaxDepth", 200)));
	// Automatically grant all the permissions required by the application-under-test on the emulator.
	capabilities.setCapability("appium:autoGrantPermissions", true);

	return capabilities;
}

/**
 * Get the current page URL.
 * <p>
 * Page URLs are unsupported on a Mobile Emulator. Therefore, the predefined URL {@link ScenarioUtil#MOBILE_APPLICATION_URL} is returned by this method as the current page URL at all time.
 * </p>
 *
 * @return The page URL as a {@link String}.
 */
@Override
public String getCurrentUrl() {
	return MOBILE_APPLICATION_URL;
}

@Override
public SupportsContextSwitching getDriver() {
	return (SupportsContextSwitching) super.getDriver();
}

AppiumServiceBuilder getServiceBuilder() {
	// Initialize the Appium service builder.
	return new AppiumServiceBuilder()
		.withArgument(LOG_LEVEL, "warn")
//		.withLogFile(this.logFile)
		.usingAnyFreePort();
}

@Override
public String getVersion() {
	return String.valueOf(((RemoteWebDriver)this.driver).getCapabilities().getCapability("platformVersion"));
}

@Override
public Set<String> getWindowHandles() throws ScenarioFailedError {
	try {
		return super.getWindowHandles();
	}
	catch (UnsupportedCommandException e) {
		// Do nothing if this operation is unsupported on a mobile emulator.
		return null;
	}
}

@Override
public boolean hasPopupWindow() {
	// Popup windows are unsupported on a mobile emulator.
	return false;
}

@Override
void initDriver() {
	// Initialize the Appium service.
	// Define the Appium service builder.
	this.service = buildService(getServiceBuilder());

    try {
    	// Start the Appium service.
    	this.service.start();
    }
    catch (AppiumServerHasNotBeenStartedLocallyException e) {
    	throw new RuntimeException(e);
    }
}

@Override
void initProfile() {
	// Initializing a profile is not required for a mobile emulator.
}

@Override
protected void initWindow() {
	// Initializing the window is not required for a mobile emulator.
}

@Override
public void refresh() {
	// Refreshing is unsupported on a mobile emulator.
}

/**
 * Scroll the page to a given element.
 * <p>
 * This is a no-op if the web element is already visible in the browser view.
 * </p>
 */
@Override
protected void scrollIntoView(final BrowserElement element) {
	try {
		super.scrollIntoView(element);
	}
	catch (UnsupportedCommandException e) {
		// Do nothing if this operation is unsupported on a mobile emulator.
	}
}

/**
 * Switches to a given context.
 *
 * @param urlPattern The pattern of the name of the context as {@link String}.
 * @param timeout The time to wait before giving up the research for a matching context.
 *
 * @throws NoSuchContextException if a context with name matching the given pattern was unavailable.
 * @see SupportsContextSwitching#context(String)
 */
public void switchToContext(final Pattern urlPattern, final int timeout) {
	if (DEBUG) debugPrintln("		+ Switch to context with name matching pattern '" + urlPattern + "'");

	final SupportsContextSwitching contextSwitchingDriver = getDriver();

	final long timeoutMillis = timeout * 1000 + System.currentTimeMillis();
	while (true) {
		try {
			final Set<String> contextHandles = contextSwitchingDriver.getContextHandles();

			for (String contextHandle : contextHandles) {
				if(urlPattern.matcher(contextHandle).matches()) {
					contextSwitchingDriver.context(contextHandle);
					return;
				}
			}
		}
		catch (WebDriverException e) {
			// A WebDriverException can occur if contexts are accessed while the application is being loaded.
			// No nothing and wait for loading to finish in such a situation.
		}

		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("A context with name matching pattern '" + urlPattern + "' count not be found before the timeout '" + timeout + "'s had reached.");
		}
	}
}

/**
 * Switch to a window with URL matching a given pattern.
 *
 * @param urlPattern The pattern of the URL of the window as {@link String}.
 * @param timeout The time to wait before giving up the research for a matching window.
 *
 * @throws NoSuchWindowException if a window with URL matching the given pattern was unavailable.
 */
public void switchToWindow(final Pattern urlPattern, final int timeout) {
	if (DEBUG) debugPrintln("		+ Switch to window with URL matching pattern '" + urlPattern + "'");

	final long timeoutMillis = timeout * 1000 + System.currentTimeMillis();
	while (true) {
		final Set<String> windowHandles = getWindowHandles();

		for (String windowHandle : windowHandles) {
			this.driver.switchTo().window(windowHandle);

			if(urlPattern.matcher(this.driver.getCurrentUrl()).matches()) return;
		}

		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("A window with URL matching pattern '" + urlPattern + "' count not be found before the timeout '" + timeout + "'s had reached.");
		}
	}
}
}