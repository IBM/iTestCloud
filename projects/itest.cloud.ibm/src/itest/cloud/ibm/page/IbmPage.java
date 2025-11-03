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
package itest.cloud.ibm.page;

import static itest.cloud.ibm.page.element.IbmAlertElement.WEB_ALERT_ELEMENT_LOCATOR;

import java.net.*;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.config.IbmUser;
import itest.cloud.ibm.page.element.IbmAlertElement;
import itest.cloud.ibm.scenario.IbmScenarioUtil;
import itest.cloud.ibm.topology.IbmTopology;
import itest.cloud.page.Page;
import itest.cloud.page.element.AlertElement;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.ScenarioFailedError;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * This class represents a generic web page and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getConfig()}: Return the configuration associated with the current page.</li>
 * <li>{@link #getUser()}: Return the user used when the page was loaded.</li>
 * <li>{@link #isInApplicationContext()}: Specifies whether the web page is in the context of the application.</li>
 * <li>{@link #matchPage()}: Return whether the current page matches this page.</li>
 * <li>{@link #prepare()}: Prepare the page for test execution by performing various operations.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getAlertElement(BrowserElement)}: Return an alert element for a given web element.</li>
 * <li>{@link #getAlertWebElements(Pattern, boolean)}: Return the alert web elements matching a given pattern.</li>
 * <li>{@link #getApplicationTitleElementLocator()}: Return the locator of the element containing the title of the application..</li>
 * <li>{@link #getBusyIndicatorElement()}: Return the element indicating that the page is undergoing an operation (busy).</li>
 * <li>{@link #getExpectedApplicationTitle()}: .</li>
 * <li>{@link #isLoaded()}: Return whether the page is currently loaded or not.</li>
 * <li>{@link #load()}: Load the current page into the browser.</li>
 * <li>{@link #matchBrowserUrl()}: Return whether the current page location matches the browser URL or not.</li>
 * <li>{@link #matchBrowserUrlPath(String, String)}: Compare the path section of the URL expected for the web page against the URL appear in the browser.</li>
 * </ul>
 * </p>
 */
public abstract class IbmPage extends Page {

public IbmPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

public IbmPage(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

/**
 * Return an alert element for a given web element.
 *
 * @param alertWebElement The alert web element as {@link BrowserElement}.
 *
 * @return The alert element as {@link AlertElement}.
 */
@Override
protected AlertElement getAlertElement(final BrowserElement alertWebElement) {
	return new IbmAlertElement(this, alertWebElement);
}

/**
 * Return the alert web elements matching a given pattern.
 *
 * @param pattern A pattern matching the alert message as {@link Pattern}.
 * @param fail Specify whether to fail if a matching alert could not be found.
 *
 * @return The alert web elements matching the given pattern as a {@link List} of {@link BrowserElement}.
 */
@Override
protected List<BrowserElement> getAlertWebElements(final Pattern pattern, final boolean fail) {
	return waitForElements(WEB_ALERT_ELEMENT_LOCATOR, (fail ? timeout() : tinyTimeout()), fail, true /*displayed*/);
}

/**
 * Return the title of the application.
 *
 * @return The title as {@link String}.
 */
private String getApplicationTitle() {
	return waitForElement(getApplicationTitleElementLocator()).getText();
}

/**
 * Return the locator of the element containing the title of the application.
 *
 * @return The locator as {@link By}.
 */
protected abstract By getApplicationTitleElementLocator();

@Override
protected By getBusyIndicatorElementLocator() {
	return IbmScenarioUtil.getBusyIndicatorElementLocator(false /*relative*/);
}

/**
 * {@inheritDoc}
 * @return The configuration as a {@link IbmConfig}.
 */
@Override
public IbmConfig getConfig() {
	return (IbmConfig) this.config;
}

/**
 * Returns the expected title of the application.
 *
 * @return The expected title of the application as {@link String}.
 */
protected abstract Pattern getExpectedApplicationTitle();

private String getNormalizedUrlPath(final URL url) {
	return url.getPath().endsWith("/") ? url.getPath().substring(0 /*beginIndex*/, url.getPath().length()-1) : url.getPath();
}

@Override
public IbmTopology getTopology() {
	return (IbmTopology) super.getTopology();
}

/**
 * {@inheritDoc}
 *
 * @return The user of the web page as a {@link IbmUser}
 */
@Override
public IbmUser getUser() {
    return (IbmUser) super.getUser();
}

/**
 * Specifies if an application title is expected for the page.
 *
 * @return <code>true</code> if an application title is expected or <code>false</code> otherwise.
 */
private boolean isApplicationTitleExpected() {
	return (getExpectedApplicationTitle() != null) && (getApplicationTitleElementLocator() != null);
}

///**
// * Specifies whether it is a web page representing a server error.
// *
// * @return <code>true</code> if it is a web page representing a server error or <code>false</code> otherwise.
// */
//private boolean isErrorPage() {
//	return waitForElement(By.xpath("//*[@class='errorDetailsContainer']"), tinyTimeout(), false /*fail*/) != null;
//}

@Override
protected boolean isLoaded() {
	return super.isLoaded() && (!isApplicationTitleExpected() || matchApplicationTitle());
}

@Override
protected void load() {
	super.load();

	// Wait for the expected title
	waitForExpectedApplicationTitle();
}

/**
 * Specify whether the application title matches the expected one.
 *
 * @return <code>true</code> if the application title matches or <code>false</code> otherwise.
 */
private boolean matchApplicationTitle() {
	final String applicationTitle = getApplicationTitle();
	final Pattern expectedApplicationTitle = getExpectedApplicationTitle();

	return expectedApplicationTitle.matcher(applicationTitle).matches();
}

/**
 * Return whether the current page location matches the browser URL or not.
 *
 * @return <code>true</code> if the location and the url match, <code>false</code>
 * otherwise.
 */
@Override
protected boolean matchBrowserUrl() {
	String url = getUrl();
	// Special case when restarting FireFox after it has died or
	// stating Chrome or Safari respectively.
	if (url.equals("about:blank") || url.startsWith("data:") || url.startsWith("chrome:") || url.isEmpty()) {
		return false;
	}

	// Compare URL starts
	try {
		URL browserURL = URI.create(url).toURL();
		URL pageURL = URI.create(this.location).toURL();
		return browserURL.getProtocol().equals(pageURL.getProtocol()) &&
		       browserURL.getHost().equals(pageURL.getHost()) &&
		       browserURL.getPort() == pageURL.getPort() &&
		       matchBrowserUrlPath(getNormalizedUrlPath(pageURL), getNormalizedUrlPath(browserURL));
	}
	catch (MalformedURLException e) {
		throw new ScenarioFailedError(e);
	}
}

/**
 * Compare the path section of the URL expected for the web page against the URL appear in the browser.
 *
 * @param pageURL The path section of the URL expected for the web page as {@link String}.
 * @param browserURL The path section of the URL appear in the Browser for the web page {@link String}.
 *
 * @return <code>true</code> if the path section of the expected URL matches the browser URL or
 * <code>false</code> otherwise.
 */
@Override
protected boolean matchBrowserUrlPath(final String pageURL, final String browserURL) {
	return pageURL.startsWith(browserURL) || browserURL.startsWith(pageURL);
}

@Override
public boolean matchPage() {
	return super.matchPage() && (!isApplicationTitleExpected() || matchApplicationTitle());
}

@Override
public void prepare() {
	//Dismiss alerts if exist.
	dismissAlerts(false /*fail*/, true /*verbose*/);
}

/**
 * Wait for the expected application title to appear.
 *
 * @throws ScenarioFailedError if the current application title does not match the expected one.
 */
private void waitForExpectedApplicationTitle() {
	// Check if a title is expected for the application.
	if(isApplicationTitleExpected()) {
		long timeoutMillis = openTimeout() * 1000 + System.currentTimeMillis();
		while (!matchApplicationTitle()) {
			if (System.currentTimeMillis() > timeoutMillis) {
				throw new WaitElementTimeoutError("Current application title '"+ getApplicationTitle() +"' does not match the expected one: '"+ getExpectedApplicationTitle() +"'");
			}
		}
	}
}

//******** Remove the following methods if not needed.
///**
//* Specifies whether a breadcrumb is available in the page.
//*
//* @return <code>true</code> if a breadcrumb is available or <code>false</code> otherwise.
//*/
//public boolean isBreadcrumbAvailable() {
//	return getBreadcrumbElement(0 /*index*/, false /*fail*/) != null;
//}
//
//private IbmSelectionElement getRememberCheckboxElement() {
//	return new IbmSelectionElement(this, waitForElement(By.xpath("//*[@for='remember']")));
//}
//
///**
//* Return the locator for the root web element of the current web page.
//*
//* @return The locator as a {@link By}.
//*/
//@Override
//protected By getRootElementLocator() {
//return By.xpath("//body[@role='main']");
//}
//
///**
//* Return the title element locator.
//*
//* @return The title element locator as a {@link By}.
//*/
//@Override
//protected By getTitleElementLocator() {
//return By.xpath("//*[@id='dap-title-text'] | " + LAST_BREADCRUMB_ELEMENT_XPATH);
//}
//
///**
//* Return the main menu element.
//*
//* @return The main menu element as a {@link AcmeMainMenuElement}.
//*/
//public AcmeMainMenuElement getMainMenuElement() {
//	return new AcmeMainMenuElement(this);
//}
///**
//* Return the breadcrumb element at a given index.
//*
//* @param index The zero-based index of the breadcrumb element.
//* If a negative value is provided for this parameter, the last breadcrumb element
//* will be returned.
//* @param fail Specifies whether to fail if a breadcrumb element is unavailable
//* at the given index.
//*
//* @return The breadcrumb element at a given index as {@link BrowserElement} or
//* <code>null</code> if a breadcrumb element is unavailable at the given index and
//* asked not to fail.
//*/
//protected BrowserElement getBreadcrumbElement(final int index, final boolean fail) {
//	String xpath = index < 0 ? LAST_BREADCRUMB_ELEMENT_XPATH : (BREADCRUMB_ELEMENTS_XPATH_PREFIX + "[" + (index+1) + "]/*");
//	return waitForElement(By.xpath(xpath), (fail ? timeout() : tinyTimeout()), fail);
//}
//
///**
//* Return the last breadcrumb text.
//*
//* @return The last breadcrumb text as {@link String}.
//*/
//public String getBreadcrumbText() {
//	return getBreadcrumbText(LAST_INDEX);
//}
//
///**
//* Return the breadcrumb text at a given index.
//*
//* @param index The zero-based index of the breadcrumb element.
//* If a negative value is provided for this parameter, the text of the
//* last breadcrumb element will be returned.
//*
//* @return The breadcrumb text at the given index as {@link String}.
//*/
//public String getBreadcrumbText(final int index) {
//	return getBreadcrumbText(getBreadcrumbElement(index, true /*fail*/));
//}
//
//private String getBreadcrumbText(final BrowserElement breadcrumElement) {
//	String titleValue = breadcrumElement.getAttribute("title");
//	return (titleValue != null) ? titleValue : breadcrumElement.getText();
//}
//
///**
//* Return all breadcrumb texts.
//*
//* @return All breadcrumb texts of the page as {@link List}.
//*/
//public List<String> getBreadcrumbTexts() {
//	List<BrowserElement> breadcrumElements = waitForElements(By.xpath(BREADCRUMB_ELEMENTS_XPATH_PREFIX));
//	List<String> breadcrumbTexts = new ArrayList<String>(breadcrumElements.size());
//
//	for (BrowserElement breadcrumElement : breadcrumElements) {
//		breadcrumbTexts.add(getBreadcrumbText(breadcrumElement));
//	}
//
//	return breadcrumbTexts;
//}
//
///**
//* Return the xpaths of elements indicating that the page is undergoing an operation (busy).
//* <p>
//* The availability of such an element implies that at least a part of the
//* web page is still loading.
//* </p>
//*
//* @return Return the xpaths of elements indicating that the page is undergoing an operation.
//*
//*/
//@Override
//protected By[] getBusyIndicatorElementLocators() {
//	return BUSY_INDICATOR_ELEMENT_LOCATORS;
//}
//
//
///**
// * Open a page by clicking on the Breadcrumb link at a given index.
// *
// * @param index The zero-based index of the Breadcrumb link.
// * @param openedPageClass The class associated with the opened page.
// * @param pageData Additional information to store in the page when opening it.
// *
// * @return The opened page as a {@link IbmPage}.
// */
//public <P extends IbmPage> P openPageViaBreadcrumb(final int index, final Class<P> openedPageClass, final String... pageData) {
//	return openPageUsingLink(getBreadcrumbElement(index, true /*fail*/), openedPageClass, pageData);
//}
//
///**
// * {@inheritDoc}
// * <p>
// * First check whether there's a not authorized or CLM server error message.
// * In that case, the login has to be done by clicking on the provided link.
// * </p>
// */
//@Override
//protected void performLogin(final User user) {
//	if (DEBUG) debugPrintln("		+ Perform login of user "+user);
//
//	// Test if login is necessary
//	if (user == null) {
//		debugPrintln("		  -> Do nothing as there's no login");
//		return;
//	}
//
//	if (user.equals(getApplication().getUser())) {
//		debugPrintln("		  -> Do nothing as user is already logged in");
//		return;
//	}
//
//	if(getLoggedUserElement(false /*fail*/, 1 /*waitTimeout*/) != null) {
//		debugPrintln("		  -> Do nothing as user session has been saved in browser");
//		// Store user in application
//		this.topology.login(this.browser.getCurrentUrl(), user);
//		return;
//	}
//
//	// The log in operation must handle the following use case:
//	// 1. The Sign In button can be provided to open the log in page. Once the log in
//	// page has been opened, the user name and password of an IBM id must be entered.
//	// 2. The log-in page can directly be opened. The the user name and password of
//	// an IBM id must be entered in this page.
//	// 3. The given IBM id can be associated with an IBM w3ID (intranet id). The password
//	// field will be hidden and a continue button will be provided after entering such an IBM id.
//	// Clicking this button will open the "Sign in with your w3id" page. The user name and password
//	// of the IBM w3ID (intranet id) must be entered in this page.
//	BrowserElement[] ibmIdLoginRelatedElements =
//		waitForMultipleElements(IBM_ID_LOG_IN_ELEMENT_LOCATOR, IBM_ID_USER_NAME_ELEMENT_LOCATOR);
//
//	// If the Log In button is present, click it to open the login page.
//	if(ibmIdLoginRelatedElements[0] != null){
//		ibmIdLoginRelatedElements[0].click();
//		sleep(1 /*seconds*/);
//	}
//
//	// Check if the user account is associated with an IBM w3ID (federated).
//	if(((IbmUser)user).isFederated()) {
//		// If the user account is federated, click the appropriate link to bring up the federated login form.
//		waitForElement(By.xpath("//a[contains(@class,'sso-login-link')]")).click();
//
//		// Enter the user name.
//		typeText(waitForElement(IBM_ID_USER_NAME_ELEMENT_LOCATOR), user.getId());
//		// Uncheck the "Remember me" checkbox if has been selected by default.
//		getRememberCheckboxElement().alter(false /*select*/);
//		// Click the continue button.
//		clickButton(IBM_ID_CONTINUE_BUTTON_LOCATOR);
//		// Wait for the following message to be displayed: "Sign in with your w3id".
//		// This will ensure that the IBM w3ID login page has loaded.
//		waitForElement(By.id("w3idheader"));
//
//		BrowserElement[] w3idLoginRelatedElements =
//			this.browser.waitForMultipleElements(
//				null /*parentElement*/, new By[]{By.name("username"), getLoggedUserElementLocator()}, timeout(), true /*fail*/, new boolean[]{true,false} /*displayFlags*/);
//
//			// If w3id login page is displayed (it will not be displayed if the user already logged on to W3 in the same browser session).
//		if(w3idLoginRelatedElements[0] != null) {
//			// Enter the username (it seems the name is copied from the first login page in Firefox, but not in Chrome and IE).
//			typeText(w3idLoginRelatedElements[0], user.getId());
//
//			// Enter the password of the IBM w3ID (intranet id).
//			typePassword(waitForElement(By.name("password")), user);
//
//			IbmSelectionElement rememberEmailCheckbox =
//				new IbmSelectionElement(this, waitForElement(By.id("chkbox_w3rememberme")), waitForElement(By.xpath("//*[@for='chkbox_w3rememberme']")));
//			// Uncheck "Remember my email address" if has been checked by default.
//			rememberEmailCheckbox.alter(false /*select*/);
//
//			// Click the "Sign in" button.
//			clickButton(By.id("btn_signin"), true /*check*/);
//
//			// If an error message (such as "Your w3id or password was entered incorrectly") is displayed, throw an exception.
//			BrowserElement w3idLoginErrorMessage = waitForElement(By.className("errorMessage"), tinyTimeout(), false /*fail*/, false /*displayed*/);
//			if(w3idLoginErrorMessage != null) {
//				throw new IbmScenarioLoginError("The following error occurred during log in operation: " + w3idLoginErrorMessage.getText());
//			}
//		}
//	}
//	else {
//		// Enter the user name.
//		typeText(waitForElement(IBM_ID_USER_NAME_ELEMENT_LOCATOR), user.getId());
//		// Enter the password.
//		typePassword(waitForElement(IBM_ID_PASSWORD_ELEMENT_LOCATOR), user);
//		// Uncheck the "Remember me" checkbox if has been selected by default.
//		getRememberCheckboxElement().alter(false /*select*/);
//		// Click Log in button.
//		clickButton(By.id("signinbutton"));
//
//		// Check whether there's a login issue in the login page.
//		BrowserElement ibmAlertElement =
//			waitForElement(By.xpath("//*[contains(@class,'ibm-alert')]/*"), 2 /*time_out*/, false /*fail*/, false /*displayed*/);
//		if (ibmAlertElement != null) {
//			// Throw an appropriate error if there is a login issue.
//			throw new IbmScenarioLoginError("Cannot login on page '" + this.location + "' with user " + user + ".\nError message is: " + ibmAlertElement.getText());
//		}
//
//		// Check if an APS Portal account does not exist for the given IBM ID.
//		BrowserElement alertErrorElement =
//			waitForElement(By.xpath("//*[contains(@class,'alert--error')]"), 1 /*time_out*/, false /*fail*/, false /*displayed*/);
//		if(alertErrorElement != null) {
//			String alertError = alertErrorElement.getText();
//
//			if(alertError.contains("There is no Data Science Experience account under your current IBM ID")) {
//				// Create an APS Portal account for the given IBM ID.
//				click(By.xpath("//a[text()='Continue with your Bluemix Credentials']"));
//				waitWhileBusy(5 * 60 /*timeout*/);
//				// Verify if the account creation has completed successfully.
//				waitForElement(By.xpath("//div[text()='Ready!']"));
//				click(By.xpath("//a[text()='Get Started']"));
//			}
//			else {
//				throw new ScenarioFailedError("The following error occurred during log in operation: " + alertError);
//			}
//		}
//
//		// Check whether there's a login issue in Watson Studio.
//		BrowserElement errorMsgElement = waitForElement(By.id("errormsg"), 1 /*time_out*/, /*fail:*/false, /*displayed:*/false);
//		if (errorMsgElement != null && errorMsgElement.isDisplayed()) {
//			BrowserElement textElement = errorMsgElement.waitForElement(By.xpath(".//p[@id='errtxt']"));
//			throw new IbmScenarioLoginError("Cannot login on page '"+this.location+"' with user "+user+".\nError message is: "+textElement.getText());
//		}
//	}
//
//	// Check if the page is in application context.
//	// Sometimes blank or error pages are loaded due to various product defects.
//	if(!isInApplicationContext()) {
//		// A BrowserError must be raised in such a situation.
//		throw new BrowserError("Web page '" + getUrl() + "' is out of scope/context of application '" + getApplication().getName() + "'");
//	}
//
//	// Wait until the login operation to be completed and the logged user information
//	// to appear as it may take some time in some geographies.
//	waitForElement(getLoggedUserElementLocator(), timeout(), true /*fail*/, false /*displayed*/);
//
//	// Store user in application
//	this.topology.login(this.browser.getCurrentUrl(), user);
//}
//
}