/*********************************************************************
 * Copyright (c) 2015, 2022 IBM Corporation and others.
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
package itest.cloud.annotation;

/**
 * Annotation to identify a scenario.
 * <p>
 * This annotation accept following argument:
 * <ul>
 * <li><b>type</b>: The scenario type, all these values are exclusive.
 * <ul>
 * <li><b>"private"</b>: Used for a valid scenario supposed to be run in private by the team who created it. This is the default value</li>
 * <li><b>"pipeline"</b>: Used for a valid scenario supposed to be run in the CT pipeline.</li>
 * <li><b>"monitored"</b>: Used for a valid scenario supposed to be run in the CT pipeline <b>and monitored</b>. That means its results will be reported in Scenarios Pipeline dashboard.</li>
 * <li><b>"perfs"</b>: Used for a valid scenario used for performances purpose.</li>
 * <li><b>"demo"</b>: Used for a valid scenario created for demonstration purpose.</li>
 * </ul>
 * </li>
 * <li><b>runs</b>: The number of runs in CT pipeline. This argument only has a meaning when scenario type is either <code>"pipeline"</code> or <code>"monitored"</code>.</li>
 * </ul>
 * </li>
 * </ul>
 * </p><p>
 * So, this annotation can be used in four different ways:
 * <ul>
 * <li><code>&#064;Scenario</code>: Valid scenario which does not run in CT pipeline.</li>
 * <li><code>&#064;Scenario("monitored")</code>: Valid scenario run once in CT pipeline and <b>monitored</b>.</li>
 * <li><code>&#064;Scenario(value="monitored", runs=2)</code>: Valid scenario run twice in CT pipeline, both runs are <b>monitored</b>.</li>
 * <li><code>&#064;Scenario("pipeline")</code>: Valid scenario run once in CT pipeline but <b>NOT monitored</b>.</li>
 * <li><code>&#064;Scenario("demo")</code>: Valid scenario class run only for demonstration purpose (ie. not in CT pipeline).</li>
 * <li><code>&#064;Scenario("perfs")</code>: Valid scenario class run only for performances purpose (ie. not in CT pipeline).</li>
 * </ul>
 * </p>
 */
public @interface Scenario {
	String value() default "pipeline";
	int runs() default 1;
}
