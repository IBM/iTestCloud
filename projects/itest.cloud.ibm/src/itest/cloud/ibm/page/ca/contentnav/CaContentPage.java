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
package itest.cloud.ibm.page.ca.contentnav;

import static itest.cloud.ibm.page.element.ca.glass.CaViewSwitcherMenuElement.VIEW_SWITCHER_MENU_LOCATOR;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.page.ca.CaPage;
import itest.cloud.ibm.page.element.IbmTabListElement;
import itest.cloud.ibm.page.element.ca.contentnav.CaContentTabElement;
import itest.cloud.page.element.BrowserElement;

/**
 * This class represents and manages the <b>Content</b> page of the Cognos Analytics application.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getHeaderTitle()}: Return the header title.</li>
 * <li>{@link #openTeamContentTab()}: Open the Team Content tab.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return the expected title for the current web page.</li>
 * </ul>
 * </p>
 */
public class CaContentPage extends CaPage {

	public static final String CONTENT_PAGE_TITLE = "Content";

public CaContentPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote(CONTENT_PAGE_TITLE));
}

/**
 * Return the header title.
 *
 * @return The header title as {@link String}.
 */
public String getHeaderTitle() {
	final BrowserElement headerTitleElement = waitForElement(By.xpath("//*[contains(@class,'pageHeader__title')]//*[contains(@class,'text')]"));
	return headerTitleElement.getText();
}

private CaContentTabElement openTab(final String name, final String... tabData) {
	final IbmTabListElement tablistElement = new IbmTabListElement(this);

	return tablistElement.openTab(name, CaContentTabElement.class, tabData);
}

/**
 * Open the <b>Team Content</b> tab.
 *
 * @return The opened Team Content tab as {@link CaContentTabElement}.
 */
public CaContentTabElement openTeamContentTab() {
	return openTab("Team content" /*name*/, "com.ibm.bi.content.navigator.teamContent__panel" /*element id*/);
}

@Override
public void waitForLoadingPageEnd() {
	super.waitForLoadingPageEnd();
	// Wait for View Switcher menu to disappear to workaround a race condition.
	this.browser.waitWhileDisplayed(null /*parentElement*/, VIEW_SWITCHER_MENU_LOCATOR, timeout(), true /*fail*/);
}
}