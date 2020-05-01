#!/bin/bash

# Try to compile and copy the clp files. If there are no errors, run the program
cp -r src/rect_partition/clp_approaches classes/rect_partition
javac -d classes -sourcepath "src/" -cp "lib/*" src/rect_partition/PartitionProblem.java
if [ $? = "0" ]; then
   . run.sh 
fi
