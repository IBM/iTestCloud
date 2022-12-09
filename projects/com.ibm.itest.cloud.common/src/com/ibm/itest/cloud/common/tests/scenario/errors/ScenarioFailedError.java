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
package com.ibm.itest.cloud.common.tests.scenario.errors;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.*;

import com.ibm.itest.cloud.common.pages.dialogs.AbstractDialog;
import com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils;

import junit.framework.AssertionFailedError;

/**
 * Manage scenario failure.
 * <p>
 * Nothing special is done, this class has just been created to let users identify
 * this framework specific error and catch it if necessary.
 * </p>
 * Design: To be finalized
 */
public class ScenarioFailedError extends AssertionFailedError {
	final Throwable error;
	final protected AbstractDialog dialog;

public ScenarioFailedError(final String message) {
	this(message, false);
}

public ScenarioFailedError(final String message, final boolean print) {
	this(message, null, print);
}

public ScenarioFailedError(final String message, final AbstractDialog dialog) {
	this(message, dialog, false);
}

public ScenarioFailedError(final String message, final AbstractDialog dialog, final boolean print) {
	super(message);
	if (print) {
		println(message);
		ScenarioUtils.printStackTrace(1);
	} else {
		debugPrintln(message);
	}
	this.error = null;
	this.dialog = dialog;
}

public ScenarioFailedError(final Throwable ex) {
	this(ex, false);
}

public ScenarioFailedError(final Throwable ex, final boolean print) {
	this(ex, null, print);
}

public ScenarioFailedError(final Throwable ex, final AbstractDialog dialog) {
	this(ex, dialog, false);
}

public ScenarioFailedError(final Throwable ex, final AbstractDialog dialog, final boolean print) {
	super(ex.getMessage() == null ? getClassSimpleName(ex.getClass()) : ex.getMessage());
	this.error = ex;
	this.dialog = dialog;
	if (print) {
		printException(ex);
	} else {
		debugPrintException(ex);
	}
}

/**
 * Cancels the dialog where the error msg is displayed.
 */
public void cancel() {
	if (this.dialog != null) {
		this.dialog.cancel();
	}
}
}
