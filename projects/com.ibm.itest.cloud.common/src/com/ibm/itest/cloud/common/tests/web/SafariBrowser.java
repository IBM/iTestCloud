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

import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

/**
 * The browser class when Safari is used to run the tests.
 */
public class SafariBrowser extends WebBrowser {

public SafariBrowser() {
	super("Safari");
}

@Override
void initDriver() {
	SafariOptions options = new SafariOptions();

	if(this.remoteAddress != null) {
		// Create driver for executing tests via Selenium Grid.
		this.driver = new RemoteWebDriver(this.remoteAddress, options);
		((RemoteWebDriver) this.driver).setFileDetector(new LocalFileDetector());
	}
	else {
		// Create driver for executing tests on local host.
		this.driver = new SafariDriver(options);
	}
}

@Override
public boolean isChromium() {
	return false;
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
	return false;
}

@Override
public boolean isInternetExplorer() {
	return false;
}

@Override
public boolean isSafari() {
	return true;
}
}