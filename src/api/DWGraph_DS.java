package api;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class DWGraph_DS implements directed_weighted_graph {
    private HashMap<Integer,node_data> _graph_nodes;
    private HashMap<Integer,EdgeDB> _graph_edges;
    private int _edge_size;
    private int _mode_count;

    public class EdgeDB {
        private HashMap<Integer,edge_data> _neighbors;
        private Collection<Integer> _received_edges;

        private EdgeDB() {
            this._neighbors = new HashMap<>();
            this._received_edges = new HashSet<>();
        }

        private void connectDB(int src, int dest, double w) {
            edge_data tmp_edge = new EdgeData(src, dest, w);
            this._neighbors.put(tmp_edge.getDest(),tmp_edge);
        }

        private boolean hasNI(int dest) {
            return this._neighbors.containsKey(dest);
        }
    }

    /**
     * The default Graph_DS constructor. Creates a new graph with empty nodes and edges Map
     * alongside initialized edge counted and mod count
     */
    public DWGraph_DS() {
        this._graph_nodes = new HashMap<>();
        this._graph_edges = new HashMap<>();
        this._edge_size = 0;
        this._mode_count = 0;
    }

    /**
     * returns the node_data by the node_id,
     *
     * @param key - the node_id
     * @return the node_data by the node_id, null if none.
     */
    @Override
    public node_data getNode(int key) {
        return this._graph_nodes.get(key);
    }

    /**
     * returns the data of the edge (src,dest), null if none.
     * Note: this method should run in O(1) time.
     *
     * @param src
     * @param dest
     * @return
     */
    @Override
    public edge_data getEdge(int src, int dest) {
        return this._graph_edges.get(src)._neighbors.get(dest);
    }

    /**
     * adds a new node to the graph with the given node_data.
     * Note: this method should run in O(1) time.
     *
     * @param n
     */
    @Override
    public void addNode(node_data n) {
        if (!this._graph_nodes.containsKey(n.getKey())) {
            this._graph_nodes.put(n.getKey(),n);
            EdgeDB tmp_edb = new EdgeDB();
            this._graph_edges.put(n.getKey(),tmp_edb);
            this._mode_count++;
        }
    }

    /**
     * Connects an edge with weight w between node src to node dest.
     * * Note: this method should run in O(1) time.
     *
     * @param src  - the source of the edge.
     * @param dest - the destination of the edge.
     * @param w    - positive weight representing the cost (aka time, price, etc) between src-->dest.
     */
    @Override
    public void connect(int src, int dest, double w) {
        if (src == dest || w < 0) return;
        if (this._graph_nodes.containsKey(src) && this._graph_nodes.containsKey(dest)) {
            if (!this._graph_edges.get(src)._neighbors.containsKey(dest)) {
                this._graph_edges.get(src).connectDB(src, dest, w);
                this._graph_edges.get(dest)._received_edges.add(src);
                this._edge_size++;
                this._mode_count++;
                return;
            }
            if (this._graph_edges.get(src).hasNI(dest)) {
                if (this._graph_edges.get(src)._neighbors.get(dest).getWeight() == w) {
                    return;
                }
                else {
                    edge_data tmp_edge = new EdgeData(src, dest, w);
                    this._graph_edges.get(src)._neighbors.put(dest,tmp_edge);
                    _mode_count++;
                }
            }
        }
    }

    /**
     * This method returns a pointer (shallow copy) for the
     * collection representing all the nodes in the graph.
     * Note: this method should run in O(1) time.
     *
     * @return Collection<node_data>
     */
    @Override
    public Collection<node_data> getV() {
        return this._graph_nodes.values();
    }

    /**
     * This method returns a pointer (shallow copy) for the
     * collection representing all the edges getting out of
     * the given node (all the edges starting (source) at the given node).
     * Note: this method should run in O(k) time, k being the collection size.
     *
     * @param node_id
     * @return Collection<edge_data>
     */
    @Override
    public Collection<edge_data> getE(int node_id) {
        return this._graph_edges.get(node_id)._neighbors.values();
    }

    /**
     * Deletes the node (with the given ID) from the graph -
     * and removes all edges which starts or ends at this node.
     * This method should run in O(k), V.degree=k, as all the edges should be removed.
     *
     * @param key
     * @return the data of the removed node (null if none).
     */
    @Override
    public node_data removeNode(int key) {
        if (this._graph_nodes.containsKey(key)) {
            node_data tmp_node = this._graph_nodes.get(key);
            for (edge_data e : this.getE(key)) {
                this.removeEdge(key,e.getDest());
            }
            if (this._graph_edges.get(key)._received_edges.size() > 0) {
                for (Integer n : this._graph_edges.get(key)._received_edges) {
                    this.removeEdge(n,key);
                }
            }
            this._graph_nodes.remove(key);
            this._graph_edges.remove(key);
            return tmp_node;
        }
        else return null;
    }

    /**
     * Deletes the edge from the graph,
     * Note: this method should run in O(1) time.
     *
     * @param src
     * @param dest
     * @return the data of the removed edge (null if none).
     */
    @Override
    public edge_data removeEdge(int src, int dest) {
        if (this._graph_edges.get(src).hasNI(dest) && src != dest) {
            edge_data tmp_edge = this._graph_edges.get(src)._neighbors.get(dest);
            this._graph_edges.get(src)._neighbors.remove(dest);
            this._graph_edges.get(dest)._received_edges.remove(src);
            this._mode_count++;
            this._edge_size--;
            return tmp_edge;
        }
        else return null;
    }

    /**
     * Returns the number of vertices (nodes) in the graph.
     * Note: this method should run in O(1) time.
     *
     * @return
     */
    @Override
    public int nodeSize() {
        return this._graph_nodes.size();
    }

    /**
     * Returns the number of edges (assume directional graph).
     * Note: this method should run in O(1) time.
     *
     * @return
     */
    @Override
    public int edgeSize() {
        return this._edge_size;
    }

    /**
     * Returns the Mode Count - for testing changes in the graph.
     *
     * @return
     */
    @Override
    public int getMC() {
        return this._mode_count;
    }
}
