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
package com.ibm.itest.cloud.common.core.nls;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.getParameterValue;
import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.println;

import java.util.*;

/**
 * Class to manage scenario NLS messages.
 */
public abstract class NlsMessages {

	/**
	 * Supported locales to run scenario.
	 */
	public enum Supported {
		US(Locale.US);
		Locale locale;
		Supported(final Locale loc) {
			this.locale = loc;
		}
		protected Locale getLocale() {
        	return this.locale;
        }
		@Override
        public String toString() {
	        return this.locale.toString();
        }
	}

	/* Fields */
	private final Locale scenarioLocale;
	protected final ResourceBundle scenarioBundle;

public NlsMessages() {

	// Init locale
	String locale = getParameterValue("locale");
	Locale selectedLocale = null;
	if (locale == null) {
		String country = getParameterValue("locale.country");
		String language = getParameterValue("locale.language");
		if (language == null) {
			if (country != null) {
				println("locale.country argument has been set to '"+country+"' but it will be ignored as no language was specified!");
			}
			selectedLocale = Locale.getDefault();
		} else if (country == null) {
			selectedLocale = new Locale(language);
		} else {
			selectedLocale = new Locale(language, country);
		}
	} else {
		selectedLocale = Supported.valueOf(locale).getLocale();
	}
	this.scenarioLocale = selectedLocale;

	// Init bundle
	this.scenarioBundle = ResourceBundle.getBundle(bundleName(), this.scenarioLocale);
}

/**
 * The bundle name of the scenario messages.
 * <p>
 * Note that this name must include both the package and the properties file name,
 * e.g. <code>com.ibm.team.fvt.tests.reporting.jrs.bvt.scenario.nls.messages</code>
 * </p>
 * @return The bundle name as a {@link String}.
 */
abstract protected String bundleName();

/**
 * Return the NLS string value for the given key using the scenario locale.
 * <p>
 * If the key is not defined, returns the key surrounded by '!' not signify that
 * the corresponding key is not supported yet.
 * </p>
 * @param key The key of the searched string in messages properties file.
 * @return The string value as a {@link String}.
 */
public final String getNLSString(final String key) {
	try {
		return this.scenarioBundle.getString(key);
	}
	catch (MissingResourceException e) {
		String fakeValue = '!' + key + '!';
		println("WARNING: There's no string value for key '"+key+"', using a fake value instead: "+fakeValue);
		return fakeValue;
	}
}

}
