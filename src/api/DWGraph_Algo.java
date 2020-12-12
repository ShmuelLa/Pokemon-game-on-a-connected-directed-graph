package api;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DWGraph_Algo implements dw_graph_algorithms {
    directed_weighted_graph _algo_graph = new DWGraph_DS();

    /**
     * Init the graph on which this set of algorithms operates on.
     *
     * @param g
     */
    @Override
    public void init(directed_weighted_graph g) {
        this._algo_graph = g;
    }

    /**
     * Return the underlying graph of which this class works.
     *
     * @return
     */
    @Override
    public directed_weighted_graph getGraph() {
        return this._algo_graph;
    }

    /**
     * Compute a deep copy of this weighted graph.
     *
     * @return
     */
    @Override
    public directed_weighted_graph copy() {
        directed_weighted_graph result = new DWGraph_DS();
        if (this._algo_graph.nodeSize() == 0) return result;
        for (node_data n : this._algo_graph.getV()) {
            result.addNode(n);
        }
        if (this._algo_graph.edgeSize() == 0) return result;
        for (node_data n : this._algo_graph.getV()) {
            for (edge_data e : this._algo_graph.getE(n.getKey())) {
                result.connect(e.getSrc(), e.getDest(), e.getWeight());
            }
        }
        return result;
    }

    /**
     * Returns true if and only if (iff) there is a valid path from each node to each
     * other node. NOTE: assume directional graph (all n*(n-1) ordered pairs).
     *
     * @return
     */
    @Override
    public boolean isConnected() {
        if (this._algo_graph.nodeSize() <= 1 || this._algo_graph == null) return true;
        Queue<Integer> queue = new LinkedList<>();
        HashSet<Integer> visited = new HashSet<>();
        int tmp_node = this._algo_graph.getV().iterator().next().getKey();
        int test_node = tmp_node;
        queue.add(tmp_node);
        visited.add(tmp_node);
        while (!queue.isEmpty()) {
            tmp_node=queue.poll();
            for (edge_data edge : this._algo_graph.getE(tmp_node)) {
                if (!visited.contains(edge.getDest())) {
                    visited.add(edge.getDest());
                    queue.add(edge.getDest());
                }
            }
        }
        if (visited.size() != this._algo_graph.nodeSize()) return false;
        directed_weighted_graph reversed_graph = new DWGraph_DS();
        for (node_data n : this._algo_graph.getV()) {
            reversed_graph.addNode(n);
        }
        for (node_data n : this._algo_graph.getV()) {
            for (edge_data e : this._algo_graph.getE(n.getKey())) {
                reversed_graph.connect(e.getDest(),e.getSrc(),e.getWeight());
            }
        }
        queue.clear();
        visited.clear();
        tmp_node = test_node;
        queue.add(test_node);
        visited.add(tmp_node);
        while (!queue.isEmpty()) {
            tmp_node=queue.poll();
            for (edge_data edge : reversed_graph.getE(tmp_node)) {
                if (!visited.contains(edge.getDest())) {
                    visited.add(edge.getDest());
                    queue.add(edge.getDest());
                }
            }
        }
        if (visited.size() != reversed_graph.nodeSize()) return false;
        else return true;
    }

    /**
     * returns the length of the shortest path between src to dest
     * Note: if no such path --> returns -1
     *
     * @param src  - start node
     * @param dest - end (target) node
     * @return
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        if (this._g.nodeSize()<=1 || this._g.getNode(src)==null || this._g.getNode(dest)==null) return -1;
        if (src == dest) return 0.0;
        PriorityQueue<node_info> pq = new PriorityQueue<>();
        double result;
        node_info cur = this._g.getNode(src);
        pq.add(cur);
        cur.setTag(0);
        while (!pq.isEmpty()) {
            cur = pq.poll();
            if (!Objects.equals(cur.getInfo(), "y")) {
                cur.setInfo("y");
                if (cur.getKey() == dest) break;
                for (node_info n : this._g.getV(cur.getKey())) {
                    if (n.getTag() == -1) {
                        n.setTag(Double.MAX_VALUE);
                    }
                    double tmp_tag = cur.getTag()+this._g.getEdge(cur.getKey(),n.getKey());
                    if (tmp_tag < n.getTag()) {
                        n.setTag(tmp_tag);
                        pq.add(n);
                    }
                }
            }
        }
        cur = this._g.getNode(dest);
        result = cur.getTag();
        if (!Objects.equals(cur.getInfo(), "y")) return -1;
        this.reset();
        return result;



        return 0;
    }

    /**
     * returns the the shortest path between src to dest - as an ordered List of nodes:
     * src--> n1-->n2-->...dest
     * see: https://en.wikipedia.org/wiki/Shortest_path_problem
     * Note if no such path --> returns null;
     *
     * @param src  - start node
     * @param dest - end (target) node
     * @return
     */
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        return null;
    }

    /**
     * Saves this weighted (directed) graph to the given
     * file name - in JSON format
     *
     * @param file - the file name (may include a relative path).
     * @return true - iff the file was successfully saved
     */
    @Override
    public boolean save(String file) {
        return false;
    }

    /**
     * This method load a graph to this graph algorithm.
     * if the file was successfully loaded - the underlying graph
     * of this class will be changed (to the loaded one), in case the
     * graph was not loaded the original graph should remain "as is".
     *
     * @param file - file name of JSON file
     * @return true - iff the graph was successfully loaded.
     */
    @Override
    public boolean load(String file) {
        return false;
    }
}
