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
package itest.cloud.ibm.topology;

import static itest.cloud.ibm.topology.IbmTopology.ApplicationInfo.*;
import static itest.cloud.scenario.ScenarioUtil.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;

import itest.cloud.ibm.config.IbmConstants;
import itest.cloud.scenario.error.ScenarioFailedError;
import itest.cloud.topology.Application;
import itest.cloud.topology.Topology;

/**
 * Class to manage the topology.
 * <p>
 * So far this is reduced at a single application.
 * </p><p>
 * Following public features are defined by this page:
 * <ul>
 * <li>{@link #isDistributed()}: Returns whether this topology is distributed across multiple servers.</li>
 * <li>{@link #getCaApplication()}: Returns the Cognos Analytics application.</li>
 * <li>{@link #getCaMobileApplication()}: Returns the Cognos Analytics Mobile application.</li>
 * <li>{@link #getWxbiApplication()}: Returns the WatsonX BI Assistant application.</li>
 * </ul>
 * </p>
 */
public class IbmTopology extends Topology implements IbmConstants {
	/**
	 * Enumeration of possible applications which can be added to the current topology.
	 */
	public enum ApplicationInfo {
		CA(CaApplication.class),
		CA_MOBILE(CaMobileApplication.class),
		WXBI(WxbiApplication.class);

		static ApplicationInfo fromId(final String id) {
			for (ApplicationInfo appInfo: values()) {
				if (appInfo.toString().equalsIgnoreCase(id)) {
					return appInfo;
				}
			}
			throw new ScenarioFailedError("Cannot find information from application ID '"+id+"'");
		}
		Class<? extends Application> clazz;
		ApplicationInfo(final Class<? extends Application> appClass) {
			this.clazz = appClass;
		}
	}

/**
 * Create a new application instance from the given information and application URL.
 *
 * @param appInfo The application information to use
 * @param appUrl The URL that will be used to initialize the application
 * @return The new created application
 * @throws ScenarioFailedError If any error occurs while creating the application
 */
public static Application createApplicationInstance(final ApplicationInfo appInfo, final String appUrl) throws ScenarioFailedError {
	try {
		Constructor<? extends Application> constructor = appInfo.clazz.getConstructor(String.class);
		return constructor.newInstance(appUrl);
	}
	catch (InvocationTargetException ite) {
		Throwable ex = ite.getTargetException();
		printException(ex);
		throw new ScenarioFailedError(ex.getMessage());
	}
	catch (Exception ex) {
		printException(ex);
		throw new ScenarioFailedError(ex.getMessage());
	}
}

///**
// * Create and add the given application to the current topology.
// * <p>
// * It's a no-op if the application already exists for the current tompology. Hence,
// * it's safe to call this method several time for the same application.
// * </p>
// * @param appInfo The application information to use
// * @param url The URL that will be used to initialize the application
// * @param name The name of the application (usually the CLM Service name)
// * @return The new created application or the existing one if it was already added
// * to the topology prior to the call
// */
//public Application addApplication(final ApplicationInfo appInfo, final String url, final String name) {
//	for (Application app: this.applications) {
//		if (url.startsWith(app.getLocation())) {
//			return app;
//		}
//	}
//	Application application = createApplicationInstance(appInfo, url);
//	if (name != null) {
//		application.setName(name);
//	}
//	addApplication(application);
//	return application;
//}

/**
 * Returns the application with the given name.
 *
 * @return The application as a {@link WxbiApplication}.
 */
private Application getApplicationFromName(final String name) {
	for (Application app: this.applications) {
		if (app.getName().equalsIgnoreCase(name)) {
			return app;
		}
	}
	return null;
}

/**
 * Returns the Cognos Analytics application.
 *
 * @return The Cognos Analytics application as {@link CaApplication}.
 */
public CaApplication getCaApplication() {
	return (CaApplication) getApplicationFromName(CA.name());
}

/**
 * Returns the Cognos Analytics Mobile application.
 *
 * @return The Cognos Analytics Mobile application as {@link CaMobileApplication}.
 */
public CaMobileApplication getCaMobileApplication() {
	return (CaMobileApplication) getApplicationFromName(CA_MOBILE.name());
}

/**
 * Returns the WatsonX BI Assistant application.
 *
 * @return The WatsonX BI Assistant application as {@link WxbiApplication}.
 */
public WxbiApplication getWxbiApplication() {
	return (WxbiApplication) getApplicationFromName(WXBI.name());
}

@Override
protected void initApplications() {
	StringTokenizer applicationsInfo = new StringTokenizer(getParameterValue(PARAMETER_APPLICATIONS).trim(), ",");
	while(applicationsInfo.hasMoreTokens()) {
		final String[] appInfo = applicationsInfo.nextToken().split(";");
		final String name = appInfo[(appInfo.length > 2) ? 2:0];
		final ApplicationInfo applicationInfo = ApplicationInfo.fromId(name);
		// An application URL is inapplicable for a mobile application running on an emulator.
		// Therefore, the predefined URL is utilized as the application URL in such a situation.
		final String appUrl = (applicationInfo == CA_MOBILE) ? MOBILE_APPLICATION_URL : appInfo[1];
		final Application application = createApplicationInstance(applicationInfo, appUrl);
		application.setName(name);
		this.applications.add(application);
	}
}

@Override
public boolean isDistributed() {
	return false;
}
}