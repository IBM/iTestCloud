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
package itest.cloud.ibm.page.ca.contentnav;

import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.page.ca.CaPage;
import itest.cloud.ibm.page.element.ca.glass.CaViewSwitcherMenuElement;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents and manages the <b>Home</b> page of the Cognos Analytics application.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getViewSwitcherMenuElement()}: Return the View Switcher Menu element.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return the expected title for the current web page.</li>
 * <li>{@link #getTitleElementLocator()}: Return the title element locator.</li>
 * </ul>
 * </p>
 */
public class CaHomePage extends CaPage {

public CaHomePage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote("Welcome!"));
}

@Override
protected By getTitleElementLocator() {
	return By.xpath("//span[contains(@class,'greetingLabel')]");
}

/**
 * Return the View Switcher Menu element.
 *
 * @return The View Switcher Menu element as {@link CaViewSwitcherMenuElement}.
 */
@Override
public CaViewSwitcherMenuElement getViewSwitcherMenuElement() {
	// The View Switcher menu may not be available in the Home Page at the product startup or
	// if all the opened asset pages have been closed deliberately. Therefore, only return the
	// View Switcher menu if it is available.
	final BrowserElement viewSwitcherContainerElement =
		waitForElement(By.id("com.ibm.bi.glass.common.viewSwitcherPluginContainer"), tinyTimeout(), false /*fail*/);
	return (viewSwitcherContainerElement != null) ? new CaViewSwitcherMenuElement(this) : null;
}
}