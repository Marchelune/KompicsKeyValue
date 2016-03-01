# KompicsKeyValue
A simple partitioned, distributed in-memory key-value store with linearisable operation semantics, in Kompics

##Operations

*PUT(key,value)
*GET(key)
*CAS(key,referenceValue,newValue)

##Guarantees

*Does not reconfigure when nodes crashes
*Provides linearizability thanks to RSM (on top of multi paxos algorithm)
