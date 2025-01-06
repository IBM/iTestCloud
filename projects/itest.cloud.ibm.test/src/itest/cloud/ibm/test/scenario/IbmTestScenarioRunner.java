/*********************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
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
package itest.cloud.ibm.test.scenario;

import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import itest.cloud.ibm.scenario.IbmScenarioRunner;

/**
 * Manage the scenario JUnit run.
 * <p>
 * This is the concrete class of this hierarchy which has to create the specific
 * scenario execution object (see {@link IbmTestScenarioExecution}).
 * </p><p>
 * Secondarily, it also defines the name of the main suite which is displayed
 * in the JUnit view when launching it.
 * </p>
 */
public class IbmTestScenarioRunner extends IbmScenarioRunner {

public IbmTestScenarioRunner(final Class< ? > klass, final RunnerBuilder builder) throws InitializationError {
	super(klass, builder);
}

@Override
protected String getName() {
	return "IBM Test Scenario Runner";
}

/**
 * Return the scenario data.
 *
 * @return The data as a {@link IbmTestScenarioData}.
 */
@Override
protected IbmTestScenarioData getScenarioData(){
	return getScenarioExecution().getData();
}

@Override
public IbmTestScenarioExecution getScenarioExecution() {
	return (IbmTestScenarioExecution) super.getScenarioExecution();
}

@Override
protected void startExecution() {
	this.scenarioExecution = new IbmTestScenarioExecution();
}
}
