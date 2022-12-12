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
package com.ibm.itest.cloud.common.pages.elements;

import static com.ibm.itest.cloud.common.config.Timeouts.DEFAULT_TIMEOUT;
import static com.ibm.itest.cloud.common.performance.PerfManager.PERFORMANCE_ENABLED;
import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.*;
import static com.ibm.itest.cloud.common.utils.ByUtils.fixLocator;
import static com.ibm.itest.cloud.common.utils.ByUtils.getLocatorString;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.By.*;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.remote.UnreachableBrowserException;

import com.ibm.itest.cloud.common.browsers.Browser;
import com.ibm.itest.cloud.common.config.*;
import com.ibm.itest.cloud.common.pages.frames.BrowserFrame;
import com.ibm.itest.cloud.common.scenario.ScenarioUtils;
import com.ibm.itest.cloud.common.scenario.errors.*;

/**
 * A web browser element found in a {@link Browser} page content.
 * <p>
 * This class implements the {@link WebElement} interface to be as most
 * compatible as possible with Selenium behavior.
 * </p><p>
 * This object is instantiated while finding element through {@link SearchContext}
 * interface. As {@link Browser} and {@link BrowserElement} implement
 * this interface, they only produce this kind of object when finding element in
 * the current web page content.
 * </p><p>
 * The main functionality of this specific web element is to be able to self recover
 * when a {@link StaleElementReferenceException} occurs while trying to execute
 * any of the {@link WebElement} interface operations.
 * </p><p>
 * The recovery uses the stored {@link SearchContext context} from which the
 * initial {@link WebElement} has been found and the mechanism to find it (ie.
 * {@link By}). When an exception occurs, it's caught and the element is
 * searched again (ie. {@link SearchContext#findElement(By)} or
 * {@link SearchContext#findElements(By)}).
 * </p><p>
 * This recovery is retried several times before given up if maximum of retries
 * ({@link #MAX_RECOVERY_ATTEMPTS}) is reached.
 * </p><p>
 * When searching the web element for the first time, the browser, the frame and
 * the index of the elements in the parent's list are also stored to have the
 * precise context used for the initial research and then be sure to find the same
 * element when recovering.
 * </p><p>
 * Additionally to the WebElement methods, this class also provide some useful
 * functionalities as:
 * <ul>
 * <li>{@link #getAncestor(int)}: Return the ancestor of the current element.</li>
 * <li>{@link #getParent()}: Return the parent of the current element.</li>
 * </ul>
 */
public class BrowserElement implements WebElement, Locatable {

	/* Locators */
	private static final By CHILDREN_LOCATOR = By.xpath("./child::*");

	/* Javascripts */
	private final static String MOUSE_OVER_JAVASCRIPT =
		"var forceHoverEvent = document.createEvent('MouseEvents');" +
		"forceHoverEvent.initEvent( 'mouseover', true, false );" +
		"arguments[0].dispatchEvent(forceHoverEvent);";

	/**
	 * The maximum of attempts when recovering the current web element.
	 */
	public static final int MAX_RECOVERY_ATTEMPTS = 5;

/**
 * Return a list of {@link BrowserElement} assuming the given list *is* a
 * list of this kind of {@link WebElement}.
 *
 * @param elements The list of {@link WebElement}.
 * @return The list of {@link BrowserElement}.
 * @throws IllegalArgumentException If one of the element of the given list is
 * not a {@link BrowserElement}.
 */
public static List<BrowserElement> getList(final List<WebElement> elements) {
	List<BrowserElement> webElements = new ArrayList<BrowserElement>(elements.size());
	for (WebElement element: elements) {
		try {
			webElements.add((BrowserElement)element);
		}
		catch (ClassCastException cce) {
			throw new IllegalArgumentException("The given list was not a list of WebBrowserElement: "+cce.getMessage());
		}
	}
	return webElements;
}

	/**
	 * The browser to use to search the web element.
	 */
	final private Browser browser;

	/**
	 * The mechanism to use to search the web element.
	 */
	final private By by;

	/**
	 * The context to use to search the web element.
	 * <p>
	 * If the search is expected to be done in the entire web document, then
	 * it will be a {@link WebDriver} object, otherwise, ie. if the search is expected
	 * to be done relatively to another web element, then it will be a
	 * {@link BrowserElement}.
	 * </p>
	 */
	final private SearchContext context;

	/**
	 * The wrapped selenium web element.
	 */
	WebElement webElement;

	/**
	 * The frame used when searching the current web element.
	 */
	private BrowserFrame frame;

/**
 * Information of parent when the current web element has been found
 * among several other elements.
 * <p>
 * These information allow recovering to be more precise, hence be sure not
 * to recover another element.
 * </p>
 */
final private int parentListSize, parentListIndex;

/**
 * Create a web browser element using the given locator.
 * <p>
 * The search of the corresponding {@link WebElement} is done through the entire
 * browser page. The browser is stored to allow recovery.
 * </p><p>
 * Note that this constructor is typically used when search for a single element.
 * </p>
 * @param browser The browser where web element is displayed.
 * @param by The mechanism to use to search for the element
 */
protected BrowserElement(final Browser browser, final By by) {
	this(browser, browser.getCurrentFrame(), browser.getDriver(), by, null, 0, -1);
}

/**
 * Create a web browser element using the given search mechanism in the given
 * search context.
 * <p>
 * The browser is stored to allow recovery.
 * </p><p>
 * Note that this constructor is typically used when search for a single element.
 * </p>
 * @param browser The browser where web element is displayed.
 * @param context The context to search for the element
 * @param by The mechanism to use to search for the element
 */
public BrowserElement(final Browser browser, final SearchContext context, final By by) {
	this(browser, browser.getCurrentFrame(), context, by, null, 0, -1);
}

/**
 * Create a web browser element using the given search mechanism in the given
 * search context and frame.
 * <p>
 * The browser is stored to allow recovery.
 * </p><p>
 * Note that this constructor is typically used when search for a single element.
 * </p>
 * @param browser The browser where web element is displayed.
 * @param webFrame The frame in which the element is supposed to be
 * @param context The context to search for the element
 * @param by The mechanism to use to search for the element
 */
public BrowserElement(final Browser browser, final BrowserFrame webFrame, final SearchContext context, final By by) {
	this(browser, webFrame, context, by, null, 0, -1);
}

/**
 * Create a web browser element using the given search mechanism in the given
 * search context.
 * <p>
 * The browser is stored to allow recovery.
 * </p><p>
 * Note that this constructor is typically used when search for a single element.
 * </p>
 * @param browser The browser where web element is displayed.
 * @param webFrame The index of the frame in which the element is supposed
 * to be
 * @param context The context to search for the element
 * @param by The mechanism to use to search for the element
 * @param element The element wrapped by the created instance. If this
 * argument is used, then the search mechanism will be ignored.
 * @param size The size of the parent element children list. This argument is
 * used when searching for several element (see {@link #findElements(By, boolean, boolean)})
 * @param index The index in the parent element children list. This argument is
 * used when searching for several element (see {@link #findElements(By, boolean, boolean)})
 */
public BrowserElement(final Browser browser, final BrowserFrame webFrame, final SearchContext context, final By by, final WebElement element, final int size, final int index) {
	super();
	this.browser = browser;
	this.context = context;
	this.by = by;
	this.frame = webFrame;
	this.parentListSize = size;
	this.parentListIndex = index;
	if (element == null) {
		if (context instanceof BrowserElement) {
			BrowserElement parentElement = (BrowserElement) context;
			this.webElement = parentElement.webElement.findElement(by);
			if (this.frame != null && !this.frame.equals(parentElement.frame) || (this.frame == null && parentElement.frame != null)) {
				throw new ScenarioFailedError("Current frame ("+this.frame+") is different than its parent ("+parentElement.frame+ ")! Web element hierarchy should be in the same frame!");
			}
		} else {
			this.webElement = context.findElement(by);
		}
	} else {
		if (element instanceof BrowserElement) {
			BrowserElement parentElement = (BrowserElement) element;
			this.webElement = parentElement.webElement;
			if (this.frame != null && !this.frame.equals(parentElement.frame) || (this.frame == null && parentElement.frame != null)) {
				throw new ScenarioFailedError("Current frame ("+this.frame+") is different than its parent ("+parentElement.frame+ ")! Web element hierarchy should be in the same frame!");
			}
		} else {
			this.webElement = element;
		}
	}
	if (this.webElement == null) {
		throw new ScenarioFailedError("Web element should not be null!");
	}
	if (this.webElement instanceof BrowserElement) {
		throw new ScenarioFailedError("Web element should not be a WebBrowserElement!");
	}
}

/**
 * Alter the selection status of the element.
 * <p>
 * This operation only applies to input elements such as checkboxes, options in a
 * select and radio buttons. The element status will only be altered if the current
 * status is different from the provided.
 * </p>
 * @param select Specifies whether to select or clear the element. The value <b>true</b>
 * or <b>false</b> implies that the element should be selected or cleared respectively.
 * @return The element that has been altered.
 */
public BrowserElement alter(final boolean select) {
	if ((select && !isSelected()) || (!select && isSelected())) {
		click();
	}
	return this;
}

/*
 * Recovery when an exception has occurred on a web element operation.
 *
 * TODO Change the exception parameter as StaleElementReferenceException
 * as this is the only caught exception now...
 */
private void catchWebDriverException(final WebDriverException wde, final String title, final int count, final boolean recovery) {

	// First use browser exception catching
	this.browser.catchWebDriverException(wde, title, count);

	// Recover the element
	int n = 0;
	while (true) {
		try {
			if (	recover(n) || !recovery) return;
			if (n++ >= MAX_RECOVERY_ATTEMPTS) {
				debugPrintln("Cannot recover even after "+MAX_RECOVERY_ATTEMPTS+" retries... give up");
				return;
			}
		}
		catch (WebDriverException ex) {
			// Give up right now if the exception is too serious
			if (!(ex instanceof StaleElementReferenceException)) {
				debugPrintln("Fatal exception occured when "+title+"'... give up");
		    	debugPrintException(ex);
				throw ex;
			}

			// Give up now if no recovery
			if (!recovery) return;

			// Give up if too many failures occurred
			if (n++ >= MAX_RECOVERY_ATTEMPTS) {
				debugPrintln("More than "+MAX_RECOVERY_ATTEMPTS+" exceptions occured when trying to find again the "+this.by+"'... give up");
		    	debugPrintException(ex);
				throw wde;
			}

			// Workaround
			debugPrint("ScenarioWorkaround exception when trying to find again the "+this.by+"': ");
			debugPrintException(ex);
		}
	}
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public void clear() {
	if (DEBUG) debugPrintln("			(clearing "+this+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				this.webElement.clear();
				// In case, the webElement.clear() method may not work at times, clear the element as a
				// user would via an appropriate key combination based on the OS where the tests are run.
				this.webElement.sendKeys((isMacOs() ? Keys.COMMAND : Keys.CONTROL) + "a");
				this.webElement.sendKeys(Keys.DELETE);

				if (DEBUG) debugPrintln("			 ( -> done.)");
				return;
			}
			catch (WebDriverException wde) {
				catchWebDriverException(wde, "clearing", count++, true);
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public void click() {
	// Start performance timer if necessary
	if (PERFORMANCE_ENABLED) {
		this.browser.getPerfManager().startServerTimer();
	}

	try {
		click(true/*recovery*/);
	}
	catch (ElementNotInteractableException enve) {
		setVisible(true);
		click(true/*recovery*/);
	}
}

/**
 * Perform the {@link WebElement#click()} operation.
 * <p>
 * If recovery is allowed, then catch any {@link WebDriverException} (except
 *  {@link InvalidSelectorException} and {@link UnreachableBrowserException})
 *  and retry the operation until success or {@link #MAX_RECOVERY_ATTEMPTS}
 *  attempts has been made.
 *  </p>
 * @param recovery Tells whether try to recover is a {@link WebDriverException}
 * occurs
 * @see WebElement#click()
 */
public void click(final boolean recovery) {
	if (DEBUG) debugPrintln("			(clicking on "+this+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				this.webElement.click();
				if (DEBUG) debugPrintln("			 ( -> done.)");
				return;
			}
			catch (WebDriverException wde) {
				if (recovery) {
					catchWebDriverException(wde, "clicking", count++, true);
				} else {
					throw wde;
				}
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * Click this element via JavaScript.
 * <p>
 * The element may not be scrolled at times due to various reasons. The JavaScript method may
 * perform the scroll operation properly during the click operation during these situations.
 * </p>
 */
public void clickViaJavaScript() {
	executeScript("click()");
}

/**
 * Double-click on the element;
 */
public void doubleClick() {
	this.browser.doubleClick(this);
}

/**
 * Double-click this element via JavaScript.
 * <p>
 * The element may not be scrolled at times due to various reasons. The JavaScript method may
 * perform the scroll operation properly during the click operation during these situations.
 * </p>
 */
public void doubleClickViaJavaScript() {
	executeScript("dblclick()");
}

/**
 * Enter the given password in current element.
 * <p>
 * Recovery is allowed for this operation which means that any {@link WebDriverException}
 * (except {@link InvalidSelectorException} and {@link UnreachableBrowserException})
 *  is caught and the operation retried until success or {@link #MAX_RECOVERY_ATTEMPTS}
 *  attempts has been made.
 *  </p>
 * @param user User whom password has to be typed in the current element
 * @see #sendKeys(boolean, CharSequence...)
 * @since 6.0
 */
public void enterPassword(final IUser user) {
	sendKeys(true/*recovery*/, true/*password*/, ((User)user).getPassword());
}

@Override
public boolean equals(final Object obj) {
	return (obj instanceof BrowserElement) && this.webElement.equals(((BrowserElement)obj).webElement);
}

/**
 * Execute the given script on the current web element.
 *
 * @param script The script to execute
 * @return One of Boolean, Long, String, List or WebElement. Or null.
 */
public Object executeScript(final String script) {
	return getJavascriptExecutor().executeScript("arguments[0]."+script+";", this.webElement);
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p><p>
 * The search is performed in the current frame.
 * </p>
 * @param findBy The mechanism to use for the search
 * @return The found web element as a {@link BrowserElement}.
 * @throws ScenarioFailedError If the element is not found.
 */
@Override
public BrowserElement findElement(final By findBy) {
	return findElement(findBy, this.frame, true/*recovery*/);
}

/**
 * Perform the {@link WebElement#findElement(By)} operation.
 * <p>
 * If recovery is allowed, then catch any {@link WebDriverException} (except
 *  {@link InvalidSelectorException} and {@link UnreachableBrowserException})
 *  and retry the operation until success or {@link #MAX_RECOVERY_ATTEMPTS}
 *  attempts has been made.
 * </p><p>
 * The search is performed in the current frame.
 *  </p>
 * @param findBy The mechanism to use for the search
 * @param recovery Tells whether try to recover if a {@link WebDriverException} occurs
 * @return The found web element as a {@link BrowserElement}.
 * @throws ScenarioFailedError If the element is not found.
 */
public BrowserElement findElement(final By findBy, final boolean recovery) {
	return findElement(findBy, this.frame, recovery);
}

/**
 * Perform the {@link WebElement#findElement(By)} operation.
 * <p>
 * If recovery is allowed, then catch any {@link WebDriverException} (except
 *  {@link InvalidSelectorException} and {@link UnreachableBrowserException})
 *  and retry the operation until success or {@link #MAX_RECOVERY_ATTEMPTS}
 *  attempts has been made.
 * </p><p>
 * If recovery is not allowed and an exception occurred, then it's still caught
 * but <code>null</code> is returned instead of retrying.
 * </p><p>
 * The search is performed in the current frame.
 *  </p>
 * @param locator The locator to use for the search
 * @param webFrame The frame to use to find for the web element
 * @param recovery Tells whether try to recover if a {@link WebDriverException} occurs
 * @return The found web element as a {@link BrowserElement}.
 * @throws ScenarioFailedError If the element is not found.
 * @see WebElement#findElement(By)
 * TODO Add the ability not to throw {@link ScenarioFailedError} when not found
 * (ie. add <code>fail</code> argument..)
 */
public BrowserElement findElement(final By locator, final BrowserFrame webFrame, final boolean recovery) {
	if (DEBUG) debugPrintln("			(finding element "+locator+" for "+this+" in frame '"+webFrame+"')");

	// Fix locator if necessary
	By fixedLocator = fixLocator(locator);

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		// Find element
		int count = 0;
		while (true) {
			try {
				BrowserElement webPageElement = new BrowserElement(this.browser, webFrame, this, fixedLocator);
				if (DEBUG) debugPrintln("			(  -> found "+webPageElement+")");
				return webPageElement;
			}
			catch (NoSuchElementException nsee) {
				return null;
			}
			catch (UnhandledAlertException uae) {
				this.browser.purgeAlerts("Finding element '"+fixedLocator+"'");
				if (!recovery) {
					return null;
				}
			}
			catch (WebDriverException wde) {
				if (recovery) {
					catchWebDriverException(wde, "finding element '"+fixedLocator+")", count, recovery);
				} else {
					if (DEBUG) debugPrintException(wde);
					return null;
				}
				count++;
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 * @param findBy The mechanism to find the elements in the current page.
 * @return The web elements list as a {@link List} of {@link BrowserElement}
 */
@Override
public List<WebElement> findElements(final By findBy) {
	return findElements(findBy, true/*displayed*/, true/*recovery*/);
}

/**
 * Perform the {@link WebElement#findElements(By)} operation.
 * <p>
 * If recovery is allowed, then catch any {@link WebDriverException} (except
 *  {@link InvalidSelectorException} and {@link UnreachableBrowserException})
 *  and retry the operation until success or {@link #MAX_RECOVERY_ATTEMPTS}
 *  attempts has been made.
 * </p><p>
 * If recovery is not allowed and an exception occurs, then it's still caught
 * but an empty list is returned instead of retrying.
 * </p><p>
 * Note that only displayed elements are added to the returned list.
 *  </p>
 * @param findBy The mechanism to find the elements in the current page.
 * @param recovery Tells whether try to recover is a {@link WebDriverException} occurs
 * @return The web elements list as a {@link List} of {@link BrowserElement}
 */
public List<WebElement> findElements(final By findBy, final boolean recovery) {
	return findElements(findBy, true/*displayed*/, recovery);
}

/**
 * Perform the {@link WebElement#findElements(By)} operation.
 * <p>
 * If recovery is allowed, then catch any {@link WebDriverException} (except
 *  {@link InvalidSelectorException} and {@link UnreachableBrowserException})
 *  and retry the operation until success or {@link #MAX_RECOVERY_ATTEMPTS}
 *  attempts has been made.
 * </p><p>
 * If recovery is not allowed and an exception occurs, then it's still caught
 * but an empty list is returned instead of retrying.
 *  </p>
 * @param locator The locator to find the elements in the current page.
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @param recovery Tells whether try to recover is a {@link WebDriverException} occurs
 * @return The web elements list as a {@link List} of {@link BrowserElement}
 * or an empty list if nothing matches
 * @see WebElement#findElements(By)
 */
public List<WebElement> findElements(final By locator, final boolean displayed, final boolean recovery) {
	if (DEBUG) debugPrintln("			(finding elements "+locator+" for "+this+", displayed="+displayed+", recovery="+recovery+")");

	// Fix locator if necessary
	By fixedLocator = fixLocator(locator);

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	// Find elements
	try{
		int count = 0;
		while (true) {
			try {
				List<WebElement> foundElements = this.webElement.findElements(fixedLocator);
				final int size = foundElements.size();
				List<WebElement> pageElements = new ArrayList<WebElement>(size);
				for (int idx=0; idx<size; idx++) {
					WebElement foundElement = foundElements.get(idx);
	//				if (foundElement.isDisplayed() || !displayed) {
					if (!displayed || foundElement.isDisplayed()) {
						BrowserElement webPageElement = new BrowserElement(this.browser, this.frame, this, fixedLocator, foundElement, size, idx);
						pageElements.add(webPageElement);
						if (DEBUG) {
							debugPrint("			  (-> found '"+webPageElement);
	//						if (foundElement.isDisplayed()) {
								debugPrintln(")");
	//						} else {
	//							debugPrintln(" - not displayed)");
	//						}
						}
					} else {
						if (DEBUG) debugPrintln("			  (-> element not displayed)");
					}
				}
				return pageElements;
			}
			catch (NoSuchElementException nsee) {
				return Browser.NO_ELEMENT_FOUND;
			}
			catch (UnhandledAlertException uae) {
				this.browser.purgeAlerts("Finding element '"+fixedLocator+"'");
				if (!recovery) {
					return Browser.NO_ELEMENT_FOUND;
				}
			}
			catch (WebDriverException wde) {
				if (recovery) {
					catchWebDriverException(wde, "finding elements '"+fixedLocator+")", count++, recovery);
				} else {
					if (DEBUG) debugPrintException(wde);
					return Browser.NO_ELEMENT_FOUND;
				}
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * Return the ancestor of the current element.
 *
 * @param depth The depth in the ancestor hierarchy. Must be positive, if <code>0</code>
 * then return the current instance.
 * @return The web element ancestor as a {@link BrowserElement}.
 */
public BrowserElement getAncestor(final int depth) {
	if (depth < 0) {
		throw new IllegalArgumentException("Cannot get ancestor with negative or zero relative depth.");
	}
	if (depth == 0) return this;
	StringBuilder xpathBuilder = new StringBuilder("..");
	for (int i=1; i<depth; i++) {
		xpathBuilder.append("/..");
	}
	return findElement(By.xpath(xpathBuilder.toString()));
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public String getAttribute(final String name) {
	return getAttribute(name, false/*fail*/);
}

private String getAttribute(final String name, final boolean fail) throws ScenarioFailedError {
	if (DEBUG) debugPrintln("			(getting attribute '"+name+"' for "+this+", fail="+fail+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				String attribute = this.webElement.getAttribute(name);
				if (DEBUG) debugPrintln("			 ( -> \""+attribute+"\")");
				if (attribute == null || attribute.isEmpty()) {
					if(fail) {
						throw new ScenarioFailedError("Cannot find attribute '"+name+"' in web element "+this);
					}
					return null;
				}
				return attribute;
			}
			catch (WebDriverException wde) {
				catchWebDriverException(wde, "getting attribute '"+name+")", count++, true);
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * Return the value of the given attribute.
 * <p>
 * Contreversary to {@link #getAttribute(String)} method, this one will fail if
 * the attribute is not found (ie. if it would have returned <code>null</null>).
 * </p><p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 * @param name The attribute name
 * @return The non-null attribute value as a {@link String}
 * @throws ScenarioFailedError If no attribute is found
 */
public String getAttributeValue(final String name) throws ScenarioFailedError {
	return getAttribute(name, true/*fail*/);
}

/**
 * Return the search mechanism to find the current element.
 *
 * @return The search mechanism as a {@link By}.
 */
public By getBy() {
	return this.by;
}

/**
 * Return the child of the current element.
 *
 * @return The web element child as a {@link BrowserElement}.
 * @throws ScenarioFailedError If there are either no child or several children
 * for the current element.
 */
public BrowserElement getChild() throws ScenarioFailedError{
	List<BrowserElement> children = getChildren();
	switch(children.size()) {
		case 1:
			return children.get(0);
		case 0:
			throw new WaitElementTimeoutError("Web element "+this+" has no child.");
		default:
			throw new ScenarioFailedError("Web element "+this+" has more than one child.");
	}
}

/**
 * Return the child with the given tag of the current element.
 *
 * @param tag The tag of the expected child element.
 * @return The web element child as a {@link BrowserElement}.
 * @throws ScenarioFailedError If there are either no child or several children
 * for the current element.
 */
public BrowserElement getChild(final String tag) throws ScenarioFailedError{
	List<BrowserElement> children = getChildren(tag);
	switch(children.size()) {
		case 1:
			return children.get(0);
		case 0:
			throw new WaitElementTimeoutError("Web element "+this+" has no child.");
		default:
			throw new ScenarioFailedError("Web element "+this+" has more than one child.");
	}
}

/**
 * Return all children of the current element.
 *
 * @return The list of web element children as a {@link List} of {@link BrowserElement}.
 */
public List<BrowserElement> getChildren() {
	return getList(findElements(CHILDREN_LOCATOR));
}

/**
 * Return specific children of the current element.
 *
 * @return The list of web element children as a {@link List} of {@link BrowserElement}.
 */
public List<BrowserElement> getChildren(final String tag) {
	return getList(findElements(By.xpath("./child::"+tag)));
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public Coordinates getCoordinates() {
	if (DEBUG) debugPrintln("			(getting coordinates for "+this+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				Coordinates coord = ((Locatable) this.webElement).getCoordinates();
				if (DEBUG) debugPrintln("			 ( -> "+coord+")");
				return coord;
			}
			catch (WebDriverException wde) {
				catchWebDriverException(wde, "getting coordinates", count++, true);
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public String getCssValue(final String propertyName) {
	if (DEBUG) debugPrintln("			(getting CSS value of '"+propertyName+"' for "+this+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				String value = this.webElement.getCssValue(propertyName);
				if (DEBUG) debugPrintln("			 ( -> \""+value+"\")");
				return value;
			}
			catch (WebDriverException wde) {
				catchWebDriverException(wde, "getting CSS value of '"+propertyName+")", count++, true);
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * Return the element frame.
 *
 * @return The frame as a {@link BrowserFrame}.
 */
public BrowserFrame getFrame() {
	return this.frame;
}

/**
 * Return the full xpath for the current element.
 *
 * @return The full xpath as a {@link String} or <code>null</code> if the search
 * mechanism was not found {@link ByXPath} one.
 */
public String getFullPath() {
	StringBuilder xpathBuilder = new StringBuilder();
	if (this.context instanceof BrowserElement) {
		xpathBuilder.append(((BrowserElement) this.context).getFullPath());
	}
	String locatorString = getLocatorString(this.by);
	final String currentXpath;
	if (this.by instanceof ByXPath) {
		currentXpath = locatorString;
	}
	else {
		String relative = xpathBuilder.length() > 0 ? "." : "";
		if (this.by instanceof ByClassName) {
			currentXpath = relative + "//*[contains(@class,'" + locatorString+ "')]";
		}
		else if (this.by instanceof ById) {
			currentXpath = relative + "//*[@id='" + locatorString+ "']";
		}
		else if (this.by instanceof ByName) {
			currentXpath = relative + "//*[@name='" + locatorString+ "']";
		}
		else if (this.by instanceof ByTagName) {
			currentXpath = relative + "//" + locatorString+ "]";
		}
		else if (this.by instanceof ByLinkText) {
			currentXpath = relative + "//a[text()='" + locatorString+ "']";
		}
		else if(this.by instanceof ByPartialLinkText) {
			currentXpath = relative + "//a[contains[text(),'"+locatorString+"')]";
		} else if (this.by instanceof ByCssSelector) {
			currentXpath = relative + "css=" + this.by.toString();
		} else {
			throw new ScenarioFailedError("Not implemented!");
		}
	}
	if (xpathBuilder.length() > 0 && currentXpath.length() > 1) {
		int start = currentXpath.charAt(0) == '(' ? 1 : 0; // Start position depends whether the xpath starts with a '(' or not
		if (currentXpath.charAt(start) == '.') {
			switch (currentXpath.charAt(start+1)) {
				case '.':
					xpathBuilder.append('/').append(currentXpath);
					break;
				case '/':
					xpathBuilder.append(currentXpath.substring(1));
					break;
				default:
					xpathBuilder.append(currentXpath);
					break;
			}
		} else if (!currentXpath.startsWith("parent")) {
			println("Non relative xpath for child element:");
			println("	- xpath: "+currentXpath);
			println("	- parent: "+xpathBuilder);
			println("	- stack trace:");
			StackTraceElement[] elements = new Exception().getStackTrace();
			printStackTrace(elements, 2);
		}
	} else {
		xpathBuilder.append(currentXpath);
	}
	return xpathBuilder.toString();
}

private JavascriptExecutor getJavascriptExecutor() {
	if (this.context instanceof WebDriver) {
		return (JavascriptExecutor) this.context;
	}
	return ((BrowserElement)this.context).getJavascriptExecutor();
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public Point getLocation() {
	if (DEBUG) debugPrintln("			(getting location of "+this+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				Point location = this.webElement.getLocation();
				if (DEBUG) debugPrintln("			 ( -> "+location+")");
				return location;
			}
			catch (WebDriverException wde) {
				catchWebDriverException(wde, "getting location", count++, true);
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * Return the parent of the current element.
 *
 * @return The web element parent as a {@link BrowserElement}.
 */
public BrowserElement getParent() {
	return findElement(By.xpath(".."));
}

/**
 * Get the value of a given internal property of the element. Will return the current value, even if this has
 * been modified after the page has been loaded.
 *
 * @param name The name of the property. For example, <code>innerHTML</code>, <code>clientHeight</code>.
 * Nested properties are also supported as long as each nested property is separated with character `.`.
 * For example, <code>__data__.label</code>, <code>__data__.layout.decorations.length</code>.
 *
 * @return The value of the given internal property of the element as {@link String} or <code>null</code> if
 * the value is not set or found.
 */
public String getProperty(final String name) {
	try {
		Object value = getJavascriptExecutor().executeScript("return arguments[0]." + name + ";", this.webElement);
		return value != null ? value.toString() : null;
	}
	catch (WebDriverException e) {
//		if(Pattern.matches("cannot read .* undefined", e.getMessage().toLowerCase())) return null;
//		throw e;
		return null;
	}
}

/**
 * Get the value of a given internal property of the element. Will return the current value, even if this has
 * been modified after the page has been loaded.
 * <p>
 * Contreversary to {@link #getProperty(String)} method, this one will fail if the property is not found.
 * </p>
 *
 * @param name The name of the property. For example, <code>innerHTML</code>, <code>clientHeight</code>.
 * Nested properties are also supported as long as each nested property is separated with character `.`.
 * For example, <code>__data__.label</code>, <code>__data__.layout.decorations.length</code>.
 *
 * @return The non-null value of the given internal property of the element as {@link String} or <code>null</code>
 * if the value is not set.
 *
 * @throws ScenarioFailedError If the property is not set or found.
 */
public String getPropertyValue(final String name) throws ScenarioFailedError {
	String value = getProperty(name);

	if(value == null) {
		throw new ScenarioFailedError("Cannot find property '" + name + "' in web element " + this);
	}

	return value;
}

@Override
public Rectangle getRect() {
	return this.webElement.getRect();
}

@Override
public <X> X getScreenshotAs(final OutputType<X> target) throws WebDriverException {
	return this.webElement.getScreenshotAs(target);
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public Dimension getSize() {
	if (DEBUG) debugPrintln("			(getting size of "+this+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				Dimension size = this.webElement.getSize();
				if (DEBUG) debugPrintln("			 ( -> "+size+")");
				return size;
			}
			catch (WebDriverException wde) {
				catchWebDriverException(wde, "getting size", count++, true);
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public String getTagName() {
	if (DEBUG) debugPrintln("			(getting tag name of "+this+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				String tagName= this.webElement.getTagName();
				if (DEBUG) debugPrintln("			 ( -> \""+tagName+"\")");
				return tagName;
			}
			catch (WebDriverException wde) {
				catchWebDriverException(wde, "getting tag name", count++, true);
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public String getText() {
	return getText(true/*recovery*/);
}

/**
 * Perform the {@link WebElement#getText()} operation.
 * <p>
 * If recovery is allowed, then catch any {@link WebDriverException} (except
 *  {@link InvalidSelectorException} and {@link UnreachableBrowserException})
 *  and retry the operation until success or {@link #MAX_RECOVERY_ATTEMPTS}
 *  attempts has been made. In the latter case, no exception is raised, but an
 *  empty string is returned.
 * </p><p>
 * If recovery is not allowed and an exception occurs, then it's still caught
 * but an empty string is returned instead of retrying.
 *  </p><p>
 *  Make sure that the element is in the currently selected frame before
 *  calling this function. Otherwise the driver will switch active frames
 *  without the new frame being reflected in {@link Browser}
 *  </p>
 *
 * @param recovery Tells whether try to recover is a {@link WebDriverException}
 * occurs
 * @see WebElement#getText()
 */
public String getText(final boolean recovery) {
	if (DEBUG) debugPrintln("			(getting text for "+this+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				String text;

				if(this.isDisplayed()) {
					text = this.webElement.getText();
				}
				else {
					text = getAttribute("textContent");
				}

				if (DEBUG) debugPrintln("			 ( -> \""+text+"\")");
				return text;
			}
			catch (WebDriverException wde) {
				if (recovery) {
					try{
						catchWebDriverException(wde, "getting text", count++, true);
					}
					catch (WebDriverException wde2) {
						if (DEBUG) {
							debugPrintln("			(WORKAROUND: exception "+wde2.getMessage()+" has been caught...");
							debugPrintln("			 -> return empty string \"\" instead)");
						}
						return EMPTY_STRING;
					}
				} else {
	//				if (DEBUG) debugPrintException(wde);
					if (DEBUG) {
						debugPrintln("			(WORKAROUND: exception "+wde.getMessage()+" has been caught...");
						debugPrintln("			 -> return empty string \"\" instead)");
					}
					return EMPTY_STRING;
				}
			}
		}
	}
	finally {
		if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * Returns the text of the web element after having ensured that it's visible.
 * <p>
 * This method try to make the web element visible if the text is empty.
 * </p>
 */
public String getTextWhenVisible() {
	String text = getText(true/*recovery*/);
	if (text.isEmpty()) {
		makeVisible(true/*force*/);
		text = getText(true/*recovery*/);
	}
	return text;
}

/**
 * Return the wrapped {@link WebElement}.
 *
 * @return The wrapped web element as a {@link WebElement}.
 */
public WebElement getWebElement() {
	return this.webElement;
}

@Override
public int hashCode() {
	return this.webElement.hashCode();
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public boolean isDisplayed() {
	return isDisplayed(true/*recovery*/);
}

/**
 * Perform the {@link WebElement#isDisplayed()} operation.
 * <p>
 * If recovery is allowed, then catch any {@link WebDriverException} (except
 *  {@link InvalidSelectorException} and {@link UnreachableBrowserException})
 *  and retry the operation until success or {@link #MAX_RECOVERY_ATTEMPTS}
 *  attempts has been made. In the latter case, no exception is raised, but an
 *  <code>false</code> is returned.
 * </p><p>
 * If recovery is not allowed and an exception occurs, then it's still caught
 * but <code>false</code> is returned instead of retrying.
 *  </p>
 * @param recovery Tells whether try to recover is a {@link WebDriverException}
 * occurs
 * @see WebElement#isDisplayed()
 */
public boolean isDisplayed(final boolean recovery) {
	if (DEBUG) debugPrintln("			(getting displayed state for "+this+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				boolean state = this.webElement.isDisplayed();
				if (DEBUG) debugPrintln("			 ( -> "+state+")");
				return state;
			}
			catch (WebDriverException wde) {
				if (recovery) {
					try{
						catchWebDriverException(wde, "getting displayed state", count++, true);
					}
					catch (WebDriverException wde2) {
						if (DEBUG) {
							debugPrintln("			(WORKAROUND: exception "+wde2.getMessage()+" has been caught...");
							debugPrintln("			 -> return false instead)");
						}
						return false;
					}
				} else {
	//				if (DEBUG) debugPrintException(wde);
					if (DEBUG) {
						debugPrintln("			(WORKAROUND: exception "+wde.getMessage()+" has been caught...");
						debugPrintln("			 -> return false instead)");
					}
					return false;
				}
			}
			// A NullPointerException may be thrown if org.openqa.selenium.remote.RemoteWebElement.isDisplayed invoked immediately before a dialog disappears from the DOM in FireFox.
			catch (NullPointerException npe) {
				// If reached here, it implies that the element is still visible.
				return true;
			}
		}
	}
	finally {
		if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public boolean isEnabled() {
	return isEnabled(true/*recovery*/);
}

/**
 * Perform the {@link WebElement#isEnabled()} operation.
 * <p>
 * If recovery is allowed, then catch any {@link WebDriverException} (except
 *  {@link InvalidSelectorException} and {@link UnreachableBrowserException})
 *  and retry the operation until success or {@link #MAX_RECOVERY_ATTEMPTS}
 *  attempts has been made. In the latter case, no exception is raised, but a
 *  <code>false</code> is returned.
 * </p><p>
 * If recovery is not allowed and an exception occurs, then it's still caught
 * but <code>false</code> is returned instead of retrying.
 *  </p>
 * @param recovery Tells whether try to recover if a {@link WebDriverException}
 * occurs
 * @see WebElement#isEnabled()
 */
public boolean isEnabled(final boolean recovery) {
	if (DEBUG) debugPrintln("			(getting enabled state for "+this+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				boolean state = this.webElement.isEnabled();
				if (DEBUG) debugPrintln("			 ( -> "+state+")");
				return state;
			}
			catch (WebDriverException wde) {
				if (recovery) {
					try{
						catchWebDriverException(wde, "getting enabled state", count++, true);
					}
					catch (WebDriverException wde2) {
						if (DEBUG) debugPrintln("Workaround exceptions by simulating a false return to isEnabled() call!");
						return false;
					}
				} else {
					if (DEBUG) debugPrintException(wde);
					return false;
				}
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

public boolean isInFrame() {
	return this.frame != null;
}

private boolean isMacOs() {
	return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public boolean isSelected() {
	if (DEBUG) debugPrintln("			(getting selected state for "+this+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				boolean state = this.webElement.isSelected();
				if (DEBUG) debugPrintln("			 ( -> "+state+")");
				return state;
			}
			catch (WebDriverException wde) {
				catchWebDriverException(wde, "getting selected state", count++, true);
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * Make the web element visible.
 * <p>
 * This is a no-op if the current web element is already visible.
 * </p>
 * @return This element to allow callers to insert this method .
 */
public BrowserElement makeVisible() {
	return makeVisible(false);
}

/**
 * Make the web element visible.
 *
 * @param force Force the visibility, even if it's already visible. That makes the
 * mouse cursor move to the current web element.
 * @return This element to allow callers to insert this method .
 */
public BrowserElement makeVisible(final boolean force) {
	if (force || !this.webElement.isDisplayed()) {
		moveToElement(true/*entirelyVisible*/);
	}
	return this;
}

/**
 * Simulate a mouse over by forcing a trigger of the javascript <code>mouseover</code>
 * event on the associated web element.
 * <p>
 * Note this method is a workaround of numerous issues we get with the mouse
 * over using Google Chrome and Internet Explorer and also with Firefox since
 * Selenium version 2.35.0...
 * </p>
 */
public void mouseOver() {
	getJavascriptExecutor().executeScript(MOUSE_OVER_JAVASCRIPT, this.webElement);
}

/**
 * Move to the current web element.
 * <p>
 * This is a simple move, hence web element might not be entirely visible in
 * the browser window after this action.
 * </p><p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 * @see #moveToElement(boolean)
 */
public void moveToElement() {
	moveToElement(false);
}

/**
 * Move to the current web element.
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 * @param entirelyVisible Ensure that the entire web element will be visible in
 * the browser window
 */
public void moveToElement(final boolean entirelyVisible) {
	if (DEBUG) debugPrintln("			(move to "+this+", entirelyVisible="+entirelyVisible+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				this.browser.moveToElement(this, entirelyVisible);
				if (DEBUG) debugPrintln("			 ( -> done.)");
				return;
			}
			catch (WebDriverException wde) {
				catchWebDriverException(wde, "move to element", count++, true);
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/*
 * Recover the web element. When the current browser element has a
 * WebBrowserElement as parent, then recover it first.
 */
private boolean recover(final int n) {
	debugPrintln("		+ Recover "+this);

	// If there's a parent, then recover it first
	if (this.context instanceof BrowserElement) {
		final BrowserElement parentElement = (BrowserElement) this.context;
		if (!parentElement.recover(n)) {
			return false;
		}
	}

	// Select the frame again if necessary
	BrowserFrame browserFrame = this.browser.getCurrentFrame();
	if (this.frame != browserFrame) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	// 	Try to find again the web element
	try {
		debugPrint("		  -> find element {"+this.by+"}");
		WebElement recoveredElement = null;
		if (this.parentListSize == 0) {

			// Single element expected
			if (this.context instanceof BrowserElement) {
				debugPrintln(" as single element in parent element...");
				try {
					recoveredElement = ((BrowserElement) this.context).webElement.findElement(this.by);
				}
				catch (NoSuchElementException nsee) {
					if (n == MAX_RECOVERY_ATTEMPTS) {
						debugPrintln("Workaround: recovery cannot find the element again, try with any possible frame");
						recoveredElement = this.browser.findElementInFrames(this.by).webElement;
						if (recoveredElement == null) {
							debugPrintln("	-> recovery cannot find the element even in other frame, give up...");
							throw nsee;
						}
						this.frame = this.browser.getCurrentFrame();
					}
				}
			} else {
				debugPrintln(" as single element in web driver...");
				try {
					recoveredElement = this.context.findElement(this.by);
				}
				catch (NoSuchElementException nsee) {
					if (n == MAX_RECOVERY_ATTEMPTS) {
						recoveredElement = this.browser.findElementInFrames(this.by);
						if (recoveredElement == null) {
							debugPrintln("	-> recovery cannot find the element even in other frame, give up...");
							throw nsee;
						}
						this.frame = this.browser.getCurrentFrame();
					}
				}
			}
		} else {
			// Multiple element expected
			List<WebElement> foundElements;
			if (this.context instanceof BrowserElement) {
				debugPrintln(" as multiple elements in parent element...");
				foundElements = ((BrowserElement) this.context).webElement.findElements(this.by);
			} else {
				debugPrintln(" as multiple elements in web driver...");
				foundElements = this.context.findElements(this.by);
			}

			// If no element was found, give up now
			final int size = foundElements.size();
			debugPrintln("		  -> found "+size+" elements:");
			if (size == 0) {
				debugPrintln("		  -> no element found => cannot recover, hence give up");
				return false;
			}

			// Check the element position
			int idx = 0;
			WebElement tempElement = null;
			boolean canRecover = true;
			for (WebElement foundElement: foundElements) {
				if (foundElement.isDisplayed()) {
					if (size == this.parentListSize && idx == this.parentListIndex) {
						debugPrintln("		  -> an element is visible at the same place int the list ("+idx+") => use it to recover.");
						recoveredElement = foundElement;
						break;
					}
					if (tempElement == null) {
						debugPrintln("		  -> an element is visible at the a different place in the list ("+idx+") => store it in case it will be the only one...");
						tempElement = foundElement;
					} else {
						debugPrintln("		  -> more than one element is visible at the a different place in the list ("+idx+") => if none is found at the same index, we'll try to recover with the first one...");
						canRecover = false;
					}
				} else {
					if (size == this.parentListSize && idx == this.parentListIndex) {
						debugPrintln("		  -> an element is hidden at the same place int the list ("+idx+") => it to recover.");
						tempElement = foundElement;
					} else {
						debugPrintln("		  -> an element is hidden at the a different place in the list ("+idx+") => it won't be stored...");
					}
				}
				idx++;
			}

			// If element position does not match exactly the expected one, try to use
			// the better found one, if any.
			if (recoveredElement == null) {
				if (canRecover) {
					if (tempElement == null) {
						debugPrintln("		  -> no visible element was found to recover!");
					} else if (n == MAX_RECOVERY_ATTEMPTS) {
						debugPrintln("		  -> last try, hence use possible element found.");
						recoveredElement = tempElement;
					}
				} else if (n == MAX_RECOVERY_ATTEMPTS) {
					debugPrintln("		  -> last try, hence use possible element found.");
					recoveredElement = tempElement;
				} else {
					debugPrintln("		  -> several visible elements were found to recover but not at the same index!");
				}
			}
		}

		// Give up if no element was found
		if (recoveredElement == null) {
			debugPrintln("WARNING: Cannot recover web element for "+this.by+"!");
			return false;
		}

		// Store the recovered element
		this.webElement = recoveredElement;

		// Check element type
		if (this.webElement instanceof BrowserElement) {
			throw new ScenarioFailedError("Web element should not be a WebBrowserElement!");
		}
		return true;
	}
	finally {
		if (browserFrame != this.frame) {
			this.browser.selectFrame();
		}
	}
}

/**
 * Remove an element via JavaScript.
 */
public void removeViaJavaScript() {
	executeScript("remove()");
}

/**
 * Performs a right click action on the element.
 */
public void rightClick() {

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		this.browser.getActions().contextClick(this.webElement).perform();
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * Scroll the page to the given element.
 * <p>
 * This is a no-op if the web element is already visible in the browser view.
 * </p>
 */
public void scrollIntoView() {
	if (DEBUG) debugPrintln("		+ Scroll current web element into view");
	executeScript("scrollIntoView( true )");
}

/**
 * Select or check the given element. This operation only applies to input
 * elements such as checkboxes, options in a select and radio buttons.
 * The element will only be selected/checked if it has not been
 * selected/checked already.
 *
 * @return The element that has been selected or checked.
 */
public BrowserElement select() {
	return alter(true /* select */);
}

private void sendKeys(final boolean recovery, final boolean password, final CharSequence... keysToSend) {
	if (DEBUG) {
		StringBuilder builder = new StringBuilder();
		String separator = "";
		for (CharSequence sequence: keysToSend) {
			builder.append(sequence.toString()).append(separator);
			separator = "', '";
		}
		String printedText;
		if (password) {
			printedText = "password '*****'";
		} else {
			printedText = "keys '"+builder.toString()+"'";
		}
		debugPrintln("			(sending "+printedText+" to "+this+")");
	}

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				this.webElement.sendKeys(keysToSend);
				if (DEBUG) debugPrintln("			 ( -> done.)");
				return;
			}
			catch (WebDriverException wde) {
				if (recovery) {
					catchWebDriverException(wde, "sending keys '"+keysToSend+")", count++, true);
				} else {
	//				if (DEBUG) debugPrintException(wde);
					if (DEBUG) {
						debugPrintln("			(WORKAROUND: exception "+wde.getMessage()+" has been caught...");
						debugPrintln("			 -> DO Nothing instead!)");
					}
					return;
				}
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * Perform the {@link WebElement#sendKeys(CharSequence...)} operation.
 * <p>
 * If recovery is allowed, then catch any {@link WebDriverException} (except
 *  {@link InvalidSelectorException} and {@link UnreachableBrowserException})
 *  and retry the operation until success or {@link #MAX_RECOVERY_ATTEMPTS}
 *  attempts has been made.
 * </p><p>
 * If recovery is not allowed and an exception occurs, then it silently ignors
 * {@link StaleElementReferenceException} exception.
 *  </p>
 * @param recovery Tells whether try to recover is a {@link WebDriverException}
 * occurs
 * @see WebElement#sendKeys(CharSequence...)
 */
public void sendKeys(final boolean recovery, final CharSequence... keysToSend) {
	sendKeys(recovery, false/*password*/, keysToSend);
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public void sendKeys(final CharSequence... keysToSend) {
	sendKeys(true/*recovery*/, keysToSend);
}

private void setVisible(final boolean visible) {
	getJavascriptExecutor().executeScript("arguments[0].style.visibility=\""+(visible?"visible":"hidden")+"\";", this.webElement);
}

/**
 * {@inheritDoc}
 * <p>
 * Catch {@link WebDriverException} and retry the operation until success or
 * {@link #MAX_RECOVERY_ATTEMPTS} attempts has been made.
 * </p>
 */
@Override
public void submit() {
	if (DEBUG) debugPrintln("			(submitting on "+this+")");

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		int count = 0;
		while (true) {
			try {
				this.webElement.submit();
				if (DEBUG) debugPrintln("			 ( -> done.)");
				return;
			}
			catch (WebDriverException wde) {
				catchWebDriverException(wde, "submitting", count++, true);
			}
		}
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * Resynchronize the current with web element displayed in the page.
 * <p>
 * This synchronization is usually handled automatically by the framework, but
 * in certain circumstances, object user might know that a refresh will be necessary
 * before getting some information from the current instance.
 * </p>
 * @noreference Outside the framework plugins!
 */
public WebElement synchronize() {
	recover(MAX_RECOVERY_ATTEMPTS);
	return this.webElement;
}

@Override
public String toString() {
	StringBuilder builder = new StringBuilder("Web element {");
	builder.append("full xpath: ").append(getFullPath());
	builder.append("} in ");
	if (this.frame == null) {
		builder.append("no frame");
	} else {
		builder.append("<frame: ").append(this.frame).append(">");
	}
	return builder.toString();
}

/**
 * Wait until have found the element using given locator.
 * <p>
 * Note that:
 * <ol>
 * <li>hidden element are not returned by this method.</li>
 * <li>{@link Timeouts#DEFAULT_TIMEOUT} is used as default timeout before given
 * up the search operation if no element is found.</li>
 * </ol>
 * </p>
 * @param locator Locator to find the element in the current page.
 * @return The web element as {@link BrowserElement}. It cannot be <code>null</code>.
 * @throws WaitElementTimeoutError If no element is found before the default
 * timeout.
 * @throws ScenarioFailedError If there are several found elements as only
 * one is expected.
 */
public BrowserElement waitForElement(final By locator) {
	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		return this.browser.waitForElement(this, locator, true/*fail*/, DEFAULT_TIMEOUT, true/*displayed*/, true/*single*/);
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * Wait until have found the element using given search mechanism.
 *
 * @param locator The locator to find the element in the current page.
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout
 * @throws ScenarioFailedError If there are several found elements although
 * only one was expected.
 */
public BrowserElement waitForElement(final By locator, final boolean displayed) {
	return waitForElement(locator, DEFAULT_TIMEOUT, displayed, true /*single*/);
}

/**
 * Wait until have found the element using given search mechanism.
 * <p>
 * Note that hidden element are not returneded by this method.
 * </p>
 * @param locator The locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout
 * @throws ScenarioFailedError If there are several found elements although
 * only one was expected.
 */
public BrowserElement waitForElement(final By locator, final int timeout) {
	return waitForElement(locator, timeout, true/*displayed*/, true/*single*/);
}

/**
 * Wait until have found the element using given search mechanism.
 *
 * @param locator The locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @param single Tells whether a single element is expected
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout
 * @throws ScenarioFailedError If there are several found elements although
 * only one was expected.
 */
public BrowserElement waitForElement(final By locator, final int timeout, final boolean displayed, final boolean single) {
	if (DEBUG) debugPrintln("		+ waiting for element: ["+locator+"] relatively to "+this);

	// Wait for all elements
	List<BrowserElement> foundElements = waitForElements(locator, timeout, displayed);
	if (foundElements == null) return null;
	int size = foundElements.size();
	if (size == 0) return null;
	if (!ScenarioUtils.getParameterBooleanValue("performanceEnabled",false)&&size > 1) {
		if (single) {
			throw new MultipleVisibleElementsError(foundElements);
		}
		debugPrintln("WARNING: found more than one elements ("+size+"), return the first one!");
	}

	// Return the found element
	return foundElements.get(0);
}

/**
 * Wait until have found one of element using the given search mechanisms.
 * <p>
 * Fail if:
 * <ul>
 * <li>none of the possible elements are found after having waited the given
 * timeout.</li>
 * <li>several elements are found for the same mechanism.</li>
 * </p>
 * @param locators The locators of the expected elements.
 * @param timeout The time to wait before giving up the research
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail
 * @throws ScenarioFailedError if no element was found before the timeout or
 * several elements are found for the same mechanism.
 */
public BrowserElement waitForElement(final By[] locators, final int timeout) {
	return this.browser.waitForElement(this, locators, true/*fail*/, timeout);
}

/**
 * Wait until have found at least one element using the given locator.
 * <p>
 * Note that:
 * <ul>
 * <li>hidden element will be ignored</li>
 * <li>it will fail if no element is found before {@link Timeouts#SHORT_TIMEOUT} seconds</li>
 * </ul>
 * </p>
 * @param locator The locator to find the elements
 * @return A {@link List} of web element as {@link BrowserElement}. Cannot
 * be empty
 * @throws WaitElementTimeoutError If no element was found before the timeout
 * @since 6.0.0
 */
public List<BrowserElement> waitForElements(final By locator) {
	return this.browser.waitForElements(this, locator, true/*fail*/, DEFAULT_TIMEOUT, true/*displayed*/);
}

/**
 * Wait until have found at least one element using the given search mechanism.
 * <p>
 * Only displayed element are return by this method.
 * </p>
 * @param locator The locator to find the element
 * @param timeout The time in seconds before giving up if the element is not
 * found
 * @return A {@link List} of web element as {@link BrowserElement}. Might
 * be empty if no element was found before the timeout
 */
public List<BrowserElement> waitForElements(final By locator, final int timeout) {
	return waitForElements(locator, timeout, true/*displayed*/);
}

/**
 * Wait until have found at least one element using the given search mechanism.
 *
 * @param locator The locator to find the element
 * @param timeout The time in seconds before giving up if the element is not
 * found
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @return A {@link List} of web element as {@link BrowserElement}. Might
 * be empty if no element was found before the timeout
 */
public List<BrowserElement> waitForElements(final By locator, final int timeout, final boolean displayed) {
	if (DEBUG) debugPrintln("		+ waiting for elements: ["+locator+"] relatively to "+this);

	// Select the frame again if necessary
	if (this.frame != this.browser.getFrame()) {
		this.browser.selectFrame(this.frame, false/*store*/);
	}

	try {
		return this.browser.waitForElements(this, locator, false/*do not fail*/, timeout, displayed);
	}
	finally {
	if (this.frame != this.browser.getFrame()) {
			this.browser.selectFrame();
		}
	}
}

/**
 * Wait until have found at least one of the elements using the given mechanisms.
 * <p>
 * Note that:
 * <li>it will fail if the element is not found before the timeout</li>
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
	return this.browser.waitForMultipleElements(this, findBys, true /* fail */, DEFAULT_TIMEOUT);
}

/**
 *  Wait while the current web element is disabled in the page.
 *
 *  @param timeout The timeout in seconds before giving up if the element is still disabled.
 *  @param fail Tells whether to return <code>false</code> instead throwing
 *  a {@link WaitElementTimeoutError} when the timeout is reached.
 *  @return <code>true</code> if the element has enabled before the timeout
 *  is reached. Otherwise return <code>false</code> only if it has been asked not
 *  to fail.
 *  @throws WaitElementTimeoutError If the element is still disabled after the
 *  given timeout has been reached and it has been asked to fail.
 */
public boolean waitWhileDisabled(final int timeout, final boolean fail) {
	// Loop until the status element is enabled.
	long timeoutMillis = timeout * 1000 + System.currentTimeMillis();
	while (!isEnabled(false/* recovery */)) {
		if (System.currentTimeMillis() > timeoutMillis) {
			if (fail) {
				throw new WaitElementTimeoutError(this+" was still disabled after " + timeout + " seconds, give up.");
			}
			return false;
		}
	}
	return true;
}

/**
 *  Wait while a given child web element is displayed in the page.
 *
 *  @param locator Locator to find the child element in the current page.
 *  @param seconds The timeout before giving up if the element is still displayed.
 *  @param fail Tells whether to return <code>false</code> instead throwing
 *  a {@link WaitElementTimeoutError} when the timeout is reached.
 *  @return <code>true</code> if the element has disappeared before the timeout
 *  is reached. Otherwise return <code>false</code> only if it has been asked not
 *  to fail.
 *  @throws WaitElementTimeoutError If the element is still displayed after the
 *  given timeout has been reached and it has been asked to fail.
 */
public boolean waitWhileDisplayed(final By locator, final int seconds, final boolean fail) {
	return this.browser.waitWhileDisplayed(this, locator, seconds, fail);
}

/**
 *  Wait while the current web element is displayed in the page.
 *
 *  @param seconds The timeout before giving up if the element is still displayed.
 *  @throws WaitElementTimeoutError If the element is still displayed after the
 *  given timeout has been reached.
 */
public boolean waitWhileDisplayed(final int seconds) {
	return waitWhileDisplayed(seconds, true/*fail*/);
}

/**
 *  Wait while the current web element is displayed in the page.
 *
 *  @param seconds The timeout before giving up if the element is still displayed.
 *  @param fail Tells whether to return <code>false</code> instead throwing
 *  a {@link WaitElementTimeoutError} when the timeout is reached.
 *  @return <code>true</code> if the element has disappeared before the timeout
 *  is reached. Otherwise return <code>false</code> only if it has been asked not
 *  to fail.
 *  @throws WaitElementTimeoutError If the element is still displayed after the
 *  given timeout has been reached and it has been asked to fail.
 */
public boolean waitWhileDisplayed(final int seconds, final boolean fail) {
	// Loop until the status element is displayed
	long timeout = seconds * 1000 + System.currentTimeMillis();
	while (isDisplayed(false/* recovery */)) {
		if (System.currentTimeMillis() > timeout) {
			if (fail) {
				throw new WaitElementTimeoutError(this+" was still displayed after " + seconds + " seconds, give up.");
			}
			return false;
		}
	}
	return true;
}
}
