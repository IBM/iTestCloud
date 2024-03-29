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
package itest.cloud.acme.pages.elements;

import org.openqa.selenium.By;

import itest.cloud.acme.pages.AcmeAbstractWebPage;
import itest.cloud.pages.Page;
import itest.cloud.pages.elements.*;

/**
 * This class represents a generic web element and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getPage()}: Return the web page from which the window has been opened.</li>
 * </ul>
 * </p>
 */
public abstract class AcmeAbstractExpandableElement extends ExpandableElement {

public AcmeAbstractExpandableElement(final ElementWrapper parent, final By locator) {
	super(parent, locator);
}

public AcmeAbstractExpandableElement(final ElementWrapper parent, final By locator, final By expansionLocator) {
	super(parent, locator, expansionLocator);
}

public AcmeAbstractExpandableElement(final ElementWrapper parent, final BrowserElement webElement, final By expansionLocator) {
	super(parent, webElement, expansionLocator);
}

public AcmeAbstractExpandableElement(final Page page, final By locator) {
	super(page, locator);
}

public AcmeAbstractExpandableElement(final Page page, final By locator, final By expansionLocator) {
	super(page, locator, expansionLocator);
}

public AcmeAbstractExpandableElement(final Page page, final BrowserElement webElement) {
	super(page, webElement);
}

public AcmeAbstractExpandableElement(final Page page, final BrowserElement webElement, final By expansionLocator) {
	super(page, webElement, expansionLocator);
}

@Override
protected AcmeAbstractWebPage getPage() {
	return (AcmeAbstractWebPage)super.getPage();
}
}
