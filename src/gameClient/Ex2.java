package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gameClient.util.Gframe;
import gameClient.util.compAdapt;
import gameClient.util.myMusic;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Ex2 implements Runnable {
    private static Gframe gframe;
    private int scenario;
    private static Arena _ar;
    public static long _sleep_time = 90;
    private compAdapt adapt;///
    public static directed_weighted_graph _game_graph;

    public synchronized static void main(String[] args) {
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
    public synchronized void run() {
        gframe = new Gframe();
        adapt = new compAdapt();/////////////
        adapt.setFrame(gframe);//////////////
        gframe.addComponentListener(adapt); //////////
        gframe.setSize(800, 600);
        gframe.setResizable(true);
        gframe.setDefaultCloseOperation(EXIT_ON_CLOSE);
        gframe.initMain();
        gframe.show();
        long fps = 2;
        while(!gframe.getPressed()) {
            try {
                gframe.repaint();
                Thread.sleep(fps);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        myMusic song2 = new myMusic(2);
        this.scenario = Integer.parseInt(gframe.getJTextString());
        gframe.setVisible(false);
        gframe.dispose();//////
        game_service game = Game_Server_Ex2.getServer(this.scenario);
        init(game);
        setAgentsTargetedArea(_ar.getAgents(),_ar.getGraph());
        game.startGame();
        int ind = 0;
        gframe.setTitle("Pokemon Game - Scenario number: "+scenario);
        while (game.isRunning()) {
            moveAgents(game);
            game.move();
            try {
                if(ind % 1==0) {
                    Thread.sleep(_sleep_time);
                    gframe.repaint();
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
        placeAgents(game);
        gframe = new Gframe();
        gframe.addComponentListener(adapt);//////
        gframe.setSize(800, 600);
        gframe.updategame(_ar);
        gframe.show();
        _ar.updateArena(game);
    }

    public synchronized static void moveAgents(game_service game) {
        _ar.updateArena(game);
        List<CL_Agent> agents = _ar.getAgents();
        List<CL_Pokemon> pokemons = _ar.getPokemons();
        resetTargeting(pokemons);
        chooseTargets(game,agents,pokemons);
        game.move();
    }

    private synchronized static void chooseTargets(game_service game, List<CL_Agent> agents, List<CL_Pokemon> pokemons) {
        for(int i=0; i<agents.size(); i++) {
            CL_Agent current_agent = agents.get(i);
            int dest = current_agent.getNextNode();
            int src = current_agent.getSrcNode();
            if(dest == -1) {
                CL_Pokemon closest = returnClosestPokemon(pokemons,current_agent,_game_graph);
                if (closest.isTargeted()) {
                    for (CL_Pokemon next_closest : returnClosestPokemonArr(pokemons,current_agent,_game_graph)) {
                        if (!next_closest.isTargeted()) {
                            next_closest.targetPokemon();
                            current_agent.setCurrentTarget(next_closest);
                            dest = chooseNextNode(_game_graph, current_agent, next_closest);
                            current_agent.setNextNode(dest);
                            game.chooseNextEdge(current_agent.getID(),dest);
                            if (checkProximityCase(current_agent, pokemons) != null) {
                                dest = checkProximityCase(current_agent, pokemons).poll().get_edge().getDest();
                            }
                            if (current_agent.get_curr_edge() != null) {
                                System.out.println(current_agent.get_curr_edge().toString() + "  " + closest.get_edge().toString());
                                System.out.println(current_agent.toString()+src+" -> "+dest+" SP "+current_agent.getSpeed());
                            }
                            else {
                                System.out.println(current_agent.get_curr_edge() + "  " + closest.get_edge().toString());
                                System.out.println(current_agent.toString()+src+" -> "+dest+" SP "+current_agent.getSpeed());
                            }
                        }
                    }
                }
                else {
                    closest.targetPokemon();
                    current_agent.setCurrentTarget(closest);
                    dest = chooseNextNode(_game_graph, current_agent, closest);
                    current_agent.setNextNode(dest);
                    if (checkProximityCase(current_agent, pokemons) != null) {
                        dest = checkProximityCase(current_agent, pokemons).poll().get_edge().getDest();
                    }
                    game.chooseNextEdge(current_agent.getID(),dest);
                    if (current_agent.get_curr_edge() != null) {
                        System.out.println(current_agent.get_curr_edge().toString() + "  " + closest.get_edge().toString());
                        System.out.println(current_agent.toString()+src+" -> "+dest+" SP "+current_agent.getSpeed());
                    }
                    else {
                        System.out.println(current_agent.get_curr_edge() + "  " + closest.get_edge().toString());
                        System.out.println(current_agent.toString()+src+" -> "+dest+" SP "+current_agent.getSpeed());
                    }
                }
            }
            //setTimeToSleep(current_agent, dest, graph_game);
        }
    }

    private synchronized static int chooseNextNode(directed_weighted_graph graph, CL_Agent agent, CL_Pokemon pokemon) {
        dw_graph_algorithms graph_algo = new DWGraph_Algo();
        graph_algo.init(graph);
        List<node_data> poke_path;
/*        if (agent.get_curr_edge().equals(pokemon.get_edge())) {
            return agent.get_curr_edge().getDest();
        }*/
        if (pokemon.getType() > 0) {
            int targeted_node = Math.min(pokemon.get_edge().getDest(),pokemon.get_edge().getSrc());
            poke_path = graph_algo.shortestPath(agent.getSrcNode(),targeted_node);
            if (poke_path != null) {
                if (poke_path.size() > 1) {
                    return poke_path.get(1).getKey();
                }
                //else return poke_path.get(0).getKey();
            }
            return Math.max(pokemon.get_edge().getDest(),pokemon.get_edge().getSrc());
        }
        else {
            int targeted_node = Math.max(pokemon.get_edge().getDest(),pokemon.get_edge().getSrc());
            poke_path = graph_algo.shortestPath(agent.getSrcNode(),targeted_node);
            if (poke_path != null) {
                if (poke_path.size() > 1) {
                    return poke_path.get(1).getKey();
                }
                //else return poke_path.get(0).getKey();
            }
            return Math.min(pokemon.get_edge().getDest(),pokemon.get_edge().getSrc());
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

    /**
     * Handles the case an agent is currently in a node that has 1 or more
     * pokemons in proximity. In that case sets the agent to immediately eat
     * the pokemon with the highest value
     */
    public synchronized static PriorityQueue<CL_Pokemon> checkProximityCase(CL_Agent agent, List<CL_Pokemon> pokemons) {
        PriorityQueue<CL_Pokemon> poke_queue = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(CL_Pokemon poke1, CL_Pokemon poke2) {
                if (poke1.getValue() > poke2.getValue()) return -1;
                else if (poke1.getValue() < poke2.getValue()) return 1;
                else return 0;
            }
        });
        for (edge_data edge : _game_graph.getE(agent.getSrcNode())) {
            for (CL_Pokemon pokemon : pokemons) {
                if(Arena.isOnEdge(edge, pokemon, _game_graph)) {
                    if (pokemon.getType() > 0 && (pokemon.get_edge().getSrc() < pokemon.get_edge().getDest())) {
                        poke_queue.add(pokemon);
                    }
                    else if (pokemon.getType() < 0 && (pokemon.get_edge().getSrc() > pokemon.get_edge().getDest())) {
                        poke_queue.add(pokemon);
                    }
                }
            }
        }
        if (!poke_queue.isEmpty()) return poke_queue;
        else return null;
    }



    public synchronized static void placeAgents(game_service game) {
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
                if (c_pokemon.getType() > 0) {
                    Math.min(c_pokemon.get_edge().getSrc(),c_pokemon.get_edge().getDest());
                    game.addAgent(Math.min(c_pokemon.get_edge().getSrc(),c_pokemon.get_edge().getDest()));
                }
                else if (c_pokemon.getType() < 0) {
                    Math.max(c_pokemon.get_edge().getSrc(),c_pokemon.get_edge().getDest());
                    game.addAgent(Math.max(c_pokemon.get_edge().getSrc(),c_pokemon.get_edge().getDest()));
                }
                treated_agents--;
            }
        }
        while (treated_agents > 0) {
            game.addAgent(ThreadLocalRandom.current().nextInt(1, _game_graph.nodeSize()));
            treated_agents--;
        }
        List<CL_Agent> result_agents_arr = Arena.initAgentsFromJson(game.getAgents());
        _ar.setAgents(result_agents_arr);
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

    public synchronized void updateAgentsFormerValue(List<CL_Agent> agents, game_service game) {
        String agents_json = game.getAgents();
        for (CL_Agent agent : agents) {

        }
    }

    public static synchronized int avoidRepeat(CL_Pokemon pokemon, CL_Agent agent) {
        for (edge_data edge : _game_graph.getE(agent.getSrcNode())) {
            if (edge.equals(pokemon.get_edge())) {
                if (pokemon.getType() > 0) {
                    return pokemon.get_edge().getDest();
                }
                else return pokemon.get_edge().getSrc();
            }
        }
        return -1;
    }

    public synchronized static void checkIfEaten(List<CL_Agent> agents) {
        boolean flag = true;
        for (CL_Agent agent : agents) {
            if (agent.getCurrentTarget() != null) {
                if (agent.getCurrentTarget().get_edge().getDest() == agent.getSrcNode()) {

                }
            }
        }
    }
}
