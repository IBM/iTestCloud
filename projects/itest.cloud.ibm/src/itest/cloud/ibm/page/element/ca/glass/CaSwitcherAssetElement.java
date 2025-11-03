/*********************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
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

import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.ca.CaPage;
import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class defines and manages an asset element in {@link CaViewSwitcherMenuElement}. Such an asset can be a Report, Data Module, Dashboard, Notebook, ...etc.
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
 * <li>{@link #open(Class, String...)}: Open the asset by opening a given page.</li>
 * </ul>
 * </p>
 */
public abstract class CaSwitcherAssetElement extends IbmElementWrapper {

public CaSwitcherAssetElement(final ElementWrapper parent, final BrowserElement element, final String... data) {
	super(parent, element, data);
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
	return By.xpath(".//*[@class='commonMenuLink']//span");
}

/**
 * Open the asset by opening a given page.
 *
 * @param openedPageClass The class associated with the opened page as a {@link CaPage}.
 * @param pageData Provide additional information to store in the page when opening it
 *
 * @return The opened web page as a {@link CaPage}).
 */
protected <P extends CaPage> P open(final Class<P> openedPageClass, final String... pageData) {
	return openPageUsingLink(getTitleElement(), openedPageClass, pageData);
}
}