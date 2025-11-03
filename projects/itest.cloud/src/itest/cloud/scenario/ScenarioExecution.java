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
package itest.cloud.scenario;

import static itest.cloud.browser.Browser.JAVASCRIPT_ERROR_ALERT_PATTERN;
import static itest.cloud.scenario.ScenarioUtil.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.junit.AssumptionViolatedException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.UnreachableBrowserException;

import itest.cloud.annotation.*;
import itest.cloud.browser.Browser;
import itest.cloud.config.Config;
import itest.cloud.page.Page;
import itest.cloud.performance.PerfManager;
import itest.cloud.scenario.error.*;
import itest.cloud.topology.Application;
import itest.cloud.topology.Topology;

/**
 * Manage scenario execution.
 * <p>
 * This class is responsible to initialize and store the configuration and the data.
 * </p><p>
 * It also controls the scenario behavior when failure occurs using following
 * arguments:
 * <ul>
 * <li>{@link #STOP_ON_FAILURE_ID}: flag to tell whether the scenario can
 * continue after a test failure or should stop immediately. By default, the
 * execution will continue after a failure, but setting this argument to <code>true</code>
 * will make it stop at the first failure.</li>
 * <li>{@link #FAILURES_THRESHOLD_ID}: Number of tolerated failure coming
 * from selenium WebDriver API (ie. when a {@link WebDriverException} is raised)
 * when running the <b>entire</b> scenario.<br>
 * Above the threshold, the test will fail, otherwise it will run it again in case
 * it was a transient problem. In the latter case, a snapshot will be taken and
 * put in the warning directory and the failure stack trace will be written in the
 * console output.</li>
 * <li>{@link #ALERTS_THRESHOLD_ID}: Number of tolerated alerts when running
 * the <b>entire</b> scenario.<br>
 * Above the threshold, the test will fail, otherwise it will accept the alert and
 * continue the execution.</li>
 * <li>{@link #TIMEOUTS_THRESHOLD_ID}: Number of tolerated timeouts failure
 * when running the <b>entire</b> scenario.<br>
 * Above the threshold, the test will fail, otherwise it will run it again in case
 * it was a transient problem. In the latter case, a snapshot will be taken and
 * put in the warning directory and the failure stack trace will be written in the
 * console output.</li>
 * </ul>
 * </p><p>
 * Another important thing done by this class is to store the current {@link Page page}
 * to be able to pass it from test to test inside a scenario step and also from step
 * to step inside the scenario. That allow easy transition between tests when
 * a test ends on the same page where the following one starts.
 * </p>
 */
public abstract class ScenarioExecution implements ScenarioDataConstants {

	private class Blemishes {
		int failures = 0;
		int alerts = 0;
		int timeouts = 0;
		int multiples = 0;
		int browserErrors = 0;
		int invocations = 0;

		public Blemishes() {}
	}

	/* Constants */
	// Control the execution after a failure
	private final static String STOP_ON_FAILURE_ID = "stopOnFailure";
	private final static String FAILURES_THRESHOLD_ID = "failuresThreshold";
	private final static String ALERTS_THRESHOLD_ID = "alertsThreshold";
	private final static String TIMEOUTS_THRESHOLD_ID = "timeoutsThreshold";
	private final static String INVOCATIONS_THRESHOLD_ID = "invocationsThreshold";
	private final static String MULTIPLES_THRESHOLD_ID = "multiplesThreshold";
	private final static String BROWSER_ERRORS_THRESHOLD_ID = "browserErrorsThreshold";
	private final static int DEFAULT_ALERTS_THRESHOLD = 2;
	private final static int DEFAULT_FAILURES_THRESHOLD = 2;
	private final static int DEFAULT_TIMEOUTS_THRESHOLD = 2;
	private final static int DEFAULT_INVOCATIONS_THRESHOLD = 2;
	private final static int DEFAULT_MULTIPLES_THRESHOLD = 2;
	private final static int DEFAULT_BROWSER_ERRORS_THRESHOLD = 2;
	private final static String STOP_ON_EXCEPTION_ID = "stopOnException";

	private static final String[] BROWSER_CRASHED_MESSAGES = new String[] {
		"Failed to connect to binary FirefoxBinary",
		"Failed to decode response from marionette",
		"Browsing context has been discarded",
		"Tried to run command without establishing a connection",
		"session deleted because of page crash",
		"no such session",
		"no such window", "target window already closed",
		"chrome not reachable",
		"browser did not respond"
	};

	// Execution control
	private final boolean stopOnFailure;
	private final int failuresThreshold;
	private final int alertsThreshold;
	private final int timeoutsThreshold;
	private final int invocationsThreshold;
	private final int multiplesThreshold;
	private final int browserErrorsThreshold;
	private boolean shouldStop = false;
	private boolean singleStep = false;
	private List<FrameworkMethod> mandatoryTests = new ArrayList<FrameworkMethod>();
	private boolean slowServer = false;
	private final boolean stopOnException;
	private final boolean closeBrowserOnExit;
	private final boolean verifyDependencies;

	private final boolean verifyDependenciesOnly;
	// Test information
	private Blemishes blemishes;

	private Hashtable<String, Boolean> testResults = new Hashtable<String, Boolean>();

	// Configuration
	protected Config config;

	// Data
	protected ScenarioData data;
	// Step info
	protected String stepName;
	protected String testName;
	protected String packageName;

public ScenarioExecution() {

	// Init Debug
	ScenarioUtil.debugOpen();

	// Init execution controls
	this.stopOnFailure = getParameterBooleanValue(STOP_ON_FAILURE_ID, false);
	this.stopOnException = getParameterBooleanValue(STOP_ON_EXCEPTION_ID, this.stopOnFailure);
	this.failuresThreshold = getParameterIntValue(FAILURES_THRESHOLD_ID, DEFAULT_FAILURES_THRESHOLD);
	this.alertsThreshold = getParameterIntValue(ALERTS_THRESHOLD_ID, DEFAULT_ALERTS_THRESHOLD);
	this.timeoutsThreshold = getParameterIntValue(TIMEOUTS_THRESHOLD_ID, DEFAULT_TIMEOUTS_THRESHOLD);
	this.invocationsThreshold = getParameterIntValue(INVOCATIONS_THRESHOLD_ID, DEFAULT_INVOCATIONS_THRESHOLD);
	this.browserErrorsThreshold = getParameterIntValue(BROWSER_ERRORS_THRESHOLD_ID, DEFAULT_BROWSER_ERRORS_THRESHOLD);
	this.multiplesThreshold = getParameterIntValue(MULTIPLES_THRESHOLD_ID, DEFAULT_MULTIPLES_THRESHOLD);
	this.closeBrowserOnExit = getParameterBooleanValue("closeBrowserOnExit", true);
	this.verifyDependencies = getParameterBooleanValue("verifyDependencies", true);
	this.verifyDependenciesOnly = getParameterBooleanValue("verifyDependenciesOnly", false);

	// Init Config
	initConfig();

	// Init data
	initData();
}

/**
 * Add a list of mandatory tests.
 *
 * @param tests The mandatory tests to add
 */
public void addMandatoryTests(final List<FrameworkMethod> tests) {
	this.mandatoryTests.addAll(tests);
}

/*
 * Helper method to check if the server speed is considered normal (according to
 * pipeline averages) or slower than normal.  Individual tests can trigger this
 * check with the {@link CheckServerSpeed} annotation; passing in the max time
 * (in seconds) that is considered normal for that test in the pipeline.
 * <p></p>
 * This method sets the {@link slowServer} attribute. When a test takes longer than
 * normal, the attribute is set to <code>true</code>.  When a test takes less than expected,
 * the attribute is set (or reset) to <code>false</code>.
 * </p>
 * @param start Start time of the test - used to determine how long the test has taken
 * @param timeLimit Maximum number of seconds that this test should have taken; tests taking longer
 * indicate a slower server.
 */
private void checkServerSpeed(final long start, final int timeLimit) {
	if (DEBUG) println("		+ Checking server speed: this test should take less than '" + timeLimit + "' seconds.");

	if (getElapsedTime(start) > timeLimit*1000) {
		this.slowServer = true;
		if (DEBUG) println("		> Server is considered *slow* because this test took longer '" + timeLimit + "' seconds.");
		return;
	}

	// If we get here, the server is behaving normally.
	this.slowServer = false;
	if (DEBUG) println("		> Server is considered *normal* because this test took less than '" + timeLimit + "' seconds.");
}

/**
 * Do some cleanup usually before re-run the test when it failed.
 * <p>
 * Default clean up is to clear pages cache.
 * </p>
 * @param t The failure which requires a clean-up
 */
protected void cleanUp(final Throwable t) {
	println("		-> Cleanup test by clearing the pages cache.");
	Page.clearHistory();
}

private void createExecutionDetailsFile() {
	Properties props = new Properties();
	try {
		props.setProperty("browser.name", getBrowser().getName());
		props.setProperty("browser.version", getBrowser().getVersion());
		props.setProperty("os.name", getOsName());
		props.setProperty("os.version", getOsVersion());
		props.setProperty("os.architecture", getOsArchitecture());
		props.setProperty("application.urls", getApplicationUrls());

		FileOutputStream ostream = new FileOutputStream(EXECUTION_DETAILS_FILE);
		props.store(ostream, "Execution details added");
		ostream.close();
	}
	catch (IOException e) {
		e.printStackTrace();
	}
}

/**
 * Ends the scenario execution.
 */
public void finish() {
	ScenarioUtil.debugClose();
	createExecutionDetailsFile();
	try {
		if (this.closeBrowserOnExit) {
			getBrowser().close();
		} else {
			println("INFO: The browser has been kept opened in order to continue to use its session for further investigation or tests...");
		}
	}
	catch (UnreachableBrowserException ube) {
		// Skip as browser was already dead.
	}
	catch (Exception ex) {
		println("Exception '"+ex.getMessage()+"' has been skipped while closing browser...");
	}
}

private String getApplicationUrls() {
	StringBuffer urls = new StringBuffer();
	List<Application> applications = getTopology().getApplications();
	for (int i = 0; i < applications.size(); i++) {
		urls.append(applications.get(i).getLocation());
		if(i < applications.size() - 1) urls.append(",");
	}
	return urls.toString();
}

/**
 * Return the browser used to run the scenario
 *
 * @return The {@link Browser}.
 */
public Browser getBrowser() {
	return this.config.getBrowser();
}

/**
 * Return the scenario configuration to use during the run.
 *
 * @return The scenario {@link Config}.
 */
public Config getConfig() {
	return this.config;
}

/**
 * Return the scenario data to use during the run.
 *
 * @return The scenario data as {@link ScenarioData}.
 */
public ScenarioData getData() {
	return this.data;
}

private String getShouldStopReason(final boolean mandatoryTest) {
	String reason = EMPTY_STRING;
	if (this.stopOnFailure) {
		reason = "stopOnFailure=true";
	}
	if (mandatoryTest) {
		if (reason.length() > 0) reason += " and";
		reason += "it's a mandatory test";
	}
	return reason;
}

/**
 * Return the scenario topology used during the run.
 *
 * @return The scenario {@link Topology}.
 */
public Topology getTopology() {
	return this.config.getTopology();
}

private void handleAlert(final FrameworkMethod frameworkMethod, final WebDriverException wde) throws Throwable {
	this.blemishes.alerts++;
	printException(wde);
	getBrowser().purgeAlert("Running test "+this.testName, 0);
	if (this.blemishes.alerts > this.alertsThreshold) {
		takeSnapshotFailure();
		this.shouldStop = this.stopOnFailure || this.mandatoryTests.contains(frameworkMethod);
		logTestFailure(wde);
	}
	println("WORKAROUND: Try to run the test again in case the alert was a transient issue...");
	takeSnapshotWarning(); // take a snapshot just to notify the warning
}

private void handleBrowserError(final Statement statement, final FrameworkMethod frameworkMethod, final Object target, final boolean isNotRerunnable, final long start, final Throwable e) throws Throwable {
	this.blemishes.browserErrors++;
	Page currentPage = Page.getCurrentPage();
	manageFailure(start, e, isNotRerunnable && (currentPage != null));
	if (this.blemishes.browserErrors >= this.browserErrorsThreshold) {
		println("Too many browser errors occurred during scenario execution, give up.");
		takeSnapshotInfo(e.toString());
		this.shouldStop = this.stopOnFailure || this.mandatoryTests.contains(frameworkMethod);
		// Restart the browser in case that can help for next test to proceed properly.
		// However, restarting the browser may not be possible if the tests are executed on a remote host
		// while using a Selenium Grid for example. In such a situation, simply refresh the current web
		// page than restarting the browser.
		if(currentPage != null) {
			if(getBrowser().isRemoteTestExecution()) {
				println("WORKAROUND: Refresh the browser...");
				try { currentPage.refresh(); } catch (Throwable t) {}
			}
			else {
				println("WORKAROUND: Restart the browser...");
				currentPage.startNewBrowserSession();
			}
		}
		logTestFailure(e);
	}
	println("WORKAROUND: Try to run the test again in case this was a transient issue...");
	takeSnapshotWarning();

	// Restart the browser in case that can help. However, restarting the browser may not be possible if
	// the tests are executed on a remote host while using a Selenium Grid for example. In such a situation,
	// simply refresh the current web page than restarting the browser.
	if(currentPage != null) {
		if(getBrowser().isRemoteTestExecution()) {
			println("WORKAROUND: Refresh the browser...");
			try { currentPage.refresh(); } catch (Throwable t) {}
		}
		else {
			println("WORKAROUND: Restart the browser...");
			currentPage.startNewBrowserSession();
		}
	}

	// Re-run the test
	println("		  -> Re-run the test...");
	rerunTest(statement, frameworkMethod, target);
}

/**
 * Initialize the configuration.
 * <p>
 * That needs to be overridden by the specific scenario to instantiate its own
 * object.
 * </p>
 */
abstract protected void initConfig();

/**
 * Initialize the data.
 * <p>
 * That needs to be overridden by the specific scenario to instantiate its own
 * object.
 * </p>
 */
abstract protected void initData();

private boolean isBrowserCrashedMessage(final String message) {
	for (String browserCrashedMessage : BROWSER_CRASHED_MESSAGES) {
		if(message.contains(browserCrashedMessage)) return true;
	}

	return false;
}

/**
 * @return the singleStep
 */
public boolean isSingleStep() {
	return this.singleStep;
}

///*
// * Restart the browser assuming the current session has gone...
// */
//private void restartBrowser(final WebPage currentPage) {
//
//	// Open new browser session
//	println("		  -> Restarting browser to try to see if it accidentally died...");
//	getConfig().openNewBrowser();
//	sleep(1);
//
//	// Clear login data
//	User user = currentPage.getUser();
//	getTopology().logoutApplications();
//
//	// Reload current page
//	println("		  -> Reopening current page: " + currentPage.getLocation());
//	WebPage.reopenPage(currentPage, user);
//	sleep(1);
//}

/**
 * Return whether or not a server is considered slow.
 *
 * @return <code>true</code> if the server is considered slow; <code>false</code> otherwise.
 */
public boolean isSlowServer() {
	return this.slowServer;
}

/**
 * Log a given test failure.
 *
 * @param throwable The exception associated with the test failure.
 *
 * @throws Throwable The exception associated with the test failure.
 */
protected void logTestFailure(final Throwable throwable) throws Throwable {
	throw throwable;
}

/**
 * Manage the given failure.
 * <p>
 * Default behavior is to:
 * <ol>
 * <li>Print failure stack trace</li>
 * <li>Purge alerts</li>
 * <li>Print failure message</li>
 * <li>Store current page and reset it.</p>
 * </lo>
 * </p><p>
 * Specific scenarios might want to override this method to add specific behavior.
 * </p>
 * @param start The test starting time
 * @param t The failure to be managed
 * @param isNotRerunnable Tells whether the current test is rerunnable or not.
 */
protected void manageFailure(final long start, final Throwable t, final boolean isNotRerunnable) {

	// Print failure stack trace
	printException(t);
	String message = t.getMessage();

	// Purge alert if any
	if (!(t instanceof WebDriverException)) {
		try {getBrowser().purgeAlerts("After having got exception '" + message + "'");} catch (WebDriverException e) {}
	}

	// Print info
	println("	  -> KO (in " + elapsedTimeString(start) + ")");
	println("		due to: " + message);

	// If a CLM Server error occurs display the error message
	if (t instanceof ServerMessageError) {
		ServerMessageError sme = (ServerMessageError) t;
		println("SERVER ERROR MESSAGE:");
		println(sme.getSummary());
		if (sme.getDetails() != null) {
			println(sme.getDetails());
		}
	}

	// Stop execution when test is not re-runnable
	if (isNotRerunnable) {
		// Skip test re-run
		try {takeSnapshotFailure();} catch (WebDriverException e) {}
		throw new ScenarioFailedError(t);
	}

	// Clear up before eventually re-run the test
//	cleanUp(t);
}

private void recordTestResult(final String test, final Boolean result) {
	this.testResults.put(test, result);
}

/**
 * Run the current test and take specific actions when some typical exception
 * or error occurs (e.g. take a snapshot when a error occurs, retry when allowed).
 * <p>
 * <b>Design Needs finalization</b>
 * </p>
 */
public void rerunTest(final Statement statement, final FrameworkMethod frameworkMethod, final Object target) throws Throwable {
	runTest(statement, frameworkMethod, target, false /*isNewStep*/, false /*isFirstRun*/);
}

/**
 * Run the current test and take specific actions when some typical exception
 * or error occurs (e.g. take a snapshot when a error occurs, retry when allowed).
 */
public void runTest(final Statement statement, final FrameworkMethod frameworkMethod, final Object target, final boolean isNewStep) throws Throwable {
	runTest(statement, frameworkMethod, target, isNewStep, true /*isFirstRun*/);
}

/**
 * Run the current test and take specific actions when some typical exception
 * or error occurs (e.g. take a snapshot when a error occurs, retry when allowed).
 * <p>
 * <b>Design Needs finalization</b>
 * </p>
 */
private void runTest(final Statement statement, final FrameworkMethod frameworkMethod, final Object target, final boolean isNewStep, final boolean isFirstRun) throws Throwable {
	// Store names
	setStepName(target, isNewStep);
	setTestName(frameworkMethod.getName());
	setPackageName(target);

	// Store performances information if necessary
	PerfManager perfManager = getBrowser().getPerfManager();
	if (perfManager != null) {
    	perfManager.setStepName(this.stepName);
    	perfManager.setTestName(this.testName);
	}

	// Create the Blemishes instance for the test if the test is run for the first time.
	if(isFirstRun) {
		this.blemishes = new Blemishes();
	}

	// Record the re-runnable status.
	boolean isNotRerunnable = frameworkMethod.getAnnotation(NotRerunnable.class) != null;

	// Record the initial result of the test to FALSE in advance.
	final String qualifiedTestName = target.getClass().getName() + "." + this.testName;
	recordTestResult(qualifiedTestName, FALSE);

	// Run test and take snapshots if a failure or error occurs
	long start = System.currentTimeMillis();

	try {
		// Check if the dependent tests have passed.
		verifyDependencies(frameworkMethod, target);

		// Run the test unless the sole purpose of the test execution is to merely validate the dependencies between tests.
		if(!this.verifyDependenciesOnly) statement.evaluate();

		// The test has passed. Update the result accordingly.
		recordTestResult(qualifiedTestName, TRUE);

		// Individual tests may be annotated to check server speed.
		CheckServerSpeed annotation = frameworkMethod.getAnnotation(CheckServerSpeed.class);
		if (annotation != null) {
			checkServerSpeed(start, annotation.value());
		}

		println("	  -> OK (in "+elapsedTimeString(start)+")");
	}
	catch(AssumptionViolatedException ave) {
		manageFailure(start, ave, false /*isNotRerunnable*/);
		throw ave;
	}
	catch (UnhandledAlertException uae) {
		handleAlert(frameworkMethod, uae);
		rerunTest(statement, frameworkMethod,target);
	}
	catch (UnreachableBrowserException ube) {
		this.blemishes.browserErrors++;
		Page currentPage = Page.getCurrentPage();
		manageFailure(start, ube, isNotRerunnable && (currentPage != null));
		if (this.blemishes.browserErrors >= this.browserErrorsThreshold) {
			println("		  -> The browser seems to have a problem which cannot be worked around by just a restart, give up!");
			this.shouldStop = true;
			logTestFailure(ube);
		}
		// Restart the browser on the current page
		println("WORKAROUND: Try to run the test again in case this was a transient issue...");
		// Restart the browser in case that can help
		println("WORKAROUND: Restart the browser...");
		if(currentPage != null) currentPage.startNewBrowserSession();
		// Re-run the test
		println("		  -> Re-run the test...");
		rerunTest(statement, frameworkMethod, target);
	}
	catch (StaleElementReferenceException sere) {
		// Handle the StaleElementReferenceException as a BrowserError.
		println(getClassSimpleName(sere.getClass()) + " occurred. As a result, it'll be handled as a " + getClassSimpleName(BrowserError.class) + " instead.");
		handleBrowserError(statement, frameworkMethod, target, isNotRerunnable, start, sere);
	}
	catch (WebDriverException wde) {
		String message = wde.getMessage();
		if (message.matches(JAVASCRIPT_ERROR_ALERT_PATTERN)) {
			handleAlert(frameworkMethod, wde);
		}
		else if (isBrowserCrashedMessage(message)) {
			this.blemishes.browserErrors++;
			Page currentPage = Page.getCurrentPage();
			manageFailure(start, wde, isNotRerunnable && (currentPage != null));
			if (this.blemishes.browserErrors >= this.browserErrorsThreshold) {
				println("		  -> The browser seems to have a problem which cannot be worked around by just a restart, give up!");
				this.shouldStop = true;
				logTestFailure(wde);
			}
			// Restart the browser on the current page after a pause.
			println("WORKAROUND: Try to run the test again in case this was a transient issue...");
			// Restart the browser in case that can help
			println("WORKAROUND: Restart the browser...");
			if(currentPage != null) currentPage.startNewBrowserSession();
		}
		else {
			this.blemishes.failures++;
			Page currentPage = Page.getCurrentPage();
			manageFailure(start, wde, isNotRerunnable);
			if (this.blemishes.failures >= this.failuresThreshold) {
				takeSnapshotFailure();
				this.shouldStop = this.stopOnFailure || this.mandatoryTests.contains(frameworkMethod);
				logTestFailure(wde);
			}
			println("WORKAROUND: Try to run the test again in case this was a transient issue...");
			takeSnapshotWarning();
			// Refresh browser in case that can help...
			println("WORKAROUND: Refresh the browser...");
			try { currentPage.refresh(); } catch (Throwable t) {}
		}
		// Re-run the test
		println("		  -> Re-run the test...");
		rerunTest(statement, frameworkMethod, target);
	}
	catch (IncorrectTitleError ite) {
		// Handle the IncorrectTitleError as a BrowserError.
		println(getClassSimpleName(ite.getClass()) + " occurred. As a result, it'll be handled as a " + getClassSimpleName(BrowserError.class) + " instead.");
		handleBrowserError(statement, frameworkMethod, target, isNotRerunnable, start, ite);
	}
	catch (WaitElementTimeoutError wete) {
		Page currentPage = Page.getCurrentPage();
		// If a WaitElementTimeoutError occurs during the login operation in a new browser session, then the current page
		// will be null. This situation should be considered.
		if((currentPage != null) && currentPage.isInApplicationContext()) {
			this.blemishes.timeouts++;
			// If the current web page is in the context of the application, handle the issue as a WaitElementTimeoutError.
			manageFailure(start, wete, isNotRerunnable);
			if (this.blemishes.timeouts >= this.timeoutsThreshold) {
				println("Too many timeout errors occurred during scenario execution, give up.");
				takeSnapshotFailure();
				this.shouldStop = this.stopOnFailure || this.mandatoryTests.contains(frameworkMethod);
				logTestFailure(wete);
			}
			println("WORKAROUND: Try to run the test again in case this was a transient issue...");
			takeSnapshotWarning();
			// Refresh browser in case that can help...
			println("WORKAROUND: Refresh the browser...");
			try { currentPage.refresh(); } catch (Throwable t) {}

			if(currentPage.isInApplicationContext()) {
				// Re-run the test
				println("		  -> Re-run the test...");
				rerunTest(statement, frameworkMethod, target);
			}
			else {
				// If the current web page is not in the context of the application, it implies that the current page can be
				// some type of error page. Therefore, handle the WaitElementTimeoutError as a BrowserError.
				println("Web page was out of scope/context of application after refreshing browser. As a result, it'll be handled as a " + getClassSimpleName(BrowserError.class) + " instead.");
				handleBrowserError(statement, frameworkMethod, target, isNotRerunnable, start, wete);
			}
		}
		else {
			// If the current web page is not in the context of the application, it implies that the current page can be
			// some type of error page. Therefore, handle the WaitElementTimeoutError as a BrowserError.
			println(getClassSimpleName(wete.getClass()) + " occurred since web page was out of scope/context of application. As a result, it'll be handled as a " + getClassSimpleName(BrowserError.class) + " instead.");
			handleBrowserError(statement, frameworkMethod, target, isNotRerunnable, start, wete);
		}
	}
	catch (InvocationTargetException ite) {
		this.blemishes.invocations++;
		Page currentPage = Page.getCurrentPage();
		manageFailure(start, ite, isNotRerunnable);
		if (this.blemishes.invocations >= this.invocationsThreshold) {
			println("Too many invocation target exceptions occurred during scenario execution, give up.");
			takeSnapshotFailure();
			this.shouldStop = this.stopOnFailure || this.mandatoryTests.contains(frameworkMethod);
			// Restart browser in case that can help for next test to proceed properly...
			if(currentPage != null) currentPage.startNewBrowserSession();
			logTestFailure(ite);
		}
		println("WORKAROUND: Try to run the test again in case this was a transient issue...");
		takeSnapshotWarning();
		// Refresh browser in case that can help...
		println("WORKAROUND: Restart the borwser...");
		if(currentPage != null) currentPage.startNewBrowserSession();
		// Re-run the test
		println("		  -> Re-run the test...");
		rerunTest(statement, frameworkMethod, target);
	}
	catch (MultipleVisibleElementsError mvee) {
		this.blemishes.multiples++;
		Page currentPage = Page.getCurrentPage();
		manageFailure(start, mvee, isNotRerunnable);
		if (this.blemishes.multiples >= this.multiplesThreshold) {
			println("Too many multiple elements errors occurred during scenario execution, give up.");
			takeSnapshotFailure();
			this.shouldStop = this.stopOnFailure || this.mandatoryTests.contains(frameworkMethod);
			logTestFailure(mvee);
		}
		println("WORKAROUND: Try to run the test again in case this was a transient issue...");
		takeSnapshotWarning();
		// Refresh browser in case that can help...
		println("WORKAROUND: Refresh the browser...");
		try { currentPage.refresh(); } catch (Throwable t) {}
		// Re-run the test
		println("		  -> Re-run the test...");
		rerunTest(statement, frameworkMethod, target);
	}
	catch (ExistingDataError ede) {
		manageFailure(start, ede, false/*test won't be rerun*/);
		takeSnapshotFailure();
		this.shouldStop = true;
		logTestFailure(ede);
	}
	catch (ServerMessageError sme) {
		manageFailure(start, sme, false/*test won't be rerun*/);
		// Print information on the server error
		println("Server error summary: "+sme.getSummary());
		String details = sme.getDetails();
		if (details != null) {
			println("Server error details: "+details);
		}
		// Take snapshot with error details expanded
		sme.showDetails();
		takeSnapshotFailure();
		this.shouldStop = this.stopOnFailure || this.mandatoryTests.contains(frameworkMethod);
		logTestFailure(sme);
	}
	catch (BrowserConnectionError bce) {
		manageFailure(start, bce, false/*test won't be rerun*/);
		takeSnapshotFailure();
		this.shouldStop = true;
		logTestFailure(bce);
	}
	catch (ScenarioImplementationError sie) {
		manageFailure(start, sie, false/*test won't be rerun*/);
		takeSnapshotFailure();
		this.shouldStop = true;
		logTestFailure(sie);
	}
	catch (ScenarioMissingImplementationError smie) {
		manageFailure(start, smie, false/*test won't be rerun*/);
		takeSnapshotFailure();
		this.shouldStop = true;
		logTestFailure(smie);
	}
	catch (BrowserUrlUnchangedError buue) {
		// Handle the BrowserUrlUnchangedError as a BrowserError.
		println(getClassSimpleName(buue.getClass()) + " occurred. As a result, it'll be handled as a " + getClassSimpleName(BrowserError.class) + " instead.");
		handleBrowserError(statement, frameworkMethod, target, isNotRerunnable, start, buue);
	}
	catch (ClassCastException cce) {
		// Handle the ClassCastException as a BrowserError.
		println(getClassSimpleName(cce.getClass()) + " occurred. As a result, it'll be handled as a " + getClassSimpleName(BrowserError.class) + " instead.");
		handleBrowserError(statement, frameworkMethod, target, isNotRerunnable, start, cce);
	}
	catch (BrowserError be) {
		handleBrowserError(statement, frameworkMethod, target, isNotRerunnable, start, be);
	}
	catch (Error err) {
		// Basic failure management for any kind of other error (including ScenarioFailedError)
		manageFailure(start, err, false/*test won't be rerun*/);
		takeSnapshotFailure();
		boolean mandatoryTest = this.mandatoryTests.contains(frameworkMethod);
		this.shouldStop = this.stopOnFailure || mandatoryTest;
		if (this.shouldStop) {
			String reason = getShouldStopReason(mandatoryTest);
			println("ERROR: Unexpected error encountered while running current test, scenario execution will be aborted as "+reason+"!");
		}
		logTestFailure(err);
	}
	catch (Exception ex) {
		// Basic exception management
		manageFailure(start, ex, false/*test won't be rerun*/);
		takeSnapshotFailure();
		boolean mandatoryTest = this.mandatoryTests.contains(frameworkMethod);
		this.shouldStop = this.stopOnException || mandatoryTest;
		if (this.shouldStop) {
			String reason = getShouldStopReason(mandatoryTest);
			println("ERROR: Unexpected exception encountered while running current test, scenario execution will be aborted because "+reason+"!");
		}
		logTestFailure(ex);
	}
}

void setPackageName(final Object target) {
	this.packageName = getPackageName(target.getClass());
}

///**
// * Set the current scenario page.
// */
//public void setPage(final WebPage page) {
//    this.page = page;
//}

///**
// * @param shouldStop the shouldStop to set
// */
//public void setShouldStop(final boolean shouldStop) {
//	this.shouldStop = shouldStop;
//}

/**
 * @param singleStep the singleStep to set
 */
public void setSingleStep(final boolean singleStep) {
	this.singleStep = singleStep;
}

void setStepName(final Object target, final boolean isNewStep) {
	this.stepName = getClassSimpleName(target.getClass());
	// If it is a new step, print an appropriate message to the console.
	if(isNewStep) {
		printStepStart(this.stepName);
	}
}

void setTestName(final String methodName) {
	println("	- " + TIME_FORMAT.format(new Date(System.currentTimeMillis())) + ": start test case '" + methodName +"'...");
    this.testName = methodName;
}

/**
 * @return the shouldStop
 */
public boolean shouldStop() {
	return this.shouldStop;
}

public void showVersionInfo() {
	println("Selenium and Browser information:");
	println("	- " + ScenarioUtil.getSeleniumVersion());
	println("	- " + getBrowser().getName() + " version: " + getBrowser().getVersion());
}

/**
 * Takes a failure snapshot.
 */
public void takeSnapshotFailure() {
	// Print warning message in console
	println("		+ one snapshot taken when the failure occurred:");
	// Take snapshot
	getBrowser().takeSnapshotFailure(toString());
}

/**
 * Takes a graph snapshot.
 *
 * @param title The title to display in the console when taking the snapshot
 */
public void takeSnapshotInfo(final String title) {
	// Print warning message in console
	println("		+ ********** WARNING **********");
	print("		  -> Please check that the ");
	print(title);
	println(" graph was correctly generated in following screenshot file");
	// Take snapshot
	getBrowser().takeSnapshotInfo(toString());
}

/**
 * Takes a warning snapshot.
 */
public void takeSnapshotWarning() {
	takeSnapshotWarning(null);
}
/**
 * Takes a warning snapshot, adding text to console for reason.
 *
 * @param text Reason the snapshot was taken.
 */
public void takeSnapshotWarning(final String text) {
	// Print warning message in console
	println("		+ one snapshot taken:");
	if (text != null) {
		println("		-> Reason: " + text);
	}
	// Take snapshot
	getBrowser().takeSnapshotWarning(toString());
}

/**
 * {@inheritDoc}
 * <p>
 * Return the current step.test names
 * </p>
 */
@Override
public String toString() {
	return this.stepName + "." + this.testName;
}

private void verifyDependencies(final FrameworkMethod frameworkMethod, final Object target) {
	// If asked not to verify dependencies (while debugging for example), simply return.
	if(!this.verifyDependencies) return;
	// Verify dependencies otherwise.
	Dependency dependency = frameworkMethod.getAnnotation(Dependency.class);
	if (dependency != null) {
		for (String dependentTest : dependency.value()) {
			String formalizedDependentTest =
				dependentTest
					.replace(CLASS_INDICATOR_OF_DEPENDENCY, target.getClass().getName())
					.replace(PACKAGE_INDICATOR_OF_DEPENDENCY, target.getClass().getPackage().getName());

			// Throw an appropriate exception if the dependent test was not run, failed or ignored.
			Boolean testResult = this.testResults.get(formalizedDependentTest);
			if((testResult == null) || !testResult.booleanValue()) {
				throw new AssumptionViolatedException("Passing of test '" + formalizedDependentTest + "' was a prerequisite for this test, but the dependent test failed, ignored or did not run");
			}
		}
	}
}
}
