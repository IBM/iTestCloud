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

import itest.cloud.ibm.config.IbmUser;
import itest.cloud.scenario.ScenarioData;

/**
 * This class provide APIs for accessing data, which is used while executing test scenarios.
 * <p>
 * Following features are available on this page:
 * <ul>
 * </ul>
 * </p>
 */
public class IbmScenarioData extends ScenarioData implements IbmScenarioConstants {

public IbmScenarioData() {
	super();
}

@Override
protected void initUsers() {
	this.testUser = new IbmUser("tester");
}
}