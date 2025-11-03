/*********************************************************************
 * Copyright (c) 2024, 2025 IBM Corporation and others.
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

import static itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioUtil.getListItemLocator;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;
import itest.cloud.ibm.page.ca.contentnav.CaContentPage;
import itest.cloud.ibm.page.ca.mobile.CaMobilePage;
import itest.cloud.ibm.page.ca.mobile.ContentNavigationPage;
import itest.cloud.ibm.page.element.IbmTabElement;
import itest.cloud.page.Page;

/**
 * This class represents a tab element in {@link CaContentPage} and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #openAsset(String, Class)}: Open a given asset in the tab.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getPage()}: Return the web page where the element resides.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * Reflection would be used to create a page object based on the following constructors:
 * <ul>
 * </ul>
 * </p>
 */
public class CaContentTabElement extends IbmTabElement {

public CaContentTabElement(final Page page, final String... data) {
	super(page, AppiumBy.accessibilityId(data[0] + "-tab"));
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

@Override
protected ContentNavigationPage getPage() {
	return (ContentNavigationPage) super.getPage();
}

@Override
protected By getTitleElementLocator() {
	return null;
}

/**
 * Open a given asset in the tab.
 *
 * @param name The name of the asset as {@link String}.
 * @param openedPageClass The class associated with the opened page as a {@link CaMobilePage}.
 *
 * @return The web page opened after clicking on the link as a {@link CaMobilePage}.
 */
public <P extends CaMobilePage> P openAsset(final String name, final Class<P> openedPageClass) {
	final ContentNavigationPage parentPage = getPage();
	parentPage.search(name);
	return parentPage.openMobilePageUsingLink(waitForElement(getListItemLocator(name)), openedPageClass, name);
}
}