/*********************************************************************
 * Copyright (c) 2012, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.common.tests.config;

import static com.ibm.itest.cloud.common.tests.config.Timeouts.*;

import com.ibm.itest.cloud.common.config.IConfig;
import com.ibm.itest.cloud.common.tests.topology.Topology;
import com.ibm.itest.cloud.common.tests.web.WebBrowser;
import com.ibm.itest.cloud.common.tests.web.WebPage;

/**
 * Manage the scenario configuration.
 * <p>
 * To run a scenario, following parameters need to be defined:
 * <ul>
 * <li><b>timeouts</b>: Timeouts on different tests operations, e.g. open a
 * web page (see {@link Timeouts} for more details).</li>
 * <li><b>topology</b>: The topology of the CLM applications (see {@link Topology}
 * for more details).</li>
 * <li><b>browser</b>: The browser used for the scenario (see {@link WebBrowser}
 * for more details).</li>
 * <li><b>screenshots directory</b>: the directories where screenshots taken
 * during the scenario has to be put. There are two directories, one for
 * screenshots taken when a failure occurs and one for screenshots taken just
 * for information.</li>
 * </ul>
 * </p>
 */
abstract public class Config implements IConfig {

	// General
	protected Topology topology;
	protected Timeouts timeouts;
	WebBrowser browser;

public Config() {
	// Init topology
	initTopology();
	// Init timeouts
	initTimeouts();
	// Init browser
	this.browser = WebBrowser.createInstance();
}

public Config(final Config config) {
	this.browser = config.browser;
	this.topology = config.topology;
	this.timeouts = config.timeouts;
}

/**
 * Return the timeout used to wait for a dialog to be closed.<br>
 * This time is expressed in <b>seconds</b>.
 *
 * @return The timeout as a <code>int</code>.
 */
@Override
public int closeDialogTimeout() {
	return CLOSE_DIALOG_TIMEOUT;
}

/**
 * Return the browser used while running the scenario.
 *
 * @return The browser as {@link WebBrowser}.
 */
@Override
public WebBrowser getBrowser() {
	return this.browser;
}

/**
 * Return the default timeout used to wait for an expected element in
 * the current web page. This time is expressed in <b>seconds</b>.
 *
 * @return The timeout as a <code>int</code>.
 */
@Override
public int getDefaultTimeout() {
	return DEFAULT_TIMEOUT;
}

/**
 * Return the default timeout used to wait for a downloading of a file to start.
 * This time is expressed in <b>seconds</b>.
 *
 * @return The timeout as a <code>int</code>.
 */
@Override
public int getDownloadStartTimeout() {
	return CLOSE_DIALOG_TIMEOUT;
}

/**
 * Return the timeout used to wait for a page to be loaded.<br>
 * This time is expressed in <b>seconds</b>.
 *
 * @return The timeout as a <code>int</code>.
 */
@Override
public int getOpenPageTimeout() {
	return OPEN_PAGE_TIMEOUT;
}

/**
 * Return the timeout used to wait for a short run operation.<br>
 * This time is expressed in <b>seconds</b>.
 *
 * @return The timeout as a <code>int</code>.
 */
@Override
public int getShortTimeout() {
	return SHORT_TIMEOUT;
}

/**
 * Return the timeout used to wait for an expected element in
 * the current web page. This time is expressed in <b>seconds</b>.
 *
 * @return The timeout as a <code>int</code>.
 * @deprecated Since 6.0.0 NG, use {@link #getDefaultTimeout()} instead.
 */
@Deprecated
public int getTimeout() {
	return DEFAULT_TIMEOUT;
}

/**
 * Return the timeouts used while running the scenario.
 *
 * @return The timeouts as {@link Timeouts}.
 * @deprecated Since 6.0.0 NG. Should NOT be used!
 */
@Deprecated
public Timeouts getTimeouts() {
	return this.timeouts;
}

/**
 * Return the timeout used to wait for a momentarily run operation.<br>
 * This time is expressed in <b>seconds</b>.
 *
 * @return The timeout as a <code>int</code>.
 */
@Override
public int getTinyTimeout() {
	return TINY_TIMEOUT;
}

/**
 * Return the topology used while running the scenario.
 *
 * @return The topology as {@link Topology}.
 */
@Override
public Topology getTopology() {
	return this.topology;
}

/**
 * Initialize the timeouts.
 * <p>
 * That needs to be overridden by the specific scenario to instantiate its own
 * object.
 * </p>
 */
abstract protected void initTimeouts();

/**
 * Initialize the topology.
 * <p>
 * That needs to be overridden by the specific scenario to instantiate its own
 * object.
 * </p>
 */
abstract protected void initTopology();

/**
 * Close the current browser session and open a new one. <p>
 *
 * <b>Warning:</b> This should not be called directly as the page cache needs to be reset after this call
 * and the current page needs to be reopened. Use startNewBrowserSession in {@link WebPage} instead.
 *
 * @return The newly opened browser as a {@link WebBrowser}
 */
public WebBrowser openNewBrowser() {
	this.browser.close();
	return this.browser = WebBrowser.createInstance();
}
}