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

/**
 * The main algorithmic class of the project. This class contains and manages the whole project by
 * relying on the Arena class. First we initialize a game with a specific scenario and than
 * each and every step in the game is planned by the rules and funtions in that class.
 *
 * @author gison.avziz & shmuel.lavian
 */
public class Ex2 implements Runnable {
    private static Gframe gframe;
    private int _scenario;
    private compAdapt adapt;///
    private static Arena _arena;
    public static long _sleep_time = 20;
    private static game_service _game;
    public static directed_weighted_graph _game_graph;
    private static final double _proximity_factor = 7;

    public synchronized static void main(String[] args) {
        Thread client = new Thread(new Ex2());
        client.start();
    }

    /**
     * An Ex2 Trivial constructor
     *
     */
    public Ex2() {
    }

    /**
     * Constructs an Ex2 object for a specific stage,
     * used mainly for testing
     *
     * @param scenario The chosen game stage level
     */
    public Ex2(int scenario) {
        this._scenario = scenario;
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
        gframe.initMain(1,1);
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
        this._scenario = Integer.parseInt(gframe.getJTextString());
        gframe.setVisible(false);
        gframe.dispose();//////
        _game = Game_Server_Ex2.getServer(this._scenario);
        init();
        gframe.setTitle("Pokemon Game - Scenario number: "+_scenario);
        setAgentsTargetedArea(_arena.getAgents());
        _game.startGame();
        int ind = 0;
        while (_game.isRunning()) {
            try {
                moveAgents();
                _game.move();
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
       System.out.println(_game.toString());
       System.exit(0);
    }

    /**
     * Initializes a new game by creating a game Arena Object
     * and initializing the current frame for the game and constantly updates
     * all the game objects including the graph, pokemons and agents. This is very important
     * because these objects can change mid-game.
     *
     */
    public synchronized void init() {
        _game_graph = Arena.parseGraph(_game.getGraph());
        _arena = new Arena(_game);
        placeAgents();
        gframe = new Gframe();
        gframe.addComponentListener(adapt);//////
        gframe.setSize(800, 600);
        gframe.updategame(_arena);
        gframe.show();
        _arena.updateArena(_game);
    }

    /**
     * The main move method, it calls all the planning algorithms in order to set the
     * agents next node than calls move,
     *
     */
    public synchronized static void moveAgents() {
        _arena.updateArena(_game);
        resetTargeting(_arena.getPokemons());
        chooseTargets();
        _game.move();
    }

    /**
     * The main planning algorithm for playing the game, Capable of handling wide varieties
     * of cases. This algorithm call out the different case handling algorithms according
     * to the agent and pokemon placements on the graph.
     * 
     * The main function of this function is to set the next node for each adn every agent
     * on the graph.
     * 
     */
    public synchronized static void chooseTargets() {
        for (CL_Agent current_agent : _arena.getAgents()) {
            int dest = current_agent.getNextNode();
            int src = current_agent.getSrcNode();
            if (dest == -1) {
                CL_Pokemon closest = returnClosestPokemon(current_agent);
                if (closest.isTargeted()) {
                    for (CL_Pokemon next_closest : returnClosestPokemonArr(current_agent)) {
                        if (!next_closest.isTargeted()) {
                            next_closest.targetPokemon();
                            current_agent.setCurrentTarget(next_closest);
                            dest = chooseNextNode(current_agent, next_closest);
                            current_agent.setNextNode(dest);
                            _game.chooseNextEdge(current_agent.getID(),dest);
                            if (checkProximityCase(current_agent) != null) {
                                dest = checkProximityCase(current_agent).poll().get_edge().getDest();
                            }
                            printMoves(current_agent, src, dest, closest);
                        }
                    }
                }
                else {
                    closest.targetPokemon();
                    current_agent.setCurrentTarget(closest);
                    dest = chooseNextNode(current_agent, closest);
                    current_agent.setNextNode(dest);
                    if (checkProximityCase(current_agent) != null) {
                        dest = checkProximityCase(current_agent).poll().get_edge().getDest();
                    }
                    _game.chooseNextEdge(current_agent.getID(),dest);
                    printMoves(current_agent, src, dest, closest);
                }
            }
            //setTimeToSleep(current_agent, dest, graph_game);
        }
    }
    
    public synchronized static void chooseTargetsByProximity() {
        for (CL_Agent current_agent : _arena.getAgents()) {
            int dest = current_agent.getNextNode();
            int src = current_agent.getSrcNode();
            if (dest == -1) {
                CL_Pokemon closest = returnClosestPokemon(current_agent);
                if (closest.isTargeted()) {
                    for (CL_Pokemon next_closest : returnClosestPokemonArr(current_agent)) {
                        if (!next_closest.isTargeted() && !checkAgentProximityConflict(current_agent,next_closest)) {
                            next_closest.targetPokemon();
                            current_agent.setCurrentTarget(next_closest);
                            dest = chooseNextNode(current_agent, next_closest);
                            current_agent.setNextNode(dest);
                            if (checkProximityCase(current_agent) != null) {
                                dest = checkProximityCase(current_agent).poll().get_edge().getDest();
                            }
                            _game.chooseNextEdge(current_agent.getID(),dest);
                            printMoves(current_agent, src, dest, closest);
                        }
                    }
                }
                else if (!checkAgentProximityConflict(current_agent,closest)){
                    closest.targetPokemon();
                    current_agent.setCurrentTarget(closest);
                    dest = chooseNextNode(current_agent, closest);
                    current_agent.setNextNode(dest);
                    if (checkProximityCase(current_agent) != null) {
                        dest = checkProximityCase(current_agent).poll().get_edge().getDest();
                    }
                    _game.chooseNextEdge(current_agent.getID(),dest);
                    printMoves(current_agent, src, dest, closest);
                }
                else if (!checkAgentProximityConflict(current_agent,closest)){
                    for (CL_Pokemon next_closest : returnClosestPokemonArr(current_agent)) {
                        if (!next_closest.isTargeted() && !checkAgentProximityConflict(current_agent,next_closest)) {
                            next_closest.targetPokemon();
                            current_agent.setCurrentTarget(next_closest);
                            dest = chooseNextNode(current_agent, next_closest);
                            current_agent.setNextNode(dest);
                            if (checkProximityCase(current_agent) != null) {
                                dest = checkProximityCase(current_agent).poll().get_edge().getDest();
                            }
                            _game.chooseNextEdge(current_agent.getID(),dest);
                            printMoves(current_agent, src, dest, closest);
                        }
                    }
                }
                else {
                    for (CL_Pokemon next_closest : returnClosestPokemonArr(current_agent)) {
                        if (!next_closest.isTargeted()) {
                            next_closest.targetPokemon();
                            current_agent.setCurrentTarget(next_closest);
                            dest = chooseNextNode(current_agent, next_closest);
                            current_agent.setNextNode(dest);
                        }
                    }
                    if (checkProximityCase(current_agent) != null) {
                        dest = checkProximityCase(current_agent).poll().get_edge().getDest();
                    }
                    _game.chooseNextEdge(current_agent.getID(),dest);
                    printMoves(current_agent, src, dest, closest);
                }
            }
            //setTimeToSleep(current_agent, dest, graph_game);
        }
    }

    /**
     * Chooses the next node for the received agent. This function is called after searching for the
     * best pokemon to be targeted and than builds the shortest path to that node by using WDGraph_Algo
     * class.
     *
     * @param agent The agent to choose the next node to
     * @param pokemon The targeted pokemon for that agent
     * @return And integer representing the next node to be set for te received agent
     */
    public synchronized static int chooseNextNode(CL_Agent agent, CL_Pokemon pokemon) {
        dw_graph_algorithms graph_algo = new DWGraph_Algo(_game_graph);
        List<node_data> poke_path;
        poke_path = graph_algo.shortestPath(agent.getSrcNode(),pokemon.get_edge().getSrc());
        if (poke_path != null) {
            if (poke_path.size() > 1) {
                return poke_path.get(1).getKey();
            }
        }
        return pokemon.get_edge().getSrc();
    }

    /**
     * Scans and the pokemons in the graph in order to find the pokemon with the
     * shortest path from the received agent
     *
     * @param agent The agent to scan the graph for
     * @return CL_Pokemon pointing to the pokemon object with the closest path from the agent
     */
    public synchronized static CL_Pokemon returnClosestPokemon(CL_Agent agent) {
        dw_graph_algorithms graph_algo = new DWGraph_Algo();
        graph_algo.init(_game_graph);
        double path_lengh;
        HashMap<Double,CL_Pokemon> distance_map = new HashMap<>();
        List<Double> distances = new ArrayList<>();
        for (CL_Pokemon pokemon : _arena.getPokemons()) {
            path_lengh = graph_algo.shortestPathDist(agent.getSrcNode(),pokemon.get_edge().getSrc());
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
     *
     * This method will be called in each and every node iteration in order to
     * check if there is any pokemon that can be eaten with one move from the current pokemon.
     * If true, it will by pass all the other rules,
     *
     */
    public synchronized static PriorityQueue<CL_Pokemon> checkProximityCase(CL_Agent agent) {
        PriorityQueue<CL_Pokemon> poke_queue = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(CL_Pokemon poke1, CL_Pokemon poke2) {
                if (poke1.getValue() > poke2.getValue()) return -1;
                else if (poke1.getValue() < poke2.getValue()) return 1;
                else return 0;
            }
        });
        for (edge_data edge : _game_graph.getE(agent.getSrcNode())) {
            for (CL_Pokemon pokemon : _arena.getPokemons()) {
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

    /**
     * Places all the agents at the beginning of the game. Each agent is
     * placed and the source node of the pokemon with the highest value
     * by a descending order
     *
     */
    public synchronized static void placeAgents() {
        if (_arena.getPokemons().size() == 1) {
            for (CL_Pokemon pokemon : _arena.getPokemons()) {
                _game.addAgent(pokemon.get_edge().getSrc());
            }
        }
        PriorityQueue<CL_Pokemon> pokemon_value_queue = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(CL_Pokemon poke1, CL_Pokemon poke2) {
                if (poke1.getValue() > poke2.getValue()) return -1;
                else if (poke1.getValue() < poke2.getValue()) return 1;
                else return 0;
            }
        });
        pokemon_value_queue.addAll(_arena.getPokemons());
        JsonObject json_obj = JsonParser.parseString(_game.toString()).getAsJsonObject();
        json_obj.getAsJsonObject("GameServer").get("agents").getAsInt();
        int agents_number = json_obj.getAsJsonObject("GameServer").get("agents").getAsInt();
        int treated_agents = agents_number;
        for(int i = 0; i < agents_number; i++) {
            if (!pokemon_value_queue.isEmpty()) {
                CL_Pokemon c_pokemon = pokemon_value_queue.poll();
                if (c_pokemon.getType() > 0) {
                    _game.addAgent(Math.min(c_pokemon.get_edge().getSrc(),c_pokemon.get_edge().getDest()));
                }
                else if (c_pokemon.getType() < 0) {
                    _game.addAgent(Math.max(c_pokemon.get_edge().getSrc(),c_pokemon.get_edge().getDest()));
                }
                treated_agents--;
            }
        }
        while (treated_agents > 0) {
            _game.addAgent(ThreadLocalRandom.current().nextInt(1, _game_graph.nodeSize()));
            treated_agents--;
        }
        List<CL_Agent> result_agents_arenar = Arena.initAgentsFromJson(_game.getAgents());
        _arena.setAgents(result_agents_arenar);
    }

    /**
     * Arranges all the pokemons on the graph by distance from the
     * received agent and sorts them in a list. This method is used for targeting
     *
     * @param agent The current checked agent
     * @return List<CL_Pokemon> ordered by the shortest distance from the agent
     */
    public synchronized static List<CL_Pokemon> returnClosestPokemonArr(CL_Agent agent) {
        dw_graph_algorithms graph_algo = new DWGraph_Algo(_game_graph);
        List<CL_Pokemon> result = new ArrayList<>();
        double path_lengh;
        HashMap<Double,CL_Pokemon> distance_map = new HashMap<>();
        List<Double> distances = new ArrayList<>();
        for (CL_Pokemon pokemon : _arena.getPokemons()) {
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

    /**
     * Resets the targetting of all the pokemons in the board by setting their
     * "isTargeted" field to false. This method will be called before each
     * move planning operation
     *
     * @param pokemons an array representing all the pokemons in the game
     */
    public synchronized static void resetTargeting(List<CL_Pokemon> pokemons) {
        for (CL_Pokemon pokemon : pokemons) {
            pokemon.untargetPokemon();
        }
    }

    /**
     * Used in order to divide the graph by sections according to the agents. Each agent will be set on
     * a specific area and will not leave it in order to avoid conflicts on the same area and maximise
     * the collected points in the game
     *
     * @param agents
     */
    public synchronized static void setAgentsTargetedArea(List<CL_Agent> agents) {
        if (agents.size() <= 2) {
            return;
        }
        int nodes_per_arenaea = (int) Math.ceil(((_game_graph.nodeSize()*1.0) / (agents.size()*1.0)) + 1);
        System.out.println(nodes_per_arenaea);
        for (CL_Agent agent : agents) {
            int outer_index = 0;
            int neighbor = 0;
            HashSet<Integer> result = new HashSet<>();
            while (outer_index < nodes_per_arenaea) {
                if (_game_graph.getE(neighbor) != null && !result.contains(neighbor)) {
                    for (edge_data edge : _game_graph.getE(outer_index)) {
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

    /**
     * Sets the time to sleep according to the agent the the targeted pokemon
     * Checks if the pokemon was failed to be eaten and then reduces the sleep time of the main game thread
     *
     * @param agent The agent to check the scenario for
     * @param target The node of the targeted pokemon
     * @return
     */
    public synchronized static long setTimeToSleep(CL_Agent agent, int target) {
        if (target == -1) {
            return 100;
        }
        edge_data compared_edge = _arena.getGraph().getEdge(agent.getSrcNode(), target);
        node_data node = _game_graph.getNode(target);
        if (agent.getCurrentTarget() != null) {
            if (agent.getCurrentTarget().get_edge().equals(compared_edge)) {
                double tmp;
                double path_to_target = agent.getLocation().distance(agent.getCurrentTarget().getLocation());
                double way_to_node = agent.getLocation().distance(node.getLocation());
                tmp = (((path_to_target / way_to_node) * compared_edge.getWeight()) / agent.getSpeed()) * 1000;
                _sleep_time = (long) tmp;
            }
        }
        return 100;
    }

    /**
     * Checks agent conflict according to their proximity to each other uses another method
     * to receive the maximum distance in the graph and divides it by a constant factor.
     * This function is another effective way to avoid more than one pokemon to occupy the same area
     *
     * @param agent The acurrent agent to check conflict with the other agents
     * @param target The targeted pokemon for the conflict check
     * @return
     */
    public synchronized static boolean checkAgentProximityConflict(CL_Agent agent, CL_Pokemon target) {
        double distanceModifier = getGraphMaxDistance() / (_arena.getAgents().size()+_proximity_factor);
        boolean flag = false;
        for (CL_Agent other : _arena.getAgents()) {
            if (!other.equals(agent) && other.getLocation().distance(target.getLocation()) < distanceModifier) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * Checks if this agent has eaten he's target successfully. If not reduces the sleep time
     *
     * @param agents The agent to check
     */
    public synchronized static void checkIfEaten(List<CL_Agent> agents) {
        boolean flag = true;
        for (CL_Agent agent : agents) {
            if (agent.getCurrentTarget() != null) {
                if (agent.getCurrentTarget().get_edge().getDest() == agent.getSrcNode()) {

                }
            }
        }
    }

    public synchronized void moreAgentsThanPokemonsMove() {
    }

    /**
     * Calculates the distances between all the nodes geo_locations
     * in the current graph.
     *
     * @return The highest distance possible between two nodes in the graph
     */
    public synchronized static double getGraphMaxDistance() {
        List<Double> distances = new ArrayList<>();
        for (node_data node : _game_graph.getV()) {
            for (node_data node2 : _game_graph.getV()) {
               distances.add(node.getLocation().distance(node2.getLocation()));
            }
        }
        Collections.sort(distances);
        return distances.get(distances.size()-1);
    }

    /**
     * A function used to printout each and every agent move, This method is used mainly for testing
     * And game behavior research
     *
     * @param current_agent A pointer to the current agent object to print
     * @param src The src node of the agent
     * @param dest The destination chosen for the agent
     * @param closest A pointer to the closest pokemon to the received agent
     */
    public synchronized static void printMoves(CL_Agent current_agent, int src, int dest, CL_Pokemon closest) {
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