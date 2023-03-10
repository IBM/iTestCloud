/*********************************************************************
 * Copyright (c) 2013, 2022 IBM Corporation and others.
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
package itest.cloud.pages.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import itest.cloud.pages.Page;
import itest.cloud.scenario.errors.ScenarioFailedError;

/**
 * This class represents a HTML dropdown list element and manages all its common actions.
 * <p>
 * The following is an example for such a dropdown list element</br>
 * <xmp>
 * <select name="cars" id="car-selector">
 *     <option value="">--Please choose an option--</option>
 *     <option value="toyota">Toyota</option>
 *     <option value="dodge">Dodge</option>
 *     <option value="bmw">BMW</option>
 * </select>
 * </xmp>
 * </p><p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class HtmlDropdownlistElement extends ElementWrapper {

	final Select select;

public HtmlDropdownlistElement(final ElementWrapper parent, final BrowserElement element) {
	super(parent, element);
	this.select = new Select(this.element);
}

public HtmlDropdownlistElement(final ElementWrapper parent, final By findBy) {
	super(parent, findBy);
	this.select = new Select(this.element);
}

public HtmlDropdownlistElement(final Page page, final BrowserElement element) {
	super(page, element);
	this.select = new Select(this.element);
}

public HtmlDropdownlistElement(final Page page, final By findBy) {
	super(page, findBy);
	this.select = new Select(this.element);
}

/**
 * Clear all selected entries. this is only valid when the SELECT supports multiple selections.
 */
public void deselectAll() {
	this.select.deselectAll();
}

/**
 * Return all elements list.
 *
 * @return The list of option as a {@link List} of {@link BrowserElement}.
 */
public List<BrowserElement> getAllElements() {
	return BrowserElement.getList(this.select.getOptions());
}

/**
 * Return the select labels list.
 *
 * @return The list of option as a {@link List} of {@link String}.
 */
public List<String> getAllLabels() {
	List<WebElement> options = this.select.getOptions();
	List<String> labels = new ArrayList<String>();
	for (WebElement option : options) {
		labels.add(option.getText());
	}
	return labels;
}

/**
 * Return the selected elements list.
 *
 * @return The list of option as a {@link List} of {@link BrowserElement}.
 */
public List<BrowserElement> getAllSelectedElements() {
	return BrowserElement.getList(this.select.getAllSelectedOptions());
}

/**
 * Return the select labels list.
 *
 * @return The list of option as a {@link List} of {@link String}.
 */
public List<String> getAllValues() {
	List<WebElement> options = this.select.getOptions();
	List<String> values = new ArrayList<String>();
	for (WebElement option : options) {
		values.add(option.getAttribute("value"));
	}
	return values;
}

/**
 * Return the WebBrowserElement specified by the option label.
 *
 * @param option A {@link String} that represents the option to look for.
 * @return A {@link BrowserElement} identified by the option label specified. Returns null if the element is not found.
 */
public BrowserElement getOptionElement(final String option) {
	for (BrowserElement optionElement : getAllElements()) {
		if (optionElement.getText().equals(option)) {
			return optionElement;
		}
	}
	return null;
}

/**
 * Return the selected element.
 *
 * @return The first selected option as a {@link BrowserElement}.
 */
public BrowserElement getSelectedElement() {
	return (BrowserElement) this.select.getFirstSelectedOption();
}

/**
 * Return the label of the selected option.
 *
 * @return The selected option label as a {@link String}.
 */
@Override
public String getText() {
	return getSelectedElement().getText();
}

/**
 * Checks if a specific option is available for selection using its label.
 *
 * @param option A {@link String} that represents the option to look for.
 * @return <code>true</code> if the option is found; <code>false</code> otherwise.
 */
public boolean hasOption(final String option) {
	return getAllLabels().contains(option);
}

/**
 * Checks if a specific option is available for selection using its value.
 *
 * @param value A {@link String} that represents the option value to look for.
 * @return <code>true</code> if the option is found; <code>false</code> otherwise.
 */
public boolean hasValue(final String value) {
	return getAllValues().contains(value);
}

/**
 * Selects an option matching a given pattern.
 *
 * @param pattern the pattern to match the option.
 *
 * @throws ScenarioFailedError if the option is not found or if it's found but disabled.
 */
public void select(final Pattern pattern) throws ScenarioFailedError {
	// Loop trough the option to compare them to the option with the given pattern
	List<WebElement> optionElements = this.select.getOptions();

	for (int i = 0; i < optionElements.size(); i++) {
		WebElement optionElement = optionElements.get(i);
		String optionText = optionElement.getText();

		if (pattern.matcher(optionText).matches()) {
			// A matching option found.
			if (!optionElement.isEnabled()) {
				throw new ScenarioFailedError("Cannot select option '" + optionText + "' because it's disabled.");
			}
			// Select the option
			this.select.selectByIndex(i);
			// Return to caller.
			return;
		}
	}
	throw new ScenarioFailedError("An option matching pattern '" + pattern + "' was not found in dropdown list element " + this.element);
}

/**
 * Select the corresponding option using the given text.
 *
 * @param option The option to select in the list.
 * @throws NoSuchElementException If the option is not present in the list
 */
public void select(final String option) throws NoSuchElementException {
	this.select.selectByVisibleText(option);
}

/**
 * Select the corresponding option using the given value.
 *
 * @param value The value of the option to be selected in the list.
 */
public void selectByValue(final String value) {
	this.select.selectByValue(value);
}
}
