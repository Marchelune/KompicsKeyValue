# KompicsKeyValue
A simple partitioned, distributed in-memory key-value store with linearisable operation semantics, implemented within the [Kompics](http://kompics.sics.se/) framework. Keys are integers and stored values are strings.

## Operations

- PUT(key,value)
- GET(key)
- CAS(key,referenceValue,newValue)

## Guarantees

- Provides **linearizability of operations** thanks to RSM (on top of multi paxos algorithm)
- **Availability until d/2 processes** crashes (d=replication degree), then we have partial availability.
- The store is not dynamically reconfigurable. To change the configuration, one has to stop the system, change the conf file, and restart it.


## Run the project
After downloading :

1. **run deploy.sh script**. It will ask you for the maximum key in the kvstore, the number of partition and replication degree. Then one folder per node will be created in a temp folder accordingly. The client will also be configured.
2. **run launcher.sh script**. It will launch the store. (You can also manually run the individualLauncher.sh script in each node folder.)
3. **run clientLauncher.sh script**, more likely in a separate shell. It should already have one address of the nodes to contact the store. If you want to change that address or launch several clients you will have to change the reference.conf file in the client folder. Syntaxe :
    - PUT(34,word)
    - GET(34)
    - CAS(34,word,otherword)
4. **type q or “QUIT” to quit client.**
5. **press any key to quit the store.**
