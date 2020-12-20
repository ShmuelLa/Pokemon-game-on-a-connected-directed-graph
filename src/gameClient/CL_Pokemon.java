package gameClient;

import api.*;
import com.google.gson.JsonObject;
import gameClient.util.Point3D;

/**
 * This class represents a pokemon. Each pokemon is represented with a location an an edge
 * on a graph. The main target is to eat as many pokemon objects as possible in the given game time
 * each pokemon is initialized, updated and created via a json given from the game server.
 * Unlike the agents we can control the pokemons so we need to analyze their behavior appropriately
 * in order to set our targeting algorithms in Ex2 to reach these pokemons as fast and efficient as possible
 *
 * @author gison.avziz & shmuel.lavian
 */
public class CL_Pokemon {
	private double _value;
	private int _type;
	private Point3D _location;
	private edge_data _edge;
	private boolean _isTargeted = false;

	/**
	 * The main CL_Pokemon constructor, parses a received json object from the
	 * game servers into a CL_Pokemon by using Gson
	 *
	 * @param json String representing CL_Pokemon field from the game server
	 */
	public CL_Pokemon(JsonObject json) {
		this._value = json.getAsJsonObject("Pokemon").get("value").getAsInt();
		this._type = json.getAsJsonObject("Pokemon").get("type").getAsInt();
		this._location = new Point3D(json.getAsJsonObject("Pokemon").get("pos").getAsString());
	}

	/**
	 * Secondary CL_Pokemon constructor, Used mainly for testing
	 * Initialized by manua,y inputing each field
	 *
	 * @param val double value
	 * @param type int type
	 * @param pos Geo_location
	 */
	public CL_Pokemon(int val, int type, Point3D pos) {
		this._value = val;
		this._type = type;
		this._location = pos;
	}

	/**
	 * Returns this pokemons current edge
	 * Note: This field need to be scanned in order to be found, this action is handles in the
	 * Arena class
	 *
	 * @return edge_data of this pokemon
	 */
	public edge_data get_edge() {
		return _edge;
	}

	/**
	 * Sets this pokemons edge_data, this method is used in graph scanning algorithms in
	 * the Arena class in order to update the pokemon state on the graph from the
	 * Json input from the game server
	 *
	 * @param _edge edge_data to be set as this pokemon's current edge
	 */
	public void set_edge(edge_data _edge) {
		this._edge = _edge;
	}

	/**
	 * Returns this pokemon's Geo_Location
	 *
	 * @return Geo_location object of this pokemon's location
	 */
	public Point3D getLocation() {
		return _location;
	}

	/**
	 * Return the type of this pokemon
	 * Note: Positive type means this pokemon edge node are ascending, for example
	 * if the pokemon is on the edge from node 1 to 4 it's type is positive while
	 * a negative type is exactly the opposite
	 *
	 * @return int representing this pokemons type
	 */
	public int getType() {
		return _type;
	}

	/**
	 * Returns this pokemons value, the higer the value the more points we collect for the
	 * end of the game. This method is used for example for choosing the best start point in the graph
	 *
	 * @return double representing this nodes value
	 */
	public double getValue() {
		return _value;
	}

	/**
	 * Sets this pokemon as targeted, this field is used to avoid conflicting
	 * between agents so only one agent wil target one pokemon at a time
	 *
	 */
	public void targetPokemon() {
		this._isTargeted = true;
	}

	/**
	 * Sets this pokemos as untargeted so other agents can target it again
	 *
	 */
	public void untargetPokemon() {
		this._isTargeted = false;
	}

	/**
	 * Checks if this pokemon is targeted to avoid conflicting between other agents
	 *
	 * @return True if this pokemon is targeted false otherwise
	 */
	public boolean isTargeted() {
		return this._isTargeted;
	}
}
