package gameClient;

import api.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This class represents a multi agent arena it is the main parsing and updating component
 * for all the game information during playtime. Ech and every stage is parsed by methods here
 * by receiving different Json objects from the game server.
 * The update occurs before every target choosing in the game.
 *
 * @author gison.avziz & shmuel.lavian
 */
public class Arena {
	public static final double EPS = 0.001*0.001;
	private directed_weighted_graph _graph;
	private List<CL_Agent> _agents;
	private List<CL_Pokemon> _pokemons;

	/**
	 * The main arena constructor, receives a game JSon representing all the different components
	 * of a game from the server and parses each and every one of them individual in to the
	 * relevant object
	 *
	 * @param game Json representing the game status, received from the game server
	 */
	public Arena(game_service game) {
		this._graph = parseGraph(game.getGraph());
		this._agents = new ArrayList<>();
		this._pokemons = Arena.initPokemonsFromJson(game.getPokemons());
		this.updatePokemonEdges();
	}

	/**
	 * Updates all the fields in the arena from the same Json object
	 * This method is crucial for handling ongoing changes in the game
	 *
	 * @param game Json representing the game status, received from the game server
	 */
	public void updateArena(game_service game) {
		this.updateAgentsFromJson(game.getAgents());
		this._pokemons = initPokemonsFromJson(game.getPokemons());
		this.updatePokemonEdges();
		this.updateAgentEdges();
		this._graph = parseGraph(game.getGraph());
		Ex2._game_graph = parseGraph(game.getGraph());
	}

	/**
	 * Sets the pokemon List of the arena, This method is used mainly for testing
	 * due to being processed mid game via the update method above
	 *
	 * @param pokemons List<CL_Pokemon> representing the game pokemons
	 */
	public void setPokemons(List<CL_Pokemon> pokemons) {
		this._pokemons = pokemons;
	}

	/**
	 * Sets the agents List of the arena, This method is used mainly for testing
	 * due to being processed mid game via the update method above
	 *
	 * @param agents List<CL_Agent> representing the game agents
	 */
	public void setAgents(List<CL_Agent> agents) {
		this._agents = agents;
	}

	/**
	 * Sets the graph of this arena
	 *
	 * @param graph directed_weighted_graph of this Arena's game
	 */
	public void setGraph(directed_weighted_graph graph) {
		this._graph = graph;
	}

	/**
	 * Returns this games pokemons
	 *
	 * @return List<CL_Pokemon> of the game's pokemons
	 */
	public List<CL_Pokemon> getPokemons() {
		return _pokemons;
	}

	/**
	 * Returns this games graph
	 *
	 * @return directed_weighted_graph of the game's graph
	 */
	public directed_weighted_graph getGraph() {
		return this._graph;
	}

	/**
	 * Creates a graph directly from a received json String
	 * Used based on the graph_algo and used for loading a graph
	 * Without the need of saving/loading from a file
	 *
	 * @param json The received graph as a json String
	 * @return directed_weighted_graph build from the received json
	 */
	public static directed_weighted_graph parseGraph(String json) {
		directed_weighted_graph result = new DWGraph_DS();
		JsonObject j_obj = JsonParser.parseString(json).getAsJsonObject();
		JsonArray edges_arr = j_obj.get("Edges").getAsJsonArray();
		JsonArray nodes_arr = j_obj.get("Nodes").getAsJsonArray();
		for (JsonElement node : nodes_arr) {
			JsonObject node_object = node.getAsJsonObject();
			Integer key = node_object.get("id").getAsInt();
			String[] tmp_str = node_object.getAsJsonPrimitive("pos").getAsString().split(",");
			Double[] pos_arr = new Double[tmp_str.length];
			for (int i=0; i<tmp_str.length; i++) {
				pos_arr[i] = Double.parseDouble(tmp_str[i]);
			}
			node_data tmp_node = new NodeData(key);
			geo_location tmp_location = new GeoLocation(pos_arr[0],pos_arr[1],pos_arr[2]);
			tmp_node.setLocation(tmp_location);
			result.addNode(tmp_node);
		}
		for (JsonElement edge : edges_arr) {
			JsonObject edge_object = edge.getAsJsonObject();
			Integer src = edge_object.get("src").getAsInt();
			Integer dest = edge_object.get("dest").getAsInt();
			Double weight = edge_object.get("w").getAsDouble();
			result.connect(src,dest,weight);
		}
		return result;
	}

	/**
	 * Returns a collection of the agents
	 *
	 * @return List<CL_Agent> of all the game agents
	 */
	public List<CL_Agent> getAgents() {
		return _agents;
	}

	/**
	 * Initializes the game agents form the received Json, This method used for first creating the
	 * agent's and their corresponding object and will be updated in a separate method
	 *
	 * @param json Representing the game agents
	 * @return ArrayList<CL_Agent> Collection on the received agents
	 */
	public static ArrayList<CL_Agent> initAgentsFromJson(String json) {
		ArrayList<CL_Agent> result = new ArrayList<>();
		JsonObject agents_obj = JsonParser.parseString(json).getAsJsonObject();
		JsonArray J_obj = agents_obj.getAsJsonArray("Agents");
		for (JsonElement gson : J_obj) {
			JsonObject json_agent = gson.getAsJsonObject();
			result.add(new CL_Agent(json_agent));
		}
		return result;
	}

	/**
	 * This method is used to update the game agents during the game from the periodically
	 * received Json's from the game server. This separation from the initialization
	 * method is created in order to avoid creating new object every new iteration
	 *
	 * @param json Representing the game agents
	 */
	public void updateAgentsFromJson(String json) {
		JsonObject agents_obj = JsonParser.parseString(json).getAsJsonObject();
		JsonArray J_obj = agents_obj.getAsJsonArray("Agents");
		for (CL_Agent agent : this._agents) {
			for (JsonElement json_agent : J_obj) {
				JsonObject json_agent_obj = json_agent.getAsJsonObject();
				if (json_agent_obj.getAsJsonObject("Agent").get("id").getAsInt() == agent.getID()) {
					agent.update(json_agent_obj);
				}
			}
			agent.setCurrentEdge(this._graph.getEdge(agent.getSrcNode(),agent.getNextNode()));
		}
	}

	/**
	 * Initializes the game pokemons from the received Json from the game server
	 * This method is used to create and store the new Pokemon objects
	 *
	 * @param json Representing the game pokemons
	 * @return ArrayList<CL_Pokemon> collection of the game pokemons
	 */
	public static ArrayList<CL_Pokemon> initPokemonsFromJson(String json) {
		ArrayList<CL_Pokemon> result = new ArrayList<>();
		JsonObject pokemons_obj = JsonParser.parseString(json).getAsJsonObject();
		JsonArray P_obj = pokemons_obj.getAsJsonArray("Pokemons");
		for (JsonElement gson : P_obj) {
			JsonObject json_pokemon = gson.getAsJsonObject();
			result.add(new CL_Pokemon(json_pokemon));
		}
		return result;
	}

	/**
	 * Scans the graph to find the specific edge of the received pokemon.
	 * This method uses another method that checks validity of the pokemon time and the right
	 * relation to the edge's nodes
	 *
	 * @param pokemon The scanned pokemon
	 * @param graph directed_weighted_graph of the current game graph
	 */
	public static void updateEdge(CL_Pokemon pokemon, directed_weighted_graph graph) {
		for (node_data n : graph.getV()) {
			for (edge_data e : graph.getE(n.getKey())) {
				boolean found = isOnEdge(e, pokemon, graph);
				if(found) {
					pokemon.set_edge(e);
				}
			}
		}
	}

	/**
	 * Uses the update edge method for iterating over all the game pokemons
	 * and updating all their edges. This method will be called in each iteration in order to
	 * detect every change in the server during the game
	 *
	 */
	public void updatePokemonEdges() {
		for(int i = 0; i < this._pokemons.size(); i++) {
			Arena.updateEdge(this._pokemons.get(i), this._graph);
		}
	}

	/**
	 * Update all the agents edges on the graph, like all the similar method
	 * this method is called in every update iteration in order
	 * to keep track on every change in the game state
	 *
	 */
	public void updateAgentEdges() {
		for (CL_Agent agent : this._agents) {
			agent.setCurrentEdge(this._graph.getEdge(agent.getSrcNode(),agent.getNextNode()));
		}
	}

	/**
	 * Checks if a specific pokemon is located on a specific edge,
	 * this method is used in every pokemon scanning method and algorithm
	 *
	 * @param edge The edge to check for the pokemon
	 * @param pokemon The searched pokemon
	 * @param graph The current game graph
	 * @return True if the pokemon is on the received edge, false otherwise
	 */
	public static boolean isOnEdge(edge_data edge, CL_Pokemon pokemon, directed_weighted_graph graph) {
		int src = edge.getSrc();
		int dest = edge.getDest();
		if ((pokemon.getType() < 0 && dest > src) || (pokemon.getType() > 0 && src > dest)) {
			return false;
		}
		double distance = graph.getNode(src).getLocation().distance(graph.getNode(dest).getLocation());
		double d1 = graph.getNode(src).getLocation().distance(pokemon.getLocation())
				+ pokemon.getLocation().distance(graph.getNode(dest).getLocation());
		return distance > d1 - EPS;
	}

	/**
	 * Receives a graph and returns the two dimensional range for it
	 * This method is used mainly for drawing the graph on the frame
	 *
	 * @param graph The current game graph
	 * @return the two dimensional range of the graph
	 */
	private static Range2D GraphRange(directed_weighted_graph graph) {
		Iterator<node_data> itr = graph.getV().iterator();
		double x0=0,x1=0,y0=0,y1=0;
		boolean first = true;
		while(itr.hasNext()) {
			geo_location p = itr.next().getLocation();
			if(first) {
				x0=p.x(); x1=x0;
				y0=p.y(); y1=y0;
				first = false;
			}
			else {
				if(p.x()<x0) {x0=p.x();}
				if(p.x()>x1) {x1=p.x();}
				if(p.y()<y0) {y0=p.y();}
				if(p.y()>y1) {y1=p.y();}
			}
		}
		Range xr = new Range(x0,x1);
		Range yr = new Range(y0,y1);
		return new Range2D(xr,yr);
	}

	/**
	 * Converts the two dimensional coordinates to frame coordinates. This method is used
	 * mainly for drawing the graph and in all the Frame methods
	 *
	 * @param graph The current game graph
	 * @param frame The current game frame
	 * @return Ranged frame for the graph drawings
	 */
	public static Range2Range w2f(directed_weighted_graph graph, Range2D frame) {
		Range2D world = GraphRange(graph);
		return new Range2Range(world, frame);
	}
}
