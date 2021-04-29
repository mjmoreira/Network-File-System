#!/bin/bash

echo "Removing old application build files"
rm build/app/nfs/*/*.class

echo "----"
echo "Building application files ..."
javac -Xlint:all -d build/app --source-path src/app src/app/nfs/*/*.java && echo "Done."

echo "----"
echo "TODO: generate jars"

echo "----"
echo "Removing old test build files"
rm build/test/nfs/*/*.class

echo "----"
echo "Building test files ..."
javac -Xlint:all -d build/test -cp junit-platform-console-standalone-1.7.1.jar:build/app/ \
  src/test/nfs/*/*.java && echo "Done"
