package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import com.google.gson.JsonObject;
import gameClient.util.Point3D;
import java.util.HashSet;

/**
 * This class represents an agent. Each agent is represented with a location an an edge
 * on a graph. And the main interest is to guide these agents to each as much pokemons
 * We can. Each an every agent is initialized and updated by a json string received from the game server
 *
 * @author gison.avziz & shmuel.lavian
 */
public class CL_Agent { 
	private int _id;
	private geo_location _location;
	private double _speed;
	private edge_data _curr_edge;
	private node_data _curr_node;
	private double _former_value;
	private directed_weighted_graph _graph;
	private CL_Pokemon _current_target;
	private CL_Pokemon _former_target;
	private long _sleep_time;
	private double _value;
	private HashSet<Integer> _targetArea;

	/**
	 * The main CL_Agent constructor, creates a new agents by receiving a json object parsed
	 * from the server.
	 *
	 * @param json The json object representing the new CL_agent
	 */
	public CL_Agent(JsonObject json) {
		this._id = json.getAsJsonObject("Agent").get("id").getAsInt();
		this._value = json.getAsJsonObject("Agent").get("value").getAsDouble();
		this._speed = json.getAsJsonObject("Agent").get("speed").getAsDouble();
		this._location = new Point3D(json.getAsJsonObject("Agent").get("pos").getAsString());
		this._graph = Ex2._game_graph;
		this._curr_node = Ex2._game_graph.getNode(json.getAsJsonObject("Agent").get("src").getAsInt());
		this._curr_edge = Ex2._game_graph.getEdge(_curr_node.getKey(), json.getAsJsonObject("Agent").get("dest").getAsInt());
	}

	/**
	 * A secondary constructor for the object which is less efficient and needs more input
	 * this is used mainly for testing
	 *
	 * @param json A json object representing the agent
	 * @param graph A graph representing the game graph
	 */
	public CL_Agent(JsonObject json, directed_weighted_graph graph) {
		this._id = json.getAsJsonObject("Agent").get("id").getAsInt();
		this._value = json.getAsJsonObject("Agent").get("value").getAsDouble();
		this._speed = json.getAsJsonObject("Agent").get("speed").getAsDouble();
		this._location = new Point3D(json.getAsJsonObject("Agent").get("pos").getAsString());
		this._graph = graph;
		this._curr_node = graph.getNode(json.getAsJsonObject("Agent").get("src").getAsInt());
		this._curr_edge = graph.getEdge(_curr_node.getKey(), json.getAsJsonObject("Agent").get("dest").getAsInt());
	}

	/**
	 * A secondary constructor for the object which is less efficient and needs more input
	 * this is used mainly for testing
	 *
	 * @param id The agents ID
	 * @param val The agents Value
	 * @param src The agents source node
	 * @param graph The agents graph
	 */
	public CL_Agent(int id, int val, int src, directed_weighted_graph graph) {
		this._id = id;
		this._value = val;
		this._graph = graph;
		this._curr_node = graph.getNode(src);
	}

	/**
	 * Update the receiving agent according to the new Json.
	 * This method is key for the game deue to the constant changes in the game level
	 *
	 * @param json A json object representing the agent
	 */
	public void update(JsonObject json) {
		this._id = json.getAsJsonObject("Agent").get("id").getAsInt();
		this._value = json.getAsJsonObject("Agent").get("value").getAsDouble();
		this._speed = json.getAsJsonObject("Agent").get("speed").getAsDouble();
		this._location = new Point3D(json.getAsJsonObject("Agent").get("pos").getAsString());
		this._graph = Ex2._game_graph;
		this._curr_node = Ex2._game_graph.getNode(json.getAsJsonObject("Agent").get("src").getAsInt());
		this._curr_edge = Ex2._game_graph.getEdge(_curr_node.getKey(), json.getAsJsonObject("Agent").get("dest").getAsInt());
	}

	/**
	 * Returns the source node of the agent
	 *
	 * @return Integer of the source node on the graph
	 */
	public int getSrcNode() {
		return this._curr_node.getKey();
	}

	/**
	 * Returns a Json string representing the agent
	 *
	 * @return String Json with akk the agent information
	 */
	public String toJSON() {
		int d = this.getNextNode();
		String ans = "{\"Agent\":{"
				+ "\"id\":"+this._id+","
				+ "\"value\":"+this._value+","
				+ "\"src\":"+this._curr_node.getKey()+","
				+ "\"dest\":"+d+","
				+ "\"speed\":"+this.getSpeed()+","
				+ "\"pos\":\""+_location.toString()+"\""
				+ "}"
				+ "}";
		return ans;
	}

	/**
	 * Sets the agents next node, This method is mainly used for validating the
	 * game progress
	 *
	 * @param dest The destination node for the agent
	 */
	public void setNextNode(int dest) {
		int src = this._curr_node.getKey();
		this._curr_edge = _graph.getEdge(src, dest);
	}

	/**
	 * Sets this agents current node by key
	 *
	 * @param src This agents current node key
	 */
	public void setCurrNode(int src) {
		this._curr_node = _graph.getNode(src);
	}

	/**
	 * Checks if this agent is moving by checking if it's placed in between two nodes and
	 * on a specific edge on the graph
	 *
	 * @return True if this agent is moving false otherwise
	 */
	public boolean isMoving() {
		return this._curr_edge!=null;
	}

	/**
	 * Printouts this current agents status and details, used mainly for presentation and testing
	 *
	 * @return String representing this current agent minimalistic information
	 */
	public String toString() {
		return "ID: "+this.getID()+" Val: "+this.getValue()+", "+_location+", "+isMoving()+", ";
	}

	/**
	 * Returns this agents ID
	 *
	 * @return Integer of this agents ID
	 */
	public int getID() {
		return this._id;
	}

	/**
	 * Returns this agents Geo_location
	 *
	 * @return Geo_location of this agent
	 */
	public geo_location getLocation() {
		return _location;
	}

	/**
	 * Returns the value of this current agent
	 *
	 * @return double of this agents value
	 */
	public double getValue() {
		return this._value;
	}

	/**
	 * Returns the next node of this agent
	 *
	 * @return Integer representing the key of this agents next node
	 */
	public int getNextNode() {
		int ans = -2;
		if(this._curr_edge==null) {
			ans = -1;}
		else {
			ans = this._curr_edge.getDest();
		}
		return ans;
	}

	/**
	 * Returns the speed of this agent
	 *
	 * @return Double representing the agents speed
	 */
	public double getSpeed() {
		return this._speed;
	}


	/**
	 * Returns this agents current target
	 *
	 * @return CL_Pokemon representing this agents current target
	 */
	public CL_Pokemon getCurrentTarget () {
		return this._current_target;
	}

	/**
	 * Sets this agents current target
	 *
	 * @param current_target CL_Agent of this agents current target
	 */
	public void setCurrentTarget (CL_Pokemon current_target) {
		this._current_target = current_target;
	}

	/**
	 * Calculates current agents sleep time by checking this agents achievements
	 *
	 * @param sleep_time Current sleep time
	 */
	public void calculateSleepTime(long sleep_time) {
		long ddt = sleep_time;
		if(this._curr_edge!=null) {
			double w = get_curr_edge().getWeight();
			geo_location dest = _graph.getNode(get_curr_edge().getDest()).getLocation();
			geo_location src = _graph.getNode(get_curr_edge().getSrc()).getLocation();
			double de = src.distance(dest);
			double dist = _location.distance(dest);
			if(this.getCurrentTarget().get_edge()==this.get_curr_edge()) {
				 dist = _current_target.getLocation().distance(this._location);
			}
			double norm = dist/de;
			double dt = w*norm / this.getSpeed();
			ddt = (long)(1000.0*dt);
		}
		this.set_sleep_time(ddt);
	}

	/**
	 * Returns this agents current edge data if available (If the agent is not moving)
	 *
	 * @return edge_data of this agents current edge, null otherwise
	 */
	public edge_data get_curr_edge() {
		return this._curr_edge;
	}

	/**
	 * Sets this agents current edge
	 *
	 * @param edge edge_data to set for this agent
	 */
	public void setCurrentEdge(edge_data edge) {
		this._curr_edge = edge;
	}

	/**
	 * Returns this agents sleep time, used to calculate the games sleep time
	 *
	 * @return long of this agent suggested sleep time
	 */
	public long get_sleep_time() {
		return _sleep_time;
	}

	/**
	 * Sets this agents current sleep time, used mainly for algorithmic purposes
	 *
	 * @param sleep_time long new sleep time to set
	 */
	public void set_sleep_time(long sleep_time) {
		this._sleep_time = _sleep_time;
	}

	/**
	 * Sets the former pokemon target for this agent
	 *
	 * @param pokemon Former target of this agent
	 */
	public void setFormerTarget(CL_Pokemon pokemon) {
		this._former_target = pokemon;
	}

	/**
	 * Returns this agents former pokemon target
	 *
	 * @return CL_Pokemon of the former target
	 */
	public CL_Pokemon checkFormerTarget() {
		return this._former_target;
	}

	/**
	 * Sets the targeted area for this agent
	 *
	 * @param area HashSet<Integer> representing the nodes in the agents area
	 */
	public void setTargetedArea(HashSet<Integer> area) {
		this._targetArea = area;
	}

	/**
	 * Returns the targeted area for this agent
	 *
	 * @return HashSet<Integer> representing the nodes in the agents area
	 */
	public HashSet<Integer> getTargetedArea() {
		return this._targetArea;
	}

	/**
	 * Sets the former value for the current agent
	 *
	 * @param value The former value amount
	 */
	public void setFormerValue(double value) {
		this._former_value = value;
	}

	/**
	 * Gets the former value for the current agent
	 *
	 * @return The former value amount
	 */
	public double getFormerValue() {
		return this._former_value;
	}

	/**
	 * Sets the Geo_Location of this agent
	 *
	 * @param location The new Geo_Location
	 */
	public void setLocation(Point3D location) {
		this._location = location;
	}
}
