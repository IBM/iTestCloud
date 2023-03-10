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
	echo "Usage: $0 \"<firefox-version>\""
	echo "Example: $0 \"91.5.0esr\""
}

if [  $# -le 0 ]
	then
	display_usage
	exit 1
fi

firefoxVersion=$1

echo -e ">>>>>>>>>>>>>> Downloading Firefox"
cd /opt || errorExit "Changing dir to /opt failed, exiting"
wget https://ftp.mozilla.org/pub/firefox/releases/${firefoxVersion}/linux-x86_64/en-US/firefox-${firefoxVersion}.tar.bz2 || errorExit "Downloading Firefox failed, exiting"

echo -e ">>>>>>>>>>>>>> Deleting existing Firefox"
rm -rf ./firefox || errorExit "Deleting existing Firefox failed, exiting"

echo -e ">>>>>>>>>>>>>> Extracting Firefox"
tar -xjvf ./firefox-${firefoxVersion}.tar.bz2 || errorExit "Extracting Firefox failed, exiting"

echo -e ">>>>>>>>>>>>>> Deleting Firefox installer"
rm -f ./firefox-${firefoxVersion}.tar.bz2 || errorExit "Deleting Firefox installer failed, exiting"
