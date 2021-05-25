#!/bin/bash

java -cp app/ -Djava.security.policy=security.policy nfs.storage.StorageServer $1
