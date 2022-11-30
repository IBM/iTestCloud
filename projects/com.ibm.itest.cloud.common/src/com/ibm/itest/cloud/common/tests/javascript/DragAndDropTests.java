/*********************************************************************
 * Copyright (c) 2016, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.common.tests.javascript;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.println;

import org.openqa.selenium.By;

import com.ibm.itest.cloud.common.tests.web.FirefoxBrowser;
import com.ibm.itest.cloud.common.tests.web.WebBrowserElement;

@SuppressWarnings("unused")
public class DragAndDropTests {
	private static final String JAVASCRIPT_CODE_FOR_DND = "(function( $ ) {\r\n" +
			"        $.fn.simulateDragDrop = function(options) {\r\n" +
			"                return this.each(function() {\r\n" +
			"                        new $.simulateDragDrop(this, options);\r\n" +
			"                });\r\n" +
			"        };\r\n" +
			"        $.simulateDragDrop = function(elem, options) {\r\n" +
			"                this.options = options;\r\n" +
			"                this.simulateEvent(elem, options);\r\n" +
			"        };\r\n" +
			"        $.extend($.simulateDragDrop.prototype, {\r\n" +
			"                simulateEvent: function(elem, options) {\r\n" +
			"                        /*Simulating drag start*/\r\n" +
			"                        var type = 'dragstart';\r\n" +
			"                        var event = this.createEvent(type);\r\n" +
			"                        this.dispatchEvent(elem, type, event);\r\n" +
			"\r\n" +
			"                        /*Simulating drop*/\r\n" +
			"                        type = 'drop';\r\n" +
			"                        var dropEvent = this.createEvent(type, {});\r\n" +
			"                        dropEvent.dataTransfer = event.dataTransfer;\r\n" +
			"                        this.dispatchEvent($(options.dropTarget)[0], type, dropEvent);\r\n" +
			"\r\n" +
			"                        /*Simulating drag end*/\r\n" +
			"                        type = 'dragend';\r\n" +
			"                        var dragEndEvent = this.createEvent(type, {});\r\n" +
			"                        dragEndEvent.dataTransfer = event.dataTransfer;\r\n" +
			"                        this.dispatchEvent(elem, type, dragEndEvent);\r\n" +
			"                },\r\n" +
			"                createEvent: function(type) {\r\n" +
			"                        var event = document.createEvent(\"CustomEvent\");\r\n" +
			"                        event.initCustomEvent(type, true, true, null);\r\n" +
			"                        event.dataTransfer = {\r\n" +
			"                                data: {\r\n" +
			"                                },\r\n" +
			"                                setData: function(type, val){\r\n" +
			"                                        this.data[type] = val;\r\n" +
			"                                },\r\n" +
			"                                getData: function(type){\r\n" +
			"                                        return this.data[type];\r\n" +
			"                                }\r\n" +
			"                        };\r\n" +
			"                        return event;\r\n" +
			"                },\r\n" +
			"                dispatchEvent: function(elem, type, event) {\r\n" +
			"                        if(elem.dispatchEvent) {\r\n" +
			"                                elem.dispatchEvent(event);\r\n" +
			"                        }else if( elem.fireEvent ) {\r\n" +
			"                                elem.fireEvent(\"on\"+type, event);\r\n" +
			"                        }\r\n" +
			"                }\r\n" +
			"        });\r\n" +
			"})(jQuery);";

public static void main01(final String[] args) {
//	WebDriver driver = new FirefoxBrowser().getDriver();
//	driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//	driver.get("http://www.dhtmlgoodies.com/submitted-scripts/i-google-like-drag-drop/");
//	WebElement source = driver.findElement(By.xpath("//h1[text()='Block 1']"));
//	WebElement target = driver.findElement(By.xpath("//h1[text()='Block 3']"));
//	Actions actions = new Actions(driver);
//	actions.dragAndDrop(source, target).perform();
}

public static void main02(final String[] args) {
	FirefoxBrowser browser = new FirefoxBrowser();
	browser.get("http://www.dhtmlgoodies.com/submitted-scripts/i-google-like-drag-drop/");
	WebBrowserElement source = browser.findElement(By.xpath("//h1[text()='Block 1']"));
	WebBrowserElement target = browser.findElement(By.xpath("//h1[text()='Block 3']"));
	browser.dragAndDrop(source, target);
	browser.close();
}

public static void main03(final String[] args) {
	FirefoxBrowser browser = new FirefoxBrowser();
	browser.get("http://the-internet.herokuapp.com/drag_and_drop");
	WebBrowserElement source = browser.findElement(By.id("column-a"));
	WebBrowserElement target = browser.findElement(By.id("column-b"));
	println("Source text is initially: "+source.getText());
	println("Target text is initially: "+target.getText());
	browser.executeScript(JAVASCRIPT_CODE_FOR_DND+"$('#column-a').simulateDragDrop({ dropTarget: '#column-b'});");
	source = browser.findElement(By.id("column-a"));
	target = browser.findElement(By.id("column-b"));
	println("Source text is now: "+source.getText());
	println("Target text is now: "+target.getText());
	browser.close();
}

public static void main(final String[] args) {
	FirefoxBrowser browser = new FirefoxBrowser();
	browser.get("http://www.dhtmlgoodies.com/submitted-scripts/i-google-like-drag-drop/");
	WebBrowserElement source = browser.findElement(By.xpath("//h1[text()='Block 1']"));
	WebBrowserElement target = browser.findElement(By.xpath("//h1[text()='Block 3']"));
//	browser.dragAndDrop(source, target, Position.Top_Left, Position.Top_Left);
	browser.close();
}
}
