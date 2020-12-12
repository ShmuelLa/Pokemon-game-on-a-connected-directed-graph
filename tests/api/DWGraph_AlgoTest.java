package api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DWGraph_AlgoTest {

    @Test
    void copy() {
    }

    @Test
    void isConnected() {
        directed_weighted_graph dg = DWGraph_DSTest.mainTestGraph();
        dw_graph_algorithms dga = new DWGraph_Algo();
        dga.init(dg);
        assertFalse(dga.isConnected());
        dg.connect(3,4,3);
        assertFalse(dga.isConnected());
        dg.connect(2,3,3);
        dga.isConnected();
        assertTrue(dga.isConnected());
    }

    @Test
    void shortestPathDist() {
    }

    @Test
    void shortestPath() {
    }

    @Test
    void save() {
    }

    @Test
    void load() {
    }
}