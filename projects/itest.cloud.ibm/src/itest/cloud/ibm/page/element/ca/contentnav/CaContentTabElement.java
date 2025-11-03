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
package itest.cloud.ibm.page.element.ca.contentnav;

import static itest.cloud.ibm.entity.ca.CaAssetType.FOLDER;
import static itest.cloud.ibm.entity.ca.CaAssetType.REPORT;
import static itest.cloud.scenario.ScenarioUtil.println;
import static itest.cloud.scenario.ScenarioUtil.splitPath;

import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import itest.cloud.ibm.entity.ca.CaAssetType;
import itest.cloud.ibm.page.ca.CaPage;
import itest.cloud.ibm.page.ca.contentnav.CaContentPage;
import itest.cloud.ibm.page.ca.reporting.CaReportPage;
import itest.cloud.ibm.page.element.IbmTabElement;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;
import itest.cloud.scenario.ScenarioUtil;
import itest.cloud.scenario.error.ScenarioFailedError;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * This class represents a tab element in {@link CaContentPage} and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #editRport(String)}: Edit a specific Report at a given path.</li>
 * <li>{@link #openRport(String)}: Open a specific Report at a given path.</li>
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
	super(page, By.id(data[0]));
}

/**
 * Edit a specific Report at a given path.
 * <p>
 * If the path contains multiple sections, they must be separated by the character '/' {@link ScenarioUtil#PATH_SEPARATOR},
 * The following are some example paths.
 * <ul>
 * <li>Samples/* Get started/My first report</li>
 * </ul>
 * </p>
 *
 * @param path The path to the Report as {@link String}.
 *
 * @return The opened Report Page as {@link CaPage}).
 */
public CaReportPage editRport(final String path) {
	final CaContentReportElement reportElement = (CaContentReportElement) getAssetElement(path, REPORT /*assetType*/, true /*fail*/);
	return reportElement.edit();
}

/**
 * Get a specific asset at a given path.
 * <p>
 * If the path contains multiple sections, they must be separated by the character '/' {@link ScenarioUtil#PATH_SEPARATOR},
 * The following are some example paths.
 * <ul>
 * <li>Samples</li>
 * <li>Samples/* Get started/My first report</li>
 * </ul>
 * </p>
 *
 * @param path The path to the asset as {@link String}.
 * @param assetType The type of the asset as {@link CaAssetType}.
 */
private CaContentAssetElement getAssetElement(final String path, final CaAssetType assetType, final boolean fail) {
	final String[] pathItems = splitPath(path);
	boolean useBreadcrumbsForNavigation = true;
	// Open the hierarchy of folders containing the asset element as specified in the path.
	for (int i = 0; i < pathItems.length - 1; i++) {
		// Try to use bread crumbs for navigation if possible.
		if(useBreadcrumbsForNavigation) {
			// Check if a bread crumb exists for the folder under consideration. The first bread crumb would always
			// be the tab name. Therefore, check from the second bread crumb onwards.
			final String breadcrumbText = getBreadcrumbText(i+1, false /*fail*/);
			if(breadcrumbText != null) {
				// If a bread crumb is found, check if it matches the folder under consideration.
				if(breadcrumbText.equals(pathItems[i])) {
					// If a matching bread crumb is found, it implies that the folder is already open.
					// Therefore, leave it as-is.
					continue;
				}
				// If a matching bread crumb is not found, open the parent folder via the appropriate bread crumb.
				final BrowserElement previousBreadcrumbElement = getBreadcrumbElement(i, true /*fail*/);
				previousBreadcrumbElement.click();
			}
			// No more bread crumbs can exist at this point. Therefore, stop considering the use of bread crumbs
			// for navigation from now on.
			useBreadcrumbsForNavigation = false;
		}
		// Look for the folder under consideration.
		final CaContentAssetElement assetElement = getAssetElementHelper(pathItems[i], FOLDER /*assetType*/, fail);
		// If the desired folder not exist and specified not to fail in this situation, return null.
		if(assetElement == null) return null;
		// Open the folder under consideration.
		((CaContentFolderElement) assetElement).open();
		// Check whether a new bread crumb has just been added after opening the folder.
		// The first bread crumb would always be the tab name. Therefore, check from the second bread crumb onwards.
		final String breadcrumbText = getBreadcrumbText(i+1, true /*fail*/);
		// The newly added bred crumb should be named after the folder.
		if(!breadcrumbText.equals(pathItems[i])) {
			throw new WaitElementTimeoutError("The bread crumb that got added after opening the folder '" + pathItems[i] + "' had the incorrect label '" + breadcrumbText + "'.");
		}
		// The header title of the Content Page should now be named after the folder as well.
		final String headerTitle = getPage().getHeaderTitle();
		if(!headerTitle.equals(pathItems[i])) {
			throw new WaitElementTimeoutError("The header title of the Content Page was expected to be named after the opened folder '" + pathItems[i] + "', but it it was incorrectly named '" + headerTitle + "' instead.");
		}
	}
	// Return the asset element representing the leaf item of the given path.
	return getAssetElementHelper(pathItems[pathItems.length - 1], assetType, fail);
}

@Override
protected CaContentPage getPage() {
	return (CaContentPage) super.getPage();
}

private CaContentAssetElement getAssetElementHelper(final String name, final CaAssetType assetType, final boolean fail) {
	final BrowserElement assetWebElement = waitForElement(
		By.xpath(".//*[contains(@class,'_asset')]//label[.//*[@aria-label='" + name + "'] and .//*[(name()='svg') and (@title='" + assetType.getLabel() + "')]]"),
		fail ? timeout() : tinyTimeout(), fail);

	if (assetWebElement == null) return null;

	// Instantiate the the asset element.
	try {
		return assetType.getContentAssetElementClass().
			getConstructor(ElementWrapper.class, BrowserElement.class, String[].class).
			newInstance(this, assetWebElement, new String[] {name});
	}
	catch (WebDriverException e) {
		throw e;
	}
	catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
		println("Exception cause: " + e.getCause());
		throw new ScenarioFailedError(e);
	}
	catch (Throwable e) {
		println("Exception cause: " + e.getCause());
		throw new WaitElementTimeoutError(e);
	}
}

/**
 * Return the bread crumb element at a given index.
 *
 * @param index The zero-based index of the bread crumb element.
 * If a negative value is provided for this parameter, the last bread crumb element
 * will be returned.
 * @param fail Specifies whether to fail if a bread crumb element is unavailable
 * at the given index.
 *
 * @return The bread crumb element at a given index as {@link BrowserElement} or
 * <code>null</code> if a bread crumb element is unavailable at the given index and
 * asked not to fail.
 */
private BrowserElement getBreadcrumbElement(final int index, final boolean fail) {
	return waitForElement(getBreadcrumbElementLocator(index), fail ? timeout() : tinyTimeout(), fail);
}

/**
 * Return the bread crumb element locator at a given index.
 *
 * @param index The zero-based index of the bread crumb element.
 * If a negative value is provided for this parameter, the last bread crumb element locator
 * will be returned.
 *
 * @return The bread crumb element locator at a given index as {@link By}.
 */
private By getBreadcrumbElementLocator(final int index) {
	return By.xpath(".//*[contains(@class,'breadcrumbItem')][" + (index + 1) + "]//*[contains(@class,'_text')]");
}

/**
 * Return the bread crumb text at a given index.
 *
 * @param index The zero-based index of the bread crumb element.
 * If a negative value is provided for this parameter, the text of the
 * last bread crumb element will be returned.
 *
 * @return The bread crumb text at the given index as {@link String}.
 */
private String getBreadcrumbText(final int index, final boolean fail) {
	final BrowserElement breadcrumbElement = getBreadcrumbElement(index, fail);
	return (breadcrumbElement != null) ? breadcrumbElement.getText() : null;
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

@Override
protected By getTitleElementLocator() {
	return null;
}

/**
 * Open a specific Report at a given path.
 * <p>
 * If the path contains multiple sections, they must be separated by the character '/' {@link ScenarioUtil#PATH_SEPARATOR},
 * The following are some example paths.
 * <ul>
 * <li>Samples/* Get started/My first report</li>
 * </ul>
 * </p>
 *
 * @param path The path to the Report as {@link String}.
 *
 * @return The opened Report Page as {@link CaPage}).
 */
public CaReportPage openRport(final String path) {
	final CaContentReportElement reportElement = (CaContentReportElement) getAssetElement(path, REPORT /*assetType*/, true /*fail*/);
	return reportElement.open();
}
}