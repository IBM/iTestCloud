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
package itest.cloud.ibm.test.step.wxbi;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import itest.cloud.annotation.Dependency;
import itest.cloud.ibm.annotation.wxbi.CloudTest;
import itest.cloud.ibm.page.dialog.wxbi.modeling.WxbiCreateMetricDefinitionDialog;
import itest.cloud.ibm.page.wxbia.modeling.WxbiModelingPage;
import itest.cloud.ibm.test.scenario.IbmTestScenarioStepRunner;
import itest.cloud.ibm.test.scenario.wxbi.WxbiTestScenarioStep;

/**
 * This class defines a set of tests to validate the free form tests and their associated functionality.
 * <p>
 * The following is a list of tests associated with this test suite:
 * <ul>
 * <li>{@link #test01_OpenModelingPage()}: Open the modeling page.</li>
 * <li>{@link #test02_SelectTableInSemanticModel()}: Select the table in the semantic model.</li>
 * <li>{@link #test03_ValidateGridTab()}: Validate the Grid tab.</li>
 * <li>{@link #test04_CreateMetricDefinition()}: Create a metric definition.</li>
 * </ul>
 * </p>
 */
@CloudTest
@RunWith(IbmTestScenarioStepRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StepE01_BasicModelingTests extends WxbiTestScenarioStep {

	private static final String GOSALES_AND_FORECAST_TABLE = "gosales_and_forecast";
	private static final String CALENDAR_YEAR_COLUMN = "CalendarYear";

	private static final String TEST_OPEN_MODELING_PAGE = CLASS_INDICATOR_OF_DEPENDENCY + ".test01_OpenModelingPage";
	private static final String TEST_SELECT_TABLE_IN_SEMANTIC_MODEL = CLASS_INDICATOR_OF_DEPENDENCY + ".test02_SelectTableInSemanticModel";

/**
 * Open the modeling page.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test01_OpenModelingPage() {
	openModelingPage(getData().getTestUser());
}

/**
 * Select the table in the semantic model.
 */
@Test
@Dependency({TEST_OPEN_MODELING_PAGE})
public void test02_SelectTableInSemanticModel() {
	selectItemInSemanticModel(GOSALES_AND_FORECAST_TABLE, getData().getTestUser());
}

/**
 * Validate the Grid tab.
 */
@Test
@Dependency({TEST_SELECT_TABLE_IN_SEMANTIC_MODEL})
public void test03_ValidateGridTab() {
	final WxbiModelingPage page = openModelingPage(getData().getTestUser());
	page.openGridTab();
}

/**
 * Create a metric definition.
 */
@Test
@Dependency({TEST_OPEN_MODELING_PAGE})
public void test04_CreateMetricDefinition() {
	final WxbiModelingPage page = openModelingPage(getData().getTestUser());
	final WxbiCreateMetricDefinitionDialog dialog = page.createMetricDefinition(CALENDAR_YEAR_COLUMN);
	dialog.cancel();
}
}