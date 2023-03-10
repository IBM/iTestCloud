/*********************************************************************
 * Copyright (c) 2014, 2022 IBM Corporation and others.
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
package itest.cloud.config;

import itest.cloud.browsers.Browser;
import itest.cloud.topology.Topology;

public interface IConfig {

/**
 * Return the timeout used to wait for a dialog to be closed.<br>
 * This time is expressed in <b>seconds</b>.
 *
 * @return The timeout as a <code>int</code>.
 */
public int closeDialogTimeout();

/**
 * Return the browser used while running the scenario.
 *
 * @return The browser as {@link Browser}.
 */
public Browser getBrowser();

/**
 * Return the default timeout used to wait for an expected element in
 * the current web page. This time is expressed in <b>seconds</b>.
 *
 * @return The timeout as a <code>int</code>.
 */
public int getDefaultTimeout();

/**
 * Return the default timeout used to wait for a downloading of a file to start.
 * This time is expressed in <b>seconds</b>.
 *
 * @return The timeout as a <code>int</code>.
 */
public int getDownloadStartTimeout();

/**
 * Return the timeout used to wait for a page to be loaded.<br>
 * This time is expressed in <b>seconds</b>.
 *
 * @return The timeout as a <code>int</code>.
 */
public int getOpenPageTimeout();

/**
 * Return the timeout used to wait for short run operation.<br>
 * This time is expressed in <b>seconds</b>.
 *
 * @return The timeout as a <code>int</code>.
 */
public int getShortTimeout();

/**
 * Return the timeout used to wait for a momentarily run operation.<br>
 * This time is expressed in <b>seconds</b>.
 *
 * @return The timeout as a <code>int</code>.
 */
public int getTinyTimeout();

/**
 * Return the topology used while running the scenario.
 *
 * @return The topology as {@link Topology}.
 */
public Topology getTopology();
}
