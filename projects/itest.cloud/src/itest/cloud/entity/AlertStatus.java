/*********************************************************************
 * Copyright (c) 2024, 2025 IBM Corporation and others.
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
package itest.cloud.entity;

import itest.cloud.page.element.AlertElement;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This enum represents the statuses of an alert element {@link AlertElement}.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public enum AlertStatus {

	Success, Failure, Warning, Error,
	Information("info"),
	NoSeverity("no-severity");

	/**
	 * Return the service type of representing a given label.
	 *
	 * @param label The label of the service type.
	 *
	 * @return The service type of representing a given label as {@link AlertStatus}.
	 */
	public static final AlertStatus toEnum(final String label) {
		final String lowercaseLabel = label.toLowerCase();

		for (AlertStatus notificationStatus : values()) {
			if (((notificationStatus.label != null) && lowercaseLabel.contains(notificationStatus.label.toLowerCase())) ||
				(lowercaseLabel.contains(notificationStatus.name().toLowerCase()))) {
				return notificationStatus;
			}
		}

		throw new ScenarioFailedError("Notification status '" + label + "' is unrecognized by this method");
	}

	final String label;

AlertStatus() {
	this.label = null;
}

AlertStatus(final String label) {
	this.label = label;
}

/**
 * Return the label of this enum constant.
 *
 * @return the label of this enum constant.
 */
public String getLabel() {
	return (this.label != null) ? this.label : name();
}
}