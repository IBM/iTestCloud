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
package itest.cloud.acme.topology;

import static itest.cloud.scenario.ScenarioUtils.getParameterValue;
import static itest.cloud.scenario.ScenarioUtils.printException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;

import itest.cloud.acme.config.AcmeConstants;
import itest.cloud.scenario.errors.ScenarioFailedError;
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
 * <li>{@link #getApsPortalApplication()}: Returns the topology single application.</li>
 * </ul>
 * </p>
 */
public class AcmeTopology extends Topology implements AcmeConstants {

	/**
	 * Enumeration of possible applications which can be added to the current topology.
	 */
	public enum ApplicationInfo {
		AcmePortal(AcmeApplication.class);

		Class<? extends Application> clazz;
		ApplicationInfo(final Class<? extends Application> appClass) {
			this.clazz = appClass;
		}
		static ApplicationInfo fromId(final String id) {
			for (ApplicationInfo appInfo: values()) {
				if (appInfo.toString().equalsIgnoreCase(id)) {
					return appInfo;
				}
			}
			throw new ScenarioFailedError("Cannot find information from application ID '"+id+"'");
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

/**
 * Create and add the given application to the current topology.
 * <p>
 * It's a no-op if the application already exists for the current tompology. Hence,
 * it's safe to call this method several time for the same application.
 * </p>
 * @param appInfo The application information to use
 * @param url The URL that will be used to initialize the application
 * @param name The name of the application (usually the CLM Service name)
 * @return The new created application or the existing one if it was already added
 * to the topology prior to the call
 */
public Application addApplication(final ApplicationInfo appInfo, final String url, final String name) {
	for (Application app: this.applications) {
		if (url.startsWith(app.getLocation())) {
			return app;
		}
	}
	Application application = createApplicationInstance(appInfo, url);
	if (name != null) {
		application.setName(name);
	}
	addApplication(application);
	return application;
}

/**
 * Returns the application with the given name.
 *
 * @return The application as a {@link AcmeApplication}.
 */
private Application getApplicationFromName(final String name) {
	for (Application app: this.applications) {
		if (app.getName().equals(name)) {
			return app;
		}
	}
	return null;
}

/**
 * Returns the application.
 *
 * @return The application as {@link AcmeApplication}.
 */
public AcmeApplication getApsPortalApplication() {
	return (AcmeApplication) getApplicationFromName("acme");
}

@Override
protected void initApplications() {
	StringTokenizer applicationInfo = new StringTokenizer(getParameterValue("applications").trim(), ",");
	while(applicationInfo.hasMoreTokens()) {
		String[] appInfo = applicationInfo.nextToken().split(";");
		Application application = createApplicationInstance(ApplicationInfo.fromId(appInfo[0]), appInfo[1]);
		if (appInfo.length > 2) {
			application.setName(appInfo[2]);
		} else {
			application.setName(appInfo[0]);
		}
		this.applications.add(application);
	}
}

@Override
public boolean isDistributed() {
	return false;
}
}