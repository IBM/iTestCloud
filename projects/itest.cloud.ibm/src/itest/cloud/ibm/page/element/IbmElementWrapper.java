/*********************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
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
package itest.cloud.ibm.page.element;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.IbmPage;
import itest.cloud.ibm.scenario.IbmScenarioUtil;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class wraps a web element in an IBM application and add some actions and functionalities
 * that anyone can use. It also add some specific operations only accessible to the class hierarchy.
 * <p></p>
 * Following public features are accessible from this class:
 * <ul>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getBusyIndicatorElementLocator()}:
 * Return the xpaths of element indicating that the element is undergoing an operation (busy).</li>
 * <li>{@link #getPage()}: Return the web page where the wrapped element resides.</li>
 * </ul>
 * </p>
 */
public abstract class IbmElementWrapper extends ElementWrapper {

public IbmElementWrapper(final ElementWrapper parent) {
	super(parent);
}

public IbmElementWrapper(final ElementWrapper parent, final BrowserElement element) {
	super(parent, element);
}

public IbmElementWrapper(final ElementWrapper parent, final BrowserElement element, final String... data) {
	super(parent, element, data);
}

public IbmElementWrapper(final ElementWrapper parent, final By findBy) {
	super(parent, findBy);
}

public IbmElementWrapper(final ElementWrapper parent, final By findBy, final String... data) {
	super(parent, findBy, data);
}

public IbmElementWrapper(final ElementWrapper parent, final String... data) {
	super(parent, data);
}

public IbmElementWrapper(final Page page) {
	super(page);
}

public IbmElementWrapper(final Page page, final BrowserElement element) {
	super(page, element);
}

public IbmElementWrapper(final Page page, final BrowserElement element, final String... data) {
	super(page, element, data);
}

public IbmElementWrapper(final Page page, final By findBy) {
	super(page, findBy);
}

public IbmElementWrapper(final Page page, final By findBy, final String... data) {
	super(page, findBy, data);
}

public IbmElementWrapper(final Page page, final String... data) {
	super(page, data);
}

@Override
protected By getBusyIndicatorElementLocator() {
	return IbmScenarioUtil.getBusyIndicatorElementLocator(true /*relative*/);
}

/**
 * {@inheritDoc}
 *
 * @return The page as a subclass of {@link IbmPage}.
 */
@Override
protected IbmPage getPage() {
	return (IbmPage) this.page;
}
}