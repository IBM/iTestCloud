/*********************************************************************
 * Copyright (c) 2016, 2022 IBM Corporation and others.
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

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.*;
import static com.ibm.itest.cloud.common.tests.utils.ByUtils.isRelativeLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.Action;

import com.ibm.itest.cloud.common.pages.dialogs.AbstractDialog;
import com.ibm.itest.cloud.common.tests.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.tests.scenario.errors.WaitElementTimeoutError;

/**
 * This class represents a generic dropdown list element and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getOptions()}: Return the list of options available in the dropdown list.</li>
 * <li>{@link #getSelection()}: Return the currently selected item in the dropdown list.</li>
 * <li>{@link #isExpandable()}:
 * Returns whether the current wrapped web element is expandable or not.</li>
 * <li>{@link #isExpanded()}: Returns whether the current wrapped web element is expanded or not.</li>
 * <li>{@link #isOptionAvailable(Pattern)}:
 * Specifies whether an item matching a given pattern exists as an option in the dropdown list.</li>
 * <li>{@link #isOptionAvailable(String)}:
 * Specifies whether a given item exists as an option in the dropdown list.</li>
 * <li>{@link #isOptionEnabled(Pattern)}:
 * Specifies whether an item matching a given pattern is available and enabled as an option in the dropdown list.</li>
 * <li>{@link #select(int)}:
 * Select an item (option) at a given index from the drop down list.</li>
 * <li>{@link #select(Pattern)}:
 * Select an item (option) matching the given pattern from the dropdown list.</li>
 * <li>{@link #select(String)}: Select a given item (option) from the dropdown list.</li>
 * Select a specific item (option) from the dropdown list element by opening a {@link AbstractDialog}.</li>
 * <li>{@link #selectByOpeningDialog(String, Class)}:
 * Select a specific item (option) from the dropdown list element by opening a {@link AbstractDialog}.</li>
 * <li>{@link #selectByOpeningElement(Pattern, By, Class, String...)}:
 * Select a specific item (option) from the dropdown list element by opening an element.</li>
 * <li>{@link #selectByOpeningElement(Pattern, Class, String...)}:
 * Select a specific item (option) from the dropdown list element by opening an element.</li>
 * <li>{@link #selectByOpeningElement(String, By, Class, String...)}:
 * Select a specific item (option) from the dropdown list element by opening an element.</li>
 * <li>{@link #selectByOpeningElement(String, Class, String...)}:
 * Select a specific item (option) from the dropdown list element by opening an element.</li>
 * <li>{@link #selectByOpeningPage(Pattern, Class, String...)}:
 * Select a specific item (option) from the dropdown list element by opening a {@link WebPage}.</li>
 * <li>{@link #selectByOpeningPage(String, Class, String...)}:
 * Select a specific item (option) from the dropdown list element by opening a {@link WebPage}.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpandableAttribute()}: Return the expandable attribute.</li>
 * </ul>
 * </p>
 */
public class WebDropdownlistElement extends AbstractExpandableElement {

	private static final int SELECTION_RETRY_LIMIT = 5;

	protected By selectionLocator;

public WebDropdownlistElement(final WebElementWrapper parent) {
	super(parent);
	this.selectionLocator = null;
}

public WebDropdownlistElement(final WebElementWrapper parent, final By locator, final By expansionLocator, final By selectionLocator) {
	super(parent, locator, expansionLocator);
	this.selectionLocator = selectionLocator;
}

public WebDropdownlistElement(final WebElementWrapper parent, final WebBrowserElement webElement, final By expansionLocator, final By selectionLocator) {
	super(parent, webElement, expansionLocator);
	this.selectionLocator = selectionLocator;
}

public WebDropdownlistElement(final WebPage page) {
	super(page);
	this.selectionLocator = null;
}

public WebDropdownlistElement(final WebPage page, final By findBy, final By expansionLocator, final By selectionLocator) {
	super(page, findBy, expansionLocator);
	this.selectionLocator = selectionLocator;
}

public WebDropdownlistElement(final WebPage page, final WebBrowserElement webElement, final By expansionLocator, final By selectionLocator) {
	super(page, webElement, expansionLocator);
	this.selectionLocator = selectionLocator;
}

@Override
protected String getExpandableAttribute() {
	throw new ScenarioFailedError("This method should never be called.");
}

/**
 * Return the option element matching a given pattern.
 *
 * @param fail Specifies whether to fail if a matching option element is not found before the timeout.
 *
 * @return The option element matching the given pattern as {@link WebBrowserElement}.
 */
protected WebBrowserElement getOptionElement(final Pattern pattern, final boolean fail) {
	long timeoutMillis = (fail ? timeout() : tinyTimeout()) * 1000 + System.currentTimeMillis();

	while(true) {
		List<WebBrowserElement> optionsItemElements = getOptionElements(fail);

		for (WebBrowserElement optionsItemElement : optionsItemElements) {
			if(pattern.matcher(getOptionElementLabel(optionsItemElement)).matches()){
				return optionsItemElement;
			}
		}

		if (System.currentTimeMillis() > timeoutMillis) {
			if(fail) {
				throw new WaitElementTimeoutError("Option item in pattern '" + pattern.pattern() + "' could not be found in dropdown list.");
			}
			return null;
		}
	}
}

/**
 * Return the label associated with a given option element.
 *
 * @param optionsItemElement The option element as {@link WebBrowserElement}.
 *
 * @return The label associated with a given option element as {@link String}.
 */
protected String getOptionElementLabel(final WebBrowserElement optionsItemElement) {
	return optionsItemElement.getText();
}

/**
 * Return the locator to search for a generic option element.
 *
 * @return Return the locator to search for a generic option element as {@link By}.
 */
protected By getOptionElementLocator() {
	return By.xpath(".//button[(@class='select__options__item__a')]");
}

/**
 * Return all the option elements in the drop-down list element.
 *
 * @param fail Specifies whether to fail if no option elements are found before the timeout.
 *
 * @return All the option elements in the drop-down list element.
 */
protected List<WebBrowserElement> getOptionElements(final boolean fail) {
	// Expand the dropdown list in case its option elements are only made available in the HTML DOM after
	// the dropdown list has been expanded.
	expand();
	// Find the option elements next.
	final By optionElementLocator = getOptionElementLocator();
	WebBrowserElement parentElement = isRelativeLocator(optionElementLocator) ? this.element : null;

	return this.browser.waitForElements(parentElement, optionElementLocator, fail, fail ? timeout() : tinyTimeout(), false /*displayed*/);
}

/**
 * Return the list of options available in the dropdown list.
 *
 * @return The list of options available in the dropdown list as {@link List}.
 */
public List<String> getOptions() {
	List<WebBrowserElement> optionsItemElements = getOptionElements(false /*displayed*/);
	List<String> items = new ArrayList<String>();

	for (WebBrowserElement optionsItemElement : optionsItemElements) {
		items.add(optionsItemElement.getText());
	}

	return items;
}

/**
 * Return the currently selected item in the dropdown list.
 *
 * @return The currently selected item in the dropdown list as a {@link String}
 */
public String getSelection() {
	WebBrowserElement currentlySelectionElement = getSelectionElement();
	return (currentlySelectionElement != null) ? currentlySelectionElement.getText() : null;
}

/**
 * Return the selection element.
 *
 * @return the selection element as {@link WebBrowserElement}.
 */
protected WebBrowserElement getSelectionElement() {
	if(this.selectionLocator == null) return null;
	WebBrowserElement parentElement = isRelativeLocator(this.selectionLocator) ? this.element : null;

	return this.browser.waitForElement(parentElement, this.selectionLocator, false /*fail*/, 0 /*timeout*/, false /*displayed*/, true /*single*/);
}

@Override
public boolean isExpandable() throws ScenarioFailedError {
	return true;
}

@Override
public boolean isExpanded() throws ScenarioFailedError {
	// In the newer version of drop-down list elements, the expanded status is specified with text
	// 'open' in the class attribute of the element itself.
	if((this.element.isDisplayed() && this.element.getAttributeValue("class").toLowerCase().contains("open")) ||
	   (this.expansionElement.isDisplayed() && this.expansionElement.getAttributeValue("class").toLowerCase().contains("open"))) {
		return true;
	}

	WebBrowserElement selectOptionsElement =
		this.element.waitForElement(By.xpath(".//ul[@class='select__options']"), tinyTimeout(), false /*displayed*/, true /*single*/);
	if(selectOptionsElement != null) {
		String style = selectOptionsElement.getAttributeValue("style");
		// remove style values "overflow:hidden", "overflow-x:hidden", "overflow-y:hidden"
		style = style.replace(" ", "").replaceAll("overflow((-x)|(-y))?:hidden", "");
		return selectOptionsElement.isDisplayed() && !style.contains("hidden") && !style.contains("height:0px");
	}

	return false;
}

/**
 * Specifies whether an item matching a given pattern exists as an
 * option in the dropdown list.
 *
 * @param pattern The pattern to match an item in the dropdown list.
 *
 * @return <code>true</code> if an item matching the given pattern exists
 * or <code>false</code> otherwise.
 */
public boolean isOptionAvailable(final Pattern pattern) {
	return getOptionElement(pattern, false /*fail*/) != null;
}

/**
 * Specifies whether a given item exists as an option in the dropdown list.
 *
 * @param item The item to check for existence in the dropdown list.
 *
 * @return <code>true</code> if the given item exists in the dropdown list
 * or <code>false</code> otherwise.
 */
public boolean isOptionAvailable(final String item) {
	return getOptionElement(Pattern.compile(Pattern.quote(item)), false /*fail*/) != null;
}

/**
 * Specifies whether an item matching a given pattern is available and enabled as an
 * option in the dropdown list.
 *
 * @param pattern The pattern to match an item in the dropdown list.
 *
 * @return <code>true</code> if an item matching the given pattern is available and enabled or <code>false</code> otherwise.
 */
public boolean isOptionEnabled(final Pattern pattern) {
	return getOptionElement(pattern, true /*fail*/).isEnabled();
}

/**
 * Specifies whether a given list item has already been selected.
 *
 * @param pattern The pattern to search the given list item
 *
 * @return <code>true</code> if the given list item has already been selected or
 * <code>false</code> otherwise.
 */
protected boolean isSelected(final Pattern pattern) {
	String selection = getSelection();
	return selection != null ? pattern.matcher(selection).matches() : false;
}

/**
 * Select an item (option) at a given index from the drop down list.
 *
 * @param index The zero-based index of the item to be selected from the menu.
 *
 * @return The selected item as {@link WebBrowserElement}.
 */
public WebBrowserElement select(final int index) {
	List<String> options = getOptions();
	// Check if the given index is valid.
	if(options.size() <= index) {
		throw new ScenarioFailedError("Given index '" + index + "' is unavailable in the drop down list");
	}

	return select(options.get(index));
}

/**
 * Select an item (option) matching a given pattern from the dropdown list.
 *
 * @param pattern The pattern to use to match the item to select.
 *
 * @return The selected menu item as a {@link WebBrowserElement}
 */
public WebBrowserElement select(final Pattern pattern) {
	String selection = null;

	for (int i = 0; i < SELECTION_RETRY_LIMIT; i++) {
		WebBrowserElement optionElement = selectHelper(pattern);

		// Check if the desired option is already the currently selected item in the dropdown list.
		// If so, simply return;
		if(optionElement == null) {
			return null;
		}

		// Select the option.
		selectOptionElement(optionElement);

		// If the selection check is not required or the desired option has been selected,
		// then return to the caller.
		if(!selectionCheckExpected() || isSelected(pattern)) return optionElement;
		// The desired option may not be selected at times due to an unknown reason.
		// In such a situation, attempt the proper selection again.
		selection = getSelection();
		println("	  -> Option with pattern '"+ pattern +"' was expected to be selected, but option '" + selection + "' was selected instead.");
		println("	  -> Attempting the selection again...");
	}
	// Throw an appropriate exception if the desired project has not been selected
	// even after the maximum number of attempts.
	throw new ScenarioFailedError("Option with pattern '"+ pattern +"' was expected to be selected, but option '" + selection + "' was selected instead even in '" + SELECTION_RETRY_LIMIT + "' attempts");
}

/**
 * Select a given item (option) from the drop down list.
 *
 * @param item The item to select from the menu.
 *
 * @return The selected item as {@link WebBrowserElement}.
 */
public WebBrowserElement select(final String item) {
	return select(Pattern.compile(Pattern.quote(item)));
}

/**
 * Select a specific item (option) from the dropdown list element by opening a {@link AbstractDialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param findBy The locator of the confirmation dialog element opened after clicking on the particular menu menu item.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 * @param postElementClickAction The action to perform after clicking the element as {@link Action}.
 * @param data Additional information to store in the dialog when it is opened.
 *
 * @return The opened confirmation dialog as a subclass of {@link AbstractDialog}.
 */
public <P extends AbstractDialog> P selectByOpeningDialog(final Pattern pattern, final By findBy, final Class<P> dialogClass, final Action postElementClickAction, final String... data) {
	P confirmationDialog;
	try {
		if(findBy != null) {
			if((data == null) || (data.length == 0)) {
				confirmationDialog = dialogClass.getConstructor(WebPage.class, By.class).newInstance(getPage(), findBy);
			}
			else {
				confirmationDialog = dialogClass.getConstructor(WebPage.class, By.class, String[].class).newInstance(getPage(), findBy, data);
			}
		}
		else {
			if((data == null) || (data.length == 0)) {
				confirmationDialog = dialogClass.getConstructor(WebPage.class).newInstance(getPage());
			}
			else {
				confirmationDialog = dialogClass.getConstructor(WebPage.class, String[].class).newInstance(getPage(), data);
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

	confirmationDialog.open(selectHelper(pattern), postElementClickAction);
	return confirmationDialog;
}

/**
 * Select a specific item (option) from the dropdown list element by opening a {@link AbstractDialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param findBy The locator of the confirmation dialog element opened after clicking on the particular menu menu item.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 * @param data Additional information to store in the dialog when it is opened.
 *
 * @return The opened confirmation dialog as a subclass of {@link AbstractDialog}.
 */
public <P extends AbstractDialog> P selectByOpeningDialog(final Pattern pattern, final By findBy, final Class<P> dialogClass, final String... data) {
	return selectByOpeningDialog(pattern, findBy, dialogClass, null /*postElementClickAction*/, data);
}

/**
 * Select a specific item (option) from the dropdown list element by opening a {@link AbstractDialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 * @param postElementClickAction The action to perform after clicking the element as {@link Action}.
 * @param data Additional information to store in the dialog when it is opened.
 *
 * @return The opened confirmation dialog as a subclass of {@link AbstractDialog}.
 */
public <P extends AbstractDialog> P selectByOpeningDialog(final Pattern pattern, final Class<P> dialogClass, final Action postElementClickAction, final String... data) {
	return selectByOpeningDialog(pattern, null /*findBy*/, dialogClass, postElementClickAction, data);
}

/**
 * Select a specific item (option) from the dropdown list element by opening a {@link AbstractDialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 * @param data Additional information to store in the dialog when it is opened.
 *
 * @return The opened confirmation dialog as a subclass of {@link AbstractDialog}.
 */
public <P extends AbstractDialog> P selectByOpeningDialog(final Pattern pattern, final Class<P> dialogClass, final String... data) {
	return selectByOpeningDialog(pattern, dialogClass, null /*postElementClickAction*/, data);
}


/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link AbstractDialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param findBy The locator of the confirmation dialog element opened after clicking on the particular menu menu item.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 *
 * @return The opened confirmation dialog as a subclass of {@link AbstractDialog}.
 */
public <P extends AbstractDialog> P selectByOpeningDialog(final String item, final By findBy, final Class<P> dialogClass, final String...  data) {
	return selectByOpeningDialog(Pattern.compile(Pattern.quote(item)), findBy, dialogClass, data);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link AbstractDialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 *
 * @return The opened confirmation dialog as a subclass of {@link AbstractDialog}.
 */
public <P extends AbstractDialog> P selectByOpeningDialog(final String item, final Class<P> dialogClass) {
	return selectByOpeningDialog(Pattern.compile(Pattern.quote(item)), dialogClass);
}

/**
 * Select a specific item (option) from the dropdown list element by opening a {@link AbstractDialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 * @param postElementClickAction The action to perform after clicking the element as {@link Action}.
 * @param data Additional information to store in the dialog when it is opened.
 *
 * @return The opened confirmation dialog as a subclass of {@link AbstractDialog}.
 */
public <P extends AbstractDialog> P selectByOpeningDialog(final String item, final Class<P> dialogClass, final Action postElementClickAction, final String... data) {
	return selectByOpeningDialog(Pattern.compile(Pattern.quote(item)), dialogClass, postElementClickAction, data);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link AbstractDialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 *
 * @return The opened confirmation dialog as a subclass of {@link AbstractDialog}.
 */
public <P extends AbstractDialog> P selectByOpeningDialog(final String item, final Class<P> dialogClass, final String...  data) {
	return selectByOpeningDialog(Pattern.compile(Pattern.quote(item)), dialogClass, data);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * an element.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param findBy The locator of the element opened after clicking on the link element.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link WebPage}.
 */
public <T extends WebElementWrapper> T selectByOpeningElement(final Pattern pattern, final By findBy, final Class<T> elementClass, final String... elementData) {
	return openElementUsingLink(selectHelper(pattern), findBy, elementClass, elementData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * an element.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link WebPage}.
 */
public <T extends WebElementWrapper> T selectByOpeningElement(final Pattern pattern, final Class<T> elementClass, final String... elementData) {
	return selectByOpeningElement(pattern, (By)null /*findBy*/, elementClass, elementData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * an element.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param findBy The locator of the element opened after clicking on the link element.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link WebPage}.
 */
public <T extends WebElementWrapper> T selectByOpeningElement(final String item, final By findBy, final Class<T> elementClass, final String... elementData) {
	return selectByOpeningElement(Pattern.compile(Pattern.quote(item)), findBy, elementClass, elementData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * an element.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link WebPage}.
 */
public <T extends WebElementWrapper> T selectByOpeningElement(final String item, final Class<T> elementClass, final String... elementData) {
	return selectByOpeningElement(item, (By)null /*findBy*/, elementClass, elementData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link WebPage}.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param pageClass A class representing the web page opened
 * after clicking on the particular menu item.
 * @param postLinkClickAction The action to perform after clicking the link as {@link Action}.
 * @param pageData Additional information to store in the page when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link WebPage}.
 */
@SuppressWarnings("unchecked")
public <T extends WebPage> T selectByOpeningPage(final Pattern pattern, final Class<T> pageClass, final Action postLinkClickAction, final String... pageData) {
	WebBrowserElement menuItemElement = selectHelper(pattern);

	if(menuItemElement == null){
		return (T) getPage();
	}

	return openPageUsingLink(menuItemElement, pageClass, postLinkClickAction, pageData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link WebPage}.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param pageClass A class representing the web page opened
 * after clicking on the particular menu item.
 * @param pageData Additional information to store in the page when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link WebPage}.
 */
public <T extends WebPage> T selectByOpeningPage(final Pattern pattern, final Class<T> pageClass, final String... pageData) {
	return selectByOpeningPage(pattern, pageClass, null /*postLinkClickAction*/, pageData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link WebPage}.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param pageClass A class representing the web page opened
 * after clicking on the particular menu item.
 * @param postLinkClickAction The action to perform after clicking the link as {@link Action}.
 * @param pageData Additional information to store in the page when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link WebPage}.
 */
public <T extends WebPage> T selectByOpeningPage(final String item, final Class<T> pageClass, final Action postLinkClickAction, final String... pageData) {
	return selectByOpeningPage(Pattern.compile(Pattern.quote(item)), pageClass, postLinkClickAction, pageData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link WebPage}.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param pageClass A class representing the web page opened
 * after clicking on the particular menu item.
 * @param pageData Additional information to store in the page when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link WebPage}.
 */
public <T extends WebPage> T selectByOpeningPage(final String item, final Class<T> pageClass, final String... pageData) {
	return selectByOpeningPage(item, pageClass, null /*postLinkClickAction*/, pageData);
}

/**
 * Perform the following generic tasks towards selecting an item from the list.
 * <ol>
 * <li>Check if the given item has already been selected in the list.
 * No further actions will be performed if that is the case</li>
 * <li>Expand the list by bringing its items to visibility</li>
 * <li>Locating and returning a list item matching the given</li>
 * </ol>
 *
 * @param pattern The pattern to match a list item for the given.
 *
 * @return A list item matching the given as a {@link WebBrowserElement}.
 */
protected WebBrowserElement selectHelper(final Pattern pattern) {
	if (DEBUG) debugPrintln("		+ Select item matching pattern '"+ pattern.pattern() + "' from the drop down list");

	if(isSelected(pattern)) {
		debugPrintln("		  -> No need to select '" + pattern.pattern() + "' because it is already the currently selected item in the dropdown list.");
		return null;
	}

	return getOptionElement(pattern, true /*fail*/);
}

private boolean selectionCheckExpected() {
	return this.selectionLocator != null;
}

/**
 * Select a given option element.
 *
 * @param optionElement The option element to select.
 */
protected void selectOptionElement(final WebBrowserElement optionElement) {
//	optionElement.click();
	// The option element is not scrolled at times. Therefore, use the JavaScript method to click the option element to
	// select it since the particular method seems to perform the scroll operation properly.
	optionElement.clickViaJavaScript();
}
}
