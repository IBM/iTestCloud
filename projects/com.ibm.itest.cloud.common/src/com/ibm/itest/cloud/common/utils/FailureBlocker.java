/*********************************************************************
 * Copyright (c) 2012, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.common.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.ibm.itest.cloud.common.scenario.ScenarioExecution;
import com.ibm.itest.cloud.common.scenario.ScenarioStep;

/**
 * Annotation to identify step and/or test blocking scenario execution when
 * failing.
 * <p>
 * A <b>failure blocker</b> test must pass. In case it fails, then the scenario
 * execution will stop whatever the STOP_ON_FAILURE_ID argument value is
 * (see {@link ScenarioExecution}).
 * </p><p>
 * When set on a step class (ie. subclass of {@link ScenarioStep}), it means
 * that <b>all</b> tests will be considered as failure blockers. This can be
 * overridden for any test in such step by using the {@link FailureRelaxer} interface.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FailureBlocker {

}
