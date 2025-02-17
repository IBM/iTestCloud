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

import itest.cloud.ibm.scenario.IbmScenarioStepRunner;

/**
 * Manage a scenario step JUnit run.
 * <p>
 * This is the concrete class of this hierarchy which has to create the specific
 * scenario execution object (see {@link IbmTestScenarioExecution}) in the case
 * the scenario step is executed as a single JUnit test class.
 * </p>
 */
public class IbmTestScenarioStepRunner extends IbmScenarioStepRunner {

public IbmTestScenarioStepRunner(final Class< ? > klass) throws InitializationError {
	super(klass);
}

@Override
protected void startExecution() {
	this.scenarioExecution = new IbmTestScenarioExecution();
}

}
