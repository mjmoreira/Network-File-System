#!/bin/bash

## precisa de mais argumentos
# java -jar ~/Downloads/junit-platform-console-standalone-1.7.1.jar -cp test/:src/ -c nfs.storage.StorageTest

# java -Djava.rmi.server.codebase=file:/home/miguel/Dropbox/coding_stuff/NetworkFileSystem/src/nfs/shared/shared.jar -Djava.rmi.hostname=localhost -Djava.security.policy=test/security.policy -jar ~/Downloads/junit-platform-console-standalone-1.7.1.jar -cp test/:src/ -p nfs

java -Djava.rmi.server.codebase=file:/home/miguel/Dropbox/coding_stuff/NetworkFileSystem/src/nfs/shared/shared.jar -Djava.rmi.hostname=localhost -Djava.security.policy=security.policy -jar junit-platform-console-standalone-1.7.1.jar -cp .:../src/ -c nfs.NFSTest