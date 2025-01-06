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
package itest.cloud.ibm.page.element.ca.mobile;

import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Action;

import itest.cloud.ibm.page.ca.mobile.CaMobilePage;
import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;

/**
 * This class wraps a web element in the Cognos Analytics Mobile Application and add some actions and functionalities
 * that anyone can use. It also add some specific operations only accessible to the class hierarchy.
 * <p>
 * The expansion and collapse are done using the avatar icon on page top right corner.
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #openMobilePageUsingLink(BrowserElement, Class, String...)}: Click on the given link assuming that will open a new page.</li>
 * <li>{@link #openMobilePageUsingLink(By, Class, String...)}: Click on the given link assuming that will open a new page.</li>
 * <li>{@link #openMobilePageUsingLink(Class, Action, String...)}: Click on the given link assuming that will open a new page.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getPage()}: Return the web page where the wrapped element resides.</li>
 * </ul>
 * </p>
 */
public abstract class CaMobileElementWrapper extends IbmElementWrapper {

public CaMobileElementWrapper(final Page page, final BrowserElement element) {
	super(page, element);
}

public CaMobileElementWrapper(final Page page, final BrowserElement element, final String... data) {
	super(page, element, data);
}

@Override
protected CaMobilePage getPage() {
	return (CaMobilePage) super.getPage();
}

/**
 * Click on the given link assuming that will open a new page.
 *
 * @param linkElement The link element on which to click as {@link BrowserElement}.
 * @param openedPageClass The class associated with the opened page as a {@link CaMobilePage}.
 * @param pageData Additional information to store in the page when opening it as an array of {@link String}s.
 *
 * @return The web page as a {@link CaMobilePage} which is opened after having clicked on the link.
 *
 * @see CaMobilePage#openMobilePageUsingLink(BrowserElement, Class, String...)
 */
public <P extends CaMobilePage> P openMobilePageUsingLink(final BrowserElement linkElement, final Class<P> openedPageClass, final String... pageData) {
	return getPage().openMobilePageUsingLink(linkElement, openedPageClass, pageData);
}

/**
 * Click on the given link assuming that will open a new page.
 *
 * @param linkBy The locator of the link element on which to click as {@link By}.
 * @param openedPageClass The class associated with the opened page as a {@link CaMobilePage}.
 * @param pageData Additional information to store in the page when opening it as an array of {@link String}s.
 *
 * @return The web page as a {@link CaMobilePage} which is opened after having clicked on the link.
 *
 * @see CaMobilePage#openMobilePageUsingLink(By, Class, String...)
 */
public <P extends CaMobilePage> P openMobilePageUsingLink(final By linkBy, final Class<P> openedPageClass, final String... pageData) {
	return getPage().openMobilePageUsingLink(linkBy, openedPageClass, pageData);
}

/**
 * Click on the given link assuming that will open a new page.
 *
 * @param openedPageClass The class associated with the opened page as a {@link CaMobilePage}.
 * @param clickAction The action that performs the clicking of the link as a {@link Action}.
 * @param pageData Additional information to store in the page when opening it as an array of {@link String}s.
 *
 * @return The web page as a {@link CaMobilePage} which is opened after having clicked on the link.
 *
 * @see CaMobilePage#openMobilePageUsingLink(Class, Action, String...)
 */
public <P extends CaMobilePage> P openMobilePageUsingLink(final Class<P> openedPageClass, final Action clickAction, final String... pageData) {
	return getPage().openMobilePageUsingLink(openedPageClass, clickAction, pageData);
}
}