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

import java.lang.reflect.InvocationTargetException;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.IbmPage;
import itest.cloud.ibm.page.wxbia.WxbiPage;
import itest.cloud.ibm.scenario.IbmScenarioUtil;
import itest.cloud.page.Page;
import itest.cloud.page.element.ElementWrapper;
import itest.cloud.page.element.TabElement;

/**
 * This class represents a generic tab and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * Reflection would be used to create a page object based on the following constructors:
 * <ul>
 * <li>{@link #getBusyIndicatorElementLocator()}:
 * Return the xpaths of element indicating that the element is undergoing an operation (busy).</li>
 * <li>{@link #getPage()}: Return the web page where the element resides.</li>
 * </ul>
 * </p>
 */
public abstract class IbmTabElement extends TabElement {

/**
 * Initializes a newly created {@code ApsPortalTab} object so that it represents
 * a tab element.
 *
 * @param parent The class that represents the parent element containing the tab element.
 * @param findBy The xpath to locate the tab element in the given page. The provided xpath
 * must locate a tab element in the page. An {@link InvocationTargetException} will be raised
 * otherwise.
 */
public IbmTabElement(final ElementWrapper parent, final By findBy) {
	super(parent, findBy);
}

/**
 * Initializes a newly created {@code ApsPortalTab} object so that it represents
 * a tab element.
 *
 * @param parent The class that represents the parent element containing the tab element.
 * @param findBy The xpath to locate the tab element in the given page. The provided xpath
 * must locate the tab element in the page. An {@link InvocationTargetException} will be raised
 * otherwise.
 * @param data A set of data to be passed to the tab element for initialization.
 * This data must be dynamic and change when the same tab element is opened in different
 * circumstances. Any static data must not be passed to the tab element via this parameter.
 * The static data must be hard-coded in the tab element class instead. For example, if the
 * title of the tab element changes when it is opened in different circumstances, then
 * the particular changing title must be passed via this parameter. If the title is constant
 * on the other hand, then it should be hard-coded in {@link #getExpectedTitle()} method.
 */
public IbmTabElement(final ElementWrapper parent, final By findBy, final String... data) {
	super(parent, findBy, data);
}

/**
 * Initializes a newly created {@code ApsPortalTab} object so that it represents
 * a tab element.
 *
 * @param page The class that represents the page containing the tab element.
 */
public IbmTabElement(final Page page) {
	super(page, By.xpath("//*[@role='tabpanel']"));
}

/**
 * Initializes a newly created {@code ApsPortalTab} object so that it represents
 * a tab element.
 *
 * @param page The class that represents the page containing the tab element.
 * @param findBy The xpath to locate the tab element in the given page. The provided xpath
 * must locate a tab element in the page. An {@link InvocationTargetException} will be raised
 * otherwise.
 */
public IbmTabElement(final Page page, final By findBy) {
	super(page, findBy);
}

/**
 * Initializes a newly created {@code ApsPortalTab} object so that it represents
 * a tab element.
 *
 * @param page The class that represents the page containing the tab element.
 * @param findBy The xpath to locate the tab element in the given page. The provided xpath
 * must locate the tab element in the page. An {@link InvocationTargetException} will be raised
 * otherwise.
 * @param data A set of data to be passed to the tab element for initialization.
 * This data must be dynamic and change when the same tab element is opened in different
 * circumstances. Any static data must not be passed to the tab element via this parameter.
 * The static data must be hard-coded in the tab element class instead. For example, if the
 * title of the tab element changes when it is opened in different circumstances, then
 * the particular changing title must be passed via this parameter. If the title is constant
 * on the other hand, then it should be hard-coded in {@link #getExpectedTitle()} method.
 */
public IbmTabElement(final Page page, final By findBy, final String... data) {
	super(page, findBy, data);
}

@Override
protected By getBusyIndicatorElementLocator() {
	return IbmScenarioUtil.getBusyIndicatorElementLocator(true /*relative*/);
}

/**
 * {@inheritDoc}
 *
 * @return The page as a subclass of {@link WxbiPage}.
 */
@Override
protected IbmPage getPage() {
	return (IbmPage) this.page;
}
}