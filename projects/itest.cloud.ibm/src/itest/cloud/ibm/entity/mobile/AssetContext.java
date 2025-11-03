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
package itest.cloud.ibm.entity.mobile;

import itest.cloud.ibm.page.ca.mobile.ContentNavigationPage;

/**
 * This enum represents the context of an asset in the {@link ContentNavigationPage} where the asset should be searched in.
 */
public enum AssetContext {

	RECENT("recent"),
	MY_CONTENT("my_content" /*id*/),
	TEAM_CONTENT("team_content" /*id*/);

	private final String id;

AssetContext(final String id) {
	this.id = id;
}

/**
 * Return the id associated with the context.
 *
 * @return The id associated with the context as {@link String}
 */
public String getId() {
	return this.id;
}
}
