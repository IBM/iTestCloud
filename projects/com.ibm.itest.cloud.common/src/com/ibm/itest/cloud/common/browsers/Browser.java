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
package com.ibm.itest.cloud.common.browsers;

import static com.ibm.itest.cloud.common.pages.elements.BrowserElement.MAX_RECOVERY_ATTEMPTS;
import static com.ibm.itest.cloud.common.performance.PerfManager.PERFORMANCE_ENABLED;
import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.*;
import static com.ibm.itest.cloud.common.utils.ByUtils.fixLocator;
import static com.ibm.itest.cloud.common.utils.FileUtil.createDir;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import com.ibm.itest.cloud.common.config.IUser;
import com.ibm.itest.cloud.common.javascript.DrapAndDropSimulator;
import com.ibm.itest.cloud.common.javascript.DrapAndDropSimulator.Position;
import com.ibm.itest.cloud.common.pages.Page;
import com.ibm.itest.cloud.common.pages.elements.BrowserElement;
import com.ibm.itest.cloud.common.pages.frames.*;
import com.ibm.itest.cloud.common.performance.PerfManager;
import com.ibm.itest.cloud.common.scenario.ScenarioUtils;
import com.ibm.itest.cloud.common.scenario.errors.*;
import com.ibm.itest.cloud.common.utils.*;
import com.ibm.itest.cloud.common.utils.ByUtils.ComparisonPattern;

/**
 * Abstract class to handle information of browser used to run FVT Selenium tests.
 * <p>
 * Currently supported browsers are:
 * <ul>
 * <li>Firefox, versions 3.6 and 10</li>
 * <li>InternetExplorer, versions 7, 8 and 9</li>
 * <li>Google Chrome</li>
 * </ul>
 * </p><p>
 * By default, the browser used when running a scenario is Firefox 10. To change
 * it, use the following parameters:
 * <ul>
 * <li><code>"browserKind"</code>
 * <p>
 * Kind of browser to use during the scenario run, one of the following values:
 * <ul>
 * <li><b>1</b>: Firefox</li>
 * <li><b>2</b>: Internet Explorer</li>
 * <li><b>3</b>: Google Chrome</li>
 * </ul>
 * </p><p>
 * <b>Default value</b>: <b>1</b>
 * </p><p>
 * Usage:
 * <ul>
 * <li><code>browserKind=2</code> in the properties file</li>
 * <li><code>-DbrowserKind=2</code> in the VM Arguments field of the launch
 * configuration.</li>
 * </ul></p>
 * </li><li>
 * <p><code>"browserPath"</code>
 * <p>
 * Path for the browser executable on the machine where the scenario is run.
 * If not set, then it's assumed to be on the system path to be accessible by default.
 * </p><p>
 * Note that for Google chrome, this is not the path of Chrome executable, but
 * the path of the Chrome Driver instead.
 * </p><p>
 * Usage:
 * <ul>
 * <li><code>browserPath=C:/Jazz/Firefox/v10/firefox.exe</code> in the properties
 * file</li>
 * <li><code>-DbrowserPath=C:/Jazz/Firefox/v10/firefox.exe</code> in the VM
 * Arguments field of the launch configuration.</li>
 * </ul>
 * </p><p>
 * Note that even for Windows, the path has to use slash.
 * </p>
 * </li><li>
 * <p><code>"browserProfile"</code>
 * <p>
 * The path to the folder of the browser profile. It only works for Firefox and
 * Google Chrome browsers.
 * </p><p>
 * Usage:
 * <ul>
 * <li><code>browserProfile=C:/tmp/profiles/ff.v10/dvpt</code> in the properties
 * file</li>
 * <li><code>-DbrowserProfile=C:/tmp/profiles/ff.v10/dvpt</code> in the VM
 * Arguments field of the launch configuration.</li>
 * </ul>
 * </p><p>
 * Note that even for Windows, the path has to use slash.
 * </p>
 * </li><li>
 * <p><code>"newBrowserSessionPerUser"</code>
 * <p>
 * This is to specify whether or not to open a new browser session per each user.
 * </p><p>
 * Usage:
 * <ul>
 * <li><code>newBrowserSessionPerUser=true</code> in the properties file</li>
 * <li><code>-DnewBrowserSessionPerUser=true</code> in the VM
 * Arguments field of the launch configuration.</li>
 * </ul>
 * </p>
 * </li>
 * </ul>
 * </p><p>
 * This class is also responsible to access the Selenium WebDriver. This new
 * Selenium API deals with {@link WebElement} but due to web page script
 * execution a web element found at some point might become stale a few
 * seconds after. Hence, current class implements the {@link SearchContext} and
 * instantiates internal {@link BrowserElement} which has the ability
 * to recover itself when such failure occurs.
 * </p>
 */
public abstract class Browser implements SearchContext {

	/**
	 * Inner class to help to find elements in current browser page frames even if
	 * some of them are embedded.
	 */
	class FramesScanner {
		int[] currentIndexes = new int[100];
		int[] maxIndexes = new int[100];
		int depth, maxDepth;
		final TargetLocator targetLocator;

		FramesScanner() {
		    this.targetLocator = Browser.this.driver.switchTo();
	    }

		/**
		 * Find the elements matching the given locator in the current browser page
		 * or in any of its visible frames, even if they are embedded.
		 *
		 * @param locator The elements locator
		 * @return The list of found element as a {@link List} of {@link BrowserElement}
		 * or <code>null</code> if none was found.
		 */
		List<BrowserElement> findElements(final By locator) {
			if (DEBUG) debugPrintln("		+ Find elements in frames");

			// Try to find the element in current frame
			int frameIndex = getCurrentFrame() == null ? -1 : getCurrentFrame().getIndex();
			if (DEBUG) debugPrintln("		  -> find element in current frame: "+frameIndex);
			selectFrame();
			List<WebElement> elements = Browser.this.findElements(locator, false/*no recovery*/);
			if (elements.size() > 0) return BrowserElement.getList(elements);

			// Get frames info
			getFrames();

			// Scan discovered frames
			if (DEBUG) debugPrintln("		  -> scan frames to find elements");
			for (int level=0; level<this.maxDepth; level++) {
				if (DEBUG) debugPrintln("			+ level "+level);
				for (int index=0; index<this.maxIndexes[level]; index++) {
					if (DEBUG) debugPrintln("				* index "+index);
					selectParentFrame(level);
					this.targetLocator.frame(index);
					final IndexedFrame webFrame = new IndexedFrame(Browser.this, index);
					elements = Browser.this.findElements(locator, true/*displayed*/, webFrame, false/*no recovery*/);
					if (elements.size() > 0) {
						if (DEBUG) debugPrintln("				-> found "+elements.size()+" elements");
						setCurrentFrame(webFrame);
						return BrowserElement.getList(elements);
					}
				}
			}

			// No element was found in any frame, give up
			return null;
		}

		/**
		 * Get all frames displayed in the current browser page.
		 */
		void getFrames() {
			this.depth = 0;
			getFrames(0);
			this.maxDepth = this.depth +1;
			System.arraycopy(this.maxIndexes, 0, this.maxIndexes = new int[this.maxDepth], 0, this.maxDepth);
			this.currentIndexes = new int[this.maxDepth];
		}

		/**
		 * Get the frames displayed at a certain level of frames depth.
		 *
		 * @param level The parent frame depth level
		 */
		void getFrames(final int level) {
			selectParentFrame(level);
			List<WebElement> frames = Browser.this.findElements(By.tagName("iframe"));
			if (this.depth < level) this.depth = level;
			this.maxIndexes[level] = frames.size();
			for (int index=0; index<this.maxIndexes[level]; index++) {
				this.currentIndexes[level] = index;
				getFrames(level+1);
			}
		}

		/**
		 * Select the frame at the given depth level.
		 *
		 * @param level The depth level
		 */
		void selectParentFrame(final int level) {
			this.targetLocator.defaultContent();
			for (int f=0; f<level; f++) {
				this.targetLocator.frame(this.currentIndexes[f]);
			}
		}
	}
	/*
	 * Enumerations
	 */
	public enum PopupWindowState {
		CLOSED,
		OPENED,
		TRANSIENT
	}
	/*
	 * Constants
	 */
	// Parameters IDs
	private final static String BROWSER_PATH_ID = "browserPath";
	private final static String BROWSER_DRIVER_ID = "browserDriver";
	protected final static String BROWSER_PROFILE_ID = "browserProfile";
	private final static String BROWSER_PROFILE_CONTAINER_ID = "browserProfileContainer";
	public final static String BROWSER_KIND_ID = "browserKind";
	public final static String BROWSER_LOCALE_ID = "browserLocale";
	private final static String BROWSER_HEADLESS_ID ="browserHeadLess";
	private final static String BROWSER_REMOTE_URL = "remoteAddress";
	// Browser kinds
	private final static int BROWSER_KIND_FIREFOX = 1;
	private final static int BROWSER_KIND_IEXPLORER = 2;
	private final static int BROWSER_KIND_GCHROME = 3;
	private final static int BROWSER_KIND_CHROMIUM = 4;
	private final static int BROWSER_KIND_SAFARI = 5;
	// Browser default download directory
	public final static String BROWSER_DOWNLOAD_DIR_ID = "download.dir";
	public final static String BROWSER_DOWNLOAD_DIR_VALUE = "downloads";
	// Browser automatic scroll behaviour
//	protected static final String ELEMENT_SCROLL_BEHAVIOR_ID = "elementScrollBehavior";
//	protected static final String ELEMENT_SCROLL_BEHAVIOR_VALUE = "1";
	// Screenshots directories
	private static final String SELENIUM_SCREENSHOT_DIR_ID = "selenium.screenshot.dir";
	private static final String SELENIUM_SNAPSHOTS_DIR_ID = "selenium.snapshots.dir";
	private static final String SELENIUM_SNAPSHOTS_DIR_DEFAULT = "snapshots";
	private static final int INFO_SNAPSHOT = 0;
	private static final int WARNING_SNAPSHOT = 1;
	private static final int FAILURE_SNAPSHOT = 2;
	// Browser sessions
	private static final String NEW_BROWSER_SESSION_PER_USER = "newBrowserSessionPerUser";
	// Window size
	private static final int MIN_HEIGHT = 900;
	private static final int MAX_HEIGHT = 1200;
	private static final int DEFAULT_HEIGHT = MAX_HEIGHT;
	private static final int MIN_WIDTH = 1200;
	private static final int MAX_WIDTH = 1920;
	private static final int DEFAULT_WIDTH = MAX_WIDTH;
	// Others
	public static final List<WebElement> NO_ELEMENT_FOUND = new ArrayList<WebElement>();

	public static final List<BrowserElement> NO_BROWSER_ELEMENT_FOUND = new ArrayList<BrowserElement>();

	// Selenium specific
	public static final String JAVASCRIPT_ERROR_ALERT_PATTERN = "JavaScript Error: \"(e|h) is null\"";

	/**
     * Create the instance of browser corresponding of the defined parameters.
     *
     * @return A specialized instance of {@link Browser}.
     */
    public static Browser createInstance() {
    	final int kind = getParameterIntValue(BROWSER_KIND_ID, BROWSER_KIND_FIREFOX);
    	switch (kind) {
    		case BROWSER_KIND_FIREFOX:
    			return new FirefoxBrowser();
    		case BROWSER_KIND_IEXPLORER:
    			return new InternetExplorerBrowser();
    		case BROWSER_KIND_GCHROME:
    			return new GoogleChromeBrowser();
    		case BROWSER_KIND_CHROMIUM:
    			return new ChromiumBrowser();
    		case BROWSER_KIND_SAFARI:
    			return new SafariBrowser();
    		default:
    			throw new RuntimeException("'"+kind+"' is not a know browser kind, only 1 (firefox), 2 (IE), 3 (Chrome) or 4 (Safari) are currently supported!");
    	}
    }
	/*
	 * Fields
	 */
	// Browser info
	String path;
	String driverPath;
	String profile;
	String locale;
	URL remoteAddress;
	File downloadDir;
	File logFile;
	protected final boolean headless;

	private final String name;
	// Selenium driver
	WebDriver driver;

	final Actions actions;
	// Snapshots
	private String snapshotsRootDir;

	private File[] snapshotsDir;
	private final File flatSnapshotsDir;

	// Specify whether or not to open a new browser session per each user
	private final boolean newSessionPerUser;

	// Page info
	private String location; // TODO Check the location is necessary for browser
	private String url; // TODO Check the url is necessary for browser
	protected String mainWindowHandle;

	// Frames
	BrowserFrame frame;
	protected BrowserFrame framePopup;

	// TODO The page will be created and cached by the browser
//	private WebPage page;
//	private final Stack<WebPage> history = new Stack<WebPage>();

	// Performances
	final PerfManager perfManager = PerfManager.createInstance(this); // Warning: Can be null!

Browser(final String name) {
	// Init info
	this.name = name;
	String pathValue = getParameterValue(BROWSER_PATH_ID);
    this.path = pathValue == null || pathValue.length() == 0 ? null : pathValue;
    this.driverPath = getParameterValue(BROWSER_DRIVER_ID, this.path);
    this.headless = getParameterBooleanValue(BROWSER_HEADLESS_ID);

    this.profile = getParameterValue(BROWSER_PROFILE_ID);
	String profileContainer = getParameterValue(BROWSER_PROFILE_CONTAINER_ID);
    if((this.profile == null) && (profileContainer != null)) {
    	this.profile =
    		new File(profileContainer, Long.toString(System.currentTimeMillis())).getAbsolutePath();
    }

	this.locale = getParameterValue(BROWSER_LOCALE_ID, "en");

	try {
		String remoteAddressUrl = getParameterValue(BROWSER_REMOTE_URL);
		this.remoteAddress = (remoteAddressUrl != null) ? new URL(remoteAddressUrl) : null;
	}
	catch (MalformedURLException e) {
		throw new ScenarioFailedError(e);
	}

	this.logFile = new File(createDir(DEBUG_DIRECTORY),
		getParameterValue("browser.log.file.name", "browser_log_" + COMPACT_DATE_STRING + ".log"));

	// Initialize the log level for Selenium.
	Logger.getLogger("org.openqa.selenium").setLevel(
		Level.parse(getParameterValue("seleniumLogLevel", Level.SEVERE.getName())));

	// Initialize the profile
	initProfile();

	// Init driver.
	// Important: A WebDriverException can occur during the initialization of the driver
	// due to not being able to bind locking port 7054 within the default timeout 45000 ms
	// if many test scenarios (more than 8) are started simultaneously. In such a situation,
	// the initialization of the driver should be retried after a small delay.
	int attempts = 0;
	while(true) {
		try {
			initDriver();
			// Driver has been successfully initialized. Therefore, break the loop.
			break;
		}
		catch (WebDriverException e) {
			// Retry the driver initialization unless the maximum threshold has been reached.
			if(attempts >= 5) throw e;

			try {
				this.driver.close();
			} catch (Exception e2) {}
			sleep(1 /*seconds*/);
			attempts++;
		}
	}

	this.actions = new Actions(this.driver);

	// Init snapshots directories
	this.snapshotsRootDir = getParameterValue(SELENIUM_SCREENSHOT_DIR_ID);
	if (this.snapshotsRootDir == null) {
		this.snapshotsRootDir = getParameterValue(SELENIUM_SNAPSHOTS_DIR_ID, SELENIUM_SNAPSHOTS_DIR_DEFAULT);
		if (this.snapshotsRootDir.indexOf(File.separatorChar) < 0) {
			this.snapshotsRootDir = System.getProperty(USER_DIR_ID) + File.separator + this.snapshotsRootDir;
		}
		this.flatSnapshotsDir = null;
	} else {
		this.flatSnapshotsDir = new File(this.snapshotsRootDir);
	}

	// Init if a new browser session to be used per each user
	this.newSessionPerUser = getParameterBooleanValue(NEW_BROWSER_SESSION_PER_USER, true);

	// Set the browser window to the desired size.
	initWindow();
}

/**
 * Accept the alert.
 * <p>
 * A warning is displayed in the console with the alerted text.
 * </p>
 */
public void acceptAlert() {
	acceptAlert(null /*message*/);
}

/**
 * Accept the alert with the given message.
 * <p>
 * A warning is displayed in the console when the accepted alert has not the
 * expected text.
 * </p>
 * @param message
 * @return <code>true</code> if the accepted has the correct message,
 * <code>false</code>  otherwise.
 */
public boolean acceptAlert(final String message) {

	// Get a handle to the open alert, prompt or confirmation
	Alert alert = this.driver.switchTo().alert();
	String alertText = alert.getText();

	// Acknowledge the alert (equivalent to clicking "OK")
	alert.accept();

	// Return OK if the alert was expected
	if ((message!= null) && alertText.contains(message)) {
		return true;
	}

	// Warn if this was an unexpected alert
	println("WARNING: Unexpected alert '"+alertText+"' was accepted although '"+ (message != null ? message : "none") +"' was expected.");
	return false;
}

/**
 * Accepts security certificates on Internet Explorer. Switches to a popup window if one exists and clicks
 * link to proceed to website. Does not switch back to main browser window if popup window is found.
 */
public void acceptInternetExplorerCertificate() {

	// Switch to the popup window
	if (DEBUG) debugPrintln("		  -> main window handle "+this.mainWindowHandle);
	Iterator<String> iterator = getWindowHandles().iterator();
	while (iterator.hasNext()) {
		String handle = iterator.next();
		if (!handle.equals(this.mainWindowHandle)) {
			if (DEBUG) debugPrintln("		  -> switch to window handle "+handle);
			this.driver.switchTo().window(handle);
			break;
		}
	}
	try {
		if (this.driver.getCurrentUrl().contains("invalidcert.htm")) {
			if (DEBUG) debugPrintln("		+ Accept Internet Explorer certificate");
			this.driver.navigate().to("javascript:document.getElementById('overridelink').click()");
		}
	}
	catch (Exception ex) {
		// skip
	}
}

/**
 * Move back a single "item" in the browser's history.
 * <p>
 * <b>Warning</b>: This method does not modify the web pages cache, hence
 * it should be used with caution. It would be better to use {@link Page#goBack()}
 * method instead to keep browser content and pages cache synchronized.
 * </p><p>
 * Note that this will possible desynchronization will be fixed when pages cache
 * is managed by the browser itself instead of WebPage class...
 * </p>
 * @see "https://jazz.net/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/253564"
 * @see org.openqa.selenium.WebDriver.Navigation#back()
 */
public void back() {
	if (DEBUG) {
		debugPrintln("		+ Move back one step in browser history: ");
		debugPrintln("		  -> current state: "+this);
	}
	if (isGoogleChrome()) sleep(2);
	this.driver.navigate().back();
	sleep(2);
	purgeAlerts("While going back to previous page...");
	this.location = getCurrentUrl();
	debugPrintln("		  -> location after back: "+this.location);

	// Set current page
//	this.page = this.history.pop();
}

/**
 * Catch WebDriverException until max allowed recovery tries is reached.
 *
 * TODO Change the exception parameter as StaleElementReferenceException
 * as this is the only caught exception now...
 */
public void catchWebDriverException(final WebDriverException wde, final String title, final int count) {
	// Special treatment for alert exception
	if (purgeAlerts(title) > 0) {
		return;
	}

	// Give up right now if the exception is too serious
	if (!(wde instanceof StaleElementReferenceException)) {
		debugPrintln("Fatal exception occured when "+title+"'... give up");
    	debugPrintException(wde);
		throw wde;
	}

	// If max retry has been reached, then really throw the WDE exception
	if (count > MAX_RECOVERY_ATTEMPTS) {
		debugPrintln("More than 10 exceptions occured when "+title+"'... give up");
    	debugPrintException(wde);
		throw wde;
	}

	// ScenarioWorkaround the WDE exception
	// Workaround
	debugPrint("Workaround exception when "+title+"': ");
	debugPrintException(wde);
}

/**
 * Set, unset or toggle the given element assuming this is a check-box.
 *
 * @param element The check-box in the current page
 * @param toggle Tells whether it should be toggled (0), set "on" (1) or set "off" (-1).
 * For all other values than 0, 1 or -1, 0 will be used.
 * @param validate Validate whether the check-box value is well set.
 * @return <code>true</code>If the check-box value has been changed,
 * <code>false</code> otherwise.
 */
public boolean check(final BrowserElement element, final int toggle, final boolean validate) {
	if (DEBUG) debugPrintln("		+ check box '"+element+"'");

	// Check the box
	boolean expected;
	boolean selected = element.isSelected();
	String selectedString = selected ? "selected" : "unselected";
	switch (toggle) {
		case 1: // set "on"
			if (selected) {
				if (DEBUG) debugPrintln("		  -> check-box was already 'on'");
				return false;
			}
			if (DEBUG) debugPrintln("		  -> set check-box to 'on'");
			element.click();
			expected = true;
			pause(250);
			break;
		case -1: // set "off"
			if (selected) {
				if (DEBUG) debugPrintln("		  -> set check-box to 'off'");
				element.click();
			} else {
				if (DEBUG) debugPrintln("		  -> check-box was already 'off'");
				return false;
			}
			expected = false;
			pause(250);
			break;
		default: // toggle
			if (DEBUG) debugPrintln("		  -> check-box was "+selectedString);
			element.click();
			pause(250);
			if (DEBUG) debugPrintln("		  -> check-box is now '"+(element.isSelected() ? "selected" : "unselected")+"'");
			expected = !selected;
			break;
	}

	// Validate the check-box state
	if (validate) {
		String expectedString = expected ? "selected" : "unselected";
		if (DEBUG) debugPrintln("		  -> validate that check-box is now "+expectedString+"...");
		long timeoutMillis = 10 * 1000 + System.currentTimeMillis();
		while (element.isSelected() != expected) {
			if (System.currentTimeMillis() > timeoutMillis) { // wait 10 seconds max
				if (DEBUG) debugPrintln("		  -> never turned "+expectedString+"!");
				throw new WaitElementTimeoutError("Check-box never turned "+expectedString+"!");
			}
			element.click();
		}
	}

	// The check-box value has changed
	return true;
}

/**
 * Check whether there's a connection error or not.
 *
 * @throws BrowserConnectionError If there's a connection error with the current page.
 * TODO Check that the web element is the same for all browsers
 */
public void checkConnection() {
	BrowserElement errorContainerElement = findElement(By.xpath("//*[@id='errorPageContainer']"));
	if (errorContainerElement != null) {
		throw new BrowserConnectionError("Cannot access page "+getCurrentUrl());
	}
}

/**
 * Click on the given element at an offset from the top-left corner of the element.
 *
 * @param element element to click.
 * @param xOffset Offset from the top-left corner. A negative value means coordinates left from
 * the element.
 * @param yOffset Offset from the top-left corner. A negative value means coordinates above
 * the element.
 */
public BrowserElement click(final BrowserElement element, final int xOffset, final int yOffset) {
	this.actions.moveToElement(element.getWebElement(), xOffset, yOffset);
	this.actions.click();
	this.actions.build().perform();

	return element;
}

/**
 * Click on the given element and might validate whether it turns disabled after
 * the operation (e.g. for save buttons...)
 *
 * @param button The button to click on
 * @param timeout The time in seconds to wait before failing if the button never
 * turns enable.
 * @param validate Validate whether the button turns disabled after having been
 * clicked.
 */
public BrowserElement clickButton(final BrowserElement button, final int timeout, final boolean validate) {
	if (DEBUG) debugPrintln("		+ click on button '"+button+"'");

	// Wait until the button is enabled.
	button.waitWhileDisabled(timeout, true /*fail*/);

	// Click on enabled button.
	// At times, the button element may be obscured by another element and therefore, not be clickable.
	// As a result, a WebDriverException can occur.
	try {
		button.click();
	}
	catch (WebDriverException e) {
		// If the button.click() method causes a WebDriverException, use JavaScript to perform the
		// click on the link element in this case.
		debugPrintln("Clicking on button element (WebBrowserElement.click()) caused following error. Therefore, try JavaScript (WebBrowserElement.clickViaJavaScript()) to perform click as a workaround.");
		debugPrintln(e.toString());
		debugPrintStackTrace(e.getStackTrace(), 1 /*tabs*/);
		button.clickViaJavaScript();
	}

	// Check, if requested, that button turn disabled after the click
	if (validate) {
		if (DEBUG) debugPrintln("		+ check button to turn disabled");
		boolean click = true;
		long timeoutMillis = timeout * 1000 + System.currentTimeMillis();

		while (button.isEnabled(false)) {

			// Check whether the button is still displayed. If not, then stop the validation and dump a warning message.
			if (!button.isDisplayed(false)) {
				println("WARNING: Button has disappeared while trying to validate that it was turning disabled!");
				printStackTrace(1);
				println("Workaround is to cancel the validation but that means the scenario should not have tried to validate when clicking on that button...");
				println();
				break;
			}

			// Try to redo the action.
			if (DEBUG) debugPrintln("		  -> redo action by "+(click?"clicking again on the button":"hitting the Enter key"));
			if (click) {
				button.click();
			} else {
				button.sendKeys(Keys.ENTER);
			}
			click = !click;

			// Give up after 10 seconds
			if (System.currentTimeMillis() > timeoutMillis) {
				if (DEBUG) debugPrintln("		  -> never turned disabled!");
				throw new WaitElementTimeoutError("Button never turned disabled!");
			}
		}
	}

	// Return button
	return button;
}

/**
 * Close the browser which closes every associated window.
 */
public void close() {
	if (DEBUG) debugPrintln("		+ Close browser.");

	// Save performance results and close the writers/logs
	if (this.perfManager != null) {
		this.perfManager.close();
	}

	// Shutdown current Selenium session
//	this.driver.quit();
	// Workaround for Firefox issue https://bugzilla.mozilla.org/show_bug.cgi?id=1027222
	// See also https://code.google.com/p/selenium/issues/detail?id=7506

	// Close the driver.
	try {
		if (DEBUG) debugPrintln("		  -> closing current window (handle: "+this.driver.getWindowHandle()+")");
		this.driver.close();
		sleep(1);
	}
	catch (Exception e) {
		if (DEBUG) debugPrintln("		  -> Browser looks to have been already closed, do nothing...");
		return;
	}

	// Shutdown all other existing sessions if any
	try {
		Set<String> windowHandles = this.driver.getWindowHandles();
		int size = windowHandles.size();
		if (size > 0) {
			if (DEBUG) debugPrintln("		  -> "+size+" other window"+(size>1?"s were opened, close them all.":" was opened, close it."));
			for (String handle: windowHandles) {
				if (DEBUG) debugPrintln("			+ handle "+handle);
				this.driver.switchTo().window(handle);
				this.driver.close();
				sleep(1);
			}
		}
	}
	catch (Exception e) {
		// skip
	}

	// TODO Clean page caches
//	this.page = null;
//	this.history.clear();
}

/**
 * Close the popup window, assuming there is more than one window open.
 * Switches to the main window.  Added as part of working with RM Reviews as
 * of 6.0, where a new window (or tab) is opened when a review is started.
 *
 * @throws ScenarioFailedError if this is attempted with only one window open.
 */
public void closePopupWindow() {
	// Only do this if there is more than one window
	if (this.driver.getWindowHandles().size() > 1) {

		// Switch to the popup window
		if (DEBUG) debugPrintln("		  -> main window handle "+this.mainWindowHandle);
		Iterator<String> iterator = getWindowHandles().iterator();
		while (iterator.hasNext()) {
			String handle = iterator.next();
			if (!handle.equals(this.mainWindowHandle) && !handle.equals(this.driver.getWindowHandle())) {
				if (DEBUG) debugPrintln("		  -> switch to window handle "+handle);
				this.driver.switchTo().window(handle);
				break;
			}
		}

		if (DEBUG) debugPrintln("		  -> closing current window (handle: "+this.driver.getWindowHandle()+")");
		this.driver.close();

	} else {
		throw new ScenarioFailedError("Trying to the popup but there's only one window.");
	}

	// Make sure we're back on the current window.
	switchToMainWindow();
}

/**
 * Delete all current browser cookies.
 *
 * @see Options#deleteAllCookies()
 */
public void deleteAllCookies() {
	this.driver.manage().deleteAllCookies();
}

/**
 * Delete the given cookie stored in the browser.
 *
 * @param cookieName The name of the cookie to be deleted.
 * @see Options#deleteCookieNamed(String)
 */
public void deleteCookieNamed(final String cookieName) {
	this.driver.manage().deleteCookieNamed(cookieName);
}

/**
 * Double-click on the given element;
 *
 * @param element The web element to doubl-click on
 */
public void doubleClick(final BrowserElement element) {
	this.actions.moveToElement(element.getWebElement());
	this.actions.doubleClick(element.getWebElement());
	this.actions.build().perform();
}

/**
 * Drag given sourceElement and drop it to targetElement.
 *
 * @param sourceElement The web element to be dragged
 * @param targetElement The web element over which sourceElement has to be dropped
 * @see Actions#dragAndDrop(WebElement, WebElement)
 */
public void dragAndDrop(final BrowserElement sourceElement, final BrowserElement targetElement) {
	debugPrintln("		+ Drag " + sourceElement + " to " + targetElement);
	this.actions.moveToElement(sourceElement.getWebElement());
	this.actions.dragAndDrop(sourceElement.getWebElement(), targetElement.getWebElement());
	this.actions.build().perform();
}

/**
 * Perform a drag and drop from the given horizontal and vertical offsets.
 *
 * @param element The element to be dragged and dropped
 * @param xOffset The horizontal offset for the drag
 * @param yOffset The vertical offset for the drag
 */
public void dragAndDropBy(final BrowserElement element, final int xOffset, final int yOffset) {
	debugPrintln("		+ Drag " + element + " to (" + xOffset+", "+yOffset+")");
	this.actions.moveToElement(element.getWebElement());
	this.actions.dragAndDropBy(element.getWebElement(), xOffset, yOffset);
	this.actions.build().perform();
}

/**
 * Drag given sourceElement and drop it to targetElement via JavaScript.
 *
 * @param sourceElement The web element to be dragged
 * @param targetElement The web element over which sourceElement has to be dropped
 * @see Actions#dragAndDrop(WebElement, WebElement)
 */
public void dragAndDropViaJavaScript(final BrowserElement sourceElement, final BrowserElement targetElement) {
	debugPrintln("		+ Drag " + sourceElement + " to " + targetElement);
	dragAndDropViaJavaScript(sourceElement, targetElement, Position.Top_Left, Position.Top_Left);
}

@SuppressWarnings("boxing")
private void dragAndDropViaJavaScript(final BrowserElement dragFrom, final BrowserElement dragTo, final Position dragFromPosition, final Position dragToPosition) {
	Point fromLocation = dragFrom.getLocation();
	Point toLocation = dragTo.getLocation();
	Dimension fromSize = dragFrom.getSize();
	Dimension toSize = dragTo.getSize();

	// Get Client X and Client Y locations
	int dragFromX= fromLocation.getX() + dragFromPosition.getOffset(fromSize.getWidth());
	int dragFromY= fromLocation.getY() + dragFromPosition.getOffset(fromSize.getHeight());
	int dragToX= toLocation.getX() + dragToPosition.getOffset(toSize.getWidth());
	int dragToY= toLocation.getY() + dragToPosition.getOffset(toSize.getHeight());

	getJavascriptExecutor().executeScript(DrapAndDropSimulator.JAVASCRIPT_SIMULATE_EVENHTML5_DRAGANDDROP, dragFrom.getWebElement(), dragTo.getWebElement(), dragFromX, dragFromY, dragToX, dragToY);
}

/**
 * Execute the given script on the WebBrowser Element
 *
 * @param script The script to execute.
 * @param args The arguments to the script. May be empty.
 *
 * @return One of Boolean, Long, String, List or WebElement. Or null.
 */
public Object executeScript(final String script, final Object... args) {
	return getJavascriptExecutor().executeScript(script, args);
}

/**
 * Find an element in the current browser page for the given locator.
 * <p>
 * Note that this method allow recovery while trying to find the element
 * (see {@link #findElement(By, boolean)} for details on recovery). So, if an
 * exception occurs during the operation it will retry it {@link ScenarioUtils#MAX_RECOVERY_TRIES}
 * times before giving up and actually raise the exception...
 * </p><p>
 * The element is searched in the current browser frame.
 * </p>
 * @param locator The way to find the element in the page (see {@link By}).
 * @return The found element as {@link BrowserElement}.
 */
@Override
public BrowserElement findElement(final By locator) {
	return findElement(locator, getCurrentFrame(), true/*recovery*/);
}

/**
 * Find an element in the current browser page for the given locator.
 * <p>
 * If recovery is allowed, {@link WebDriverException} exceptions are caught
 * and the operation is retried again until maximum of allowed retries is reached.
 * </p><p>
 * If recovery is not allowed, <code>null</code> is returned when a {@link WebDriverException}
 * occurs...
 * </p><p>
 * The element is searched in the current browser frame.
 * </p>
 * @param locator The locator to find the element in the page.
 * @param recovery Tells whether recovery is allowed when searching the element.
 * @return The found element as {@link BrowserElement} or <code>null</code>.
 */
public BrowserElement findElement(final By locator, final boolean recovery) {
	return findElement(locator, getCurrentFrame(), recovery);
}

/**
 * Find an element in the current browser page for the given locator.
 * <p>
 * If recovery is allowed, {@link WebDriverException} exceptions are caught
 * and the operation is retried again until maximum of allowed retries is reached.
 * </p><p>
 * If recovery is not allowed, <code>null</code> is returned when a {@link WebDriverException}
 * occurs...
 * </p>
 * @param locator The locator to find the element in the page.
 * @param webFrame The expected frame where the element should be searched
 * @param recovery Tells whether recovery is allowed when searching the element.
 * @return The found element as {@link BrowserElement} or <code>null</code>.
 * TODO Add the ability not to throw {@link ScenarioFailedError} when not found
 * (ie. add <code>fail</code> argument..)
 */
public BrowserElement findElement(final By locator, final BrowserFrame webFrame, final boolean recovery) {
	if (DEBUG) debugPrintln("			(finding element "+locator+" for browser "+this+" in frame '"+webFrame+"')");

	// Fix locator if necessary
	By fixedLocator = fixLocator(locator);

	// Loop until exception
	int count = 0;
	while (true) {
		try {
			// Create a specific web element to be able to manage recovery
			BrowserElement webBrowserElement = new BrowserElement(this, webFrame, this.driver, fixedLocator);

			// If element is created, then no exception occurs, return it
			if (DEBUG) debugPrintln("			(  -> found "+webBrowserElement+")");
			return webBrowserElement;

		}
		catch (NoSuchElementException nsee) {
			return null;
		}
		catch (UnhandledAlertException uae) {
			purgeAlerts("Finding element '"+fixedLocator+"'");
			if (!recovery) {
				return null;
			}
		}
		catch (WebDriverException wde) {
			// If recovery is allowed, catch exception to retry
			if (recovery) {
				catchWebDriverException(wde, "finding element '"+fixedLocator+"'", count++);
			} else {
				// If not, dump exception and leave with no result
				if (DEBUG) debugPrintException(wde);
				return null;
			}
		}
	}
}

/**
 * Find an element with the given locator in the current browser page or one of
 * its visible frame, even if there are some embedded ones.
 * <p>
 * If an element is found in one of this frame, it becomes the current browser
 * frame. In case no element is found, the browser has no frame selected when
 * returning from this method.
 * </p><p>
 * Note that this method allow recovery while trying to find the element
 * (see {@link #findElements(By, boolean)} for details on recovery). So, if an
 * exception occurs during the operation it will retry it {@link ScenarioUtils#MAX_RECOVERY_TRIES}
 * times before giving up and actually raise the exception...
 * </p>
 * @param locator The locator to find the element in the page (see {@link By}).
 * @return The found element as {@link BrowserElement}.
 * TODO Try to get rid off this method as its has a high performance cost.
 * Frames should be explicitly handled...
 */
public BrowserElement findElementInFrames(final By locator) {
	if (DEBUG) debugPrintln("		+ Find frame element");

	// Find elements
	List<BrowserElement> foundElements = findElementsInFrames(locator);
	if (foundElements == null) return null;
	int size = foundElements.size();
	if (size == 0) return null;

	// Warn if several are found
	if (size > 1) {
//		if (single) {
//			throw new MultipleVisibleElementsError("Unexpected multiple elements found.");
//		}
		debugPrintln("WARNING: found more than one elements ("+size+"), return the first one!");
	}

	// Return the found element
	return foundElements.get(0);
}

/**
 * Find elements in the current browser page for the given locator.
 * <p>
 * Note that this method allow recovery while trying to find the element
 * (see {@link #findElements(By, boolean)} for details on recovery). So, if an
 * exception occurs during the operation it will retry it {@link ScenarioUtils#MAX_RECOVERY_TRIES}
 * times before giving up and actually raise the exception...
 * </p>
 * @param locator The locator to find the element in the page.
 * @return The list of found elements as {@link List}. Note that each element of
 * the list is a  {@link BrowserElement}.
 */
@Override
public List<WebElement> findElements(final By locator) {
	return findElements(locator, true/*displayed*/, getCurrentFrame(), true/*recovery*/);
}

/**
 * Find elements in the current browser page for the given locator.
 *
 * @param locator The locator to find the element in the page
 * @param recovery Tells whether the research should try to workaround safely or
 * return <code>null</code> right away if any {@link WebDriverException}
 * exception occurs.
 * @return The list of found elements as {@link List}. Each element of the list
 * is a  {@link BrowserElement}.
 */
public List<WebElement> findElements(final By locator, final boolean recovery) {
	return findElements(locator, true/*displayed*/, getCurrentFrame(), recovery);
}

/**
 * Find elements in the current browser page for the given locator.
 *
 * @param locator The locator to find the element in the page
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @param recovery Tells whether the research should try to workaround safely or
 * return <code>null</code> right away if any {@link WebDriverException}
 * exception occurs.
 * @return The list of found elements as {@link List}. Each element of the list
 * is a  {@link BrowserElement}.
 */
public List<WebElement> findElements(final By locator, final boolean displayed, final boolean recovery) {
	return findElements(locator, displayed, getCurrentFrame(), recovery);
}

/**
 * Find elements in the current browser page for the given locator.
 *
 * @param locator The locator to find the element in the page
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @param webFrame The expected frame where the element should be searched
 * @param recovery Tells whether the research should try to workaround safely or
 * return <code>null</code> right away if any {@link WebDriverException}
 * exception occurs.
 * @return The list of found elements as {@link List}. Each element of the list
 * is a  {@link BrowserElement}.
 */
public List<WebElement> findElements(final By locator, final boolean displayed, final BrowserFrame webFrame, final boolean recovery) {
	if (DEBUG) debugPrintln("			(finding elements "+locator+" for "+this+")");

	// Fix locator if necessary
	By fixedLocator = fixLocator(locator);

	// Loop until exception
	int count = 0;
	while (true) {
		try {
			// Find web driver elements with the given locator
			List<WebElement> foundElements = this.driver.findElements(fixedLocator);

			// Workaround: it seems that although WebDriver.findElements contract
			// assumes to return a list, it might happen that a <code>null</code>
			// value was returned (might be a Selenium bug?).
			// To prevent the NPE, returns an empty list when this unexpected case happens
			if (foundElements == null) {
				if (DEBUG) debugPrintln("Workaround: return empty list when this.driver.findElements(By) returns null!");
				return NO_ELEMENT_FOUND;
			}

			// Seek the list got to build framework web elements list
			final int size = foundElements.size();
			int idx = 0;
			List<WebElement> pageElements = new ArrayList<WebElement>(size);
			for (WebElement foundElement: foundElements) {
				if (foundElement == null) {
					// Workaround: it happened sometimes that we get null slots
					// in the returned list (might be a Selenium bug?)!
					if (DEBUG) debugPrintln("Workaround: skip null element in this.driver.findElements(By) list!");
				} else {
					boolean isDisplayed = false;
					try {
						// Check whether the element is displayed
						isDisplayed = !displayed || foundElement.isDisplayed();
					}
					catch (NullPointerException | WebDriverException e) {
						// Skip a NullPointerException can occur from Selenium in FireFox by considering the faulty element as not displayed.
						// In addition, skip any web driver exception by considering the faulty element as not displayed.
					}
					if (isDisplayed) {
						// Create a specific web element to be able to manage recovery
						final BrowserElement webBrowserElement = new BrowserElement(this, webFrame, this.driver, fixedLocator, foundElement, size, idx);
						pageElements.add(webBrowserElement);
						if (DEBUG) {
							debugPrint("			  (-> found '"+webBrowserElement);
							debugPrintln(")");
						}
					} else {
						if (DEBUG) debugPrintln("			  (-> element not displayed)");
					}

					// Increment the web elements index in its parent list
					idx++;
				}
			}

			// Return the web elements list
			return pageElements;
		}
		catch (UnreachableBrowserException ure) {
			throw ure;
		}
		catch (WebDriverException wde) {
			// If recovery is allowed, catch exception to retry
			if (recovery) {
				catchWebDriverException(wde, "finding elements '"+fixedLocator+"'", count++);
			} else {
				// If not, dump exception and leave with no result
				if (DEBUG) debugPrintException(wde);
				return NO_ELEMENT_FOUND;
			}
		}
	}
}

/**
 * Find elements with the given locator in the current browser page or one of
 * its visible frame, even if there are some embedded ones.
 * <p>
 * If elements are found in one of this frame, it becomes the current browser
 * frame. In case no element is found, the browser has no frame selected when
 * returning from this method.
 * </p><p>
 * Note that this method allow recovery while trying to find the element
 * (see {@link #findElements(By, boolean)} for details on recovery). So, if an
 * exception occurs during the operation it will retry it {@link ScenarioUtils#MAX_RECOVERY_TRIES}
 * times before giving up and actually raise the exception...
 * </p>
 * @param locator The locator to find the element in the page (see {@link By}).
 * @return The found element as {@link BrowserElement}.
 * TODO Try to get rid off this method as its has a high performance cost.
 * Frames should be explicitly handled...
 */
public List<BrowserElement> findElementsInFrames(final By locator) {
	if (DEBUG) debugPrintln("		+ Find frame elements");

	// Check that no popup is opened
	if (hasPopupWindow()) {
		throw new ScenarioFailedError("An element cannot be found by looking in page frames when there's an opened popup window!");
	}

	// Return found elements
	FramesScanner scanner = new FramesScanner();
	return scanner.findElements(locator);
}

/**
 * Get the web page content at the given URL.
 * <p>
 * This is a no-op if either the browser url or the driver url are already at the
 * given page url.
 * </p><p>
 * This method already handles the InternetExplorer certificate, no needs to add
 * specific after this call to manage it.
 * </p><p>
 * Alerts opened at the page opening are also handled by this method and purged
 * as workarounds.
 * </p>
 *
 * @param pageLocation The page location
 * @see WebDriver#get(String)
 */
public final void get(final String pageLocation) {

	// URL has changed, load the new one
	String currentUrl = this.driver.getCurrentUrl();
	if (pageLocation.equals(currentUrl)) {
		if (DEBUG) debugPrintln("INFO: browser was already at '"+pageLocation+"'.");
	} else {
		if (DEBUG) {
			debugPrintln("		+ browser get: "+pageLocation);
			debugPrintln("		  -> driver url: "+currentUrl);
			debugPrintln("		  -> stored location: "+this.location);
		}

		// Get current location
		this.driver.get(pageLocation);

		// Hack to bypass the Navigation Error page
		if (isInternetExplorer()) {
			acceptInternetExplorerCertificate();
		}

		// Purge alerts if necessary
		purgeAlerts("Alerts observed when getting page "+pageLocation);

		// Wait 2 seconds that browser URL changes (only if not in login operation)
		if (!currentUrl.endsWith("login")) {
			long timeout = System.currentTimeMillis() + 2000; // 2 seconds
			while (currentUrl.equals(this.driver.getCurrentUrl())) {
				if (System.currentTimeMillis() > timeout) {
					break;
//					throw new BrowserError("Browser URL didn't change after having being set to '"+pageLocation+"', it stayed at '"+currentUrl+"'");
				}
			}
		}
	}

	// Store new location
	this.location = pageLocation;

	// Dump the info that there was a page redirection
	// (or at least the browser URL has changed after the page load...)
	if (!pageLocation.equals(this.driver.getCurrentUrl())) {
		// Info
		if (DEBUG) debugPrintln("INFO: URL of page '"+pageLocation+"' has changed to '"+this.driver.getCurrentUrl()+"' just after having been loaded.");
	}
}

/**
 * Return the actions instance.
 *
 * @return The actions instance as {@link Actions}.
 */
public Actions getActions() {
	return this.actions;
}

/**
 * Get the current cookies
 *
 * @return The current cookies in web browser
 * @see Options#getCookies()
 */
public Set<Cookie> getCookies() {
	return this.driver.manage().getCookies();
}

///**
// * Get the web page content for the given web page.
// * <p>
// * This is a no-op if the browser has already loaded the page.
// * </p>
// *
// * @param newPage The page to open
// * @see #get(String)
// */
//public final void get(final WebPage newPage) {
//	if (this.page != null) {
//		this.history.push(this.page);
//	}
//	this.page = newPage;
//	get(newPage.location);
//}

/**
 * Return the current frame used by the browser.
 * <p>
 * Note that in case of an opened popup window, this the frame of this window
 * which is returned.
 * </p>
 * @return The frame as a {@link BrowserFrame}
 */
public BrowserFrame getCurrentFrame() {
	if (hasPopupWindow()) {
		return this.framePopup;
	}
	return this.frame;
}

/**
 * Return the current frame used by the browser.
 *
 * @return The frame as a {@link BrowserFrame}
 */
public BrowserFrame getFrame() {
	return this.frame;
}

/**
 * Get the current page URL.
 *
 * @return The page URL as a {@link String}.
 * @see WebDriver#getCurrentUrl()
 */
public final String getCurrentUrl() {
	return this.url = this.driver.getCurrentUrl();
}

/**
 * Get the path of the default download directory.
 *
 * @return The path of the default download directory as a {@link File}
 */
public File getDownloadDir(){
    return this.downloadDir;
}

/**
 * Return a list of files in the download directory.
 *
 * @return A list of files in the download directory as {@link List}.
 */
public List<File> getDownloadDirContents() {
	File[] files = getDownloadDir().listFiles(new FileFilter() {
		@Override
		public boolean accept(final File pathname) {
			return pathname.isFile();
		}
	});

	return files != null ? Arrays.asList(files) : new ArrayList<File>(0 /*initialCapacity*/);
}

/**
 * Return the driver corresponding to the current browser.
 *
 * @return The driver corresponding to the current browser as {@link WebDriver}.
 */
public WebDriver getDriver() {
	return this.driver;
}

private JavascriptExecutor getJavascriptExecutor() {
	return (JavascriptExecutor) this.driver;
}

/**
 * Return the name of the currently running browser.
 *
 * @return The name of the currently running browser.
 */
public String getName() {
	return this.name;
}

///**
// * Return the page currently loaded in the browser.
// *
// * @return The loaded page as a {@link WebPage}.
// */
//public WebPage getCurrentPage() {
//	return this.page;
//}

/**
 * Return a new file appeared in the download directory.
 * <p>
 * Such a file may appear as a result of a download.
 * </p>
 *
 * @param initialFiles A list of file existed in the download directory
 * prior for the new file to appear.
 *
 * @return The new file appeared in the download directory as {@link File}.
 */
public File getNewFileInDownloadDir(final List<File> initialFiles) {
	List<File> currentFiles = getDownloadDirContents();
	for (File currentFile : currentFiles) {
		if(!initialFiles.contains(currentFile)) {
			return currentFile;
		}
	}

	return null;
}

/**
 * Return the performance manager.
 *
 * @return The performance manager as a {@link PerfManager}.
 */
public PerfManager getPerfManager() {
	return this.perfManager;
}

/**
 * Return the version of the currently running browser.
 *
 * @return The version of the currently running browser.
 */
public String getVersion() {
	return ((RemoteWebDriver)this.driver).getCapabilities().getBrowserVersion();
}

/**
 * Find the visible frame expecting this is the last one. Display warnings if they
 * are several visible frame or if it's not the last frame in the browser page.
 */
private BrowserElement getVisibleFrame(final int retries) throws ScenarioFailedError {
	debugPrintln("		+ Get visible frame");

	// Reset frame
	resetFrame();

	// Get all page frames
	List<BrowserElement> frames = waitForElements(null, By.tagName("iframe"), true, 10/*sec*/, false);

	// Wait for a visible frame
	BrowserElement visibleFrameElement = null;
	BrowserElement lastFrameElement = null;
	for (BrowserElement frameElement: frames) {
		if (frameElement.isDisplayed()) {
			if (visibleFrameElement != null) {
				throw new ScenarioFailedError("There are several visible frame!!!");
			}
			visibleFrameElement = frameElement;
			debugPrintln("		  -> visible frame: "+frameElement);
		}
		lastFrameElement = frameElement;
	}

	// Retry if none was found
	if (visibleFrameElement == null) {
		debugPrintln("		  -> no visible frame was found");
		if (retries > 10) {
			throw new ScenarioFailedError("No visible frame found.");
		}
		debugPrintln("		  -> retry after having waited 1 seconds ("+retries+")");
		sleep(1);
		return getVisibleFrame(retries+1);
	}

	// Retry if the visible frame is not the last one and that was expected
	if (visibleFrameElement != lastFrameElement) {
		debugPrintln("		  -> last frame was not visible.");
		if (retries < 2) {
			debugPrintln("		  -> retry after having waited 1 seconds ("+retries+")");
			sleep(1);
			return getVisibleFrame(retries+1);
		}
		debugPrintln("WORKAROUND: select visible frame which was not the last one!");
	}

	// Store the visible frame
	return visibleFrameElement;
}

/**
 * Return a set of window handles which can be used to iterate over all open windows of this WebDriver instance.
 *
 * @return A set of window handles which can be used to iterate over all open windows.
 */
public Set<String> getWindowHandles() throws ScenarioFailedError {
//	Set<String> handles = this.driver.getWindowHandles();
//	if (handles.size() > 2) {
//		println("Unexpected number of window handles: "+handles.size());
//		println(" - main window handle: "+this.mainWindowHandle);
//		println(" - handles: "+getTextFromList(handles));
//		throw new ScenarioFailedError("Unexpected number of frames: "+handles.size());
//	}
	return this.driver.getWindowHandles();
}

/**
 * Tells whether the browser has a frame or not.
 *
 * @return <code>true</code> if a frame is selected, <code>false</code> otherwise.
 */
public boolean hasFrame() {
	return getCurrentFrame() != null;
}

/**
 * Tells whether a popup windows is currently opened.
 *
 * @return <code>true</code> if a popup window is opened,
 * <code>false</code> otherwise.
 */
public boolean hasPopupWindow() {
	if (getWindowHandles().size() > 1) {
		return true;
	}
	this.framePopup = null;
	return false;
}

/**
 * Hover over the middle of the given element.
 *
 * @param element element to hover over.
 */
public BrowserElement hover(final BrowserElement element) {
	this.actions.moveToElement(element.getWebElement());
	this.actions.build().perform();

	return element;
}

/*
 * Init the driver corresponding to the current browser.
 */
abstract void initDriver();

/*
 * Init the browser profile.
 * The default is to initialize download directory if necessary in case it was used
 * while setting the profile.
 */
void initProfile() {
	String dirPath = getParameterValue(BROWSER_DOWNLOAD_DIR_ID, BROWSER_DOWNLOAD_DIR_VALUE);
	if (dirPath != null) {
		this.downloadDir = FileUtil.createDir(dirPath);
	}
}

protected void initWindow() {

	// Store the main window handle
	this.mainWindowHandle = this.driver.getWindowHandle();

	// Read width argument
	String paramWidth = getParameterValue("windowWidth");
	int windowWidth = DEFAULT_WIDTH;
	if (paramWidth != null) {
		try {
			windowWidth = Integer.parseInt(paramWidth);
		}
		catch (NumberFormatException nfe) {
			throw new ScenarioFailedError(paramWidth+" is not a valid number.");
		}
	}

	// Read height argument
	String paramHeight = getParameterValue("windowHeight");
	int windowHeight = DEFAULT_HEIGHT;
	if (paramHeight != null) {
		try {
			windowHeight = Integer.parseInt(paramHeight);
		}
		catch (NumberFormatException nfe) {
			throw new ScenarioFailedError(paramHeight+" is not a valid number.");
		}
	}

	// Wait for the connection to appear in the project. It may take a while.
	int timeout = 1;
	long timeoutMillis = timeout * 60 * 1000 + System.currentTimeMillis();

	// Set the window size. A WebDriverException can occur while setting the window size at times.
	// In such a situation, wait a moment and try to set the windows size again and again until the timeout is reached.
	while(true) {
		try {
			setWindowSize(windowWidth, windowHeight);
			break;
		}
		catch (WebDriverException e) {
			if (System.currentTimeMillis() > timeoutMillis) {
				throw e;
			}
		}
	}
}

/**
 * Tells whether the current browser is Chromium/Electron or not.
 *
 * @return <code>true</code> if the current browser is Chromium or Electron, <code>false</code>
 * otherwise.
 */
public abstract boolean isChromium();

/**
 * Tells whether the current browser is Edge or not.
 *
 * @return <code>true</code> if the current browser is Edge, <code>false</code>
 * otherwise.
 */
public abstract boolean isEdge();

/**
 * Tells whether the current browser is Firefox or not.
 *
 * @return <code>true</code> if the current browser is FF, <code>false</code>
 * otherwise.
 */
public abstract boolean isFirefox();

/**
 * Tells whether the current browser is Google Chrome or not.
 *
 * @return <code>true</code> if the current browser is GC, <code>false</code>
 * otherwise.
 */
public abstract boolean isGoogleChrome();

/**
 * Tells whether the current browser is Internet Explorer or not.
 *
 * @return <code>true</code> if the current browser is IE, <code>false</code>
 * otherwise.
 */
public abstract boolean isInternetExplorer();

/**
 * Specify whether the tests are executed on a remote host.
 * <p>
 * The tests are executed on a remote host when using a Selenium Grid for example.
 * </p>
 *
 * @return <code>true</code> if the tests are executed on a remote host or <code>false</code> otherwise.
 */
public boolean isRemoteTestExecution() {
	return (this.remoteAddress != null);
}

/**
 * Tells whether the current browser is Safari or not.
 *
 * @return <code>true</code> if the current browser is Safari, <code>false</code>
 * otherwise.
 */
public abstract boolean isSafari();

/**
 * Maximize the browser window.
 */
public void maximize() {
	this.driver.manage().window().maximize();
}

/**
 * Move the mouse to the middle of the given element.
 * <p>
 * Note that for link this action trigger the rich hover.
 * </p><p>
 * <b>Warning</b>: It's strongly advised to use {@link BrowserElement#moveToElement(boolean)}
 * instead which is protected against {@link StaleElementReferenceException}
 * although this method is not...
 * </p>
 * @param element The web element to move to
 * @param entirelyVisible Ensure that the entire web element will be visible in
 * the browser window
 * @see Actions#moveToElement(WebElement)
 */
public void moveToElement(final BrowserElement element, final boolean entirelyVisible) {
	if (DEBUG) debugPrintln("		+ Move mouse to web element "+element);

	// Get element size
	Dimension size = element.getSize();

	// Add extra move if we want the entire element to be visible
	if (entirelyVisible) {
		// Put the mouse to the element's top-left corner
		try {
			this.actions.moveToElement(element.getWebElement(), -size.width/2, -size.height/2);
		}
		catch (MoveTargetOutOfBoundsException ex) {
			// skip
		}
		// Put the mouse to the element's bottom-right corner
		try {
			this.actions.moveToElement(element.getWebElement(), size.width/2, size.height/2);
		}
		catch (MoveTargetOutOfBoundsException ex) {
			// skip
		}
	}

	// Put the mouse into the middle of the element
    try {
	    this.actions.moveToElement(element.getWebElement()).build().perform();
    } catch (MoveTargetOutOfBoundsException mtoobe) {
    	if (!element.isInFrame()) {
	    	printException(mtoobe);
			println("		  	Warning: catching MoveTargetOutOfBoundsException while trying to move to an element, hence do nothing...");
    	}
		if (DEBUG) {
			debugPrintException(mtoobe);
			debugPrintln("		  -> catching MoveTargetOutOfBoundsException while trying to move to an element"+(element.isInFrame()?" (which was in a frame)":"")+", hence do nothing...");
		}
		return;
    }
}

/**
 * Moves the mouse to an offset from the top-left corner of the element.
 *
 * @param element The web element to move to
 * @param xOffset Offset from the top-left corner. A negative value means coordinates right from
 * the element.
 * @param yOffset Offset from the top-left corner. A negative value means coordinates above
 * the element.
 */
public void moveToElement(final BrowserElement element, final int xOffset, final int yOffset) {
	this.actions.moveToElement(element.getWebElement(), xOffset, yOffset).build().perform();
}

/**
 * Return whether or not to open a new browser session per each user.
 *
 * @return Whether or not to open a new browser session per each user as a <code>boolean</code>.
 */
public boolean newSessionPerUser() {
	return this.newSessionPerUser;
}

/**
 * Purge the given alert by accepting them before executing the given action.
 *
 * @return <code>true</code> if an alert was actually purged, <code>false</code>
 * if no alert was present.
 */
public boolean purgeAlert(final String action, final int count) {
	if (count > 10) {
		throw new ScenarioFailedError("Too many unexpected alerts, give up!");
	}

	// Get a handle to the open alert, prompt or confirmation
	Alert alert;
	try {
		alert = this.driver.switchTo().alert();
	}
	catch (NoAlertPresentException e) {
		return false;
	}

	// Get alert text
	String alertText;
	try {
		// Sometimes getText() on found alert can throw an ClassCassException.
		// Handle this exception if occurs and then accept alert even if getText() failed.
		alertText = alert.getText();
	}
	catch(Exception e) {
		alertText = "??? (unable to get alert text due to: "+e.getMessage()+")";
	}

	// Display the alert text
	println("Alert "+count+": "+alertText);
	println("	- action: "+action);
//	println("	- pause 1 second...");
//	sleep(1);

	// Acknowledge the alert (equivalent to clicking "OK")
	try {
		println("	- accept the alert...");
		alert.accept();
		println("done.");
	}
	catch(Exception e){
		println("	 get following exception while accepting the alert:");
		printException(e);
	}

	// Return that an alert was purged
	return true;
}

/**
 * Purge alerts by accepting them before executing the given action.
 *
 * @param action The action message
 */
public int purgeAlerts(final String action) {
	int n=1;
	while (purgeAlert(action, n)) {
		if (++n > 10) {
			throw new ScenarioFailedError("Too many unexpected alerts, give up!");
		}
	}
	return n-1;
}

/**
 * Refresh the current page content.
 */
public void refresh() {
	if (DEBUG) debugPrintln("		+ Refresh page "+this.location);
	this.driver.navigate().refresh();
}

/**
 * Reset the current browser embedded frame.
 * <p>
 * If the current browser is not an embedded frame (ie; {@link EmbeddedFrame})
 * then nothing is done on the current browser frame.
 * </p>
 */
public void resetEmbeddedFrame() {
    BrowserFrame currentFrame = getCurrentFrame();
	if (DEBUG) debugPrintln("		+ Reset embedded frame "+currentFrame);
	if (currentFrame instanceof EmbeddedFrame) {
		EmbeddedFrame embeddedFrame = (EmbeddedFrame) currentFrame;
		ElementFrame parentFrame = embeddedFrame.switchToParent();
		if (hasPopupWindow()) {
			this.framePopup = parentFrame;
		} else {
			this.frame = parentFrame;
		}
	}
}

/**
 * Reset the current browser frame.
 * <p>
 * After this operation no frame will be selected in the browser.
 * </p>
 */
public void resetFrame() {
	resetFrame(true/*store*/);
}

/**
 * Reset the current browser frame.
 * <p>
 * <b>WARNING</b>: When caller used a <code>false</code>value to not store
 * the reset, it's strongly recommended to call {@link #selectFrame()} after
 * in order to resynchronize the browser instance with its window.
 * </p>
 *
 * @param store Tells whether the reset frame should be stored in the browser or not.
 */
public void resetFrame(final boolean store) {
	if (DEBUG) debugPrintln("		+ Reset frame "+getCurrentFrame());
	try {
		this.driver.switchTo().defaultContent();
	}
	finally {
		if (store) {
			this.frame = null;
			this.framePopup = null;
		}
	}
}

/**
 * Right click on the given element at an offset from the top-left corner of the element.
 *
 * @param element element to right-click.
 * @param xOffset Offset from the top-left corner. A negative value means coordinates left from
 * the element.
 * @param yOffset Offset from the top-left corner. A negative value means coordinates above
 * the element.
 */
public BrowserElement rightClick(final BrowserElement element, final int xOffset, final int yOffset) {
	this.actions.moveToElement(element.getWebElement(), xOffset, yOffset);
	this.actions.contextClick();
	this.actions.build().perform();

	return element;
}

/**
 * Scroll the page to bottom.
 */
public void scrollPageBottom() {
	if (DEBUG) debugPrintln("		+ Scroll to bottom of page");
    executeScript("window.scrollTo(0,document.body.scrollHeight);");
}

/**
 * Scroll the page to the given element.
 * <p>
 * This is a no-op if the web element is already visible in the browser view.
 * </p>
 * @param element The web element to scroll the page to
 */
public void scrollPageTo(final BrowserElement element) {
	element.scrollIntoView();
}

/**
 * Scroll the page to top.
 */
public void scrollPageTop() {
	if (DEBUG) debugPrintln("		+ Scroll to top of page");
    executeScript("window.scrollTo(0,0);");
}

/**
 * Select items in elements list got from the given list element and the given
 * locator to find its children.
 * <p>
 * If  useControl is set to true, it holds the {@link Keys#CONTROL} key
 * to perform a multi-selection. Of course, that works only if the list web element
 * allow multiple selection.
 * </p><p>
 * If the expected entries are not found, {@link BrowserElement#MAX_RECOVERY_ATTEMPTS}
 * attempts are done before raising a {@link ScenarioFailedError}. Note that a sleep
 * of 2 seconds is done between each attempt.
 * </p>
 * @param listElement The element which children are the elements list to
 * consider for selection.
 * @param entriesBy The way to find the children
 * @param useControl should hold control while selecting multiple elements
 * @param patterns The patterns matching the items to select in the list, assuming that text matches
 * @return The array of the selected elements as {@link BrowserElement}.
 * @throws ScenarioFailedError if not all elements to select were found after
 * having retried {@link BrowserElement#MAX_RECOVERY_ATTEMPTS} times.
 */
public BrowserElement[] select(final BrowserElement listElement, final By entriesBy, final boolean useControl, final Pattern... patterns) {
	BrowserElement[] selectedElements = new BrowserElement[patterns.length];
	List<BrowserElement> entryElements = listElement.waitForElements(entriesBy);

	outer:
	for(int i = 0; i < patterns.length; i++) {
		for(BrowserElement entryElement : entryElements) {
			final String entryText = entryElement.getText();

			if (patterns[i].matcher(entryText).matches()) {
				// Check if the enter element is already selected.
				if(Boolean.parseBoolean(entryElement.getAttribute("selected"))) {
					// If reached here, it implies that the enter element is already selected.
					println("	  -> Entry '" + entryText + "' already selected in dropdown list. Therefore, no attempt was made to select it.");
					selectedElements[i] = null;
				}
				else {
					// If reached here, it implies that the enter element is unselected.
					// Therefore, select the entry element appropriately.
					if (useControl) {
						// Hold control while selecting
						Actions selectAction = new Actions(this.driver);
						selectAction = selectAction.keyDown(Keys.CONTROL);
						// Select element
						selectAction = selectAction.click(entryElement);
						// Release control
						selectAction = selectAction.keyUp(Keys.CONTROL);
						selectAction.build().perform();
					}
					else {
						entryElement.click();
					}
					selectedElements[i] = entryElement;
				}
				continue outer;
			}
		}
		throw new WaitElementTimeoutError("Item with pattern '" + patterns[i] + "' could not be found in dropdown list ");
	}

	return selectedElements;
}

///**
// * Select items in elements list got from the given list element and the given
// * locator to find its children.
// * <p>
// * If  useControl is set to true, it holds the {@link Keys#CONTROL} key
// * to perform a multi-selection. Of course, that works only if the list web element
// * allow multiple selection.
// * </p><p>
// * If the expected entries are not found, {@link WebBrowserElement#MAX_RECOVERY_ATTEMPTS}
// * attempts are done before raising a {@link ScenarioFailedError}. Note that a sleep
// * of 2 seconds is done between each attempt.
// * </p>
// * @param listElement The element which children are the elements list to
// * consider for selection.
// * @param entriesBy The way to find the children
// * @param useControl should hold control while selecting multiple elements
// * @param expected The items to select in the list, assuming that text matches
// * @return The array of the selected elements as {@link WebBrowserElement}.
// * @throws ScenarioFailedError if not all elements to select were found after
// * having retried {@link WebBrowserElement#MAX_RECOVERY_ATTEMPTS} times.
// */
//public WebBrowserElement[] select(final WebBrowserElement listElement, final By entriesBy, final boolean useControl, final StringComparisonCriterion comparisonCriterion, final String... expected) {
//	return select(listElement, entriesBy, useControl, new StringComparisonCriterion[]{comparisonCriterion}, expected);
//}
//
///**
// * Select items in elements list got from the given list element and the given
// * locator to find its children.
// * <p>
// * If  useControl is set to true, it holds the {@link Keys#CONTROL} key
// * to perform a multi-selection. Of course, that works only if the list web element
// * allow multiple selection.
// * </p><p>
// * If the expected entries are not found, {@link WebBrowserElement#MAX_RECOVERY_ATTEMPTS}
// * attempts are done before raising a {@link ScenarioFailedError}. Note that a sleep
// * of 2 seconds is done between each attempt.
// * </p>
// * @param listElement The element which children are the elements list to
// * consider for selection.
// * @param entriesBy The way to find the children
// * @param useControl should hold control while selecting multiple elements
// * @param comparisonCriteria A list of criteria to determine how to match an item in the
// * elements list to the expected/given option
// * @param expected The items to select in the list, assuming that text matches
// * @return The array of the selected elements as {@link WebBrowserElement}.
// * @throws ScenarioFailedError if not all elements to select were found after
// * having retried {@link WebBrowserElement#MAX_RECOVERY_ATTEMPTS} times.
// */
//public WebBrowserElement[] select(final WebBrowserElement listElement, final By entriesBy, final boolean useControl, final StringComparisonCriterion[] comparisonCriteria, final String... expected) {
//
//	// Init array to return
//	final int length = expected.length;
//	WebBrowserElement[] selectedElements = new WebBrowserElement[length];
//	int selected = 0;
//
//	// Select elements with possible recovery
//	String selectedOptions = "no option";
//	recoveryLoop: for (int recovery=1; recovery<=MAX_RECOVERY_ATTEMPTS; recovery++) {
//
//		// Get the elements list
//		List<WebElement> listElements = listElement.findElements(entriesBy, true/*displayed*/, false/*no recovery*/);
//		final int size = listElements.size();
//		if (size < length) {
//			debugPrintln("Workaround ("+recovery+"): only "+size+" items were found ("+length+" expected at least...), retry to get the elements...");
//			sleep(recovery);
//			continue;
//		}
//
//		// Look for requirements in the list
//		selected = 0;
//		Iterator<WebElement> optionsListIterator = listElements.iterator();
//		optionsLoop: while (optionsListIterator.hasNext()) {
//
//			// Get the option element text
//			WebBrowserElement optionElement = (WebBrowserElement) optionsListIterator.next();
//			String optionText = optionElement.getText();
//			if (optionText.isEmpty()) {
//				optionText = optionElement.getAttribute("aria-label");
//				if (optionText.isEmpty()) {
//					optionText = optionElement.getAttribute("value");
//					if (optionText.isEmpty()) {
//						throw new ScenarioFailedError("Cannot find text for option '"+optionElement+"' of selection '"+listElement+"'");
//					}
//				}
//			}
//
//			// First wait while the list content is loaded
//			int loadTimeout = 0;
//			while (optionText.equals("Loading...")) {
//				if (loadTimeout++ > 10) { // 10 seconds
//					debugPrintln("Workaround ("+recovery+"): list items are still loading after 10 seconds, retry to get the elements...");
//					continue recoveryLoop;
//				}
//				sleep(1);
//			}
//
//			// Check for the requested selections
//			for (int i=0; i<length; i++) {
//				// Compare a candidate to select by relying on the given list of
//				// comparison criteria
//				for (StringComparisonCriterion comparisonCriterion : comparisonCriteria) {
//					if (comparisonCriterion.compare(optionText, expected[i])) {
//						selectedElements[i] = optionElement;
//						if (++selected == length) {
//							if (useControl) {
//								try {
//									// Hold control while selecting
//									Actions selectAction = new Actions(this.driver);
//									selectAction = selectAction.keyDown(Keys.CONTROL);
//									// Select elements
//									for (WebBrowserElement element: selectedElements) {
//										selectAction = selectAction.click(element);
//									}
//									selectAction = selectAction.keyUp(Keys.CONTROL);
//									selectAction.build().perform();
//								}
//								catch (StaleElementReferenceException sere) {
//									continue recoveryLoop;
//								}
//							} else {
//								for (WebBrowserElement element: selectedElements) {
//									element.click();
//								}
//							}
//
//							// Return the selected elements
//							return selectedElements;
//						}
//						// A matching element has been found. Therefore, return to the
//						// optionsLoop to continue with searching for the remaining elements.
//						continue optionsLoop;
//					}
//                }
//			}
//		}
//
//		// We missed at least one item to select, try again
//		if (selected == 0) {
//			selectedOptions = "no option";
//		} else {
//			selectedOptions = "only "+selected+" option";
//			if (selected > 1) {
//				selectedOptions += "s";
//			}
//		}
//		final StringBuffer message = new StringBuffer("Workaround (")
//			.append(recovery)
//			.append("): ")
//			.append(selectedOptions)
//			.append(" selected although we expected ")
//			.append(length)
//			.append(", try again...");
//		debugPrintln(message.toString());
//		sleep(2);
//	}
//
//	// Fail as not all elements were selected
//	final StringBuffer message = new StringBuffer("After ")
//		.append(MAX_RECOVERY_ATTEMPTS)
//		.append(" attempts, there are still ")
//		.append(selectedOptions)
//		.append(" selected although we expected ")
//		.append(length)
//		.append(", give up");
//	throw new WaitElementTimeoutError(message.toString());
//}

/**
 * Select items in elements list got from the given list element and the given
 * locator to find its children.
 * <p>
 * All {@link StringComparisonCriterion} are used to determine how to match an item in the elements
 * list to the expected/given option
 * </p>
 * <p>
 * If  useControl is set to true, it holds the {@link Keys#CONTROL} key
 * to perform a multi-selection. Of course, that works only if the list web element
 * allow multiple selection.
 * </p><p>
 * If the expected entries are not found, {@link BrowserElement#MAX_RECOVERY_ATTEMPTS}
 * attempts are done before raising a {@link ScenarioFailedError}. Note that a sleep
 * of 2 seconds is done between each attempt.
 * </p>
 * @param listElement The element which children are the elements list to
 * consider for selection.
 * @param entriesBy The way to find the children
 * @param useControl should hold control while selecting multiple elements
 * @param items The items to select in the list, assuming that text matches
 * @return The array of the selected elements as {@link BrowserElement}.
 * @throws ScenarioFailedError if not all elements to select were found after
 * having retried {@link BrowserElement#MAX_RECOVERY_ATTEMPTS} times.
 */
public BrowserElement[] select(final BrowserElement listElement, final By entriesBy, final boolean useControl, final String... items) {
	Pattern[] patterns = new Pattern[items.length];

	for (int i = 0; i < items.length; i++) {
		patterns[i] = Pattern.compile(Pattern.quote(items[i]));
	}

	return select(listElement, entriesBy, useControl, patterns);
}

/**
 * Select items in elements list got from the given list element and the given
 * locator to find its children.
 * <p>
 * If the expected entries are not found, {@link BrowserElement#MAX_RECOVERY_ATTEMPTS}
 * attempts are done before raising a {@link ScenarioFailedError}. Note that a sleep
 * of 2 seconds is done between each attempt.
 * </p>
 * @param listElement The element which children are the elements list to
 * consider for selection.
 * @param entriesBy The way to find the children
 * @param patterns The patterns matching the items to select in the list, assuming that text matches
 * @return The array of the selected elements as {@link BrowserElement}.
 * @throws ScenarioFailedError if not all elements to select were found after
 * having retried {@link BrowserElement#MAX_RECOVERY_ATTEMPTS} times.
 */
public BrowserElement[] select(final BrowserElement listElement, final By entriesBy, final Pattern... patterns) {
	return select(listElement, entriesBy, patterns.length > 1, patterns);
}

/**
 * Select items in elements list got from the given list element and the given
 * locator to find its children.
 * <p>
 * If the expected entries are not found, {@link BrowserElement#MAX_RECOVERY_ATTEMPTS}
 * attempts are done before raising a {@link ScenarioFailedError}. Note that a sleep
 * of 2 seconds is done between each attempt.
 * </p>
 * @param listElement The element which children are the elements list to
 * consider for selection.
 * @param entriesBy The way to find the children
 * @param expected The items to select in the list, assuming that text matches
 * @return The array of the selected elements as {@link BrowserElement}.
 * @throws ScenarioFailedError if not all elements to select were found after
 * having retried {@link BrowserElement#MAX_RECOVERY_ATTEMPTS} times.
 */
public BrowserElement[] select(final BrowserElement listElement, final By entriesBy, final String... expected) {
	return select(listElement, entriesBy, expected.length > 1, expected);
}

/**
 * Set the current browser frame to the given web element.
 *
 * @param frameElement The frame element to select.
 */
public void selectEmbeddedFrame(final BrowserElement frameElement, final BrowserFrame browserFrame) {
	selectEmbeddedFrame(frameElement, browserFrame, true /*store*/);
}

/**
 * Set the current browser frame to the given web element.
 *
 * @param frameElement The frame element to select.
 */
public void selectEmbeddedFrame(final BrowserElement frameElement, final BrowserFrame browserFrame, final boolean store) {
	if (DEBUG) {
		debugPrintln("		+ Select embedded frame "+frameElement);
		debugPrintln("		  -> current frame: "+getCurrentFrame());
	}
	if (browserFrame == null) {
		selectFrame(frameElement, true/*force*/, store);
	} else {
		final EmbeddedFrame newFrame = new EmbeddedFrame(this, browserFrame, frameElement);
		newFrame.switchTo();
		if (store) {
			setCurrentFrame(newFrame);
		}
	}
}

/**
 * Select the current stored frame.
 * <p>
 * That can be necessary in case of desynchronization between the browser
 * instance and the real browser...
 * </p>
 */
public void selectFrame() {

	if (getCurrentFrame() == null) {
		if (DEBUG) debugPrintln("		+ Select no frame ");
		this.driver.switchTo().defaultContent();
	} else {
		if (DEBUG) debugPrintln("		+ Select current frame "+getCurrentFrame());
		getCurrentFrame().switchTo();
	}
}

/**
 * Set the current browser frame to a given web element.
 *
 * @param locator Locator to find the frame element in the current page.
 * @param timeout The time in seconds to wait before giving up the research.
 */
public void selectFrame(final By locator, final int timeout) {
	if (DEBUG) {
		debugPrintln("		+ select frame " + locator);
		debugPrintln("		  -> current frame: "+getCurrentFrame());
	}
	this.driver.switchTo().frame(waitForElement(locator, timeout).getWebElement());
}

/**
 * Set the current browser frame to the given index.
 *
 * @param index The index of the frame to select.
 */
public void selectFrame(final int index) {
	if (DEBUG) {
		debugPrintln("		+ select frame index to "+index);
		debugPrintln("		  -> current frame: "+getCurrentFrame());
	}
	try {
		if (index < 0) {
			resetFrame();
		} else {
			final IndexedFrame newFrame = new IndexedFrame(this, index);
			newFrame.switchTo();
			setCurrentFrame(newFrame);
		}
	}
	catch (NoSuchFrameException nsfe) {
		// Workaround
		debugPrintln("Workaround: cannot select given frame index "+index+", hence get back to the previous one: "+getCurrentFrame());
		selectFrame();
	}
}

/**
 * Set the current browser frame to the given name.
 * <p>
 * Note that nothing happen if the given frame is null. To reset the frame,
 * caller has to use the explicit method {@link #resetFrame()}.
 * </p>
 * @param frameName The name of the frame to select. That can be either a real
 * name for the frame or an index value.
 * @param force Force the frame selection. If not set and the browser frame
 * is the same than the given one, then nothing is done.
 * @throws NoSuchFrameException If the given frame name or index does not
 * exist in the web page content.
 */
public void selectFrame(final String frameName, final boolean force) {
	if (DEBUG) {
		debugPrintln("		+ Select frame to '"+frameName+"' (force="+force+")");
		debugPrintln("		  -> current frame:"+getCurrentFrame());
	}
	if (frameName != null || force) {
		try {
			try {
				int frameIndex = Integer.parseInt(frameName);
				if ((getCurrentFrame().getIndex() == frameIndex) && !force) {
					if (DEBUG) debugPrintln("		  -> nothing to do.");
				} else {
					final IndexedFrame newFrame = new IndexedFrame(this, frameIndex);
					newFrame.switchTo();
					setCurrentFrame(newFrame);
				}
			}
			catch (NumberFormatException nfe) {
				if (getCurrentFrame() != null && getCurrentFrame().getName().equals(frameName) && !force) {
					if (DEBUG) debugPrintln("		  -> nothing to do");
				} else {
					final NamedFrame newFrame = new NamedFrame(this, frameName);
					newFrame.switchTo();
					setCurrentFrame(newFrame);
				}
			}
		}
		catch (NoSuchFrameException nsfe) {
			// Workaround
			debugPrintln("Workaround: cannot select given frame name '"+frameName+"', hence get back to the previous one: "+getCurrentFrame());
			selectFrame();
		}
	}
}

/**
 * Set the current browser frame to the given web element.
 *
 * @param frameElement The frame element to select.
 * @see org.openqa.selenium.WebDriver.TargetLocator#frame(WebElement)
 */
public void selectFrame(final BrowserElement frameElement) {
	selectFrame(frameElement, true);
}

/**
 * Set the current browser frame to the given web element.
 *
 * @param frameElement The frame element to select.
 * @param force Tells whether the frame should be set even if it's already the
 * current browser one
 * @see org.openqa.selenium.WebDriver.TargetLocator#frame(WebElement)
 */
public void selectFrame(final BrowserElement frameElement, final boolean force) {
	selectFrame(frameElement, force, true/*store*/);
}

/**
 * Set the current browser frame to the given web element.
 *
 * @param frameElement The frame element to select.
 * @param force Tells whether the frame should be set even if it's already the
 * current browser one
 * @param store Tells whether the new frame should be store in the browser or not.
 * Setting this argument to <code>false</code>  should be done cautiously as that
 * will imply a desynchronization between the browser instance and the window.
 * @see org.openqa.selenium.WebDriver.TargetLocator#frame(WebElement)
 */
public void selectFrame(final BrowserElement frameElement, final boolean force, final boolean store) {
	if (DEBUG) {
		debugPrintln("		+ select frame "+frameElement);
		debugPrintln("		  -> current frame: "+getCurrentFrame());
	}
	try {
		if (frameElement == null) {
			if (getCurrentFrame() != null || force) {
				resetFrame();
			}
		} else if (force || getCurrentFrame() == null || !getCurrentFrame().getElement().equals(frameElement)) {
			final ElementFrame newFrame = new ElementFrame(this, frameElement);
			newFrame.switchTo();
			if (store) {
				setCurrentFrame(newFrame);
			}
		}
	}
	catch (NoSuchFrameException nsfe) {
		// Workaround
		debugPrintln("Workaround: cannot select given frame elment "+frameElement+", hence get back to the previous one: "+getCurrentFrame());
		selectFrame();
	}
}

/**
 * Set the current browser frame to a given web element.
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the frame element in the current page.
 * @param timeout The time in seconds to wait before giving up the research.
 */
public void selectFrame(final BrowserElement parentElement, final By locator, final int timeout) {
	if (DEBUG) {
		debugPrintln("		+ select frame " + locator);
		debugPrintln("		  -> current frame: "+getCurrentFrame());
	}
	this.driver.switchTo().frame(waitForElement(parentElement, locator, true /*fail*/, timeout).getWebElement());
}

public void selectFrame(final BrowserFrame webFrame) {
	selectFrame(webFrame, true /*store*/);
}

public void selectFrame(final BrowserFrame webFrame, final boolean store) {
	if (DEBUG) debugPrintln("		+ Select frame "+webFrame);
	debugPrintln("		  -> current frame:"+getCurrentFrame());
	try {
		if (webFrame == null) {
			this.driver.switchTo().defaultContent();
		} else {
			webFrame.switchTo();
		}
		if (store) {
			setCurrentFrame(webFrame);
		}
	}
	catch (NoSuchFrameException nsfe) {
		// Workaround
		debugPrintln("Workaround: cannot select given frame name '"+webFrame+"', hence get back to the previous one: "+getCurrentFrame());
		selectFrame();
	}
}

/**
 * Select a number of elements by clicking on each while holding down the shift key.
 *
 * @param elements elements to be selected.
 */
public void selectMultipleElements(final BrowserElement... elements) {
	this.actions.keyDown(Keys.SHIFT);

	for (BrowserElement element : elements) {
		this.actions.click(element.getWebElement());
	}

	this.actions.keyUp(Keys.SHIFT).build().perform();
}

/**
 * Select the current visible browser frame.
 * <p>
 * Do nothing if the browser has no visible frame.
 * </p>
 * @param timeout Timeout to find the visible frame
 * @return <code>true</code> if there was a visible frame in the browser and
 * becomes the current one, <code>false</code> otherwise.
 * @throws ScenarioFailedError If no visible frame has been found.
 */
public boolean selectVisibleFrame(final int timeout) {
	if (DEBUG) {
		debugPrintln("		+ Select visible frame");
		debugPrintln("		  -> current frame: "+getCurrentFrame());
	}

	try {
		// Check whether there's already a visible frame selected
		BrowserFrame currentFrame = getCurrentFrame();
		if (currentFrame instanceof ElementFrame) {
			if (((ElementFrame) currentFrame).isDisplayed()) {
				currentFrame.switchTo();
				return true;
			}
		}

		// Get visible frame
		BrowserElement frameElement = getVisibleFrame(0);
		ElementFrame visibleFrame = new ElementFrame(this, frameElement);

		// Switch to the visible frame
		visibleFrame.switchTo();
		setCurrentFrame(visibleFrame);
	}
	catch (ScenarioFailedError sfe) {
		// If error occurs when getting visible, that means there no visible frame
		if (DEBUG) debugPrintln("		  -> no frame visible in browser");
		return false;
	}

	return true;
}

/**
 * Type the given text to the active element.
 * <p>
 * As the element isn't refocused after, you may use this
 * method to TAB between elements
 * </p>
 * @param sequence the key sequence to execute
 * @see Actions#sendKeys(CharSequence...)
 */
public void sendKeys(final CharSequence... sequence) {
	if (DEBUG) debugPrintln("		+ Send keys sequence '"+sequence+"' to current browser element.");
	this.actions.sendKeys(sequence).build().perform();
}

/**
 * Set current browser frame with the given one.
 * <p>
 * Note that current frame might be in the popup window if any is opened.
 * </p>
 * @param newFrame The new frame to store
 */
void setCurrentFrame(final BrowserFrame newFrame) {
	if (hasPopupWindow()) {
		this.framePopup = newFrame;
	} else {
		this.frame = newFrame;
	}
}

/**
 * Set the browser window size.
 *
 * @param width The new browser window width. Should be a value between
 * {@link #MIN_WIDTH} and {@link #MAX_WIDTH}, otherwise it will be ignored.
 * @param height The new browser window height. Should be a value between
 * {@link #MIN_HEIGHT} and {@link #MAX_HEIGHT}, otherwise it will be ignored.
 */
public void setWindowSize(final int width, final int height) {

	// Check width argument
	int newWidth = width;
	if (newWidth < MIN_WIDTH || newWidth > MAX_WIDTH) {
		println("Specified window width: "+newWidth+" is not valid to run a CLM scenario, expected it between "+MIN_WIDTH+" and "+MAX_WIDTH+".");
		println("	=> this argument will be ignored and "+DEFAULT_WIDTH+" width used instead.");
		newWidth = DEFAULT_WIDTH;
	}

	// Check height argument
	int newHeight = height;
	if (newHeight < MIN_HEIGHT || newHeight > MAX_HEIGHT) {
		println("Specified window height: "+newHeight+" is not valid to run a CLM scenario, expected it between "+MIN_HEIGHT+" and "+MAX_HEIGHT+".");
		println("	=> this argument will be ignored and "+DEFAULT_HEIGHT+" height used instead.");
		newHeight = DEFAULT_HEIGHT;
	}

	// Resize window if necessary
	Window window = this.driver.manage().window();
	Dimension dimension = window.getSize();
	if (dimension.getWidth() < newWidth || dimension.getHeight() < newHeight) {
		int windowWidth = dimension.getWidth() < newWidth ? newWidth : dimension.getWidth();
		int windowHeight= dimension.getHeight() < newHeight ? newHeight : dimension.getHeight();
		window.setSize(new Dimension(windowWidth, windowHeight));
	}
}

/**
 * Perform shift click to do a range selection.
 *
 * @param destination The last WebBrowseElment of a range selection selected by performing a shift click
 */
public void shiftClick(final BrowserElement destination) {
	this.actions.keyDown(Keys.SHIFT)
	    .moveToElement(destination.getWebElement())
	    .click()
	    .keyUp(Keys.SHIFT)
	    .build()
	    .perform();
}

/**
 * Selects either the first frame on the page, or the main document when a page contains iframes.
 */
public void switchToMainWindow() {
	if (DEBUG) debugPrintln("		+ Switch to main window using stored handle " + this.mainWindowHandle);
	this.driver.switchTo().defaultContent();
}

/**
 * Switch to a new opened window.
 *
 * @param close Tells whether previous window should be closed
 * @throws NoSuchWindowException When popup is transient and closed before
 * being able to switch to it.
 */
public void switchToNewWindow(final boolean close) throws NoSuchWindowException {
	if (DEBUG) debugPrintln("		+ Switch to popup window");

	// Check that a popup exist
	long timeout = 10000 + System.currentTimeMillis(); // Timeout 10 seconds
	while (!hasPopupWindow()) {
		if (System.currentTimeMillis() > timeout) {
			throw new NoSuchWindowException("Popup window never comes up.");
		}
	}

	// Get new window handle
	if (DEBUG) debugPrintln("		  -> main window handle "+this.mainWindowHandle);
	Iterator<String> handles = getWindowHandles().iterator();
	String newWindowHandle = null;
	while (handles.hasNext()) {
		String handle = handles.next();
		if (handle.equals(this.mainWindowHandle)) {
			if (!handle.equals(this.driver.getWindowHandle())) {
				throw new ScenarioFailedError("Unexpected driver handle: "+this.driver.getWindowHandle()+", it should have been "+handle);
			}
		} else {
			newWindowHandle = handle;
		}
	}

	// Close previous window if requested
	if (close) {
		this.driver.close();
	}

	// Switch to the new window
	if (DEBUG) debugPrintln("		  -> switch to window handle "+newWindowHandle);
	this.driver.switchTo().window(newWindowHandle);
	this.mainWindowHandle = newWindowHandle;

	// Accept certificate
	if (isInternetExplorer()) {
		acceptInternetExplorerCertificate();
	}
}

/**
 * Change focus to the parent context.
 * <p>
 * If the current context is the top level browsing context, the context remains unchanged.
 * </p>
 */
public void switchToParentFrame() {
	this.driver.switchTo().parentFrame();
}

///**
// * Switch to popup window.
// *
// * @throws NoSuchWindowException When popup is transient and closed before
// * being able to switch to it.
// */
//public void switchToPopupWindow() throws NoSuchWindowException {
//	if (DEBUG) debugPrintln("		+ Switch to popup window");
//
//	// Check that a popup exist
//	long timeout = 10000 + System.currentTimeMillis(); // Timeout 10 seconds
//	while (!hasPopupWindow()) {
//		if (System.currentTimeMillis() > timeout) {
//			throw new NoSuchWindowException("Popup window never comes up.");
//		}
//	}
//
//	// Switch to the popup window
//	if (DEBUG) debugPrintln("		  -> main window handle "+this.mainWindowHandle);
//	Iterator<String> iterator = getWindowHandles().iterator();
//	while (iterator.hasNext()) {
//		String handle = iterator.next();
//		if (!handle.equals(this.mainWindowHandle) && !handle.equals(this.driver.getWindowHandle())) {
//			if (DEBUG) debugPrintln("		  -> switch to window handle "+handle);
//			this.driver.switchTo().window(handle);
//			break;
//		}
//	}
//
//	// Accept certificate
//	if (isInternetExplorer()) {
//		acceptInternetExplorerCertificate();
//	}
//}

/**
 * Switch to a given window.
 *
 * @param handle The window handle to switch to as {@link String}.
 * @param close Tells whether previous windows should be closed
 *
 * @throws NoSuchWindowException When the given window is transient and closed before
 * being able to switch to it.
 */
public void switchToWindow(final String handle, final boolean close) throws NoSuchWindowException {
	if (DEBUG) debugPrintln("		+ Switch to popup window '" + handle + "'");

	// Check that the given window exists.
	long timeout = 10 * 1000 + System.currentTimeMillis(); // Timeout 10 seconds
	while (!getWindowHandles().contains(handle)) {
		if (System.currentTimeMillis() > timeout) {
			throw new NoSuchWindowException("A window with handle '" + handle + "' could not be found before timeout 10s.");
		}
	}

	// Close previous windows if requested.
	if (close) {
		Iterator<String> handles = getWindowHandles().iterator();
		while (handles.hasNext()) {
			String aHandle = handles.next();

			if(!aHandle.equals(handle)) {
				println("	  -> Closing windows with handle '"+ aHandle +"'");
				this.driver.switchTo().window(aHandle);
				this.driver.close();
			}
		}
	}

	// Switch to the new window.
	if (DEBUG) debugPrintln("		  -> switch to window handle " + handle);
	this.driver.switchTo().window(handle);
	this.mainWindowHandle = handle;

	// Accept certificate.
	if (isInternetExplorer()) {
		acceptInternetExplorerCertificate();
	}

	// Set the browser window to the desired size.
	initWindow();
}

/**
 * Takes a snapshot of the given kind.
 *
 * @param fileName The name of the snapshot.
 * @param kind Snapshot kind, can be:
 * <ul>
 * <li>0: Snapshot for information (default)</li>
 * <li>1: Snapshot for warnings</li>
 * <li>2: Snapshot for failures</li>
 * <ul>
 */
private void takeSnapshot(final String fileName, final int kind) {

	// Get snapshot dir
	if (this.snapshotsRootDir == null) return;
	File currentSnapshotsDir;
	String destFilePrefix = EMPTY_STRING;
	if (this.flatSnapshotsDir == null) {
		if (this.snapshotsDir == null) {
			this.snapshotsDir =  new File[3];
			this.snapshotsDir[FAILURE_SNAPSHOT] = FileUtil.createDir(this.snapshotsRootDir, "failures");
			this.snapshotsDir[WARNING_SNAPSHOT] = FileUtil.createDir(this.snapshotsRootDir, "warnings");
			this.snapshotsDir[INFO_SNAPSHOT] = FileUtil.createDir(this.snapshotsRootDir, "infos");
		}
		currentSnapshotsDir = this.snapshotsDir[kind];
	} else {
		currentSnapshotsDir = this.flatSnapshotsDir;
		switch (kind) {
			case FAILURE_SNAPSHOT:
				destFilePrefix = "failures_";
				break;
			case WARNING_SNAPSHOT:
				destFilePrefix = "warnings_";
				break;
			case INFO_SNAPSHOT:
				destFilePrefix = "infos_";
				break;
			default:
				throw new IllegalArgumentException("Unexpected value for snapshot kind: "+kind);
		}
	}

	// Get destination file name
	String destFileName = destFilePrefix + COMPACT_DATE_STRING + "_" + fileName + ".png";
	File file = new File(currentSnapshotsDir, destFileName);
	if (file.exists()) {
		int idx = 1;
		while (file.exists()) {
			destFileName = COMPACT_DATE_STRING + "_" + fileName + (idx < 10 ? "_0" : "_") + idx + ".png";
			file = new File(currentSnapshotsDir, destFileName);
			idx++;
		}
	}

	try {
		// Take snapshot
   	    File snapshotFile = ((TakesScreenshot)this.driver).getScreenshotAs(OutputType.FILE);
	    try {
	        File destFile = FileUtil.copyFile(snapshotFile, currentSnapshotsDir, destFileName);
		    println("		  -> screenshot available at " + destFile.getAbsolutePath());
        } catch (IOException e) {
	        printException(e);
		    println("		  -> cannot copy "+snapshotFile.getAbsolutePath()+" to "+currentSnapshotsDir+File.separator+destFileName+"!!!");
        }
	} catch (WebDriverException wde) {
		// Catch if any Selenium exception occurs but which should not prevent
		// BVT test to succeed at this stage...
		wde.printStackTrace();
    }
}

/**
 * Takes a snapshot and return it as a bytes array.
 *
 * @return The snapshot bytes array.
 */
public byte[] takeSnapshotBytes() {
	 return ((TakesScreenshot)this.driver).getScreenshotAs(OutputType.BYTES);
}

/**
 * Takes a failure snapshot.
 *
 * @param fileName The name of the snapshot file.
 */
public void takeSnapshotFailure(final String fileName) {
	takeSnapshot(fileName, FAILURE_SNAPSHOT);
}

/**
 * Takes an information snapshot.
 *
 * @param fileName The name of the snapshot file.
 */
public void takeSnapshotInfo(final String fileName) {
	takeSnapshot(fileName, INFO_SNAPSHOT);
}

/**
 * Takes a warning snapshot.
 *
 * @param fileName The name of the snapshot file.
 */
public void takeSnapshotWarning(final String fileName) {
	takeSnapshot(fileName, WARNING_SNAPSHOT);
}

@Override
public String toString() {
	StringBuilder builder = new StringBuilder(this.name+" browser");
	if (this.path != null) builder.append(", path=").append(this.path);
	if (this.driverPath != null) builder.append(", driver=").append(this.driverPath);
	if (this.location != null) {
		builder.append(", url=").append(this.url);
		if (this.url == null  || !this.url.equals(this.location)) {
			builder.append(", location=").append(this.location);
		}
		builder.append(" (").append(getCurrentFrame()).append(')');
	}
	return builder.toString();
}

/**
 * Type a text in an input element.
 * <p>
 * Note that to raise the corresponding javascript even, an additional {@link Keys#TAB}
 * is hit after having entered the text.<br>
 * </p>
 * @param element The input field.
 * @param timeout The timeout before giving up if the text is not enabled
 * @param user User whom password has to be typed
 * @throws ScenarioFailedError if the input is not enabled before the timeout
 * @since 6.0
 */
public void typePassword(final BrowserElement element, final int timeout, final IUser user) {
	typeText(element, null, Keys.TAB, 100/*delay works for most of the cases*/, true/*clear*/, timeout, user);
}

/**
 * Type a text in an input element.
 * <p>
 * Note that to raise the corresponding javascript even, an additional {@link Keys#TAB}
 * is hit after having entered the text.<br>
 * </p>
 * @param element The input field.
 * @param text The text to type
 * @param key The key to hit after having entered the text in the input field
 * @param clear Tells whether the input field needs to be cleared before putting
 * the text in.
 * @param timeout The timeout before giving up if the text is not enabled
 * @throws ScenarioFailedError if the input is not enabled before the timeout
 */
public void typeText(final BrowserElement element, final String text, final Keys key, final boolean clear, final int timeout) {
	typeText(element, text, key, 100/*delay works for most of the cases*/, clear, timeout);
}

/**
 * Type a text in an input element.
 * <p>
 * Note that to raise the corresponding javascript even, an additional {@link Keys#TAB}
 * is hit after having entered the text.<br>
 * </p>
 * @param element The input field.
 * @param text The text to type.
 * @param key The key to hit after having entered the text in the input field.
 * @param keyDelay Defines the waiting time before the key parameter send to the input element.
 * @param clear Tells whether the input field needs to be cleared before putting.
 * the text in.
 * @param timeout The timeout before giving up if the text is not enabled.
 * @throws ScenarioFailedError if the input is not enabled before the timeout.
 */
public void typeText(final BrowserElement element, final String text, final Keys key, final int keyDelay, final boolean clear, final int timeout) {
	typeText(element, text, key, keyDelay, clear, timeout, null/*user*/);
}

private void typeText(final BrowserElement element, final String text, final Keys key, final int keyDelay, final boolean clear, final int timeout, final IUser user) {
	String printedText;
	if (user == null) {
		printedText = "text '"+text+"'";
	} else {
		printedText = "password '******'";
	}
	if (DEBUG) debugPrintln("		+ Type "+printedText+(clear?" (cleared)":EMPTY_STRING));

	// Check argument
	if (text == null && user == null) {
		throw new ScenarioFailedError("Invalid null arguments while type text in input web element.");
	}

	// Wait until the text is enabled.
	element.waitWhileDisabled(timeout, true /*fail*/);

	// Move to element that will help to trigger javascript events
	element.scrollIntoView();

	// Type text
	if (clear) element.clear();
	if (user == null) {
		element.sendKeys(text);
	} else {
		element.enterPassword(user);
	}

	// Hit the key
	if (key != null) {
		pause(keyDelay); // short pause for elements such as 'FilterSelect'
		element.sendKeys(key);
	}
}


/**
 * Wait until have found the element using given locator.
 * <p>
 * Note that:
 * <ul>
 * <li>the search occurs in the entire page or in the current frame if there's
 * one selected in the browser (see {@link #hasFrame()})</li>
 * <li>hidden element will be ignored</li>
 * <li>it will fail if:
 * <ol>
 * <li>the element is not found before timeout seconds</li>
 * <li>there's more than one element found</li>
 * </ol></li>
 * </ul>
 * </p>
 * </p>
 * @param locator Locator to find the element in the current page.
 * @param timeout The time to wait before giving up the research
 * @return The web element as {@link BrowserElement}
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail
 * @throws MultipleVisibleElementsError if several elements are found and only single one was expected
 */
public BrowserElement waitForElement(final By locator, final int timeout) {
	return waitForElement(null, locator, true/*fail*/, timeout, true/*displayed*/, true/*single*/);
}

/**
 * Wait until have found the element using given locator.
 * <p>
 * Only fail if specified and after having waited the given timeout.
 * </p>
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param timeout The time to wait before giving up the research
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail
 * @throws MultipleVisibleElementsError if several elements are found and only single one was expected
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By locator, final boolean fail, final int timeout) {
	return waitForElement(parentElement, locator, fail, timeout, true/*displayed*/, true/*single*/);
}

/**
 * Wait until have found the element using given locator.
 * <p>
 * Only fail if specified and after having waited the given timeout.
 * </p>
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param timeout The time to wait before giving up the research
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @param single Tells whether a single element is expected
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail
 * @throws MultipleVisibleElementsError if several elements are found and only single one was expected
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By locator, final boolean fail, final int timeout, final boolean displayed, final boolean single) {
	if (DEBUG) {
		debugPrint("		+ waiting for element: [");
		if (parentElement != null) debugPrint(parentElement.getFullPath()+"]//[");
		debugPrint(locator+"]");
		debugPrintln(" (fail="+fail+", timeout="+timeout+", displayed="+displayed+", single="+single+")");
	}

	// Wait for all elements
	List<BrowserElement> foundElements = waitForElements(parentElement, locator, fail, timeout, displayed);
	if (foundElements == null) return null;
	int size = foundElements.size();
	if (size == 0) return null;
	if (!PERFORMANCE_ENABLED&&size > 1) {
		if (single) {
			throw new MultipleVisibleElementsError(foundElements);
		}
		debugPrintln("WARNING: found more than one elements ("+size+"), return the first one!");
	}

	// Return the found element
	return foundElements.get(0);
}

/**
 * Wait until have found one of element using the given search locators.
 * <p>
 * Fail only if specified and after having waited the given timeout.
 * </p>
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locators Search locators of the expected elements.
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param timeout The time to wait before giving up the research
 * @return The web element as {@link BrowserElement} or <code>null</code>
 * if no element was found before the timeout and asked not to fail
 * @throws WaitElementTimeoutError if no element was found before the timeout and asked to fail
 */
public BrowserElement waitForElement(final BrowserElement parentElement, final By[] locators, final boolean fail, final int timeout) {
	if (DEBUG) {
		debugPrintln("		+ waiting until finding one of following elements: ");
		if (parentElement != null) debugPrintln("		  - parent: "+parentElement.getFullPath());
		debugPrintln("		  - elements: ");
		for (By locator: locators) {
			debugPrintln("			* "+locator);
		}
	}

	// Wait for first found element
	BrowserElement[] multipleElements = waitForMultipleElements(parentElement, locators, fail, timeout);
	if (multipleElements != null) {
		for (BrowserElement foundElement: multipleElements) {
			if (foundElement != null) return foundElement;
		}
	}

	// No found element
	if (fail) {
		throw new WaitElementTimeoutError("Cannot find any of the researched elements.");
	}
	return null;
}

/**
 * Wait until have found one or several elements using given locator.
 * <p>
 * Only fail if specified and after having waited the given timeout.
 * </p>
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param timeout The time to wait before giving up the research
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @return A {@link List} of web element as {@link BrowserElement}. Might
 * be empty if no element was found before the timeout and asked not to fail
 * @throws WaitElementTimeoutError if no element was found before the timeout and
 * asked to fail
 */
public List<BrowserElement> waitForElements(final BrowserElement parentElement, final By locator, final boolean fail, final int timeout, final boolean displayed) {
	if (DEBUG) {
		debugPrint("		+ waiting for elements: [");
		if (parentElement != null) debugPrint(parentElement.getFullPath()+"]//[");
		debugPrintln(locator+"]");
	}

	long timeoutMillis = timeout * 1000 + System.currentTimeMillis();

	// Timeout Loop until timeout is reached
	while (System.currentTimeMillis() <= timeoutMillis) {

		// Find web driver elements or parent web element children elements
		List<WebElement> foundElements = parentElement == null
			? findElements(locator, displayed, true/*recovery*/)
			: parentElement.findElements(locator, displayed, true/*recovery*/);

		// Seek found elements to split visible and hidden ones
		int size = foundElements.size();
		List<BrowserElement> hiddenElements = new ArrayList<BrowserElement>(size);
		List<BrowserElement> visibleElements = new ArrayList<BrowserElement>(size);
		for (WebElement foundElement: foundElements) {

			// List element is a framework web element
			BrowserElement foundBrowserElement = (BrowserElement) foundElement;

			// Split visible and hidden elements
			if (!displayed) {
				visibleElements.add(foundBrowserElement);
				if (DEBUG) debugPrintln("		  -> found element: "+foundBrowserElement);
			}
			else if (foundBrowserElement.isDisplayed(false)) {
				visibleElements.add(foundBrowserElement);
				if (DEBUG) debugPrintln("		  -> found element: "+foundBrowserElement+" (visible)");
			} else {
				hiddenElements.add(foundBrowserElement);
				if (DEBUG) debugPrintln("		  -> found element: "+foundBrowserElement+" (hidden)");
			}
		}

		// Return visible elements if any
		int visibleSize = visibleElements.size();
		if (visibleSize > 0) {
			if (DEBUG) debugPrintln("		  -> return "+visibleSize+(displayed ? " visible" : " ")+" elements");
			return visibleElements;
		}

		// Return hidden elements if any and allowed
		int hiddenSize = hiddenElements.size();
		if (hiddenSize > 0 && !displayed) {
			if (DEBUG) debugPrintln("		  -> return "+hiddenSize+" hidden elements");
			return hiddenElements;
		}
	}

	// Fail as we can only reach this point if expected elements were not found in time
	if (DEBUG) debugPrintln("		  -> no element was found");
	if (fail) {
		StringBuilder builder = new StringBuilder("Timeout while waiting for '");
		builder.append(locator);
		builder.append("'. Took longer than '");
		builder.append(timeout);
		builder.append("' seconds.");

		throw new WaitElementTimeoutError(builder.toString());
	}
	return NO_BROWSER_ELEMENT_FOUND;
}

/**
 * Wait until at least one element is found using each of the given locator.
 * <p>
 * That method stores each found element using the given locators in the
 * the returned array, hence it may have more than one non-null slot.
 * </p><p>
 * Note that the method stop to search as soon as at least one element is found.
 * Hence, when several elements are found and returned in the array, that means
 * they have been found in the same loop. The timeout is only reached when
 * <b>no</b> element is found...
 * </p><p>
 * Note also that only displayed elements are returned.
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param locators List of locators to use to find the elements in the current page.
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param timeout The time to wait before giving up the research
 * @return An array with one non-null slot per element found before timeout
 * occurs or <code>null</code> if none was found and it has been asked not to fail.
 * @throws WaitElementTimeoutError if no element is found before the timeout occurs
 * and it has been asked to fail.
 */
public BrowserElement[] waitForMultipleElements(final BrowserElement parentElement, final By[] locators, final boolean fail, final int timeout) {
	return waitForMultipleElements(parentElement, locators, null/*displayed*/, fail, timeout);
}

/**
 * Wait until at least one element is found using each of the given locator.
 * <p>
 * That method stores each found element using the given locators in the
 * the returned array, hence it may have more than one non-null slot.
 * </p><p>
 * Note that the method stop to search as soon as at least one element is found.
 * Hence, when several elements are found and returned in the array, that means
 * they have been found in the same loop. The timeout is only reached when
 * <b>no</b> element is found...
 * </p><p>
 * Note also that only displayed elements are returned.
 * </p>
 * @param parentElement The parent element where to start to search from,
 * if <code>null</code>, then search in the entire page content
 * @param locators List of locators to use to find the elements in the current page.
 * @param displayFlags List of flag telling whether the corresponding element should
 * be displayed or not. If <code>null</code>, then it's assumed that all elements
 * have to be displayed.
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param timeout The time to wait before giving up the research
 * @return An array with one non-null slot per element found before timeout
 * occurs or <code>null</code> if none was found and it has been asked not to fail.
 * @throws WaitElementTimeoutError if no element is found before the timeout occurs
 * and it has been asked to fail.
 */
public BrowserElement[] waitForMultipleElements(final BrowserElement parentElement, final By[] locators, final boolean[] displayFlags, final boolean fail, final int timeout) {
	// Check arrays length
	if (displayFlags != null && displayFlags.length != locators.length) {
		throw new RuntimeException("Invalid lengthes of arrays: "+locators.length+" xpaths and "+displayFlags.length+" displayed flags.");
	}
	StringBuilder locatorBuilder = new StringBuilder();
	if (DEBUG) {
		debugPrint("		+ waiting for multiple elements (fail="+fail+", timeout="+timeout+"): ");
		String sep = "";
		int i=0;
		for (By locator: locators) {
			locatorBuilder.append(sep+"'"+locator+"'");
			if (displayFlags != null) {
				boolean displayed = displayFlags[i++];
				locatorBuilder.append(" ("+(displayed?"displayed":"hidden")+")");
			}
			sep = ", ";
		}
		debugPrintln(locatorBuilder.toString());
	}

	// Init
	int length = locators.length;
	BrowserElement[] foundElements = new BrowserElement[length];
	long timeoutMillis = timeout * 1000 + System.currentTimeMillis();

	// Timeout Loop until timeout is reached
	while (System.currentTimeMillis() <= timeoutMillis) {
		boolean found = false;

		// For each specified find locator
		for (int i=0; i<length; i++) {

			// Get displayed flag
			boolean displayed = displayFlags == null ? true : displayFlags[i];

			// Find the framework web elements
			List<WebElement> findElements = parentElement == null
				? findElements(locators[i], displayed, fail/*recovery*/)
				: parentElement.findElements(locators[i], displayed, fail/*recovery*/);

			// Put the found element in the return array
			for (WebElement findElement: findElements) {
				if (DEBUG)  debugPrintln("		  -> found '"+locators[i]+"'");
				foundElements[i] = (BrowserElement) findElement;
				found = true;
				break;
			}
		}

		// Leave as soon as one of the element is found
		if (found) {
			return foundElements;
		}
	}

	// No elements were not found in allowed time, fail or return null
	if (DEBUG) debugPrintln("		  -> no elements were found");
	if (fail) {
		StringBuilder errorBuilder = new StringBuilder("Timeout while waiting for multiple elements: ");
		errorBuilder.append(locatorBuilder);
		errorBuilder.append(". Took longer than '");
		errorBuilder.append(timeout);
		errorBuilder.append("' seconds.");
		throw new WaitElementTimeoutError(errorBuilder.toString());
	}
	return null;
}

/**
 * Wait for a popup window to be opened and/or closed.
 * <p>
 * The possible state to wait for are:
 * <ul>
 * <li>0: Wait for the popup window to be opened.</li>
 * <li>1: Wait for the popup window to be closed.</li>
 * <li>2: Wait for the popup window to be opened, then closed.</li>
 * </ul>
 * </p>
 * @param state The expected state for the popup window, see above for the
 * valid values.
 * @param seconds Timeout in seconds to wait for the expected popup window
 * status
 * @param fail Tells whether to fail (ie. throw a {@link ScenarioFailedError}) if
 * the popup window state does not match the expected one
 * @return <code>true</code> if the popup window behaved as expected,
 * <code>false</code> otherwise when no failure is expected
 * @throws ScenarioFailedError if the popup window does not behave as expected
 * and failure was requested
 */
public boolean waitForPopupWindowState(final PopupWindowState state, final int seconds, final boolean fail) throws ScenarioFailedError {
	if (DEBUG) {
		debugPrintln("		+ Wait for a popup window to "+state);
	}

	// Wait for popup to be opened if necessary
	long timeout = seconds * 1000 + System.currentTimeMillis();
	if (state != PopupWindowState.CLOSED) {
		while (!hasPopupWindow()) {
			if (System.currentTimeMillis() > timeout) {
				if (fail) {
					throw new ScenarioFailedError("Popup window never comes up.");
				}
				println("WARNING: Popup window never comes up.");
				return false;
			}
		}
	}

	// Accept certificate
	if (isInternetExplorer()) {

		// Switch to the popup window
		if (DEBUG) debugPrintln("		  -> main window handle "+this.mainWindowHandle);
		Iterator<String> iterator = getWindowHandles().iterator();
		while (iterator.hasNext()) {
			String handle = iterator.next();
			if (!handle.equals(this.mainWindowHandle)) {
				if (DEBUG) debugPrintln("		  -> switch to window handle "+handle);
				this.driver.switchTo().window(handle);
				break;
			}
		}

		// Accept certificate
		acceptInternetExplorerCertificate();

		// Back to main window
		switchToMainWindow();
	}

	// Wait for popup to be closed if necessary
	timeout = seconds * 1000 + System.currentTimeMillis();
	if (state != PopupWindowState.OPENED) {
		while (hasPopupWindow()) {
			if (System.currentTimeMillis() > timeout) {
				if (fail) {
					throw new ScenarioFailedError("Popup window never close down.");
				}
				println("WARNING: Popup window never close down.");
				return false;
			}
		}
	}

	// Popup window state is correct
	return true;
}

/**
 * Returns the text for the given element if it matches one of the given ones or
 * <code>null</code> if none matches before the given timeout.
 *
 * @param element The web element to get the text from
 * @param fail Tells whether to fail if element text does not match any of the
 * given ones before timeout occurs
 * @param timeout The time to wait before giving up
 * @param texts Several possible texts for the given element text.
 * @return The matching text as a <code>String</code> if one matches
 * before after having waited the given timeout or <code>null</code> when
 * it's asked not to fail.
 * @throws ScenarioFailedError If the text never matches before timeout occurs
 * and if it's asked to fail.
 */
public String waitForText(final BrowserElement element, final boolean fail, final int timeout, final String... texts) {

	// Get the text web element
	if (DEBUG) {
		debugPrint("		+ wait for texts: ");
		String separator = "";
		for (String msg : texts) {
			debugPrint(separator + "\"" + msg + "\"");
			separator = ", ";
		}
		debugPrintln();
	}

	// Timeout Loop until timeout is reached
	String previousText = null;
	long timeoutMillis = timeout * 1000 + System.currentTimeMillis();

	while (System.currentTimeMillis() <= timeoutMillis) {

		// Get element text
		final String elementText = element.getText();

		// Check if text matches one of the given ones
		for (String text : texts) {
			if ((text.length() == 0 && elementText.length() == 0 ||
				(text.length() > 0 && elementText.startsWith(text)))) {
				if (DEBUG) debugPrintln("		  -> text was found: \""+elementText+"\"");
				return text;
			}
		}

		// Display element text if it has changed
		if (!elementText.equals(previousText)) {
			if (DEBUG) debugPrintln("		  -> current text is: \""+elementText+"\"");
			previousText = elementText;
		}
	}

	// No elements were not found in allowed time, fail or return null
	if (DEBUG) debugPrintln("		  -> no text was found!");
	if (fail) {
		StringBuilder builder = new StringBuilder("timeout while waiting for '");
		builder.append(element.getBy());
		builder.append('\'');
		throw new WaitElementTimeoutError(builder.toString());
	}
	return null;
}

/**
 * Returns whether one of the given text is present in the current displayed page
 * content.
 *
 * @param parentElement The element from where the search must start.
 * If <code>null</code> then element is expected in the current page.
 * @param fail Tells whether to fail if none of the locators is find before timeout
 * @param timeout The time to wait before giving up the research
 * @param displayed When <code>true</code> then only displayed element can be returned.
 * When <code>false</code> then the returned element can be either displayed or hidden.
 * @param single Tells whether a single element is expected
 * @param pattern The pattern used for matching text (see {@link ComparisonPattern}).
 * @param texts List of the text to find in the page
 * @return The found web element where text matches one of the given one or
 *  <code>null</code> if not asked to fail
 * @throw ScenarioFailedError if none of the text is found before the timeout
 * was reached.
 */
public BrowserElement waitForTextPresent(final BrowserElement parentElement, final boolean fail, final int timeout, final boolean displayed, final boolean single, final ComparisonPattern pattern, final String... texts) {

	// Create the search locator
	By	textBy = ByUtils.xpathMatchingTexts(pattern, false/*all*/, texts);

	// Return the element found
	return waitForElement(parentElement, textBy, fail, timeout, displayed, single);

}

/**
 *  Wait while an element with a given locator is displayed in the page.
 *
 * @param parentElement The parent element of the desired element.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 * @param seconds The timeout before giving up if the element is still displayed.
 * @param fail Tells whether to return <code>false</code> instead throwing
 * a {@link WaitElementTimeoutError} when the timeout is reached.
 * @return <code>true</code> if the element has disappeared before the timeout
 * is reached. Otherwise return <code>false</code> only if it has been asked not
 * to fail.
 * @throws WaitElementTimeoutError If the element is still displayed after the
 * given timeout has been reached and it has been asked to fail.
 */
public boolean waitWhileDisplayed(final BrowserElement parentElement, final By locator, final int seconds, final boolean fail) {
	return waitWhileDisplayed(parentElement, locator, seconds, fail, true /*single*/);
}

/**
 *  Wait while an element with a given locator is displayed in the page.
 *
 * @param parentElement The parent element of the desired element.
 * If <code>null</code> then element is expected in the current page.
 * @param locator Locator to find the element in the current page.
 * @param seconds The timeout before giving up if the element is still displayed.
 * @param fail Tells whether to return <code>false</code> instead throwing
 * a {@link WaitElementTimeoutError} when the timeout is reached.
 * @param single Specifies whether a single element is expected.
 * @return <code>true</code> if the element has disappeared before the timeout
 * is reached. Otherwise return <code>false</code> only if it has been asked not
 * to fail.
 * @throws WaitElementTimeoutError If the element is still displayed after the
 * given timeout has been reached and it has been asked to fail.
 */
public boolean waitWhileDisplayed(final BrowserElement parentElement, final By locator, final int seconds, final boolean fail, final boolean single) {
	BrowserElement element = waitForElement(parentElement, locator, false /*fail*/, 1 /*timeout*/, true /*displayed*/, single);

	return (element != null)  ? element.waitWhileDisplayed(seconds, fail) : true;
}
}
