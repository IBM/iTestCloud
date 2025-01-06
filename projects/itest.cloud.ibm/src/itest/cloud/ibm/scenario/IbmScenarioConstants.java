/*********************************************************************
 * Copyright (c) 2018, 2024 IBM Corporation and others.
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
package itest.cloud.ibm.scenario;

import org.openqa.selenium.By;

import itest.cloud.scenario.ScenarioDataConstants;

/**
 * Common utility methods.
 */
public interface IbmScenarioConstants extends ScenarioDataConstants {

	public final By DIALOG_CONTENT_ELEMENT_LOCATOR = By.xpath(".//div[contains(@class,'modal-content')]");
	public final By DIALOG_LOCATOR = By.xpath("//*[@role='dialog']");
	public final By DIALOG_TITLE_ELEMENT_LOCATOR = By.xpath(".//*[contains(@class,'heading')]");
	public final By CONTEXT_MENU_EXPANSION_LOCATOR = By.xpath(".//*[contains(@class,'overflow')]");
}