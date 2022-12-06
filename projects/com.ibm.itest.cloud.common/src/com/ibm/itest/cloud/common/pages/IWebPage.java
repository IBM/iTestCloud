/*********************************************************************
 * Copyright (c) 2015, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.common.pages;

import com.ibm.itest.cloud.common.tests.web.WebBrowser;

/**
 * Interface to define web page object API.
 * <p>
 * Available API methods on such object are:
 * <ul>
 * <li>{@link #getLocation()}: Return the page location used when creating the object.</li>
 * <li></li>
 * </p>
 * @since 6.0
 * TODO Rename this interface as <b>WebPage</p>
 */
public interface IWebPage {

/**
 * Return the page location used when creating it.
 * <p>
 * Note that it can be slightly different from the browser URL.
 * </p>
 * @return The page location
 */
String getLocation();

/**
 * Return the title of the page.
 *
 * @return The title as a {@link String}.
 */
String getTitle();

/**
 * Refresh the page content using {@link WebBrowser#refresh()} and wait for
 * the page to be loaded.
 */
void refresh();
}
