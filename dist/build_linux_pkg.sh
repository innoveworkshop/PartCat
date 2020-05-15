#!/bin/bash

### build_linux_pkg.sh
### Builds a simple .tar.gz package for Linux.
###
### Author: Nathan Campos <nathan@innoveworkshop.com>

relver=$1

if [[ $# -lt 1 ]]; then
	echo "Usage: $0 version"
	echo
	echo -e "    version\tVersion number of the release."
	echo

	exit 1
fi

tar czvf PartCat-$relver-linux.tar.gz \
	icon.svg \
	install.sh \
	partcat \
	PartCat.desktop \
	PartCat.jar

