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
package itest.cloud.ibm.page.element.ca.contentnav;

import itest.cloud.ibm.page.ca.reporting.CaReportPage;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class defines and manages a Report element in {@link CaContentTabElement}.
 * <p>
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #edit()}: Edit the Report.</li>
 * <li>{@link #open()}: Open the Report.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * </ul>
 * </p>
 */
public class CaContentReportElement extends CaContentAssetElement {

public CaContentReportElement(final ElementWrapper parent, final BrowserElement element, final String... data) {
	super(parent, element, data);
}

/**
 * Edit the Report.
 *
 * @return The opened Report Page as {@link CaReportPage}).
 */
public CaReportPage edit() {
	return selectItemFromActionMenu("Edit report" /*item*/, CaReportPage.class);
}

/**
 * Open the Report.
 *
 * @return The opened Report Page as {@link CaReportPage}).
 */
public CaReportPage open() {
	return open(CaReportPage.class);
}
}