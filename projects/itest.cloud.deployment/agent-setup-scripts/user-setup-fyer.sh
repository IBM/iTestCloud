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

echo -e ">>>>>>>>>>>>>> Creating Jenkins dir"
mkdir /home/automation/jenkins || errorExit "Creating Jenkins dir failed, exiting"

echo -e ">>>>>>>>>>>>>> Downloading agent.jar"
cd /home/automation/jenkins || errorExit "Changing dir to /home/automation/jenkins failed, exiting"
wget http://sottdevperf1.fyre.ibm.com:8080/jnlpJars/agent.jar || errorExit "Downloading agent.jar failed, exiting"

echo -e ">>>>>>>>>>>>>> Creating VNC session and setting password"
vncserver :1 -geometry 1920x1080 || errorExit "Creating VNC session and setting password failed, exiting"

echo -e ">>>>>>>>>>>>>> ToDo by hand"
echo -e "1. Connect to VNC session :1"
echo -e "2. Set screen resolution to 1920x1080"
echo -e "3. Start FireFox and disable automatic updating"
