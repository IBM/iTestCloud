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
package com.ibm.itest.cloud.common.pages.elements;

/**
 * Interface defining API for a window element.
 * <p>
 * Following methods are available on such element:
 * <ul>
 * <li>{@link #cancel()}: Close the window by clicking on the cancel button (usually the 'Cancel' button).</li>
 * <li>{@link #close()}: Close the window by clicking on the close button (usually the 'OK' button).</li>
 *</ul>
 * </p>
 */
public interface Window {

/**
 * Close the window by clicking on the cancel button (usually the 'Cancel' button).
 */
void cancel();

/**
 * Close the window by clicking on the close button (usually the 'OK' button).
 */
void close();

/**
 * Tells whether the window can be closed or not.
 * <p>
 * By default it's checking whether the close button is enabled or not.
 * </p>
 * @return <code>true</code> if there's a enabled close button, <code>false</code> otherwise
 */
boolean isCloseable();

}
