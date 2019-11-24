# aggregateexposure 
This is an example app for compute aggregate exposure for a set of clients having position in a set of market valuations (instruments). 

# design goals
* allow quick updates to large numbers of positions based on changes in the underlying valuation
* allow multiple kinds of assets to be contained in the position sets
* allow easy addition of new types of assets if necessary in the future

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
This has been tested across a series of VMs from 4 to 32 cores, and shows very linear performance characteristics. We see a transaction rate very stable at around 1000/ms, scaling linearly with number of cores
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

The nature of the per-client updates also means that we can partition clients in order to scale the system horizontally, even dynamically by spinning up new instances of compute as necessary. 

