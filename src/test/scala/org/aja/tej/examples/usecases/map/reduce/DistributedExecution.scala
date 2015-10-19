package org.aja.tej.examples.usecases.map.reduce

/**
 * Created by mageswaran on 17/10/15.
 */

/**
Distributed Task Execution
==========================
Problem Statement:
------------------
  There is a large computational problem that can be divided into multiple parts and results from all
parts can be combined together to obtain a final result.
Solution: Problem description is split in a set of specifications and specifications are stored as input data for Mappers.
Each Mapper takes a specification, performs corresponding computations and emits results. Reducer combines all
emitted parts into the final result.

Case Study: Simulation of a Digital Communication System
---------------------------------------------------------
  There is a software simulator of a digital communication system like WiMAX that passes some volume of random data
through the system model and computes error probability of throughput. Each Mapper runs simulation for specified
amount of data which is 1/Nth of the required sampling and emit error rate. Reducer computes average error rate.

Applications:
-------------
  Physical and Engineering Simulations, Numerical Analysis, Performance Testing
*/

class DistributedExecution {

}
