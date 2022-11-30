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
	echo "Usage: $0 \"<gecko-driver-version>\""
	echo "Example: $0 \"0.28.0\""
}

if [  $# -le 0 ]
	then
	display_usage
	exit 1
fi

geckoDriverVersion=$1

echo -e ">>>>>>>>>>>>>> Changing directory to /opt/drivers"
cd /opt/drivers/ || errorExit "Changing directory to /opt/drivers failed, exiting"

echo -e ">>>>>>>>>>>>>> Downloading Gecko driver archive"
wget https://github.com/mozilla/geckodriver/releases/download/v${geckoDriverVersion}/geckodriver-v${geckoDriverVersion}-linux64.tar.gz || errorExit "Downloading Gecko driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Deleting existing Gecko driver"
rm -f ./geckodriver || errorExit "Deleting existing Gecko driver failed, exiting"

echo -e ">>>>>>>>>>>>>> Extracting Gecko driver archive"
tar -xzvf ./geckodriver-v${geckoDriverVersion}-linux64.tar.gz || errorExit "Extracting Gecko driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Deleting Gecko driver archive"
rm -f ./geckodriver-v${geckoDriverVersion}-linux64.tar.gz || errorExit "Deleting Gecko driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Setting execution permission for Gecko driver"
chmod +x ./geckodriver || errorExit "Setting execution permission for Gecko driver failed, exiting"
