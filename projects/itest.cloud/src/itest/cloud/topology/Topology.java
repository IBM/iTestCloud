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
package itest.cloud.topology;

import static itest.cloud.scenario.ScenarioUtils.*;

import java.util.*;

import itest.cloud.config.User;
import itest.cloud.scenario.errors.ScenarioFailedError;

/**
 * Manage the scenario topology. A topology is made of applications
 * (e.g. CLM applications: JTS, CCM, QM, RM, and LPA) which can be deployed
 * either on collocated or distributed machines.
 * <p>
 * The topology has a {@link #baseUrl} computed from <code>"baseUrl"</code>
 * parameter. Each application might have its own address if the
 * <code>"baseUrl<i>APPLI</i>"</code> parameter is specified.
 * </p><p>
 * The topology manages user login to each application and typically provides
 * service to know whether a login is necessary before going to a specific location.
 * </p>
 */
abstract public class Topology {

	// Base URL
	protected String baseUrl;

	// Applications and servers
	protected List <Application> applications = new ArrayList<Application>();
	protected Map<String, List<Application>> servers = new HashMap<String, List<Application>>();

/**
 * Initialize the topology from a given CLM version.
 */
public Topology() {

	// Store base URL addresses
	this.baseUrl = getParameterValue("baseUrl", defaultBaseUrl());

	// Store applications
	initApplications();

	// Store servers
	initServers();
}

public Topology(final Topology topology) {
	this.baseUrl = topology.baseUrl;
	this.applications = topology.applications;
	this.servers = topology.servers;
}

/**
 * Add an application to the topology.
 *
 * @param application The application to add
 */
protected void addApplication(final Application application) {
	this.applications.add(application);
	updateServer(application);
}

/**
 * Returns the default base URL for the current application.
 * <p>
 * Default is to have no default value, ie. returns <code>null</code>.
 * </p>
 */
protected String defaultBaseUrl() {
	return null;
}

/**
 * Return the application matching the given url address.
 *
 * @param url The address
 * @return The {@link Application} corresponding to the given address.
 * @throws ScenarioFailedError If not application is found for the given
 * address.
 */
public Application getApplication(final String url) {
	for (Application application: this.applications) {
		if (application.isApplicationFor(url)) {
			if (DEBUG) debugPrintln("		  -> found application '"+application+"' for URL: "+url);
			return application;
		}
	}
	return null;
}

/**
 * Return the list of application titles
 *
 * @return All the supported applications during the test scenario as a {@link List}
 * of {@link Application}.
 */
public List<Application> getApplications() {
	return this.applications;
}

/**
 * Get the base URL for a given application.
 * <p>
 * The default base URL for an application (e.g. JTS) is:<br>
 * <code>baseUrl/<i>app</i>"</code><br>
 * where <code><i>app</i></app></code> may be: "jts", "ccm", "qm", "rm", "lpa", "dcc".
 * </p><p>
 * However, tester can override this default by setting corresponding parameter
 * like <code>-DbaseUrl<i>APP</i></code> where <code><i>APP</i></app></code>
 * may be: "JTS", "CCM", "QM", "RM", "LPA", "DCC".
 * </p>
 *
 * @param app The application
 * @return The base URL for the given application as a <code>String</code>.
 */
protected String getBaseUrlApplication(final String app) {
	return getParameterValue("baseUrl"+app.toUpperCase(), this.baseUrl+"/"+app);
}

public User getLoggedUser(final String url) {
	return getApplication(url).user;
}

/**
 * Return the modified page URL if necessary.
 *
 * @return The page URL as a {@link String}.
 * @see Application#getPageUrl(String)
 */
public String getPageUrl(final String currentUrl) {
	Application application = getApplication(currentUrl);
	if (application != null) {
		return application.getPageUrl(currentUrl);
	}
	return currentUrl;
}

/**
 * Initialize all topology applications.
 */
abstract protected void initApplications();

///**
// * Returns whether the topology is distributed or not.
// *
// * @return <code>true</code> if at least one application is at a different
// * address than the the others, <code>false</code> otherwise.
// */
//public boolean isDistributed() {
//	return this.distributed;
//}

/*
 * Initialize topology servers.
 */
private void initServers() {
	Collection<Application> list = this.applications;
	for (Application application: list) {
		updateServer(application);
	}
}

/**
 * Returns whether this topology is distributed across multiple servers
 *
 * @return <code>true</code> if the topology is distributed, <code>false</code> otherwise
 */
public abstract boolean isDistributed();

/**
 * Login the given user to the application matching the given location.
 * <p>
 * The login is propagated to other applications which are on the same server.
 * </p>
 *
 * @param location The location concerned by the login
 * @param user The user concerned by the login
 * @return <code>true</code> if the user was changed on the application from
 * which the location belongs to, <code>false</code> otherwise.
 */
public boolean login(final String location, final User user) {
	if (DEBUG) debugPrintln("		+ Login user "+user.getId()+" for all applications");

	// Get the application matching the given location
	Application application = getApplication(location);
	if (application == null) {
		throw new ScenarioFailedError("Cannot find any application at the given location: "+location);
	}

	// Login to the application belonging the given location
	boolean login = application.login(user);

	// Propagate user login to other server applications
	if (login) {
		List<Application> serverApplications = this.servers.get(application.server);
		for (Application appli: serverApplications) {
			appli.login(user);
		}
	}

	// Return whether there was a user change on the application
	return login;
}

/**
 * Logout the given user from the application matching the given location and
 * do the same for all other applications on the same server.
 *
 * @param location The location concerned by the logout
 */
public boolean logout(final String location) {
	if (DEBUG) debugPrintln("		+ Logout applications from "+location);

	// Get all application on the same server than the application
	Application application = getApplication(location);
	List<Application> serverApplications = this.servers.get(application.server);

	// Get applications needing login on the server
	boolean appliUserChanged = false;
	for (Application appli: serverApplications) {
		boolean changed = appli.logout();
		if (appli.equals(application)) {
			appliUserChanged = changed;
		}
	}

	// Return whether the user has change for the application
	return appliUserChanged;
}

/**
 * Logs out all applications, regardless of the user currently logged in.
 */
public void logoutApplications() {
	for (Application app : this.applications) {
		app.logout();
	}
}

/**
 * Returns whether the given user needs to login before accessing the given
 * location.
 *
 * @param location The location to go to
 * @param user The user to use when going to the location
 * @return <code>true</code> if the user has never accessed to the application
 * matching the location and neither to no any other application of the same server,
 * <code>false</code> otherwise.
 */
public boolean needLogin(final String location, final User user) {
	if (user == null) {
		throw new IllegalArgumentException("Need a user to decide whether login is needed or not.");
	}
	if (DEBUG) debugPrintln("		+ See whether it needs to log in before going to "+location);

	// If application does not need login, then return
	Application application = getApplication(location);
	if (!application.needLogin(user)) {
		return false;
	}

	// Get all application on the same server than the application
	List<Application> serverApplications = this.servers.get(application.server);

	// In case the application is not logged yet, check if there's another application
	// on the same server logged on the given user
	if ((application.getUser() == null) && (serverApplications != null)) {
		for (Application appli: serverApplications) {
			if (user.equals(appli.getUser())) {
//				application.login(user);
				return false;
			}
		}
	}

	// None of the application is logged, it needs login
	if (DEBUG) debugPrintln("		  -> no application is already logged in on same server");
	return true;
}

/**
 * Return whether two applications are hosted on the same server.
 *
 * @param firstApp The first application to check
 * @param secondApp The second application to check
 *
 * @return <code>true</code> if the two applications are on the
 * same server, <code>false</code> otherwise
 */
public boolean onSameHost(final Application firstApp, final Application secondApp) {
	return firstApp.getHostUrl().equals(secondApp.getHostUrl());
}

/*
 * Update topology servers.
 */
private void updateServer(final Application application) {
	List<Application> serverApplications = this.servers.get(application.server);
	if (serverApplications == null) {
		this.servers.put(application.server, serverApplications = new ArrayList<Application>());
	}
	serverApplications.add(application);
}
}