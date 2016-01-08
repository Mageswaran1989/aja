#!/usr/bin/env bash
echo "Add Java ubuntu repository..."
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
echo "Install Sun Java 8..."
sudo apt-get install oracle-java8-installer
echo "Select the Java SDK you wanted:"
sudo update-alternatives --config java
echo "Select the Java Compiler you wanted:"
sudo update-alternatives --config javac
echo "Select the Java Header you wanted:"
sudo update-alternatives --config javah
