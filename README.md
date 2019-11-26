# aggregateexposure 

![](https://github.com/jasonparrott/aggregateexposure/workflows/Java%20CI/badge.svg)

This is an example app for compute aggregate exposure for a set of clients having position in a set of market valuations (instruments). 

# NOTE: This project is currently being rewored to use a graph based calculation engine, and that work is on the graphsourced- branch(es). It will be merged back here when complete but is not yet finished.

# design goals
* allow quick updates to large numbers of positions based on changes in the underlying valuation
* allow multiple kinds of assets to be contained in the position sets
* allow easy addition of new types of assets if necessary in the future

# extension
In order to extend this to include additinal product classes, do the following
1. Provide a Trade implementation. If your product is Linear you can extend LinearProduct and leverage existing workflows.
1. Provide a RiskCalculator for your new Trade type that correctly values risk given the trade and applicable MarketValuation data
1. Enhance your PortfolioBuilder to generate your new Trade type with your new RiskCalculator during portfolio construction

Keep in mind that the update callback triggered by trade or market updates is a diff between the previous
risk and thw newly calculated risk. The details of that calculation will be specific to your
trade type and its workflow.

# implementation
## client valuation
Per-client value is stored as a Fenwick Tree of positions. As these data structures naturally represent summed values, 
theyre an obvious candidate for this purpose. These data structures are very quick to update as changes in a child node only need
update its parent nodes to the root in order to affect the overall valuation of the tree. 

## valuation map
The market valuations are multi-mapped to a collection of positions as they are created. This allows us quick access to the 
position pool for each instrument as it is updated. 

# runtime
The application first creates its model, by generating a random set of positions for each of the supplied clients. These use random
instruments from the pool, and generate a random number of positions in the range of the supplied min and max counts.

Once complete, the update process begins, selecting 10000 market valuations at random and updating its value. Then for each one of the 
positions for this valuation all client valuations are updated to reflect the change in underlying market value. Timings are provided. Updates are dispatched to a work stealing thread pool on a per-valuation basis.

# command line
`java -jar aggregateexposure.jar <MARKET VALUATIONS> <CLIENTS> <MIN POSITIONS> <MAX POSITIONS>`

* MARKET VALUATIONS - Number of valuations 
* CLIENTS - Number of clients
* MIN POSITIONS - This is the minimum number of positions per asset class, per client.
* MAX POSITIONS - This is the max number of positions per asset class, per client.

# performance
The following results have been found on my home PC.
`apprunner@apprunner:~/aggregateexposure$ java -jar target/aggregateexposure-1.0-SNAPSHOT.jar 10000 200 500 5000`
`Created collection of 200 clients using 1646473 positions in 10000 market valuations`
`1532031 updates took: 1398 ms`

`apprunner@apprunner:~/aggregateexposure$ java -jar target/aggregateexposure-1.0-SNAPSHOT.jar 10000 500 500 5000`
`Created collection of 500 clients using 4098708 positions in 10000 market valuations`
`4102657 updates took: 3685 ms`

`apprunner@apprunner:~/aggregateexposure$ java -jar target/aggregateexposure-1.0-SNAPSHOT.jar 20000 500 2000 10000`
`Created collection of 500 clients using 9008265 positions in 20000 market valuations`
`4502657 updates took: 4178 ms`

`apprunner@apprunner:~/aggregateexposure$ java -jar target/aggregateexposure-1.0-SNAPSHOT.jar 10000 1000 2000 10000`
`Created collection of 1000 clients using 18034070 positions in 10000 market valuations`
`17414787 updates took: 10370 ms`


Valuations | Clients | Min Positions | Max Positions | Total Positions | Updates Made | Time(ms) | Updates per Second
---------- | ------- | ------------- | ------------- | --------------- | ------------ | -------- | -------------------
10000 |	200 |	500 | 5000 | 1646473 | 1532031 | 1398 | 1095873.391
10000	| 500	| 500 | 5000 | 4098708 | 4102657 | 3685 | 1113339.756
20000	| 500	| 2000 | 10000 | 9008265 | 4502657 | 4178 | 1077706.319
10000 |	1000 | 2000 | 10000 | 18034070 | 17414787 | 10370 | 1679343.009

## scaling
This has been tested across a series of VMs from 4 to 32 cores, and shows very linear performance characteristics. I tested each of the 4 compute sizes 4 times and took averages of the transaction rate across them. We see a transaction rate very stable at around 1000/ms, scaling linearly with number of cores

| Cores | Trans   | Time | trans/time   | avg          |
|-------|---------|------|--------------|--------------|
| 4     | 1120931 | 1131 | 991\.0972591 | 964\.3617213 |
| 4     | 1057818 | 1129 | 936\.9512843 |
| 4     | 1076959 | 1126 | 956\.446714  |
| 4     | 1045923 | 1075 | 972\.9516279 |
| 8     | 1636411 | 1525 | 1073\.056393 | 1055\.560256 |
| 8     | 1658717 | 1601 | 1036\.050593 |
| 8     | 1666251 | 1521 | 1095\.497041 |
| 8     | 1611937 | 1584 | 1017\.636995 |
| 16    | 3005562 | 2965 | 1013\.68027  | 1036\.422401 |
| 16    | 3529944 | 4004 | 881\.6043956 |
| 16    | 3833239 | 3682 | 1041\.075231 |
| 16    | 3447799 | 2851 | 1209\.329709 |
| 32    | 4470244 | 4042 | 1105\.94854  | 1031\.893884 |
| 32    | 4384245 | 4358 | 1006\.022258 |
| 32    | 4495832 | 4158 | 1081\.248677 |
| 32    | 4508268 | 4825 | 934\.3560622 |

The nature of the per-client updates also means that we can partition clients in order to scale the system horizontally, even dynamically by spinning up new instances of compute as necessary. These tests were done using the 20000 valuation/500 client/2000-10000 position set of hyper parameters.

