package gameClient;
import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Ex2 implements Runnable {
    private static MyFrame _win;
    private static Arena _ar;

    public static void main(String[] args) {
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
        Integer scenario = 11;
        game_service game = Game_Server_Ex2.getServer(scenario);
        directed_weighted_graph game_graph = loadGraphJson(game.getGraph(),scenario.toString());
        String game_info = game.toString();
        init(game);
        game.startGame();
        _win.setTitle("Ex2 - OOP Test Num: "+scenario.toString()+" "+game.toString());
        int ind=0;
        long dt=100;
        while (game.isRunning()) {
            moveAgents3(game, game_graph);
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

    public static void moveAgents2(game_service game, directed_weighted_graph graph_game) {
        String agents_status = game.getAgents();
        String pokemons_json =  game.getPokemons();
        List<CL_Agent> agents_arr = Arena.getAgents(agents_status, graph_game);
        List<CL_Pokemon> pokemons_arr = Arena.json2Pokemons(pokemons_json);
        updateAllEdges(pokemons_arr, agents_arr, graph_game);
        _ar.setAgents(agents_arr);
        _ar.setPokemons(pokemons_arr);
        for(int i=0; i<agents_arr.size(); i++) {
            CL_Agent current_agent = agents_arr.get(i);
            int dest = current_agent.getNextNode();
            int src = current_agent.getSrcNode();
            if(dest == -1) {
                //CL_Pokemon closest = returnClosestPokemon(pokemons_arr,current_agent,graph_game);
                dest = chooseNextNode(graph_game, src, pokemons_arr.get(i));
                game.chooseNextEdge(current_agent.getID(), dest);
                System.out.println("Agent: "+current_agent.getID()+", val: "+current_agent.getValue()+"   turned to node: "+dest);
            }
        }
        game.move();
    }

    public static void moveAgents3(game_service game, directed_weighted_graph graph_game) {
        String agents_status = game.getAgents();
        String pokemons_json =  game.getPokemons();
        List<CL_Agent> agents_arr = Arena.getAgents(agents_status, graph_game);
        List<CL_Pokemon> pokemons_arr = Arena.json2Pokemons(pokemons_json);
        updateAllEdges(pokemons_arr, agents_arr, graph_game);
        _ar.setAgents(agents_arr);
        _ar.setPokemons(pokemons_arr);
        for(int i=0; i<agents_arr.size(); i++) {
            CL_Agent current_agent = agents_arr.get(i);
            int dest = current_agent.getNextNode();
            int src = current_agent.getSrcNode();
            if(dest == -1) {
                CL_Pokemon closest = returnClosestPokemon(pokemons_arr,current_agent,graph_game);
                if (closest.isTargeted()) {
                    for (CL_Pokemon next_closest : returnClosestPokemonArr(pokemons_arr,current_agent,graph_game)) {
                        if (!next_closest.isTargeted()) {
                            next_closest.targetPokemon();
                            dest = chooseNextNode(graph_game, src, next_closest);
                            game.chooseNextEdge(current_agent.getID(),dest);
                            System.out.println("Agent: "+current_agent.getID()+", val: "+current_agent.getValue()+"   turned to node: "+dest);
                        }
                    }
                }
                else {
                    closest.targetPokemon();
                    dest = chooseNextNode(graph_game, src, closest);
                    game.chooseNextEdge(current_agent.getID(),dest);
                    System.out.println("Agent: "+current_agent.getID()+", val: "+current_agent.getValue()+"   turned to node: "+dest);
                }
            }
        }
        game.move();
    }

    public static void moveAgents(game_service game, directed_weighted_graph graph_game) {
        String agents_status = game.getAgents();
        String pokemons_json =  game.getPokemons();
        List<CL_Agent> agents_arr = Arena.getAgents(agents_status, graph_game);
        List<CL_Pokemon> pokemons_arr = Arena.json2Pokemons(pokemons_json);
        resetTargeting(pokemons_arr);
        updateAllEdges(pokemons_arr, agents_arr, graph_game);
        _ar.setAgents(agents_arr);
        _ar.setPokemons(pokemons_arr);
        for(int i=0; i < agents_arr.size(); i++) {
            CL_Agent current_agent = agents_arr.get(i);
            if (current_agent.getCurrentTarget() != null && current_agent.get_curr_edge() != null) {
                if (current_agent.checkFormerTarget() == current_agent.getCurrentTarget()
                        && current_agent.getCurrentTarget().get_edge() == current_agent.get_curr_edge()) {
                    current_agent.getCurrentTarget().blacklist();
                }
            }
            int dest = current_agent.getNextNode();
            int src = current_agent.getSrcNode();
            if(dest == -1) {
                CL_Pokemon closest = returnClosestPokemon(pokemons_arr,current_agent,graph_game);
                if (!closest.isTargeted() && !closest.checkIfBlacklisted()) {
                    current_agent.setFormerTarget(current_agent.getCurrentTarget());
                    dest = chooseNextNode(graph_game, src, closest);
                    game.chooseNextEdge(current_agent.getID(), dest);
                    current_agent.setCurrentTarget(closest);
                    System.out.println("Agent: "+current_agent.getID()+", val: "+current_agent.getValue()+"   turned to node: "+dest);
                }
                else {
                    for (CL_Pokemon pokemon : returnClosestPokemonArr(pokemons_arr,current_agent,graph_game)) {
                        if (!pokemon.isTargeted() && !pokemon.checkIfBlacklisted()) {
                            current_agent.setFormerTarget(current_agent.getCurrentTarget());
                            dest = chooseNextNode(graph_game, src, pokemon);
                            game.chooseNextEdge(current_agent.getID(), dest);
                            current_agent.setCurrentTarget(pokemon);
                            System.out.println("Agent: "+current_agent.getID()+", val: "+current_agent.getValue()+"   turned to node: "+dest);
                            break;
                        }
                    }
                }

/*                if (agents_arr.get(i).getCurrentTarget() != pokemons_arr.get(i)) {
                    agents_arr.get(i).setCurrentTarget(pokemons_arr.get(i));
                    dest = chooseNextNode(graph_game, src, pokemons_arr.get(i));
                    game.chooseNextEdge(agent.getID(), dest);
                    System.out.println("Agent: "+agent.getID()+", val: "+v+"   turned to node: "+dest);
                }*/
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

    public static void updateAllEdges(List<CL_Pokemon> pokemons_arr,List<CL_Agent> agents_arr, directed_weighted_graph graph) {
        for (CL_Pokemon pokemon : pokemons_arr) {
            Arena.updateEdge(pokemon, graph);
        }
/*        for (CL_Agent agent : agents_arr) {
            Arena.updateEdge(agent, graph);
        }*/
    }

    public static CL_Pokemon returnClosestPokemon(List<CL_Pokemon> pokemons_arr, CL_Agent agent, directed_weighted_graph graph) {
        dw_graph_algorithms graph_algo = new DWGraph_Algo();
        graph_algo.init(graph);
        double path_lengh;
        HashMap<Double,CL_Pokemon> distance_map = new HashMap<>();
        List<Double> distances = new ArrayList<>();
        for (CL_Pokemon pokemon : pokemons_arr) {
            path_lengh = graph_algo.shortestPathDist(agent.getSrcNode(),pokemon.get_edge().getDest());
            distance_map.put(path_lengh,pokemon);
            distances.add(path_lengh);
        }
        Collections.sort(distances);
        return distance_map.get(distances.get(0));
    }

    public static List<CL_Pokemon> returnClosestPokemonArr(List<CL_Pokemon> pokemons_arr, CL_Agent agent, directed_weighted_graph graph) {
        dw_graph_algorithms graph_algo = new DWGraph_Algo();
        List<CL_Pokemon> result = new ArrayList<>();
        graph_algo.init(graph);
        double path_lengh;
        HashMap<Double,CL_Pokemon> distance_map = new HashMap<>();
        List<Double> distances = new ArrayList<>();
        for (CL_Pokemon pokemon : pokemons_arr) {
            path_lengh = graph_algo.shortestPathDist(agent.getSrcNode(),pokemon.get_edge().getDest());
            distance_map.put(path_lengh,pokemon);
            distances.add(path_lengh);
        }
        Collections.sort(distances);
        for (int i=0; i<distances.size(); i++) {
            result.add(distance_map.get(distances.get(i)));
        }
        return result;
    }

    public static void resetTargeting(List<CL_Pokemon> pokemons_arr) {
        for (CL_Pokemon pokemon : pokemons_arr) {
            pokemon.untargetPokemon();
        }
    }
}
