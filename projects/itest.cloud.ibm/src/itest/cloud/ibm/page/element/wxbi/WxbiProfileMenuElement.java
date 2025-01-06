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
package itest.cloud.ibm.page.element.wxbi;

import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.dialog.wxbi.WxbiLogOutDialog;
import itest.cloud.ibm.page.element.IbmExpandableElement;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This class defines and manages the <b>Profile</b> menu element.
 * <p>
 * The expansion and collapse are done using the avatar icon on page top right corner.
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #isExpandable()}:
 * Returns whether the current wrapped web element is expandable or not.</li>
 * <li>{@link #isExpanded()}: Returns whether the current wrapped web element is expanded or not.</li>
 * <li>{@link #logout()}: Perform the logout operation.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getExpandableAttribute()}: Return the expandable attribute.</li>
 * </ul>
 * </p>
 */
public class WxbiProfileMenuElement extends IbmExpandableElement {

	public static final By USERNAME_ELEMENT_LOCATOR = By.xpath("//*[@class='username']");

/**
 * Create an instance belonging to the given web page.
 *
 * @param page The page in which the created instance will belong to
 */
public WxbiProfileMenuElement(final Page page) {
	super(page, By.id("dap-profile"), By.xpath(".//a[contains(@class,'toggle')]"));
}

@Override
protected String getExpandableAttribute() {
	throw new ScenarioFailedError("This method should never be called.");
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote(getUser().getName()));
}

@Override
protected By getTitleElementLocator() {
	return USERNAME_ELEMENT_LOCATOR;
}

@Override
public boolean isExpandable() throws ScenarioFailedError {
	return true;
}

@Override
public boolean isExpanded() throws ScenarioFailedError {
	BrowserElement sidebarElement =
		waitForElement(By.xpath(".//ul[contains(@class,'dropdown-menu')]"), timeout(), true /*fail*/, false /*displayed*/);
	return sidebarElement.isDisplayed() && sidebarElement.getAttributeValue("class").contains("open");
}

/**
 * Perform the logout operation.
 */
public void logout() {
	expand();

	final WxbiLogOutDialog logOutDialog = new WxbiLogOutDialog(getPage());
	logOutDialog.open(waitForElement(By.xpath(".//a[.='Log out']")));
	logOutDialog.close();
}
}