/*********************************************************************
 * Copyright (c) 2017, 2023 IBM Corporation and others.
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

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.page.Page;

/**
 * This class represents a generic dropdown list element that allowing multiple selections of
 * its items.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #collapse()}: Collapse the current web element.</li>
 * <li>{@link #select(Pattern[])}:
 * Select a list of items (options) matching a given list of patterns from the dropdown list.</li>
 * <li>{@link #select(String[])}: Select a given list of items (options) from the dropdown list.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class MultiSelectDropdownlistElement extends DropdownlistElement {

public MultiSelectDropdownlistElement(final ElementWrapper parent, final By findBy, final By expansionLocator, final By selectionLocator, final By optionLocator) {
	super(parent, findBy, expansionLocator, selectionLocator, optionLocator);
}

public MultiSelectDropdownlistElement(final Page page, final By findBy, final By expansionLocator, final By selectionLocator, final By optionLocator) {
	super(page, findBy, expansionLocator, selectionLocator, optionLocator);
}

public MultiSelectDropdownlistElement(final Page page, final BrowserElement webElement, final By expansionLocator, final By selectionLocator, final By optionLocator) {
	super(page, webElement, expansionLocator, selectionLocator, optionLocator);
}

@Override
protected boolean isSelected(final Pattern pattern) {
	String selection = getSelection();
	if(selection == null) return false;

	String[] selections = getSelection().split(",");
	for (String aSelection : selections) {
		if(pattern.matcher(aSelection.trim()).matches()) return true;
	}
	return false;
}

/**
 * Select a list of items (options) matching a given list of patterns from the dropdown list.
 *
 * @param patterns The list of patterns to use to match the items to select.
 */
public void select(final Pattern[] patterns) {
	for (Pattern pattern : patterns) {
		select(pattern);
	}
	// Collapse the list since it may not be done by default.
	collapse();
}

/**
 * Select a given list of items (options) from the dropdown list.
 *
 * @param items The list of items to select.
 */
public void select(final String[] items) {
	for (String item : items) {
		select(item);
	}
	// Collapse the list since it may not be done by default.
	collapse();
}
}
