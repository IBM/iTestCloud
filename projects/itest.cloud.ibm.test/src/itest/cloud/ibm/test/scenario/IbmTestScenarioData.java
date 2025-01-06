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

import static itest.cloud.scenario.ScenarioUtil.getParameterValue;

import itest.cloud.ibm.scenario.IbmScenarioData;
import itest.cloud.scenario.ScenarioData;

/**
 * Manage data needed while running the scenario.
 * <p>
 * Following information are needed to run the scenario:
 * <ul>
 * </ul>
 * </p><p>
 * These information can be modified either through parameter and/or by specifying
 * a prefix ( see {@link ScenarioData#getPrefix()}) put before each name to avoid
 * conflict in case the scenario was run several time on the same topology.<br>
 * </p>
 */
public class IbmTestScenarioData extends IbmScenarioData implements IbmTestScenarioConstants {

	private final String testUserCloudAccount;
//	private final File pictureFile;
	private final String conversationName;
	private final String chartName;

public IbmTestScenarioData() {
	super();

	// Init data
	this.testUserCloudAccount = getParameterValue("testUserCloudAccount", getTestUser().getName());
	this.conversationName = getParameterValue("conversationName", this.prefix + "My Conversation");
	this.chartName = getParameterValue("chartName", this.prefix + "My Chart");
//	this.pictureFile = new File(getParameterValue("pictureFile", "artifacts/robot.png"));
}

/**
 * Return the name of the chart to be used for creating key metrics.
 *
 * @return The name of the chart to be used for creating key metrics as {@link String}.
 */
public String getChartName() {
	return this.chartName;
}

/**
 * Return the conversation.
 *
 * @return The name of the conversation as {@link String}.
 */
public String getConversationName() {
	return this.conversationName;
}

///**
// * Return the profile picture file for the test users.
// *
// * @return The profile picture file.
// */
//public File getPictureFile() {
//	return this.pictureFile;
//}

/**
 * Return the name of the cloud account associated with the default user.
 *
 * @return The name of the cloud account associated with the default user as {@link String}.
 */
public String getTestUserCloudAccount() {
	return this.testUserCloudAccount;
}
}