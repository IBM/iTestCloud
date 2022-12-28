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
package com.ibm.itest.cloud.acme.scenario;

import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

/**
 * Manage the scenario JUnit run.
 * <p>
 * This is the concrete class of this hierarchy which has to create the specific
 * scenario execution object (see {@link AcmeSanityScenarioExecution}).
 * </p><p>
 * Secondarily, it also defines the name of the main suite which is displayed
 * in the JUnit view when launching it.
 * </p>
 */
public class AcmeSanityScenarioRunner extends AcmeScenarioRunner {

public AcmeSanityScenarioRunner(final Class< ? > klass, final RunnerBuilder builder) throws InitializationError {
	super(klass, builder);
}

@Override
protected String getName() {
	return "APS Portal Demo Scenario";
}

/**
 * Return the scenario data.
 *
 * @return The data as a {@link AcmeSanityScenarioData}.
 */
@Override
protected AcmeSanityScenarioData getScenarioData(){
	return getScenarioExecution().getData();
}

@Override
public AcmeSanityScenarioExecution getScenarioExecution() {
	return (AcmeSanityScenarioExecution) super.getScenarioExecution();
}

@Override
protected void startExecution() {
	this.scenarioExecution = new AcmeSanityScenarioExecution();
}
}
