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
package itest.cloud.ibm.page.dialog.mobile;

import static itest.cloud.page.Page.NO_DATA;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.ca.mobile.CaMobilePage;
import itest.cloud.ibm.scenario.IbmScenarioUtil;
import itest.cloud.page.Page;
import itest.cloud.page.dialog.ConfirmationDialog;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents a native implementation of a confirmation dialog in Cognos Analytics Mobile Application and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #closeByOpeningMobilePage(Class, String...)}: Close the dialog by opening a web page.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getBusyIndicatorElementLocator()}: Return the xpaths of element indicating that the element is undergoing an operation (busy).</li>
 * <li>{@link #getCloseButton(boolean)}: Return the xpath of the button to close the window.</li>
 * <li>{@link #getContentElementLocator()}: Return the locator for the content element of the current dialog.</li>
 * <li>{@link #getPage()}: Return the web page from which the window has been opened.</li>
 * <li>{@link #getTitle(BrowserElement)}: Return the title from a given title element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current dialog.</li>
 * </ul>
 * </p>
 */
public abstract class CaMobileNativeConfirmationDialog extends ConfirmationDialog {

public CaMobileNativeConfirmationDialog(final Page page) {
	this(page, NO_DATA);
}

public CaMobileNativeConfirmationDialog(final Page page, final String... data) {
	super(page, By.xpath("//*[@*='input-modal']"), data);
}

/**
 * Close the dialog by opening a web page.
 *
 * @param pageClass A class representing the web page opened after closing the dialog as a {@link CaMobilePage}.
 * @param pageData Additional information to store in the page when opening it as {@link String}[].
 *
 * @return The opened web page as a subclass of {@link CaMobilePage}.
 */
public <T extends CaMobilePage> T closeByOpeningMobilePage(final Class<T> pageClass, final String... pageData) {
	return getPage().openMobilePageUsingLink(By.xpath(getCloseButton(true /*validate*/)), pageClass, pageData);
}

@Override
protected By getBusyIndicatorElementLocator() {
	return IbmScenarioUtil.getBusyIndicatorElementLocator(true /*relative*/);
}

@Override
protected String getCloseButton(final boolean validate) {
	return ".//*[contains(name(),'Button') and ./*[@*='" + (validate ? getPrimaryButtonText() : getSecondaryButtonText()) + "']]";
}

@Override
protected By getContentElementLocator(){
	// A content element does not exist in a native confirmation dialog.
	return null;
}

/**
 * {@inheritDoc}
 *
 * @return The page as a subclass of {@link CaMobilePage}.
 */
@Override
protected CaMobilePage getPage() {
	return (CaMobilePage) this.page;
}

@Override
protected String getTitle(final BrowserElement titleElement) {
	return titleElement.getTextAttribute();
}

@Override
protected By getTitleElementLocator() {
	return By.xpath("./*/*[contains(name(),'TextView')]");
}
}