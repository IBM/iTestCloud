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
package itest.cloud.acme.config;

import static itest.cloud.acme.config.AcmeTimeouts.CATALOG_PROCESSING_COMPLETION_TIMEOUT;

import itest.cloud.acme.topology.AcmeTopology;
import itest.cloud.config.Config;

/**
 * This class defines and manages the configuration.
 * <p>
 * </p>
 */
public class AcmeConfig extends Config implements AcmeConstants {

public AcmeConfig() {
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
 * Set the timeouts as {@link AcmeTimeouts} object.
 * </p>
 */
@Override
protected void initTimeouts() {
	// Init timeouts
	this.timeouts = new AcmeTimeouts();
}

@Override
protected void initTopology() {
	this.topology = new AcmeTopology();
}
}