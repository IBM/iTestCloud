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

import static itest.cloud.scenario.ScenarioUtil.splitPath;

import itest.cloud.config.IUser;
import itest.cloud.ibm.page.ca.CaAssetPage;
import itest.cloud.ibm.page.ca.CaPage;
import itest.cloud.ibm.page.ca.contentnav.CaContentPage;
import itest.cloud.ibm.page.ca.contentnav.CaHomePage;
import itest.cloud.ibm.page.ca.reporting.CaReportPage;
import itest.cloud.ibm.page.element.ca.contentnav.CaContentTabElement;
import itest.cloud.ibm.page.element.ca.glass.CaSideMenuElement;
import itest.cloud.ibm.page.element.ca.glass.CaViewSwitcherMenuElement;
import itest.cloud.ibm.page.wxbia.WxbiPage;
import itest.cloud.ibm.scenario.IbmScenarioStep;
import itest.cloud.ibm.topology.CaApplication;
import itest.cloud.ibm.topology.IbmTopology;
import itest.cloud.scenario.ScenarioUtil;

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

private CaPage openAssetPage(final String name, final Class<? extends CaAssetPage> webPageClass, final IUser user) {
	final CaPage page = openWebPage(webPageClass, user);

	// If the current page matches the asset type, check if the currently opened asset is the desired one.
	if (page == null) {
		final CaAssetPage openedAssetPage = (CaAssetPage) getCurrentPage();

		if(openedAssetPage.getName().equals(name)) {
			// If so, return null to indicate to the caller that no further
			// actions are required to open the particular web page since
			// the user is already in the correct web page.
			return null;
		}
	}

	return page;
}

/**
 * Logout the user.
 *
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 */
protected void logout(final IUser user) {
	final CaPage page = openWebPage(null /*webPageClass*/, user);
	page.logout();
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
	CaPage page = openWebPage(CaContentPage.class, user);

	// If the current page is the Content Page, simple return it.
	if (page == null) {
		page = getCurrentPage();
		return (CaContentPage) page;
	}

	// If the the View Switcher menu is available in the current page and the Content Page in this menu,
	// open the Content Page via View Switcher menu.
	final CaViewSwitcherMenuElement viewSwitcherMenuElement = page.getViewSwitcherMenuElement();
	if(viewSwitcherMenuElement != null) {
		final CaContentPage openedPage = viewSwitcherMenuElement.openContentPageIfAvailable();
		if(openedPage != null) return openedPage;
	}

	// Otherwise, open the Content Page via the Side menu.
	final CaSideMenuElement sideMenu = page.openSideMenu();
	return sideMenu.openContentPage();
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
	final CaPage page = openWebPage(CaHomePage.class, user);

	return (page == null) ? (CaHomePage) getCurrentPage() : page.openHomePage();
}

/**
 * Open a given Report.
 *
 * <p>
 * If the path contains multiple sections, they must be separated by the character '/' {@link ScenarioUtil#PATH_SEPARATOR},
 * The following are some example paths.
 * <ul>
 * <li>Samples/* Get started/My first report</li>
 * </ul>
 * </p>
 *
 * @param path The path to the Report as {@link String}.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The opened Report Page as {@link CaPage}).
 */
protected CaReportPage openReport(final String path, final IUser user) {
	final String[] pathItems = splitPath(path);
	final String reportName = pathItems[pathItems.length - 1];
	CaPage page = openAssetPage(reportName, CaReportPage.class, user);

	// If the current opened Report page is the desired one, simply return it.
	if (page == null) return (CaReportPage)(page = getCurrentPage());

	// If the the View Switcher menu is available in the current page and the desired Report exists in this menu,
	// open the Report via View Switcher menu.
	final CaViewSwitcherMenuElement viewSwitcherMenuElement = page.getViewSwitcherMenuElement();
	if(viewSwitcherMenuElement != null) {
		final CaReportPage reportPage = viewSwitcherMenuElement.openRportIfAvailable(reportName);
		if(reportPage != null) return reportPage;
	}

	// Otherwise, open the desired Report via the Content Page.
	final CaContentTabElement teamContentTab = openTeamContentTab(user);
	return teamContentTab.openRport(path);
}

private CaContentTabElement openTeamContentTab(final IUser user) {
	final CaContentPage contentPage = openContentPage(user);
	return contentPage.openTeamContentTab();
}

private CaPage openWebPage(final Class<? extends CaPage> webPageClass, final IUser user) {
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
}