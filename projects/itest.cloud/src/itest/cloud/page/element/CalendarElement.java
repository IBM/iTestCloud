/*********************************************************************
 * Copyright (c) 2016, 2023 IBM Corporation and others.
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
package itest.cloud.page.element;

import java.time.LocalDate;
import java.time.Month;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.page.Page;

/**
 * This class represents a generic calendar element and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #select(LocalDate)}: Select a given data from the calendar.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * </p>
 */
public class CalendarElement extends DynamicExpandableElement {

public CalendarElement(final Page page, final String id) {
	super(page, By.xpath("//*[contains(@class,'calendar')]"), By.xpath("//*[contains(@class,'date-picker__input') and contains(@id, '" + id + "')]"));
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

@Override
protected By getTitleElementLocator() {
	return null;
}

/**
 * Select a given data from the calendar.
 *
 * @param date The date to be selected as {@link LocalDate}.
 */
public void select(final LocalDate date) {
	expand();

	// Check if the desired date is today and set it appropriately.
	if(date.equals(LocalDate.now())) {
		waitForElement(By.xpath(".//*[contains(@class, 'today')]")).click();
	}
	// Otherwise set the desired date.
	else {
		setYear(date.getYear());
		setMonth(date.getMonth());
		setDay(date.getDayOfMonth());
	}
}

private void setDay(final int day) {
	waitForElement(By.xpath(".//*[contains(@class, 'flatpickr-day bx--date') and text() = '" + day + "']")).click();
}

private void setMonth(final Month month) {
	int difference = month.getValue() - LocalDate.now().getMonthValue();
	while(difference > 0) {
		waitForElement(By.xpath(".//*[contains(@class, 'next-month')]")).click();
		difference -= 1;
	}
	while(difference < 0) {
		waitForElement(By.xpath(".//*[contains(@class, 'prev-month')]")).click();
		difference += 1;
	}
}

private void setYear(final int year) {
	typeText(this.element, By.xpath(".//*[contains(@class, 'cur-year')]"), Integer.toString(year));
}
}
