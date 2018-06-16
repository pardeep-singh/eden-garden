#!/bin/bash

dirname=~/mongodb_setup
if [ -d "$dirname" ]; then
    echo "$dirname Exists"
else
    echo "$dirname doesnt exists, creating now."
    mkdir $dirname
    cd $dirname
    
    echo "Creating data directory and log file"
    mkdir -p $dirname/data/node1
    mkdir -p $dirname/data/node2
    mkdir -p $dirname/data/arbiter
    mkdir -p $dirname/log

    echo "Installing MongoDB"
    #wget -nv "https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-3.4.7.tgz"
    #tar xvf "mongodb-linux-x86_64-3.4.7.tgz"
    sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 0C49F3730359A14518585931BC711F9BA15703C6
    echo "deb [ arch=amd64,arm64 ] http://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.4.list

    sudo apt-get update
    sudo apt-get install -y mongodb-org
    sudo get-get install -y mongodb-org-server
    echo "Done with MongoDB Installation"
fi

#mongo_path=":/home/vagrant/mongodb_setup/mongodb-linux-x86_64-3.4.7/bin"
which mongo
which mongod

echo "Starting Replica Set Cluster"
/sbin/service mongod --replSet "myapp" --dbpath $dirname/data/node1 --port 40000 --logpath $dirname/log/node1.log --fork
/sbin/service mongod --replSet "myapp" --dbpath $dirname/data/node2 --port 40001 --logpath $dirname/log/node2.log --fork
/sbin/service mongod --replSet "myapp" --dbpath $dirname/data/arbiter --port 40002 --logpath $dirname/log/arbiter.log --fork
