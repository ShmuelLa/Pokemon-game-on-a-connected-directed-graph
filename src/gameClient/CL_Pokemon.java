package gameClient;

import api.*;
import com.google.gson.JsonObject;
import gameClient.util.Point3D;
import org.json.JSONObject;
import java.util.Iterator;

public class CL_Pokemon {
	private double _value;
	private int _type;
	private Point3D _location;
	private edge_data _edge;
	private boolean _isTargeted = false;

	public CL_Pokemon(JsonObject json) {
		this._value = json.getAsJsonObject("Pokemon").get("value").getAsInt();
		this._type = json.getAsJsonObject("Pokemon").get("type").getAsInt();
		this._location = new Point3D(json.getAsJsonObject("Pokemon").get("pos").getAsString());
	}

	public edge_data get_edge() {
		return _edge;
	}

	public void set_edge(edge_data _edge) {
		this._edge = _edge;
	}

	public Point3D getLocation() {
		return _location;
	}

	public int getType() {
		return _type;
	}

	public double getValue() {
		return _value;
	}

/*	public edge_data searchPokemonEdge(CL_Pokemon fr, directed_weighted_graph g) {
		//	oop_edge_data ans = null;
		Iterator<node_data> itr = g.getV().iterator();
		edge_data result = new EdgeData();
		while(itr.hasNext()) {
			node_data v = itr.next();
			Iterator<edge_data> iter = g.getE(v.getKey()).iterator();
			while(iter.hasNext()) {
				edge_data e = iter.next();
				boolean found = Arena.isOnEdge(this._pos, e,fr.getType(), g);
				if(found) {
					fr.set_edge(e);
					result = e;
					return e;
				}
			}
		}
		return result;
	}*/

	/*	public String () {
		return "F:{v="+_value+", t="+_type+"}";
	}*/

	public void targetPokemon() {
		this._isTargeted = true;
	}

	public void untargetPokemon() {
		this._isTargeted = false;
	}

	public boolean isTargeted() {
		return this._isTargeted;
	}
}
