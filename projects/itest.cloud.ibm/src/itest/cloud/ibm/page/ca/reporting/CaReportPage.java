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
package itest.cloud.ibm.page.ca.reporting;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.page.ca.CaAssetPage;

/**
 * This class represents and manages a report page.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getName()}: Return the name of the report.</li>
 * <li>{@link #waitForLoadingPageEnd()}: Wait for the page loading to be finished.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return the expected title for the current web page.</li>
 * </ul>
 * </p>
 */
public class CaReportPage extends CaAssetPage {

//	private static final By FORMAT_BUTTON_LABEL_LOCATOR = By.xpath("//*[@id='AppToolbarLeftPane_btnFormats']//span");

public CaReportPage(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

private void selectFrame() {
	this.browser.switchToFrame(By.id("rsIFrameManager_1"), timeout());
}

//private void getFormatsDropdownlistElement() {
//	final IbmDropdownlistElement formatsDropdownlistElement = new IbmDropdownlistElement(this,
//		By.id("mnuAppToolbarLeftPaneFormatsPopup") /*locator*/,
//		FORMAT_BUTTON_LABEL_LOCATOR /*expansionLocator*/,
//		FORMAT_BUTTON_LABEL_LOCATOR /*selectionLocator*/,
//		By.xpath("//*[@class='clsMenuItemLabelDiv']") /*optionLocator*/);
//}

@Override
public void waitForLoadingPageEnd() {
	super.waitForLoadingPageEnd();
	// Wait for the iframe containing the report to load.
	try {
		// Select the iframe.
		selectFrame();
		// Wait for the progress spinners in the iframe to disappear.
		super.waitWhileBusy();
		// Wait for the contents of the report in the iframe to load.
		waitForElement(By.id("idLayoutView"), timeout(), true /*fail*/, false /*displayed*/, false /*single*/);
	}
	finally {
		// Switch the scope to the page itself.
		switchToMainWindow();
	}
}
}