#######################################################################
# Copyright (c) 2022 IBM Corporation and others.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#######################################################################

#!/bin/bash

function releaseRepo {
	git checkout -b "${BRANCH}"
	git push origin "${BRANCH}" --force
}

function release {
	git clone https://github.ibm.com/iTestCloud/${REPO}.git ${REPO}
	cd ${REPO}
	BRANCH="qa"
	releaseRepo;
	# BRANCH="ys1prod"
	# releaseRepo;
	BRANCH="production"
	releaseRepo;
	cd ..
	rm -rf ./${REPO}
}

REPO="com.ibm.itest.cloud.apsportal"
release;
REPO="com.ibm.itest.cloud.apsportal.project"
release;
REPO="com.ibm.itest.cloud.apsportal.rstudio"
release;
REPO="com.ibm.itest.cloud.apsportal.notebook"
release;
REPO="com.ibm.itest.cloud.apsportal.machine.learning"
release;
REPO="com.ibm.itest.cloud.apsportal.sanity"
release;
REPO="com.ibm.itest.cloud.apsportal.community"
release;
REPO="com.ibm.itest.cloud.apsportal.dashboard"
release;
REPO="com.ibm.itest.cloud.apsportal.modeler.flow"
release;
REPO="com.ibm.itest.cloud.apsportal.experiment"
release;
REPO="com.ibm.itest.cloud.apsportal.data.assets"
release;
REPO="com.ibm.itest.cloud.apsportal.connection"
release;
REPO="com.ibm.itest.cloud.apsportal.autoai"
release;
REPO="com.ibm.itest.cloud.apsportal.catalog.integration"
release;
REPO="com.ibm.itest.cloud.apsportal.orchestration.flow"
release;
REPO="com.ibm.itest.cloud.apsportal.open.scale"
release;
REPO="com.ibm.itest.cloud.apsportal.space"
release;
REPO="com.ibm.itest.cloud.apsportal.operations.view"
release;
REPO="com.ibm.itest.cloud.apsportal.governance"
release;
REPO="com.ibm.itest.cloud.apsportal.lineage"
release;
