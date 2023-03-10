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
package itest.cloud.acme.scenario;

import org.junit.runner.RunWith;

import itest.cloud.scenario.ScenarioStepRunner;

/**
 * Manage common functionalities to any scenario step.
 */
@RunWith(ScenarioStepRunner.class)
public class AcmeSanityScenarioStep extends AcmeScenarioStep implements AcmeSanityScenarioConstants {

/**
 * {@inheritDoc}
 *
 * @return The scenario execution as {@link AcmeSanityScenarioData}
 */
@Override
protected AcmeSanityScenarioData getData() {
	return (AcmeSanityScenarioData) super.getData();
}

/**
 * {@inheritDoc}
 *
 * @return The scenario execution as a {@link AcmeSanityScenarioExecution}.
 */
@Override
protected AcmeSanityScenarioExecution getScenarioExecution() {
	return (AcmeSanityScenarioExecution) super.getScenarioExecution();
}
}
