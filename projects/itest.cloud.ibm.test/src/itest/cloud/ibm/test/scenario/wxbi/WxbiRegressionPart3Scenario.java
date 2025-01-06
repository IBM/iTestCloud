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
package itest.cloud.ibm.test.scenario.wxbi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import itest.cloud.annotation.Scenario;
import itest.cloud.ibm.test.scenario.IbmTestScenarioRunner;
import itest.cloud.ibm.test.step.wxbi.StepA01_PrerequisiteTests;

@Scenario("WatsonX BI Assistant Regression Scenario Part 3")
@RunWith(IbmTestScenarioRunner.class)
@SuiteClasses({
	StepA01_PrerequisiteTests.class,

//	StepD01_KeyMetricRevenueByTimeTests.class,
//	StepD02_KeyMetricTotalRevenueByCountryTests.class,
//	StepD03_KeyMetricRevenueComparedToPlannedRevenueTests.class,
//	StepD05_KeyMetricTop10ProductBrandsByRevenueTests.class,
//	StepD04_KeyMetricRevenueComparedToPlannedRevenueByProductLineTests.class,
//	StepD06_KeyMetricProductRevenueShareTests.class,
//
//	StepZ01_CleanupTests.class
})
public class WxbiRegressionPart3Scenario {
}
