#!/bin/bash

java -jar ../junit-platform-console-standalone-1.7.1.jar -cp test/:app/ \
  -p nfs.shared -p nfs.filesystem