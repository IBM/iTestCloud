/*********************************************************************
 * Copyright (c) 2013, 2022 IBM Corporation and others.
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
package itest.cloud.performance;

import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;

/**
 * Timer class, for gathering execution/response times.
 * <p>
 * The timer class allows for simple gathering of execution/response times in seconds.
 * The class measures using nano time so there is no dependency on the system clock.
 * <ul>
 * <li>{@link #startTime}: nano time when the timer was started</li>
 * <li>{@link #endTime}: nano time when the timer was ended</li>
 * <li>{@link #timeDateStamp}: time date stamp when the timer was started</li>
 * </ul>
 * </p>
 */
public class Timer {

// Global Variables
private long startTime = 0;
private long endTime = 0;
private long timeDateStamp = 0;


/**
 * Start timer.
 */
public void start() {
    this.startTime = System.nanoTime();
    this.timeDateStamp = System.currentTimeMillis();
}

/**
 * End timer.
 */
public void end() {
    this.endTime = System.nanoTime();
}

/**
 * Return the time/date stamp taken when the timer last started.
 *
 * @return The time/date stamp as a long.
 */
public long getTimeDateStamp() {
	return this.timeDateStamp;
}

/**
 * Return the total execution time as seconds.  Note the code uses nano time and must divide by 1 billion to get seconds.
 * We also use the decimal format class to crop the results to three places.
 *
 * @return The total execution time as a double.
 */
public double getTotalTime() {

	// If start time was never started then the total time is invalid and should be sent back as 0
	if (this.startTime == 0) return 0;

	// Otherwise the totalTime is valid and should be caculated
	return round(((this.endTime - this.startTime)/1000000000.00),2);
}

/**
 * Reset timer values to 0.
 */
public void reset() {
    this.startTime = 0;
	this.endTime = 0;
	this.timeDateStamp = 0;
}

/**
 * Return the input double with precision places rounded up
 *
 * @return The double rounded to precision places.
 */
public static double round(final double unrounded, final int precision) {
    BigDecimal bigDec = new BigDecimal(unrounded);
    BigDecimal scale = bigDec.setScale(precision, HALF_UP);
    return scale.doubleValue();
}

}