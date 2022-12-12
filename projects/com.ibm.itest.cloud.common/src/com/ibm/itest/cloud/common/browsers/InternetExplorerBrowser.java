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
package com.ibm.itest.cloud.common.browsers;

import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * The browser class when Internet Explorer is used to run the tests.
 */
public class InternetExplorerBrowser extends WebBrowser {

public InternetExplorerBrowser() {
	super("Internet Explorer");
}

@Override
void initDriver() {
	System.setProperty("webdriver.ie.driver", this.driverPath);
	InternetExplorerOptions options = new InternetExplorerOptions();
//	DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
//	ieCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
////	ieCapabilities.setCapability(ELEMENT_SCROLL_BEHAVIOR_ID, ELEMENT_SCROLL_BEHAVIOR_VALUE);
//	ieCapabilities.setCapability("ie.ensureCleanSession", true);
//	ieCapabilities.setCapability("javascriptEnabled", true);
//	ieCapabilities.setCapability("ignoreZoomSetting", true);

	if(this.remoteAddress != null) {
		// Create driver for executing tests via Selenium Grid.
		this.driver = new RemoteWebDriver(this.remoteAddress, options);
		((RemoteWebDriver) this.driver).setFileDetector(new LocalFileDetector());
	}
	else {
		// Create driver for executing tests on local host.
		this.driver = new InternetExplorerDriver(options);
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
	return true;
}

@Override
public boolean isSafari() {
	return false;
}
}