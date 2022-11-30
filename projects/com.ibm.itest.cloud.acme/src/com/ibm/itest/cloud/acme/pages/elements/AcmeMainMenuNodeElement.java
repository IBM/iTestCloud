/*********************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.acme.pages.elements;

import org.openqa.selenium.By;

import com.ibm.itest.cloud.acme.pages.AcmeAbstractWebPage;
import com.ibm.itest.cloud.common.tests.web.WebBrowserElement;
import com.ibm.itest.cloud.common.tests.web.WebPage;

/**
 * This class represents and manages a node (item) of the main menu element of a web page
 * {@link AcmeAbstractWebPage}.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #createChildElement(WebBrowserElement)}:
 * Create a element wrapper for a given child tree node or tree leaf element.</li>
 * <li>{@link #getChildElement(String, boolean)}:
 * Return the child tree node or tree leaf element at a given path.</li>
 * </ul>
 * </p>
 */
public class AcmeMainMenuNodeElement extends AcmeTreeNodeElement {

	private static final String LABEL_ELEMENT_XPATH = "./a";
	private static final By LABEL_ELEMENT_LOCATOR = By.xpath(LABEL_ELEMENT_XPATH);
	private static final By CHILD_ELEMENT_LOCATOR = By.xpath("./ul/li[" + LABEL_ELEMENT_XPATH + "]");

public AcmeMainMenuNodeElement(final WebPage page, final By locator) {
	super(page, locator, LABEL_ELEMENT_LOCATOR, null /*expansionBy*/, CHILD_ELEMENT_LOCATOR);
}

public AcmeMainMenuNodeElement(final WebPage page, final WebBrowserElement webElement) {
	super(page, webElement, LABEL_ELEMENT_LOCATOR, null /*expansionBy*/, CHILD_ELEMENT_LOCATOR);
}

@Override
protected AcmeMainMenuNodeElement createChildElement(final WebBrowserElement childWebElement) {
	return new AcmeMainMenuNodeElement(getPage(), childWebElement);
}

@Override
protected AcmeMainMenuNodeElement getChildElement(final String path, final boolean fail) {
	return (AcmeMainMenuNodeElement) super.getChildElement(path, fail);
}
}
