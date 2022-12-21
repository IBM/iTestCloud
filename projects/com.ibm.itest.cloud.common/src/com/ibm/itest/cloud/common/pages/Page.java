/*********************************************************************
 * Copyright (c) 2012, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.common.pages;

import static com.ibm.itest.cloud.common.config.Timeouts.DELAY_AFTER_CLICK_LINK_TIMEOUT;
import static com.ibm.itest.cloud.common.config.Timeouts.DELAY_BEFORE_CLICK_LINK_TIMEOUT;
import static com.ibm.itest.cloud.common.pages.Page.ClickType.CLICK;
import static com.ibm.itest.cloud.common.performance.PerfManager.PERFORMANCE_ENABLED;
import static com.ibm.itest.cloud.common.performance.PerfManager.USER_ACTION_NOT_PROVIDED;
import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.*;
import static java.util.regex.Pattern.DOTALL;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;

import com.ibm.itest.cloud.common.PageWorkaround;
import com.ibm.itest.cloud.common.browsers.Browser;
import com.ibm.itest.cloud.common.config.*;
import com.ibm.itest.cloud.common.nls.NlsMessages;
import com.ibm.itest.cloud.common.pages.elements.*;
import com.ibm.itest.cloud.common.performance.PerfManager;
import com.ibm.itest.cloud.common.performance.PerfManager.RegressionType;
import com.ibm.itest.cloud.common.scenario.errors.*;
import com.ibm.itest.cloud.common.topology.Application;
import com.ibm.itest.cloud.common.topology.Topology;
import com.ibm.itest.cloud.common.utils.FileUtil;
import com.ibm.itest.cloud.common.utils.ByUtils.ComparisonPattern;

/**
 * The abstract class for any web page connected to a {@link Browser}.
 * <p>
 * A web page is created using a {@link #location} which is the initial url sent to
 * the browser to load the page. It may slightly differ from the browser current
 * url after it has been loaded (see {@link #getUrl()}.
 * </p><p>
 * It also stores the configuration used while running the test in order
 * to access any necessary information from it (e.g. timeouts).
 * </p><p>
 * Finally, the web page is also associated with a {@link #getUser()} as its content
 * might slightly or completely differ depending of it. This user let also the page
 * know whether a login operation when a new one is connected to it.
 * </p><p>
 * This class provides the following basic functionalities:
 * <ul>
 * <li>{@link #get()}: get page content.</li>
 * <li>{@link #getPage(String)}: Get from the cache the page instance for the
 * given location.</li>
 * <li>{@link #startNewBrowserSession()}: Close the current browser session and
 * open a new one.</li>
 * </ul>
 * It also provides any convenient method to access basic browser functionalities
 * without having to specify all parameters, ie. by using default parameter value:
 * <ul>
 * </ul>
 * </p><p>
 * </p>
 * TODO Rename this class as <b>WebAbstractPage</p>
 */
public abstract class Page implements IPage {

	public enum ClickType {CLICK, DOUBLE_CLICK}
	// Data
	public final static String[] NO_DATA = null;

	private final static String VERIFY_PAGE_USER_ARGUMENT = "verifyPageUser";
	// Pages cache
//	private final static Map<String, WebPage> CLM_PAGES = new WeakHashMap<String, WebPage>();
	// TODO Following block should be activated when moving page creation and cache to WebBrowser
	/*
	private final static ThreadLocal<Map<String, WebPage>> CLM_PAGES = new ThreadLocal<Map<String,WebPage>>();
	static {
		CLM_PAGES.set(new WeakHashMap<String, WebPage>());
	}
	private final static ThreadLocal<WebPage> CURRENT_PAGE = new ThreadLocal<WebPage>();
	*/
	private final static ThreadLocal<List<Page>> PAGES_HISTORY = new ThreadLocal<List<Page>>();

	static {
		PAGES_HISTORY.set(new ArrayList<Page>());
	}

	/**
	 * Clear web pages history.
	 */
	public static void clearHistory() {
		getPagesHistory().clear();
	}

	// TODO Move page creation and cache to WebBrowser
	protected static <P extends Page> P createPage(final String location, final Config config, final User user, final Class<P> pageClass) {
		return createPage(location, config, user, pageClass, (String[]) null);
	}

	// TODO Move page creation and cache to WebBrowser
	@SuppressWarnings("unchecked")
	protected static <P extends Page> P createPage(final String location, final Config config, final User user, final Class<P> pageClass, final String... data) {
		if (DEBUG) {
			debugPrintln("		+ Create page "+location+ " for user "+user);
			debugPrintln("			-> location: "+location);
			debugPrintln("			-> user: "+user);
			debugPrintln("			-> class:    "+pageClass.getName());
		}

		// Get the page from cache
		String locationKey = location; // getLocationKey(location, pageClass);
		P page = (P) searchPageInHistory(locationKey);

		// If page does not exist create it
		if (page == null) {
			page = createPageInstance(location, config, user, pageClass, data);
			if (DEBUG) debugPrintln("		  -> store page at "+locationKey+": "+page.getUrl());
		} else {
			if (DEBUG) debugPrintln("		  - > found page at "+locationKey+": "+page.getUrl());
			// Update the data associated with the cached page in case the cached page is associated with
			// a different set of data.
			page.data = data;
			// Check if the cached page is associated with a different user.
			if (user != null && !user.equals(page.getUser())) {
				if (DEBUG) debugPrintln("		  - > change page user from "+page.getUser().getId()+" to "+user.getId());
				page.login(user);
			}
		}

		// Return the page
		return page;
	}
	// TODO Move page creation and cache to WebBrowser
	@SuppressWarnings("unchecked")
	protected static <P> P createPageInstance(final String location, final Config config, final User user, final Class<P> pageClass, final String... data) {
		Exception exception = null;
		Class<? extends User> userClass = user == null ? User.class : user.getClass();
		while (userClass.getSuperclass() != null) {
			Class<? extends Config> configClass = config.getClass();
			while (configClass.getSuperclass() != null) {
				try {
					if (data == null || data.length == 0) {
						Constructor<P> constructor = pageClass.getConstructor(String.class, configClass, userClass);
						return constructor.newInstance(location, config, user);
					}
					Constructor<P> constructor = pageClass.getConstructor(String.class, configClass, userClass, String[].class);
					return constructor.newInstance(location, config, user, data);
				}
				catch (NoSuchMethodException ex) {
					if (exception == null) {
						exception = ex;
					}
				}
				catch (InvocationTargetException ex) {
					// Application can crash by returning error 500.
					// This type of error is typically associated with an InvocationTargetException.
					println("InvocationTargetException cause: " + ex.getCause());
					throw new BrowserError(ex);
				}
				catch (Exception ex) {
					throw new ScenarioFailedError(ex);
				}
				configClass = (Class< ? extends Config>) configClass.getSuperclass();
			}
			userClass = (Class< ? extends User>) userClass.getSuperclass();
		}
		throw new ScenarioFailedError(exception);
	}
	/**
	 * Return the current page displayed on the browser.
	 * <p>
	 * This is the last page of the internal cache.
	 * </p>
	 * @return The current page as a {@link Page}.
	 */
	public static Page getCurrentPage() {
		final List<Page> pagesHistory = getPagesHistory();
		int size = pagesHistory.size();
		if (size == 0) return null;
		return pagesHistory.get(size-1);
	}
	/**
	 * Seek the cache to find the instance of the given page class.
	 * <p>
	 * Note that this is a very expensive method which should not be used too
	 * frequently.
	 * </p><p>
	 * TODO Replace the pages cache keys to store class instead location.
	 *
	 * @param pageClass The page class
	 * @return The instance of the class associate with the give page page class or
	 * <code>null</code> if this page hasn't been created yet.
	 */
	// TODO Move page creation and cache to WebBrowser
	@SuppressWarnings("unchecked")
	public static <P extends Page> P getPage(final Class<P> pageClass) {
		if (DEBUG) debugPrintln("		+ Get web page for "+pageClass);

		// Get the page from cache
		Iterator<Page> pageInstances = getPagesHistory().iterator();
		while (pageInstances.hasNext()) {
			Page pageInstance = pageInstances.next();
			if (pageInstance.getClass().equals(pageClass)) {
				return (P) pageInstance;
			}
		}

		// Return the found page
		return null;
	}

	/**
	 * Get from the cache the page instance for the given location.
	 *
	 * @param location The page location
	 * @return The instance of the class associate with the page or <code>null</code>
	 * if this page hasn't been created yet.
	 */
	// TODO Move page creation and cache to WebBrowser
	public static Page getPage(final String location) {
		if (DEBUG) debugPrintln("		+ get page "+location);

		// Get the page from cache
		Page page = searchPageInHistory(location);

		// The page should have been found
		if (page == null) {
			if (DEBUG) debugPrintln("		  -> not found.");
			int index = location.indexOf('&');
			if (index > 0) {
				String locationKey = location.substring(0, index);
				if (DEBUG) debugPrintln("		  -> try with key="+locationKey);
				page = searchPageInHistory(locationKey);
			}
		}

		// Return the found page
		return page;
	}

	private static List<Page> getPagesHistory() {
		return PAGES_HISTORY.get();
	}
	/**
	 * Retrieve the existing page for the browser current URL. Create it if it's the first
	 * time the page is requested.
	 *
	 * @return The instance of the class associate with the page or <code>null</code>
	 * if no page location matching the browser url is found in the cache...
	 */
	@SuppressWarnings("unchecked")
	public static <P extends Page> P getPageUsingBrowser(final Config config, final User user, final Class<P> pageClass, final String... data) {
		if (DEBUG) debugPrintln("		+ Get page using browser...");

		// Get page url from browser
		String currentUrl = config.getBrowser().getCurrentUrl();
		if (DEBUG) debugPrintln("		  -> current URL:"+currentUrl);

		// Adapt browser url to application
		Application application = config.getTopology().getApplication(currentUrl);
		String pageUrl = application == null ? currentUrl : application.getPageUrl(currentUrl);
		if (DEBUG) debugPrintln("		  -> page URL:"+pageUrl);

		// Look for a page location matching the page url in the cache
		P page = (P) searchPageInHistory(pageUrl);
		if (page == null) {
			// Open the page and for it being loaded
			page = openPage(pageUrl, USER_ACTION_NOT_PROVIDED, config, user, pageClass, data);
			page.waitForLoadingPageEnd();
		}
		else {
			debugPrintln("		  -> page "+page+" was found.");
		}
		return page;
	}
	// TODO Move page creation and cache to WebBrowser
	public static Page openPage(final String location) {

		// Get the page
		Page page = getPage(location);
		if (page == null) {
			throw new ScenarioFailedError("The page with url '"+location+"' was not already created!");
		}

		// Get the page content
		page.get();

		// Return page
		return page;
	}
	/**
	 * Retrieve the existing page for the given location. Create it if it is the first time
	 * the page is requested.
	 *
	 * @param location The url of the page
	 * @param config The config to use for the requested page
	 * @param user The user to use on the requested page
	 * @param pageClass The class associated with the page to open
	 * @param data Additional CLM information to be stored in the page
	 * @return The instance of the class associate with the page.
	 */
	// TODO Move page creation and cache to WebBrowser
	public static <P extends Page> P openPage(final String location, final Config config, final User user, final Class<P> pageClass, final String... data) {
		return openPage(location, USER_ACTION_NOT_PROVIDED, config, user, pageClass, data);
	}

	/**
	 * Retrieve the existing page for the given location. Create it if it is the first time
	 * the page is requested.
	 *
	 * @param location The url of the page
	 * @param userAction The user action executed while opening the page
	 * @param config The config to use for the requested page
	 * @param user The user to use on the requested page
	 * @param pageClass The class associated with the page to open
	 * @param data Additional CLM information to be stored in the page
	 * @return The instance of the class associate with the page.
	 */
	protected static <P extends Page> P openPage(final String location, final String userAction, final Config config, final User user, final Class<P> pageClass, final String... data) {

		// Create page
		P page = createPage(location, config, user, pageClass, data);

		// Set performance user action if any
		if (PERFORMANCE_ENABLED && !userAction.equals(USER_ACTION_NOT_PROVIDED)) {
			page.setPerfManagerUserActionName(userAction);
		}

		// Get the page content
		page.get();

		// Return page
		return page;
	}

	/**
	 * Reopen the given page.
	 * <p>
	 * This specific method is used when restarting the browser on the given page.
	 * </p>
	 * @param page The page to reopen
	 * @param user The user associated with the page. It's necessary because the
	 * login information of the provided page might have been reset prior the call...
	 * @return The instance of the class associate with the page.
	 */
	public static Page reopenPage(final Page page, final IUser user) {
		return openPage(page.location, USER_ACTION_NOT_PROVIDED, page.config, (User) user, page.getClass(), page.data);
	}

	/**
	 * Return the first page found in the history.
	 *
	 * @param location The page location
	 * @return The found page or <code>null</code> if none was found.
	 */
	public static Page searchPageInHistory(final String location) {
		final List<Page> pagesHistory = getPagesHistory();
		int size = pagesHistory.size();
		for (int i=size-1; i>=0; i--) {
			Page page = pagesHistory.get(i);
			String pageLocation = page.getLocation();
			if (pageLocation.equals(location)) {
				return page;
			}
		}
		return null;
	}

	// The browser in which the current page is displayed
	protected Browser browser;

	/**
	 *  Temporary access to WebDriver as RQM web page objects as needs it
	 */
	 protected WebDriver driver;

// Test config
protected Config config;

// Page info
protected String location;

private User loginUser;


protected Topology topology;


protected Application application;

// Info telling whether workaround has been applied
protected boolean refreshed = false;

// Timeouts
private int timeout;

protected int openTimeout;

protected int shortTimeout;

protected int tinyTimeout;

protected int delayBeforeLinkClick;

protected int delayAfterLinkClick;

// Web elements
protected BrowserElement bodyElement;

// Count how many times we've tried to verify that the displayed username is correct
private int verifyTries = 1;

// NLS messages
public final NlsMessages nlsMessages;

// Additional information
protected String[] data;

public Page(final String url, final Config config, final User user) {
	this(url, config, user, NO_DATA);
}

public Page(final String url, final Config config, final User user, final String... data) {

	// Init fields
	this.browser = config.getBrowser();
	this.driver = this.browser.getDriver();
	this.location = url;
    this.config = config;
    this.data = data;

	// Store Clm topology
	this.topology = config.getTopology();
	this.application = this.topology.getApplication(this.location);
	this.loginUser = user == null || ((!needLogin(user) && user.equals(this.application.getUser()))) ? null : user;

    // Init timeouts to make their access faster and also allow each page to change them easily
    this.timeout = config.getDefaultTimeout();
    this.openTimeout = config.getOpenPageTimeout();
    this.shortTimeout = config.getShortTimeout();
    this.tinyTimeout = config.getTinyTimeout();
    this.delayBeforeLinkClick = DELAY_BEFORE_CLICK_LINK_TIMEOUT;
    this.delayAfterLinkClick = DELAY_AFTER_CLICK_LINK_TIMEOUT;

    // Init NLS messages
    this.nlsMessages = initNlsMessages();
}

/**
 * Add a performance result if manager is activated.
 * <p>
 * Note that the result is added as a regression type to server.
 * This is a no-op if the performances are not managed during the scenario
 * execution.
 * </p>
 * @param regressionType Regression to apply
 * @param pageTitle The page title
 * @throws ScenarioFailedError If performances are <b>not enabled</b> during
 * scenario execution. Hence, callers have to check whether the performances are
 * enabled before calling this method using {@link PerfManager#PERFORMANCE_ENABLED}.
 */
public void addPerfResult(final RegressionType regressionType, final String pageTitle) throws ScenarioFailedError {

	// Check that performances are enabled
	if (!PERFORMANCE_ENABLED) {
		throw new ScenarioFailedError("Performances are not enabled for the scenario execution. Use -DperformanceEnabled=true to avoid this failure.");
	}

	// Experimental Client loading
	PerfManager perfManager = this.browser.getPerfManager();
	perfManager.loadClient();

	// Set regression type
	if (regressionType != null) {
		setPerfManagerRegressionType(regressionType,false);
	}

	// Post final performance result
	perfManager.addPerfResult(pageTitle, getUrl());
}

/**
 * Set on the check-box found in the page using the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the check-box is not found before {@link #timeout()} seconds</li>
 * <li>if the check-box is already checked, then nothing happen</li>
 * <li>validate that the check-box is well checked after having clicked on it</li>
 * </p>
 * @param findBy The mechanism to find the check-box element in the current
 * page
 * @return The check-box web element (as a {@link BrowserElement}) found
 * in the page
 *
 * @see #waitForElement(By)
 * @see Browser#check(BrowserElement, int, boolean)
 */
protected BrowserElement check(final By findBy) {
	BrowserElement element = waitForElement(findBy);
	this.browser.check(element, 1/*on*/, true/*validate*/);
	return element;
}

/**
 * Set or unset the check-box found inside the current page using the given locator.
 * <p>
 * The result of the check operation will be verified.
 * </p><p>
 * Note that this method can also be used to 'select' a radio button, i.e.,
 * when the argument 'on' is <code>true</code>.  Technically, there is no way to
 * 'unselect' a radio button (without selecting some other radio button), so this
 * method should not be used with a radio button and <code>false</code>.
 * </p><p>
 * Note that it will fail:
 * <ul>
 * <li>if the check-box is not found before {@link #timeout()} seconds</li>
 * <li>if the check-box is not in the expected state after the operation</li>
 * </p>
 * @param locator The locator to find the check-box element in the current page
 * @param on Tells whether the check-box should be set or unset.
 * @return The check-box web element (as a {@link BrowserElement}) found in the page
 *
 * @see #waitForElement(BrowserElement, By)
 * @see Browser#check(BrowserElement, int, boolean)
 */
protected BrowserElement check(final By locator, final boolean on) {
	return check(locator, on, true/*validate*/);
}

/**
 * Set or unset the check-box found inside the current page using the given locator
 * and checking the result if specified.
 * <p>
 * Note that this method can also be used to 'select' a radio button, i.e.,
 * when the argument 'on' is <code>true</code>.  Technically, there is no way to
 * 'unselect' a radio button (without selecting some other radio button), so this
 * method should not be used with a radio button and <code>false</code>.
 * </p><p>
 * Note that it will fail:
 * <ul>
 * <li>if the check-box is not found before {@link #timeout()} seconds</li>
 * <li>if the check-box is not in the expected state after the operation</li>
 * </p>
 * @param locator The locator to find the check-box element in the current page
 * @param on Tells whether the check-box should be set or unset.
 * @param validate Tells whether the validate check-box after the operation or not
 * @return The check-box web element (as a {@link BrowserElement}) found in the page
 *
 * @see #waitForElement(BrowserElement, By)
 * @see Browser#check(BrowserElement, int, boolean)
 */
protected BrowserElement check(final By locator, final boolean on, final boolean validate) {
	BrowserElement element = waitForElement(locator);
	this.browser.check(element, on ? 1 : -1, validate);
	return element;
}

/**
 * Set on the given check-box web element.
 * <p>
 * Note that:
 * <ul>
 * <li>if the check-box is already checked, then nothing happen</li>
 * <li>validate that the check-box is well checked after having clicked on it</li>
 * </p>
 * @param element The check-box to check
 * @return <code>true</code>If the check-box value has been changed,
 * <code>false</code> otherwise.
 *
 * @see Browser#check(BrowserElement, int, boolean)
 */
protected boolean check(final BrowserElement element) {
	return this.browser.check(element, 1/*on*/, true/*validate*/);
}

/**
 * Set/Unset the given check-box web element .
 * <p>
 * Note that:
 * <ul>
 * <li>if the check-box is already in the given state, then nothing happen</li>
 * <li>validate that the check-box is well checked/unchecked after having clicked
 * on it</li>
 * </p>
 * @param element The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param on Tells whether set or unset the check-box
 * @return <code>true</code>If the check-box value has been changed,
 * <code>false</code> otherwise.
 *
 * @see #check(BrowserElement, By, int, boolean)
 */
protected boolean check(final BrowserElement element, final boolean on) {
	return this.browser.check(element, on ? 1 : -1, true/*validate*/);
}

/**
 * Set on the check-box found inside the given parent web element using
 * the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the check-box is not found before {@link #timeout()} seconds</li>
 * <li>if the check-box is already checked, then nothing happen</li>
 * <li>validate that the check-box is well checked after having clicked on it</li>
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param findBy The mechanism to find the check-box element in the current
 * page
 * @return The check-box web element (as a {@link BrowserElement}) found
 * in the page
 *
 * @see #check(BrowserElement, By, int, boolean)
 */
protected BrowserElement check(final BrowserElement parentElement, final By findBy) {
	return check(parentElement, findBy, 1/*on*/, true/*validate*/);
}

/**
 * Toggle the check-box found inside the given parent web element using
 * the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the check-box is not found before {@link #timeout()} seconds</li>
 * <li>the check-box is checked if it was unchecked and vice-versa</li>
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param findBy The mechanism to find the check-box element in the current
 * page
 * @param toggle Tells whether the check-box should be toggled (0), set "on" (1)
 * or set "off" (-1). If any other values is specified then toggle (0) is assumed.
 * @param validate Tells whether to validate hat the check-box has the expected
 * state after having clicked on it</li>
 * @return The check-box web element (as a {@link BrowserElement}) found
 * in the page
 *
 * @see #waitForElement(By, By)
 * @see Browser#check(BrowserElement, int, boolean)
 */
protected BrowserElement check(final BrowserElement parentElement, final By findBy, final int toggle, final boolean validate) {
	BrowserElement element = waitForElement(parentElement, findBy);
	this.browser.check(element, toggle, validate);
	return element;
}

/**
 * Check the hover title of the given link element.
 * <p>
 * This check opens the hover by positioning the mouse pointer over
 * the given link element and checks whether its title matches the given text.
 * </p>
 * @param <P> The expected class for the hover
 * @param linkElement The link on which to hover
 * @param hoverClass The expected class for the hover
 * @return The opened hover web element as {@link BrowserElement}
 * @throws ScenarioFailedError in following cases:
 * <ul>
 * <li>The hover is not found (typically when it fails to open)</li>
 * <li>The hover title is not found after {@link #shortTimeout()} (typically
 * when the hover is still empty when the timeout is reached)</li>
 * <li>The title does not match the expected one</li>
 * </ul>
 * TODO Should infer the hover class as done in ClmProjectAreaPageHelper#checkRichHover(...)
 */
public <P extends LinkHoverElement<? extends Page>> P checkHoverTitle(final BrowserElement linkElement, final Class<P> hoverClass) {
	if (DEBUG) debugPrintln("		+ Check hover on "+linkElement+" using "+hoverClass);
	Assert.assertNotNull("Cannot hover on a null element.", linkElement);

	// Get hover
	P hover = hoverOverLink(linkElement, hoverClass);

	// Check the title
	if (!hover.getTitleElement().getText().equals(linkElement.getText())) {
		throw new ScenarioFailedError("Unexpected hover title.");
	}

	// Return the rich hover for further usage
	return hover;
}

/**
 * Check whether an error occurs by preventing the page being loaded and take an appropriate action in
 * such a situation.
 * <p>
 * A loading errors may redirect the original web page to an error page at times. Such an error must be
 * handled by making the framework aware that a redirection has happened and the current page is now a
 * different web page than what was expected.
 * </p>
 */
protected void checkLoadingErrors() {
	// Check if the page is in application context.
	// Sometimes blank or error pages are loaded due to various product defects.
	if(!isInApplicationContext()) {
		// A BrowserError must be raised in such a situation.
		throw new BrowserError("Web page '" + getUrl() + "' is out of scope/context of application '" + getApplication().getName() + "'");
	}
}

/**
 * Check the rich hover of the given link element.
 * <p>
 * This check opens the rich hover by positioning the mouse pointer over
 * the given link element and perform checks on its content (typically the title).
 * </p>
 * @param <RH> The expected class for the hover
 * @param linkElement The link on which to hover
 * @param richHoverClass The expected class for the hover
 * @return The opened rich hover web element as {@link BrowserElement}
 * @throws ScenarioFailedError in following cases:
 * <ul>
 * <li>The rich hover is not found (typically when it fails to open)</li>
 * <li>The rich hover title is not found after {@link #shortTimeout()} (typically
 * when the hover is still empty when the timeout is reached)</li>
 * <li>The title does not match the expected one</li>
 * </ul>
 * TODO Should infer the hover class as done in ClmProjectAreaPageHelper#checkRichHover(...)
 */
public <RH extends RichHoverElement<? extends Page>> RH checkRichHover(final BrowserElement linkElement, final Class<RH> richHoverClass, final String... pageData) {
	if (DEBUG) debugPrintln("		+ Check rich hover on "+linkElement+" using "+richHoverClass);
	Assert.assertNotNull("Cannot hover on a null element.", linkElement);

	// Get hover
	RH richHover = richHoverOverLink(linkElement, richHoverClass, pageData);

	// Check the hover
	richHover.check();

	// Return the rich hover for further usage
	return richHover;
}

/**
 * Click on the web element found in the current page using the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * </p>
 * @param findBy The mechanism to find the element in the current page
 * @return The web element (as a {@link BrowserElement}) found in the page
 *
 * @see #waitForElement(By)
 * @see BrowserElement#click()
 */
protected BrowserElement click(final By findBy) {
	return click((By)null, findBy);
}

/**
 * Click on the web element found relatively to the parent web element found
 * using the respective given mechanisms.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if either the parent or the element are not found before
 * {@link #timeout()} seconds</li>
 * </p>
 * @param parentBy The mechanism to find the parent element in the current
 * page, if <code>null</code>, the element will be searched in the entire page
 * content
 * @param findBy The mechanism to find the element in the current page or
 * from the given parent element if not <code>null</code>
 * @return The web element (as a {@link BrowserElement}) found in the page
 *
 * @see #waitForElement(By, By)
 * @see BrowserElement#click()
 */
protected BrowserElement click(final By parentBy, final By findBy) {
	if (DEBUG) debugPrintln("		+ Click on "+(parentBy==null ? "" : parentBy+"//")+findBy);
	BrowserElement parentElement = parentBy == null ? null : waitForElement(parentBy);
	return click(parentElement, findBy);
}

/**
 * Click on the web element found using the given mechanism relatively to
 * the given parent web element.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if either the parent or the element are not found before
 * {@link #timeout()} seconds</li>
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param findBy The mechanism to find the element in the current page
 * @return The web element (as a {@link BrowserElement}) found in the page
 *
 * @see #waitForElement(BrowserElement, By)
 * @see BrowserElement#click()
 */
public BrowserElement click(final BrowserElement parentElement, final By findBy) {
	if (DEBUG) debugPrintln("		+ Click on "+parentElement+"//"+findBy);

	// Store page title in case of perf result
	String pageTitle = getTitle();

	// Wait for element
	BrowserElement element = waitForElement(parentElement, findBy);

	// Click on given element
	// At times, the element may be obscured by another element and therefore, not be clickable.
	// As a result, a WebDriverException can occur.
	try {
		element.click();
	}
	catch (WebDriverException e) {
		// If the element.click() method causes a WebDriverException, use JavaScript to perform the
		// click on the element in this case.
		debugPrintln("Clicking on element (WebBrowserElement.click()) caused following error. Therefore, try JavaScript (WebBrowserElement.clickViaJavaScript()) to perform click as a workaround.");
		debugPrintln(e.toString());
		debugPrintStackTrace(e.getStackTrace(), 1 /*tabs*/);
		element.clickViaJavaScript();
	}

	// Add Performance result
	if (PERFORMANCE_ENABLED) {
		addPerfResult(RegressionType.CLIENT, pageTitle+ ": Action: " + findBy);
	}

	// Return the found element
	return element;
}

/**
 * Click on the button found in the current page using the given
 * mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * <li>there's no verification that the button turns to enable after having clicked
 * on it</li>
 * </p>
 * @param buttonBy The mechanism to find the button in the current page
 * @return The web element (as a {@link BrowserElement}) found in the page
 *
 * @see #waitForElement(By)
 * @see Browser#clickButton(BrowserElement, int, boolean)
 */
public BrowserElement clickButton(final By buttonBy) {
	BrowserElement button = waitForElement(buttonBy);
	return this.browser.clickButton(button, timeout(), false);
}

/**
 * Click on the button found in the current page using the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * </p>
 * @param buttonBy The mechanism to find the button in the current page
 * @param check Tells whether to check the button turns disabled after having
 * been clicked or not.
 * @return The web element (as a {@link BrowserElement}) found in the page
 *
 * @see #waitForElement(By)
 * @see Browser#clickButton(BrowserElement, int, boolean)
 */
public BrowserElement clickButton(final By buttonBy, final boolean check) {
	BrowserElement button = waitForElement(buttonBy);
	return this.browser.clickButton(button, timeout(), check);
}

/**
 * Click on the button found in the current page using the given
 * mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before the given timeout (in seconds)</li>
 * <li>there's no verification that the button turns to enable after having clicked
 * on it</li>
 * </p>
 * @param buttonBy The mechanism to find the button in the current page
 * @param time_out The time (in seconds) to wait before giving up the research
 * @return The web element (as a {@link BrowserElement}) found in the page
 *
 * @see #waitForElement(By)
 * @see Browser#clickButton(BrowserElement, int, boolean)
 */
protected BrowserElement clickButton(final By buttonBy, final int time_out) {
	BrowserElement button = waitForElement(buttonBy, true/*fail*/, time_out);
	return this.browser.clickButton(button, time_out, false);
}

/**
 * Click on the given button.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * </p>
 * @param button The button in the current page
 * @param check Tells whether to check the button turns disabled after having
 * been clicked or not.
 * @return The web element (as a {@link BrowserElement}) found in the page
 *
 * @see Browser#clickButton(BrowserElement, int, boolean)
 */
protected BrowserElement clickButton(final BrowserElement button, final boolean check) {
	return this.browser.clickButton(button, timeout(), check);
}

/**
 * Click on the button found relatively to the given parent web element.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * <li>there's no verification that the button turns to enable after having clicked
 * on it</li>
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param buttonBy The mechanism to find the button in the current page
 * @return The web element (as a {@link BrowserElement}) found in the page
 *
 * @see #waitForElement(BrowserElement, By)
 * @see Browser#clickButton(BrowserElement, int, boolean)
 */
public BrowserElement clickButton(final BrowserElement parentElement, final By buttonBy) {
	BrowserElement button = waitForElement(parentElement, buttonBy);
	return this.browser.clickButton(button, timeout(), false);
}

/**
 * Click on the button found relatively to the given parent web element.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param buttonBy The mechanism to find the button in the current page
 * @param check Tells whether to check the button turns disabled after having
 * been clicked or not.
 * @return The web element (as a {@link BrowserElement}) found in the page
 *
 * @see #waitForElement(BrowserElement, By)
 * @see Browser#clickButton(BrowserElement, int, boolean)
 */
protected BrowserElement clickButton(final BrowserElement parentElement, final By buttonBy, final boolean check) {
	BrowserElement button = waitForElement(parentElement, buttonBy);
	return this.browser.clickButton(button, timeout(), check);
}

@Override
public boolean equals(final Object o) {
	if (o instanceof Page) {
		Page p = (Page) o;
		return getUrl().equals(p.getUrl());
	}
	return false;
}

/**
 * Get the page content.
 *
 * @return The page instance
 */
public final Page get() {
	if (DEBUG) {
		debugPrintln("		+ get page content ("+getLocation()+")");
		debugPrintln("		  -> browser URL: "+getUrl());
		debugPrintln("		  -> current user: "+getUser());
	}

	final boolean verifyPageUser = getParameterBooleanValue(VERIFY_PAGE_USER_ARGUMENT, true);

	// Do nothing if the page is already loaded
	if (this.loginUser == null && isLoaded()) {
		if (DEBUG) {
			debugPrintln("		  -> page was already loaded");
			debugPrintln("		  -> browser URL: "+getUrl());
		}

		// Add performances result
		if (PERFORMANCE_ENABLED) {
			addPerfResult(RegressionType.SERVER, getTitle());
		}

		// Check for loading errors and take an appropriate action in such a situation.
		// A loading errors may redirect the original web page to an error page at times. Such an error must be
		// handled by making the framework aware that a redirection has happened and the current page is now a
		// different web page than what was expected.
		checkLoadingErrors();

		// Prepare the page for test execution by performing last minute
		// tasks such as suppressing unwanted panes, messages, notifications ...etc.
		prepare();

		// Verify user if available and requested to do so.
		if (verifyPageUser && (getUser() != null)) {
			verifyPageUser();
		}

		// Add page to history
	   	getPagesHistory().add(this);

	   	// Returned opened page.
		return this;
	}

	// Get page content
	long start = System.currentTimeMillis();
	if (DEBUG) {
		debugPrintln("		  -> loading page...");
		debugPrintln("		  -> browser URL: "+getUrl());
	}

	// Start server timer
	if (PERFORMANCE_ENABLED) {
		startPerfManagerServerTimer();
	}

	// Load the web page
	load();

	// Verify user if available and requested to do so.
	if (verifyPageUser && (getUser() != null)) {
		verifyPageUser();
	}

	// Add performance result
	if (PERFORMANCE_ENABLED) {
		addPerfResult(RegressionType.SERVER, getTitle());
	}

	// Returned opened page
	if (DEBUG) {
		debugPrintln("		  -> page loaded in "+elapsedTimeString(start));
		debugPrintln("		  -> browser URL: "+getUrl());
	}

	// Add page to history
   	getPagesHistory().add(this);

   	return this;
}

/**
 * Return the application associated with the current page.
 *
 * @return The application as subclass of {@link Application}
 * @see Topology#getApplication(String)
 */
public Application getApplication() {
	return this.topology.getApplication(this.location);
}

private BrowserElement getBodyElement() {
	if (this.bodyElement == null) {
		this.bodyElement = waitForElement(By.xpath("//body"));
	}

	return this.bodyElement;
}

/**
 * Return the browser associated with the current page.
 *
 * @return The browser as {@link Browser}
 */
public Browser getBrowser() {
	return this.browser;
}

/**
 * Return the xpaths of elements indicating that the page is undergoing an operation (busy).
 * <p>
 * The availability of such an element implies that at least a part of the
 * web page is still loading.
 * </p>
 *
 * @return Return the xpaths of elements indicating that the page is undergoing an operation.
 *
 */
protected abstract By[] getBusyIndicatorElementLocators();

/**
 * Return the elements indicating that the page is undergoing an operation (busy).
 * <p>
 * The availability of such an element implies that at least a part of the
 * web page is still loading.
 * </p>
 *
 * @return Return the elements indicating that the page is undergoing an operation or
 * <code>null</code> if no such element was found.
 *
 */
protected BrowserElement[] getBusyIndicatorElements() {
	return waitForMultipleElements(false /*fail*/, tinyTimeout(), getBusyIndicatorElementLocators());
}

/**
 * Return the configuration associated with the current page.
 *
 * @return The configuration as {@link Config}
 */
public Config getConfig() {
	return this.config;
}

/**
 * Return the data associated with the current web page.
 *
 * @return The data associated with the current web page.
 */
public String[] getData() {
	return this.data;
}

/**
 * Return a list of files in the download directory.
 *
 * @return A list of files in the download directory as {@link List}.
 *
 * @deprecated Use {@link Browser#getDownloadDirContents()} instead.
 */
@Deprecated
protected List<File> getDownloadDirContents() {
	return this.browser.getDownloadDirContents();
}

//protected String getExpectedTitle() {
//	if (isTitleExpected()) {
//		throw new ScenarioMissingImplementationError(getClassSimpleName(getClass())+".getExpectedTitle() method");
//	}
//	return null;
//}

/**
 * Return the expected title for the current web page.
 * <p>
 * Note that subclasses which do not expect a title in their page should have
 * the {@link #getExpectedTitle()} method to return <code>null</code>.
 * </p><p>
 * Otherwise, subclasses which are expecting a title have to override this method
 * to avoid having a {@link ScenarioFailedError} error thrown.
 * </p>
 * @return The page title as a {@link String}
 * @throw ScenarioMissingImplementationError If a subclass expects a title
 * and does not override this method.
 */
protected abstract String getExpectedTitle();

/**
 * Return a list of frames available in the web page.
 * <p>
 * Any frame within another frame (child frames) will not be
 * included by this list.
 * </p>
 * @return A list of frames available in the web page as {@link List}
 */
protected List<BrowserElement> getFrames() {
	return waitForElements(By.xpath("//iframe"), false /*fail*/, tinyTimeout());
}

/**
 * Return the page location used when creating it.
 *
 * @return The page location
 */
@Override
public String getLocation() {
	return this.location;
}

protected BrowserElement getLoggedUserElement(final boolean fail, final int waitTimeout) throws WaitElementTimeoutError {
	if (DEBUG) debugPrintln("		+ Get logged user web element (fail is "+fail+", timeout is "+waitTimeout+" sec)");
	return waitForElement(getLoggedUserElementLocator(), fail, waitTimeout, false /*displayed*/);
}

/**
 * Return the locator of the web element displaying the logged user name.
 *
 * @return The locator as a {@link By}.
 */
protected abstract By getLoggedUserElementLocator();

/**
 * Return a new file appeared in the download directory.
 * <p>
 * Such a file may appear as a result of a download.
 * </p>
 *
 * @param initialFiles A list of file existed in the download directory
 * prior for the new file to appear.
 *
 * @return The new file appeared in the download directory as {@link File}.
 *
 * @deprecated Use {@link Browser#getNewFileInDownloadDir(List)} instead.
 */
@Deprecated
protected File getNewFileInDownloadDir(final List<File> initialFiles) {
	return this.browser.getNewFileInDownloadDir(initialFiles);
}

/**
 * Get the root web element of the current web page.
 *
 * @return The root element as a {@link BrowserElement}.
 */
protected final BrowserElement getRootElement() {
	this.browser.resetFrame();
	return waitForElement(getRootElementLocator());
}

/**
 * Return the locator for the root web element of the current web page.
 *
 * @return The locator as a {@link By}.
 */
protected By getRootElementLocator() {
	return By.xpath("//body");
}

/**
 * {@inheritDoc}
 */
@Override
public String getTitle() {
	BrowserElement titleElement = waitForTitle(true /*fail*/);
	if (titleElement == null) return null;
	return titleElement.getText().trim();
}

/**
 * Return the title element locator.
 *
 * @return The title element locator as a {@link By}.
 */
protected abstract By getTitleElementLocator();

/**
 * Return the current test topology that the current page belongs to.
 *
 * @return The topology as a {@link Topology}
 */
public Topology getTopology() {
	return this.topology;
}

/**
 * Return the URL of the page loaded in the browser.
 *
 * @return The browser URL as a {@link String}
 * @see Browser#getCurrentUrl()
 */
public final String getUrl() {
	final String currentUrl = this.browser.getCurrentUrl();
	return getTopology().getPageUrl(currentUrl);
}

/**
 * Return the user used when the page was loaded.
 *
 * @return The {@link User user}.
 */
public User getUser() {
	return this.application.getUser();
}

/**
 * Move back a single "item" in the browser's history.
 *
 * @see Browser#back()
 */
public Page goBack() {
	if (DEBUG) debugPrintln("		+ Go back to previous page from "+this);

	// Back one page on browser
	this.browser.back();

	// Remove current page from history
	List<Page> pagesHistory = getPagesHistory();
	int size = pagesHistory.size();
	if (size < 2) {
		throw new ScenarioFailedError("We should have found a page to go back.");
	}
	pagesHistory.remove(size-1);

	// Return previous page
	return pagesHistory.get(size-2);
}

@Override
public int hashCode() {
	return getUrl().hashCode();
}

/**
 * Perform a mouse hovering over the given link element.
 *
 * @param <H> The hover specialized class
 * @param linkElement The web element on which to hover
 * @param hoverClass The hover specialized class
 * @return The opened hover as the given specialized class
 */
@SuppressWarnings("unchecked")
protected <H extends LinkHoverElement<? extends Page>> H hoverOverLink(final BrowserElement linkElement, final Class<H> hoverClass) {

	// Create the hover window
	H hover;
	try {
		Constructor<? extends LinkHoverElement<? extends Page>> constructor = hoverClass.getConstructor(Page.class);
		hover = (H) constructor.newInstance(this);
	}
	catch (WebDriverException e) {
		throw e;
	}
	catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
		println("Exception cause: " + e.getCause());
		throw new ScenarioFailedError(e);
	}
	catch (Throwable e) {
		println("Exception cause: " + e.getCause());
		throw new WaitElementTimeoutError(e);
	}

	// Hover over the link element
	hover.open(linkElement);

	// Return the created hover
	return hover;
}

/**
 * Initialize and return the NLS messages instance.
 * <p>
 * By default no NLS support is provided in web pages. Each subclass needs
 * to override this method to return their own NLS messages class in order to
 * have convienent getters of the supported NLS strings.
 * </p>
 * @return A NLS messages instance as a subclass of {@link NlsMessages}.
 */
protected NlsMessages initNlsMessages() {
	return null;
}

/**
 * Specifies whether the web page is in the context of the application.
 * <p>
 * A web page in the context of the application should contain common elements
 * such as a header, footer, banner ...etc. This method checks whether the web page
 * contains one or more such common elements.
 * </p>
 *
 * @return <code>true</code> if the the web page is in the context of the
 * application or <code>false</code> otherwise.
 */
public abstract boolean isInApplicationContext();

/**
 * Return whether the page is currently loaded or not.
 * <p>
 * By default, check whether the current URL (see {@link #getUrl()}) starts with
 * the page location or not.
 * </p><p>
 * Subclasses might want (or need) to override this default behavior. However,
 * it's strongly recommended to always call this super implementation to be sure
 * that browser URL is still checked.
 * </p>
 */
protected boolean isLoaded() {
	if (DEBUG) debugPrintln("		+ Test if the current web page is already loaded:"+this.location);

	if(!getClass().isInstance(getCurrentPage()) || !matchBrowserUrl()){
		return false;
	}

	// Check if the page is in application context.
	// Sometimes blank or error pages are loaded due to various product defects.
	if(!isInApplicationContext()) {
		// A BrowserError must be raised in such a situation.
		throw new BrowserError("Web page '" + getUrl() + "' is out of scope/context of application '" + getApplication().getName() + "'");
	}

	// Get the title
	waitForTitle(false/*do not fail*/);

	// Test the title
	if (getTitle() != null) {
		// Wait for the loading end
		long timeoutMillis = 2 * openTimeout() * 1000 + System.currentTimeMillis();	 // Timeout currentTimeMilliseconds

		while (getTitle().startsWith("Loading")) {
			if (System.currentTimeMillis() > timeoutMillis) { // Timeout currentTimeMilliseconds test timeout
				return false;
			}
		}

		// If the title does not match, the page is still loading
		if(!matchTitle()){
			return false;
		}
	}

	// Finally, check if at least one spinner/loading element is visible in both main page and all iFrames.
	// If such an element exists, then the page is still loading.
	if(getBusyIndicatorElements() != null) return false;
	// Checking iFrames
	for (BrowserElement frameElement : getFrames()) {
		this.browser.selectFrame(frameElement);
		boolean isLoading = getBusyIndicatorElements() != null;
//		WebBrowserElement errorPageContentElement = getErrorPageContainerElement();
		this.browser.resetFrame();

//		if (errorPageContentElement != null) throw new ApsPortalServerMessageError(this, errorPageContentElement);
		if (isLoading) return false;
	}

	return true;
}

/**
 * Specifies whether a given is a temporary file.
 * <p>
 * Such a file may be created during a download operation to collect the
 * downloaded content and discarded after the download has completed.
 * </p>
 *
 * @param file The corresponding file to check.
 *
 * @return <code>true</code> if the given is a temporary file or
 * <code>false</code> otherwise.
 *
 * @deprecated Use {@link FileUtil#isTemporaryFile(File)} instead.
 */
@Deprecated
protected boolean isTemporaryFile(final File file) {
	return FileUtil.isTemporaryFile(file);
}

/**
 * Specifies if a title is expected for the element.
 *
 * @return <code>true</code> if a title is expected or <code>false</code> otherwise.
 */
protected boolean isTitleExpected() {
	return (getExpectedTitle() != null) && (getTitleElementLocator() != null);
}

@SuppressWarnings("unused")
private boolean isValidUrl(final String url) {
	try {
		new URL(url);
		return true;
	}
	catch (MalformedURLException e) {
		return false;
	}
}

/**
 * Load the current page into the browser.
 * <p>
 * By default, it loads the current page location into the browser (see
 * {@link Browser#get(String)}.
 * </p><p>
 * Subclasses might want (or need) to override this default behavior to add
 * specific load operations. However, it's strongly recommended to always call
 * this super implementation to be sure that browser will perform the load operation.
 * </p>
 */
protected void load() {
	debugPrintln("		+ Load the current page: "+this.location);

	if (!matchBrowserUrl() || (this.loginUser != null)) {
		// Load the page if the browser does not match the current page location.
		this.browser.get(this.location);

		// Login if necessary
		if (this.loginUser != null) {
			if (DEBUG) debugPrintln("		  -> should login");
			performLogin(this.loginUser);
			this.loginUser = null;
		}

		// Wait initial loading
		waitInitialPageLoading();
	}

	this.refreshed = false;

	// Check for loading errors and take an appropriate action in such a situation.
	// A loading errors may redirect the original web page to an error page at times. Such an error must be
	// handled by making the framework aware that a redirection has happened and the current page is now a
	// different web page than what was expected.
	checkLoadingErrors();

	// Wait for the title
	if (isTitleExpected() && (waitForTitle(true) == null)) {
		// That might happen in case of error message in the loaded page
		return;
	}

	// Wait for the expected title
	waitForExpectedTitle();

	// Wait while busy
	waitWhileBusy(2 * openTimeout());

	// Prepare the page for test execution by performing last minute
	// tasks such as suppressing unwanted panes, messages, notifications ...etc.
	prepare();
}

/**
 * Login the page from current user to the given user.
 * <p>
 * Nothing happen if the current user is already logged in.
 * </p>
 * @param user The user to log in.
 */
public boolean login(final User user) {
	return login(user, false /* force */);
}

/**
 * Login the page from current user to the given user.
 * <p>
 * Nothing happen if the current user is already logged in.
 * </p>
 * @param user The user to log in.
 * @param force Force the user login, even if it's already logged
 */
public boolean login(final User user, final boolean force) {

	// Current user
	User currentUser = getUser();

	// Login if necessary
	if (force || needLogin(user)) {

		// Check whether the current page is on the expected user or not
		BrowserElement userProfileElement;
		if (!force) {
			userProfileElement = getLoggedUserElement(false/*fail*/, 2/*sec*/);
			if (userProfileElement != null && matchDisplayedUser(user, userProfileElement)) {
				return true;
			}
		}

		// Logout if necessary
		if (currentUser != null) {
			performLogout();
		}

		// Set user login
		this.loginUser = user;

		// Get page content (the login operation will happen there...)
		get();

		// Check that new user is well displayed in the page
		userProfileElement = getLoggedUserElement(true/*fail*/, timeout());
		if (!matchDisplayedUser(user, userProfileElement)) {
			println("WARNING: User name '"+userProfileElement.getText()+"' does not match expected one: '"+user.getName()+"'");
		}

		// Return that user has changed
		return true;
	}

	// No real login
	return false;

}

/**
 * Perform a logout operation.
 */
public void logout() {
	performLogout();
	this.application.logout();
}

/**
 * Make a hidden element with the given id visible in the web page.
 *
 * @param id The id of the element to make visible.
 */
public void makeElementVisible(final String id) {
	this.browser.executeScript("document.getElementById('" + id + "').style.visibility = 'visible';");
	this.browser.executeScript("document.getElementById('" + id + "').style.display = 'block';");
}

/**
 * Return whether the current page location matches the browser URL or not.
 * <p>
 * That basically looks whether the browser URL starts with the location after
 * having replaced "%20" characters by space. But that might be refined by
 * subclass to more subtle match.
 * </p><p>
 * At this general level, it also try to see if there's an id (ie. "&id=") in both
 * addresses and , if it's the case, compare them.
 * </p>
 * @return <code>true</code> if the location and the url match, <code>false</code>
 * otherwise.
 */
protected boolean matchBrowserUrl() {
	String url = getUrl();
	// Special case when restarting FireFox after it has died or
	// stating Chrome or Safari respectively.
	if (url.equals("about:blank") || url.startsWith("data:") || url.startsWith("chrome") || url.isEmpty()) {
		return false;
	}

	// Compare URL starts
	URL browserURL = null;
	URL pageURL = null;
	try {
		browserURL = new URL(URLDecoder.decode(url, "UTF-8"));
		pageURL = new URL(URLDecoder.decode(this.location, "UTF-8"));
		if (!browserURL.getProtocol().equals(pageURL.getProtocol()) ||
		        !browserURL.getHost().equals(pageURL.getHost()) ||
		        browserURL.getPort() != pageURL.getPort() ||
		        !browserURL.getPath().startsWith(pageURL.getPath())) {
			return false;
		}
	}
	catch (MalformedURLException mue) {
		throw new ScenarioFailedError(mue);
	}
	catch (UnsupportedEncodingException uee) {
		throw new ScenarioFailedError(uee);
	}

	// If fragments are the same then return now
	String browserUrlRef = browserURL.getRef();
	if (browserUrlRef != null && browserUrlRef.equals(pageURL.getRef())) {
		return true;
	}

	// Check IDs if possible
	String browserUrl = browserURL.toString();
	int idx1 = -1, idx2 = -1;
	if ((idx1 = browserUrl.indexOf("&id=")) > 0 && (idx2 = this.location.indexOf("&id=")) > 0) {
		String browserUrlID = browserUrl.substring(idx1 + 4);
		if ((idx1 = browserUrlID.indexOf('&')) > 0) {
			browserUrlID = browserUrlID.substring(0, idx1);
		}
		String locationID = this.location.substring(idx2 + 4);
		if ((idx2 = locationID.indexOf('&')) > 0) {
			locationID = locationID.substring(0, idx2);
		}
		if (browserUrlID.equals(locationID)) {
			return true;
		}
	}

	// Remove vvc configuration from browser URLs
	int vvcIndex = -1;
	if ((vvcIndex = browserUrl.indexOf("&vvc.configuration=")) > 0) {
		browserUrl = browserUrl.substring(0, vvcIndex);
	}

	// Check that browser URL at least starts with page location
	String pageUrl = getTopology().getPageUrl(this.location).replaceAll("%20", SPACE_STRING);
	try {
		pageUrl = URLDecoder.decode(pageUrl, "UTF-8");
	} catch (UnsupportedEncodingException e) {
		// skip
	}
	if ((vvcIndex = pageUrl.indexOf("&vvc.configuration=")) > 0) {
		pageUrl = pageUrl.substring(0, vvcIndex);
	}
	return browserUrl.replaceAll("%20", SPACE_STRING).startsWith(pageUrl);
}

/**
 * Return whether the displayed user matches the user name or not.
 * <p>
 * Note that this method is only called when page has a logged in a user.
 * </p>
 * @param user The user to check name display
 * @param loggedUserElement The web element displaying user name in the page.
 * @return <code>true</code> if the displayed user matches the page user name,
 * <code>false</code> otherwise.
 */
abstract protected boolean matchDisplayedUser(User user, BrowserElement loggedUserElement);

/**
 * Return whether the current page matches this page.
 * <p>
 * This method inspects whether the current page location matches the browser URL via
 * {@link #matchBrowserUrl()} and the title of the current page matches the expected one
 * via {@link #matchTitle()}. A subclass may further refine this method for a more
 * suitable match.
 * </p>
 *
 * @return <code>true</code> if the current page matches this page or <code>false</code>
 * otherwise.
 */
public boolean matchPage() {
	return matchBrowserUrl() && (!isTitleExpected() || matchTitle());
}

/**
 * Returns whether the page title matches the expected one.
 *
 * @return <code>true</code> if the page title is part of the expected title
 * or vice-versa, <code>false</code> otherwise.
 */
public boolean matchTitle() {
	String title = getTitle();
	Pattern pattern = Pattern.compile(getExpectedTitle(), DOTALL);

	return (title != null) && (pattern.matcher(title).matches() || title.equalsIgnoreCase(getExpectedTitle()));
}

public boolean needLogin(final User user){
	return this.topology.needLogin(this.location, user);
}

/**
 * Helper method to open a page and wait until it's finished loading.
 *
 * @param pageClass TThe class associated with the page to open
 * @param waiting Tells whether wait for the initial loading or not
 * @param pageData Additional CLM information to be stored in the page
 * @return The instance of the class associate with the page.
 */
private <P extends Page> P openAndWaitForPage(final Class<P> pageClass, final boolean waiting, final String... pageData) {

	// Wait that at least 'Loading...' appears in the page
	if (waiting) {
		waitInitialPageLoading(false /* throwError */);
	}

	// Open the page and for it being loaded
	P page = openPage(getUrl(), USER_ACTION_NOT_PROVIDED, this.config, getUser(), pageClass, pageData);
	page.waitForLoadingPageEnd();

	// Return the opened page
	return page;
}

/**
 * Helper method to open a page and wait until it's finished loading.
 *
 * @param pageLocation The page location
 * @param newConfig The config to open the page with
 * @param newUser The user to open the page with
 * @param pageClass TThe class associated with the page to open
 * @param pageData Additional CLM information to be stored in the page
 * @return The instance of the class associate with the page.
 */
public <P extends Page> P openAndWaitForPage(final String pageLocation, final IConfig newConfig, final IUser newUser, final Class<P> pageClass, final String... pageData) {

	// Open the page and for it being loaded
	P page = openPage(pageLocation, USER_ACTION_NOT_PROVIDED, (Config) newConfig, (User) newUser, pageClass, pageData);
	page.waitForLoadingPageEnd();

	// Return the opened page
	return page;
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkBy The link locator on which to click.
 * @param findBy The locator of the element opened after clicking on the link element.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link ElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends ElementWrapper> P openElementUsingLink(final By linkBy, final By findBy, final Class<P> elementClass, final String... elementData) {
	return openElementUsingLink(waitForElement(linkBy), findBy, elementClass, elementData);
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkBy The link locator on which to click.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link ElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends ElementWrapper> P openElementUsingLink(final By linkBy, final Class<P> elementClass, final String... elementData) {
	return openElementUsingLink(waitForElement(linkBy), elementClass, elementData);
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkElement The link on which to click.
 * @param findBy The locator of the element opened after clicking on the link element.
 * @param elementClass The class associated with the opened element.
 * @param clickType The type of click to make on the link element to open the new element as {@link ClickType}
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link ElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends ElementWrapper> P openElementUsingLink(final BrowserElement linkElement, final By findBy, final Class<P> elementClass, final ClickType clickType, final String... elementData) {
	if (DEBUG) debugPrintln("		+ Open element '"+ elementClass.getSimpleName() + "' from '" + getClass().getSimpleName() + "'");

	// Click on the link
	switch (clickType) {
	case CLICK:
		linkElement.makeVisible().click();
		break;
	case DOUBLE_CLICK:
		linkElement.makeVisible().doubleClick();
		break;
	default:
		throw new ScenarioFailedError("Click type '" + clickType + "' is not supported by this method");
	}

	P element;
	try {
		if(findBy != null) {
			if((elementData == null) || (elementData.length == 0)) {
				element = elementClass.getConstructor(Page.class, By.class).newInstance(this, findBy);
			}
			else {
				element = elementClass.getConstructor(Page.class, By.class, String[].class).newInstance(this, findBy, elementData);
			}
		}
		else {
			if((elementData == null) || (elementData.length == 0)) {
				element = elementClass.getConstructor(Page.class).newInstance(this);
			}
			else {
				element = elementClass.getConstructor(Page.class, String[].class).newInstance(this, elementData);
			}
		}
	}
	catch (WebDriverException e) {
		throw e;
	}
	catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
		println("Exception cause: " + e.getCause());
		throw new ScenarioFailedError(e);
	}
	catch (Throwable e) {
		println("Exception cause: " + e.getCause());
		throw new WaitElementTimeoutError(e);
	}

	// Wait for loading of element to finish.
	element.waitForLoadingEnd();
	return element;
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkElement The link on which to click.
 * @param findBy The locator of the element opened after clicking on the link element.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link ElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends ElementWrapper> P openElementUsingLink(final BrowserElement linkElement, final By findBy, final Class<P> elementClass, final String... elementData) {
	return openElementUsingLink(linkElement, findBy, elementClass, CLICK, elementData);
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkElement The link on which to click.
 * @param elementClass The class associated with the opened element.
 * @param clickType The type of click to make on the link element to open the new element as {@link ClickType}
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link ElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends ElementWrapper> P openElementUsingLink(final BrowserElement linkElement, final Class<P> elementClass, final ClickType clickType, final String... elementData) {
	return openElementUsingLink(linkElement, null /*findBy*/, elementClass, clickType, elementData);
}

/**
 * Click on the given link assuming that will open a new element.
 *
 * @param linkElement The link on which to click.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The element (as a subclass of {@link ElementWrapper}) opened after
 * having clicked on the link.
 */
public <P extends ElementWrapper> P openElementUsingLink(final BrowserElement linkElement, final Class<P> elementClass, final String... elementData) {
	return openElementUsingLink(linkElement, null /*findBy*/, elementClass, elementData);
}

/**
 * Retrieve the existing page for the browser current URL. Create it if it's the first
 * time the page is requested.
 *
 * @param pageClass The class associated with the page to open
 * @param pageData Additional CLM information to be stored in the page
 * @return The instance of the class associate with the page.
 */
public <P extends Page> P openPageUsingBrowser(final Class<P> pageClass, final String... pageData) {

	// Notify the perfManager that a page is loading
	if (PERFORMANCE_ENABLED) this.browser.getPerfManager().setPageLoading(true);

	// Open, wait for, and return the opened page
	return openAndWaitForPage(pageClass, true, pageData);
}

/**
 * Retrieve the existing page for the browser current URL. Create it if it's the first
 * time the page is requested.
 *
 * @param pageClass The class associated with the page to open
 * @param pageData Additional CLM information to be stored in the page
 * @return The instance of the class associate with the page.
 */
public <P extends Page> P openPageUsingBrowserWithoutWaiting(final Class<P> pageClass, final String... pageData) {

	// Notify the perfManager that a page is loading
	if (PERFORMANCE_ENABLED) this.browser.getPerfManager().setPageLoading(true);

	// Open, wait for, and return the opened page
	return openAndWaitForPage(pageClass, false, pageData);
}

/**
 * Click on the given hover title to open a new page.
 * <p>
 * Note that the browser url after having clicked on the hover title will be used
 * for the page location.
 * </p>
 * @param hover The hover on which to click on title
 * @param openedPageClass The class associated with the opened page
 * @param pageData Provide additional information to store in the page when opening it
 * @return The web page (as a subclass of {@link Page}) opened after
 * having clicked on the link
 *
 * @see #openPage(String, Config, User, Class, String...)
 * @see BrowserElement#click()
 */
public <P extends Page> P openPageUsingHoverTitle(final RichHoverElement<? extends Page> hover, final Class<P> openedPageClass, final String... pageData) {
	if (DEBUG) debugPrintln("		+ Click to hover title '"+hover.getTitleElement()+"' to open "+openedPageClass.getName());

	// Click on hover title and close it
	hover.getTitleElement().click();
	hover.close();

	// Open, wait for, and return the opened page
	return openAndWaitForPage(openedPageClass, true, pageData);
}

/**
 * Click on the link found using the given mechanism assuming that will open
 * a new {@link Page page}.
 * <p>
 * The opened page URL is got from the application which usually takes it from
 * the <code>href</code> attribute of the link web element (see
 * {@link Topology#getPageUrl(String)}.
 * </p><p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * <li>no additional info is provided to the opened page</li>
 * </p>
 * @param linkBy The mechanism to find the link element in the current page
 * @param openedPageClass The class associated with the opened page
 * @return The web page (as a subclass of {@link Page}) opened after
 * having clicked on the link
 *
 * @see #openPageUsingLink(BrowserElement, By, Class, boolean, int, String...)
 */
public <P extends Page> P openPageUsingLink(final By linkBy, final Class<P> openedPageClass) {
	return openPageUsingLink(null, linkBy, openedPageClass, true/*fail*/, timeout());
}

/**
 * Click on the link found using the given mechanism assuming that will open
 * a new {@link Page page}.
 * <p>
 * The opened page URL is got from the application which usually takes it from
 * the <code>href</code> attribute of the link web element (see
 * {@link Topology#getPageUrl(String)}.
 * </p><p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * <li>no additional info is provided to the opened page</li>
 * </p>
 * @param linkBy The mechanism to find the link element in the current page
 * @param openedPageClass The class associated with the opened page
 * @param timeOut Seconds to wait before giving up if the web element is not
 * found.
 * @return The web page (as a subclass of {@link Page}) opened after
 * having clicked on the link
 *
 * @see #openPageUsingLink(BrowserElement, By, Class, boolean, int, String...)
 */
public <P extends Page> P openPageUsingLink(final By linkBy, final Class<P> openedPageClass, final int timeOut) {
	return openPageUsingLink(null, linkBy, openedPageClass, false/*do not fail*/, timeOut);
}

/**
 * Click on the link found using the given mechanism assuming that will open
 * a new {@link Page page}.
 * <p>
 * The opened page URL is got from the application which usually takes it from
 * the <code>href</code> attribute of the link web element (see
 * {@link Topology#getPageUrl(String)}.
 * </p><p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * </p>
 * @param linkBy The mechanism to find the link element in the current page
 * @param openedPageClass The class associated with the opened page
 * @param info Provide additional information to store in the page when opening it
 * @return The web page (as a subclass of {@link Page}) opened after
 * having clicked on the link
 *
 * @see #openPageUsingLink(BrowserElement, By, Class, boolean, int, String...)
 */
public <P extends Page> P openPageUsingLink(final By linkBy, final Class<P> openedPageClass, final String... info) {
	return openPageUsingLink(null, linkBy, openedPageClass, true/*fail*/, timeout(), info);
}

/**
 * Click on the link found using the given mechanism assuming that will open
 * a new {@link Page page}.
 * <p>
 * The opened page URL is got from the application which usually takes it from
 * the <code>href</code> attribute of the link web element (see
 * {@link Topology#getPageUrl(String)}.
 * </p><p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * <li>no additional info is provided to the opened page</li>
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param linkBy The mechanism to find the link element in the current page
 * @param openedPageClass The class associated with the opened page
 * @return The web page (as a subclass of {@link Page}) opened after
 * having clicked on the link
 *
 * @see #openPageUsingLink(BrowserElement, By, Class, boolean, int, String...)
 */
public <P extends Page> P openPageUsingLink(final BrowserElement parentElement, final By linkBy, final Class<P> openedPageClass) {
	return openPageUsingLink(parentElement, linkBy, openedPageClass, true/*fail*/, timeout());
}

/**
 * Click on the link found using the given mechanism assuming that will open
 * a new {@link Page page}.
 * <p>
 * The opened page URL is got from the application which usually takes it from
 * the <code>href</code> attribute of the link web element (see
 * {@link Topology#getPageUrl(String)}.
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param linkBy The mechanism to find the link element in the current page
 * @param openedPageClass The class associated with the opened page
 * @param fail Tells whether to fail if none of the elements is find before timeout
 * @param time_out The time to wait before giving up the research
 * @param info Provide additional information to store in the page when opening it
 * @return The web page (as a subclass of {@link Page}) opened after
 * having clicked on the link
 *
 * @see Browser#waitForElement(BrowserElement, By, boolean, int, boolean, boolean)
 * @see #openPageUsingLink(BrowserElement, Class, String...)
 * @see BrowserElement#click()
 */
public <P extends Page> P openPageUsingLink(final BrowserElement parentElement, final By linkBy, final Class<P> openedPageClass, final boolean fail, final int time_out, final String... info) {
	BrowserElement linkElement = this.browser.waitForElement(parentElement, linkBy, fail, time_out, true/*visible*/, true/*single element expected*/);
	if (linkElement == null) return null;
	return openPageUsingLink(linkElement, openedPageClass, info);
}

public <P extends Page> P openPageUsingLink(final BrowserElement parentElement, final By linkBy, final Class<P> openedPageClass, final String... pageData) {
	return openPageUsingLink(parentElement, linkBy, openedPageClass, true/*fail*/, timeout(), pageData);
}

/**
 * Click on the given link assuming that will open a new page.
 * <p>
 * The opened page URL is got from the application (see
 * {@link Topology#getPageUrl(String)}) which usually takes
 * it from the <code>href</code> attribute of the link web element.
 * </p>
 * @param linkElement The link on which to click
 * @param openedPageClass The class associated with the opened page
 * @param postLinkClickAction The action to perform after clicking the link as {@link Action}.
 * @param pageData Provide additional information to store in the page when opening it
 * @return The web page (as a subclass of {@link Page}) opened after
 * having clicked on the link
 */
public <P extends Page> P openPageUsingLink(final BrowserElement linkElement, final Class<P> openedPageClass, final Action postLinkClickAction, final String... pageData) {
	return openPageUsingLink(linkElement, getUser(), openedPageClass, postLinkClickAction, pageData);
}

/**
 * Click on the given link assuming that will open a new page.
 * <p>
 * The opened page URL is got from the application (see
 * {@link Topology#getPageUrl(String)}) which usually takes
 * it from the <code>href</code> attribute of the link web element.
 * </p>
 * @param linkElement The link on which to click
 * @param openedPageClass The class associated with the opened page
 * @param pageData Provide additional information to store in the page when opening it
 * @return The web page (as a subclass of {@link Page}) opened after
 * having clicked on the link
 */
public <P extends Page> P openPageUsingLink(final BrowserElement linkElement, final Class<P> openedPageClass, final String... pageData) {
	return openPageUsingLink(linkElement, getUser(), openedPageClass, pageData);
}

/**
 * Click on the given link assuming that will open a new page.
 * <p>
 * The opened page URL is got from the application (see
 * {@link Topology#getPageUrl(String)}) which usually takes
 * it from the <code>href</code> attribute of the link web element.
 * </p>
 * @param linkElement The link on which to click
 * @param openedPageClass The class associated with the opened page
 * @param postLinkClickAction The action to perform after clicking the link as {@link Action}.
 * @param pageData Provide additional information to store in the page when opening it
 * @return The web page (as a subclass of {@link Page}) opened after
 * having clicked on the link
 */
@SuppressWarnings("unchecked")
public <P extends Page> P openPageUsingLink(final BrowserElement linkElement, final User user, final Class<P> openedPageClass, final Action postLinkClickAction, final String... pageData) {
	if (DEBUG) debugPrintln("		+ Click to link "+linkElement+ " to open web page of "+openedPageClass.getName()+" class");

	// Store current page information to check whether the link click did really occurred
	Page currentPage = getCurrentPage();
	String currentPageClass = currentPage == null ? null : getClassSimpleName(currentPage.getClass());
	String currentUrl = getUrl();
	String linkUrl = linkElement.getAttribute("href");
	String linkString = linkElement.toString();

	// Pause the given amount of time prior to clicking the link.
	if (this.delayBeforeLinkClick > 0) {
		if (DEBUG) debugPrintln("		  -> delay before click used: "+this.delayBeforeLinkClick+"ms");
		pause(this.delayBeforeLinkClick);
	}

	Set<String> beforeWindowHandles = this.browser.getWindowHandles();

	// Click on the link.
	// At times, the link element may be obscured by another element and therefore, not be clickable.
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

	// Pause the given amount of time after clicking the link.
	if (this.delayAfterLinkClick > 0) {
		if (DEBUG) debugPrintln("		  -> delay after click used: "+this.delayAfterLinkClick+"ms");
		pause(this.delayAfterLinkClick);
	}

	// Perform the given action after clicking the link.
	if(postLinkClickAction != null) postLinkClickAction.perform();

	// Notify the perfManager that a page is loading
	if (PERFORMANCE_ENABLED) this.browser.getPerfManager().setPageLoading(true);

	// The new page may be opened in a new window. If so, switch to the new Window by closing
	// the original windows. A BrowserUrlUnchangedError will be raised otherwise.
	Set<String> afterWindowHandles = this.browser.getWindowHandles();

	if(afterWindowHandles.size() > beforeWindowHandles.size()) {
		afterWindowHandles.removeAll(beforeWindowHandles);
		this.browser.switchToWindow(afterWindowHandles.iterator().next(), true /*close*/);
	}

	// Accept any alert if one is present
	try {
		this.browser.acceptAlert();
	}
	catch (NoAlertPresentException e) { /* do nothing if no alert is present*/}

	// Check that the browser URL has changed to a proper URL after the click. Browser URL may initially
	// change to something similar to `about:blank` or `data:xxxx` prior to changing to a proper URL after
	// the page contents have somewhat loaded. In such situations, wait until the browser URL gains its
	// proper form.
	String browserUrl = getUrl();
	if (DEBUG) debugPrintln("		  -> browser URL: "+browserUrl);
	int urlChangeTimeout = 2 * timeout();
	long stimeout = urlChangeTimeout *1000 + System.currentTimeMillis();
	int count = 0;
	while (currentUrl.equals(browserUrl = getUrl()) || !isValidUrl(browserUrl)) {
//		if (count == 0) {
//			println("================================================================================");
//			println("WARNING: Browser URL hasn't changed after having clicked on "+linkString);
//			println("	- current URL: "+currentUrl);
//			println("	- expected URL: "+linkUrl);
//			println("	- stack trace:");
//			printStackTrace(1);
//			println("	- browser URL: "+browserUrl);
//		}
		if (System.currentTimeMillis() > stimeout) {
			println("	Browser URL still stays the same after " + urlChangeTimeout + " seconds...");
//			println("	=> Workaround is to open page directly setting browser URL with: "+linkUrl);
			println("================================================================================");
//			return WebPage.openPage(linkUrl, getConfig(), user, openedPageClass, pageData);
			throw new BrowserUrlUnchangedError("Browser URL remains unchanged after " + urlChangeTimeout + " seconds.");
		}
		count++;
		sleep(1);
	}
	if (count > 0) {
		println();
		println("================================================================================");
	}

	// Wait that at least for the body element appeared in the page
	waitForElement(By.xpath("/html/body"));

	// Open the page and wait for it being loaded
	P page = openPage(browserUrl, USER_ACTION_NOT_PROVIDED, this.config, user, openedPageClass, pageData);
	page.waitForLoadingPageEnd();

	// Fail to click on links occasionally, which causes ClassCastExceptions
	// If link failed, directly open page from href property of the link
	Class<? extends Page> pageClass = page.getClass();
	boolean validClass = pageClass.equals(openedPageClass);
	while(!validClass) {
		pageClass = (Class< ? extends Page>) pageClass.getSuperclass();
		if (pageClass == null) break;
		validClass = pageClass.equals(openedPageClass);
	}
	if (!validClass) {
		println("================================================================================");
		println("WARNING: Unexpected new page class after having clicked on "+linkString);
		println("	- previous class before click: "+currentPageClass);
		println("	- current class after click: "+getClassSimpleName(page.getClass()));
		println("	- expected class after click: "+getClassSimpleName(openedPageClass));
		println("	=> Workaround is to open page directly setting browser URL with: "+linkUrl);
		println("================================================================================");
		return Page.openPage(linkUrl, getConfig(), user, openedPageClass, pageData);
	}

	// Return opened page
	return page;
}

/**
 * Click on the given link assuming that will open a new page.
 * <p>
 * The opened page URL is got from the application (see
 * {@link Topology#getPageUrl(String)}) which usually takes
 * it from the <code>href</code> attribute of the link web element.
 * </p>
 * @param linkElement The link on which to click
 * @param openedPageClass The class associated with the opened page
 * @param pageData Provide additional information to store in the page when opening it
 * @return The web page (as a subclass of {@link Page}) opened after
 * having clicked on the link
 */
public <P extends Page> P openPageUsingLink(final BrowserElement linkElement, final User user, final Class<P> openedPageClass, final String... pageData) {
	return openPageUsingLink(linkElement, user, openedPageClass, null /*prePageOpenAction*/, pageData);
}

@SuppressWarnings("unchecked")
public <P extends IPage> P openPageUsingLink2(final BrowserElement parentElement, final By linkBy, final Class<P> openedPageClass) {
	return (P) openPageUsingLink(parentElement, linkBy, (Class<? extends Page>) openedPageClass, true/*fail*/, timeout());
}

@SuppressWarnings("unchecked")
public <P extends IPage> P openPageUsingLink2(final BrowserElement parentElement, final By linkBy, final Class<P> openedPageClass, final boolean fail, final int time_out, final String... info) {
	return (P) openPageUsingLink(parentElement, linkBy, (Class<? extends Page>) openedPageClass, fail, time_out, info);
}

@SuppressWarnings("unchecked")
public <P extends IPage> P openPageUsingLink2(final BrowserElement linkElement, final Class<P> openedPageClass, final String... pageData) {
	return (P) openPageUsingLink(linkElement, (Class<? extends Page>) openedPageClass, pageData);
}

/**
 * Return the timeout while opening the page.
 *
 * @return The timeout as an <code>int</code>
 */
public int openTimeout() {
	return this.openTimeout;
}

/**
 * Park the mouse at a location where links are not found in order to prevent unexpected rich hovers
 * from loading by accident. The parking zone used for this purpose is the (0,0) location of
 * the <body> element, which is the top-left corner of the display area of the browser.
 */
public void parkMouse() {
	this.browser.moveToElement(getBodyElement(), 0, 0);
}

/**
 * Perform login operation on the current page to be connected to the given user.
 *
 * @param user The user to connect the page to
 */
abstract protected void performLogin(final User user);

/**
 * Logout the page from current user to new user.
 */
protected abstract void performLogout();

/**
 * Prepare the page for test execution by performing various operations.
 * <p>
 * For example, dismissing unwanted panes, messages, notifications ...etc.
 * </p>
 */
public abstract void prepare();

/**
 * {@inheritDoc}
 * <p>
 * If subclass overrides this method, it's strongly recommended to call the super
 * implementation in order to implicitly wait for the end of the page load, but also
 * to set the {@link #refreshed} flag...
 * </p>
 * @see #waitForLoadingPageEnd()
 */
@Override
public void refresh() {
	if (DEBUG) debugPrintln("		+ Refresh browser page content for current page '"+getLocation()+"'");

	// Perform the refresh action
	this.browser.refresh();

	// Accept any alert if one is present
	try {
		this.browser.acceptAlert();
	}
	catch (NoAlertPresentException e) { /* do nothing if no alert is present*/}

	// Replace the page location if necessary
	String browserUrl = this.browser.getCurrentUrl();
	if (!this.location.equals(browserUrl)) {
		Page historyPage = searchPageInHistory(this.location);
		if (historyPage != null) {
			getPagesHistory().remove(historyPage);
		}
		getPagesHistory().add(this);
		this.location = browserUrl;
		if (DEBUG) debugPrintln("		  -> the page location has been replaced with browser URL:'"+browserUrl+"'");
	}

	// Wait for the initial page loading
	waitInitialPageLoading();

	// Wait for the end of the page loading
	waitForLoadingPageEnd();

	// Store that a refresh occurred
	this.refreshed = true;

	// Prepare the page by disabling or suppressing unwanted elements such as notifications, tours, ...etc.
	prepare();
}

/**
 * Perform a mouse hovering over the given link element.
 *
 * @param <RH> The rich hover specialized class
 * @param linkElement The web element on which to hover
 * @param richHoverClass The rich hover specialized class
 * @param additionalData Additional data to check in the rich hover
 * @return The opened rich hover as the given specialized class
 */
@SuppressWarnings("unchecked")
public <RH extends TextHoverElement> RH richHoverOverLink(final BrowserElement linkElement, final Class<RH> richHoverClass, final String... additionalData) {

	// Check link element
	if (linkElement == null) {
		throw new WaitElementTimeoutError("Cannot hover over a null link.");
	}

	// Create the hover window
	RH richHover;
	try {
		if (additionalData == null || additionalData.length == 0) {
			Constructor<? extends TextHoverElement> constructor = richHoverClass.getConstructor(Page.class);
			richHover = (RH) constructor.newInstance(this);
		} else {
			Constructor<? extends TextHoverElement> constructor = richHoverClass.getConstructor(Page.class, String[].class);
			richHover = (RH) constructor.newInstance(this, additionalData);
		}
	}
	catch (WebDriverException e) {
		throw e;
	}
	catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
		println("Exception cause: " + e.getCause());
		throw new ScenarioFailedError(e);
	}
	catch (Throwable e) {
		println("Exception cause: " + e.getCause());
		throw new WaitElementTimeoutError(e);
	}

	// Tempo for Google Chrome browser
	if (this.browser.isGoogleChrome()) {
		sleep(1);
	}

	// Hover over the link element
	richHover.open(linkElement);

	// Return the created hover
	return richHover;
}

/**
 * Select the given item in the list element found using the given search
 * mechanism.
 * <p>
 * The items of the found list are supposed to be found using <code>by.xpath("./option")</code>
 * search mechanism.
 * </p>
 * @param findBy The mechanism to find the list element in the current page.
 * @param selection The item to select in the list, assuming that text matches
 * @return The selected element as {@link BrowserElement}.
 * @throws ScenarioFailedError if no item matches the expected selection.
 */
public BrowserElement select(final By findBy, final String selection) {
	return select(findBy, Pattern.compile(Pattern.quote(selection)));
}

/**
 * Select the given item in the list element found using the given search
 * mechanism.
 * <p>
 * The items of the found list are supposed to be found using <code>by.xpath("./option")</code>
 * search mechanism.
 * </p>
 * @param findBy The mechanism to find the list element in the current page.
 * @param pattern A pattern matching the item to select in the list, assuming that text matches
 * @return The selected element as {@link BrowserElement}.
 * @throws ScenarioFailedError if no item matches the expected selection.
 */
public BrowserElement select(final By findBy, final Pattern pattern) {
	BrowserElement listElement = waitForElement(findBy);
	return select(listElement, pattern);
}

/**
 * Select the given item in the given list element found.
 * <p>
 * The items of the selection list are supposed to be found using
 * <code>by.xpath("./option")</code> search mechanism.
 * </p>
 * @param listElement The list element in which perform the selection.
 * @param pattern A pattern matching the item to select in the list, assuming that text matches
 * @return The selected element as {@link BrowserElement}.
 * @throws ScenarioFailedError if no item matches the expected selection.
 */
public BrowserElement select(final BrowserElement listElement, final Pattern pattern) {
	BrowserElement[] selectedElements = this.browser.select(listElement, By.xpath(".//option"), pattern);
	return selectedElements[0];
}

/**
 * Select the given item in the given list element found.
 * <p>
 * The items of the selection list are supposed to be found using
 * <code>by.xpath("./option")</code> search mechanism.
 * </p>
 * @param listElement The list element in which perform the selection.
 * @param selection The item to select in the list, assuming that text matches
 * @return The selected element as {@link BrowserElement}.
 * @throws ScenarioFailedError if no item matches the expected selection.
 */
public BrowserElement select(final BrowserElement listElement, final String selection) {
	return select(listElement, Pattern.compile(Pattern.quote(selection)));
}

/**
 * Set the delay after link click in milliseconds.
 *
 * @param delayAfterLinkClick The delay after link click in milliseconds.
 */
public void setDelayAfterLinkClick(final int delayAfterLinkClick) {
	this.delayAfterLinkClick = delayAfterLinkClick;
}

final protected void setOpenTimeout(final int timeout) {
	this.openTimeout = timeout;
}

/**
 * Set regression type on performances manager.
 * <p>
 * This method sets the default regression type to provided regressionType.
 * </p>
 * @param regressionType : Regression type to apply.
 * @param override : True will override and lock the regression type,
 * while false will only change the regression type if it is not locked.
 * @throws ScenarioFailedError If performances are <b>not enabled</b> during
 * scenario execution. Hence, callers have to check whether the performances are
 * enabled before calling this method using {@link PerfManager#PERFORMANCE_ENABLED}.
 */
protected void setPerfManagerRegressionType(final RegressionType regressionType, final boolean override) throws ScenarioFailedError {
	if (PERFORMANCE_ENABLED) {
		this.browser.getPerfManager().setRegressionType(regressionType, override);
	} else {
		throw new ScenarioFailedError("Performances are not enabled for the scenario execution. Use -DperformanceEnabled=true to avoid this failure.");
	}
}

/**
 * Set user action name on performances manager.
 *
 * @param action The action name
 * @throws ScenarioFailedError If performances are <b>not enabled</b> during
 * scenario execution. Hence, callers have to check whether the performances are
 * enabled before calling this method using {@link PerfManager#PERFORMANCE_ENABLED}.
 */
protected void setPerfManagerUserActionName(final String action) throws ScenarioFailedError {
	if (PERFORMANCE_ENABLED) {
		this.browser.getPerfManager().setUserActionName(action);
	} else {
		throw new ScenarioFailedError("Performances are not enabled for the scenario execution. Use -DperformanceEnabled=true to avoid this failure.");
	}
}

final protected void setShortTimeout(final int timeout) {
	this.shortTimeout = timeout;
}

final protected void setTimeout(final int timeout) {
	this.timeout = timeout;
}

/**
 * Return the short timeout used on the page.
 *
 * @return The timeout as an <code>int</code>
 */
final public int shortTimeout() {
	return this.shortTimeout;
}

/**
 * Close the current browser session and open a new one.
 */
public void startNewBrowserSession() {
	this.browser = getConfig().openNewBrowser();
	this.driver = this.browser.getDriver();

	// Clear page cache except for current page object
	getPagesHistory().clear();

	//Clear login data
	getTopology().logoutApplications();
}

/**
 * Starts the perfManager server timer
 *
 * @throws ScenarioFailedError If performances are <b>not enabled</b> during
 * scenario execution. Hence, callers have to check whether the performances are
 * enabled before calling this method using {@link PerfManager#PERFORMANCE_ENABLED}.
 */
public void startPerfManagerServerTimer() throws ScenarioFailedError {
	if (PERFORMANCE_ENABLED) {
		this.browser.getPerfManager().startServerTimer();
	} else {
		throw new ScenarioFailedError("Performances are not enabled for the scenario execution. Use -DperformanceEnabled=true to avoid this failure.");
	}
}

///**
// * Close the current browser session and open a new one at the same page.
// */
//public void startNewBrowserSession() {
//	this.browser = getConfig().openNewBrowser();
//	this.driver = this.browser.driver;
//
//	// Clear page cache except for current page object
//	getPagesHistory().clear();
//	getPagesHistory().add(this);
//
//	//Clear login data
//	getTopology().logoutApplications();
//
//	// Reopen current page in browser
//	this.browser.get(this.location);
//}

/**
 * Takes a failure snapshot.
 *
 * @param fileName The name of the snapshot file.
 */
public void takeSnapshotFailure(final String fileName) {
	this.browser.takeSnapshotFailure(fileName);
}

/**
 * Takes an information snapshot.
 *
 * @param fileName The name of the snapshot file.
 */
public void takeSnapshotInfo(final String fileName) {
	this.browser.takeSnapshotInfo(fileName);
}

/**
 * Takes a warning snapshot.
 *
 * @param fileName The name of the snapshot file.
 */
public void takeSnapshotWarning(final String fileName) {
	this.browser.takeSnapshotWarning(fileName);
}

/**
 * Return the general timeout used on the page.
 *
 * @return The timeout as an <code>int</code>
 */
public int timeout() {
	return this.timeout;
}

/**
 * Return the tiny timeout used on the page.
 *
 * @return The timeout as an <code>int</code>
 */
public int tinyTimeout() {
	return this.tinyTimeout;
}

@Override
public String toString() {
	return "Web page at location '"+this.location+"' ("+getApplication()+", url: "+getUrl()+")";
}

/**
 * Type a password into the given input web element.
 * <p>
 * Note that:
 * <ul>
 * <li>if will fail if the input field does not turn enabled before {@link #timeout()}
 * seconds</li>
 * <li>the input element will be cleared prior entering the given text</li>
 * </ul>
 * </p><p>
 * Note also that a {@link Keys#TAB} is hit after having entered the text in the
 * input field in order to trigger the 'keyEvent' and makes the javascript associated
 * with the filed working properly.
 * </p>
 * @param element The input web element in the current page
 * @param user User whom password has to be typed
 *
 * @see Browser#typePassword(BrowserElement, int, IUser)
 * @since 6.0
 */
public void typePassword(final BrowserElement element, final IUser user) {
	this.browser.typePassword(element, timeout(), user);
}

/**
 * Type a text into an input web element found relatively to a parent web element
 * using respective given mechanisms.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the input field is not found before {@link #timeout()} seconds</li>
 * <li>if will fail if the input field does not turn enabled before {@link #timeout()}
 * seconds</li>
 * <li>the input element will be cleared prior entering the given text</li>
 * </ul>
 * </p><p>
 * Note also that a {@link Keys#TAB} is hit after having entered the text in the
 * input field in order to trigger the 'keyEvent' and makes the javascript associated
 * with the filed working properly.
 * </p>
 * @param parentBy The mechanism to find the parent element in the current
 * page, if <code>null</code>, the element will be searched in the entire page
 * content
 * @param findBy The mechanism to find the element in the current page or
 * from the given parent element if not <code>null</code>
 * @param text The text to type in the input element
 * @return The text web element (as a {@link BrowserElement}) found
 * in the page
 *
 * @see #waitForElement(By, By)
 * @see Browser#typeText(BrowserElement, String, Keys, boolean, int)
 */
public BrowserElement typeText(final By parentBy, final By findBy, final String text) {
	BrowserElement element = waitForElement(parentBy, findBy);
	this.browser.typeText(element, text, Keys.TAB, true/*clear*/, timeout());
	return element;
}

/**
 * Type a text into an input web element found relatively to a parent web element
 * using respective given mechanisms.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the input field is not found before {@link #timeout()} seconds</li>
 * <li>if will fail if the input field does not turn enabled before {@link #timeout()}
 * seconds</li>
 * </ul>
 * </p><p>
 * Note also that a {@link Keys#TAB} is hit after having entered the text in the
 * input field in order to trigger the 'keyEvent' and makes the javascript associated
 * with the filed working properly.
 * </p>
 * @param parentBy The mechanism to find the parent element in the current
 * page, if <code>null</code>, the element will be searched in the entire page
 * content
 * @param findBy The mechanism to find the element in the current page or
 * from the given parent element if not <code>null</code>
 * @param text The text to type in the input element
 * @param clear Tells whether the text needs to be cleared to type in the input element
 * @return The text web element (as a {@link BrowserElement}) found
 * in the page
 *
 * @see #waitForElement(By, By)
 * @see Browser#typeText(BrowserElement, String, Keys, boolean, int)
 */
public BrowserElement typeText(final By parentBy, final By findBy, final String text, final boolean clear) {
	BrowserElement element = waitForElement(parentBy, findBy);
	this.browser.typeText(element, text, Keys.TAB, clear, timeout());
	return element;
}

/**
 * Type a text into an input web element found in the current page using the
 * given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the input field is not found before {@link #timeout()} seconds</li>
 * <li>if will fail if the input field does not turn enabled before {@link #timeout()}
 * seconds</li>
 * <li>the input element will be cleared prior entering the given text</li>
 * </ul>
 * </p><p>
 * Note also that a {@link Keys#TAB} is hit after having entered the text in the
 * input field in order to trigger the 'keyEvent' and makes the javascript associated
 * with the filed working properly.
 * </p>
 * @param findBy The mechanism to find the input web element in the current
 * page
 * @param text The text to type in the input element
 * @return The text web element (as a {@link BrowserElement}) found
 * in the page
 *
 * @see #waitForElement(By)
 * @see Browser#typeText(BrowserElement, String, Keys, boolean, int)
 */
public BrowserElement typeText(final By findBy, final String text) {
	BrowserElement element = waitForElement(findBy);
	this.browser.typeText(element, text, Keys.TAB, true/*clear*/, timeout());
	return element;
}

/**
 * Type a text into an input web element found in the current page using the
 * given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the input field is not found before {@link #timeout()} seconds</li>
 * <li>if will fail if the input field does not turn enabled before {@link #timeout()}
 * seconds</li>
 * </ul>
 * </p><p>
 * Note also that a {@link Keys#TAB} is hit after having entered the text in the
 * input field in order to trigger the 'keyEvent' and makes the javascript associated
 * with the filed working properly.
 * </p>
 * @param findBy The mechanism to find the input web element in the current
 * page
 * @param text The text to type in the input element
 * @param clear Tells whether the text needs to be cleared to type in the input element
 * @return The text web element (as a {@link BrowserElement}) found
 * in the page
 *
 * @see #waitForElement(By)
 * @see Browser#typeText(BrowserElement, String, Keys, boolean, int)
 */
public BrowserElement typeText(final By findBy, final String text, final boolean clear) {
	BrowserElement element = waitForElement(findBy);
	this.browser.typeText(element, text, Keys.TAB, clear, timeout());
	return element;
}

/**
 * Type a text into an input web element found in the current page using the
 * given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the input field is not found before {@link #timeout()} seconds</li>
 * <li>if will fail if the input field does not turn enabled before {@link #timeout()}
 * seconds</li>
 * <li>the input element will be cleared prior entering the given text</li>
 * </ul>
 * </p>
 * @param findBy The mechanism to find the input web element in the current
 * page
 * @param text The text to type in the input element
 * @param key The key to hit after having entered the text in the input field.
 * If <code>null</code> is provided as the value of this parameter, a key will not
 * be hit after having entered the text in the input field.
 * @return The text web element (as a {@link BrowserElement}) found
 * in the page
 *
 * @see #waitForElement(By)
 * @see Browser#typeText(BrowserElement, String, Keys, boolean, int)
 */
public BrowserElement typeText(final By findBy, final String text, final Keys key) {
	BrowserElement element = waitForElement(findBy);
	this.browser.typeText(element, text, key, true/*clear*/, timeout());
	return element;
}

/**
 * Type a text into an input web element found inside the given parent web element
 * using the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the input field is not found before {@link #timeout()} seconds</li>
 * <li>if will fail if the input field does not turn enabled before {@link #timeout()}
 * seconds</li>
 * <li>the input element will be cleared prior entering the given text</li>
 * </ul>
 * </p><p>
 * Note also that a {@link Keys#TAB} is hit after having entered the text in the
 * input field in order to trigger the 'keyEvent' and makes the javascript associated
 * with the filed working properly.
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param findBy The mechanism to find the input web element in the current
 * page
 * @param text The text to type in the input element
 * @return The text web element (as a {@link BrowserElement}) found
 * in the page
 *
 * @see #waitForElement(BrowserElement, By)
 * @see Browser#typeText(BrowserElement, String, Keys, boolean, int)
 */
public BrowserElement typeText(final BrowserElement parentElement, final By findBy, final String text) {
	BrowserElement element = waitForElement(parentElement, findBy);
	this.browser.typeText(element, text, Keys.TAB, true/*clear*/, timeout());
	return element;
}

/**
 * Type a text into the given input web element found.
 * <p>
 * Note that:
 * <ul>
 * <li>if will fail if the input field does not turn enabled before {@link #timeout()}
 * seconds</li>
 * <li>the input element will be cleared prior entering the given text</li>
 * </ul>
 * </p><p>
 * Note also that a {@link Keys#TAB} is hit after having entered the text in the
 * input field in order to trigger the 'keyEvent' and makes the javascript associated
 * with the filed working properly.
 * </p>
 * @param inputElement The web element to enter the text in
 * @param text The text to type in the input element
 *
 * @see Browser#typeText(BrowserElement, String, Keys, boolean, int)
 */
public void typeText(final BrowserElement inputElement, final String text) {
	this.browser.typeText(inputElement, text, Keys.TAB, true/*clear*/, timeout());
}

/**
 * Type a text into the given input web element found.
 * <p>
 * Note that:
 * <ul>
 * <li>if will fail if the input field does not turn enabled before {@link #timeout()}
 * seconds</li>
 * <li>the input element will be cleared prior entering the given text</li>
 * </ul>
 * </p><p>
 * Note also that a {@link Keys#TAB} is hit after having entered the text in the
 * input field in order to trigger the 'keyEvent' and makes the javascript associated
 * with the filed working properly.
 * </p>
 * @param inputElement The web element to enter the text in
 * @param text The text to type in the input element
 * @param key The key to hit after having entered the text in the input field.
 * If <code>null</code> is provided as the value of this parameter, a key will not
 * be hit after having entered the text in the input field.
 *
 * @see Browser#typeText(BrowserElement, String, Keys, boolean, int)
 */
public void typeText(final BrowserElement inputElement, final String text, final Keys key) {
	this.browser.typeText(inputElement, text, key, true/*clear*/, timeout());
}

/**
 * Verify that page user matches the expected one.
 * <p>
 * Check that user name displayed in the User Profile matches the current page user name.
 * </p>
 * <p>
 * Note that no error is raised when an inconsistency is first detected. Instead, a log in operation
 * with the page user is done to synchronize the page and the browser. However, a {@link ScenarioFailedError}
 * is eventually raised if the verification fails {@link #MAX_RECOVERY_TRIES} times.
 * </p>
 * @throws ScenarioFailedError if verification fails {@link #MAX_RECOVERY_TRIES} times
 */
protected void verifyPageUser() throws ScenarioFailedError {
	if (DEBUG) debugPrintln("		+ Verify page user "+getUser());

	// Check whether the current page is on the expected user or not
	BrowserElement loggedUserElement = getLoggedUserElement(false/*fail*/, timeout());
	if (loggedUserElement != null && !matchDisplayedUser(getUser(), loggedUserElement)) {
		this.browser.takeSnapshotInfo("VerifyPageUser");
		println("INFO: User name '"+loggedUserElement.getText()+"' does not match expected one: '"+getUser().getName()+"'");
		println("     Workaround this issue by forcing a login with "+getUser());

		// It may be the case that a re-try will let us login properly.  However, if there is a disconnect
		// between the server's info (name/id) and the properties files, we'll be stuck in a loop
		// between get(), verifyPageUser(), login().  To avoid that, only try MAX_RECOVERY_TRIES
		// to login/get/verify.
		if (this.verifyTries++ >= MAX_RECOVERY_TRIES) {
			throw new ScenarioFailedError("User with id '" + getUser().getId() + "' is not going to be able to login because their name '"
					+ getUser().getName() + "' does not match the server value of '" + loggedUserElement.getText() + "'.");
		}

		// Otherwise, try logging in again
		login(getUser(), true);
	} else {
		if (DEBUG) debugPrintln("		  -> OK");
	}
}

/**
 * Wait until have found the web element using the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if:
 * <ol>
 * <li>the element is not found before {@link #timeout()} seconds</li>
 * <li>there's more than one element found</li>
 * </ol></li>
 * <li>hidden element will be ignored</li>
 * </ul>
 * </p>
 * @param locator The locator to find the element in the current page.
 * @return The web element as {@link BrowserElement}
 * @throws ScenarioFailedError if no element was found before the timeout.
 *
 * @see Browser#waitForElement(BrowserElement, By, boolean, int, boolean, boolean)
 */
public BrowserElement waitForElement(final By locator) {
	return this.browser.waitForElement(null, locator, true/* fail */, timeout(), true/* displayed */, true/* single */);
}

/**
 * Wait until have found the web element using the given mechanism.
 * <p>
 * Note that it will fail if:
 * <ol>
 * <li>the element is not found before {@link #timeout()} seconds</li>
 * <li>there's more than one element found</li>
 * </ol>
 * </p>
 * @param locator The locator to find the element in the current page.
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @return The web element as {@link BrowserElement}
 * @throws ScenarioFailedError if no element was found before the timeout.
 *
 * @see Browser#waitForElement(BrowserElement, By, boolean, int, boolean, boolean)
 */
public BrowserElement waitForElement(final By locator, final boolean displayed) {
	return this.browser.waitForElement(null, locator, true/*fail*/, timeout(), displayed, true/*single element expected*/);
}

/**
 * Wait until have found the web element using the given mechanism.
 * <p>
 * Note that it will fail if the element is not found before {@link #timeout()}
 * seconds
 * </p>
 * @param locator The locator to find the element in the current page.
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @param single Tells whether a single element is expected
 * @return The web element as {@link BrowserElement}
 * @throws ScenarioFailedError if no element was found before the timeout.
 *
 * @see Browser#waitForElement(BrowserElement, By, boolean, int, boolean, boolean)
 */
public BrowserElement waitForElement(final By locator, final boolean displayed, final boolean single) {
	return this.browser.waitForElement(null, locator, true/*fail*/, timeout(), displayed, single);
}

/**
 * Wait until have found the web element using the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if:
 * <ol>
 * <li>the element is not found before {@link #timeout()} seconds and asked to fail</li>
 * <li>there's more than one element found</li>
 * </ol></li>
 * <li>hidden element will be ignored</li>
 * </ul>
 * </p>
 * @param locator The locator to find the element in the current page.
 * @param fail Tells whether to fail if the element is not found before timeout
 * @param time_out The time to wait before giving up the research
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail
 * @throws ScenarioFailedError if no element was found before the timeout and
 * asked to fail
 *
 * @see Browser#waitForElement(BrowserElement, By, boolean, int, boolean, boolean)
 */
public BrowserElement waitForElement(final By locator, final boolean fail, final int time_out) {
	return this.browser.waitForElement(null, locator, fail, time_out, true /* displayed */, true /* single */);
}

/**
 * Wait until have found the web element using the given mechanism.
 * <p>
 * Note that it will fail if there's more than one element found.
 * </p>
 * @param locator The locator to find the element in the current page.
 * @param fail Tells whether to fail if none of the elements is find before timeout
 * @param time_out The time to wait before giving up the research
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail
 * @throws ScenarioFailedError if no element was found before the timeout and
 * asked to fail
 *
 * @see Browser#waitForElement(BrowserElement, By, boolean, int, boolean, boolean)
 */
public BrowserElement waitForElement(final By locator, final boolean fail, final int time_out, final boolean displayed) {
	return this.browser.waitForElement(null, locator, fail, time_out, displayed, true/*single element expected*/);
}

/**
 * Wait until have found the web element using the given mechanism.
 *
 * @param locator The locator to find the element in the current page.
 * @param fail Tells whether to fail if none of the elements is find before timeout
 * @param time_out The time to wait before giving up the research
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @param single Tells whether a single element is expected
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail
 * @throws ScenarioFailedError if no element was found before the timeout and
 * asked to fail
 *
 * @see Browser#waitForElement(BrowserElement, By, boolean, int, boolean, boolean)
 */
public BrowserElement waitForElement(final By locator, final boolean fail, final int time_out, final boolean displayed, final boolean single) {
	return this.browser.waitForElement(null, locator, fail, time_out, displayed, single);
}

/**
 * Wait until have found the web element relatively to a parent element using
 * the respective given mechanisms.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if:
 * <ol>
 * <li>the element is not found before {@link #timeout()} seconds</li>
 * <li>there's more than one element found</li>
 * </ol></li>
 * <li>hidden element will be ignored</li>
 * </p>
 * @param parentLocator The locator to find the parent element in the current
 * page, if <code>null</code>, the element will be searched in the entire page
 * content
 * @param locator The locator to find the element in the current page or
 * from the given parent element if not <code>null</code>
 * @return The web element as {@link BrowserElement}
 * @throws ScenarioFailedError if no element was found before the {@link #timeout()}.
 *
 * @see #waitForElement(By, By, boolean, int)
 */
public BrowserElement waitForElement(final By parentLocator, final By locator) {
	return waitForElement(parentLocator, locator, true/*fail*/, timeout());
}

/**
 * Wait until have found the web element relatively to a parent element using
 * the respective given mechanisms.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if there's more than one element found</li>
 * <li>hidden element will be ignored</li>
 * </p>
 * @param parentLocator The locator to find the parent element in the current
 * page, if <code>null</code>, the element will be searched in the entire page
 * content
 * @param locator The locator to find the element in the current page or
 * from the given parent element if not <code>null</code>
 * @param fail Tells whether to fail if none of the elements is find before timeout
 * @param time_out The time to wait before giving up the research
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail
 * @throws ScenarioFailedError if no element was found before the timeout and
 * asked to fail
 *
 * @see Browser#waitForElement(BrowserElement, By, boolean, int, boolean, boolean)
 */
public BrowserElement waitForElement(final By parentLocator, final By locator, final boolean fail, final int time_out) {
	BrowserElement parentElement = parentLocator == null ? null : waitForElement(parentLocator);
	return this.browser.waitForElement(parentElement, locator, fail, time_out, true/*visible*/, true/*single element expected*/);
}

/**
 * Wait until have found the web element using the given mechanism relatively
 * to the given parent element.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if:
 * <ol>
 * <li>the element is not found before {@link #timeout()} seconds</li>
 * <li>there's more than one element found</li>
 * </ol></li>
 * <li>hidden element will be ignored</li>
 * </ul>
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param locator The locator to find the element in the current page.
 * @return The web element as {@link BrowserElement}
 * @throws ScenarioFailedError if no element was found before the timeout.
 *
 * @see Browser#waitForElement(BrowserElement, By, boolean, int, boolean, boolean)
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By locator) {
	return this.browser.waitForElement(parentElement, locator, true/* fail */, timeout(), true/* visible */, true/* single */);
}

/**
 * Wait until have found the web element using the given mechanism relatively
 * to the given parent element.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if:
 * <ol>
 * <li>the element is not found before {@link #timeout()} seconds</li>
 * <li>there's more than one element found</li>
 * </ol></li>
 * <li>hidden element will be ignored</li>
 * </ul>
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param locator The locator to find the element in the current page.
 * @param displayed If <code>true</code> then only displayed element can be returned.
 * If <code>false</code> then the returned element can be either displayed or hidden.
 * @return The web element as {@link BrowserElement}
 * @throws ScenarioFailedError if no element was found before the timeout.
 *
 * @see Browser#waitForElement(BrowserElement, By, boolean, int, boolean, boolean)
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By locator, final boolean displayed) {
	return this.browser.waitForElement(parentElement, locator, true/* fail */, timeout(), displayed, true/* single */);
}

/**
 * Wait until have found the web element using the given mechanism relatively
 * to the given parent element.
 * <p>
 * <ul>
 * Note that:
 * </ul>
 * <li>it will fail if there's more than one element found</li>
 * <li>hidden element will be ignored</li>
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param locator The locator to find the element in the current page.
 * @param fail Tells whether to fail if none of the elements is find before timeout
 * @param time_out The time to wait before giving up the research
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail
 * @throws ScenarioFailedError if no element was found before the timeout and
 * asked to fail
 *
 * @see Browser#waitForElement(BrowserElement, By, boolean, int, boolean, boolean)
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By locator, final boolean fail, final int time_out) {
	return this.browser.waitForElement(parentElement, locator, fail, time_out, true/*displayed*/, true/*single element expected*/);
}

/**
 * Wait until have found some elements (ie. at least one) web elements using
 * the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if:
 * <ol>
 * <li>no element is found before {@link #timeout()} seconds</li>
 * </ol></li>
 * <li>hidden element will be ignored</li>
 * </ul>
 * </p>
 * @param locator The locator to find the element in the current page.
 * @return The web elements list as {@link List} of {@link BrowserElement}
 * @throws ScenarioFailedError if no element was found before the timeout.
 *
 * @see Browser#waitForElements(BrowserElement, By, boolean, int, boolean)
 */
public List<BrowserElement> waitForElements(final By locator) {
	return this.browser.waitForElements(null, locator, true/*fail*/, timeout(), true/*visible*/);
}

/**
 * Wait until have found some elements (ie. at least one) web elements using
 * the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>hidden element will be ignored</li>
 * </p>
 * @param locator The locator to find the element in the current page.
 * @param fail True if this should fail if the element is not found.
 * @return The web elements list as {@link List} of {@link BrowserElement}.
 * Might be empty if no element was found before the timeout and asked not to fail
 * @throws ScenarioFailedError if no element was found before the timeout and
 * asked to fail
 *
 * @see Browser#waitForElements(BrowserElement, By, boolean, int, boolean)
 */
public List<BrowserElement> waitForElements(final By locator, final boolean fail) {
	return this.browser.waitForElements(null, locator, fail, timeout(), true/*visible*/);
}

/**
 * Wait until have found some elements (ie. at least one) web elements using
 * the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>hidden element will be ignored</li>
 * </p>
 * @param locator The locator to find the element in the current page.
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param time_out The time to wait before giving up the research
 * @return The web elements list as {@link List} of {@link BrowserElement}.
 * Might be empty if no element was found before the timeout and asked not to fail
 * @throws ScenarioFailedError if no element was found before the timeout and
 * asked to fail
 *
 * @see Browser#waitForElements(BrowserElement, By, boolean, int, boolean)
 */
public List<BrowserElement> waitForElements(final By locator, final boolean fail, final int time_out) {
	return this.browser.waitForElements(null, locator, fail, time_out, true/*visible*/);
}

/**
 * Wait until have found some elements (ie. at least one) web elements using
 * the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>hidden element will be ignored</li>
 * </p>
 * @param locator The locator to find the element in the current page.
 * @param time_out The time to wait before giving up the research
 * @return A non-empty web elements list as {@link List} of {@link BrowserElement}.
 * @throws ScenarioFailedError if no element was found before the timeout.
 *
 * @see Browser#waitForElements(BrowserElement, By, boolean, int, boolean)
 */
public List<BrowserElement> waitForElements(final By locator, final int time_out) {
	return this.browser.waitForElements(null, locator, true/*fail*/, time_out, true/*visible*/);
}

/**
 * Wait until have found some elements (ie. at least one) web elements using
 * the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>hidden element will be ignored</li>
 * </p>
 * @param locator The locator to find the element in the current page.
 * @param time_out The time to wait before giving up the research
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @return A non-empty web elements list as {@link List} of {@link BrowserElement}.
 * @throws ScenarioFailedError if no element was found before the timeout.
 *
 * @see Browser#waitForElements(BrowserElement, By, boolean, int, boolean)
 */
public List<BrowserElement> waitForElements(final By locator, final int time_out, final boolean displayed) {
	return this.browser.waitForElements(null, locator, true/*fail*/, time_out, displayed);
}

/**
 * Wait until have found some elements (ie. at least one) web elements using
 * the given mechanism.
 * <p>
 * Note that:
 * <ul>
 * <li>hidden element will be ignored</li>
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param locator The locator to find the element in the current page.
 * @return A non-empty web elements list as {@link List} of {@link BrowserElement}.
 * @throws ScenarioFailedError if no element was found before the timeout.
 *
 * @see Browser#waitForElements(BrowserElement, By, boolean, int, boolean)
 */
public List<BrowserElement> waitForElements(final BrowserElement parentElement, final By locator) {
	return this.browser.waitForElements(parentElement, locator, true/*fail*/, timeout(), true/*visible*/);
}

/**
 * Wait for the expected title to appear.
 *
 * @throws ScenarioFailedError if the current page title does not match the expected one.
 */
protected void waitForExpectedTitle() {
	if (isTitleExpected()) {
		long timeoutMillis = openTimeout() * 1000 + System.currentTimeMillis();
		if (!matchTitle()) {
			debugPrintln("		+ Wait for expected title '"+getExpectedTitle()+"' (current is '"+getTitle()+"')");
			while (!matchTitle()) {
				if (System.currentTimeMillis() > timeoutMillis) {
					throw new IncorrectTitleError("Current page title '" + getTitle() + "' does not match the expected one: '" + getExpectedTitle() + "' before timeout '" + openTimeout() + "' seconds");
				}
			}
		}
	}
}

/**
 * Wait for the page loading to be finished.
 * <p>
 * The default behavior is to wait for the status message to be triggered (ie.
 * waiting the message to appear, then waiting the message to vanish).
 * </p><p>
 * In case the message appearance was missed at the beginning, then it
 * automatically give up after {@link #shortTimeout()} seconds. No error is
 * raised in such a case, it just hopes that while waiting for the message which
 * never comes, the page had enough time to be completely loaded...
 * </p>
 */
protected void waitForLoadingPageEnd() {
	if (DEBUG) debugPrintln("		+ Waiting for loading page end");

	waitWhileBusy();

	long waitTimeout = openTimeout() * 1000 + System.currentTimeMillis();	 // Timeout currentTimeMilliseconds
	while (!isLoaded()) {
		if (System.currentTimeMillis() > waitTimeout) {
			this.browser.takeSnapshotWarning("LoadTimeout_"+getClassSimpleName(getClass()));
			println("WARNING: Page " + this +" did not load in " + openTimeout() + " seconds!");
			println("	- browser URL: "+this.browser.getCurrentUrl().replaceAll("%20", SPACE_STRING));
			println("	- location: "+this.location);
			println("	- page URL: "+getTopology().getPageUrl(this.location).replaceAll("%20", SPACE_STRING));
			println("	- stack trace: ");
			printStackTrace(2);
			println();
			break;
		}
	}
}

/**
 * Wait until have found at least one of the elements using the given mechanisms.
 * <p>
 * Note that:
 * <ul>
 * <li>hidden element will be ignored</li>
 * </ul>
 * </p>
 * @param findBys The mechanisms to find the element in the current page.
 * @param fail Tells whether to fail if none of the elements is find before timeout
 * @param time_out The time to wait before giving up the research
 * @return The array of web elements as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail
 * @throws ScenarioFailedError if no element was found before the timeout and
 * asked to fail
 *
 * @see Browser#waitForMultipleElements(BrowserElement, By[], boolean, int)
 * to have more details on how the returned array is filled with found elements
 */
public BrowserElement[] waitForMultipleElements(final boolean fail, final int time_out, final By... findBys) {
	return this.browser.waitForMultipleElements(null, findBys, fail, time_out);
}

/**
 * Wait until have found at least one of the elements using the given mechanisms.
 * <p>
 * Note that:
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * <li>hidden element will be ignored</li>
 * <ul>
 * </p>
 * @param findBys The mechanisms to find the element in the current page.
 * @return The array of web elements as {@link BrowserElement}
 * @throws ScenarioFailedError if no element was found before the timeout
 *
 * @see Browser#waitForMultipleElements(BrowserElement, By[], boolean, int)
 * to have more details on how the returned array is filled with found elements
 */
public BrowserElement[] waitForMultipleElements(final By... findBys) {
	return this.browser.waitForMultipleElements(null, findBys, true/*fail*/, timeout());
}

/**
 * Wait until have found at least one of the elements relatively to the given
 * parent element using the given mechanisms.
 * <p>
 * Note that:
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * <li>hidden element will be ignored</li>
 * <ul>
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param findBys The mechanisms to find the element in the current page.
 * @return The array of web elements as {@link BrowserElement}
 * @throws ScenarioFailedError if no element was found before the timeout
 *
 * @see Browser#waitForMultipleElements(BrowserElement, By[], boolean, int)
 * to have more details on how the returned array is filled with found elements
 */
public BrowserElement[] waitForMultipleElements(final BrowserElement parentElement, final By... findBys) {
	return this.browser.waitForMultipleElements(parentElement, findBys, true/*fail*/, timeout());
}

private void waitForReadyState() {
	long timeoutMillis = openTimeout() * 1000 + System.currentTimeMillis();
	while (!this.browser.executeScript("return document.readyState").equals("complete")) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new PageBuysTimeoutError("Document did not turn ready state before timeout '" + openTimeout() + "s'");
		}
	}
}

/**
 * Wait until have got one of the expected texts on of the given element.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if the element is not found before {@link #timeout()} seconds</li>
 * </ul>
 * </p>
 * @param element The text element to be read
 * @param fail Tells whether to fail if none of the elements is find before timeout
 * @param time_out The time to wait before giving up the research
 * @param texts The expected texts
 * @return One of the expected text as {@link String} or <code>null</code>
 * if element text never matches one before the timeout and asked not to fail
 * @throws ScenarioFailedError if element text never matches an expected ones
 * before the timeout and asked to fail
 *
 * @see Browser#waitForText(BrowserElement, boolean, int, String...)
 */
public String waitForText(final BrowserElement element, final boolean fail, final int time_out, final String... texts) {
	return this.browser.waitForText(element, fail, time_out, texts);
}

/**
 * Wait until have got one of the expected texts on of the given element.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if none of the text is found before {@link #timeout()} seconds</li>
 * </ul>
 * </p>
 * @param pattern The comparison pattern to find the expected texts.
 * @param texts The expected texts
 * @return One of the expected text as {@link String} or <code>null</code>
 * if element text never matches one before the timeout and asked not to fail
 * @throws ScenarioFailedError if element text never matches an expected ones
 * before the timeout and asked to fail
 *
 * @see Browser#waitForText(BrowserElement, boolean, int, String...)
 */
public BrowserElement waitForTextPresent(final ComparisonPattern pattern, final String... texts) {
	return this.browser.waitForTextPresent(null, true/*fail*/, timeout(), true/*displayed*/, false/*multiple*/, pattern, texts);
}

/**
 * Wait until have got one of the expected texts on of the given element.
 * <p>
 * Note that:
 * <ul>
 * <li>it will fail if none of the text is found before {@link #timeout()} seconds</li>
 * </ul>
 * </p>
 * @param texts The expected texts
 * @return One of the expected text as {@link String} or <code>null</code>
 * if element text never matches one before the timeout and asked not to fail
 * @throws ScenarioFailedError if element text never matches an expected ones
 * before the timeout and asked to fail
 *
 * @see Browser#waitForText(BrowserElement, boolean, int, String...)
 */
public BrowserElement waitForTextPresent(final String... texts) {
	return this.browser.waitForTextPresent(null, true/*fail*/, timeout(), true/*displayed*/, false/*first occurrence*/, ComparisonPattern.STARTS_WITH, texts);
}

/**
 * Wait for the title to be displayed in the current web page.
 * <p>
 * Note that subclasses which do not expect a title in their page should have
 * the {@link #getExpectedTitle()} method to return <code>null</code>.
 * </p><p>
 * Otherwise, subclasses which are expecting a title have to override this method
 * to avoid having a {@link ScenarioFailedError} error thrown.
 * </p><p>
 * If the wait is not supposed to fail, then it just lasts one second to avoid
 * wasting too much time when loading is needed.
 * </p>
 * @param fail Tells whether callers allow the title not to be found
 * @return The found title or <code>null</code> if not found and it was allowed.
 * @throws ScenarioFailedError If the title is not found and that was not allowed
 * @throw ScenarioMissingImplementationError If a subclass expects a title
 * and does not override this method.
 */
protected BrowserElement waitForTitle(final boolean fail) {
	if (isTitleExpected()) {
		try {
			return waitForElement(getTitleElementLocator(), fail, fail ? timeout() : tinyTimeout());
		}
		catch (WaitElementTimeoutError e) {
			throw new IncorrectTitleError(e);
		}
	}
	return null;
}

/**
 * Wait for the page initial load.
 *
 * @throws ServerMessageError If any server error message occurs while waiting
 * for the page to be initially loaded.
 */
protected void waitInitialPageLoading() throws ServerMessageError {
	waitInitialPageLoading(true/*throwError*/);
}

/**
 * Wait for the page initial load.
 * <p>
 * Wait for the root element having at least one child.
 * </p>
 * @param throwError Tells whether a {@link ServerMessageError} has to
 * be thrown if a server error is detected during the load operation.
 * @throws ServerMessageError If any server error message occurs while waiting
 * for the page to be initially loaded and it has been told to throw the error
 */
protected void waitInitialPageLoading(final boolean throwError) {
	waitForReadyState();

	// Get root web element
	BrowserElement rootElement = getRootElement();

	// Wait until something is displayed under root
	long time_out = openTimeout() * 1000 + System.currentTimeMillis();
	while (rootElement.getChildren().size() == 0) {
		if (System.currentTimeMillis() > time_out) {
			throw new WaitElementTimeoutError("Initial page loading never finish.");
		}
	}
}

/**
 * Wait default timeout while the page is busy.
 * <p>
 * By default the page is busy if the status message is displayed and the timeout
 * is {@link #openTimeout()} seconds.
 * </p>
 * @throws ScenarioFailedError If the timeout is reached while the page is still
 * busy.
 */
public void waitWhileBusy() {
	waitWhileBusy(openTimeout());
}

/**
 * Wait given timeout while the page is busy.
 * <p>
 * By default the page is busy if the status message is displayed.
 * </p>
 * @param busyTimeout The number of seconds to wait while the page is busy
 * @throws WaitElementTimeoutError If the timeout is reached while the page is still
 * busy.
 */
public void waitWhileBusy(final int busyTimeout) {
	long timeoutInMillis = busyTimeout * 1000 + System.currentTimeMillis();

	while (getBusyIndicatorElements() != null) {
		if (System.currentTimeMillis() > timeoutInMillis) {
			throw new PageBuysTimeoutError("Page was undergoing an operation which did not finish before timeout '" + busyTimeout + "s'");
		}
	}
}

/**
 * Execute a workaround to avoid raising the given {@link ScenarioFailedError}
 * exception.
 * <p>
 * The default workaround is to refresh the page. Of course subclass might
 * either add some other actions or even replace it by more typical actions.
 * </p>
 * @param message The exception message
 * @throws ScenarioFailedError with the given message if no workaround is
 * possible (typically, when too many tries have been done...)
 */
public void workaround(final String message) {
	PageWorkaround workaround = new PageWorkaround(this, message);
	workaround.execute();
}
}