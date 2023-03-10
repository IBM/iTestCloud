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
package itest.cloud.acme.scenario;

import static itest.cloud.scenario.ScenarioUtils.getParameterIntValue;
import static itest.cloud.scenario.ScenarioUtils.sleep;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import itest.cloud.acme.config.AcmeConfig;
import itest.cloud.acme.config.AcmeConstants;
import itest.cloud.acme.topology.AcmeTopology;
import itest.cloud.scenario.ScenarioExecution;

/**
 * Manage scenario execution.
 */
public class AcmeScenarioExecution extends ScenarioExecution {

	// Run flavors
	final int delay;

public AcmeScenarioExecution() {
	super();
    this.delay= getParameterIntValue(AcmeConstants.STEP_DELAY_ID);
}

/**
 * {@inheritDoc}
 *
 * @return The Bluemix scenario configuration as {@link AcmeConfig}.
 */
@Override
public AcmeConfig getConfig() {
	return (AcmeConfig) this.config;
}

/**
 * {@inheritDoc}
 *
 * @return the scenario data as a {@link AcmeScenarioData}.
 */
@Override
public AcmeScenarioData getData() {
	return (AcmeScenarioData) super.getData();
}

/**
 * {@inheritDoc}
 *
 * @return The Bluemix scenario topology as {@link AcmeTopology}.
 */
@Override
public AcmeTopology getTopology() {
	return (AcmeTopology) super.getTopology();
}

@Override
protected void initConfig() {
	this.config = new AcmeConfig();
}

@Override
protected void initData() {
	this.data = new AcmeScenarioData();
}

@Override
public void runTest(final Statement statement, final FrameworkMethod frameworkMethod, final Object target, final boolean isNewStep) throws Throwable {
	super.runTest(statement, frameworkMethod, target, isNewStep);
	if (this.delay > 0) sleep(this.delay);
}
}
