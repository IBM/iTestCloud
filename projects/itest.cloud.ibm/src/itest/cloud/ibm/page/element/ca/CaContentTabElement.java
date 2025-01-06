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
package itest.cloud.ibm.page.element.ca;

import static itest.cloud.scenario.ScenarioUtil.PATH_SEPARATOR;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.ca.CaContentPage;
import itest.cloud.ibm.page.element.IbmTabElement;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.ScenarioUtil;

/**
 * This class represents a tab element in {@link CaContentPage} and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getContentElement(String, boolean)}: Get a specific content element at a given path.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * Reflection would be used to create a page object based on the following constructors:
 * <ul>
 * </ul>
 * </p>
 */
public class CaContentTabElement extends IbmTabElement {

public CaContentTabElement(final Page page) {
	super(page);
}

///**
// * Return the breadcrumb element at a given index.
// *
// * @param index The zero-based index of the breadcrumb element.
// * If a negative value is provided for this parameter, the last breadcrumb element
// * will be returned.
// * @param fail Specifies whether to fail if a breadcrumb element is unavailable
// * at the given index.
// *
// * @return The breadcrumb element at a given index as {@link BrowserElement} or
// * <code>null</code> if a breadcrumb element is unavailable at the given index and
// * asked not to fail.
// */
//private BrowserElement getBreadcrumbElement(final int index, final boolean fail) {
//	return waitForElement(getBreadcrumbElementLocator(index), fail? timeout() : tinyTimeout(), fail);
//}
//
///**
// * Return the breadcrumb element locator at a given index.
// *
// * @param index The zero-based index of the breadcrumb element.
// * If a negative value is provided for this parameter, the last breadcrumb element locator
// * will be returned.
// *
// * @return The breadcrumb element locator at a given index as {@link By}.
// */
//private By getBreadcrumbElementLocator(final int index) {
//	return By.xpath(".//*[contains(@class,'breadcrumbItem')][" + (index + 1) + "]//*[contains(@class,'_text')]");
//}
//
///**
// * Return the breadcrumb text at a given index.
// *
// * @param index The zero-based index of the breadcrumb element.
// * If a negative value is provided for this parameter, the text of the
// * last breadcrumb element will be returned.
// *
// * @return The breadcrumb text at the given index as {@link String}.
// */
//private String getBreadcrumbText(final int index, final boolean fail) {
//	return getBreadcrumbElement(index, fail).getText();
//}

/**
 * Get a specific content element at a given path.
 * <p>
 * If the path contains multiple sections, they must be separated by the character '/' {@link ScenarioUtil#PATH_SEPARATOR},
 * The following are some example paths.
 * <ul>
 * <li>Samples</li>
 * <li>Team content/Samples/* Get started/My first report</li>
 * </ul>
 * </p>
 *
 * @param path The path to the content element as {@link String}.
 */
public CaContentElement getContentElement(final String path, final boolean fail) {
	final String[] pathItems = path.split(PATH_SEPARATOR);
//	boolean checkbBreadcrumbs = true;

	for (int i = 0; i < pathItems.length - 1; i++) {
//		// If the desired path is already open or can be opened via bread crumbs item is not already open, open it now.
//		if(checkbBreadcrumbs) {
//			final String breadcrumbText = getBreadcrumbText(i, false /*fail*/);
//			// If the desired path item is already open, leave it open.
//			if((checkbBreadcrumbs == breadcrumbText.equals(pathItems[i]))) continue;
//		}
		final CaContentElement contentElement = getContentElementHelper(pathItems[i], fail);
		// If the desired path item does not exist and specified not to fail in this situation, return null.
		if(contentElement == null) return null;
		// If the desired path item is not already open, open it now.
		contentElement.open();
	}
	// Return the content element representing the leaf item of the given path.
	return getContentElementHelper(pathItems[pathItems.length - 1], fail);
}

private CaContentElement getContentElementHelper(final String name, final boolean fail) {
	final BrowserElement contentWebElement = waitForElement(
		By.xpath(".//*[contains(@class,'_asset')]//label[.//*[contains(@class,'_text') and (text()='" + name + "')]]"),
		fail ? timeout() : tinyTimeout(), fail);

	return (contentWebElement != null) ? new CaContentElement(this, contentWebElement, name) : null;
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

@Override
protected By getTitleElementLocator() {
	return null;
}
}