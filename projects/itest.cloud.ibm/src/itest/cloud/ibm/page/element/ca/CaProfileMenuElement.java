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
package itest.cloud.ibm.page.element.ca;

import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.element.IbmDynamicExpandableElement;
import itest.cloud.page.Page;

/**
 * This class defines and manages the <b>Profile</b> menu element.
 * <p>
 * The expansion and collapse are done using the avatar icon on page top right corner.
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #logout()}: Perform the logout operation.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * </p>
 */
public class CaProfileMenuElement extends IbmDynamicExpandableElement {

/**
 * Create an instance belonging to the given web page.
 *
 * @param page The page in which the created instance will belong to
 */
public CaProfileMenuElement(final Page page) {
	super(page, By.xpath("//ul[contains(@class,'personalMenu')]"), By.id("com.ibm.bi.glass.common.personalMenu"));
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote(getUser().getName()));
}

@Override
protected By getTitleElementLocator() {
	return By.xpath(".//*[contains(@class,'display-name')]");
}

/**
 * Perform the logout operation.
 */
public void logout() {
	expand();

	waitForElement(By.xpath(".//a[contains(@data-item,'logout')]")).click();
}
}