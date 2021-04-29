#!/bin/bash

java -cp app/ -Djava.rmi.hostname=localhost -Djava.security.policy=security.policy nfs.metaserver.MetaDataServer
