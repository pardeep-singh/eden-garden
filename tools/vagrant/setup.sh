#!/bin/bash

dirname=/home/vagrant/mongodb_setup
if [ -d "$dirname" ]; then
    echo "$dirname Exists"
else
    echo "$dirname doesnt exists, creating now."
    mkdir $dirname
    cd $dirname
    
    echo "Creating data directory and log files"
    mkdir -p $dirname/data/node1
    mkdir -p $dirname/data/node2
    mkdir -p $dirname/data/arbiter
    mkdir -p $dirname/log

    echo "Installing MongoDB"
    wget -nv --tries=2 "https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-3.4.7.tgz"
    tar xvf "mongodb-linux-x86_64-3.4.7.tgz"
    echo "Done with MongoDB Installation"

    echo export PATH="$PATH:/home/vagrant/mongodb_setup/mongodb-linux-x86_64-3.4.7/bin" > /home/vagrant/.bash_profile
fi

source /home/vagrant/.bash_profile

echo "Starting Replica Set Cluster"
mongod --replSet "myapp" --dbpath $dirname/data/node1 --port 40000 --logpath $dirname/log/node1.log --fork
mongod --replSet "myapp" --dbpath $dirname/data/node2 --port 40001 --logpath $dirname/log/node2.log --fork
mongod --replSet "myapp" --dbpath $dirname/data/arbiter --port 40002 --logpath $dirname/log/arbiter.log --fork
