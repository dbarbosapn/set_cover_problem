#!/bin/bash

# Try to compile. If there are no errors, run the program
javac rect_partition/*.java
if [ $? = "0" ]; then
    java rect_partition.PartitionProblem
fi