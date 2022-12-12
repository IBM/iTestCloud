/*********************************************************************
 * Copyright (c) 2013, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.common.scenario.errors;

import com.ibm.itest.cloud.common.pages.dialogs.AbstractDialog;

/**
 * Error to report Server error message.
 */
public abstract class ServerMessageError extends ScenarioFailedError {

public ServerMessageError(final String message) {
	super(message);
}

public ServerMessageError(final String message, final AbstractDialog dialog) {
	super(message, dialog);
}

/**
 * Returns the error message details if any.
 *
 * @return The server error message details as a {@link String} or
 * <code>null</code> if there's no details to show
 */
public abstract String getDetails();

/**
 * Returns the error message summary.
 *
 * @return The server error message summary as a {@link String}.
 */
public abstract String getSummary();

/**
 * Show the details of the Server error message.
 *
 * @return <code>true</code> if the details message has been shown,
 * <code>false</code> otherwise.
 */
abstract public boolean showDetails();
}
