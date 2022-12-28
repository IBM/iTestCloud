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

import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.getParameterValue;

import java.io.File;

import com.ibm.itest.cloud.common.scenario.ScenarioData;

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
public class AcmeSanityScenarioData extends AcmeScenarioData implements AcmeSanityScenarioConstants {

	private final File pictureFile;

public AcmeSanityScenarioData() {
	super();

	// Init data
	this.pictureFile = new File(getParameterValue("pictureFile", "artifacts/robot.png"));
}

/**
 * Return the profile picture file for the test users.
 *
 * @return The profile picture file.
 */
public File getPictureFile() {
	return this.pictureFile;
}
}
