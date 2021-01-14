package api;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class Ex3_Time_Test {

    @Test
    void ex3_timing_test1() {
        DWGraph_DS g1 = new DWGraph_DS();
        DWGraph_Algo ga = new DWGraph_Algo();
        ga.init(g1);
        ga.load("C:\\Users\\shmue\\Documents\\Git\\OOP_ex2\\data\\Graphs_on_circle\\G_10_80_1.json");
        ga.scc();
    }

    @Test
    void ex3_timing_test2() {
        DWGraph_DS g1 = new DWGraph_DS();
        DWGraph_Algo ga = new DWGraph_Algo();
        ga.init(g1);
        ga.load("C:\\Users\\shmue\\Documents\\Git\\OOP_ex2\\data\\Graphs_on_circle\\G_100_800_1.json");
        ga.scc();
    }

    @Test
    void ex3_timing_test3() {
        DWGraph_DS g1 = new DWGraph_DS();
        DWGraph_Algo ga = new DWGraph_Algo();
        ga.init(g1);
        ga.load("C:\\Users\\shmue\\Documents\\Git\\OOP_ex2\\data\\Graphs_on_circle\\G_1000_8000_1.json");
        ga.scc();
    }

    @Test
    void ex3_timing_test4() {
        DWGraph_DS g1 = new DWGraph_DS();
        DWGraph_Algo ga = new DWGraph_Algo();
        ga.init(g1);
        ga.load("C:\\Users\\shmue\\Documents\\Git\\OOP_ex2\\data\\Graphs_on_circle\\G_10000_80000_1.json");
        ga.scc();
    }

    @Test
    void ex3_timing_test5() {
        DWGraph_DS g1 = new DWGraph_DS();
        DWGraph_Algo ga = new DWGraph_Algo();
        ga.init(g1);
        ga.load("C:\\Users\\shmue\\Documents\\Git\\OOP_ex2\\data\\Graphs_on_circle\\G_20000_160000_1.json");
        ga.scc();
    }

    @Test
    void ex3_timing_test6() {
        DWGraph_DS g1 = new DWGraph_DS();
        DWGraph_Algo ga = new DWGraph_Algo();
        ga.init(g1);
        ga.load("C:\\Users\\shmue\\Documents\\Git\\OOP_ex2\\data\\Graphs_on_circle\\G_30000_240000_1.json");
        ga.scc();
    }
}
