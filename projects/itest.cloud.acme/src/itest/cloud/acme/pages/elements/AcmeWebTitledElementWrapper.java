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
 * This class represents a generic titled web element and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #waitForLoadingEnd()}: Wait for loading of the tab to complete.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getPage()}: Return the web page from which the window has been opened.</li>
 * </ul>
 * </p>
 */
public abstract class AcmeWebTitledElementWrapper extends TitledElementWrapper {

public AcmeWebTitledElementWrapper(final ElementWrapper parent, final By findBy) {
	super(parent, findBy);
}

public AcmeWebTitledElementWrapper(final ElementWrapper parent, final By findBy, final String... data) {
	super(parent, findBy, data);
}

public AcmeWebTitledElementWrapper(final Page page, final By findBy) {
	super(page, findBy);
}

public AcmeWebTitledElementWrapper(final Page page, final By findBy, final String... data) {
	super(page, findBy, data);
}

public AcmeWebTitledElementWrapper(final Page page, final BrowserElement element) {
	super(page, element);
}

public AcmeWebTitledElementWrapper(final Page page, final BrowserElement element, final String... data) {
	super(page, element, data);
}

@Override
protected AcmeAbstractWebPage getPage() {
	return (AcmeAbstractWebPage)super.getPage();
}

/**
 * Wait for loading of the tab to complete.
 */
@Override
public void waitForLoadingEnd() {
	// Wait for loading to finish.
	getPage().waitWhileBusy();

	super.waitForLoadingEnd();
}
}
