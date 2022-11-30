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
 * A specific error to handle incorrect implementation error while running a scenario.
 */
public class ScenarioImplementationError extends ScenarioFailedError {

public ScenarioImplementationError() {
	super("This part of code should never be called or used.");
}

public ScenarioImplementationError(final String message) {
	super(message);
}

public ScenarioImplementationError(final Exception exception) {
	super(exception);
}

public ScenarioImplementationError(final Exception exception, final boolean print) {
	super(exception, print);
}
}
