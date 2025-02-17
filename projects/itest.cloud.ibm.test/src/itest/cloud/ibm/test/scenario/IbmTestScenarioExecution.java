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

import itest.cloud.ibm.scenario.IbmScenarioExecution;

/**
 * Manage the scenario execution.
 * <p>
 * This is the concrete class of this hierarchy which has to create the specific
 * scenario data object in the {@link #initData()} method.
 * </p>
 * @see IbmTestScenarioData
 */
public class IbmTestScenarioExecution extends IbmScenarioExecution {

public IbmTestScenarioExecution() {
}

/**
 * Override the superclass implementation to create the specific <b>demo</b>
 * scenario data object (see {@link IbmTestScenarioData}).
 */
@Override
protected void initData() {
	this.data = new IbmTestScenarioData();
}

/**
 * {@inheritDoc}
 *
 * @return The scenario data as a {@link IbmTestScenarioData}.
 */
@Override
public IbmTestScenarioData getData() {
	return (IbmTestScenarioData) super.getData();
}
}
