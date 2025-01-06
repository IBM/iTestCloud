/*********************************************************************
 * Copyright (c) 2018, 2024 IBM Corporation and others.
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
package itest.cloud.ibm.page.element;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.page.Page;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This class represents the account menu and manages all its common actions. There are three forms of accounts that this class is to handle.
 *
 * <p>For example, when the real account name is John-Paul Smith, it could have 3 display forms in the account menu.</p>
 * <ul>
 * <li> Form 1: John-Paul Smith's Account
 * <p>The real account name is followed by the suffix of "'s Account" as display name.</p>
 * </li>
 * <li> Form 2: John-Paul Smith
 * <p>The real account name is the display account name.</p>
 * </li>
 * <li> Form 3: 87654321 - John-Paul Smith's Account
 * <p>The real account name is surrounded by the prefix of a number and a dash, and the suffix of "'s Account" as display name.</p>
 * </li>
 * </ul>
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getAccount()}: Return the real account name.</li>
 * <li>{@link #setAccount(String)}: Select a given cloud account.</li>
 * </ul>
 */
public class IbmAccountMenuElement extends IbmDropdownlistElement {

	private static final String ACCOUNT_SUFFIX = "'s Account";
	private static final String ACCOUNT_PREFIX_DELIMITER = " - ";
	private static final String ACCOUNT_PREFIX_REGEX = "^(\\d+" + ACCOUNT_PREFIX_DELIMITER + ")?";

public IbmAccountMenuElement(final Page page) {
	super(page, By.id("dap-account-info"), By.xpath("./a"), By.xpath(".//*[contains(@class,'account-info')]"), By.xpath(".//li/a"));
}

/**
 * Return the name of the currently selected could account.
 *
 * @return The name of the currently selected could account as {@link String}.
 */
public String getAccount() {
	final String selection = getSelection();
	final int beginIndex =
		selection.contains(ACCOUNT_PREFIX_DELIMITER) ? (selection.indexOf(ACCOUNT_PREFIX_DELIMITER) + ACCOUNT_PREFIX_DELIMITER.length()) : 0;
	return selection.substring(beginIndex, selection.contains(ACCOUNT_SUFFIX) ? selection.indexOf(ACCOUNT_SUFFIX) : selection.length()).trim();
}

@Override
public boolean isExpanded() throws ScenarioFailedError {
	return this.element.getAttributeValue("class").contains("open");
}

/**
 * Select a given cloud account.
 *
 * @param account The name of the cloud account as {@link String}.
 */
public void setAccount(final String account) {
	select(Pattern.compile(ACCOUNT_PREFIX_REGEX + account + "(" + ACCOUNT_SUFFIX  + ")?", Pattern.CASE_INSENSITIVE));
}
}