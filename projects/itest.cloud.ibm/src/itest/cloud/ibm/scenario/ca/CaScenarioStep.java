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
package itest.cloud.ibm.scenario.ca;

import itest.cloud.config.IUser;
import itest.cloud.ibm.page.ca.*;
import itest.cloud.ibm.page.wxbia.WxbiPage;
import itest.cloud.ibm.scenario.IbmScenarioStep;
import itest.cloud.ibm.topology.CaApplication;
import itest.cloud.ibm.topology.IbmTopology;

/**
 * Manage a scenario step of the Cognos Analytics application.
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
public class CaScenarioStep extends IbmScenarioStep {

/**
 * @see IbmTopology#getCaApplication()
 */
protected CaApplication getCaApplication() {
	return getTopology().getCaApplication();
}

/**
 * {@inheritDoc}
 *
 * @return The page as a subclass of {@link WxbiPage}.
 * May be null if no page was stored neither in current test nor in previous one.
 */
@Override
protected CaPage getCurrentPage() {
	return (CaPage) super.getCurrentPage();
}

private CaPage openCaWebPage(final Class<? extends CaPage> webPageClass, final IUser user) {
	// Change the user to the given
	changeUser(user);

	CaPage currentPage = getCurrentPage();

	if (currentPage == null) {
		// No previous page was stored and therefore, open the home page.
		currentPage = openWebPage(getCaApplication().getHomePageUrl(), getConfig(), user, CaHomePage.class);
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

//protected void editReport(final String path, final IUser user) {
//
//}

/**
 * Open the Content page.
 *
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The opened Content page as {@link CaContentPage}.
 */
protected CaContentPage openContentPage(final IUser user) {
	final CaPage page = openCaWebPage(CaContentPage.class, user);

	return (page == null) ? (CaContentPage) getCurrentPage() : page.openSideMenu().openContentPage();
}

/**
 * Open the home page.
 *
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The opened home page as {@link CaHomePage}.
 */
protected CaHomePage openHomePage(final IUser user) {
	final CaPage page = openCaWebPage(CaHomePage.class, user);

	return (page == null) ? (CaHomePage) getCurrentPage() : page.openHomePage();
}

/**
 * Open any page of the application.
 *
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return the opened page as a {@link CaPage}.
 * If the specified user is already in a page of the application, the current page is returned.
 */
protected CaPage openCaWebPage(final IUser user) {
	return openCaWebPage(null /*webPageClass*/, user);
}
}