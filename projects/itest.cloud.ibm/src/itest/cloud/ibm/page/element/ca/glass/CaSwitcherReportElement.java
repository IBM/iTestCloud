/*********************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
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
package itest.cloud.ibm.page.element.ca.glass;

import itest.cloud.ibm.page.ca.reporting.CaReportPage;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class defines and manages a Report element in {@link CaViewSwitcherMenuElement}.
 * <p>
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #open()}: Open the Report.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * </ul>
 * </p>
 */
public class CaSwitcherReportElement extends CaSwitcherAssetElement {

public CaSwitcherReportElement(final ElementWrapper parent, final BrowserElement element, final String... data) {
	super(parent, element, data);
}

/**
 * Open the Report.
 *
 * @return The opened Report page as {@link CaReportPage}).
 */
protected CaReportPage open() {
	return open(CaReportPage.class, getName());
}
}