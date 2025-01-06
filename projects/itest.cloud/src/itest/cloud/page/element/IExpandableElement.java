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
package itest.cloud.page.element;

import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * Interface for an expandable element.
 * <p>
 * Following methods are available on such element:
 * <ul>
 * <li>{@link #collapse()}: Collapse the current wrapped web element.</li>
 * <li>{@link #expand()}: Expand the current wrapped web element.</li>
 * <li>{@link #isExpandable()}: Returns whether the current wrapped web element is expandable or not.</li>
 * <li>{@link #isExpanded()}: Returns whether the current wrapped web element is expandable or not.</li>
 * <li>{@link #toggle()}: Toggle the current web element.</li>
 *</ul>
 * </p>
 */
public interface IExpandableElement {

/**
 * Collapse the current web element.
 * <p>
 * If the web element is already expanded, then nothing happens.
 * </p>
 * @throws ScenarioFailedError If the wrapped web element does not have
 * the <code>aria-expanded</code> attribute.
 */
void collapse() throws ScenarioFailedError;

/**
 * Expand the current web element.
 * <p>
 * If the web element is already expanded, then nothing happens.
 * </p>
 * @throws ScenarioFailedError If the wrapped web element does not have
 * the <code>aria-expanded</code> attribute.
 */
void expand() throws ScenarioFailedError;

/**
 * Returns whether the current wrapped web element is expandable or not.
 * <p>
 * Subclass must override this method if the web element has no specific
 * expandable attribute.
 * </p>
 * @return <code>true</code> if the current node is expanda, <code>false>/code>
 * otherwise.
 * @throws ScenarioFailedError If the wrapped web element does not have
 * the <code>aria-expanded</code> attribute.
 */
boolean isExpandable() throws ScenarioFailedError;

/**
 * Returns whether the current wrapped web element is expanded or not.
 * <p>
 * Subclass must override this method if the web element has no specific
 * expandable attribute.
 * </p>
 * @return <code>true</code> if the current node is expanded, <code>false>/code>
 * otherwise.
 * @throws ScenarioFailedError If the wrapped web element does not have
 * the <code>aria-expanded</code> attribute.
 */
boolean isExpanded() throws ScenarioFailedError;

/**
 * Toggle the current web element.
 * <p>
 * If the web element is already expanded, then nothing happens.
 * </p>
 * @throws ScenarioFailedError If the wrapped web element does not have
 * the <code>aria-expanded</code> attribute.
 */
void toggle() throws ScenarioFailedError;
}
