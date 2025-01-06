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
package itest.cloud.ibm.page.ca;

import static itest.cloud.ibm.entity.ApplicationType.CA;
import static itest.cloud.scenario.ScenarioUtil.*;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.config.IbmUser;
import itest.cloud.ibm.page.IbmPage;
import itest.cloud.ibm.page.element.IbmDropdownlistElement;
import itest.cloud.ibm.page.element.ca.CaProfileMenuElement;
import itest.cloud.ibm.page.element.ca.CaSideMenuElement;
import itest.cloud.ibm.page.wxbia.WxbiHomePage;
import itest.cloud.ibm.scenario.IbmScenarioLoginError;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.BrowserError;

/**
 * This class represents a generic web page in the Cognos Analytics application and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #isInApplicationContext()}: Specifies whether the web page is in the context of the application.</li>
 * <li>{@link #openHomePage()}: Open the 'Overview' page by clicking on the IBM Data Portal link.</li>
 * <li>{@link #openSideMenu()}: Open the side menu.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getApplicationTitleElementLocator()}: Return the locator of the element containing the title of the application..</li>
 * <li>{@link #getExpectedApplicationTitle()}: Returns the expected title of the application.</li>
 * <li>{@link #getLoggedUserElementLocator()}: Return the locator of the web element displaying the logged user name.</li>
 * <li>{@link #matchDisplayedUser(User, BrowserElement)}: Return whether the displayed user matches the user name or not.</li>
 * <li>{@link #performLogin(User)}: Perform login operation on the current page to be connected to the given user.</li>
 * <li>{@link #performLogout()}: Logout the page from current user to new user.</li>
 * </ul>
 * </p>
 */
public abstract class CaPage extends IbmPage {

	private static final By USER_NAME_TEXT_FIELD_LOCATOR = By.id("CAMUsername");
	private static final By NAVIGATION_TITLE_LINK_LOCATOR = By.xpath("//a[contains(@class,'logoText')]");

public CaPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

public CaPage(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

@Override
protected By getApplicationTitleElementLocator() {
	return NAVIGATION_TITLE_LINK_LOCATOR;
}

@Override
protected Pattern getExpectedApplicationTitle() {
	return CA.getTitle();
}

@Override
protected By getLoggedUserElementLocator() {
	return By.xpath("//*[(@id='startupInfo') and contains(text(),'userName')]");
}

@Override
public boolean isInApplicationContext() {
	return waitForElement(NAVIGATION_TITLE_LINK_LOCATOR, timeout(), false /*fail*/) != null;
}

@Override
protected boolean matchDisplayedUser(final User user, final BrowserElement loggedUserElement) {
	final String loggedUserInfo = loggedUserElement.getText();
	return (loggedUserInfo != null) && loggedUserInfo.contains("\"defaultName\":\"" + user.getName() + "\"");
}

/**
 * Open the 'Home' page by clicking on the application title link.
 *
 * @return The opened 'Home' page as a {@link WxbiHomePage}
 */
public CaHomePage openHomePage() {
	if (DEBUG) debugPrintln("		+ Goto home page using application title link.");
	return openPageUsingLink(NAVIGATION_TITLE_LINK_LOCATOR, CaHomePage.class);
}

/**
 * Open the side menu.
 *
 * @return The opened side menu as {@link CaSideMenuElement}.
 */
public CaSideMenuElement openSideMenu() {
	final CaSideMenuElement sideMenuElement = new CaSideMenuElement(this);
	sideMenuElement.expand();

	return sideMenuElement;
}

@Override
protected void performLogin(final User user) {
	if (DEBUG) debugPrintln("		+ Perform login of user "+user);

	// Test if login is necessary
	if (user == null) {
		debugPrintln("		  -> Do nothing as there's no login");
		return;
	}

	if (user.equals(getApplication().getUser())) {
		debugPrintln("		  -> Do nothing as user is already logged in");
		return;
	}

	if(getLoggedUserElement(false /*fail*/, tinyTimeout()) != null) {
		debugPrintln("		  -> Do nothing as user session has been saved in browser");
		// Store user in application
		this.topology.login(this.browser.getCurrentUrl(), user);
		return;
	}

	// Check if selecting a name space if required.
	final BrowserElement[] loginFormRelatedElements = waitForMultipleElements(By.id("CAMNamespace"), USER_NAME_TEXT_FIELD_LOCATOR);
	if(loginFormRelatedElements[0] != null) {
		final BrowserElement namespaceDropdownElement = loginFormRelatedElements[0];
		// If reached here, it implies that selecting a name space is required. Therefore, do so.
		final IbmDropdownlistElement namespaceListElement = new IbmDropdownlistElement(this, namespaceDropdownElement);
		namespaceListElement.select(((IbmUser)user).getNamespace());
	}

	// Enter the user information.
	final BrowserElement userNameElement = (loginFormRelatedElements[1] != null) ? loginFormRelatedElements[1] : waitForElement(USER_NAME_TEXT_FIELD_LOCATOR);
	typeText(userNameElement, user.getId());
	typeText(By.id("CAMPassword"), user.getPassword());
	// Click the Login button.
	clickButton(By.id("signInBtn"));

	// Look for login errors and take an appropriate action.
	final BrowserElement[] loginErrorRelatedElements = waitForMultipleElements(
		new By[] {By.xpath("//*[contains(@class,'incorrectLogin')]//*[contains(@class,'_title')]"), getLoggedUserElementLocator()},
		timeout(), true /*fail*/, new boolean[] {true, false} /*displayFlags*/);
	final BrowserElement loggedUserElement = loginErrorRelatedElements[1];
	if(loggedUserElement == null) {
		// If a login error occurs, throw an exception.
		final BrowserElement errorMessageElement = loginErrorRelatedElements[0];
		throw new IbmScenarioLoginError("The following error occurred during log in operation: " + errorMessageElement.getText());
	}

	// Check if the page is in application context.
	// Sometimes blank or error pages are loaded due to various product defects.
	if(!isInApplicationContext()) {
		// A BrowserError must be raised in such a situation.
		throw new BrowserError("Web page '" + getUrl() + "' is out of scope/context of application '" + getApplication().getName() + "'");
	}

	// Store user in the application.
	this.topology.login(this.browser.getCurrentUrl(), user);
}

/**
 * Open the Profile menu.
 *
 * @return The opened Profile menu as {@link CaProfileMenuElement}.
 */
private CaProfileMenuElement openProfileMenu() {
	// Dismiss any existing alerts since they can overlap with the profile menu.
	dismissAlerts(false /*fail*/);

	CaProfileMenuElement profileMenuElement = new CaProfileMenuElement(this);
	profileMenuElement.expand();

	return profileMenuElement;
}

/**
 * {@inheritDoc}
 * <p>
 * In a distributed environment, a new browser session is opened for the new user
 * to avoid having cached login data.
 * </P>
 */
@Override
protected void performLogout() {
	if (DEBUG) debugPrintln("		+ Logout current page from user '"+(getUser()==null?"null":getUser().getId())+"'");

	// No logout necessary
	if (getUser() == null) {
		debugPrintln("		  -> Do nothing as there's no login");
		return;
	}

	final CaProfileMenuElement profileMenuElement = openProfileMenu();
	profileMenuElement.logout();

	// Wait for login button to reappear.
	// Sometimes a blank or error page is loaded due to various product defects.
	if(waitForMultipleElements(timeout(), false /*fail*/, getLoggedUserElementLocator(), USER_NAME_TEXT_FIELD_LOCATOR) == null) {
		// A BrowserError must be raised in such a situation.
		throw new BrowserError("Web page '" + getUrl() + "' does not contain sign-in elements");
	}

	this.refreshed = false;

	// Discard the current browser session and open a new if it is a distributed
	// environment or requested to do so by user.
	if (getTopology().isDistributed() || this.browser.newSessionPerUser()) {
		println("		+ Closing browser session for " + getUser() + " and starting new session");
		startNewBrowserSession(); // Which also clears login data
	}
	else {
		// Clear login data
		getTopology().logoutApplications();
	}
}
}