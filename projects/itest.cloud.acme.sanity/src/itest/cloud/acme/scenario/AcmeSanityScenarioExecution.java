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

/**
 * Manage the scenario execution.
 * <p>
 * This is the concrete class of this hierarchy which has to create the specific
 * scenario data object in the {@link #initData()} method.
 * </p>
 * @see AcmeSanityScenarioData
 */
public class AcmeSanityScenarioExecution extends AcmeScenarioExecution {

public AcmeSanityScenarioExecution() {
}

/**
 * Override the superclass implementation to create the specific <b>demo</b>
 * scenario data object (see {@link AcmeSanityScenarioData}).
 */
@Override
protected void initData() {
	this.data = new AcmeSanityScenarioData();
}

/**
 * {@inheritDoc}
 *
 * @return The scenario data as a {@link AcmeSanityScenarioData}.
 */
@Override
public AcmeSanityScenarioData getData() {
	return (AcmeSanityScenarioData) super.getData();
}
}
