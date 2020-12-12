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
        directed_weighted_graph dg = DWGraph_DSTest.mainTestGraph();
        dw_graph_algorithms dga = new DWGraph_Algo();
        dga.init(dg);
        assertEquals(3,dga.shortestPathDist(1,3));
        dg.connect(3,4,3);
        assertEquals(6,dga.shortestPathDist(1,4));
        dg.connect(2,3,3);
        assertEquals(3,dga.shortestPathDist(1,3));
        assertEquals(14,dga.shortestPathDist(5,1));
        dg.removeNode(3);
        assertEquals(-1,dga.shortestPathDist(5,1));
        dga.shortestPathDist(1,5);
        assertEquals(-1,dga.shortestPathDist(1,5));
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