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
package itest.cloud.page.element;

import static itest.cloud.scenario.ScenarioUtil.*;
import static itest.cloud.util.ByUtils.isRelativeLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.Action;

import itest.cloud.page.Page;
import itest.cloud.page.dialog.Dialog;
import itest.cloud.scenario.error.ScenarioFailedError;
import itest.cloud.scenario.error.WaitElementTimeoutError;

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
 * Select a specific item (option) from the dropdown list element by opening a {@link Dialog}.</li>
 * <li>{@link #selectByOpeningDialog(String, Class)}:
 * Select a specific item (option) from the dropdown list element by opening a {@link Dialog}.</li>
 * <li>{@link #selectByOpeningElement(Pattern, By, Class, String...)}:
 * Select a specific item (option) from the dropdown list element by opening an element.</li>
 * <li>{@link #selectByOpeningElement(Pattern, Class, String...)}:
 * Select a specific item (option) from the dropdown list element by opening an element.</li>
 * <li>{@link #selectByOpeningElement(String, By, Class, String...)}:
 * Select a specific item (option) from the dropdown list element by opening an element.</li>
 * <li>{@link #selectByOpeningElement(String, Class, String...)}:
 * Select a specific item (option) from the dropdown list element by opening an element.</li>
 * <li>{@link #selectByOpeningPage(Pattern, Class, String...)}:
 * Select a specific item (option) from the dropdown list element by opening a {@link Page}.</li>
 * <li>{@link #selectByOpeningPage(String, Class, String...)}:
 * Select a specific item (option) from the dropdown list element by opening a {@link Page}.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpandableAttribute()}: Return the expandable attribute.</li>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * </p>
 */
public class DropdownlistElement extends ExpandableElement {

	private static final int SELECTION_RETRY_LIMIT = 5;

	protected By selectionLocator, optionLocator;

protected DropdownlistElement(final ElementWrapper parent) {
	super(parent);
}

public DropdownlistElement(final ElementWrapper parent, final BrowserElement webElement, final By expansionLocator, final By selectionLocator, final By optionLocator) {
	super(parent, webElement, expansionLocator);
	this.selectionLocator = selectionLocator;
	this.optionLocator = optionLocator;
}

public DropdownlistElement(final ElementWrapper parent, final By locator, final By expansionLocator, final By selectionLocator, final By optionLocator) {
	super(parent, locator, expansionLocator);
	this.selectionLocator = selectionLocator;
	this.optionLocator = optionLocator;
}

protected DropdownlistElement(final Page page) {
	super(page);
}

public DropdownlistElement(final Page page, final BrowserElement webElement, final By expansionLocator, final By selectionLocator, final By optionLocator) {
	super(page, webElement, expansionLocator);
	this.selectionLocator = selectionLocator;
	this.optionLocator = optionLocator;
}

public DropdownlistElement(final Page page, final By findBy, final By expansionLocator, final By selectionLocator, final By optionLocator) {
	super(page, findBy, expansionLocator);
	this.selectionLocator = selectionLocator;
	this.optionLocator = optionLocator;
}

@Override
protected String getExpandableAttribute() {
	throw new ScenarioFailedError("This method should never be called.");
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

/**
 * Return the option element matching a given pattern.
 *
 * @param fail Specifies whether to fail if a matching option element is not found before the timeout.
 *
 * @return The option element matching the given pattern as {@link BrowserElement}.
 */
protected BrowserElement getOptionElement(final Pattern pattern, final boolean fail) {
	long timeoutMillis = (fail ? timeout() : tinyTimeout()) * 1000 + System.currentTimeMillis();

	while(true) {
		List<BrowserElement> optionItemElements = getOptionElements(fail);

		for (BrowserElement optionItemElement : optionItemElements) {
			if(pattern.matcher(getOptionElementLabel(optionItemElement)).matches()){
				return optionItemElement;
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
 * @param optionItemElement The option element as {@link BrowserElement}.
 *
 * @return The label associated with a given option element as {@link String}.
 */
protected String getOptionElementLabel(final BrowserElement optionItemElement) {
	return optionItemElement.getText();
}

/**
 * Return all the option elements in the drop-down list element.
 *
 * @param fail Specifies whether to fail if no option elements are found before the timeout.
 *
 * @return All the option elements in the drop-down list element.
 */
protected List<BrowserElement> getOptionElements(final boolean fail) {
	// Expand the dropdown list in case its option elements are only made available in the HTML DOM after
	// the dropdown list has been expanded.
	expand();
	// Find the option elements next.
	BrowserElement parentElement = isRelativeLocator(this.optionLocator) ? this.element : null;

	return waitForElements(parentElement, this.optionLocator, (fail ? timeout() : tinyTimeout()), fail, false /*displayed*/);
}

/**
 * Return the list of options available in the dropdown list.
 *
 * @return The list of options available in the dropdown list as {@link List}.
 */
public List<String> getOptions() {
	List<BrowserElement> optionsItemElements = getOptionElements(false /*displayed*/);
	List<String> items = new ArrayList<String>();

	for (BrowserElement optionsItemElement : optionsItemElements) {
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
	BrowserElement currentlySelectionElement = getSelectionElement();
	return (currentlySelectionElement != null) ? currentlySelectionElement.getText() : null;
}

/**
 * Return the selection element.
 *
 * @return the selection element as {@link BrowserElement}.
 */
protected BrowserElement getSelectionElement() {
	if(!isSelectionCheckExpected()) return null;

	BrowserElement parentElement = isRelativeLocator(this.selectionLocator) ? this.element : null;
	return waitForElement(parentElement, this.selectionLocator, tinyTimeout(), false /*fail*/, false /*displayed*/);
}

@Override
protected By getTitleElementLocator() {
	return null;
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

	BrowserElement selectOptionsElement =
		waitForElement(By.xpath(".//ul[@class='select__options']"), tinyTimeout(), false /*fail*/, false /*displayed*/);
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

private boolean isSelectionCheckExpected() {
	return this.selectionLocator != null;
}

/**
 * Select an item (option) at a given index from the drop down list.
 *
 * @param index The zero-based index of the item to be selected from the menu.
 *
 * @return The selected item as {@link BrowserElement}.
 */
public BrowserElement select(final int index) {
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
 * @return The selected menu item as a {@link BrowserElement}
 */
public BrowserElement select(final Pattern pattern) {
	String selection = null;

	for (int i = 0; i < SELECTION_RETRY_LIMIT; i++) {
		BrowserElement optionElement = selectHelper(pattern);

		// Check if the desired option is already the currently selected item in the dropdown list.
		// If so, simply return;
		if(optionElement == null) {
			return null;
		}

		// Select the option.
		selectOptionElement(optionElement);

		// If the selection check is not required or the desired option has been selected,
		// then return to the caller.
		if(!isSelectionCheckExpected() || isSelected(pattern)) return optionElement;
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
 * @return The selected item as {@link BrowserElement}.
 */
public BrowserElement select(final String item) {
	return select(Pattern.compile(Pattern.quote(item)));
}

/**
 * Select a specific item (option) from the dropdown list element by opening a {@link Dialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param findBy The locator of the confirmation dialog element opened after clicking on the particular menu menu item.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 * @param postElementClickAction The action to perform after clicking the element as {@link Action}.
 * @param dialogData Additional information to store in the dialog when it is opened.
 *
 * @return The opened confirmation dialog as a subclass of {@link Dialog}.
 */
public <P extends Dialog> P selectByOpeningDialog(final Pattern pattern, final By findBy, final Class<P> dialogClass, final Action postElementClickAction, final String... dialogData) {
	P confirmationDialog;
	try {
		if(findBy != null) {
			if((dialogData == null) || (dialogData.length == 0)) {
				confirmationDialog = dialogClass.getConstructor(Page.class, By.class).newInstance(getPage(), findBy);
			}
			else {
				confirmationDialog = dialogClass.getConstructor(Page.class, By.class, String[].class).newInstance(getPage(), findBy, dialogData);
			}
		}
		else {
			if((dialogData == null) || (dialogData.length == 0)) {
				confirmationDialog = dialogClass.getConstructor(Page.class).newInstance(getPage());
			}
			else {
				confirmationDialog = dialogClass.getConstructor(Page.class, String[].class).newInstance(getPage(), dialogData);
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
 * Select a specific item (option) from the dropdown list element by opening a {@link Dialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param findBy The locator of the confirmation dialog element opened after clicking on the particular menu menu item.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 * @param dialogData Additional information to store in the dialog when it is opened.
 *
 * @return The opened confirmation dialog as a subclass of {@link Dialog}.
 */
public <P extends Dialog> P selectByOpeningDialog(final Pattern pattern, final By findBy, final Class<P> dialogClass, final String... dialogData) {
	return selectByOpeningDialog(pattern, findBy, dialogClass, null /*postElementClickAction*/, dialogData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening a {@link Dialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 * @param postElementClickAction The action to perform after clicking the element as {@link Action}.
 * @param dialogData Additional information to store in the dialog when it is opened.
 *
 * @return The opened confirmation dialog as a subclass of {@link Dialog}.
 */
public <P extends Dialog> P selectByOpeningDialog(final Pattern pattern, final Class<P> dialogClass, final Action postElementClickAction, final String... dialogData) {
	return selectByOpeningDialog(pattern, null /*findBy*/, dialogClass, postElementClickAction, dialogData);
}


/**
 * Select a specific item (option) from the dropdown list element by opening a {@link Dialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 * @param dialogData Additional information to store in the dialog when it is opened.
 *
 * @return The opened confirmation dialog as a subclass of {@link Dialog}.
 */
public <P extends Dialog> P selectByOpeningDialog(final Pattern pattern, final Class<P> dialogClass, final String... dialogData) {
	return selectByOpeningDialog(pattern, dialogClass, null /*postElementClickAction*/, dialogData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link Dialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param findBy The locator of the confirmation dialog element opened after clicking on the particular menu menu item.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 *
 * @return The opened confirmation dialog as a subclass of {@link Dialog}.
 */
public <P extends Dialog> P selectByOpeningDialog(final String item, final By findBy, final Class<P> dialogClass, final String...  dialogData) {
	return selectByOpeningDialog(Pattern.compile(Pattern.quote(item)), findBy, dialogClass, dialogData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link Dialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 *
 * @return The opened confirmation dialog as a subclass of {@link Dialog}.
 */
public <P extends Dialog> P selectByOpeningDialog(final String item, final Class<P> dialogClass) {
	return selectByOpeningDialog(Pattern.compile(Pattern.quote(item)), dialogClass);
}

/**
 * Select a specific item (option) from the dropdown list element by opening a {@link Dialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 * @param postElementClickAction The action to perform after clicking the element as {@link Action}.
 * @param dialogData Additional information to store in the dialog when it is opened.
 *
 * @return The opened confirmation dialog as a subclass of {@link Dialog}.
 */
public <P extends Dialog> P selectByOpeningDialog(final String item, final Class<P> dialogClass, final Action postElementClickAction, final String... dialogData) {
	return selectByOpeningDialog(Pattern.compile(Pattern.quote(item)), dialogClass, postElementClickAction, dialogData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link Dialog}.
 * <p>
 * A confirmation dialog will automatically be opened after clicking
 * on the particular menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param dialogClass A class representing the confirmation dialog opened
 * after clicking on the particular menu menu item.
 *
 * @return The opened confirmation dialog as a subclass of {@link Dialog}.
 */
public <P extends Dialog> P selectByOpeningDialog(final String item, final Class<P> dialogClass, final String...  dialogData) {
	return selectByOpeningDialog(Pattern.compile(Pattern.quote(item)), dialogClass, dialogData);
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
 * @return The opened confirmation dialog as a subclass of {@link Page}.
 */
public <T extends ElementWrapper> T selectByOpeningElement(final Pattern pattern, final By findBy, final Class<T> elementClass, final String... elementData) {
	return selectByOpeningElement(pattern, (ElementWrapper)null /*elementParent*/, findBy, elementClass, elementData);
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
 * @return The opened confirmation dialog as a subclass of {@link Page}.
 */
public <T extends ElementWrapper> T selectByOpeningElement(final Pattern pattern, final Class<T> elementClass, final String... elementData) {
	return selectByOpeningElement(pattern, (ElementWrapper)null /*elementParent*/, (By)null /*findBy*/, elementClass, elementData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * an element.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param elementParent The parent element of the element to open by clicking on the link element..
 * If <code>null</code> then element is expected in the current page.
 * @param findBy The locator of the element opened after clicking on the link element.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link Page}.
 */
public <T extends ElementWrapper> T selectByOpeningElement(final Pattern pattern, final ElementWrapper elementParent, final By findBy, final Class<T> elementClass, final String... elementData) {
	return openElementUsingLink(selectHelper(pattern), elementParent, findBy, elementClass, elementData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * an element.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param elementParent The parent element of the element to open by clicking on the link element..
 * If <code>null</code> then element is expected in the current page.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link Page}.
 */
public <T extends ElementWrapper> T selectByOpeningElement(final Pattern pattern, final ElementWrapper elementParent, final Class<T> elementClass, final String... elementData) {
	return selectByOpeningElement(pattern, elementParent, (By)null /*findBy*/, elementClass, elementData);
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
 * @return The opened confirmation dialog as a subclass of {@link Page}.
 */
public <T extends ElementWrapper> T selectByOpeningElement(final String item, final By findBy, final Class<T> elementClass, final String... elementData) {
	return selectByOpeningElement(item, (ElementWrapper)null /*elementParent*/, findBy, elementClass, elementData);
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
 * @return The opened confirmation dialog as a subclass of {@link Page}.
 */
public <T extends ElementWrapper> T selectByOpeningElement(final String item, final Class<T> elementClass, final String... elementData) {
	return selectByOpeningElement(item, (ElementWrapper)null /*elementParent*/, (By)null /*findBy*/, elementClass, elementData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * an element.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param elementParent The parent element of the element to open by clicking on the link element..
 * If <code>null</code> then element is expected in the current page.
 * @param findBy The locator of the element opened after clicking on the link element.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link Page}.
 */
public <T extends ElementWrapper> T selectByOpeningElement(final String item, final ElementWrapper elementParent, final By findBy, final Class<T> elementClass, final String... elementData) {
	return selectByOpeningElement(Pattern.compile(Pattern.quote(item)), elementParent, findBy, elementClass, elementData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * an element.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param elementParent The parent element of the element to open by clicking on the link element..
 * If <code>null</code> then element is expected in the current page.
 * @param elementClass The class associated with the opened element.
 * @param elementData Additional information to store in the element when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link Page}.
 */
public <T extends ElementWrapper> T selectByOpeningElement(final String item, final ElementWrapper elementParent, final Class<T> elementClass, final String... elementData) {
	return selectByOpeningElement(item, elementParent, (By)null /*findBy*/, elementClass, elementData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link Page}.
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
 * @return The opened confirmation dialog as a subclass of {@link Page}.
 */
@SuppressWarnings("unchecked")
public <T extends Page> T selectByOpeningPage(final Pattern pattern, final Class<T> pageClass, final Action postLinkClickAction, final String... pageData) {
	BrowserElement menuItemElement = selectHelper(pattern);

	if(menuItemElement == null){
		return (T) getPage();
	}

	return openPageUsingLink(menuItemElement, pageClass, postLinkClickAction, pageData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link Page}.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param pattern The pattern to use to match the item to select.
 * @param pageClass A class representing the web page opened
 * after clicking on the particular menu item.
 * @param pageData Additional information to store in the page when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link Page}.
 */
public <T extends Page> T selectByOpeningPage(final Pattern pattern, final Class<T> pageClass, final String... pageData) {
	return selectByOpeningPage(pattern, pageClass, null /*postLinkClickAction*/, pageData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link Page}.
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
 * @return The opened confirmation dialog as a subclass of {@link Page}.
 */
public <T extends Page> T selectByOpeningPage(final String item, final Class<T> pageClass, final Action postLinkClickAction, final String... pageData) {
	return selectByOpeningPage(Pattern.compile(Pattern.quote(item)), pageClass, postLinkClickAction, pageData);
}

/**
 * Select a specific item (option) from the dropdown list element by opening
 * a {@link Page}.
 * <p>
 * A web page will automatically be opened after clicking on the particular
 * menu item.
 * </p>
 * @param item The item to select from the menu.
 * @param pageClass A class representing the web page opened
 * after clicking on the particular menu item.
 * @param pageData Additional information to store in the page when opening it.
 *
 * @return The opened confirmation dialog as a subclass of {@link Page}.
 */
public <T extends Page> T selectByOpeningPage(final String item, final Class<T> pageClass, final String... pageData) {
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
 * @return A list item matching the given as a {@link BrowserElement}.
 */
protected BrowserElement selectHelper(final Pattern pattern) {
	if (DEBUG) debugPrintln("		+ Select item matching pattern '"+ pattern.pattern() + "' from the drop down list");

	if(isSelected(pattern)) {
		debugPrintln("		  -> No need to select '" + pattern.pattern() + "' because it is already the currently selected item in the dropdown list.");
		return null;
	}

	return getOptionElement(pattern, true /*fail*/);
}

/**
 * Select a given option element.
 *
 * @param optionElement The option element to select.
 */
protected void selectOptionElement(final BrowserElement optionElement) {
//	optionElement.click();
	// The option element is not scrolled at times. Therefore, use the JavaScript method to click the option element to
	// select it since the particular method seems to perform the scroll operation properly.
	optionElement.clickViaJavaScript();
}
}
