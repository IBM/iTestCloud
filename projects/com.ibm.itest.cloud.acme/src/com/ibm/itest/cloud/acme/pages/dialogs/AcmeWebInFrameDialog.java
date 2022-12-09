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
package com.ibm.itest.cloud.acme.pages.dialogs;

import org.openqa.selenium.By;

import com.ibm.itest.cloud.acme.pages.AcmeAbstractWebPage;
import com.ibm.itest.cloud.common.pages.WebPage;
import com.ibm.itest.cloud.common.pages.dialogs.WebInFrameDialog;
import com.ibm.itest.cloud.common.pages.frames.WebBrowserFrame;
import com.ibm.itest.cloud.common.tests.web.*;

/**
 * This class represents a generic dialog opened in a frame and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getPage()}: Return the web page from which the window has been opened.</li>
 * </ul>
 * </p>
 */
abstract public class AcmeWebInFrameDialog extends WebInFrameDialog {

public AcmeWebInFrameDialog(final WebPage page, final By findBy, final WebBrowserFrame frame) {
	super(page, findBy, frame);
}

/**
 * {@inheritDoc}
 *
 * @return The page as a subclass of {@link AcmeAbstractWebPage}
 */
@Override
protected AcmeAbstractWebPage getPage() {
	return (AcmeAbstractWebPage) this.page;
}
}