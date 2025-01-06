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
package itest.cloud.ibm.annotation.wxbi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import itest.cloud.ibm.topology.WxbiApplication;

/**
 * Annotation to identify whether a step or test is related to the cloud offering of the WXBI application.
 * <p>
 * Such a step or test will be run if the URL of the application specified by the parameter <b>applications</b>
 * contains the keyword 'cpd' or will not be run otherwise (see {@link WxbiApplication}).
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CloudTest {
}
