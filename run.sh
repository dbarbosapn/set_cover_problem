#!/bin/bash

. config.properties 
java -cp "classes:lib/*" -Declipse.directory=$eclipseDir -Xmx6G rect_partition.PartitionProblem
