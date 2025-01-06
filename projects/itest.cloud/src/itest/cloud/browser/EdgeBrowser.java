/*********************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
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

import static itest.cloud.entity.BrowserType.EDGE;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

/**
 * The specialized class when MS Edge browser is used to run the tests.
 * <p>
 * This class defines following internal API methods:
 * <ul>
 * </ul>
 * </p><p>
 * This class also defines or overrides following methods:
 * <ul>
 * <li>{@link #initDriver()}: Init the driver corresponding to the current browser.</li>
 * <li>{@link #initProfile()}: Init the browser profile.</li>
 * </ul>
 * </p>
 */
public class EdgeBrowser extends Browser {

	/* Fields */
	private EdgeOptions options;

public EdgeBrowser() {
	super(EDGE);
}

@Override
protected void initDriver() {
	// Set driver properties
	System.setProperty("webdriver.edge.driver", this.driverPath);

	// Create driver
	this.driver = new EdgeDriver(this.options);
//	this.driver.manage().timeouts().implicitlyWait(ZERO.plusMillis(250));
}

@Override
protected void initProfile() {
	// Created Edge options
	this.options = new EdgeOptions();
}
}