/*********************************************************************
 * Copyright (c) 2017, 2024 IBM Corporation and others.
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

import org.openqa.selenium.By;

import itest.cloud.ibm.page.IbmPage;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class represents and manages a node (item) of the tree element in the side menu element
 * {@link IbmSideMenuElement} of a web page {@link IbmPage}.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #createChildElement(BrowserElement)}:
 * Create a element wrapper for a given child tree node or tree leaf element.</li>
 * <li>{@link #getChildElement(String, boolean)}:
 * Return the child tree node or tree leaf element at a given path.</li>
 * </ul>
 * </p>
 */
public class IbmSideMenuTreeNodeElement extends IbmTreeNodeElement {

	private static final String LABEL_ELEMENT_XPATH = "./a//*[contains(@class,'title')]";
	private static final By LABEL_ELEMENT_LOCATOR = By.xpath(LABEL_ELEMENT_XPATH);
	private static final By CHILD_ELEMENT_LOCATOR = By.xpath("./ul/li[" + LABEL_ELEMENT_XPATH + "]");

public IbmSideMenuTreeNodeElement(final ElementWrapper parent, final By locator) {
	super(parent, locator, LABEL_ELEMENT_LOCATOR, null /*expansionBy*/, CHILD_ELEMENT_LOCATOR);
}

public IbmSideMenuTreeNodeElement(final ElementWrapper parent, final BrowserElement webElement) {
	super(parent, webElement, LABEL_ELEMENT_LOCATOR, null /*expansionBy*/, CHILD_ELEMENT_LOCATOR);
}

@Override
protected IbmSideMenuTreeNodeElement createChildElement(final BrowserElement childWebElement) {
	return new IbmSideMenuTreeNodeElement(this, childWebElement);
}

@Override
protected IbmSideMenuTreeNodeElement getChildElement(final String path, final boolean fail) {
	return (IbmSideMenuTreeNodeElement) super.getChildElement(path, fail);
}
}