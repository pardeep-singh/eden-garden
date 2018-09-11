# Product CRUD APIs service written in Clojure using MongoDB Replica Set Cluster

REST API service to Add, Update, Fetch and Query products from MongoDB Replica Set Cluster. This is based on Ecommerce Application example from
[MongoDB in Action](https://www.manning.com/books/mongodb-in-action) book.


## Prerequisites

You will need [Java](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html), [Leiningen](https://leiningen.org/),
[Vagrant](https://www.vagrantup.com/intro/getting-started/install.html) to run this project.


## Running

### Start MongoDB Cluster

`tools/vagrant` directory contains a [VagrantFile](tools/vagrant/VagrantFile) with [setup script](tools/vagrant/setup.sh) to provision a vagrant box with MongoDB installation.
It will also spin up a MongoDB Replica Set Cluster with 3 mongod instances. Follow below commands to start vagrant box:
```
cd tools/vagrant
vagrant up
vagrant ssh
```

Now connect to mongodb client and initiate the Replica Set Cluster:
```
mongo --port 40000
> rs.initiate()
```

Add other nodes to cluster. We will add one node as secondary other as arbiter.
```
myapp:OTHER> rs.add("mongodb-cluster:40001")
myapp:PRIMARY> rs.add("mongodb-cluster:40002",{arbiterOnly:true})
```

Verify the cluster state. There should be 3 members(primary, secondary, arbiter) in cluster.
```
myapp:PRIMARY> rs.status();
```

Now add mongodb-cluster entry to your local machine /etc/hosts file
```
echo "192.241.33.25  mongodb-cluster" >> /etc/hosts
```

NOTE: While restarting Vagrant box,  pass --provision flag to vagrant up command as below. This is required to start Mongodb Cluster on box reboot.
```
vagrant up --provision
```

### Using Lein
To start a web server for the application, run:
```
lein run
```

This will start the server on Port 9099. Run below curl command to verify
```
curl http://localhost:9099/ping
```

### Using Java
Build the Jar using below command
```
lein uberjar
```
and then run the following command to start the API server
```
java -jar target/eden-garden-0.1.0-SNAPSHOT-standalone.jar
```
Use the curl command mentioned above to verify.

### Using Postman to use APIs
Export the <a href="/tools/eden-garden.postman_collection.json">Postman Collection</a> to Postman. This collections
includes the sample request for all the APIs.
