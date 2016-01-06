#Installing Neo4j on Ubuntu
cd ~
wget -O - http://debian.neo4j.org/neotechnology.gpg.key >> key.pgp
sudo apt-key add key.pgp
echo 'deb http://debian.neo4j.org/repo stable/' | sudo tee -a /etc/apt/sources.list.d/neo4j.list > /dev/null
sudo apt-get update && sudo apt-get install neo4j

sudo service neo4j-service (stop|start|restart)


#Editor 
http://127.0.0.1:7474

#Install Docker
sudo apt-get install docker.io

#Neo4j + Spark = MazeRunner
http://www.kennybastani.com/2014/11/using-apache-spark-and-neo4j-for-big.html
https://github.com/kbastani/neo4j-mazerunner