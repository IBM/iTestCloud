/*********************************************************************
 * Copyright (c) 2018, 2023 IBM Corporation and others.
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
package com.ibm.itest.cloud.acme.pages.elements;

import org.openqa.selenium.By;

import com.ibm.itest.cloud.common.pages.Page;
import com.ibm.itest.cloud.common.pages.elements.*;

/**
 * This class represents a generic selection element such as a check-box and radio-button
 * and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #alter(boolean)}: Alter the selection status of the element.</li>
 * <li>{@link #isSelected()}: Determine whether or not this element is selected or not.</li>
 * <li>{@link #getLabelElementLocator(String, boolean)}:
 * Return the locator of a given label element of the selection element.</li>
 * <li>{@link #select()}: Select or check the given element.</li>
 * <li>{@link #selectByOpeningDialog(Class)}:
 * Select or check the element by opening a ApsPortalConfirmationDialog.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class AcmeSelectionElement extends SelectionElement {

public AcmeSelectionElement(final ElementWrapper parent, final By labelElementLocator) {
	super(parent, labelElementLocator);
}

public AcmeSelectionElement(final ElementWrapper parent, final String label) {
	super(parent, label);
}

public AcmeSelectionElement(final ElementWrapper parent, final BrowserElement labelElement) {
	super(parent, labelElement);
}

public AcmeSelectionElement(final ElementWrapper parent, final BrowserElement element, final BrowserElement labelElement) {
	super(parent, element, labelElement);
}

public AcmeSelectionElement(final Page page, final By labelElementLocator) {
	super(page, labelElementLocator);
}

public AcmeSelectionElement(final Page page, final String label) {
	super(page, label);
}

public AcmeSelectionElement(final Page page, final BrowserElement labelElement) {
	super(page, labelElement);
}

public AcmeSelectionElement(final Page page, final BrowserElement element, final BrowserElement labelElement) {
	super(page, element, labelElement);
}
}
