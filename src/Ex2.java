import Server.Game_Server_Ex2;
import api.DWGraph_Algo;
import api.directed_weighted_graph;
import api.edge_data;
import api.game_service;
import gameClient.Arena;
import gameClient.CL_Agent;
import gameClient.CL_Pokemon;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Ex2 implements Runnable {
    private static Gframe gframe;
    private static Arena _ar;
    private int scenario;



    public static void main(String[] a) {
       Ex2 play= new Ex2();
       Thread client = new Thread(play);
       client.start();
    }
    @Override
    public void run() {

        gframe = new Gframe();
        gframe.setSize(800, 600);
        gframe.setResizable(true);
        gframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gframe.initMain();
        myMusic song1 = new myMusic(0);
        // add a thread
        gframe.show();
        long fps =2;
        while( ! gframe.getPressed()) {
            try {
                gframe.repaint();
                Thread.sleep(fps);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        myMusic song2 = new myMusic(2);
        // add a thread
        this.scenario = Integer.parseInt(gframe.getJTextString());
        game_service game = Game_Server_Ex2.getServer(this.scenario);
        String graphFromFile = GraphToFile(game.getGraph());
        DWGraph_Algo graph_algo = new DWGraph_Algo();
        graph_algo.load(graphFromFile);
        directed_weighted_graph workingGraph = graph_algo.getGraph();



        init(game, workingGraph);
        game.startGame();
        gframe.setTitle("Pokemon Game - Scenario number: "+scenario);

        int ind=0;
        long dt=0;
        while(game.isRunning() ) {
            moveAgants(game, workingGraph);
            try {
                gframe.repaint();
                Thread.sleep(dt);
                ind++;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        String res = game.toString();
        System.out.println(res);
        System.exit(0);
    }
    private  void init(game_service game,directed_weighted_graph workingGraph){
        String s_pokemons = game.getPokemons();
        String s_info = game.toString();
        _ar = new Arena(game);
        _ar.setGraph(workingGraph);
        _ar.setPokemons(Arena.initPokemonsFromJson(s_pokemons));
        gframe = new Gframe();
        gframe.setSize(800, 600);
        gframe.updategame(_ar);
        gframe.show();
        JSONObject line;
        try {
            line = new JSONObject(s_info);
            JSONObject gameString = line.getJSONObject("GameServer");
            int asize = gameString.getInt("agents");
            ArrayList<CL_Pokemon> pArrlist = Arena.initPokemonsFromJson(s_pokemons);
            for(int i = 0; i < pArrlist.size() ;i++) {
                Arena.updateEdge(pArrlist.get(i),workingGraph);
            }
            for(int i = 0;i < asize ;i++) {
                int pokemon = (i % (pArrlist.size()));
                CL_Pokemon p = pArrlist.get(pokemon);
                game.addAgent(pokemon);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private static void moveAgants(game_service game, directed_weighted_graph gg) {

        List<CL_Agent> log = Arena.initAgentsFromJson(game.getAgents());
        _ar.setAgents(log);
        String fs =  game.getPokemons();
        List<CL_Pokemon> ffs = Arena.initPokemonsFromJson(fs);
        _ar.setPokemons(ffs);
        for(int i=0;i<log.size();i++) {
            CL_Agent ag = log.get(i);
            int id = ag.getID();
            int dest = ag.getNextNode();
            int src = ag.getSrcNode();
            double v = ag.getValue();
            if(dest==-1) {
                dest = nextNode(gg, src);
                game.chooseNextEdge(ag.getID(), dest);
                //System.out.println("Agent: "+id+", val: "+v+"   turned to node: "+dest);
            }
        }
    }
    private static int nextNode(directed_weighted_graph g, int src) {
        int ans = -1;
        Collection<edge_data> ee = g.getE(src);
        Iterator<edge_data> itr = ee.iterator();
        int s = ee.size();
        int r = (int)(Math.random()*s);
        int i=0;
        while(i<r) {itr.next();i++;}
        ans = itr.next().getDest();
        return ans;
    }
    public static String GraphToFile(String graph){
        String location = "C://Users/Gidon/Desktop/Pokemon/0.txt";
        try{
            FileWriter output = new FileWriter(location);
            output.write(graph);
            output.flush();
            return location;

        }catch (IOException i){
            i.printStackTrace();
        }
        return null;
    }

}
