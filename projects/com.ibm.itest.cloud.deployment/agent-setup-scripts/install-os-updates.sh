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

echo -e ">>>>>>>>>>>>>> Updating installed packages"
yum -y update || errorExit "Updating installed packages failed, exiting"

echo -e ">>>>>>>>>>>>>> Available Chrome driver version="`/opt/drivers/chromedriver --version`

echo -e ">>>>>>>>>>>>>> Available Chrome version="`google-chrome --version`

echo -e ">>>>>>>>>>>>>> Rebooting"
reboot
