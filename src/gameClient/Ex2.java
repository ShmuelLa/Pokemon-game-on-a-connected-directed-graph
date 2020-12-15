package gameClient;
import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Ex2 implements Runnable {
    private static MyFrame _win;
    private static Arena _ar;

    public static void main(String[] args) {
        //BADtest1();
        Thread client = new Thread(new Ex2());
        client.start();
    }

    private static directed_weighted_graph loadGraphJson(String Json, String filename) {
        directed_weighted_graph game_graph = new DWGraph_DS();
        dw_graph_algorithms game_graph_algo = new DWGraph_Algo();
        game_graph_algo.init(game_graph);
        try {
            FileWriter output = new FileWriter("tests/IO/"+filename);
            output.write(Json);
            output.flush();
            game_graph_algo.load("tests/IO/"+filename);
            return game_graph_algo.getGraph();
        } catch (IOException e) {
            System.out.println("Wrong Input");
            e.printStackTrace();
        }
        return game_graph_algo.getGraph();
    }

    public static void BADtest1() {
        game_service game = Game_Server_Ex2.getServer(2); // you have [0,23] games
        String graph = game.getGraph();
        directed_weighted_graph getgraph = game.getJava_Graph_Not_to_be_used();
        //game.login(12345);  // please use your ID only as a key. uncomment this will upload your results to the server
        node_data nn = getgraph.getNode(10);
        String info = game.toString();
        System.out.println(info);
        System.out.println(graph);
        System.out.println(game.getPokemons());
        int src_node = 0;  // arbitrary node, you should start at one of the fruits
        game.addAgent(src_node);
        game.startGame();
        int i=0;
        while(game.isRunning()) {
            long t = game.timeToEnd();
            String lg = game.move();
            List<CL_Agent> log = Arena.getAgents(lg, getgraph);
            for(int a=0;a< log.size();a++) {
                CL_Agent r = log.get(a);
                int dest = r.getNextNode();
                int src = r.getSrcNode();
                int id = r.getID();
                if(dest==-1) {
                    int new_dest = BADnextNode(getgraph, src);
                    game.chooseNextEdge(id, new_dest);
                    System.out.println(i+") "+a+") "+r+"  move to node: "+new_dest);
                }
            }
            i++;
        }
    }

    /**
     * a very simple random walk implementation!
     * @param g
     * @param src
     * @return
     */
    private static int BADnextNode(directed_weighted_graph g, int src) {
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

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        Integer scenario = 0;
        game_service game = Game_Server_Ex2.getServer(scenario);
        directed_weighted_graph game_graph = loadGraphJson(game.getGraph(),scenario.toString());
        String game_info = game.toString();
        System.out.println(game_info);
        System.out.println(game.getPokemons());
        init(game);
        game.startGame();
        _win.setTitle("Ex2 - OOP Test Num: "+scenario.toString()+" "+game.toString());
        int ind=0;
        long dt=100;
        while (game.isRunning()) {
            moveAgents(game, game_graph);
            try {
                if(ind % 1==0) {
                    _win.repaint();
                }
                Thread.sleep(dt);
                ind++;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        String game_status = game.toString();
        System.out.println(game_status);
        System.exit(0);
    }

    public static void moveAgents(game_service game, directed_weighted_graph graph_game) {
        String agents_status = game.getAgents();
        String pokemons_json =  game.getPokemons();
        List<CL_Agent> agents_arr = Arena.getAgents(agents_status, graph_game);
        List<CL_Pokemon> pokemons_arr = Arena.json2Pokemons(pokemons_json);
        _ar.setAgents(agents_arr);
        _ar.setPokemons(pokemons_arr);
        //ArrayList<OOP_Point3D> rs = new ArrayList<OOP_Point3D>();
        for(int i=0; i<agents_arr.size(); i++) {
            CL_Agent agent = agents_arr.get(i);
            int id = agent.getID();
            int dest = agent.getNextNode();
            int src = agent.getSrcNode();
            double v = agent.getValue();
            if(dest == -1) {
                dest = chooseNextNode(graph_game, src, pokemons_arr.get(i));
                game.chooseNextEdge(agent.getID(), dest);
                System.out.println("Agent: "+id+", val: "+v+"   turned to node: "+dest);
            }
        }
        game.move();
    }

    private static int chooseNextNode(directed_weighted_graph graph, int src, CL_Pokemon pokemon) {
        dw_graph_algorithms graph_algo = new DWGraph_Algo();
        graph_algo.init(graph);
        List<node_data> poke_path;
        poke_path = graph_algo.shortestPath(src,Arena.getPokemonEdge(pokemon, graph).getDest());
        if (poke_path.size() > 1) {
            return poke_path.get(1).getKey();
        }
        else return Arena.getPokemonEdge(pokemon, graph).getSrc();
    }

    public void init(game_service game) {
        String graph = game.getGraph();
        String pokemons = game.getPokemons();
        directed_weighted_graph game_graph = loadGraphJson(game.getGraph(),"1");
        _ar = new Arena();
        _ar.setGraph(game_graph);
        _ar.setPokemons(Arena.json2Pokemons(pokemons));
        _win = new MyFrame("test Ex2");
        _win.setSize(1200, 900);
        _win.update(_ar);
        _win.show();
        String info = game.toString();
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject text_jason = line.getJSONObject("GameServer");
            int agents_number = text_jason.getInt("agents");
            System.out.println(info);
            System.out.println(game.getPokemons());
            ArrayList<CL_Pokemon> pokemons_arr = Arena.json2Pokemons(game.getPokemons());
            //int agent_src_node = Arena.getPokemonEdge(pokemons_arr.get(0),game_graph).getDest();;  // arbitrary node, you should start at one of the pokemon
            for(int i = 0; i < pokemons_arr.size(); i++) {
                Arena.updateEdge(pokemons_arr.get(i),game_graph);
            }
            for(int i = 0; i < agents_number; i++) {
                int ind = i % pokemons_arr.size();
                CL_Pokemon c_pokemon = pokemons_arr.get(ind);
                int pokemon_dest_node = c_pokemon.get_edge().getDest();
                if(c_pokemon.getType() < 0) {
                    pokemon_dest_node = c_pokemon.get_edge().getSrc();
                }
                game.addAgent(pokemon_dest_node);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
