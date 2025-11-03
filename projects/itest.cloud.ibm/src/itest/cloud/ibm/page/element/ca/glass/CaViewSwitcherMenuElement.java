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
package itest.cloud.ibm.page.element.ca.glass;

import static itest.cloud.ibm.entity.ca.CaAssetType.ACTIVE_REPORT;
import static itest.cloud.ibm.entity.ca.CaAssetType.FOLDER;
import static itest.cloud.ibm.page.ca.contentnav.CaContentPage.CONTENT_PAGE_TITLE;
import static itest.cloud.scenario.ScenarioUtil.println;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import itest.cloud.ibm.entity.ca.CaAssetType;
import itest.cloud.ibm.page.ca.CaPage;
import itest.cloud.ibm.page.ca.contentnav.CaContentPage;
import itest.cloud.ibm.page.ca.reporting.CaReportPage;
import itest.cloud.ibm.page.element.IbmDynamicDropdownlistElement;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;
import itest.cloud.scenario.error.ScenarioFailedError;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * This class to represents the View Switcher menu element where the element is only made available after clicking on the expansion element.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #isAssetAvailable(String, CaAssetType)}: Specifies whether a given asset is available in this menu.</li>
 * <li>{@link #openContentPageIfAvailable()}: Open the Content page from this menu if it is available in the menu.</li>
 * <li>{@link #openRportIfAvailable(String)}: Open a given Report from this menu if it is available in the menu.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class CaViewSwitcherMenuElement extends IbmDynamicDropdownlistElement {

	public static final By VIEW_SWITCHER_MENU_LOCATOR = By.xpath("//*[contains(@class,'commonMenuActive')]");
	private static final String VIEW_SWITCHER_XPATH_PREFIX = "//*[@id='com.ibm.bi.glass.common.viewSwitcher']";
	public static final By VIEW_SWITCHER_TITLE_LOCATOR = By.xpath(VIEW_SWITCHER_XPATH_PREFIX + "//*[contains(@class,'switcherTitle')]");

public CaViewSwitcherMenuElement(final Page page) {
	super(page, VIEW_SWITCHER_MENU_LOCATOR, By.xpath(VIEW_SWITCHER_XPATH_PREFIX + "//*[name()='svg'][2]"),
		VIEW_SWITCHER_TITLE_LOCATOR, By.xpath(".//*[contains(@class,'commonMenuLink')]/span"));
}

private CaSwitcherAssetElement getAssetElement(final String name, final CaAssetType assetType, final boolean fail) {
	// Expand the View Switcher menu.
	expand();
	// Look for the asset element in the menu.
	final BrowserElement assetWebElement = waitForElement(
		By.xpath(".//*[(@role='listitem') and (@aria-label='" + name + "') and .//*[(name()='use') and contains(@*[name()='xlink:href'],'" + assetType.getSwitcherId() + "')]]"),
		fail ? timeout() : tinyTimeout(), fail);

	if (assetWebElement == null) return null;

	// Instantiate the the asset element.
	try {
		return assetType.getSwitcherAssetElementClass().
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
 * Specifies whether a given asset is available in this menu.
 *
 * @param name The name of the asset as {@link String}.
 * @param type The type of the asset as {@link CaAssetType}.
 *
 * @return <code>true</code> if the given asset is available in this menu or <code>false</code> otherwise.
 */
public boolean isAssetAvailable(final String name, final CaAssetType type) {
	final CaSwitcherAssetElement assetElement = getAssetElement(name, type, false /*fail*/);
	return (assetElement != null);
}

/**
 * Open the Content page from this menu if it is available in the menu.
 *
 * @return The opened Content Page as {@link CaContentPage}) or <code>null</code> if a matching Report could not be found in the menu.
 */
public CaContentPage openContentPageIfAvailable() {
	final CaSwitcherContentElement contentElement = (CaSwitcherContentElement) getAssetElement(CONTENT_PAGE_TITLE, FOLDER /*type*/, false /*fail*/);

	// If the Content page is unavailable in the menu, simply collapse the menu itself.
	if(contentElement == null) {
		collapse();
		return null;
	}

	return contentElement.open();
}

/**
 * Open a given Report from this menu if it is available in the menu.
 *
 * @param name The name of the Report as {@link String}.
 *
 * @return The opened Report Page as {@link CaPage}) or <code>null</code> if a matching Report could not be found in the menu.
 */
public CaReportPage openRportIfAvailable(final String name) {
	final CaSwitcherReportElement reportElement = (CaSwitcherReportElement) getAssetElement(name, ACTIVE_REPORT /*type*/, false /*fail*/);

	// If the Report is unavailable in the menu, simply collapse the menu itself.
	if(reportElement == null) {
		collapse();
		return null;
	}

	return reportElement.open();
}
}