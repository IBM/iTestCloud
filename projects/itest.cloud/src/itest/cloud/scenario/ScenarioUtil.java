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

import static itest.cloud.util.FileUtil.createDir;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

import javax.management.timer.Timer;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.InvalidCommandException;
import itest.cloud.scenario.error.ScenarioFailedError;
import itest.cloud.util.EncryptionUtil;

/**
 * Utils for Scenario tests execution.
 * <p>
 * The first utility is to read Scenario parameters.<br>
 * They can be defined either in a properties files specified by the System
 * property {@link #PARAM_FILE_PATH_ID} or directly by System properties
 * specified in the VM arguments tab of the launch configuration.
 * </p><p>
 * The second utility is to provide debug function allowing to dump debug
 * information either in the console or in a file if the System property
 * {@link #DEBUG_DIRECTORY} is set.
 * </p><p>
 * This class also provides following utilities:
 * <ul>
 * </li>
 * <li>{@link #elapsedTimeString(long)}
 * <p>
 * Returns a string to display the elasped time since the given start point.</p>
 * </li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * </ul>
 */
public class ScenarioUtil {

	/* Directories */
	public static final String USER_DIR_ID = "user.dir";

	/* Common characters */
	public static final char SPACE_CHAR = ' ';
	public static final char PATH_SEPARATOR_CHAR = '/';
	public static final char QUOTE = '\"';

	/* Common strings */
	public static final String EMPTY_STRING = "";
	public static final String[] EMPTY_STRING_ARRAY = new String[]{};
	public static final String SPACE_STRING = " ";
	public static final String DASH_STRING = "-";
	public static final String PERIOD_STRING = ".";
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String PATH_SEPARATOR = "/";
	public static final String PACKAGE_PREFIX = "itest.cloud";

	/* Internal */
	private static final List<String> PRINT_PARAMS = new ArrayList<String>();
	private static final List<String> SIZE_UNITS =
		Arrays.asList(new String[]{"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"});

	/* Time and date */
	public static final int ONE_MINUTE = (int)Timer.ONE_MINUTE;
	public static final long ONE_HOUR = Timer.ONE_HOUR;
	public static final SimpleDateFormat COMPACT_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss"); //$NON-NLS-1$
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss"); //$NON-NLS-1$
	public static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy"); //$NON-NLS-1$

	/* Date kept for log filtering. Compact string used to timestamp created files */
	public static final Date SCENARIO_START_TIME = new Date(System.currentTimeMillis());
	public static final String COMPACT_DATE_STRING = COMPACT_DATE_FORMAT.format(SCENARIO_START_TIME);

	/* Recovery */
	public static final int MAX_RECOVERY_TRIES = 5;

	/**
	 * Global flag whether to print output on console or not.
	 *
	 * Default is <code>true</code>.
	 */
	public static final boolean PRINT = getParameterBooleanValue("print", true);

	/* Parameters and data */

	// Parameters file path.
	private static final String PARAM_FILE_PATH_ID = "paramFilePath";
	private static final String PARAM_FILES_DIR_ID = "paramFilesDir";
	private static final String PARAM_FILES_PATH_ID = "paramFilesPath";

	// An application URL is inapplicable for a mobile application running on an emulator.
	// Therefore, the following predefined URL is utilized as the application URL in such a situation.
	public static final String MOBILE_APPLICATION_URL = "https://mobile.io";

	/**
	 * Properties where parameters and their value are stored. All parameters used
	 * to run a scenario can be defined in a properties file. That allow easy definition
	 * and exchanges among testers and test environments.
	 * <p>
	 * The properties file path can be specified using {@link #PARAM_FILE_PATH_ID}
	 * System property.
	 * </p>
	 */
	private static final Properties PARAMETERS;
	static {
		String rootDir, filesPath;
		String paramFilePath = System.getProperty(PARAM_FILE_PATH_ID);
		if (paramFilePath != null) {
			// Backward compatibility
			int idx = paramFilePath.lastIndexOf('/');
			if (idx < 0) {
				rootDir = null;
				filesPath = paramFilePath;
			} else {
				rootDir = paramFilePath.substring(0, idx);
				filesPath = paramFilePath.substring(idx+1);
			}
		} else {
			// paramFilesDir parameter has now dataRootDir parameter as default value.
			rootDir = System.getProperty(PARAM_FILES_DIR_ID);
			filesPath = System.getProperty(PARAM_FILES_PATH_ID);
		}
		if (filesPath == null) {
			PARAMETERS = null;
		} else {
			StringTokenizer pathTokenizer = new StringTokenizer(filesPath, ";");
			Properties allProperties = new Properties();
			while (pathTokenizer.hasMoreTokens()) {
				allProperties.putAll(readParametersFile(rootDir, pathTokenizer.nextToken()));
			}
			PARAMETERS = allProperties;
		}
	}

	/**
	 * Global flag whether to print debug information on console or not.
	 * <p>
	 * Default is <code>true</code>.
	 * </p>
	 * @category debug parameters
	 */
	public static final boolean DEBUG = getParameterBooleanValue("debug", false);

	/**
	 * Returns the directory to use to store debug file.
	 * <p>
	 * To specify it, then use the following parameter:
	 * <ul>
	 * <li><b>Name</b>: <code>debug.dir</code></li>
	 * <li><b>Value</b>: <code>String</code>, a valid directory name matching
	 * the OS on which you're running the scenario<br></li>
	 * <li><b>Default</b>: <i>none</i></li>
	 * <li><b>Usage</b>:
	 * <ul>
	 * <li><code>debug.dir=C:\tmp\selenium\failures</code> in the properties file</li>
	 * <li><code>-Ddebug.dir=C:\tmp\selenium\failures</code> in the VM Arguments
	 * field of the launch configuration.</li>
	 * </ul></li>
	 * </ul>
	 * </p><p>
	 * Note that this parameter is ignored even if specified when {@link #DEBUG}
	 * parameter is set to <code>false</code>.
	 * </p>
	 */
	public final static String DEBUG_DIRECTORY;
	static {
		String dir = null;
		dir = getParameterValue("debug.dir");
		if (dir != null && dir.trim().length() > 0) {
			if (dir.indexOf(File.separatorChar) < 0) {
				dir = System.getProperty(USER_DIR_ID) + File.separator + dir;
				createDir(dir);
			}
		} else {
			dir = null;
		}
		DEBUG_DIRECTORY = dir;
	}

	/**
	 * Parameter telling which directory to use to put debug file.
	 * <p>
	 * Name: <code>"log.file.name"</code><br>
	 * Value: <code>String</code>, a valid file name matching the OS on which you're running the BVT test<br>
	 * Default value: <i>debug.log</i></br>
	 * Usage: <code>-Dlog.file.name=my_debug_file.log</code> in the VM Arguments
	 * field of the launch configuration.
	 * </p></p>
	 * Note that this parameter is ignored if {@link #DEBUG} parameter
	 * is set to <code>false</code>.
	 * </p>
	 * @category debug parameters
	 */
	public final static String LOG_FILE_NAME = getParameterValue("log.file.name", "debug_"+COMPACT_DATE_STRING+".log");

//	/**
//	 * Parameter specifying the name of the log file to store messages coming from the browser/driver.
//	 * <p>
//	 * Name: <code>"browser.log.file.name"</code><br>
//	 * Value: <code>String</code>, a valid file name matching the OS on which the tests are run.<br>
//	 * Default value: <i>debug.log</i></br>
//	 * Usage: <code>-Dbrowser.log.file.name=my_borwser_log_file.log</code> in the VM Arguments
//	 * field of the launch configuration.
//	 * </p>
//	 * @category debug parameters
//	 */
//	private final static String BROWSER_LOG_FILE_NAME = getParameterValue("browser.log.file.name", "browser_log_" + COMPACT_DATE_STRING + ".log");
//
//	public final static String BROWSER_LOG_FILE = (new File(createDir(DEBUG_DIRECTORY), BROWSER_LOG_FILE_NAME)).getAbsolutePath();

	/**
	 * Parameter specifying the name of the properties file to store details about the scenario execution.
	 * <p>
	 * Name: <code>"execution.details.file.name"</code><br>
	 * Value: <code>String</code>, a valid file name matching the OS on which the tests are run.<br>
	 * Default value: <i>execution_details.properties</i></br>
	 * Usage: <code>-Dexecution.details.file.name=my_execution_details_file.properties</code> in the VM Arguments
	 * field of the launch configuration.
	 * </p>
	 * @category debug parameters
	 */
	private final static String EXECUTION_DETAILS_FILE_NAME = getParameterValue("execution.details.file.name", "execution_details_" + COMPACT_DATE_STRING + ".properties");

	public final static String EXECUTION_DETAILS_FILE = (new File(DEBUG_DIRECTORY, EXECUTION_DETAILS_FILE_NAME)).getAbsolutePath();

	/**
	 * Parameter telling which directory to use to put log of server error messages.
	 * <p>
	 * Name: <code>"server.errors.file.name"</code><br>
	 * Value: <code>String</code>, a valid file name matching the OS on which you're running the BVT test<br>
	 * Usage: <code>-Dserver.errors.file.name=my_server_error_file.log</code> in the VM Arguments field of the launch configuration.
	 * </p>
	 *
	 */
	public final static String SERVER_ERRORS_FILE_NAME = getParameterValue("server.errors.file.name", "logErrors_" + COMPACT_DATE_STRING + ".log");

	/*
	 * Stream to store debug information.
	 */
	private static PrintWriter LOG_WRITER;
	private static StringWriter STR_WRITER;
	static {
		setDebugWriter();
	}

	/*
	 * Indentations for debug print purposes.
	 */
	private final static String ONE_INDENT_TAB_WITH_PREFIX = "\t- ";
	private final static String TWO_INDENT_TAB_WITH_PREFIX = "\t\t+ ";
	private final static String THREE_INDENT_TAB_WITH_PREFIX = "\t\t\t* ";

private static void attachDebugLogToRqmAdapter(final File debugFile) {
	debugPrintln("Attach debug log file to RQM Adapter:");
    String rqmAdapterAttachmentsFile = getParameterValue("com.ibm.rqm.adapter.resultAttachmentsFile");
    if (rqmAdapterAttachmentsFile == null) {
		debugPrintln("	'com.ibm.rqm.adapter.resultAttachmentsFile' parameter is not set");
    	// Workaround
    	rqmAdapterAttachmentsFile = System.getenv("qm_AttachmentsFile");
	    if (rqmAdapterAttachmentsFile == null) {
			debugPrintln("	'qm_AttachmentsFile' envirnment variable is not set either, give up...");
	    	return;
	    }
		debugPrintln("	'qm_AttachmentsFile' = '"+rqmAdapterAttachmentsFile+"'");
    } else {
		debugPrintln("	'com.ibm.rqm.adapter.resultAttachmentsFile' = '"+rqmAdapterAttachmentsFile+"'");
    }
	Properties props = new Properties();
	try {
		FileInputStream istream = new FileInputStream(rqmAdapterAttachmentsFile);
        props.load(istream);
        istream.close();
        props.setProperty("debug.log", debugFile.getAbsolutePath());
		debugPrintln("	adding property 'debug.log' = '"+debugFile.getAbsolutePath()+"'");
		FileOutputStream ostream = new FileOutputStream(rqmAdapterAttachmentsFile);
        props.store(ostream, "Execution Result attached files/links");
        ostream.close();
	} catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
	}
}

/**
 * Close the debug stream.
 */
public static void debugClose() {
	if (DEBUG_DIRECTORY != null) {
		LOG_WRITER.println("**********  Close Debug Session: "+COMPACT_DATE_STRING+"  ********");
		LOG_WRITER.close();
		if (STR_WRITER == null) {
			if (DEBUG) {
				System.out.println();
				System.out.print("Debug information have been written to '");
				System.out.print(DEBUG_DIRECTORY);
				System.out.print(File.separator);
				System.out.print(LOG_FILE_NAME);
				System.out.println("'");
			} else {
				System.out.println();
				System.out.println("WARNING: No debug information written due argument debug="+DEBUG);
			}
		} else {
			System.out.println();
			System.out.println("**********  DEBUG INFORMATION **********");
			System.out.println(STR_WRITER.toString());
		}
	}
}

/**
 * Open debug stream.
 */
public static void debugOpen() {
	if (DEBUG_DIRECTORY == null) {
		LOG_WRITER = new PrintWriter(System.out, false);
	} else {
		// Close previous file if any
		if (LOG_WRITER != null) {
			LOG_WRITER.close();
			LOG_WRITER = null;
			STR_WRITER = null;
		}
		// Create directory for debug file
		File dir = DEBUG_DIRECTORY != null ? new File(DEBUG_DIRECTORY) : null;
		// Open debug file for writing
		if (dir != null) {
			File file = new File(dir, LOG_FILE_NAME);
			try {
				LOG_WRITER = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file, false)), false);
				LOG_WRITER.println("**********  Open Debug Session: "+COMPACT_DATE_STRING+"  ********");
				attachDebugLogToRqmAdapter(file);
			} catch (FileNotFoundException e) {
				System.err.println("Cannot create stream for log: " + e.getMessage());
			}
		}
		// If file was not opened, then use a simple string instead.
		// In that case, info will be written in the console at the end of the execution
		if (LOG_WRITER == null) {
			setDebugWriter();
			LOG_WRITER.println("**********  Open Debug Session: "+COMPACT_DATE_STRING+"  ********");
		}
	}
}

/**
 * Print a text in the debug stream.
 *
 * @param text The line to print in the stream.
 */
public static void debugPrint(final String text) {
	if (!DEBUG) return;
	LOG_WRITER.print(formatDebugLine(text));
	if (DEBUG_DIRECTORY == null) {
		LOG_WRITER.flush();
	}
}

/**
 * Print only meaningful element of an exception statck trace
 *
 * @param t The exception
 */
public static void debugPrintException(final Throwable t) {
	if (!DEBUG) return;
	StringBuilder builder = new StringBuilder(getClassSimpleName(t.getClass())).append(": ");
	final String message = t.getMessage();
	if (message == null) {
		builder.append("no message");
	} else {
		int idx = message.indexOf('\n');
		if (idx < 0) {
			builder.append(message);
		} else {
			builder.append(message, 0, idx);
		}
	}
	debugPrintln(builder.toString());
	debugPrintStackTrace(t.getStackTrace(), 1);
}

/**
 * Debug method to print expected strings of a given kind of HTML element.
 */
public static void debugPrintExpectedStrings(final String kind, final String status, final String... strings) {
	if (!DEBUG) return;
    int length = strings.length;
    if (length == 1) {
    	debugPrintln("		+ expecting following "+kind+" to be "+status+": \""+strings[0]+"\"");
    } else {
    	debugPrintln("		+ expecting one of following "+kind+"s to be "+status+":");
    	for (int i=0; i<length; i++) {
    		debugPrintln("			"+(i+1)+") \""+strings[i]+"\"");
    	}
    }
}

/**
 * Print a new line in the debug stream.
 */
public static void debugPrintln() {
	if (!DEBUG) return;
	LOG_WRITER.println();
	if (DEBUG_DIRECTORY == null) {
		LOG_WRITER.flush();
	}
}

/**
 * Print a line in the debug stream.
 *
 * @param text The line to print to the stream.
 */
public static void debugPrintln(final String text) {
	if (!DEBUG) return;
	if (LOG_WRITER != null) {
		LOG_WRITER.println(formatDebugLine(text));
		if (DEBUG_DIRECTORY == null) {
			LOG_WRITER.flush();
		}
	}
}

/**
 * Print a line in the debug stream.
 *
 * @param text The line to print in the stream.
 */
public static void debugPrintln(final String... text) {
	if (!DEBUG) return;
	for (String str: text) {
		LOG_WRITER.print(str);
	}
	LOG_WRITER.println();
	if (DEBUG_DIRECTORY == null) {
		LOG_WRITER.flush();
	}
}

/**
 * Print an indented line in the debug stream.
 *
 * @param text The line to print in the stream.
 */
public static void debugPrintln(final String text, final int indent) {
	printIndent(indent);
	debugPrintln(text);
}

/**
 * Print only meaningful element of the given stack trace for the caller
 *
 * @param tabs The number of tabs to indent each element
 */
public static void debugPrintStackTrace(final int tabs) {
	debugPrintStackTrace(new Exception().getStackTrace(), tabs);
}

/**
 * Print only meaningful element of the given stack trace
 *
 * @param elements The full stack trace elements
 */
public static void debugPrintStackTrace(final StackTraceElement[] elements) {
	debugPrintStackTrace(elements, 0);
}

/**
 * Print only meaningful element of the given stack trace
 *
 * @param elements The full stack trace elements
 * @param tabs The number of tabs to indent each element
 */
public static void debugPrintStackTrace(final StackTraceElement[] elements, final int tabs) {
	if (!DEBUG) return;
    for (StackTraceElement element: elements) {
		String elementClassName = element.getClassName();
		if (elementClassName.startsWith("com.ibm.itest")) {
    		debugPrintln(element.toString(), tabs);
    	}
    }
}

/**
 * Returns a string to display the elapsed time since the given start point.
 *
 * @param start The start of the time measure.
 * @return The elapsed time as a human readable {@link String}.
 */
public static String elapsedTimeString(final long start) {
    return timeString(getElapsedTime(start));
}

/**
 * Format a given line before being written to debug output stream.
 * <p>
 * Check to see if this is formed like a typical first line, e.g., starting with \t\t+.
 * If so, add some extra debug information.
 * </p>
 * @param line The line to print
 */
private static String formatDebugLine(final String line) {
	// Check whether the line is starting with appropriate prefix
	if (line.startsWith("\t\t+")) {
		// Get the Class.method() name
		StackTraceElement[] stackElements = whoCallsMe();
		if (stackElements.length > 0) {
			String classMethod = getClassSimpleName(stackElements[0].getClassName()) + "." + stackElements[0].getMethodName() + "()";
			// Format the line
			int index = line.indexOf('+');
			return "		+ " + classMethod + ": "+line.substring(index+1).trim();
		}
	}
	return line;
}

/**
 * Return the class name without package prefix.
 *
 * @return the simple class name as a String.
 */
public static String getClassSimpleName(final Class<?> clazz) {
    return getClassSimpleName(clazz.getName());
}

/**
 * Return the class name without package prefix.
 *
 * @return the simple class name as a String.
 */
public static String getClassSimpleName(final String className) {
    String classSimpleName = className;
    int lastDot = classSimpleName.lastIndexOf('.');
    if (lastDot != -1) {
        classSimpleName = classSimpleName.substring(lastDot + 1);
    }
	return classSimpleName;
}

/**
 * Obtain the content from a provided {@link InputStream}.
 * The provided {@link InputStream} will not be closed by this method.
 * Therefore, the caller must close the {@link InputStream} instead.
 *
 * @param inputStream The {@link InputStream} to extract content from.
 * @return The extracted content from the provided {@link InputStream}.
 * @throws IOException If an error occurs while extracting the content from the {@link InputStream}.
 */
public static String getContent(final InputStream inputStream) throws IOException{
	// Create a buffered reader to obtain the content from the input stream.
	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	StringBuffer contentBuffer = new StringBuffer();
	String line = "";

	// Read each line from the input stream.
	while ((line = bufferedReader.readLine()) != null) {
		contentBuffer.append(line + "\n");
	}

	// Return the string content.
	return contentBuffer.toString();
}

/**
 * Return the decrypted/decoded value of the the given property either got from #PARAM_FILE_NAME
 * file or from the System property value set in the launch config.
 *
 * @param name The parameter name
 *
 * @return The string corresponding to the parameter value or <code>null</code>
 * if the parameter is not defined.
 */
public static String getDecryptedParameterValue(final String name) {
	return getDecryptedParameterValue(name, null /*defaultEncryptedValue*/);
}

/**
 * Return the decrypted/decoded value of the the given property either got from #PARAM_FILE_NAME
 * file or from the System property value set in the launch config.
 *
 * @param name The parameter name
 * @param defaultEncryptedValue The default encrypted value of the parameter if a value is not defined.
 *
 * @return The string corresponding to the parameter value or <code>null</code>
 * if the parameter is not defined.
 */
public static String getDecryptedParameterValue(final String name, final String defaultEncryptedValue) {
	String encryptedValue = getProperty(name);
	if (encryptedValue == null) encryptedValue = defaultEncryptedValue;
	String value = EncryptionUtil.decrypt(encryptedValue);
	printReadParameter(name, value.charAt(0) + "*******");
	return value;
}

/**
 * Get the elapsed time since the given start point.
 *
 * @param start The start of the time measure.
 * @return The elapsed time as a long.
 */
public static long getElapsedTime(final long start) {
	return System.currentTimeMillis()-start;
}

/**
 * Return an element with text matching a given pattern from a list.
 *
 * @param elements The list containing the elements as {@link List}.
 * @param pattern The pattern to match the text of the desired element.
 * @param fail Specifies whether to fail if a matching element can not be found in the list.
 *
 * @return The element matching the given pattern as {@link BrowserElement} or <code>null</code> if
 * a matching element can not be found and asked not to fail.
 */
public static BrowserElement getElementFromList(final List<BrowserElement> elements, final Pattern pattern, final boolean fail) {
	for (BrowserElement element : elements) {
		if(pattern.matcher(element.getText()).matches()) {
			return element;
		}
	}

	if(fail) throw new ScenarioFailedError("Element matching pattern '" + pattern + "' could not be found in list: " + getTextFromList(elements));

	return null;
}

/**
 * Return the architecture of the operating system where the tests are run.
 *
 * @return The architecture of the operating system where the tests are run.
 */
public static String getOsArchitecture() {
	return System.getProperty("os.arch");
}

/**
 * Return the name of the operating system where the tests are run.
 *
 * @return The name of the operating system where the tests are run.
 */
public static String getOsName() {
	return System.getProperty("os.name");
}

/**
 * Return the version of the operating system where the tests are run.
 *
 * @return The version of the operating system where the tests are run.
 */
public static String getOsVersion() {
	return System.getProperty("os.version");
}

/**
 * Return the package name of a given class.
 *
 * @return the package name as a String.
 */
public static String getPackageName(final Class<?> clazz) {
	return clazz.getPackage().getName();
}

/**
 * Return the boolean value from the System property value set in
 * the launch config.
 *
 * @param name The parameter name
 * @return The value as an <code>boolean</code> corresponding to the system
 * property or <code>false</code> if it is not defined.
 */
public static boolean getParameterBooleanValue(final String name) {
	return getParameterBooleanValue(name, false);
}

/**
 * Return the boolean value from the System property value set in
 * the launch config.
 *
 * @param name The parameter name
 * @param defaultValue The value returned if the system property is not defined.
 * @return The value as an <code>boolean</code> corresponding to the system
 * property or the default value if it is not defined.
 */
public static boolean getParameterBooleanValue(final String name, final boolean defaultValue) {
	String parameterValue = getParameterValue(name);
	if (parameterValue == null) return defaultValue;
	return parameterValue.equals("true");
}

/**
 * Return the double value from the System property value set in
 * the launch config.
 *
 * @param name The parameter name
 * @return The value as an <code>double</code> corresponding to the system
 * property or the default value if it is not defined or if the corresponding system
 * property does not define a valid double.
 */
public static double getParameterDoubleValue(final String name) {
	return getParameterDoubleValue(name, 1.0);
}

/**
 * Return the double value from the System property value set in
 * the launch config.
 *
 * @param name The parameter name
 * @param defaultValue The value returned if the system property is not defined.
 * @return The value as an <code>double</code> corresponding to the system
 * property or the default value if it is not defined or if the corresponding system
 * property does not define a valid double.
 */
public static double getParameterDoubleValue(final String name, final double defaultValue) {
	String parameterValue = getParameterValue(name);
	if (parameterValue == null || parameterValue.trim().isEmpty()) return defaultValue;
	try {
		return Double.parseDouble(parameterValue);
	}
	catch (NumberFormatException nfe) {
		// if property is not a valid integer value, then keep the default value
		System.err.println("The specified value for parameter '"+name+"' is not a valid integer!");
		System.err.println(defaultValue+" default value will be used instead...");
	}
	return defaultValue;
}

/**
 * Return the parameter double value from the System property set in
 * the launch config.
 *
 * @param name The parameter name
 * @param defaultValue The value returned if the parameter is not defined.
 * @return The value as a {@link String} corresponding to the system property
 * or <code>defaultValue</code> if the system property is not defined.
 */
public static double[] getParameterDoubleValues(final String name, final String defaultValue) {
	String[] stringValues = getParameterValues(name, defaultValue);
	double[] doubleValues = new double[stringValues.length];

	for (int i = 0; i < stringValues.length; i++) {
		doubleValues[i] = Double.parseDouble(stringValues[i]);
	}

	return doubleValues;
}

/**
 * Return the parameter File value from the System property set in the launch config.
 *
 * @param name The parameter name
 * @param defaultParentDir The default parent directory containing the file if the parameter is not defined.
 * If <code>null</code> is provided as the value of this parameter, the current/working directory will be used as the
 * parent directory.
 * @param defaultFileName The default name of the file if the parameter is not defined.
 * @return The value as a {@link File} corresponding to the system property or the default if the system property is not
 * defined.
 */
public static File getParameterFileValue(final String name, final File defaultParentDir, final String defaultFileName) {
	String filePath = getParameterValue(name);
	File file = (filePath != null) ? new File(filePath) : new File(defaultParentDir, defaultFileName);

	if(!file.exists()) {
		File parent = file.getParentFile();
		if((parent == null) || (!parent.exists())) {
			throw new ScenarioFailedError("Neither file '" + file.getAbsolutePath() + "' nor its parent existed in file system");
		}

		StringBuffer children = new StringBuffer();
		for (File child : parent.listFiles()) {
			children.append(child.getName());
			children.append(",");
		}

		throw new ScenarioFailedError("File '" + file.getAbsolutePath() + "' did not exist in file system, but following files were present in its parent directory: " + children);
	}

	return file;
}

/**
 * Return the integer value from the System property value set in
 * the launch config.
 *
 * @param name The parameter name
 * @return The value as an <code>int</code> corresponding to the system
 * property or <code>0</code> if it is not defined.
 * @throws NumberFormatException If the corresponding system property
 * does not define a valid integer.
 */
public static int getParameterIntValue(final String name) {
	return getParameterIntValue(name, 0 /*defaultValue*/);
}

/**
 * Return the integer value from the System property value set in
 * the launch config.
 *
 * @param name The parameter name
 * @param print Specify whether to print the value of the parameter in the console.
 *
 * @return The value as an <code>int</code> corresponding to the system
 * property or <code>0</code> if it is not defined.
 */
public static int getParameterIntValue(final String name, final boolean print) {
	return getParameterIntValue(name, 0 /*defaultValue*/, print);
}

/**
 * Return the integer value from the System property value set in
 * the launch config.
 *
 * @param name The parameter name
 * @param defaultValue The value returned if the system property is not defined.
 *
 * @return The value as an <code>int</code> corresponding to the system
 * property or the default value if it is not defined or if the corresponding system
 * property does not define a valid integer.
 */
public static int getParameterIntValue(final String name, final int defaultValue) {
	return getParameterIntValue(name, defaultValue, true /*print*/);
}

/**
 * Return the integer value from the System property value set in
 * the launch config.
 *
 * @param name The parameter name
 * @param defaultValue The value returned if the system property is not defined.
 * @param print Specify whether to print the value of the parameter in the console.
 *
 * @return The value as an <code>int</code> corresponding to the system
 * property or the default value if it is not defined or if the corresponding system
 * property does not define a valid integer.
 */
public static int getParameterIntValue(final String name, final int defaultValue, final boolean print) {
	String parameterValue = getParameterValue(name, print);
	if (parameterValue == null || parameterValue.trim().isEmpty()) return defaultValue;
	try {
		return Integer.parseInt(parameterValue);
	}
	catch (NumberFormatException nfe) {
		// if property is not a valid integer value, then keep the default value
		System.err.println("The specified value for parameter '"+name+"' is not a valid integer!");
		System.err.println(defaultValue+" default value will be used instead...");
	}
	return defaultValue;
}

/**
 * Return the parameter int value from the System property set in
 * the launch config.
 *
 * @param name The parameter name
 * @param defaultValue The value returned if the parameter is not defined.
 * @return The value as a {@link String} corresponding to the system property
 * or <code>defaultValue</code> if the system property is not defined.
 */
public static int[] getParameterIntValues(final String name, final String defaultValue) {
	String[] stringValues = getParameterValues(name, defaultValue);
	int[] intValues = new int[stringValues.length];

	for (int i = 0; i < stringValues.length; i++) {
		intValues[i] = Integer.parseInt(stringValues[i]);
	}

	return intValues;
}

/**
 * Return the string value from the first defined System property from the list
 * set in the launch config.
 *
 * @param names A list of possible parameter names
 * @return The value as a {@link String} corresponding to the first defined
 * system property defined or <code>null</code> if none was found.
 */
public static String getParametersValue(final String... names) {
	for (String name: names) {
		String value = getProperty(name);
    	if (value != null) {
			printReadParameter(name, value);
    		return value;
    	}
	}
	return null;
}

/**
 * Return the parameter string value from the System property set in
 * the launch config.
 *
 * @param name The parameter name
 * @return The value as a {@link String} corresponding to the system property
 * or <code>null</code> if the system property is not defined.
 */
public static String getParameterValue(final String name) {
    return getParameterValue(name, null /*defaultValue*/);
}

/**
 * Return the parameter string value from the System property set in
 * the launch config.
 *
 * @param name The parameter name
 * @param print Specify whether to print the value of the parameter in the console.
 *
 * @return The value as a {@link String} corresponding to the system property
 * or <code>null</code> if the system property is not defined.
 */
public static String getParameterValue(final String name, final boolean print) {
	return getParameterValue(name, null /*defaultValue*/, print);
}

/**
 * Return the parameter string value from the System property set in
 * the launch config.
 * <p>
 * The value of the parameter will be printed in the console.
 * </p>
 *
 * @param name The parameter name
 * @param defaultValue The value returned if the parameter is not defined.
 * @return The value as a {@link String} corresponding to the system property
 * or <code>defaultValue</code> if the system property is not defined.
 */
public static String getParameterValue(final String name, final String defaultValue) {
	return getParameterValue(name, defaultValue, true /*print*/);
}

/**
 * Return the parameter string value from the System property set in
 * the launch config.
 *
 * @param name The parameter name
 * @param defaultValue The value returned if the parameter is not defined.
 * @param print Specify whether to print the value of the parameter in the console.
 *
 * @return The value as a {@link String} corresponding to the system property
 * or <code>defaultValue</code> if the system property is not defined.
 */
public static String getParameterValue(final String name, final String defaultValue, final boolean print) {
	String value = getProperty(name);
	if (value == null) return defaultValue;
	if (print) printReadParameter(name, value);
	return value;
}

/**
 * Return the parameter string value from the System property set in
 * the launch config.
 *
 * @param name The parameter name
 * @param defaultValue The value returned if the parameter is not defined.
 * @return The value as a {@link String} corresponding to the system property
 * or <code>defaultValue</code> if the system property is not defined.
 */
public static String[] getParameterValues(final String name, final String defaultValue) {
	String value = getProperty(name);
	if (value == null) {
		value = defaultValue;
	} else {
		printReadParameter(name, value);
	}
	return value.split(",", -1 /*limit*/);
}

/**
 * Return the parameter string value from a password property.
 * <p>
 * This method differs from default parameter value in the sense that value is not
 * not printed but just the first character followed by '*******' instead.
 * This avoids to have any password value stored and accessible either
 * in the console or in the debug log file.  Printing the first character is
 * to simplify debugging (e.g., login/LDAP issues).
 * </p>
 * @param name The password property name
 * @return The value as a {@link String} corresponding to the system property
 * or <code>null</code> if the system property is not defined.
 */
public static String getPasswordValue(final String name) {
	String value = getProperty(name);
	if (value == null) return null;
	printReadParameter(name, value.charAt(0) + "*******");
	return value;
}

/**
 * Return the value of the the given property either got from #PARAM_FILE_NAME
 * file or from the System property value set in the launch config.
 *
 * @param name The parameter name
 * @return The string corresponding to the parameter value or <code>null</code>
 * if the parameter is not defined.
 */
private static String getProperty(final String name) {
	String value = System.getProperty(name);
	if ((value != null) && !value.trim().equals(EMPTY_STRING)) return value;
	if (PARAMETERS != null) {
		value = PARAMETERS.getProperty(name);
	}
	return ((value != null) && !value.trim().equals(EMPTY_STRING)) ? value : null;
}

/**
 * Get the Selenium build and version information.
 *
 * @return Selenium build and version information.
 */
public static String getSeleniumVersion() {
	WebDriverException exception = new WebDriverException("info");
	return "Selenium " + exception.getBuildInformation();
}

/**
 * Return the given objects list as a flat text separated with comma.
 *
 * @return The text as as {@link String}
 */
public static String getTextFromList(final List<?> object) {
	return getTextFromList(object, ", ");
}

/**
 * Return the given objects list as a flat text separated with the given separator.
 *
 * @param strings The list of strings
 * @param separator String to use to separate strings
 * @return The text as as {@link String}
 */
public static String getTextFromList(final List<?> strings, final String separator) {
	Object[] array = new Object[strings.size()];
	strings.toArray(array);
	return getTextFromList(array, separator);
}

/**
 * Return the given objects list as a flat text separated with comma.
 *
 * @return The text as as {@link String}
 */
public static String getTextFromList(final Object[] strings) {
	return getTextFromList(strings, ", ");
}

/**
 * Return the given objects list as a flat text separated with the given separator.
 *
 * @param objects The list of objects
 * @param separator String to use to separate strings
 * @return The text as as {@link String}
 */
public static String getTextFromList(final Object[] objects, final String separator) {
	final StringBuilder builder = new StringBuilder();
	boolean first = true;
	for (Object obj: objects) {
		if (!first) builder.append(separator);
		builder.append(obj);
		first = false;
	}
	return builder.toString();
}

/**
 * Return the given objects list as a flat text separated with comma.
 *
 * @return The text as as {@link String}
 */
public static String getTextFromList(final Set<?> strings) {
	return getTextFromList(new ArrayList<Object>(strings), ", ");
}

/**
 * Pause during the given milli-seconds time.
 *
 * @param millisecs
 */
public static void pause(final long millisecs) {
	try {
		Thread.sleep(millisecs);
	} catch (InterruptedException ie) {
		// skip
	}
}

/**
 * Print a text to the console. The output is done iff the {@link #PRINT} flag
 * is set.
 *
 * @param text The text to print to the console.
 */
public static void print(final Object text) {
	if (PRINT) System.out.print(text);
	if (DEBUG && (!PRINT || DEBUG_DIRECTORY != null)) debugPrint(text.toString());
}

/**
 * Print only meaningful element of an exception statck trace
 *
 * @param t The exception
 */
public static void printException(final Throwable t) {
	StringBuilder builder = new StringBuilder(getClassSimpleName(t.getClass())).append(": ");
	final String message = t.getMessage();
	if (message != null) {
		int idx = message.indexOf('\n');
		if (idx < 0) {
			builder.append(message);
		} else {
			builder.append(message, 0, idx);
		}
	}
	println(builder.toString());
	printStackTrace(t.getStackTrace(), 1);
}

private static void printIndent(final int indent) {
	switch (indent) {
		case 1:
			LOG_WRITER.print(ONE_INDENT_TAB_WITH_PREFIX);
			break;
		case 2:
			LOG_WRITER.print(TWO_INDENT_TAB_WITH_PREFIX);
			break;
		case 3:
			LOG_WRITER.print(THREE_INDENT_TAB_WITH_PREFIX);
			break;
		default:
			for (int i=0; i<indent; i++) {
				LOG_WRITER.print("\t");
			}
			LOG_WRITER.print("->");
			break;
	}
}

/**
 * Print a empty line to the console. The output is done iff the {@link #PRINT}
 * flag is set.
 */
public static void println() {
	if (PRINT) System.out.println();
	if (DEBUG && (!PRINT || DEBUG_DIRECTORY != null)) debugPrintln();
}

/**
 * Print a text with a new line at the end to the console. The output is done
 * iff the {@link #PRINT} flag is set.
 *
 * @param text The text to print to the console.
 */
public static void println(final Object text) {
	if (PRINT) System.out.println(text);
	if (DEBUG && (!PRINT || DEBUG_DIRECTORY != null)) debugPrintln(String.valueOf(text));
}

private static void printReadParameter(final String name, final String value) {
	if (PRINT_PARAMS.isEmpty()) {
		println("Read parameters while running scenario:");
	}
	if (!PRINT_PARAMS.contains(name)) {
		PRINT_PARAMS.add(name);
		println("	- '"+name+"' value="+value);
	}
}

/**
 * Print only meaningful element of the given stack trace for the caller
 *
 * @param tabs The number of tabs to indent each element
 */
public static void printStackTrace(final int tabs) {
	StackTraceElement[] elements = new Exception().getStackTrace();
	boolean first = true;
    for (StackTraceElement element: elements) {
    	if (first) {
    		// Skip first element which is the current method
    	} else {
    		String elementClassName = element.getClassName();
    		if (elementClassName.startsWith(PACKAGE_PREFIX)) {
	    		printIndent(tabs);
	    		println(element.toString());
	    	}
    	}
    	first = false;
    }
}

/**
 * Print only meaningful element of the given stack trace
 *
 * @param elements The full stack trace elements
 * @param tabs The number of tabs to indent each element
 */
public static void printStackTrace(final StackTraceElement[] elements, final int tabs) {
    for (StackTraceElement element: elements) {
		String elementClassName = element.getClassName();
		if (elementClassName.startsWith(PACKAGE_PREFIX) && !elementClassName.startsWith(PACKAGE_PREFIX + ".scenario.Scenario")) {
    		printIndent(tabs);
    		println(element.toString());
    	}
    }
}

/**
 * Print the starting point for the given test case.
 *
 * @param stepName The scenario step
 */
public static void printStepStart(final String stepName) {
    StringBuilder builder = new StringBuilder(LINE_SEPARATOR)
    	.append("Starting execution of test case '")
    	.append(stepName)
    	.append("' at ")
    	.append(TIME_FORMAT.format(new Date(System.currentTimeMillis())))
    	.append(LINE_SEPARATOR)
    	.append("======================================");
    final int length = stepName.length();
    for (int i=0; i<length; i++) {
    	builder.append('=');
    }
    final String text = builder.toString();
	if (PRINT) {
	    System.out.println(text);
    }
    if (DEBUG && (!PRINT || DEBUG_DIRECTORY != null)) {
    	debugPrintln(text);
    }
}

private static Properties readParametersFile(final String fileDir, final String filePath) {
    File paramFile = new File(fileDir, filePath);

    if (!paramFile.exists() && fileDir == null && !paramFile.isAbsolute()) {
		// Maybe a relative path?
		paramFile = new File(System.getProperty("user.dir") + File.separator + filePath);
	}

	if (!paramFile.exists()) {
		final String message = "The parameters properties file '"+paramFile.getAbsolutePath()+"' has not been found!";
		System.err.println(message);
		if (DEBUG) debugPrintln(message);
		throw new RuntimeException(message);
	}

	return readPropertiesFile(paramFile);
}

/**
 * Read a given properties file.
 *
 * @param file The properties file as {@link File}.
 *
 * @return The properties of the given file as {@link Properties}.
 */
public static Properties readPropertiesFile(final File file) {
	try {
		final Properties properties = new Properties();
		final FileInputStream stream = new FileInputStream(file);

		try {
			properties.load(stream);
		}
		catch (IOException ioe) {
			throw new ScenarioFailedError(ioe);
		}
		finally {
			try {
	            stream.close();
            } catch (IOException e) {
	            // Ignore
            }
	    }
		return properties;
	}
	catch (FileNotFoundException fnfe) {
		throw new ScenarioFailedError(fnfe);
	}
}

/**
 * Run a command in the file system.
 *
 * @param cmdarray an array containing the command to call and its arguments.
 *
 * @return The output of the executed command.
 *
 * @throws InvalidCommandException If the command is found to be invalid by the operating system.
 */
public static String runCommand(final String[] cmdarray) throws InvalidCommandException {
	return runCommand(cmdarray, new int[0] /*returnCodes*/);
}

/**
 * Runs the specified command and arguments in a separate process.
 *
 * @param cmdarray an array containing the command to call and its arguments.
 * @param returnCodes a list of expected return codes.
 *
 * @return The output of the executed command.
 *
 * @throws InvalidCommandException If the command is found to be invalid by the operating system.
 */
public static String runCommand(final String[] cmdarray, final int[] returnCodes) throws InvalidCommandException {
	try {
		Process process = Runtime.getRuntime().exec(cmdarray);

		InputStream stdErrorInputStream = process.getErrorStream();
		InputStream stdOutInputStream = process.getInputStream();

		String stdout = getContent(stdOutInputStream);
		String stderror = getContent(stdErrorInputStream);

		stdOutInputStream.close();
		stdErrorInputStream.close();

		process.waitFor();
		int exitCode = process.exitValue();
		for(int code : returnCodes) {
			if(exitCode == code) return stdout.trim();
		}
		throw new InvalidCommandException(stdout + "\n" + stderror);
	}
	catch (IOException | InterruptedException e) {
		throw new InvalidCommandException(e);
	}
}

//public static void runCommandInBackground(final String[] cmdarray) throws InvalidCommandException {
//	String silentCommand = "nohup " + command + " > /dev/null 2>&1 &";
//
//	runCommand(silentCommand);
//
//	// Wait a moment
//	sleep(1 /*seconds*/);
//}

public static void setDebugWriter() {
	STR_WRITER = new StringWriter();
	LOG_WRITER = new PrintWriter(STR_WRITER);
}

/**
 * Sleep during the given seconds time.
 *
 * @param seconds The number of seconds to sleep.
 */
public static void sleep(final int seconds) {
	try {
		Thread.sleep(seconds * 1000);
	} catch (InterruptedException ie) {
		// skip
	}
}

/**
 * Returns a string to display the given time as a duration
 * formatted as:
 *	<ul>
 *	<li>"XXXms" if the duration is less than 0.1s (e.g. "43ms")</li>
 *	<li>"X.YYs" if the duration is less than 1s (e.g. "0.43s")</li>
 *	<li>"XX.Ys" if the duration is less than 1mn (e.g. "14.3s")</li>
 *	<li>"XXmn XXs" if the duration is less than 1h (e.g. "14mn 3s")</li>
 *	<li>"XXh XXmn XXs" if the duration is over than 1h (e.g. "1h 4mn 3s")</li>
 *	</ul>
 *
 * @param time The time to format as a long.
 * @return The time as a human readable readable {@link String}.
 */
public static String timeString(final long time) {
	if (time < 100) return time + "ms";

	String timeStr = Duration.of(time, ChronoUnit.MILLIS)
			.toString()
			.substring(2)
			.replaceAll("(\\d[HMS])", "$1 ")
			.toLowerCase()
			.replace("m", "mn")
			.trim();
	return timeStr += timeStr.endsWith("h") ? " 0mn 0s" : timeStr.endsWith("n") ? " 0s" : EMPTY_STRING;
}

/**
 * Convert a string to its byte value.
 *
 * @param size The size string to convert to its byte value.
 * The size must be in the following form: '<numeric> <unit>'.
 * For example, 8 bytes, 10 KB, 4 MB, 50 GB, 20 TB, ...etc.
 *
 * @return The converted byte value.
 */
public static double toBytes(final String size) {
	String[] sizeInfo = size.split(" ");
	String sizeValue = sizeInfo[0];
	String sizeUnit = sizeInfo[1];
	int index = SIZE_UNITS.indexOf(sizeUnit);

	return Integer.parseInt(sizeValue) * Math.pow(1024, index);
}

/**
 * Return a list of {@link String}s from a list of {@link WebElement}s.
 *
 * @param elements the list of web elements to extract text from.
 * @return A list of Strings representing the text of the WebElements
 */
public static List<String> toStrings(final List<? extends WebElement> elements) {
	return ScenarioUtil.toStrings(elements, false);
}

/**
 * Return a list of {@link String}s from a list of {@link WebElement}s.
 *
 * @param elements the list of web elements to extract text from.
 * @param filterEmpty true if you want the list to be filtered of any null or "" elements
 * @return A list of Strings representing the text of the WebElements
 */
public static List<String> toStrings(final List<? extends WebElement> elements, final boolean filterEmpty) {
	List<String> strings = new ArrayList<String>(elements.size());
	for (WebElement webElement : elements) {
		String string = webElement.getText();
		if (!filterEmpty || (string != null && string.length() > 0)) {
			strings.add(string);
		}
    }
	return strings;
}

/**
 * Return the name of the caller from which this method is used.
 */
public static StackTraceElement whoAmI() {
	StackTraceElement[] elements = new Exception().getStackTrace();
	return elements[1]; // Skip first element which is the ScenarioUtil current method
}

/**
 * Return the name of the caller from which this method is used.
 */
public static StackTraceElement[] whoCallsMe() {
	StackTraceElement[] elements = new Exception().getStackTrace();
	StackTraceElement[] callers = new StackTraceElement[elements.length];
	int count = 0, length = 0;
	for (StackTraceElement element : elements) {
		if (count < 2) {
			// Skip the first two elements: ScenarioUtil method and calling
			// method
		} else {
			String elementClassName = element.getClassName();
			if (elementClassName.startsWith(PACKAGE_PREFIX)) {
				callers[length++] = element;
			}
		}
		count++;
	}
	System.arraycopy(callers, 0, callers = new StackTraceElement[length], 0, length);
	return callers;
}
}