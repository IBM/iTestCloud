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

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.element.IbmDynamicDropdownlistElement;
import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class defines and manages a content element in {@link CaContentTabElement}.
 * <p>
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #getName()}: Return the name of the content item.</li>
 * <li>{@link #open()}: Open the content item.</li>
 * <li>{@link #openActionMenu()}: Open the action menu of the content item.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return the expected title for the current web page.</li>
 * <li>{@link #getTitleElementLocator()}: Return the title element locator.</li>
 * </ul>
 * </p>
 */
public class CaContentElement extends IbmElementWrapper {

public CaContentElement(final ElementWrapper parent, final BrowserElement element, final String... data) {
	super(parent, element, data);
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

/**
 * Return the name of the content item.
 *
 * @return The name of the content item as {@link String}.
 */
public String getName() {
	return this.data[0];
}

@Override
protected By getTitleElementLocator() {
	return null;
}

/**
 * Open the action menu of the content item.
 *
 * @return The opened action menu of the content item as {@link CaActionMenuElement}.
 */
public IbmDynamicDropdownlistElement openActionMenu() {
	// The expansion element of the action menu is invisible by default. Move the mouse cursor over the content element to make the
	// expansion element visible.
	this.element.moveToElement();

	return new IbmDynamicDropdownlistElement(this,
		By.xpath(".//*[contains(@class,'commonMenuActive')]"), By.xpath(".//div") /*find xpath*/,
		null /*selectionLocator*/, By.xpath(".//*[contains(@class,'commonMenuLink')]/*"));
}

/**
 * Open the content item.
 */
public void open() {
	getTitleElement().click();
}
}