package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Ex2 implements Runnable {
    private static MyFrame _win;
    private static Arena _ar;
    public static long _sleep_time = 0;
    public static directed_weighted_graph _game_graph;

    public static void main(String[] args) {
        Thread client = new Thread(new Ex2());
        client.start();
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
        init(game);
        setAgentsTargetedArea(_ar.getAgents(),_ar.getGraph());
        game.startGame();
        int ind = 0;
        _win.setTitle("Ex2 - OOP Test Num: "+scenario.toString()+" "+game.toString());
        while (game.isRunning()) {
            moveAgents(game, this._game_graph);
            game.move();
            try {
                if(ind % 1==0) {
                    Thread.sleep(_sleep_time);
                    _win.repaint();
                    ind++;
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(game.toString());
        System.exit(0);
    }

    public synchronized void init(game_service game) {
        this._game_graph = Arena.parseGraph(game.getGraph());
        _ar = new Arena(game);
        _win = new MyFrame("test Ex2");
        _win.setSize(1200, 900);
        _win.update(_ar);
        _win.show();
        placeAgents(game);
        _ar.updateArena(game);
    }

    public static void moveAgents(game_service game, directed_weighted_graph graph_game) {
        _ar.updateArena(game);
        List<CL_Agent> agents_arr = _ar.getAgents();
        List<CL_Pokemon> pokemons_arr = _ar.getPokemons();
        updateAllEdges(pokemons_arr, graph_game);
        resetTargeting(pokemons_arr);
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
                            current_agent.setCurrentTarget(next_closest);
                            dest = chooseNextNode(graph_game, src, next_closest);
                            game.chooseNextEdge(current_agent.getID(),dest);
                            System.out.println(current_agent.toString()+" turned to node: "+dest+" SP "+current_agent.getSpeed());
                        }
                    }
                }
                else {
                    closest.targetPokemon();
                    current_agent.setCurrentTarget(closest);
                    dest = chooseNextNode(graph_game, src, closest);
                    game.chooseNextEdge(current_agent.getID(),dest);
                    System.out.println(current_agent.toString()+" turned to node: "+dest+" SP "+current_agent.getSpeed());
                }
            }
            //setTimeToSleep(current_agent, dest, graph_game);
        }
        game.move();
    }

    private synchronized static int chooseNextNode(directed_weighted_graph graph, int src, CL_Pokemon pokemon) {
        dw_graph_algorithms graph_algo = new DWGraph_Algo();
        graph_algo.init(graph);
        List<node_data> poke_path;
        poke_path = graph_algo.shortestPath(src,Arena.getPokemonEdge(pokemon, graph).getDest());
        if (poke_path.size() > 1) {
            return poke_path.get(1).getKey();
        }
        else return Arena.getPokemonEdge(pokemon, graph).getSrc();
    }

    public synchronized static void setupAgents(game_service game, directed_weighted_graph graph_game) {
        String agents_status = game.getAgents();
        String pokemons_json =  game.getPokemons();
        List<CL_Agent> agents_arr = Arena.getAgents(agents_status, graph_game);
        List<CL_Pokemon> pokemons_arr = Arena.json2Pokemons(pokemons_json);
        updateAllEdges(pokemons_arr, graph_game);
        _ar.setAgents(agents_arr);
        _ar.setPokemons(pokemons_arr);
        for(int i=0; i<agents_arr.size(); i++) {
            CL_Agent current_agent = agents_arr.get(i);
            int dest = current_agent.getNextNode();
            int src = current_agent.getSrcNode();
            if(dest == -1) {
                CL_Pokemon closest = returnClosestPokemon(pokemons_arr,current_agent,graph_game);
                dest = chooseNextNode(graph_game, src, closest);
                game.chooseNextEdge(current_agent.getID(), dest);
                System.out.println("Agent: "+current_agent.getID()+", val: "+current_agent.getValue()+"   turned to node: "+dest);
            }
        }
    }

    public synchronized static void updateAllEdges(List<CL_Pokemon> pokemons_arr, directed_weighted_graph graph) {
        for (CL_Pokemon pokemon : pokemons_arr) {
            Arena.updateEdge(pokemon, graph);
        }
    }

    public synchronized static CL_Pokemon returnClosestPokemon(List<CL_Pokemon> pokemons_arr, CL_Agent agent, directed_weighted_graph graph) {
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

    public static void placeAgents(game_service game) {
        PriorityQueue<CL_Pokemon> pokemon_value_queue = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(CL_Pokemon poke1, CL_Pokemon poke2) {
                if (poke1.getValue() > poke2.getValue()) return -1;
                else if (poke1.getValue() < poke2.getValue()) return 1;
                else return 0;
            }
        });
        pokemon_value_queue.addAll(_ar.getPokemons());
        JsonObject json_obj = JsonParser.parseString(game.toString()).getAsJsonObject();
        json_obj.getAsJsonObject("GameServer").get("agents").getAsInt();
        int agents_number = json_obj.getAsJsonObject("GameServer").get("agents").getAsInt();
        int treated_agents = agents_number;
        for(int i = 0; i < agents_number; i++) {
            if (!pokemon_value_queue.isEmpty()) {
                CL_Pokemon c_pokemon = pokemon_value_queue.poll();
                int pokemon_dest_node = c_pokemon.get_edge().getSrc();
                if(c_pokemon.getType() < 0) {
                    pokemon_dest_node = c_pokemon.get_edge().getDest();
                }
                game.addAgent(pokemon_dest_node);
                treated_agents--;
            }
        }
        while (treated_agents > 0) {
            game.addAgent(ThreadLocalRandom.current().nextInt(1, _game_graph.nodeSize()));
            treated_agents--;
        }
    }

    public synchronized static List<CL_Pokemon> returnClosestPokemonArr(List<CL_Pokemon> pokemons_arr, CL_Agent agent, directed_weighted_graph graph) {
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

    public synchronized static void resetTargeting(List<CL_Pokemon> pokemons_arr) {
        for (CL_Pokemon pokemon : pokemons_arr) {
            pokemon.untargetPokemon();
        }
    }

    public synchronized static void setAgentsTargetedArea(List<CL_Agent> agents, directed_weighted_graph graph) {
        if (agents.size() <= 2) {
            return;
        }
        int nodes_per_area = (int) Math.ceil(((graph.nodeSize()*1.0) / (agents.size()*1.0)) + 1);
        System.out.println(nodes_per_area);
        for (CL_Agent agent : agents) {
            int outer_index = 0;
            int neighbor = 0;
            HashSet<Integer> result = new HashSet<>();
            while (outer_index < nodes_per_area) {
                if (graph.getE(neighbor) != null && !result.contains(neighbor)) {
                    for (edge_data edge : graph.getE(outer_index)) {
                        result.add(edge.getDest());
                        result.add(edge.getSrc());
                    }
                    neighbor++;
                    outer_index = result.size();
                    agent.setTargetedArea(result);
                }
                else if (result.contains(neighbor)) neighbor++;
            }
        }
    }

    public static synchronized long setTimeToSleep(CL_Agent agent, int target, directed_weighted_graph graph) {
        if (target == -1) {
            return 100;
        }
        edge_data compared_edge = _ar.getGraph().getEdge(agent.getSrcNode(), target);
        node_data node = graph.getNode(target);
        if (agent.getCurrentTarget() != null) {
            if (agent.getCurrentTarget().get_edge().equals(compared_edge)) {
                double tmp;
                double path_to_target = agent.getLocation().distance(agent.getCurrentTarget().getLocation());
                double way_to_node = agent.getLocation().distance(node.getLocation());
                tmp = (((path_to_target / way_to_node)*compared_edge.getWeight())/agent.getSpeed())*1000;
                _sleep_time = (long) tmp;
            }
        }
        return 100;
    }
}
