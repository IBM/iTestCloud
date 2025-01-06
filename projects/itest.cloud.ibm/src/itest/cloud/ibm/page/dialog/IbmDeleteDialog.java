/*********************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
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
package itest.cloud.ibm.page.dialog;

import static java.util.regex.Pattern.DOTALL;

import java.util.regex.Pattern;

import itest.cloud.page.Page;

/**
 * This class represents a generic delete dialog in an IBM application and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getPrimaryButtonText()}: Return the text of the primary button.</li>
 * </ul>
 * </p>
 */
public class IbmDeleteDialog extends IbmConfirmationDialog {

public IbmDeleteDialog(final Page page, final String... data) {
	super(page, data);
}

@Override
protected Pattern getExpectedTitle() {
	return Pattern.compile(".*[Delete|Remove].*", DOTALL);
}

@Override
protected String getPrimaryButtonText() {
	return "Delete";
}
}