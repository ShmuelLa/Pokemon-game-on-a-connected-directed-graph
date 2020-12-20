![alt text](resources/startscreen.jpg)
 > :lock: The historic start screen, Won't be showcased in the project due being removed in order to make the application Command Line compatible


#### NodeData

| **Methods**      |    **Details**        |
|-----------------|-----------------------|
| `NodeData()` | Constructs a new node with the given key |
| `getKey()` | Returns the nodes key |
| `geo_location()` | Returns this nodes geo_location |
| `set_location()` | Sets this nodes geo_location | 
| `getWeight()` | Returns this nodes weight |
| `setWeight()` | Sets this nodes weight |
| `getInfo()` | Returns the nodes String metadata |
| `setInfo()` | Sets the nodes String metadata |
| `getTag()` | Returns the nodes double tag |
| `setTag()` | Sets the nodes double tag |
| `compareTo()` | Compares two nodes by the tag, chooses lowest |

##### EdgeInfo

| **Methods**    |    **Details**             |
|----------------|----------------------------|
| `EdgeInfo()` | The EdgeInfo constructor |
| `setWeight()` | Sets the weight between two nodes in a single direction |
| `connectE()` | Connects an edge between two nodes in a single direction |
| `hasNi()` | Checks if a selected node has the received neighbor node |
| `getNi()` | Returns a Collection representing the neighbors of a node |
| `getW()` | Returns the weight of an edge between two nodes |
| `removeSrc()` | Clears the data structure containing all the nodes connections |
| `getNiSize()` | Returns the neighbor count of a specific node |
| `removeEd()` | Removes and edge between two nodes in a single direction |
 
### :bar_chart: Graph_Algo

| **Method**      |    **Details** |
|-----------------|--------------|
| `init()`         | Initialize the graph |
| `copy()`        | Creates a deep copy of the graph |
| `getGraph()` | Returns a pointer to the initialized graph |
| `isConnected()` | Checks if the graph is connected |
| `shortestPathDist()` | Returns the length of te shortest path between two node, if non existent returns -1 |
| `shortestPath()` | Returns a List<node_data> of the shortest path between two nodes, if non existent returns null |
| `save()` | Saves a graph to a file via Serialization |
| `load()` | Loads a graph from a file via Deserialization |
| `reset()` | Rests the graph's tag and metadata after running an algorithm |
### :bar_chart: Graph_Algo

## :mag: Tests

In this project we invested extensively in testing our implementation. 
We created a test for each and every complex and simple method in this project.

The tests rely on two main mechanisms:
- a `graph_creator()` method we build that creates a graph with the set amount 
of nodes and edges while randomizing their connections
- a complex and unique graph build in advanced that we researched it behavior and take advantage 
of that in order to test complex algorithms like BFS and Dijkstra's. 
Implemented in `mainTestGraph()` and `mainTestGraphAlg()` accordingly

## Game Results
| **Stage** | **Moves** | **Grade** |
|-----------|-----------|-----------|
| 11 | 1098 | 1383 |
| 12 | 546 | 52 |
| 14 | 528 | 139 |
| 15 | 1084 | 283 |
| 16 | 542 | 167 |
| 17 | 1084 | 846 |
| 18 | 544 | 40 |
| 19 | 1086 | 234 |
| 20 | 544 | 139 |
| 21 | 1086 | 244|
| 22 | 542 | 131 |
| 23 | 1084 | 420 |
