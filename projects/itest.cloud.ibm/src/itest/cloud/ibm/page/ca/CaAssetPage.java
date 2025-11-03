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
package itest.cloud.ibm.page.ca;

import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;

/**
 * This class represents and manages an asset page.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getName()}: Return the name of the asset.</li>
 * <li>{@link #waitForLoadingPageEnd()}: Wait for the page loading to be finished.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return the expected title for the current web page.</li>
 * </ul>
 * </p>
 */
public abstract class CaAssetPage extends CaPage {

public CaAssetPage(final String url, final IbmConfig config, final User user, final String... data) {
	super(url, config, user, data);
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote(getName()));
}

/**
 * Return the name of the asset.
 *
 * @return The name of the asset as {@link String}.
 */
public String getName() {
	return this.data[0];
}
}