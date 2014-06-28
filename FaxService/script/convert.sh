#!/bin/sh
#
# convert.sh
#
# Copyright (c) 2013-2014 Andrew D. Bucko <adb@trekologer.net>
# 
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

/usr/bin/gs -dBATCH -dNOPAUSE -sDEVICE=tiffg3 -sPAPERSIZE=letter -r204x196 -dAutoRotatePages -g1728x2156 -sOutputFile=$2 $1