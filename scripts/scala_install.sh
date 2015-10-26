#!/usr/bin/env bash
echo "Remove any older version"
sudo apt-get remove scala-library scala
echo "Download the latest Scala..."
sudo wget www.scala-lang.org/files/archive/scala-2.11.7.deb
echo "Install Scala..."
sudo dpkg -i scala-2.11.7.deb
sudo apt-get update
sudo apt-get install scala