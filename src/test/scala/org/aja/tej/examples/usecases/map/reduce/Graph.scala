package org.aja.tej.examples.usecases.map.reduce

/**
 * Created by mageswaran on 17/10/15.
 */

/**
Iterative Message Passing (Graph Processing)
============================================
Problem Statement:
------------------
  There is a network of entities and relationships between them. It is required to calculate a state of
each entity on the basis of properties of the other entities in its neighborhood. This state can represent a distance to other
nodes, indication that there is a neighbor with the certain properties, characteristic of neighborhood density and so on.
Solution: A network is stored as a set of nodes and each node contains a list of adjacent node IDs. Conceptually,
MapReduce jobs are performed in iterative way and at each iteration each node sends messages to its neighbors. Each
neighbor updates its state on the basis of the received messages. Iterations are terminated by some condition like fixed
maximal number of iterations (say, network diameter) or negligible changes in states between two consecutive
iterations. From the technical point of view, Mapper emits messages for each node using ID of the adjacent node as a
key. As result, all messages are grouped by the incoming node and reducer is able to recompute state and rewrite node
with the new state. This algorithm is shown in the figure below:

Case Studies:
-------------
Availability Propagation Through The Tree of Categories
---------------------------------------------------------
Problem Statement:
-----------------
  This problem is inspired by real life eCommerce task. There is a tree of categories that branches out
from large categories (like Men, Women, Kids) to smaller ones (like Men Jeans or Women Dresses), and eventually to
small end‐of‐line categories (like Men Blue Jeans). End‐of‐line category is either available (contains products) or not.
Some high level category is available if there is at least one available end‐of‐line category in its subtree. The goal is to
calculate availabilities for all categories if availabilities of end‐of‐line categories are know.
Solution:


Breadth­First Search
--------------------
Problem Statement:
------------------
  There is a graph and it is required to calculate distance (a number of hops) from one source node to
all other nodes in the graph.

PageRank and Mapper­Side Data Aggregation
  This algorithm was suggested by Google to calculate relevance of a web page as a function of authoritativeness
(PageRank) of pages that have links to this page. The real algorithm is quite complex, but in its core it is just a
propagation of weights between nodes where each node calculates its weight as a mean of the incoming weights:

Applications:
-------------
Graph Analysis, Web Indexing
*/

class Graph {

}
