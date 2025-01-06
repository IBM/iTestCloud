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
package itest.cloud.ibm.page.wxbia;

import static itest.cloud.ibm.entity.ApplicationType.WXBI;
import static itest.cloud.ibm.page.element.wxbi.WxbiProfileMenuElement.USERNAME_ELEMENT_LOCATOR;
import static itest.cloud.scenario.ScenarioUtil.*;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.page.IbmPage;
import itest.cloud.ibm.page.dialog.wxbi.WxbiLogOutDialog;
import itest.cloud.ibm.page.element.IbmAccountMenuElement;
import itest.cloud.ibm.page.element.IbmSelectionElement;
import itest.cloud.ibm.page.element.wxbi.WxbiProfileMenuElement;
import itest.cloud.ibm.scenario.IbmScenarioLoginError;
import itest.cloud.ibm.topology.WxbiApplication;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.BrowserError;

/**
 * This class represents a generic web page in the WatsonX BI Assistant application and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getAccount()}: Get the currently selected Cloud account.</li>
 * <li>{@link #getAnonymousId()}: Return the anonymous id of the user in the login session.</li>
 * <li>{@link #getApplication()}: Return the application associated with the current page.</li>
 * <li>{@link #openHomePage()}: Open the 'Overview' page by clicking on the IBM Data Portal link..</li>
 * <li>{@link #setAccount(String)}: Select a given Cloud account.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getApplicationTitleElementLocator()}: Return the locator of the element containing the title of the application..</li>
 * <li>{@link #getExpectedApplicationTitle()}: Returns the expected title of the application.</li>
 * <li>{@link #getLoggedUserElementLocator()}: Return the locator of the web element displaying the logged user name.</li>
 * <li>{@link #matchDisplayedUser(User, BrowserElement)}: Return whether the displayed user matches the user name or not.</li>
 * <li>{@link #openHomePage(Class)}: Open the Home page by clicking on the navigation title link.</li>
 * <li>{@link #performLogin(User)}: Perform login operation on the current page to be connected to the given user.</li>
 * <li>{@link #performLogout()}: Logout the page from current user to new user.</li>
 * </ul>
 * </p>
 */
public abstract class WxbiPage extends IbmPage {

	private static final By CPD_USER_NAME_ELEMENT_LOCATOR = By.id("username-textinput");
	private static final By WXBI_USER_NAME_ELEMENT_LOCATOR = By.id("username");
	private static final By NAVIGATION_TITLE_LINK_LOCATOR = By.xpath("//*[contains(@class,'dap-nav-title-link')]");
	private static final By W3_ID_USER_NAME_ELEMENT_LOCATOR = By.id("user-name-input");

public WxbiPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

public WxbiPage(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

//private WxbiaTheme getTheme() {
//	return getThemeEnum(getBodyElement().getClassAttribute());
//}

/**
 * Get the currently selected Cloud account.
 *
 * @return The currently selected Cloud account as {@link String}.
 */
public String getAccount() {
	IbmAccountMenuElement accountMenuElement = getAccountMenuElement();

	return accountMenuElement.getAccount();
}

private IbmAccountMenuElement getAccountMenuElement() {
	return new IbmAccountMenuElement(this);
}

/**
 * Return the anonymous id of the user in the login session.
 *
 * @return The anonymous id of the user in the login session as {@link String}.
 */
public String getAnonymousId() {
	return (String)this.browser.executeScript("return window.bluemixAnalytics.getAnonymousId()");
}

/**
 * {@inheritDoc}
 * @return The application as a subclass of {@link WxbiApplication}
 */
@Override
public WxbiApplication getApplication() {
	return (WxbiApplication) super.getApplication();
}

@Override
protected By getApplicationTitleElementLocator() {
	return By.xpath("//*[contains(@class,'desktop-title')]");
}

@Override
protected Pattern getExpectedApplicationTitle() {
	return WXBI.getTitle();
}

@Override
protected By getLoggedUserElementLocator() {
	return USERNAME_ELEMENT_LOCATOR;
}

@Override
public boolean isInApplicationContext() {
	return waitForElement(NAVIGATION_TITLE_LINK_LOCATOR, timeout(), false /*fail*/) != null;
}

@Override
protected boolean matchDisplayedUser(final User user, final BrowserElement loggedUserElement) {
	final String loggedUserName = loggedUserElement.getText();
	return (loggedUserName != null) && loggedUserName.equalsIgnoreCase(user.getName());
}

/**
 * Open the 'Home' page by clicking on the IBM Data Portal link.
 *
 * @return The opened 'Home' page as a {@link WxbiHomePage}
 */
public WxbiHomePage openHomePage() {
	if (DEBUG) debugPrintln("		+ Goto Overview page using the IBM Data Portal link");
	return openHomePage(WxbiHomePage.class);
}

/**
 * Open the Home page by clicking on the navigation title link.
 *
 * @return The opened Home Page as a {@link IbmPage}
 */
protected <P extends IbmPage> P openHomePage(final Class<P> openedPageClass) {
	return openPageUsingLink(NAVIGATION_TITLE_LINK_LOCATOR, openedPageClass);
}

/**
 * Open the Profile menu.
 *
 * @return The opened Profile menu as {@link WxbiProfileMenuElement}.
 */
private WxbiProfileMenuElement openProfileMenu() {
	// Dismiss any existing alerts since they can overlap with the profile menu.
	dismissAlerts(false /*fail*/);

	WxbiProfileMenuElement profileMenuElement = new WxbiProfileMenuElement(this);
	profileMenuElement.expand();

	return profileMenuElement;
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

	// Check if the login screen is WXBI or CPD.
	final BrowserElement[] userNameElements =
		waitForMultipleElements(WXBI_USER_NAME_ELEMENT_LOCATOR, CPD_USER_NAME_ELEMENT_LOCATOR);

	if(userNameElements[0] != null) {
		// If reached here, it implies that the login screen is WXBI.
		// Enter the user name.
		typeText(userNameElements[0], user.getId());
		// Clear the remember me check box.
		IbmSelectionElement rememberMeElement = new IbmSelectionElement(this, By.xpath("//label[contains(@for,'remember')]"));
		rememberMeElement.clear();
		// Click the continue button.
		clickButton(By.xpath("//*[contains(@class,'commitButton') or (@id='continue-button')]"));

		// The log in operation must handle the following use cases:
		// 1. If the given IBM id is NOT associated with an IBM w3ID (intranet id),
		//    the user will be asked to enter the password of his/her IBM id.
		// 2. If the given IBM id is associated with an IBM w3ID (intranet id), then
		//   2.1. The user can be directed to the "Sign in with w3id" page.
		//   2.2. The user can be directed directly to the "Sign in with your w3id credentials" page if
		//        a chosen 2 factor authentication mode has been stored in the browser session.
		// 3. If an active login session exists in the browser session, the user can be directed to the WXBIA Home Page.
		BrowserElement[] ibmIdLoginRelatedElements = waitForMultipleElements(
			new By[] {By.id("password"), By.id("credentialSignin"), W3_ID_USER_NAME_ELEMENT_LOCATOR, getLoggedUserElementLocator()},
			timeout(), true /*fail*/, new boolean[] {true, true, true, false} /*displayFlags*/);
		BrowserElement loggedUserElement = ibmIdLoginRelatedElements[3];

		// If the user is directed to the WXBIA Home Page, no further actions should be taken.
		if(loggedUserElement == null) {
			// 1. If the given IBM id is NOT associated with an IBM w3ID (intranet id),
			//    the user will be asked to enter the password of his/her IBM id.
			// Therefore, if the password element is presented, enter the password and click the login button.
			final BrowserElement ibmIdPasswordElement = ibmIdLoginRelatedElements[0];
			if(ibmIdPasswordElement != null) {
				// Enter the password.
				typeText(ibmIdPasswordElement, user.getPassword());
				// Click the login button.
				click(By.id("signinbutton"));
			}
			// 2. If the given IBM id is associated with an IBM w3ID (intranet id), then perform the following actions.
			else {
				// 2.1. If the user is directed to the "Sign in with w3id" page,
				//      click the w3id credentials option.
				final BrowserElement w3idCredentialsElement = ibmIdLoginRelatedElements[1];
				BrowserElement w3idUserNameElement = ibmIdLoginRelatedElements[2];

				if (w3idCredentialsElement != null) {
					w3idCredentialsElement.click();
					// The user should be directed to the "Sign in with your w3id credentials" page.
					w3idUserNameElement = waitForElement(W3_ID_USER_NAME_ELEMENT_LOCATOR);
				}

				// 2.2. If the user is directed to the "Sign in with your w3id" page,
				//      enter the IBM w3ID (intranet id) credentials and click the sign in button.
				// Enter the email address.
				typeText(w3idUserNameElement, user.getEmail());
				// Clear the remember my email address check box.
				IbmSelectionElement rememberMyEmailAddressElement = new IbmSelectionElement(this, "chkbox_w3rememberme");
				rememberMyEmailAddressElement.clear();
				// Enter the password.
				typeText(By.id("password-input"), user.getPassword());
				// Click the sign in button.
				click(By.id("login-button"));
			}
		}
	}
	else {
		// If reached here, it implies that the login screen is CPD.
		// Enter the user name.
		typeText(userNameElements[1], user.getId());
		// Enter the password.
		typeText(By.id("password-textinput"), user.getPassword());
		// Click the log in button.
		click(By.id("signInButton"));
	}

	// Look for login errors and take an appropriate action.
	BrowserElement[] loginErrorRelatedElements = waitForMultipleElements(
		new By[] {By.id("password-error-msg"), By.id("login-error-text"), getLoggedUserElementLocator()},
		timeout(), true /*fail*/, new boolean[] {true, true, false} /*displayFlags*/);
	BrowserElement loggedUserElement = loginErrorRelatedElements[2];
	if(loggedUserElement == null) {
		// If a login error occurs, throw an exception.
		final BrowserElement errorMessageElement = loginErrorRelatedElements[0] != null ? loginErrorRelatedElements[0] : loginErrorRelatedElements[1];
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

	final WxbiProfileMenuElement profileMenuElement = openProfileMenu();
	profileMenuElement.logout();

	// Suppress the logout dialog if presented.
	final WxbiLogOutDialog logoutDialog = new WxbiLogOutDialog(this);
	if (logoutDialog.isOpened()) {
		logoutDialog.opened();
		logoutDialog.close();
	}

	// Wait for login button to reappear.
	// Sometimes a blank or error page is loaded due to various product defects.
	if(waitForMultipleElements(timeout(), false /*fail*/, WXBI_USER_NAME_ELEMENT_LOCATOR, CPD_USER_NAME_ELEMENT_LOCATOR) == null) {
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

///**
// * Sets a given theme for the application.
// *
// * @param theme The theme to be set for application as {@link WxbiaTheme}.
// */
//public void setTheme(final WxbiaTheme theme) {
//	final IbmDropdownlistElement themeDropdownlistElement = new IbmDropdownlistElement(this, By.id("theme-dropdown"));
//	themeDropdownlistElement.select(theme.label);
//
//	// Wait for the theme to be applied to the web page.
//	final long timeoutMillis = timeout() * 60 * 1000 + System.currentTimeMillis();
//	while (getTheme() != theme) {
//		if (System.currentTimeMillis() > timeoutMillis) {
//			final String errorMessage = "Theme '" + theme.label + "' was not applied to web page before timeout " + timeout() + "seconds";
//			if (DEBUG) debugPrintln("		  -> " + errorMessage);
//			throw new WaitElementTimeoutError(errorMessage);
//		}
//	}
//}

/**
 * Select a given cloud account.
 *
 * @param account The cloud account to select as {@link String}.
 */
public void setAccount(final String account) {
	if (DEBUG) debugPrintln("		+ Set Cloud account to '"+ account + "'");
	final IbmAccountMenuElement accountMenuElement = getAccountMenuElement();
	accountMenuElement.setAccount(account);
}
}