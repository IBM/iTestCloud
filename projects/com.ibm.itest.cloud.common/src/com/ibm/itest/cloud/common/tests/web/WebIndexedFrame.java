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
package com.ibm.itest.cloud.common.tests.web;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.DEBUG;
import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.debugPrintln;

/**
 * Class to manage browser frame identified with an index.
 */
public class WebIndexedFrame extends WebBrowserFrame {

	/**
	 * The index of the current frame.
	 */
	private int index = -1;

WebIndexedFrame(final WebBrowser browser, final int index) {
	super(browser);
    this.index = index;
}

@Override
public boolean equals(final Object obj) {
	if (obj instanceof WebIndexedFrame) {
		WebIndexedFrame frame = (WebIndexedFrame) obj;
		return frame.index == this.index;
	}
	return super.equals(obj);
}

@Override
int getIndex() {
	return this.index;
}

@Override
public int hashCode() {
	return this.index;
}

/**
 * Select current frame.
 */
@Override
void switchTo() {
	if (DEBUG) debugPrintln("		+ Switch to "+this);
	this.driver.switchTo().defaultContent();
	this.driver.switchTo().frame(this.index);

}

@Override
public String toString() {
	return "Frame indexed "+this.index;
}

}
