/*********************************************************************
 * Copyright (c) 2024, 2025 IBM Corporation and others.
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
package itest.cloud.ibm.test.step.ca.mobile;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioStep;
import itest.cloud.ibm.test.scenario.IbmTestScenarioStepRunner;

/**
 * This class defines a set of tests to validate the prerequisites and their associated functionality.
 * <p>
 * The following is a list of tests associated with this test suite:
 * <ul>
 * <li>{@link #test01_ConnectApplicationToCaServer()}: Connect the mobile application to the Cognos Analytics server.</li>
 * </ul>
 * </p>
 */
@RunWith(IbmTestScenarioStepRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Step01_MobilePrerequisitesTests extends CaMobileScenarioStep {

	private static final String BOARD1 = "Board 1";
	private static final String BOARD2 = "Board 2";

/**
 * Connect the mobile application to the Cognos Analytics server.
 */
@Test
public void test01_ConnectApplicationToCaServer() {
	openBoardsPage(getTestUser());
}

/**
 * Create Board 1.
 */
@Test
public void test02_CreateBoard1() {
	createBoard(BOARD1, true /*force*/, getTestUser());
}

/**
 * Delete Board 1 via the Boards Page.
 */
@Test
public void test03_DeleteBoard1ViaBoardsPage() {
	deleteBoard(BOARD1, getTestUser());
}

/**
 * Recreate Board 1.
 */
@Test
public void test04_RecreateBoard1() {
	createBoard(BOARD1, false /*force*/, getTestUser());
}

/**
 * Open the 'Calls Resolved' chart in Board 2.
 */
@Test
public void test05_OpenCallsResolvedChartInBoard2() {
	openChart(BOARD2, "Calls Resolved", getTestUser());
}

/**
 * Open the 'AVG Cost/Call' chart in Board 2.
 */
@Test
public void test06_OpenAvgCostCallChartInBoard2() {
	openChart(BOARD2, "AVG Cost/Call", getTestUser());
}

/**
 * Delete Board 1 via the Boards Management Page.
 */
@Test
public void test07_DeleteBoard1ViaBoardsManagementPage() {
	deleteBoard(BOARD1, getTestUser());
}
}