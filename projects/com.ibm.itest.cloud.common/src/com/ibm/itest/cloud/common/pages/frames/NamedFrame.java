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
package com.ibm.itest.cloud.common.pages.frames;

import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.DEBUG;
import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.debugPrintln;

import com.ibm.itest.cloud.common.browsers.Browser;

/**
 * Class to manage browser frame identified with a name.
 */
public class NamedFrame extends BrowserFrame {

	/**
	 * The name of the current frame.
	 */
	private String name;

public NamedFrame(final Browser browser, final String name) {
	super(browser);
    this.name = name;
}

@Override
public boolean equals(final Object obj) {
	if (obj instanceof NamedFrame) {
		NamedFrame frame = (NamedFrame) obj;
		return this.name.equals(frame.name);
	}
	if (obj instanceof String) {
		return this.name.equals(obj);
	}
	return super.equals(obj);
}

@Override
public String getName() {
	return this.name;
}

@Override
public int hashCode() {
	return this.name.hashCode();
}

/**
 * Select current frame.
 */
@Override
public void switchTo() {
	if (DEBUG) debugPrintln("		+ Switch to "+this);
	this.driver.switchTo().defaultContent();
	this.driver.switchTo().frame(this.name);

}

@Override
public String toString() {
	return "Frame named '"+this.name+"'";
}

}
