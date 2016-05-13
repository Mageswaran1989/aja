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


-----------------------------------------------------------------------------------
