/*********************************************************************
 * Copyright (c) 2012, 2023 IBM Corporation and others.
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

import static itest.cloud.entity.BrowserType.CHROME;
import static java.lang.Boolean.valueOf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.chrome.*;
import org.openqa.selenium.chrome.ChromeDriverService.Builder;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * The browser class when Chrome is used to run the tests.
 */
public class ChromeBrowser extends Browser {

	private ChromeDriverService service;
	private ChromeOptions options;

public ChromeBrowser() {
	super(CHROME);
}

@Override
public void close() {
	super.close();
    this.service.stop();
}

@Override
void initDriver() {
    // Create driver
	// Accept untrusted SSL certificates.
	this.options.addArguments("ignore-certificate-errors");

	// Disable "navigator.webdriver" in ChromeDriver to avoid bot detection policy
	this.options.addArguments("--disable-blink-features=AutomationControlled");

	// Allow all remote origins.
	this.options.addArguments("--remote-allow-origins=*");

	// Set the headless mode if requested.
	if (this.headless) {
		this.options.addArguments("--headless");
		this.options.addArguments("--disable-impl-side-painting");
		this.options.addArguments("--disable-dev-shm-usage");
    }

    if(this.remoteAddress != null) {
    	// Configure executing tests via a Selenium Grid.
		// Allow downloading files from the remote computer (end-node) to the client computer.
		this.options.setEnableDownloads(true /*enableDownloads*/);
		// Create the driver.
		this.driver = new RemoteWebDriver(this.remoteAddress, this.options);
		// Allow uploading files from the client computer to the remote computer (end-node).
		((RemoteWebDriver) this.driver).setFileDetector(new LocalFileDetector());
	}
	else {
    	// Configure executing tests on the local host.
		// Create the driver.
		final Builder builder = new ChromeDriverService.Builder().usingAnyFreePort();
		// Specify the path to the Chrome Driver if one is provided. Otherwise, Selenium will automatically
		// download an appropriate version of the Chrome Driver based on the version of the browser in use.
		if(this.driverPath != null) {
			builder.usingDriverExecutable(new File(this.driverPath));
		}

		this.service = builder.build();
        try {
    		// Start the service
        	this.service.start();
        }
        catch (IOException e) {
        	throw new RuntimeException(e);
        }

        this.driver = new ChromeDriver(this.service, this.options);
	}
}

private void initExperimentalOptions() {
	// Set experimental options.
	final Map<String, Object> prefs = new HashMap<String, Object>();

	// Init download dir if necessary.
	if (this.downloadDir != null) {
		// Default download directory
		prefs.put("download.default_directory", this.downloadDir.getAbsolutePath());
		prefs.put("download.directory_upgrade", valueOf(true));
		// No prompt while download a file
		prefs.put("download.prompt_for_download", valueOf(false));
	}

	// Set browser locale if necessary.
	if (this.locale != null) {
		prefs.put("intl.accept_languages", this.locale);
	}

	// Set above options
	this.options.setExperimentalOption("prefs", prefs);
}

@Override
void initProfile() {

	// Common browsers profile initialization
	super.initProfile();

	// Set options
	this.options = new ChromeOptions();

	// Start browser in maximized mode, no-sandbox for root user
	this.options.addArguments("--start-maximized", "--no-sandbox");

	// A default download directory can not be set via the setExperimentalOptions method
	// if a custom profile is used for the test execution. Therefore, a profile directory
	// is only specified for the test execution if a default download directory is not
	// provided. In other words, the default profile will be used for the test execution
	// if a default download directory is provided.
	if (this.profile != null) {
		this.options.addArguments("--user-data-dir=" + this.profile);
	}

	// Set the browser locale if one provided.
	if (this.locale != null) {
		this.options.addArguments("--lang=" + this.locale);
	}

    // Init experimental options.
	initExperimentalOptions();
}
}