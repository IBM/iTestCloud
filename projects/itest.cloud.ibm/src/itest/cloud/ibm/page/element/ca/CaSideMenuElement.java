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

import itest.cloud.ibm.page.IbmPage;
import itest.cloud.ibm.page.ca.CaContentPage;
import itest.cloud.ibm.page.element.IbmDynamicExpandableElement;
import itest.cloud.page.Page;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This class represents and manages the side menu element of a web page {@link IbmPage}.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #openContentPage()}: Open the Content page by selecting an appropriate option from the side menu.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class CaSideMenuElement extends IbmDynamicExpandableElement {

public CaSideMenuElement(final Page page) {
	super(page, By.xpath("//*[contains(@class,'glass-menu')]"), By.id("com.ibm.bi.glass.common.navmenu"));
}

@Override
protected String getExpandableAttribute() {
	throw new ScenarioFailedError("This method should never be called.");
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
 * Open the Content page by selecting an appropriate option from the side menu.
 *
 * @return The opened Content page as {@link CaContentPage}.
 */
public CaContentPage openContentPage() {
	return openPageUsingLink(waitForElement(By.xpath(".//a[@data-item='com.ibm.bi.content.content']")), CaContentPage.class);
}
}