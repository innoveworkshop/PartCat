#!/bin/bash

### build_icon.sh
### Builds the icon for the Windows version.
###
### Author: Nathan Campos <nathan@innoveworkshop.com>

svgfile=icon.svg
icofile=icon.ico
intfn=icon-
size=(16 32 24 48 72 96 144 152 192 196 256)

echo "Creating PNGs for the various icon sizes..."
for i in ${size[@]}; do
	echo "    Building ${i}x${i} icon"
	inkscape $svgfile --export-png="$intfn$i.png" -w$i -h$i --without-gui
	#convert $svgfile -resize ${i}x${i} $intfn$i.png
done

echo "Compressing PNGs..."
pngquant -f --ext .png $intfn*.png --posterize 4 --speed 1

echo "Creating finalized icon..."
convert $(ls -v $intfn*.png) $icofile

echo "Cleaning up..."
rm $intfn*.png

echo "Done!"
