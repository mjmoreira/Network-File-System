#!/bin/bash

java -cp .:nfs/shared/shared.jar -Djava.rmi.server.codebase=file:/home/miguel/Dropbox/coding_stuff/NetworkFileSystem/src/nfs/shared/shared.jar -Djava.rmi.hostname=localhost -Djava.security.policy=storage-server.policy nfs.storage.StorageServer localhost
