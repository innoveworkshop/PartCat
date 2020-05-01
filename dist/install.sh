#!/bin/sh

### install.sh
### The simplest way to install this under any flavor of Linux.
###
### Author: Nathan Campos <nathan@innoveworkshop.com>

# Environment variables.
instdir=/opt/partcat
bindir=/usr/bin
deskdir=/usr/share/applications

if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root" 
   exit 1
fi

# Make sure we have a installation directory.
echo "Installing to $instdir"
mkdir -p "$instdir"

# Copy files to installation directory.
for f in *; do
	echo "Copying $f"
	cp -f "$f" "$instdir"
done

# Create system-wide executable.
echo "Creating a system-wide executable at $bindir"
ln -s "$instdir/partcat" "$bindir/partcat"

# Create a desktop file.
echo "Copying desktop file to $deskdir"
ln -s "$instdir/PartCat.desktop" "$deskdir/PartCat.desktop"

echo "Done!"

