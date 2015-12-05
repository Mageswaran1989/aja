# AJA - Accomplish Joyfull Adventures
## Etymology
- Unborn or uncreated
- Existing from all eternity
- Goat
- Strong willed 
- Stubborn
- Unconquerable
- To drive

---------------------------------------------------------------------------------

## Project Structure
- [org.aja.tej Pacakages](https://github.com/Mageswaran1989/aja/tree/master/src/examples/scala/org/aja/tej/examples)    : Apache Spark examples and utilities!
- [Android](https://github.com/Mageswaran1989/aja/tree/master/android) : Android + Scala integration!
- [org.aja.dhira Pacakage](https://github.com/Mageswaran1989/aja/tree/master/src/examples/scala/org/aja/dhira)  : Scala based Artificial Neural Network exploration at its infancy!
- [org.aja.tantra Pacakage](https://github.com/Mageswaran1989/aja/tree/master/src/examples/scala/org/aja/tantra/examples) : Scala language exploration examples and utilities!
- [docs](https://github.com/Mageswaran1989/aja/tree/master/docs) : All reference materials
- [data](https://github.com/Mageswaran1989/aja/tree/master/data) : Datasets used in the implementation
- [tools](https://github.com/Mageswaran1989/aja/tree/master/tools) : Custom GUI tools if any need arises
- [utils](https://github.com/Mageswaran1989/aja/tree/master/utils) : Language Specific utils wrappers

-----------------------------------------------------------------------------------

#Tej - Light, Lustrous, Radiant

Apache Spark examples and utilities!

Glimpse of what we are exploring!  
*How many tasks can I run in my cluster?*   
`M(Machines) * E(Executors/Machine) * C(Cores per Executor / 1 core for each task) = T(Tasks)`  
Eg: 12 * 2 * 12 = 288 tasks    
*How much amount of RAM is under usage?*   
`0.9 (spark.storage.safetyFraction) * 0.6 (spark.storage.memoryFraction) * M (Machines) * E (Executors/Machine) * S (Size in GBs per executor)`  
Eg: 0.9 * 0.6 * 12  * 2  * 26 = 336.96 GB

##How to compile and test?

Using SBT in aja root folder:  
`$ sbt`  
`$ sbt compile`  
To run examples under Tej: type "run-main org.aja.tej.examples." and TAB to select the example you are interested  
`$ run-main org.aja.tej.examples.ml.ANNet`  

Using IntelliJ editor:  
File -> open -> point to aja folder

Here are few links that gives glimse of data  
- https://www.worldpayzinc.com/tech-wealth/#sources  

-----------------------------------------------------------------------------------

#dhīra — a very learned and gentle scholar

Scala based Artificial Neural Network exploration at its infancy!

Inspired by https://github.com/prnicolas/ScalaMl

### Linear Algebra:  
- [Breeze](https://github.com/scalanlp/breeze)

### OpenCL for GPU integration:
- [ScalaCL](https://github.com/nativelibs4java/ScalaCL)i
- [JOCL](http://www.jocl.org/)

### Native Interfaces:
- https://github.com/java-native-access/jna

### We shall explore Neural Network libraries:
- [PyBrain](http://pybrain.org/)
- [Scikit Learn](http://scikit-learn.org/stable/modules/neural_networks.html)
- [cudnn](https://developer.nvidia.com/cudnn)
- [deeplearning4j](http://deeplearning4j.org/)

-------------------------------------------------------------------------------------

# tantra 

Scala language exploration examples and utilities!

-------------------------------------------------------------------------------------

##Build Environment
Linux Ubuntu 12.04+

## Git Links
- [aja](https://github.com/Mageswaran1989/aja)

##Wiki
- [How to start?](https://github.com/Mageswaran1989/aja/wiki/How-to-start%3F)
	
## Contribution  
- [How to set up GIT](https://github.com/Mageswaran1989/aja/wiki/Setting-up-the-GIT)

**Let us begin our jouney from here!**
> *Contact: mageswaran1989@gmail.com*

