/*********************************************************************
 * Copyright (c) 2020, 2024 IBM Corporation and others.
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
package itest.cloud.ibm.page.dialog.wxbi;

import java.util.regex.Pattern;

import itest.cloud.ibm.page.dialog.IbmConfirmationDialog;
import itest.cloud.page.Page;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This class defines and manages the Log out dialog and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getCloseButton(boolean)}: Return the xpath of the button to close the window.</li>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title
 * for the current dialog.</li>
 * </ul>
 * </p>
 */
public class WxbiLogOutDialog extends IbmConfirmationDialog {

public WxbiLogOutDialog(final Page page) {
	super(page);
}

@Override
protected String getCloseButton(final boolean validate) {
	return validate ? ".//button[starts-with(.,'Log out')]" : ".//button[.='Cancel']";
}

@Override
protected Pattern getExpectedTitle() {
	return Pattern.compile("Confirm");
}

@Override
protected String getPrimaryButtonText() {
	throw new ScenarioFailedError("This method should not be invoked");
}
}
