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
package com.ibm.itest.cloud.common.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.ibm.itest.cloud.common.scenario.ScenarioData;

/**
 * Annotation to identify whether a step or test related to the Google Chrome borwser.
 * <p>
 * Such a step or test will be run if the parameter <b>runGoogleChromeTests</b> is set to true,
 * and will not be run if the parameter is set to false (see {@link ScenarioData}).
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GoogleChromeTest {
}
