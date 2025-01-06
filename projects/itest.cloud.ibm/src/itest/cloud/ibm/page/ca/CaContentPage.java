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
package itest.cloud.ibm.page.ca;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.page.element.IbmTabListElement;
import itest.cloud.ibm.page.element.ca.CaContentTabElement;

/**
 * This class represents and manages the <b>Content</b> page of the Cognos Analytics application.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #openTeamContentTab()}: Open the Team Content tab.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return the expected title for the current web page.</li>
 * <li>{@link #getTitleElementLocator()}: Return the title element locator.</li>
 * </ul>
 * </p>
 */
public class CaContentPage extends CaPage {

public CaContentPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

@Override
protected Pattern getExpectedTitle() {
//	return compile(quote(this.data[0]));
	return null;
}

@Override
protected By getTitleElementLocator() {
	return By.xpath("//*[contains(@class,'pageHeader__title')]//*[not(*)]");
}

/**
 * Open the <b>Team Content</b> tab.
 *
 * @return The opened Team Content tab as {@link CaContentTabElement}.
 */
public CaContentTabElement openTeamContentTab() {
	return openTab("Team content");
}

private CaContentTabElement openTab(final String name) {
	IbmTabListElement tablistElement = new IbmTabListElement(this);

	return tablistElement.openTab(name, CaContentTabElement.class);
}
}