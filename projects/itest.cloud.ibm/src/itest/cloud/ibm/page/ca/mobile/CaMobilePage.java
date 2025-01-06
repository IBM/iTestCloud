/*********************************************************************
 * Copyright (c) 2024, 2025 IBM Corporation and others.
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
package itest.cloud.ibm.page.ca.mobile;

import static itest.cloud.ibm.page.element.IbmAlertElement.ALERT_ELEMENT_LOCATOR;
import static itest.cloud.performance.PerfManager.USER_ACTION_NOT_PROVIDED;
import static itest.cloud.scenario.ScenarioUtil.*;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;

import itest.cloud.browser.MobileEmulator;
import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.config.IbmUser;
import itest.cloud.ibm.page.IbmPage;
import itest.cloud.ibm.page.element.IbmAlertElement;
import itest.cloud.ibm.page.element.IbmDropdownlistElement;
import itest.cloud.ibm.scenario.IbmScenarioLoginError;
import itest.cloud.ibm.topology.*;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.*;

/**
 * This class represents a generic web page in the Cognos Analytics Mobile application and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getBrowser()}: Return the browser associated with the current page.</li>
 * <li>{@link #getCaApplication()}: Returns the Cognos Analytics application.</li>
 * <li>{@link #getCaMobileApplication()}: Returns the Cognos Analytics Mobile application.</li>
 * <li>{@link #isInApplicationContext()}: Specifies whether the web page is in the context of the application.</li>
 * <li>{@link #openMobilePageUsingLink(BrowserElement, Class, String...)}: Click on the given link assuming that will open a new page.</li>
 * <li>{@link #openMobilePageUsingLink(By, Class, String...)}: Click on the given link assuming that will open a new page.</li>
 * <li>{@link #openMobilePageUsingLink(Class, Action, String...)}: Click on the given link assuming that will open a new page.</li>
 * <li>{@link #refresh()}: Refresh the page content using Browser.refresh() and wait for the page to be loaded.</li>
 * <li>{@link #startNewBrowserSession()}: Close the current browser session and open a new one.</li>
 * <li>{@link #switchToMobilePinboardWindow()}: Switch to the Pinboard Window in the WEBVIEW_com.ibm.ba.camobile context.</li>
 * <li>{@link #switchToNativeAppContext()}: Switches to the NATIVE_APP context.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getApplicationTitleElementLocator()}: Return the locator of the element containing the title of the application..</li>
 * <li>{@link #getEventTriggerKey()}: Return the key to press to trigger the 'keyEvent' after typing text into an input web element.</li>
 * <li>{@link #getExpectedApplicationTitle()}: Returns the expected title of the application.</li>
 * <li>{@link #getLoggedUserElementLocator()}: Return the locator of the web element displaying the logged user name.</li>
 * <li>{@link #matchDisplayedUser(User, BrowserElement)}: Return whether the displayed user matches the user name or not.</li>
 * <li>{@link #performLogin(User)}: Perform login operation on the current page to be connected to the given user.</li>
 * <li>{@link #performLogout()}: Logout the page from current user to new user.</li>
 * <li>{@link #switchToCaMobileWebViewContext()}: Switches to the WEBVIEW_com.ibm.ba.camobile context.</li>
 * <li>{@link #switchToContext(Pattern)}: Switches to a given context.</li>
 * <li>{@link #switchToWindow(Pattern)}: Switch to a window with URL matching a given pattern.</li>
 * <li>{@link #waitForUrlChange(String)}: Wait for the URL of the browser to change from a given URL.</li>
 * <li>{@link #waitInitialPageLoading()}: Wait for the page initial load.</li>
 * </ul>
 * </p>
 */
public abstract class CaMobilePage extends IbmPage {

	/**
	 * This class represents an action to be performed within the context of the Mobile Pinboard Window.
	 */
	public abstract class MobilePinboardWindowAction implements Action {
		public MobilePinboardWindowAction() {
			super();
		}

		@Override
		public void perform() {
			try {
				// Switch to the Mobile Pinboard Window.
				switchToMobilePinboardWindow();
				// Perform the action within the Mobile Pinboard Window.
				performActionInMobilePinboardWindow();
			}
			catch(StaleElementReferenceException e) {
				// A StaleElementReferenceException can occur when clicking the back button, but the operation gets carried out properly.
				// Therefore, do nothing and absorb the exception.
			}
			finally {
				// Switch back to the native application context.
				switchToNativeAppContext();
			}
		}

		/**
		 * Perform the desired action within the context of the Mobile Pinboard Window.
		 *
		 * @param actionData The data to be used when performing the action as {@link Object}[].
		 */
		public abstract void performActionInMobilePinboardWindow(Object... actionData);
	}

	private static final int USER_NAME_TEXT_FIELD_INDEX = 0;
	private static final int NAMESPACE_TEXT_FIELD_INDEX = 1;

	private static final By USER_NAME_TEXT_FIELD_LOCATOR = By.id("CAMUsername");
	private static final int MANUALLY_ENTER_SERVER_URL_BUTTON_INDEX = 1;

	private static final int SKIP_INTRO_LINK_INDEX = 2;
	private static final By SKIP_INTRO_LINK_LOCATOR = By.xpath("//*[@*='ca-text-button-login']/*");
	private static final By MANUALLY_ENTER_SERVER_URL_BUTTON_LOCATOR = By.xpath("//*[@*='ca-text-button-no-qrcode']");
	private static final By SERVER_URL_TEXT_FIELD_LOCATOR = By.xpath("//*[@*='ca-textinput-server-url']");
	protected static final String BOTTOM_TAB_LIST_OPEN_LINK_LOCATOR_STRING = "//*[starts-with(@content-desc, 'bottom-tab-')]";

	protected static final By BOTTOM_TAB_LIST_LOCATOR = By.xpath(BOTTOM_TAB_LIST_OPEN_LINK_LOCATOR_STRING + "/..");

public CaMobilePage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

public CaMobilePage(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

@Override
protected By getApplicationTitleElementLocator() {
	return null;
}

@Override
public MobileEmulator getBrowser() {
	return (MobileEmulator) super.getBrowser();
}

/**
 * Returns the Cognos Analytics application.
 *
 * @return The Cognos Analytics application as {@link CaApplication}.
 *
 * @see IbmTopology#getCaApplication()
 */
public CaApplication getCaApplication() {
	return getTopology().getCaApplication();
}

/**
 * Returns the Cognos Analytics Mobile application.
 *
 * @return The Cognos Analytics Mobile application as {@link CaMobileApplication}.
 *
 * @see IbmTopology#getCaMobileApplication()
 */
public CaMobileApplication getCaMobileApplication() {
	return getTopology().getCaMobileApplication();
}

@Override
protected Keys getEventTriggerKey() {
	// A Tab key is typically entered to trigger the 'KeyEvent' after typing text into an input web element.
	// This tab is recognized as a corrupted character by Appium for some reason.
	// Therefore, no key is to be entered to trigger the 'KeyEvent' in this case.
	return null;
}

@Override
protected Pattern getExpectedApplicationTitle() {
	// The title for the application is unavailable in the pages.
	return null;
}

@Override
protected By getLoggedUserElementLocator() {
	// User information is unavailable in the pages of this application.
	return null;
}

@Override
public boolean isInApplicationContext() {
//	return waitForElement(NAVIGATION_TITLE_LINK_LOCATOR, timeout(), false /*fail*/) != null;
	return true;
}

@Override
protected boolean matchDisplayedUser(final User user, final BrowserElement loggedUserElement) {
	// User information is not available in the pages of this application.
	// Therefore, no user validation is performed in this application.
	return true;
}

/**
 * Click on the given link assuming that will open a new page.
 *
 * @param linkElement The link element on which to click as {@link BrowserElement}.
 * @param openedPageClass The class associated with the opened page as a {@link CaMobilePage}.
 * @param pageData Additional information to store in the page when opening it as an array of {@link String}s.
 *
 * @return The web page as a {@link CaMobilePage} which is opened after having clicked on the link.
 */
public <P extends CaMobilePage> P openMobilePageUsingLink(final BrowserElement linkElement, final Class<P> openedPageClass, final String... pageData) {
	return openMobilePageUsingLink(openedPageClass, new Action() {
		@Override
		public void perform() {
			// Click on the link element. At times, the link element may be obscured by another element and therefore, not be clickable.
			// As a result, a WebDriverException can occur.
			try {
				linkElement.click();
			}
			catch (WebDriverException e) {
				// If the linkElement.click() method causes a WebDriverException, use JavaScript to perform the
				// click on the link element in this case.
				debugPrintln("Clicking on link element (WebBrowserElement.click()) caused following error. Therefore, try JavaScript (WebBrowserElement.clickViaJavaScript()) to perform click as a workaround.");
				debugPrintln(e.toString());
				debugPrintStackTrace(e.getStackTrace(), 1 /*tabs*/);
				linkElement.clickViaJavaScript();
			}
		}
	}, pageData);
}

/**
 * Click on the given link assuming that will open a new page.
 *
 * @param linkBy The locator of the link element on which to click as {@link By}.
 * @param openedPageClass The class associated with the opened page as a {@link CaMobilePage}.
 * @param pageData Additional information to store in the page when opening it as an array of {@link String}s.
 *
 * @return The web page as a {@link CaMobilePage} which is opened after having clicked on the link.
 */
public <P extends CaMobilePage> P openMobilePageUsingLink(final By linkBy, final Class<P> openedPageClass, final String... pageData) {
	return openMobilePageUsingLink(waitForElement(linkBy), openedPageClass, pageData);
}

/**
 * Click on the given link assuming that will open a new page.
 *
 * @param openedPageClass The class associated with the opened page as a {@link CaMobilePage}.
 * @param clickAction The action that performs the clicking of the link as a {@link Action}.
 * @param pageData Additional information to store in the page when opening it as an array of {@link String}s.
 *
 * @return The web page as a {@link CaMobilePage} which is opened after having clicked on the link.
 */
public <P extends CaMobilePage> P openMobilePageUsingLink(final Class<P> openedPageClass, final Action clickAction, final String... pageData) {
	// Perform the click action.
	clickAction.perform();

	// Accept any alert if one is present.
	acceptAlert();

	// Open the page and wait for it being loaded.
	final String pageUrl = MOBILE_APPLICATION_URL + "/" + openedPageClass.getSimpleName();
	final P page = openPage(pageUrl, USER_ACTION_NOT_PROVIDED, this.config, getUser(), openedPageClass, pageData);
	page.waitForLoadingPageEnd();

	// Return the opened page.
	return page;
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

	BrowserElement[] loginFormRelatedElements = waitForMultipleElements(
		BOTTOM_TAB_LIST_LOCATOR, MANUALLY_ENTER_SERVER_URL_BUTTON_LOCATOR, SKIP_INTRO_LINK_LOCATOR);

	// Check if the user is prompted to skip the introduction.
	if(loginFormRelatedElements[SKIP_INTRO_LINK_INDEX] != null) {
		// If reached here, it implies that the user is prompted to skip the introduction.
		// Choose to skip the introduction.
		loginFormRelatedElements[SKIP_INTRO_LINK_INDEX].click();

		loginFormRelatedElements = waitForMultipleElements(BOTTOM_TAB_LIST_LOCATOR, MANUALLY_ENTER_SERVER_URL_BUTTON_LOCATOR);
	}

	// Check if the user is prompted to choose whether to manually enter the server URL.
	if(loginFormRelatedElements[MANUALLY_ENTER_SERVER_URL_BUTTON_INDEX] != null) {
		// If reached here, it implies that the user is prompted to choose whether to manually enter the server URL.
		// Choose to manually enter the server URL.
		loginFormRelatedElements[MANUALLY_ENTER_SERVER_URL_BUTTON_INDEX].click();

		// Manually enter the server URL.
		typeText(SERVER_URL_TEXT_FIELD_LOCATOR, getCaApplication().getBaseUrl());
		// Click next button.
		clickButton(By.xpath("//*[@*='ca-text-button-next']/*"));
		// Wait for the Login Form to load.
		waitForElement(By.xpath("//*[@*='login-webview-container']/*[1]"));

		// Enter the credentials.
		try {
			// The Login Form is presented in a WebView. Therefore, switch to the appropriate window.
			switchToLoginWindow();
			loginFormRelatedElements = waitForMultipleElements(USER_NAME_TEXT_FIELD_LOCATOR, By.id("CAMNamespace"));

			// Check if the user is prompted to select a namespace.
			if(loginFormRelatedElements[NAMESPACE_TEXT_FIELD_INDEX] != null) {
				// If reached here, it implies that the user is prompted to select a namespace.
				// Select the namespace.
				IbmDropdownlistElement namespaceDropdownlistElement = new IbmDropdownlistElement(this, loginFormRelatedElements[NAMESPACE_TEXT_FIELD_INDEX]);
				namespaceDropdownlistElement.select(((IbmUser)user).getNamespace());

				loginFormRelatedElements[USER_NAME_TEXT_FIELD_INDEX] = waitForElement(USER_NAME_TEXT_FIELD_LOCATOR);
			}

			// If reached here, it implies that the user is prompted to enter a user name and a password.
			// Enter the user name.
			typeText(loginFormRelatedElements[USER_NAME_TEXT_FIELD_INDEX], user.getId());
			// Enter the password.
			typeText(By.id("CAMPassword"), user.getPassword());
			// Click the Log in button.
			clickButton(By.id("signInBtn"));

			// Look for login errors and take an appropriate action.
			final long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
			while (loginFormRelatedElements[USER_NAME_TEXT_FIELD_INDEX].isDisplayed()) {
				final BrowserElement alertWebElement = waitForElement(ALERT_ELEMENT_LOCATOR, tinyTimeout(), false /*fail*/);

				if(alertWebElement != null) {
					// If a login error occurs, throw an exception.
					throw new IbmScenarioLoginError("The following error occurred during log in operation: " + new IbmAlertElement(this, alertWebElement).getAlert());
				}

				if (System.currentTimeMillis() > timeoutMillis) {
					throw new WaitElementTimeoutError("The logging operation did not finish before the timeout '" + timeout() + "'s had reached.");
				}
			}
		}
		finally {
			// Switch back to the native application context.
			switchToNativeAppContext();
		}

		// Check if the bottom tab list have loaded to conclude the loading of the application.
		// Sometimes blank or error pages are loaded due to various product defects.
		if(waitForElement(BOTTOM_TAB_LIST_LOCATOR, timeout(), false /*fail*/) == null) {
			// A BrowserError must be raised in such a situation.
			throw new BrowserError("The application is out of scope because the bottom tab list failed to load");
		}
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

//	final CaProfileMenuElement profileMenuElement = openProfileMenu();
//	profileMenuElement.logout();
//
//	// Wait for login button to reappear.
//	// Sometimes a blank or error page is loaded due to various product defects.
//	if(waitForMultipleElements(timeout(), false /*fail*/, getLoggedUserElementLocator(), USER_NAME_TEXT_FIELD_LOCATOR) == null) {
//		// A BrowserError must be raised in such a situation.
//		throw new BrowserError("Web page '" + getUrl() + "' does not contain sign-in elements");
//	}

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

@Override
public void refresh() {
	// Refreshing is unsupported on a mobile emulator.
}

@Override
public void startNewBrowserSession() {
	// Starting a new browser session is unsupported on a mobile emulator.
}

/**
 * Switches to the WEBVIEW_com.ibm.ba.camobile context.
 */
protected void switchToCaMobileWebViewContext() {
	switchToContext(compile("WEBVIEW.+") /*urlPattern*/);
}

/**
 * Switches to a given context.
 *
 * @param urlPattern The pattern of the name of the context as {@link String}.
 *
 * @see MobileEmulator#switchToContext(Pattern, int)
 */
protected void switchToContext(final Pattern urlPattern) {
	getBrowser().switchToContext(urlPattern, timeout());
}

private void switchToLoginWindow() {
	switchToCaMobileWebViewContext();
	switchToWindow(compile(quote(getCaApplication().getBiSubdirectoryUrl())));
}

/**
 * Switch to the Pinboard Window in the WEBVIEW_com.ibm.ba.camobile context.
 */
public void switchToMobilePinboardWindow() {
	switchToCaMobileWebViewContext();
	switchToWindow(compile(quote(getCaApplication().getPinboardViewUrl())));
}

/**
 * Switches to the NATIVE_APP context.
 */
public void switchToNativeAppContext() {
	switchToContext(compile(quote("NATIVE_APP")) /*urlPattern*/);
}

/**
 * Switch to a window with URL matching a given pattern.
 *
 * @param urlPattern The pattern of the URL of the window as {@link String}.
 *
 * @throws NoSuchWindowException if a window with URL matching the given pattern was unavailable.
 *
 * @see MobileEmulator#switchToWindow(Pattern, int)
 */
protected void switchToWindow(final Pattern urlPattern) {
	getBrowser().switchToWindow(urlPattern, timeout());
}

@Override
protected String waitForUrlChange(final String originalUrl) {
	// Page URLs are unsupported on a Mobile Emulator. // Therefore, change of URL is not considered and
	// the predefined URL {@link ScenarioUtil#MOBILE_APPLICATION_URL} is returned by this method as the current page URL at all time.
	return getUrl();
}


@Override
protected void waitInitialPageLoading() throws ServerMessageError {
	// Pages are not literally available in a mobile application when dealing with native elements.
	// Therefore, this operation is inapplicable for such an application.
}
}