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

/**
 * This interface represents a generic page without the ability to navigate to other pages via the Bottom Tab List in the Cognos Analytics Mobile application and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #close()}: Close the Non Navigable Page by opening a Navigable Page.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public interface CaMobilePageWithoutBottomTabList {

/**
 * Close the Non Navigable Page by opening a Navigable Page.
 *
 * @return The opened Navigable Page as {@link CaMobilePageWithBottomTabList}.
 */
public abstract CaMobilePageWithBottomTabList close();
}
