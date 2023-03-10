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
package itest.cloud.acme.config;

import static itest.cloud.scenario.ScenarioUtils.getParameterIntValue;

import itest.cloud.config.Timeouts;

/**
 * This class defines the timeouts.
 * <p>
 * Current available timeouts are:
 * <ul>
 * <li><code>"catalogProcessingCompletionTimeout"</code>: {@link #CATALOG_PROCESSING_COMPLETION_TIMEOUT}</li>
 * </ul>
 * </p>
 */
public class AcmeTimeouts extends Timeouts {

	/**
	 * Default timeout for the processing of the assets in a catalog to be completed.
	 * <p>
	 * The value is 30 minutes.
	 * </p>
	 */
	public static final int CATALOG_PROCESSING_COMPLETION_TIMEOUT = getParameterIntValue("catalogProcessingCompletionTimeout", 30);

}