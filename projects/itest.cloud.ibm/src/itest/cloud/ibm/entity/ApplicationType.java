/*********************************************************************
 * Copyright (c) 2018, 2024 IBM Corporation and others.
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
package itest.cloud.ibm.entity;

import static java.util.regex.Pattern.DOTALL;

import java.util.regex.Pattern;

/**
 * This enum defines the types of the application.
 */
public enum ApplicationType {
	/** IBM Cognos Analytics application **/
	CA("IBM.*Cognos Analytics"),
	/** IBM Cloud Pack for Data application **/
	CPD("IBM.*Cloud Pak for Data"),
	/** IBM watsonx BI Assistant application **/
	WXBI("IBM.*watsonx BI Assistant|" + CPD.getTitle());

	final Pattern title;

ApplicationType(final String name) {
	this.title = Pattern.compile(name, DOTALL);
}

ApplicationType() {
	this.title = null;
}

/**
 * Return a pattern of the title of the application.
 *
 * @return a pattern of the title of the application as {@link Pattern}.
 */
public Pattern getTitle() {
	return this.title;
}
}
