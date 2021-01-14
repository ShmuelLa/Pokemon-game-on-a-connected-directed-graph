package api;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

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
        assertEquals(-1,dga.shortestPathDist(1,5));
        assertEquals(-1,dga.shortestPathDist(5,1));
    }

    @Test
    void shortestPath() {
        directed_weighted_graph dg = DWGraph_DSTest.mainTestGraph();
        dw_graph_algorithms dga = new DWGraph_Algo();
        dga.init(dg);
        ArrayList<Integer> test = new ArrayList<>();
        test.add(1);
        test.add(3);
        test.add(5);
        int index = 0;
        for (node_data n : dga.shortestPath(1,5)) {
            assertEquals(n.getKey(),test.get(index));
            index++;
        }
        ArrayList<Integer> test2 = new ArrayList<>();
        test2.add(3);
        test2.add(4);
        test2.add(1);
        test2.add(2);
        index = 0;
        assertNull(dga.shortestPath(3,2));
        dg.connect(3,4,3);
        dg.connect(2,3,3);
        for (node_data n : dga.shortestPath(3,2)) {
            assertEquals(n.getKey(),test2.get(index));
            index++;
        }
        assertNull(dga.shortestPath(1,55));
        assertNull(dga.shortestPath(-22,0));
    }

    @Test
    void save_load() {
        directed_weighted_graph dg = DWGraph_DSTest.mainTestGraph();
        dw_graph_algorithms dga = new DWGraph_Algo();
        dga.init(dg);
        dga.save("tests/IO/maintestgraph.json");
        directed_weighted_graph dg2 = new DWGraph_DS();
        dw_graph_algorithms dga2 = new DWGraph_Algo();
        dga2.init(dg2);
        dga2.load("tests/IO/maintestgraph.json");
        assertEquals(dga.getGraph(),dga2.getGraph());
        try {
            String a0 = Files.readString(Path.of("data/A0"));
            dga.load("data/A0");
            dga.save("tests/IO/2nd");
            String a2 = Files.readString(Path.of("tests/IO/2nd"));
            assertEquals(a0,a2);
        } catch (IOException e) {
            System.out.println("Wrong Input");
            e.printStackTrace();
        }
    }

    @Test
    void ex3_timing_test1() {
        ArrayList<ArrayList> list1 = new ArrayList<>();
        ArrayList<Integer> list2 = new ArrayList<>();
        list1.add(list2);
        System.out.println(list1);
    }
}