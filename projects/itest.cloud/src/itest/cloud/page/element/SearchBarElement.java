/*********************************************************************
 * Copyright (c) 2016, 2023 IBM Corporation and others.
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

import static itest.cloud.scenario.ScenarioUtil.println;
import static itest.cloud.scenario.ScenarioUtil.sleep;
import static itest.cloud.util.ByUtils.toRelativeLocator;

import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import itest.cloud.page.Page;

/**
 * This class represents a generic search bar element used to perform a search.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #resetFilter()}: Reset the search filter.</li>
 * <li>{@link #search(String)}: Search for a given text.</li>
 * <li>{@link #search(String, Keys)}: Search for a given text.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * <li>{@link #isDisabled()}: Specifies whether the search bar is disabled.</li>
 * </ul>
 * </p>
 */
public class SearchBarElement extends ElementWrapper {

	private static final String CLOSE_BUTTON_LOCATOR = "//button[contains(@class,'search-close')]";
	private static final String INPUT_ELEMENT_LOCATOR = "//*[contains(@class,'search-input')]";

public static By getCloseButtonLocator(final boolean relative) {
	return relative ? toRelativeLocator(CLOSE_BUTTON_LOCATOR) : By.xpath(CLOSE_BUTTON_LOCATOR);
}

private static By getSearchInputElementLocator(final boolean relative) {
	return relative ? toRelativeLocator(INPUT_ELEMENT_LOCATOR) : By.xpath(INPUT_ELEMENT_LOCATOR);
}

	private final By inputElementLocator, closeButtonLocator;

public SearchBarElement(final ElementWrapper parent) {
	this(parent, getSearchInputElementLocator(true /*relative*/), getCloseButtonLocator(true /*relative*/));
}

public SearchBarElement(final ElementWrapper parent, final By inputElementLocator, final By closeButtonLocator) {
	super(parent);
	this.inputElementLocator = inputElementLocator;
	this.closeButtonLocator = closeButtonLocator;
}

public SearchBarElement(final Page page) {
	this(page, getSearchInputElementLocator(false /*relative*/), getCloseButtonLocator(false /*relative*/));
}

public SearchBarElement(final Page page, final By inputElementLocator, final By closeButtonLocator) {
	super(page);
	this.inputElementLocator = inputElementLocator;
	this.closeButtonLocator = closeButtonLocator;
}

private BrowserElement getCloseButtonElement() {
	return waitForElement(getParentElement(), this.closeButtonLocator, tinyTimeout(), false /*fail*/);
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

private BrowserElement getSearchInputElement() {
	return waitForElement(getParentElement(), this.inputElementLocator, tinyTimeout(), false /*fail*/);
}

@Override
protected By getTitleElementLocator() {
	return null;
}

/**
 * Specifies whether the search bar is disabled.
 *
 * @return <code>true</code> if the search bar is disabled or <code>false</code> otherwise.
 */
protected boolean isDisabled() {
	BrowserElement inputElement = getSearchInputElement();
	if(inputElement == null) return false;

	String disabledAttribute = inputElement.getAttribute("disabled");
	return !inputElement.isEnabled() || ((disabledAttribute != null) && Boolean.parseBoolean(disabledAttribute));
}

/**
 * Reset the search filter.
 */
public void resetFilter() {
	BrowserElement inputElement = getSearchInputElement();
	if(inputElement == null) {
		println("	  -> Search bar element was unavailable. Therefore, no attempt was made to reset filter.");
		return;
	}

	if(isDisabled()) {
		println("	  -> Search bar element was disabled. Therefore, no attempt was made to reset filter.");
		return;
	}

	BrowserElement searchCloseButton = getCloseButtonElement();
	if(searchCloseButton == null) {
		println("	  -> Reset button element was unavailable. Therefore, no attempt was made to reset filter.");
		return;
	}

	searchCloseButton.click();
	// Wait for the tab contents to load.
	if(getParent() != null) getParent().waitWhileBusy();
	else getPage().waitWhileBusy();
}

/**
 * Search for a given text.
 *
 * @param text The text to search for as {@link String}.
 */
public void search(final String text) {
	search(text, null /*key*/);
}

/**
 * Search for a given text.
 *
 * @param text The text to search for as {@link String}.
 * @param key The key to hit after having entered the text in the search input field.
 * If null is provided as the value of this parameter, a key will not be hit after having entered the text in the input field.
 */
public void search(final String text, final Keys key) {
	BrowserElement inputElement = getSearchInputElement();
	if(inputElement == null) {
		println("	  -> Search bar element was unavailable. Therefore, no attempt was made to search given text '" + text + "'.");
		return;
	}

	if(isDisabled()) {
		println("	  -> Search bar element was disabled. Therefore, no attempt was made to search given text '" + text + "'.");
		return;
	}

	String searchBarText = inputElement.getAttribute("value");
	// Check if the search bar already has the given text.
	if(text.equals(searchBarText)) {
		// If so, do nothing as the desired search has already been done.
		return;
	}
	// If reached here, the requested search must be performed.
	// Enter the search text.
	typeText(inputElement, text, key);
	// Pause a moment.
	sleep(1);
	// Wait for the searching to finish.
	if(getParent() != null) getParent().waitWhileBusy();
	else getPage().waitWhileBusy();
}
}