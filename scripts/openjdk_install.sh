#!/usr/bin/env bash
echo "you may or may not want to remove open-jdk (not necessary):"
sudo apt-get purge openjdk*
#sudo apt-get remove openjdk-7-jre default-jre default-jre-headless
echo " to add PPA source repository to apt:"
sudo add-apt-repository ppa:webupd8team/java
echo "to refresh the sources list:"
sudo apt-get update
echo "to install JDK 7"
sudo apt-get install oracle-java7-installer

#Prefer Sun Java 8 for now
#sudo add-apt-repository ppa:openjdk-r/ppa
#sudo apt-get update
#sudo apt-get install openjdk-8-jdk
#sudo update-alternatives --config java
#sudo update-alternatives --config javac
