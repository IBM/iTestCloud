/*********************************************************************
 * Copyright (c) 2014, 2022 IBM Corporation and others.
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


/**
 * A specific error to handle missig implementation error while running a scenario.
 */
public class ScenarioMissingImplementationError extends ScenarioFailedError {

public ScenarioMissingImplementationError(final String message) {
	super("Missing implementation of "+message);
}

public ScenarioMissingImplementationError(final Throwable ex) {
	super(ex);
}

public ScenarioMissingImplementationError(final Throwable ex, final boolean print) {
	super(ex, print);
}

public ScenarioMissingImplementationError(final StackTraceElement whoAmI) {
	super("Missing implementation of "+whoAmI.toString());
}
}
