/*********************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
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
package itest.cloud.ibm.config;

import static itest.cloud.ibm.config.IbmTimeouts.CATALOG_PROCESSING_COMPLETION_TIMEOUT;

import itest.cloud.config.Config;
import itest.cloud.ibm.topology.IbmTopology;

/**
 * This class defines and manages the configuration.
 * <p>
 * </p>
 */
public class IbmConfig extends Config implements IbmConstants {

public IbmConfig() {
	super();
}

/**
 * Return the timeout used to wait for the processing of the assets in
 * a catalog to be completed.<br>
 * This time is expressed in <b>minutes</b>.
 *
 * @return The timeout as a <code>int</code>.
 */
public int getCatalogProcessingCompletionTimeout() {
	return CATALOG_PROCESSING_COMPLETION_TIMEOUT;
}

/**
 * {@inheritDoc}
 * <p>
 * Set the timeouts as {@link IbmTimeouts} object.
 * </p>
 */
@Override
protected void initTimeouts() {
	// Init timeouts
	this.timeouts = new IbmTimeouts();
}

@Override
protected void initTopology() {
	this.topology = new IbmTopology();
}
}