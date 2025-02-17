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
package itest.cloud.scenario.error;

import itest.cloud.page.dialog.Dialog;

/**
 * Error sent when a timeout is reached while waiting for an information in
 * the corresponding page (e.g. a web element).
 */
public class WaitElementTimeoutError extends ScenarioFailedError {

public WaitElementTimeoutError(final String message) {
	super(message);
}

public WaitElementTimeoutError(final String message, final Dialog dialog) {
	super(message, dialog);
}

public WaitElementTimeoutError(final Throwable ex) {
	super(ex);
}

public WaitElementTimeoutError(final Throwable ex, final Dialog dialog) {
	super(ex, dialog);
}

}
