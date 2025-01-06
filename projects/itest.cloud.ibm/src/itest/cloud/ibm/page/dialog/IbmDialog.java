/*********************************************************************
 * Copyright (c) 2018, 2024 IBM Corporation and others.
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
package itest.cloud.ibm.page.dialog;

import static itest.cloud.ibm.scenario.IbmScenarioConstants.*;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.IbmPage;
import itest.cloud.ibm.scenario.IbmScenarioUtil;
import itest.cloud.page.Page;
import itest.cloud.page.dialog.Dialog;

/**
 * This class represents a generic dialog in an IBM application and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getBusyIndicatorElementLocator()}: Return the xpaths of element indicating that the element is undergoing an operation (busy).</li>
 * <li>{@link #getContentElementLocator()}: Return the locator for the content element of the current dialog.</li>
 * <li>{@link #getPage()}: Return the web page from which the window has been opened.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current dialog.</li>
 * </ul>
 * </p>
 */
abstract public class IbmDialog extends Dialog {

public IbmDialog(final Page page) {
	super(page, DIALOG_LOCATOR);
}

public IbmDialog(final Page page, final By findBy) {
	super(page, findBy);
}

public IbmDialog(final Page page, final By findBy, final String... data) {
	super(page, findBy, data);
}

public IbmDialog(final Page page, final String... data) {
	super(page, DIALOG_LOCATOR, data);
}

@Override
protected By getBusyIndicatorElementLocator() {
	return IbmScenarioUtil.getBusyIndicatorElementLocator(true /*relative*/);
}

@Override
protected By getContentElementLocator(){
	return DIALOG_CONTENT_ELEMENT_LOCATOR;
}

/**
 * {@inheritDoc}
 *
 * @return The page as a subclass of {@link IbmPage}.
 */
@Override
protected IbmPage getPage() {
	return (IbmPage) this.page;
}

@Override
protected By getTitleElementLocator() {
	return DIALOG_TITLE_ELEMENT_LOCATOR;
}
}