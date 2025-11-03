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
package itest.cloud.ibm.entity.ca;

import itest.cloud.ibm.page.element.ca.contentnav.*;
import itest.cloud.ibm.page.element.ca.glass.*;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This enum represents the type of an asset in Cognos Analytics application.
 */
public enum CaAssetType {

	REPORT("Report", "-report", CaContentReportElement.class, CaSwitcherReportElement.class),
	ACTIVE_REPORT("Active Report", "-interactiveReport", CaContentReportElement.class, CaSwitcherReportElement.class),
	FOLDER("Folder", "_content_nav", CaContentFolderElement.class, CaSwitcherContentElement.class);

/**
 * Return the asset type represented by a given id.
 *
 * @param keyword A keyword representing the desired asset type as {@link String}.
 *
 * @return The asset type represented by a given id as {@link CaAssetType}.
 */
public static CaAssetType getAssetType(final String keyword) {
	for (CaAssetType type : values()) {
		if(keyword.equalsIgnoreCase(type.label) || keyword.contains(type.switcherId)) return type;
	}
	throw new ScenarioFailedError("Keyword '" + keyword + "' is unknown to this method.");
}

private final String switcherId;
private final Class<? extends CaContentAssetElement> contentAssetElementClass;
private final Class<? extends CaSwitcherAssetElement> switcherAssetElementClass;

/**
 * The textual representation of the suggested question.
 */
private final String label;

CaAssetType(final String label, final String switcherId, final Class<? extends CaContentAssetElement> contentAssetElementClass, final Class<? extends CaSwitcherAssetElement> switcherAssetElementClass) {
	this.label = label;
	this.switcherId = switcherId;
	this.contentAssetElementClass = contentAssetElementClass;
	this.switcherAssetElementClass = switcherAssetElementClass;
}

/**
 * Return the Content Asset Element class associated with this Asset Type.
 *
 * @return The Content Asset Element class associated with this Asset Type as {@link Class} of an {@link CaContentAssetElement}.
 */
public Class<? extends CaContentAssetElement> getContentAssetElementClass() {
	return this.contentAssetElementClass;
}

/**
 * Return the label associated with this Asset Type.
 *
 * @return The label associated with this Asset Type as {@link String}.
 */
public String getLabel() {
	return this.label;
}

/**
 * Return the Switcher Asset Element class associated with this Asset Type.
 *
 * @return The Switcher Asset Element class associated with this Asset Type as {@link Class} of an {@link CaContentAssetElement}.
 */
public Class<? extends CaSwitcherAssetElement> getSwitcherAssetElementClass() {
	return this.switcherAssetElementClass;
}

/**
 * Return the id used to identify an asset in the View Switcher menu.
 *
 * @return The id used to identify an asset in the View Switcher menu as {@link String}.
 */
public String getSwitcherId() {
	return this.switcherId;
}
}