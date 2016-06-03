#!/usr/bin/env bash
echo "Get the latest sbt"
wget  https://bintray.com/artifact/download/sbt/debian/sbt-0.13.9.deb
echo "Install sbt..."
sudo dpkg -i sbt-0.13.9.deb
sudo apt-get update
sudo apt-get install sbt
