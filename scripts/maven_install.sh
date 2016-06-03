#!/bin/sh

sudo apt-get purge -y maven
if ! [ -e .cache/apache-maven-3.3.3-bin.tar.gz ]; then (mkdir cache; cd cache; curl -OL http://mirror.olnevhost.net/pub/apache/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz); fi
sudo tar -zxf cache/apache-maven-3.3.3-bin.tar.gz -C /usr/local/
sudo ln -s /usr/local/apache-maven-3.3.3/bin/mvn /usr/bin/mvn
#echo "Maven is on version `mvn -v`"
#echo "export M2_HOME=/usr/local/apache-maven-3.3.3" >> ~/.profile
#. ~/.profile