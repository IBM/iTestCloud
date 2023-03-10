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
package itest.cloud.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import itest.cloud.scenario.ScenarioExecution;
import itest.cloud.scenario.ScenarioStep;

/**
 * Annotation to identify non-failure blocker test in failure blocker step.
 * <p>
 * When this annotation is used on a test, it relaxes the blocking status got from
 * the step (if any). Then, even if the entire step is considered as failure blocker,
 * the test tagged with the current annotation will <b>not</b> stop the scenario
 * execution in case it fails (except if the STOP_ON_FAILURE_ID argument
 * (see {@link ScenarioExecution}) value is set to <code>true</code>.
 * </p><p>
 * Note that this annotation is necessary only  when a {@link FailureBlocker}
 * annotation has been used on a step class (ie. subclass of {@link ScenarioStep}).
 * Otherwise that does not bring any additional behavior.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FailureRelaxer {

}
