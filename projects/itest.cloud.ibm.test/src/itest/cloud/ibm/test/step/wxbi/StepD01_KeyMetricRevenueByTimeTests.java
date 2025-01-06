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
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import itest.cloud.ibm.annotation.wxbi.CloudTest;
import itest.cloud.ibm.test.scenario.IbmTestScenarioStepRunner;

/**
 * This class defines a set of tests for validating various aspects of the key metric 'Revenue by time'.
 * <p>
 * The following is a list of tests associated with this test suite:
 * <ul>
 * <li>{@link #getKeyMetricName()}: Return the name of the key metric.</li>
 * </ul>
 * </p>
 */
@CloudTest
@RunWith(IbmTestScenarioStepRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StepD01_KeyMetricRevenueByTimeTests extends StepXX_DefaultKeyMetricTests {

@Override
protected String getKeyMetricName() {
	return "Revenue by time";
}
}