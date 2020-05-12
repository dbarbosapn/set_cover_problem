# Minimum Set Cover

A cool AI project to solve the Minimum Set Cover problem for partitions of a rectangle.
The objective is to cover all the rectangles we are expected to by using the minimum number of vertexes possible.

## How It Works

This is a tool developed in **Java** and **ECLiPSe CLP** using several approaches to the problem. To use the tool, you can run the _run.sh_ script and follow the given steps to choose a method and input file. In the first time, you must compile it with _compile_and_run.sh_

## The Input Files

The input files **must** use the following format:

- In the first line, we have an integer with the number of problem instances in the file.

Then, for each instance, we have:

- An integer representing the number of rectangles

Then, for each rectangle, we have:

- The rectangle identifier

- An integer representing the number of vertexes that cover that rectangle

Then, for each vertex, we have:

- Two integers, representing their coordinates.

Finally at the end of the file, we have:

- An integer representing the number of rectangles to cover followed by their identifiers

In the file [data1.txt](./data1.txt) we have a very small example corresponding to the following image.

![data1.txt](.img/data1.png)

## The Properties File

In the [config.properties](./config.properties) file we can set a few properties for the problem. This is useful to see how results can vary according to different configurations.

- **eclipseDir**: The directory of ECLiPSe CLP. **This configuration is mandatory. The software will not work without it**
- **IDDFSinitialDepth**: The initial depth of the _iterative deepening depth-first search_ approach. (Default 0)
- **ILSiterations**: The number of iterations of the _iterated local search_ approach. (Default 500)
- **ILSvertRemovePercentage**: The percentage of verts that can be removed in the pertubation step of _iterated local search_. (Default 18)
- **ILSvertAddPercentage**: The percentage of verts that can be added in the pertubation step of _iterated local search_. (Default 14)
- **ILSprobWrongAccept**: The probability of accepting a wrong solution in the accepting step of _iterated local search WITH randomization_. (Default 0.2)
- **SAinitialTemperature**: The initial temperature of the _simulated annealing_ approach. (Default 10000)
- **SAcoolingRate**: The cooling rate of the _simulated annealing_ approach. (Default 0.003)
- **SAvertRemovePercentage**: The percentage of verts that can be removed in the pertubation step of _simulated annealing_. (Default 18)
- **SAvertAddPercentage**: The percentage of verts that can be added in the pertubation step of _simulated annealing_. (Default 14)
- **CLPselectionMethod**: The selection method of the CLP search (Default input_order)
- **CLPchoiceMethod**: The choice method of the CLP search (Default indomain)
- **CLPsearchMethod**: The choice method of the CLP search (Default complete)
- **timeout**: Limit time for execution per instance in seconds (Default 120)

For the last **3** properties (CLP), please refer to [http://eclipseclp.org/doc/bips/lib/ic/search-6.html](http://eclipseclp.org/doc/bips/lib/ic/search-6.html)
