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

import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.ca.CaPage;
import itest.cloud.ibm.page.element.IbmDynamicDropdownlistElement;
import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.ibm.page.element.ca.CaActionMenuElement;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class defines and manages an asset element in {@link CaContentTabElement}. Such an asset can be a Report, Data Module, Dashboard, Notebook, ...etc.
 * <p>
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #getName()}: Return the name of the asset.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * <li>{@link #open(Class)}: Open the asset by opening a given page.</li>
 * <li>{@link #selectItemFromActionMenu(String, Class)}: Select a specific item (option) from the Action Menu of the asset element by opening a page.</li>
 * </ul>
 * </p>
 */
public abstract class CaContentAssetElement extends IbmElementWrapper {

public CaContentAssetElement(final ElementWrapper parent, final BrowserElement element, final String... data) {
	super(parent, element, data);
}

/**
 * Open the action menu of the asset.
 *
 * @return The opened action menu of the asset as {@link CaActionMenuElement}.
 */
private IbmDynamicDropdownlistElement getActionMenuElement() {
	// The expansion element of the action menu is invisible by default. Move the mouse cursor over the content element to make the
	// expansion element visible.
	this.element.moveToElement();

	return new IbmDynamicDropdownlistElement(this,
		By.xpath("//*[contains(@class,'commonMenuActive')]"), waitForElement(By.xpath(".//button")),
		null /*selectionLocator*/, By.xpath(".//*[contains(@class,'commonMenuLink')]/*"));
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote(getName()));
}

/**
 * Return the name of the asset.
 *
 * @return The name of the asset as {@link String}.
 */
public String getName() {
	return this.data[0];
}

@Override
protected By getTitleElementLocator() {
	return By.xpath(
		".//a//*[contains(@class,'label')]//*[contains(@class,'_text')] | " + // For folder elements
		".//a//*[@class='ba-common-tooltip ' and contains(@style,'width')]/div[1]"); // For Report elements
}

/**
 * Open the asset by opening a given page.
 *
 * @param openedPageClass The class associated with the opened page as a {@link CaPage}.
 *
 * @return The opened web page as a {@link CaPage}).
 */
protected <P extends CaPage> P open(final Class<P> openedPageClass) {
	return openPageUsingLink(getTitleElement(), openedPageClass, getName());
}

/**
 * Select a specific item (option) from the Action Menu of the asset element by opening a page.
 *
 * @param item The item to select from the menu.
 * @param openedPageClass A class representing the web page opened after clicking on the particular menu item.
 *
 * @return The opened page as a {@link CaPage}.
 */
protected <P extends CaPage> P selectItemFromActionMenu(final String item, final Class<P> openedPageClass) {
	final IbmDynamicDropdownlistElement actionMenuElement = getActionMenuElement();
	return actionMenuElement.selectByOpeningPage(item, openedPageClass, getName());
}
}