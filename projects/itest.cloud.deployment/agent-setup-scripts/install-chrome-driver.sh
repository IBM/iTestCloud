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
	exit 1
}

function display_usage() { 
	echo "This script must specify the following arguments:" 
	echo "Usage: $0 \"<chrome-driver-version>\""
	echo "Example: $0 \"97.0.4692.36\""
}

if [  $# -le 0 ]
	then
	display_usage
	exit 1
fi

chromeDriverVersion=$1

echo -e ">>>>>>>>>>>>>> Changing directory to /opt/drivers"
cd /opt/drivers/ || errorExit "Changing directory to /opt/drivers failed, exiting"

echo -e ">>>>>>>>>>>>>> Deleting existing Chrome driver"
rm -f ./chromedriver || errorExit "Deleting existing Chrome driver failed, exiting"

echo -e ">>>>>>>>>>>>>> Downloading Chrome driver archive"
wget https://chromedriver.storage.googleapis.com/${chromeDriverVersion}/chromedriver_linux64.zip || errorExit "Downloading Chrome driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Extracting Chrome driver archive"
unzip ./chromedriver_linux64.zip || errorExit "Extracting Chrome driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Deleting Chrome driver archive"
rm -f ./chromedriver_linux64.zip || errorExit "Deleting Chrome driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Setting execution permission for Chrome driver"
chmod +x ./chromedriver || errorExit "Setting execution permission for Chrome driver failed, exiting"
