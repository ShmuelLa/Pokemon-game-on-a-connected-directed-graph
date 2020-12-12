package api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DWGraph_DSTest {

    /**
     * Creates this graph for testing:
     * (WikiPictures/testgraph.jpg)
     * @return WGraph_DS - an initialized graph with the above settings
     */
    public static directed_weighted_graph mainTestGraph(){
        directed_weighted_graph wg = new DWGraph_DS();
        for (int i=1; i<=16; i++) {
            node_data tmp_node = new NodeData(i);
            wg.addNode(tmp_node);
        }
        wg.connect(1,2,3);
        wg.connect(1,3,3);
        wg.connect(4,1,7);
        wg.connect(3,5,7);
        wg.connect(5,3,4);
        return wg;
    }

    @Test
    void edge_connections() {
        directed_weighted_graph wg = mainTestGraph();
        assertEquals(5,wg.edgeSize());
        assertEquals(7,wg.getEdge(3,5).getWeight());
        assertEquals(4,wg.getEdge(5,3).getWeight());
    }
}