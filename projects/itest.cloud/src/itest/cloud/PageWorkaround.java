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
package itest.cloud;

import static itest.cloud.scenario.ScenarioUtil.debugPrintln;

import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.ScenarioWorkaround;

public class PageWorkaround extends ScenarioWorkaround<Page> {

public PageWorkaround(final Page page, final String msg) {
	super(page, msg);
}

public PageWorkaround(final Page page, final String msg, final boolean fail) {
	super(page, msg, fail);
}

@Override
public BrowserElement execute() {
	debugPrintln("Workaround: try to refresh the entire page...");
	this.page.refresh();
	return null;
}

}
