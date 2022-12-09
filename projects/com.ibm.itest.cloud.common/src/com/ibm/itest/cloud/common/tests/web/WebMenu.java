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
package com.ibm.itest.cloud.common.tests.web;

import static com.ibm.itest.cloud.common.tests.performance.PerfManager.PERFORMANCE_ENABLED;
import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.*;

import java.util.List;

import org.openqa.selenium.*;

import com.ibm.itest.cloud.common.tests.performance.PerfManager;
import com.ibm.itest.cloud.common.tests.performance.PerfManager.RegressionType;
import com.ibm.itest.cloud.common.tests.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.tests.scenario.errors.WaitElementTimeoutError;

/**
 * Class to manage menus.
 * <p>
 * Menus can be considered like window as they are also opened by clicking
 * on a web element, usually a menu item or drop-down button.
 * </p><p>
 * The open operation looks like a window one but have the peculiarity to
 * check that items are loaded before returning.
 * </p><p>
 * Additionally, this generic level offers the following features:
 * <ul>
 * <li>{@link #clickItem(String)}: Click on item element found using the given label.</li>
 * <li>{@link #clickItem(String, Class, String...)}: Open the given page by clicking
 * on the given menu item label.</li>
 * <li>{@link #clickItem(String, By, Class, String...)}: Open the given page by
 * clicking on the link element found by using the given relative mechanism from
 * the given menu item element found by using the item label.</li>
 * <li>{@link #hasItem(String)}: Check whether an item with the given name exists in the menu.</li>
 * <li>{@link #getItemElement(String)}: Return the web element for the item
 * matching the given label.</li>
 * <li>{@link #getItemElement(String, boolean)}: Return the web element for the item
 * matching the given label.</li>
 * <li>{@link #getItemElements()}: Returns the list of Menu option elements within
 * this menu.</li>
 * <li>
 * </ul>
 * </p><p>
 * Following actions are accessible or overridden in this page:
 * <ul>
 * <li>{@link #closeAction(boolean)}: The action to perform to close the menu.</li>
 * <li>{@link #displayItemElement(String, WebBrowserElement)}: Display a non-visible
 * element from the menu.</li>
 * <li>{@link #getCloseButton(boolean)}: Return the xpath of the button to close
 * the menu.</li>
 * <li>{@link #getItemElement(String, int, boolean, boolean)}: Return the web
 * element for the item matching the given label.</li>
 * <li>{@link #getItemXpath(String)}: Returns the xpath for the given item.</li>
 * <li>{@link #isMenuItemDisplayed(String)}: Helper method to determine if a particular menu item is displayed.<li>
 * <li>{@link #waitForItemElement(String, boolean, int)}: Wait for the given item the given timeout.</li>
 * <li>{@link #waitForLoadingEnd()}: Wait for the window content to be loaded.</li>
 * </ul>
 * </p>
 */
public class WebMenu extends AbstractWindow {

	/**
	 * The element used to open the popup-menu. It's stored as it usually can
	 * be used to close the popup-menu.
	 */
	protected WebBrowserElement openElement;

	final private boolean useRightClick;

public WebMenu(final WebPage page, final By findBy) {
	this(page, findBy, false);
}

public WebMenu(final WebPage page, final By findBy, final boolean useRightClick) {
	super(page, findBy);
	this.useRightClick = useRightClick;
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
protected void addPerfResult(final RegressionType regressionType, final String pageTitle) throws ScenarioFailedError {

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
	perfManager.addPerfResult(pageTitle, "CLM Dropdown URL");
}

/**
 * Click on item element found using the given label.
 *
 * @param item The item text to click on
 */
public void clickItem(final String item) {

	// Get item element
	WebBrowserElement itemElement = getItemElement(item);

	// Check that the
	itemElement.click();
}

/**
 * Open the given page by clicking on the link element found by using the given
 * relative xpath from the given menu item element found by using the item label.
 *
 * @param itemLabel The item label to click on
 * @param linkBy The relative path from the found item web element to the
 * web element on which it's necessary to click to have the real action.
 * @param pageClass The class of the page to be opened by the item click
 * @param pageData Additional data to store in the opened page
 * @return The opened page
 */
public <P extends WebPage> P clickItem(final String itemLabel, final By linkBy, final Class<P> pageClass, final String... pageData) {
	WebBrowserElement itemElement = getItemElement(itemLabel);
	if (linkBy != null) {
		itemElement = itemElement.findElement(linkBy);
	}
	return getPage().openPageUsingLink(itemElement, pageClass, pageData);
}

/**
 * Open the given menu by clicking on the given menu item.
 *
 * @param item The item label to click on
 * @param windowBy The mechanism to find the window when opened
 * @param windowClass The class of the window which will be opened when
 * clicking on the item
 * @return The opened window as an instance of the given class
 */
public <W extends AbstractWindow> W clickItem(final String item, final By windowBy, final Class<W> windowClass) {
	WebBrowserElement itemElement = getItemElement(item);
	try {
		W menu = WebWindowFactory.createInstance(getPage(), windowBy, windowClass);
		menu.open(itemElement);
		return menu;
	}
	catch (Exception ex) {
		throw new ScenarioFailedError(ex);
	}
}

/**
 * Open the given page by clicking on the given menu item label.
 *
 * @param itemLabel The item label to click on
 * @param pageClass The class of the page to be opened by the item click
 * @param pageData Additional data to store in the opened page
 * @return The opened page
 */
public <P extends WebPage> P clickItem(final String itemLabel, final Class<P> pageClass, final String... pageData) {
	return clickItem(itemLabel, null/*linkPath*/, pageClass, pageData);
}

/**
 * Open the given window by clicking on the given menu item.
 *
 * @param item The item label to click on
 * @param windowClass The class of the window which will be opened when
 * clicking on the item
 * @return The opened window as an instance of the given class
 */
public <W extends AbstractWindow> W clickItem(final String item, final Class<W> windowClass) {
	return clickItem(item, null, windowClass);
}

/**
 * Open the given dialog by clicking on the given menu item element.
 *
 * @param itemElement The item element to click on
 * @param dialogClass The class of the dialog to be opened by the item click
 * @return The opened dialog
 */
public <D extends AbstractDialog> D clickItem(final WebBrowserElement itemElement, final Class<D> dialogClass) {
	try {
		D dialog = WebWindowFactory.createInstance(getPage(), dialogClass);
		dialog.open(itemElement);
		return dialog;
	}
	catch (Exception ex) {
		throw new ScenarioFailedError(ex);
	}
}

/**
 * Close the menu by sending Escape key to opening element.
 */
@Override
protected void closeAction(final boolean cancel) {
//	this.openElement.click();
	this.openElement.sendKeys(Keys.ESCAPE);
}

/**
 * Display a non-visible element from the menu.
 * <p>
 * Subclass needs to override this method with typical action in order to avoid
 * to get the default {@link ScenarioFailedError}.
 * </p>
 * @param itemLabel The item label
 * @param itemElement The web element of the menu item
 * @throws ScenarioFailedError If there's no way to make the item visible.
 */
protected void displayItemElement(final String itemLabel, final WebBrowserElement itemElement) throws ScenarioFailedError {
	throw new ScenarioFailedError("Menu item '"+itemLabel+"' exists but is not accessible.");
}

/**
 * Find a list of web elements matching the given locator.
 * <p>
 * This way to find elements take into account the fact whether the current menu
 * is displayed in a frame or not.
 * </p>
 * @param locator The locator to find elements in the page
 * @param recovery Tells whether the recovery has to be activated or not
 * @return The list of elements as a {@link List} of {@link WebElement}.
 */
protected List<WebElement> findElements(final By locator, final boolean recovery) {
	return this.element.findElements(locator, recovery);
}

/**
 * There's no action to close a menu, it closes alone.
 */
@Override
protected String getCloseButton(final boolean validate) {
	return null;
}

/**
 * Return the web element for the item matching the given label.
 * <p>
 * Note that the returned element has to be visible otherwise this method will
 * raise a {@link WaitElementTimeoutError}.
 * </p><p>
 * If the element is not found after the timeout has expired, then a page refresh
 * is done and the menu opened again to make another try. If the element is still
 * not found after this workaround, then a {@link WaitElementTimeoutError} is
 * raised.
 * </p>
 * @param itemLabel The label of the item to click on
 * @return The corresponding item element as {@link WebBrowserElement}
 * @throws WaitElementTimeoutError If the item element is still not found after
 * having try to workaround
 */
public WebBrowserElement getItemElement(final String itemLabel) throws WaitElementTimeoutError {
	return getItemElement(itemLabel, openTimeout(), true/*displayed*/, true/*workaround*/);
}

/**
 * Return the web element for the item matching the given label.
 * <p>
 * If the element is not found after the timeout has expired, then a page refresh
 * is done and the menu opened again to make another try. If the element is still
 * not found after this workaround, then a {@link WaitElementTimeoutError} is
 * raised.
 * </p>
 * @param itemLabel The label of the item to click on
 * @param displayed When <code>true</code> then only displayed item element can be returned.
 * When <code>false</code> then the returned item element can be either displayed or hidden.
 * @return The corresponding item element as {@link WebBrowserElement}
 * @throws WaitElementTimeoutError If the item element is still not found after
 * having try to workaround
 */
public WebBrowserElement getItemElement(final String itemLabel, final boolean displayed) throws WaitElementTimeoutError {
	return getItemElement(itemLabel, openTimeout(), displayed, true/*canWorkaround*/);
}

/**
 * Return the web element for the item matching the given label.
 *
 * @param itemLabel The label of the item to click on
 * @param timeout Time in seconds to wait for the item element to be found
 * @param displayed When <code>true</code> then only displayed item element can be returned.
 * When <code>false</code> then the returned item element can be either displayed or hidden.
 * @param canWorkaround Tells whether a workaround is accepted if the item
 * element is not found after the timeout
 * @return The corresponding item element in the menu as a {@link WebBrowserElement}
 * or <code>null</code> if not found after the given timeout and no workaround
 * is allowed
 * @throws ScenarioFailedError If the item element is found but not visible.
 * @throws WaitElementTimeoutError If the workaround is allowed and the item
 * element is still not found after it.
 */
protected WebBrowserElement getItemElement(final String itemLabel, final int timeout, final boolean displayed, final boolean canWorkaround) throws WaitElementTimeoutError {

	// Wait for item element
	WebBrowserElement itemElement = waitForItemElement(itemLabel, displayed, timeout);

	// If element is not found
	if (itemElement == null) {

		// If workaround is allowed then try to refresh the page
		if (canWorkaround) {

			// Print workaround info
			println("Workaround: Cannot find the '"+itemLabel+"' item in '"+this+"' menu, try to refresh the page...");
			printStackTrace(1);

			// Refresh the browser page
			this.browser.refresh();

			// Re-open the menu
			open(this.openElement);

			// Wait for the item again
			itemElement = waitForItemElement(itemLabel, displayed, timeout);
		}

		// Raise an error if the element was still not found
		if (itemElement == null) {
			throw new WaitElementTimeoutError("Cannot find the '"+itemLabel+"' item in '"+this+"' menu.");
		}
	}

	// If the element was found but not visible, try to make it visible
	if (!itemElement.isDisplayed()) {
		displayItemElement(itemLabel, itemElement);
	}

	// Return the found item
	return itemElement;
}

/**
 * Returns the list of item elements of the current menu.
 *
 * @noreference Internal public method. It should not be used outside the framework.
 * @return A list of {@link WebElement}s representing the menu items
 */
public final List<WebBrowserElement> getItemElements() {
	// Allow hidden element to speed up search of menu options
	// TODO: see item 232323 for a general solution all over the framework...
	return waitForElements(getItemElementsLocator(), timeout(), false/*displayed*/);
}

/**
 * Returns the locator of the menu item elements.
 * <p>
 * By default the item elements are identified as <code>tr</code> tag name web
 * having <code>class</code> attribute containing with <code>'dijitMenuItem'</code>
 * and not being read only.
 * </p><p>
 * Subclasses might want to override this method in order to provide a more specific
 * locator.
 * </p>
 * @return The locator as a {@link By}
 */
protected By getItemElementsLocator() {
	return By.xpath(".//tr[contains(@class,'dijitMenuItem') and not(contains(@class,'dijitReadOnly'))]");
}

/**
 * Returns the locator for the given item.
 *
 * @param itemLabel The item label
 * @return The item locator as a {@link By}.
 */
protected String getItemXpath(final String itemLabel) {
	char firstChar = itemLabel.charAt(0);
	if (Character.isLetter(firstChar) || Character.isDigit(firstChar)) {
		char stringQuote = itemLabel.indexOf('\'') < 0 ? '\'' : '"';
		StringBuilder xpathBuilder = new StringBuilder(".//*[(@dojoattachpoint='containerNode' or @data-dojo-attach-point='containerNode') and normalize-space(text())=")
			.append(stringQuote)
			.append(itemLabel)
			.append(stringQuote)
			.append(']');
		return xpathBuilder.toString();
	}
	return itemLabel;
}

/**
 * Returns the list of Menu option strings within this popup menu.
 * <p>
 * Menu options are identified as <code>tr</code> tag name web element having
 * ids starting with <code>'jazz_ui_menu_MenuItem'</code> or <code>'dijit_MenuItem'</code>.
 * </p>
 * @param shouldClose Tells whether to close the menu after providing the list
 * @return A list of {@link String}s representing the menu items
 */
public List<String> getStringMenuOptions(final boolean shouldClose) {
	List<WebBrowserElement> elements  = getItemElements();
	List<String> strings = toStrings(elements);
	if (shouldClose) {
		close();
	}
	return strings;
}

/**
 * Check whether an item with the given name exists in the menu or not.
 *
 * @param menuItem the name of the item to check the existence of.
 * @return <code>true</false> if the item exists, <code>false</code> otherwise.
 */
public boolean hasItem(final String menuItem) {
	return waitForItemElement(menuItem, true /*displayed*/, 1/*second*/) != null;
}

/**
 * Returns whether the given menu item is displayed or not.
 *
 * @param menuItem name of the menu item.
 * @return <code>true</code> if the item is displayed, <code>false</code> otherwise.
 */
protected boolean isMenuItemDisplayed(final String menuItem) {
	// Get all menu options as string
	List<String> items = getStringMenuOptions(false);

	// Iterate over the items
	for (String item : items) {
		// If target item found return true
		if (item.equals(menuItem)) {
			return true;
		}
	}

	// Return false
	return false;
}

/**
 * {@inheritDoc}
 * <p>
 * Open the menu found with the given search mechanism and return the
 * corresponding web element.
 * </p><p>
 * Note that the menu is opened by clicking on a link element found using
 * the given search mechanism.
 * </p><p>
 * When possible, it also waits for all items to be loaded before returning.
 * </p>
 */
@Override
public WebBrowserElement open(final WebBrowserElement webElement) {
	if (DEBUG) debugPrintln("		+ Get popup-menu "+this.findBy+" by clicking on "+webElement);

	// Get popup menu web element
	WebBrowserElement menuElement = this.browser.waitForElement(getParentElement(), this.findBy, false/*do not fail*/, 0/*sec*/, true/*displayed*/, true/*single*/);

	// Loop until the web element is really found and displayed
	int count = 0;
	while (menuElement == null || !menuElement.isEnabled()) {
		if (count++ > 10) {
			throw new WaitElementTimeoutError("Menu was never displayed.");
		}
		if (this.useRightClick) {
			webElement.rightClick();
		} else {
			webElement.click();
		}
		menuElement = this.browser.waitForElement(getParentElement(), this.findBy, false/* do not fail */, 1/* sec */, true/* displayed */, true/* single */);
	}

	// Store the open element
	this.openElement = webElement;

	// Store the menu web element
	this.element = menuElement;

	// Wait for the end of the load of the menu items
	waitForLoadingEnd();

	// Add performance result
	if (PERFORMANCE_ENABLED) {
		addPerfResult(RegressionType.Server, "CLM Dropdown");
	}

	// Return the menu element
	return this.element;
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
 * {@inheritDoc}
 * <p>
 * Overridden to take into account the fact whether the current menu is displayed in a frame or not.
 * </p>
 */
@Override
public WebBrowserElement waitForElement(final By locator, final int timeout) {
	return this.element.waitForElement(locator, timeout);
}


/**
 * {@inheritDoc}
 * <p>
 * Overridden to take into account the fact whether the current menu is displayed in a frame or not.
 * </p>
 */
@Override
public List<WebBrowserElement> waitForElements(final By locator) {
	return waitForElements(locator, timeout(), /*displayed:*/true);
}


/**
 * {@inheritDoc}
 * <p>
 * Overridden to take into account the fact whether the current menu is displayed in a frame or not.
 * </p>
 */
@Override
public List<WebBrowserElement> waitForElements(final By locator, final int timeout, final boolean displayed) {
	return this.element.waitForElements(locator, timeout, displayed);
}

/**
 * Wait for the given item the given timeout.
 *
 * @param itemLabel The item label
 * @param seconds The amount of seconds to wait before given up.
 * @return The item element as a {@link WebBrowserElement} or <code>null</code>
 * if not found before the given timeout.
 */
protected WebBrowserElement waitForItemElement(final String itemLabel, final boolean displayed, final int seconds) {

	// Gtet item locator
	By itemBy = By.xpath(getItemXpath(itemLabel));

	// Get item element
	WebBrowserElement itemElement = this.element.waitForElement(itemBy, displayed);

	// Try to workaround when item has not be found
	if (itemElement == null) {

		// First loop until openTimeout occurs
		long timeout = seconds * 1000 + System.currentTimeMillis();
		while (itemElement == null) {
			if (System.currentTimeMillis() > timeout) break;
			itemElement = this.element.waitForElement(itemBy, displayed);
		}
	}

	// Return found element
	return itemElement;
}

/**
 * {@inheritDoc}
 * <p>
 * Wait until the menu is loaded
 * </p><p>
 * So far, it only waits for the first item not to be 'Loading...'.
 * </p><p>
 * Initially we were also testing that the items number was stable, but that was
 * too time consuming. Then, we tried to test that last item become stable, but
 * that didn't work for menus which have several columns...
 * </p>
 */
@Override
public void waitForLoadingEnd() {

	// Get first item
	startTimeout(openTimeout(), "Menu "+this.element+" never finish to load.");
	List<WebBrowserElement> itemElements = getItemElements();
	int size = itemElements.size();
	if (size == 0) {
		// We should have at least one item, hence something wrong happened
		// Try to reopen the menu if the timeout has not been reached
		testTimeout();
		debugPrintln("Menu "+this.element+" had no item, try to reopen it...");
		open(this.openElement);
		return;
	}

	// Wait until first item is no longer saying "Loading..."
	for (WebBrowserElement itemElement: itemElements) {
		String itemText = itemElement.getText(false/*recovery*/);
		if (itemText.startsWith("Loading")) {
			testTimeout();
			waitForLoadingEnd();
			return;
		}
	}

	// Check that menu items are all displayed
	// TODO Improve following commented algorithm which tests the last item
	// and wait to become stable. Unfortunately that was not possible to activate
	// it as it didn't work for multi-column menus...
//	WebBrowserElement lastItemElement = getLastItemElement();
//	String initialLastItemText = previousLastItemElement.getText();
//	while (!lastItemElement.getText().equals(initialLastItemText)) {
//		testTimeout();
//		sleep(1);
//	}

	// Reset timeout
	resetTimeout();
}
}
