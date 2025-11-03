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

import static itest.cloud.ibm.entity.mobile.AssetContext.TEAM_CONTENT;
import static itest.cloud.scenario.ScenarioDataConstants.CLASS_INDICATOR_OF_DEPENDENCY;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import itest.cloud.annotation.Dependency;
import itest.cloud.ibm.scenario.ca.mobile.CaMobileScenarioStep;
import itest.cloud.ibm.test.scenario.IbmTestScenarioStepRunner;

/**
 * This class defines a set of tests to validate dashboards and their associated functionality.
 * <p>
 * The following is a list of tests associated with this test suite:
 * <ul>
 * <li>{@link #test01_CreateBoard3()}: Create Board .</li>
 * </ul>
 * </p>
 */
@RunWith(IbmTestScenarioStepRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Step02_DashboardTests extends CaMobileScenarioStep {

	private static final String TEST_PIN_CHART_FROM_DASHBOARD_IN_BOARD3 = CLASS_INDICATOR_OF_DEPENDENCY + ".test03_PinChartFromDashboardInBoard3";
	private static final String TEST_CREATE_BOARD3 = CLASS_INDICATOR_OF_DEPENDENCY + ".test01_CreateBoard3";
	private static final String TEST_OPEN_DASHBOARD = CLASS_INDICATOR_OF_DEPENDENCY + ".test02_OpenDashboard";

	private static final String DASHBOARD = "Agent quarterly performance";
	private static final String CALLS_RESOLVED_CHART = "Calls Resolved";
	private static final String BOARD3 = "Automation Board 3";
//	private static final String BOARD4 = "Automation Board 4";

/**
 * Create Board 3.
 */
@Test
public void test01_CreateBoard3() {
	createBoard(BOARD3, false /*force*/, getTestUser());
}

/**
 * Open dashboard 'Agent quarterly performance'.
 */
@Test
public void test02_OpenDashboard() {
	openDashboard(TEAM_CONTENT, DASHBOARD, false /*force*/, getTestUser());
}

/**
 * Pin the chart 'Calls Resolved' from the dashboard 'Agent quarterly performance' in Board 3.
 */
@Test
@Dependency({TEST_CREATE_BOARD3, TEST_OPEN_DASHBOARD})
public void test03_PinChartFromDashboardInBoard3() {
	pinChartFromDashboardInBoard(TEAM_CONTENT, DASHBOARD, CALLS_RESOLVED_CHART, BOARD3, true /*force*/, getTestUser());
}

/**
 * Unpin the chart 'Calls Resolved' from the dashboard 'Agent quarterly performance' in Board 3.
 */
@Test
@Dependency({TEST_PIN_CHART_FROM_DASHBOARD_IN_BOARD3})
public void test04_UnpinChartFromBoard3() {
	unpinChartFromBoard(BOARD3, CALLS_RESOLVED_CHART, getTestUser());
}
}