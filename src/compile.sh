#!/bin/bash

echo "Compile nfs.shared"
javac nfs/shared/*.java && echo "Done."

echo "Create shared.jar "
jar cvf nfs/shared/shared.jar nfs/shared/*.class && echo "Done."

echo "Compile nfs.metaserver"
javac -cp nfs/shared/shared.jar nfs/metaserver/MetaDataServer.java && echo "Done."

echo "Compile nfs.client"
javac -cp nfs/shared/shared.jar nfs/client/Client.java && echo "Done."

echo "Compile nfs.storage"
javac -cp nfs/shared/shared.jar nfs/storage/StorageServer.java && echo "Done."
