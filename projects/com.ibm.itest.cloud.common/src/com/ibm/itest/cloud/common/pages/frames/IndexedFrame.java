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
 * Class to manage browser frame identified with an index.
 */
public class IndexedFrame extends BrowserFrame {

	/**
	 * The index of the current frame.
	 */
	private int index = -1;

public IndexedFrame(final Browser browser, final int index) {
	super(browser);
    this.index = index;
}

@Override
public boolean equals(final Object obj) {
	if (obj instanceof IndexedFrame) {
		IndexedFrame frame = (IndexedFrame) obj;
		return frame.index == this.index;
	}
	return super.equals(obj);
}

@Override
public int getIndex() {
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
public void switchTo() {
	if (DEBUG) debugPrintln("		+ Switch to "+this);
	this.driver.switchTo().defaultContent();
	this.driver.switchTo().frame(this.index);

}

@Override
public String toString() {
	return "Frame indexed "+this.index;
}

}
