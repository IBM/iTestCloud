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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.io.File;

import org.openqa.selenium.firefox.*;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.ibm.itest.cloud.common.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.utils.FileUtil;

/**
 * The browser class when Firefox is used to run the tests.
 */
public class FirefoxBrowser extends WebBrowser {

	// Firefox specific info
	FirefoxProfile firefoxProfile;

public FirefoxBrowser() {
	super("Firefox");
}

private void initDownloadDir() {
	// Don't show download manager
	this.firefoxProfile.setPreference("browser.download.manager.showWhenStarting", FALSE);

	// # 0 means to download to the desktop,
	// 1 means to download to the default "Downloads" directory,
	// 2 means to use the directory you specify in "browser.download.dir"
	this.firefoxProfile.setPreference("browser.download.folderList", new Integer(2));

	// Without asking a location, download files to the directory specified in browser.download.folderList
	this.firefoxProfile.setPreference("browser.download.useDownloadDir", TRUE);

	// Set download directory.
	this.firefoxProfile.setPreference("browser.download.dir", this.downloadDir.getAbsolutePath());

	// Never ask when saving all files.
	this.firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
		"application/x-msexcel;audio/aac;application/x-abiword;application/x-freearc;image/avif;video/x-msvideo;"
		+ "application/octet-stream;image/bmp;application/x-bzip;application/x-bzip2;application/x-cdf;application/x-csh;text/css;text/csv;"
		+ "application/msword;application/vnd.openxmlformats-officedocument.wordprocessingml.document;application/vnd.ms-fontobject;"
		+ "application/epub+zip;application/gzip;image/gif;text/html;image/vnd.microsoft.icon;text/calendar;application/java-archive;"
		+ "image/jpeg;text/javascript;application/json;application/ld+json;audio/midi;audio/x-midi;text/javascript;audio/mpeg;video/mp4;"
		+ "video/mpeg;application/vnd.apple.installer+xml;application/vnd.oasis.opendocument.presentation;"
		+ "application/vnd.oasis.opendocument.spreadsheet;application/vnd.oasis.opendocument.text;audio/ogg;video/ogg;application/ogg;"
		+ "audio/opus;font/otf;image/png;application/pdf;application/x-httpd-php;application/vnd.ms-powerpoint;"
		+ "application/vnd.openxmlformats-officedocument.presentationml.presentation;application/vnd.rar;application/rtf;application/x-sh;"
		+ "image/svg+xml;application/x-shockwave-flash;application/x-tar;image/tiff;video/mp2t;font/ttf;text/plain;application/vnd.visio;"
		+ "audio/wav;audio/webm;video/webm;image/webp;font/woff;font/woff2;application/xhtml+xml;application/vnd.ms-excel;"
		+ "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;application/xml;text/xml;application/vnd.mozilla.xul+xml;"
		+ "application/zip;video/3gpp;audio/3gpp;video/3gpp2;audio/3gpp2;application/x-7z-compressed");
}

@Override
void initDriver() {
	FirefoxOptions firefoxOptions = new FirefoxOptions();
//	firefoxCapabilities.setCapability("unexpectedAlertBehaviour", UnexpectedAlertBehaviour.IGNORE);
//	firefoxCapabilities.setCapability(ELEMENT_SCROLL_BEHAVIOR_ID, ELEMENT_SCROLL_BEHAVIOR_VALUE);

	if(this.remoteAddress != null) {
		// Create driver for executing tests via Selenium Grid.
		this.driver = new RemoteWebDriver(this.remoteAddress, firefoxOptions);
		((RemoteWebDriver) this.driver).setFileDetector(new LocalFileDetector());
	}
	else {
		System.setProperty("webdriver.gecko.driver", this.driverPath);
		System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
		// Create driver for executing tests on local host.
		FirefoxBinary binary = this.path == null ? new FirefoxBinary() : new FirefoxBinary(new File(this.path));
		firefoxOptions.setBinary(binary).setProfile(this.firefoxProfile);
		// Set the headless mode if requested.
		if (this.headless) {
			firefoxOptions.setHeadless(true /*headless*/);
		}
		this.driver = new FirefoxDriver(firefoxOptions);
	}
}

@Override
void initProfile() {
	// Common browsers profile initialization
	super.initProfile();

	// Set profile
	if (this.profile == null) {
		this.firefoxProfile = new FirefoxProfile();
	} else {
		File dir = FileUtil.createDir(this.profile);
		if (dir == null) {
			throw new ScenarioFailedError("Cannot create firefox profile at "+this.profile+"!");
		}
		this.firefoxProfile = new FirefoxProfile(dir);
	}
//	this.firefoxProfile.setEnableNativeEvents(false);

	// Accept untrusted SSL certificates
	this.firefoxProfile.setAcceptUntrustedCertificates(true /*acceptUntrustedSsl*/);

	// Init download dir if necessary
	if (this.downloadDir != null) {
		initDownloadDir();
	}

	// Set browser locale
	if (this.locale != null) {
		this.firefoxProfile.setPreference("intl.accept_languages", this.locale);
	}

	this.firefoxProfile.setPreference("app.update.auto", FALSE);
	this.firefoxProfile.setPreference("app.update.enabled", FALSE);
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
	return true;
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
	return false;
}
}