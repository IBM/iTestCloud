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
package com.ibm.itest.cloud.common.tests.scenario;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.*;

import java.util.*;

import com.ibm.itest.cloud.common.pages.dialogs.AbstractDialog;
import com.ibm.itest.cloud.common.pages.elements.WebBrowserElement;
import com.ibm.itest.cloud.common.tests.scenario.errors.WaitElementTimeoutError;
import com.ibm.itest.cloud.common.tests.web.*;
import com.ibm.itest.cloud.common.tests.web.browsers.WebBrowser;

/**
 * Manage workaround applied when running a scenario.
 * <p>
 * </p>
 * Design Need to be finalized Workaround
 */
abstract public class ScenarioWorkaround<P extends WebPage> {

	/* Constants */
	private final static Set<String> WORKAROUNDED_PAGES = new HashSet<String>();

	/* Fields */
	private String message;
	private boolean shouldFail;
	private long id;
	protected P page;
	protected AbstractDialog dialog;

public ScenarioWorkaround(final P page, final String msg) {
	this(page, msg, true, null);
}

public ScenarioWorkaround(final P page, final String msg, final boolean fail) {
	this(page, msg, fail, null);
}

public ScenarioWorkaround(final P page, final String msg, final boolean fail, final AbstractDialog dialog) {
	this(page, msg, fail, dialog, true /* report */);
}

public ScenarioWorkaround(final P page, final String msg, final boolean fail, final AbstractDialog dialog, final boolean report) {
	this.message = msg;
	this.page = page;
	this.dialog = dialog;
	this.id = System.currentTimeMillis();
	this.shouldFail = fail;

	if (report) {
		println("WORKAROUND: " + this.message);
		page.takeSnapshotWarning(getClassSimpleName(page.getClass()) + "_Workaround");
	}

	if (WORKAROUNDED_PAGES.contains(page.getLocation())) {
		if (fail) {
			if (dialog != null) {
				dialog.cancel();
			}
			throw new WaitElementTimeoutError(this.message);
		}
	} else {
		WORKAROUNDED_PAGES.add(page.getLocation());
	}
}

public ScenarioWorkaround(final P page, final String msg, final AbstractDialog dialog) {
	this(page, msg, true, dialog);
}

/**
 * Execute an action to workaround the failure.
 * <p>
 * Subclass has to specify what to do to workaround the problem.
 * </p>
 */
abstract public WebBrowserElement execute();

/**
 * Return the web browser.
 *
 * @return The browser as a {@link WebBrowser}.
 */
public WebBrowser getBrowser() {
	return this.page.getBrowser();
}

/**
 * Get the workaround timestamp.
 *
 * @return The timestamp as a {@link String} with 'YYYYMMDD-HHMMSS' format.
 */
public String getTimestamp() {
	return COMPACT_DATE_FORMAT.format(new Date(this.id));
}

/**
 * Returns whether the current workaround should raise a failure at the end of the test execution.
 *
 * @return <code>true</code> if the workaround should raise a failure, <code>false</code> otherwise.
 */
public boolean shouldFail() {
	return this.shouldFail;
}

@Override
public String toString() {
	final String kind = this.shouldFail ? "failure" : "normal";
	return ("WORKAROUND: time creation=" + getTimestamp() + "', message= '" + this.message + "', kind=" + kind);
}
}
