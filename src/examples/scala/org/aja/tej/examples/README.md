#Tej - Light, Lustrous, Radiant
##Apache Spark examples

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
