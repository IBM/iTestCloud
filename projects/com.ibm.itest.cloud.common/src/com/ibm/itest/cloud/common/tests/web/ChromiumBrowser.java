/*********************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.common.tests.web;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.getParameterValue;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.chrome.*;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
/**
 * The browser class when Chromium or Electron is used to run the tests.
 */
public class ChromiumBrowser extends WebBrowser {

	private ChromeDriverService service;

public ChromiumBrowser() {
	super("Chromium");
}

@Override
public void close() {
	super.close();
    this.service.stop();
}

@Override
public boolean hasPopupWindow() {
	// Two window handles exist in Chromium or Electron browser by default even though only one window
	// handle exists in other browsers instead.
	if (getWindowHandles().size() > 2) {
		return true;
	}
	this.framePopup = null;
	return false;
}

@Override
void initDriver() {
	ChromeOptions options = new ChromeOptions();
	// Specify the parth to the Chromium or Electron application.
	options.setBinary(getParameterValue("applicationPath"));

    // Create driver
	if(this.remoteAddress != null) {
		// Create driver for executing tests via Selenium Grid.
		options.addArguments("ignore-certificate-errors");
		this.driver = new RemoteWebDriver(this.remoteAddress, options);
		((RemoteWebDriver) this.driver).setFileDetector(new LocalFileDetector());
	}
	else {
		// Create driver for executing tests on local host.
		// Start service
//		System.setProperty("webdriver.chrome.driver", this.driverPath);
//		System.setProperty("webdriver.chrome.logfile", BROWSER_LOG_FILE);
		this.service = new ChromeDriverService.Builder()
	            .usingDriverExecutable(new File(this.driverPath))
	            .usingAnyFreePort()
	            .build();
        try {
        	this.service.start();
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }

        this.driver = new ChromeDriver(this.service, options);
	}
//	this.driver.manage().timeouts().implicitlyWait(ZERO.plusMillis(250));
}

@Override
void initProfile() {
}

@Override
protected void initWindow() {
	// Store the main window handle
	this.mainWindowHandle = this.driver.getWindowHandle();
}

@Override
public boolean isChromium() {
	return true;
}

@Override
public boolean isEdge() {
	return false;
}

@Override
public boolean isFirefox() {
	return false;
}

@Override
public boolean isGoogleChrome() {
	return true;
}

@Override
public boolean isInternetExplorer() {
	return false;
}

@Override
public boolean isSafari() {
	return false;
}
}
