/*********************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
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
package itest.cloud.acme.pages.dialogs;

import java.util.regex.Pattern;

import itest.cloud.pages.Page;
import itest.cloud.scenario.errors.ScenarioFailedError;

/**
 * This class defines and manages a generic delete dialog.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getCloseButton(boolean)}: Return the xpath of the button to
 * close the window.</li>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title
 * for the current dialog.</li>
 * <li>{@link #getPrimaryButtonText()}: Return the text of the primary button.</li>
 * </ul>
 * </p>
 */
public class AcmeDeleteDialog extends AcmeConfirmationDialog {

public AcmeDeleteDialog(final Page page) {
	super(page);
}

public AcmeDeleteDialog(final Page page, final String... data) {
	super(page, data);
}

@Override
protected String getCloseButton(final boolean validate) {
//	return validate ? ".//button[(.='Delete') or (.='Remove') or (.='Revoke') or (.='Stop') or starts-with(.,'Cancel')]" : CANCEL_BUTTON_XPATH;
	return validate ?
		((this.data != null) && (this.data.length >= 2) && (this.data[1] != null) ?
			(".//button[starts-with(.,'" + this.data[1] + "')]") :
			(".//button[(.='Delete') or (.='Remove') or (.='Revoke') or starts-with(.,'Cancel')]")) :
		CANCEL_BUTTON_XPATH;
}

@Override
protected Pattern getExpectedTitle() {
//	return Pattern.compile("Delete.*|Remove.*|Revoke.*|Stop.*");
	return ((this.data != null) && (this.data.length >= 1) && (this.data[0] != null)) ?
		Pattern.compile(this.data[0] + ".*") : Pattern.compile("Delete.*|Remove.*|Revoke.*");
}

@Override
protected String getPrimaryButtonText() {
	throw new ScenarioFailedError("This method should not be invoked.");
}
}