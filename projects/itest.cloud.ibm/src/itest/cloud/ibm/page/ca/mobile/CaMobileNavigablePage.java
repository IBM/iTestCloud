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
package itest.cloud.ibm.page.ca.mobile;

import static itest.cloud.ibm.page.ca.mobile.CaMobileNavigablePage.Page.BOARDS;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;

/**
 * This class represents a generic page with the ability to navigate to other pages via the Bottom Menu in the Cognos Analytics Mobile application and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #openBoardsPage()}: Open the Board Page.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public abstract class CaMobileNavigablePage extends CaMobilePage {

	enum Page {
		BOARDS("Boards");

		final String label;

		Page(final String label) {
			this.label = label;
		}
	}

public CaMobileNavigablePage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

public CaMobileNavigablePage(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

/**
 * Open the Boards Page via its Bottom Navigation Menu.
 *
 * @return the opened Boards Page as {@link BoardsPage}.
 */
public BoardsPage openBoardsPage() {
	return openPage(BOARDS, BoardsPage.class);
}

private <P extends CaMobileNavigablePage> P openPage(final Page page, final Class<P> openedPageClass, final String... info) {
	return openMobilePageUsingLink(By.xpath("//*[@*='bottom-tab-" + page.label + "']"), openedPageClass, info);
}
}