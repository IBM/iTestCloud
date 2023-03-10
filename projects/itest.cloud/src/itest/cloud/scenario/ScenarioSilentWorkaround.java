/*********************************************************************
 * Copyright (c) 2014, 2022 IBM Corporation and others.
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

import itest.cloud.pages.Page;
import itest.cloud.pages.dialogs.AbstractDialog;

/**
 * Manage a workaround applied when running a scenario.
 * <p>
 * Unlike {@link ScenarioWorkaround}, the application of the workaround will be kept
 * in silence. In other words, neither a warning snapshot will be created nor a
 * message will be displayed in the console to as a part of this workaround.
 * </p>
 */
public abstract class ScenarioSilentWorkaround<P extends Page> extends ScenarioWorkaround<P> {

public ScenarioSilentWorkaround(final P page, final String msg) {
	this(page, msg, true, null);
}

public ScenarioSilentWorkaround(final P page, final String msg, final boolean fail) {
	this(page, msg, fail, null);
}

public ScenarioSilentWorkaround(final P page, final String msg, final boolean fail, final AbstractDialog dialog) {
	super(page, msg, fail, dialog, false /* report */);
}

}
