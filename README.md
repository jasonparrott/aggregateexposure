# aggregateexposure 

![](https://github.com/jasonparrott/aggregateexposure/workflows/Java%20CI/badge.svg)

This is an example app for compute aggregate exposure for a set of clients having position in a set of market valuations (instruments). 

# design goals
* allow quick updates to large numbers of positions based on changes in the underlying valuation
* allow multiple kinds of assets to be contained in the position sets
* allow easy addition of new types of assets if necessary in the future

# components
To understand the system design it is useful to become familiar with the components used in the eninge.

## trade listener
A source of trade infromation for the engine. This component is polled regularly by the engine looking for new trades.
## position service
A service which takes a trade and returns an associated position for that trade detail.
## security group
A collection of positions, sharing some common feature. This is the main aggregation unit in the system. The 
nature of the agrigation is determined by the partitioner as described below. For example, if we wanted to
group positions by the client's ID, each Security Group would represent 1 single client ID. 
## partitioner
A component which determines which Security Group to place a given position in. Two example partitioners given
in the project are:
* Client ID Partitioner - splits positions based on the client they are against
* Client Credit Rating Partitioner - splits positions based on the credit rating of their client

## calculation graph
A directed acyclic graph which processes market data updates and provides a merket data input source for the engine.
There are 2 kinds of nodes in the graph, as described below.

### market valuation node
A node providing raw market data to the graph. This is a direct input from some external market data update source,
such as Reuters or BBG.

### intermediate node
A graph node which performs and operation on 1 or more other input nodes, but does not otherwise directly provide any
other information. For example an intermediate node might sum the values of 2 other market valuation nodes.

## metrics calculator
A component which processes all of the current input values for a security group, and generates the current set of
risk metrics for that group.

### chained calculator
A special kind of metrics calculator which holds a series of other calculators. This can be useful if you have 
common calculation logic which you would like to apply to several product types. That logic can be put in its 
own calculator and then added to the chain for any necessary products.
 
## publisher
A service which makes the calculation result available to other systems. This might publish to a queue, make a
web service call, or take some other action. Examples given are ones which write updates to the console and to
a log file. 

# extension
In order to extend this to include additional product classes, do the following
1. Provide a Trade implementation. Select a security ID for it to return for each unique security within that type. 
1. Provide a new ProductType. If yours does not fit into one of the existing product types, create a new enum value and
have your new trade instances return this as its ProductType
1. Provide a SecurityGroupValueMapper for your new ProductType if you created one. These are created by the framework via factories
which you will also need to create. Your factory should implement the ValuationMapperFactory interface and be annotated
with the `ValuationMapperProducer` annotation. Use the new ProductType you created above to denote that this is the factory for the
mappers for your new product type. For example, `@ValuationMapperProducer(productType = ProductType.Swap)`
1. Similarly, create a MetricsCalculator for your new ProductType, and an associated factory. This interface is
`MetricsCalculatorFactory` and the annotation is `MetricsCalculatorProducer`. For example `@MetricsCalculatorProducer(productType = ProductType.Bond)`
If you are using the ChainedCalculator, simply add your new one to a chain.
1. (optional) Create a Publisher. If you do not want to use one of the standard publishers, create a new custom
Publisher. This publisher is responsible for making the computed metrics available externally.

Thats it. The framework will find the factories you created automatically based on your annotations, and use them
when creating trade sets with your new product type. Your metrics calculator will be called by updates to the associated
calculation graph nodes in order to compute the analytics.

# implementation
## client valuation
Trades are grouped into SecurityGroups according to the logic in the configured Partitioner. This is an aggregate container 
for a number of positions of the same type. By grouping the positions in this way we can perform a smaller number of calculations when recomputing 
risk. effectively this means batching the risk updates such that we apply 1 update across a large number of positions.

## calculation graph
The implementation creates a calculation graph in order to provide source input to the risk calculations. This is a directed
acyclic graph of compute nodes. Nodes may be direct market data input (ie driven by live market updates) or otherwise are
intermediate calculation nodes. Intermediate nodes perform some arbitrary computation on 1 or more inputs from other nodes.

## valuation map
Each security group is mapped to 1 or more nodes in the compute graph by the associated ValuationMapper. When these nodes 
are updated, it necessarily adjusts the risk profile of that security group, and an update task is created to be 
completed by a worker. The compute for each type of trade is done by a MetricsCalculator specific to that product type. 

# runtime
When a new trade is first added to the engine, the engine must initialize a new security group for that partitions
trades. This process involves setting up the security group, registering it for input updates, and registering
it as a provider of Metrics for the configured publisher. A sequence diagram of the processes is:
![New Security Group Sequence](https://raw.githubusercontent.com/jasonparrott/aggregateexposure/master/doc/newsecgroup.png)

## market data updates
When a market data update occurs, the calculation graph is updated such that all dependent nodes have new values,
and interested security groups are notified. Those groups then begin a process to update their risk metrics utilizing
the newly available input data. Once complete the groups will publish the upadate via the configured publisher.
 ![Market Data Update Sequence](https://raw.githubusercontent.com/jasonparrott/aggregateexposure/master/doc/marketdataupdate.png)

# deployment patterns
The abilty to specify various kind of partitioners means that the same engine can be deployed multiple times 
using separate partioners to get different risk aggregations. A typical deployment might look something like
![Typical Deployment](https://raw.githubusercontent.com/jasonparrott/aggregateexposure/master/doc/riskreporting.png)
Each of these instances could feed a reporting service such as Tableau Server which then presents the various
aggregations to users in a concise manner. Notice on this sample report that each block of info could be generated
by simply having a separate Partitioner for a different instance of the engine.
![Sample Report](https://raw.githubusercontent.com/jasonparrott/aggregateexposure/master/doc/report.png)

# optimizations
## batching
SecurityGroups provide a large optimization compared to per-trade level updates. By aggregating positions we can
avoid potentially hundreds of calculations and instead make a single one. In the future it may be worth while
to further be able to partition the collection of trades for thing like per-action valuations, but this can
be done within the existing groups, and is a secondary calculation. The core risk calculations should be batched
as much as possible in order to update aggregate results as close to realtime as possible.
![SecurityGroup dependent on 3 nodes](https://raw.githubusercontent.com/jasonparrott/aggregateexposure/master/doc/tradesetdepends.png)

## subgraph updates
The directed acyclic nature of our calculation graph implies that updates to parent nodes will potentially affect child
nodes. This can cause a large number of valuations to occur as we update nodes in the graph, and then by design
cause their associated SecurityGroups to recalculate. As an optimization we choose not to update the full graph 
when a value changes, but rather only the subtree affected by that update. 

For example consider this graph containing live market data and intermediate calculation nodes.
![Full Calculation Graph](https://raw.githubusercontent.com/jasonparrott/aggregateexposure/master/doc/fullgraph.png)
Suppose that node 19, a live market data node is updated. 
![Node 19 Update](https://raw.githubusercontent.com/jasonparrott/aggregateexposure/master/doc/19update.png)
Its clear that nodes such as 1,2,3, and 10 are not affected by this update. If we were to update the whole 
graph, we would update 21 nodes, and all trades in the system. This is inefficient.

Therefore we should not evalue SecurityGroups assocaited with those nodes. Instead we find the subtree affected by this update. In this case
that would be 19-18, and 18-21. There are actually only 3 nodes that need to be updated as shown here
![Update Path Requiring Recalc](https://raw.githubusercontent.com/jasonparrott/aggregateexposure/master/doc/19updatepath.png)

We can then recalc only SecurityGroups associated with nodes 19, 18, and 21 as a result of this market data change.
 
## graph path caching
The process of determining the connected subgraph from each node can be expensive, especially in large graphs.
As a result we do not recompute the graph once a node has been updated once. This is a tradeoff in terms of 
a dynamic graph in exchange for speed of updated. However I believe its unlikely that a graph will change
structurally for a trade set within a single run. If necessary we could add some sort of "refresh" that periodically
rebuilt the grid and paths if necessary but thats not been done here. I believe that its very likely the 
graph will remain structurally identical overtime, and instead feature mostly live market updates to existing nodes, 
rather than new nodes being added ad-hoc.

# command line
The "DEMO" project contains a sample application of the risk engine. It mostly uses mock/fake data however so you
are recomended to use it as a template and build you own input/output/calculators.

mvn springboot:run -Dvaluations=<MARKET VALUATIONS> -Dclients=<CLIENTS> -Dassets.min=<MIN POSITIONS> -Dassets.max=<MAX POSITIONS> -Dassets.count=<ASSETS COUNT>

* MARKET VALUATIONS - Number of valuations 
* CLIENTS - Number of clients
* MIN POSITIONS - This is the minimum number of positions per asset class, per client.
* MAX POSITIONS - This is the max number of positions per asset class, per client.
* ASSETS COUNT - The number of securities to use in the pool of random instruments.

# performance
ValuationPerfIT contains a JMH benchmark updating valuation notes in a complete pricing graph. This shows
updates taking in the range of 16us / update.

`# Warmup Iteration   1: 16.727 us/op`

` Iteration   1: 16.114 us/op`

` Iteration   2: 16.077 us/op`

` Iteration   3: 16.089 us/op`
 
` Result "com.jasonparrott.aggregateexposure.perf.ValuationPerfIT.testValuations":`

`   16.093 ±(99.9%) 0.344 us/op [Average]`

`   (min, avg, max) = (16.077, 16.093, 16.114), stdev = 0.019`

`   CI (99.9%): [15.749, 16.438] (assumes normal distribution)`


` # Run complete. Total time: 00:00:42`