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
package itest.cloud.config;

import static itest.cloud.config.Timeouts.Performance.AVERAGE;
import static itest.cloud.scenario.ScenarioUtils.getParameterIntValue;
import static itest.cloud.scenario.ScenarioUtils.getParameterValue;

/**
 * General timeouts used while running a scenario.
 * <p>
 * Current available timeouts are:
 * <ul>
 * <li><code>"timeout"</code>: {@link #DEFAULT_TIMEOUT}</li>
 * <li><code>"timeoutShort"</code>: {@link #SHORT_TIMEOUT}</li>
 * <li><code>"timeoutToOpenPage"</code>: {@link #OPEN_PAGE_TIMEOUT}</li>
 * <li><code>"timeoutCloseDialog"</code>: {@link #CLOSE_DIALOG_TIMEOUT}</li>
 * <li><code>"delayAfterClickLink"</code>: {@link #DELAY_AFTER_CLICK_LINK_TIMEOUT}</li>
 * <li><code>"delayBeforeClickLink"</code>: {@link #DELAY_BEFORE_CLICK_LINK_TIMEOUT}</li>
 * </ul>
 * </p>
 */
abstract public class Timeouts {

	/**
	 * This enum represents the performance of the application-under-test.
	 */
	public enum Performance {
		AVERAGE(1),
		SLOW(2),
		SNAIL(3);

		final int multiplier;

		Performance(final int multiplier) {
			this.multiplier = multiplier;
		}

		/**
		 * Get the multiplier.
		 *
		 * @return the multiplier.
		 */
		public int getMultiplier() {
			return this.multiplier;
		}
	}

	// Constants
	public static final Performance PERFORMANCE = Performance.valueOf(getParameterValue("performance", AVERAGE.name()));
	private final static int DEFAULT_MAIN_TIMEOUT = 60  * PERFORMANCE.multiplier;

	/**
	 * Default timeout used all over the framework (e.g. while searching a web element
	 * in a page).
	 * <p>
	 * The value is 60 seconds.
	 * </p>
	 */
	static final public int DEFAULT_TIMEOUT = getParameterIntValue("timeout", DEFAULT_MAIN_TIMEOUT);

	/**
	 * Short timeout used while searching web element.
	 * <p>
	 * The value is 10 seconds.
	 * </p>
	 */
	static final public int SHORT_TIMEOUT = getParameterIntValue("timeoutShort", 10 * PERFORMANCE.multiplier);

	/**
	 * Momentary timeout used while searching web element.
	 * <p>
	 * The value is 1 seconds.
	 * </p>
	 */
	static final public int TINY_TIMEOUT = getParameterIntValue("timeoutTiny", (PERFORMANCE == Performance.SLOW) ? 1 : 0);

	/**
	 * Timeout used to wait for a page to be opened.
	 * <p>
	 * The value is 30 seconds.
	 * </p>
	 */
	static final public int OPEN_PAGE_TIMEOUT = getParameterIntValue("timeoutToOpenPage", 30 * PERFORMANCE.multiplier);

	/**
	 * Timeout used to wait for a dialog to be closed.
	 * <p>
	 * The value is the same than {@link #OPEN_PAGE_TIMEOUT}.
	 * </p>
	 */
	static final public int CLOSE_DIALOG_TIMEOUT = getParameterIntValue("timeoutCloseDialog", OPEN_PAGE_TIMEOUT);

	/**
	 * Timeout used to wait for the downloading of a file to start.
	 * <p>
	 * The value is 120 seconds (2 minutes).
	 * </p>
	 */
	static final public int DOWNLOAD_START_TIMEOUT = getParameterIntValue("timeoutDownloadStart", 2 * 60 * PERFORMANCE.multiplier);

	/**
	 * Timeout used to a delay after having clicked on a link element.
	 * <p>
	 * Expressed in milli-seconds, default value is 0.
	 * </p>
	 */
	final public static int DELAY_AFTER_CLICK_LINK_TIMEOUT = getParameterIntValue("delayAfterClickLink", 0 * PERFORMANCE.multiplier);

	/**
	 * Timeout used to a delay before clicking on a link element.
	 * <p>
	 * Expressed in milli-seconds, default value is 1000.
	 * </p>
	 */
	final public static int DELAY_BEFORE_CLICK_LINK_TIMEOUT = getParameterIntValue("delayBeforeClickLink", 1000 * PERFORMANCE.multiplier);
}