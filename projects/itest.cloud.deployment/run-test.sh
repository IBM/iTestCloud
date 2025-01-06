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

function errorExit () {
	echo "$1" 1>&2
#	exit 1
}

# if [ ${BROWSER} = "FireFox" ]
# then
# 	echo -e ">>>>>>>>>>>>>> Downloading Firefox"
# 	cd /opt || errorExit "Changing dir to /opt failed, exiting"
# 	wget https://ftp.mozilla.org/pub/firefox/releases/78.6.0esr/linux-x86_64/en-US/firefox-78.6.0esr.tar.bz2 || errorExit "Downloading Firefox failed, exiting"

# 	echo -e ">>>>>>>>>>>>>> Extracting Firefox"
# 	tar -xjvf firefox-60.1.0esr.tar.bz2 || errorExit "Extracting Firefox failed, exiting"

# 	echo -e ">>>>>>>>>>>>>> Deleting Firefox installer"
# 	rm -f firefox-60.1.0esr.tar.bz2 || errorExit "Deleting Firefox installer failed, exiting"

# 	echo -e ">>>>>>>>>>>>>> Downloading Gecko driver archive"
# 	wget https://github.com/mozilla/geckodriver/releases/download/v0.28.0/geckodriver-v0.28.0-linux64.tar.gz || errorExit "Downloading Gecko driver archive failed, exiting"

# 	echo -e ">>>>>>>>>>>>>> Extracting Gecko driver archive"
# 	tar -xzvf geckodriver-v0.21.0-linux64.tar.gz || errorExit "Extracting Gecko driver archive failed, exiting"
# 	chmod +x ./geckodriver || errorExit "Setting execution permission for Gecko driver failed, exiting"

# 	echo -e ">>>>>>>>>>>>>> Deleting Gecko driver archive"
# 	rm -f geckodriver-v0.21.0-linux64.tar.gz || errorExit "Deleting Gecko driver archive failed, exiting"

# 	export FF_PATH=firefox/firefox
# 	export FF_DRIVER=geckodriver
# fi

# if [ ${BROWSER} = "Chrome" ]
# then
# 	echo -e ">>>>>>>>>>>>>> Downloading Chrome"
# 	wget -nv https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb || errorExit "Downloading Chrome failed, exiting"

# 	echo -e ">>>>>>>>>>>>>> Installing Chrome"
# 	sudo dpkg -i google-chrome-stable_current_amd64.deb || errorExit "Installing Chrome failed, exiting"

# 	echo -e ">>>>>>>>>>>>>> Downloading Chrome driver archive"
# 	wget -nv https://chromedriver.storage.googleapis.com/89.0.4389.23/chromedriver_linux64.zip || errorExit "Downloading Chrome driver archive failed, exiting"

# 	echo -e ">>>>>>>>>>>>>> Extracting Chrome driver archive"
# 	unzip chromedriver_linux64.zip || errorExit "Extracting Chrome driver archive failed, exiting"
# 	chmod +x ./chromedriver || errorExit "Setting execution permission for Chrome driver failed, exiting"

# 	echo -e ">>>>>>>>>>>>>> Deleting Chrome driver archive"
# 	rm -f chromedriver_linux64.zip || errorExit "Deleting Chrome driver archive failed, exiting"

# 	export CHROME_DRIVER=chromedriver
# fi

if [ ${BROWSER} == "Random" ]
then
	D=$(date +%d)
	R=$(expr ${D} % 2)
 	if [ ${R} -eq 0 ]
 	then
 		export BROWSER="FireFox"
 	else
 		export BROWSER="Chrome"
 	fi
 	echo -e ">>>>>>>>>>>>>> ${BROWSER} was selected as browser to run tests"
fi

#echo -e ">>>>>>>>>>>>>> Creating /etc/hosts file"
#sudo rm -f /etc/hosts
#sudo cp repos/com.ibm.itest.cloud.deployment/hosts /etc/hosts

export CLASSPATH=projects/com.ibm.itest.cloud.common/libs/junit-4.12.jar:projects/com.ibm.itest.cloud.common/libs/hamcrest-core-1.3.jar || errorExit "Defining class path failed, exiting"

cp projects/itest.cloud.deployment/build.xml build.xml || errorExit "Copying build.xml failed, exiting"

ant run -Dtest=${1} -Dlocale="${LOCALE}" -Denvironment="${ENVIRONMENT}" -Dprefix="${PREFIX}" -Dparams="${PARAMS}" -Dheadless=true -Dperformance="${PERFORMANCE}" || errorExit "Invoking test scenario failed, exiting"

if [[ -z ${ARTIFACTORY_ACCOUNT} ]];
then
	echo "ARTIFACTORY_ACCOUNT not set; skipping upload..."
else
	LOG_ZIP=${BUILD_NUMBER}.zip || errorExit "Defining name of test results archive failed, exiting"
	zip -r ${LOG_ZIP} build/report.xml build/debug/ || errorExit "Zipping test results failed, exiting"
	curl -u ${ARTIFACTORY_ACCOUNT} -X PUT https://na.artifactory.swg-devops.com/artifactory/wcp-itestcloud-results-generic-local/${2}/${LOG_ZIP} -T ${LOG_ZIP}
fi
