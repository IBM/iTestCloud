/*********************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
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
package itest.cloud.ibm.scenario;

import static itest.cloud.scenario.ScenarioUtil.*;

import java.io.File;
import java.util.Properties;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.config.IbmConstants;
import itest.cloud.ibm.topology.IbmTopology;
import itest.cloud.scenario.ScenarioExecution;

/**
 * Manage scenario execution.
 */
public class IbmScenarioExecution extends ScenarioExecution {

	private static final String DEFAULT_KNOWN_ISSUES_FILE = "known-issues.properties";
	private static final String KNOWN_ISSUES_FILE_ID = "knownIssuesFile";

	// Run flavors
	final int delay;
	final Properties knownIssues;

public IbmScenarioExecution() {
	super();
    this.delay= getParameterIntValue(IbmConstants.STEP_DELAY_ID);
	final File knownIssuesFile = new File(ARTIFACTS_DIR,  getParameterValue(KNOWN_ISSUES_FILE_ID, DEFAULT_KNOWN_ISSUES_FILE));
	this.knownIssues = (knownIssuesFile.exists()) ? readPropertiesFile(knownIssuesFile) : null;
}

/**
 * {@inheritDoc}
 *
 * @return The Bluemix scenario configuration as {@link IbmConfig}.
 */
@Override
public IbmConfig getConfig() {
	return (IbmConfig) this.config;
}

/**
 * {@inheritDoc}
 *
 * @return the scenario data as a {@link IbmScenarioData}.
 */
@Override
public IbmScenarioData getData() {
	return (IbmScenarioData) super.getData();
}

/**
 * {@inheritDoc}
 *
 * @return The Bluemix scenario topology as {@link IbmTopology}.
 */
@Override
public IbmTopology getTopology() {
	return (IbmTopology) super.getTopology();
}

@Override
protected void initConfig() {
	this.config = new IbmConfig();
}

@Override
protected void initData() {
	this.data = new IbmScenarioData();
}

/**
 * Log a given test failure.
 *
 * @param throwable The exception associated with the test failure.
 *
 * @throws Throwable The exception associated with the test failure.
 */
@Override
protected void logTestFailure(final Throwable throwable) throws Throwable {
	final String fullTestPath = this.packageName + PERIOD_STRING + this.stepName + PERIOD_STRING + this.testName;
	// Decide whether the issue is known.
	final String knownIssue = this.knownIssues.getProperty(fullTestPath);

	// If it is an unknown issue, log it as-is.
	if(knownIssue == null) {
		println("	  -> This is an unknown issue.");
		throw throwable;
	}

	final String message = "This is a known issue and tracked by " + knownIssue;
	// If it is a known issue, create a new throwable with the same stack trace but with a refined message with info about the known issue.
	String refinedMessage = throwable.getMessage() + ". " + message.toString();
	println("	  -> " + message.toString());

	Throwable refinedthrowable;
	try {
		refinedthrowable = throwable.getClass().getConstructor(String.class).newInstance(refinedMessage);
	}
	catch (Exception e) {
		println("	  -> Following error occurred while creating exception with info about known issue:");
		e.printStackTrace();
		throw throwable;
	}

	refinedthrowable.setStackTrace(throwable.getStackTrace());
	throw refinedthrowable;
}

@Override
public void runTest(final Statement statement, final FrameworkMethod frameworkMethod, final Object target, final boolean isNewStep) throws Throwable {
	super.runTest(statement, frameworkMethod, target, isNewStep);
	if (this.delay > 0) sleep(this.delay);
}
}