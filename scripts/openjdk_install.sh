#!/usr/bin/env bash
echo "you may or may not want to remove open-jdk (not necessary):"
sudo apt-get purge openjdk*
echo " to add PPA source repository to apt:"
sudo add-apt-repository ppa:webupd8team/java
echo "to refresh the sources list:"
sudo apt-get update
echo "to install JDK 7"
sudo apt-get install oracle-java7-installer
