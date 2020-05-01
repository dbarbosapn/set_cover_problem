#!/bin/bash

. config.properties 
java -cp "classes:lib/*" -Declipse.directory=$eclipseDir rect_partition.PartitionProblem
