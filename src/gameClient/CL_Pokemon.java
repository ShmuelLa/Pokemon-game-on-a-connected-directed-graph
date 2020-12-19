package gameClient;

import api.*;
import gameClient.util.Point3D;
import org.json.JSONObject;
import java.util.Iterator;

public class CL_Pokemon {
	private edge_data _edge;
	private double _value;
	private int _type;
	private Point3D _pos;
	private double min_dist;
	private int min_ro;
	private boolean _isTargeted = false;
	private boolean _isBlackListed = false;
	
	public CL_Pokemon(Point3D p, int t, double v, double s, edge_data e) {
		this._type = t;
		this._value = v;
		this._edge = e;
		this._pos = p;
		this.min_dist = -1;
		this.min_ro = -1;
		this._isTargeted = false;
	}

	public String toString() {
		return "F:{v="+_value+", t="+_type+"}";
	}

	public edge_data get_edge() {
		return _edge;
	}

	public void set_edge(edge_data _edge) {
		this._edge = _edge;
	}

	public Point3D getLocation() {
		return _pos;
	}

	public int getType() {
		return _type;
	}

	public double getValue() {
		return _value;
	}

	public double getMin_dist() {
		return min_dist;
	}

	public void setMin_dist(double mid_dist) {
		this.min_dist = mid_dist;
	}

	public int getMin_ro() {
		return min_ro;
	}

	public void setMin_ro(int min_ro) {
		this.min_ro = min_ro;
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

	public void targetPokemon() {
		this._isTargeted = true;
	}

	public void untargetPokemon() {
		this._isTargeted = false;
	}

	public boolean isTargeted() {
		return this._isTargeted;
	}

	public void blacklist() {
		this._isBlackListed = true;
	}

	public boolean checkIfBlacklisted() {
		return this._isBlackListed;
	}
}
