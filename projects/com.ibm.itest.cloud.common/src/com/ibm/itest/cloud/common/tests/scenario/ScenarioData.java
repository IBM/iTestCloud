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
package com.ibm.itest.cloud.common.tests.scenario;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.getParameterBooleanValue;
import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.getParameterValue;

import java.io.File;

import com.ibm.itest.cloud.common.config.IUser;

/**
 * Manage scenario general data.
 * <p>
 * A this root level, two data are defined or expected:
 * <ol>
 * <li><code>testPrefix</code>: this is a prefix which will be applied to all properties
 * of each user.<br>
 * TODO Give an example...</li>
 * <li>Test User: Provide access to a default test user that subclasses will initialize
 * (see {@link #initUsers()})</li>
 * </ol>
 * </p><p>
 * As this class is supposed to be overridden in each scenario layer, we are not
 * using interface to define API. Public and protected methods are considered as
 * API, hence supported in the long term.
 * </p><p>
 * Following public API methods are available:
 * <ul>
 * <li>{@link #getArtifactsDir()}: Return the directory containing the artifacts of a test scenario.</li>
 * artifacts of the test plug-in are located.</li>
 * <li>{@link #getDownloadDir()}: Return the directory containing the downloaded artifacts of a test scenario.</li>
 * <li>{@link #getPrefix()}: Return the prefix to apply to all data created during
 * scenario execution (project, users, etc.).</li>
 * <li>{@link #getTestUser()}: Return the default user utilized for a scenario.</li>
 * </ul>
 * </p><p>
 * Following internal methods are available:
 * <ul>
 * <li>{@link #initPrefix()}: Init prefix which will be used for all users.</li>
 * <li>{@link #initUsers()}: Init users which will be used all over the scenario steps.</li>
 * </ul>
 * </p>
 */
public abstract class ScenarioData implements ScenarioDataConstants {

	// General data
	protected String prefix;
	protected final File artifactsDir;

	// Users
	protected IUser testUser;

protected ScenarioData() {
	initPrefix();
	initUsers();
	this.artifactsDir = new File(getParameterValue("artifacts.dir", "artifacts"));
}

/**
 * Return the directory containing the artifacts of a test scenario.
 *
 * @return The directory containing the artifacts of a test scenario as {@link File}.
 */
public File getArtifactsDir() {
	return ARTIFACTS_DIR;
}

/**
 * Return the directory containing the downloaded artifacts of a test scenario.
 *
 * @return The directory containing the downloaded artifacts of a test scenario as {@link File}.
 */
public File getDownloadDir(){
	return DOWNLOAD_DIR;
}

/**
 * Return the prefix to use when creating an application item.
 *
 * @return The prefix as a {@link String}
 */
public String getPrefix() {
	return this.prefix;
}

/**
 * Return the default user utilized for a scenario.
 *
 * @return The test user as {@link IUser}.
 */
public IUser getTestUser() {
	return this.testUser;
}

/**
 * Init prefix which will be used for all users.
 * <p>
 * To set an explicit prefix set its value using the scenario argument
 * {@link ScenarioDataConstants#TEST_PREFIX_PARAM_ID}.
 * </p><p>
 * By default there's no prefix, ie. it's an empty string.
 * </p><p>
 * Note that if {@link ScenarioDataConstants#RANDOM_PREFIX_PARAM_ID} argument
 * is <code>true</code>, the prefix will be initialized with a random long value got
 * from current time.
 * </p>
 */
protected void initPrefix() {
	if (getParameterBooleanValue(RANDOM_PREFIX_PARAM_ID)) {
		// Assign a random prefix if requested by user.
		this.prefix = Long.toString(System.currentTimeMillis());
	} else {
		// Otherwise, initialize the prefix to the given or
		// default appropriately.
		this.prefix = getParameterValue(TEST_PREFIX_PARAM_ID, TEST_PREFIX_PARAM_DEFAULT_VALUE);
	}
}

/**
 * Init users which will be used all over the scenario steps.
 */
protected abstract void initUsers();
}
