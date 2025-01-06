/*********************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
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
package itest.cloud.ibm.test.scenario.ca;

import org.junit.runner.RunWith;

import itest.cloud.ibm.scenario.ca.CaScenarioStep;
import itest.cloud.ibm.test.scenario.IbmTestScenarioData;
import itest.cloud.ibm.test.scenario.IbmTestScenarioExecution;
import itest.cloud.scenario.ScenarioStepRunner;

/**
 * Manage common functionalities of the CA scenario steps.
 */
@RunWith(ScenarioStepRunner.class)
public class CaTestScenarioStep extends CaScenarioStep implements CaTestScenarioConstants {

/**
 * {@inheritDoc}
 *
 * @return The scenario execution as {@link IbmTestScenarioData}
 */
@Override
protected IbmTestScenarioData getData() {
	return (IbmTestScenarioData) super.getData();
}

/**
 * {@inheritDoc}
 *
 * @return The scenario execution as a {@link IbmTestScenarioExecution}.
 */
@Override
protected IbmTestScenarioExecution getScenarioExecution() {
	return (IbmTestScenarioExecution) super.getScenarioExecution();
}
}