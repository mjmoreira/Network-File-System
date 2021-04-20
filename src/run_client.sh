#!/bin/bash

java -cp .:nfs/shared/shared.jar -Djava.rmi.server.codebase=file:/home/miguel/Dropbox/coding_stuff/NetworkFileSystem/src/ -Djava.security.policy=client.policy nfs.client.Client localhost
