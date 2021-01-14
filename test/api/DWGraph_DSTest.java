package api;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_DSTest {

    /**
     * Creates this graph for testing:
     * (WikiPictures/testgraph.jpg)
     * @return WGraph_DS - an initialized graph with the above settings
     */
    public static directed_weighted_graph mainTestGraph(){
        directed_weighted_graph wg = new DWGraph_DS();
        for (int i=1; i<=5; i++) {
            geo_location location = new GeoLocation(2.22,3.33,4.44);
            node_data tmp_node = new NodeData(i);
            tmp_node.setLocation(location);
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

    @Test
    void remove_edge() {
        directed_weighted_graph wg = mainTestGraph();
        assertEquals(5,wg.edgeSize());
        assertEquals(7,wg.getEdge(3,5).getWeight());
        assertEquals(4,wg.getEdge(5,3).getWeight());
        wg.removeEdge(3,5);
        assertEquals(4,wg.edgeSize());
        assertNull(wg.getEdge(3,5));
        assertEquals(4,wg.getEdge(5,3).getWeight());
    }

    @Test
    void remove_node() {
        directed_weighted_graph wg = mainTestGraph();
        assertEquals(5,wg.edgeSize());
        assertEquals(7,wg.getEdge(3,5).getWeight());
        assertEquals(4,wg.getEdge(5,3).getWeight());
        wg.removeNode(3);
        assertEquals(2,wg.edgeSize());
        assertNull(wg.getEdge(3,5));
    }

    public void genEdge(DWGraph_DS g, int how_many_e){
        int max = Integer.MIN_VALUE; int min = Integer.MAX_VALUE;
        Iterator<node_data> iti = g.getV().iterator();
        while( iti.hasNext()){
            node_data sus = iti.next();
            if( sus.getKey() > max){
                max = sus.getKey();
            }if( sus.getKey() < min){
                min = sus.getKey();
            }
        }
        for ( int i = 0 ; i < how_many_e; i++){
            int r_num_S = (int) (Math.random() *(max - min ) + min );
            int r_num_D = (int) (Math.random() *(max - min ) + min );
            double r_num_W = Math.random()*10;
            if ((g.getNode(r_num_S) !=null && g.getNode(r_num_D) != null) && r_num_D != r_num_S &&(g.getEdge(r_num_S,r_num_D) == null) ){
                g.connect(r_num_S,r_num_D,r_num_W);
            }else i--;
        }
    }
}