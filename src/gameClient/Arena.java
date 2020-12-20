package gameClient;

import api.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a multi Agents Arena which move on a graph - grabs Pokemon's and avoid the Zombies.
 * @author boaz.benmoshe
 *
 */
public class Arena {
	public static final double EPS = 0.001*0.001;
	private directed_weighted_graph _graph;
	private List<CL_Agent> _agents;
	private List<CL_Pokemon> _pokemons;
	private List<String> _info;
	private static Point3D MIN = new Point3D(0, 100,0);
	private static Point3D MAX = new Point3D(0, 100,0);

	public Arena(game_service game) {
		this._info = new ArrayList<String>();
		this._graph = parseGraph(game.getGraph());
		this._agents = new ArrayList<>();
		this._pokemons = Arena.initPokemonsFromJson(game.getPokemons());
		this.updatePokemonEdges();
	}

	public void updateArena(game_service game) {
		this.updateAgentsFromJson(game.getAgents());
		this._pokemons = initPokemonsFromJson(game.getPokemons());
		this.updatePokemonEdges();
		this.updateAgentEdges();
		this._graph = parseGraph(game.getGraph());
		Ex2._game_graph = parseGraph(game.getGraph());
	}

	public void setPokemons(List<CL_Pokemon> pokemons) {
		this._pokemons = pokemons;
	}

	public void setAgents(List<CL_Agent> agents) {
		this._agents = agents;
	}

	public void setGraph(directed_weighted_graph g) {
		this._graph =g;
	}

	private void init( ) {
		MIN=null; MAX=null;
		double x0=0,x1=0,y0=0,y1=0;
		Iterator<node_data> iter = this._graph.getV().iterator();
		while(iter.hasNext()) {
			geo_location c = iter.next().getLocation();
			if(MIN==null) {x0 = c.x(); y0=c.y(); x1=x0;y1=y0;MIN = new Point3D(x0,y0);}
			if(c.x() < x0) {x0=c.x();}
			if(c.y() < y0) {y0=c.y();}
			if(c.x() > x1) {x1=c.x();}
			if(c.y() > y1) {y1=c.y();}
		}
		double dx = x1-x0, dy = y1-y0;
		MIN = new Point3D(x0-dx/10,y0-dy/10);
		MAX = new Point3D(x1+dx/10,y1+dy/10);
	}

	public List<CL_Pokemon> getPokemons() {
		return _pokemons;
	}
	
	public directed_weighted_graph getGraph() {
		return this._graph;
	}

	public List<String> get_info() {
		return this._info;
	}

	public void set_info(List<String> _info) {
		this._info = _info;
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

	public List<CL_Agent> getAgents() {
		return _agents;
	}

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

/*	public void updatePokemonsFromJson(String json) {
		JsonObject agents_obj = JsonParser.parseString(json).getAsJsonObject();
		JsonArray J_obj = agents_obj.getAsJsonArray("Pokemons");
		for (CL_Pokemon pokemon : this._pokemons) {
			for (JsonElement json_agent : J_obj) {
				JsonObject json_agent_obj = json_agent.getAsJsonObject();
				if (json_agent_obj.getAsJsonObject("Pokemon").get("id").getAsInt() == agent.getID()) {
					pokemon.update(json_agent_obj);
				}
			}
			agent.setCurrentEdge(this._graph.getEdge(agent.getSrcNode(),agent.getNextNode()));
		}
	}*/

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

	public static void updateEdge(CL_Pokemon pokemon, directed_weighted_graph g) {
		for (node_data n : g.getV()) {
			for (edge_data e : g.getE(n.getKey())) {
				boolean found = isOnEdge(e, pokemon, g);
				if(found) {
					pokemon.set_edge(e);
				}
			}
		}
	}

	public void updatePokemonEdges() {
		for(int i = 0; i < this._pokemons.size(); i++) {
			Arena.updateEdge(this._pokemons.get(i), this._graph);
		}
	}

	public void updateAgentEdges() {
		for (CL_Agent agent : this._agents) {
			agent.setCurrentEdge(this._graph.getEdge(agent.getSrcNode(),agent.getNextNode()));
		}
	}

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

	private static Range2D GraphRange(directed_weighted_graph g) {
		Iterator<node_data> itr = g.getV().iterator();
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

	public static Range2Range w2f(directed_weighted_graph g, Range2D frame) {
		Range2D world = GraphRange(g);
		Range2Range ans = new Range2Range(world, frame);
		return ans;
	}
}
