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
package itest.cloud.acme.scenario;

import static itest.cloud.scenario.ScenarioUtils.*;

import itest.cloud.acme.config.AcmeConfig;
import itest.cloud.acme.pages.AcmeAbstractWebPage;
import itest.cloud.acme.pages.AcmeHomePage;
import itest.cloud.acme.topology.AcmeApplication;
import itest.cloud.acme.topology.AcmeTopology;
import itest.cloud.config.*;
import itest.cloud.pages.Page;
import itest.cloud.scenario.ScenarioStep;
/**
 * Manage a scenario step.
 * <p>
 * The step gives access to the current location which is either the page location or the current browser URL
 * in case no page was already stored.
 * </p>
 * <p>
 * Following actions are accessible in this page:
 * <ul>
 * </ul>
 * </p>
 * <p>
 * It also contains some other useful helper methods:
 * <ul>
 * <li>{@link #changeUser(IUser)}: Login as a new user.</li>
 * </ul>
 * </p>
 */
public class AcmeScenarioStep extends ScenarioStep {

/**
 * Change the current page user to given user.
 * <p>
 * Nothing happen if there's no current page or if its user is the same than given one. Otherwise, the current user is logged out and a login occurs with the new user credentials.
 * </p>
 */
protected void changeUser(final IUser user) {
	AcmeAbstractWebPage currentPage = getCurrentPage();
	if (currentPage != null && user != null && currentPage.needLogin((User) user)) {
		// URL of a web page may contain user specific information such as user ids,
		// session ids, ...etc. Using such a URL to access the same web page for a
		// different user would not be possible and result in redirection to other
		// pages such as error pages, home page ...etc. Therefore, always logout
		// the current user and log in the new user in a web page where all users
		// have access. The URL of such a web page should not contain any user
		// specific information. The Portal Home Page is a good candidate for this
		// purpose. Therefore, open the Portal Home Page as the current user and
		// perform the logout/login operation in the particular web page rather
		// than doing so in the current page.
		currentPage.login((User)user);
	}
}

/**
 * @see AcmeTopology#getApsPortalApplication()
 */
protected AcmeApplication getApsPortalApplication() {
	return getTopology().getApsPortalApplication();
}

/**
 * {@inheritDoc}
 *
 * @return The page as a subclass of {@link AcmeAbstractWebPage}.
 * May be null if no page was stored neither in current test nor in previous one.
 */
@Override
protected AcmeAbstractWebPage getCurrentPage() {
	return (AcmeAbstractWebPage) super.getCurrentPage();
}

/**
 * {@inheritDoc}
 *
 * @return The CLM scenario topology as {@link AcmeConfig}.
 */
@Override
protected AcmeTopology getTopology() {
	return (AcmeTopology) super.getTopology();
}

/**
 * Open the Data Science Home Page.
 *
 * @param user The user to be used when accessing the page. Can be <code>null</code>
 * @return The opened web page as a{@link AcmeHomePage}.
 */
protected AcmeHomePage openHomePage(final IUser user) {
	AcmeAbstractWebPage portalWebPage = openPortalWebPageHelper(AcmeHomePage.class, user);

	return (portalWebPage == null) ? (AcmeHomePage) getCurrentPage() : portalWebPage.openHomePage();
}

private AcmeAbstractWebPage openPortalWebPageHelper(final Class<? extends AcmeAbstractWebPage> webPageClass, final IUser user) {
	// Change the user to the given
	changeUser(user);

	AcmeAbstractWebPage currentPage = getCurrentPage();

	if (currentPage == null) {
		// No previous page was stored and therefore, open the home page.
		return openWebPage(getApsPortalApplication().getLocation(), getConfig(), user, AcmeHomePage.class);
	}

	// Check whether the current page matches the given one if a specific web page class is provided.
	if ((webPageClass!= null) && webPageClass.isInstance(currentPage) && currentPage.matchPage()) {
		// If so, return null to indicate to the caller that no further
		// actions are required to open the particular web page since
		// the user is already in the correct web page.
		return null;
	}

	return currentPage;
}

/**
 * Open the web page for the given url using the given user and associated with the given class.
 * <p>
 * The page is not reopened if it already exists and displayed in the browser, it's just returned from the cache (ie. no unnecessary get is done on it).
 * </p>
 * <p>
 * When calling this method the current page of the {@link AcmeScenarioExecution} is set with the opened page.
 * </p>
 *
 * @param <P> The page associated class
 * @param url The page url
 * @param newUser The user used to access the page
 * @param pageClass The page associated class
 * @return The opened page as {@link AcmeAbstractWebPage}.
 */
protected <P extends Page> P openWebPage(final String url, final IConfig newConfig, final IUser newUser, final Class<P> pageClass, final String... data) {
	AcmeAbstractWebPage currentPage = getCurrentPage();
	if (currentPage != null) {
		println("================================================================================");
		println("WARNING: opening page from URL is strongly discouraged while running a scenario!");
		println("	Instead use product web ui way (ie. menu, links, etc.) to open " + getClassSimpleName(pageClass) + " page...");
		println("This risky page opening was done from: " + getClassSimpleName(currentPage.getClass()));
		printStackTrace(1);
		println("================================================================================");
	}
	return Page.openPage(url, (Config) newConfig, (User) newUser, pageClass, data);
}
}