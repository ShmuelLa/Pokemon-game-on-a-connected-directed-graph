package gameClient;

import api.*;
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
	public static final double EPS1 = 0.001;
	public static final double EPS2=EPS1*EPS1, EPS=EPS2;
	private directed_weighted_graph _gg;
	private List<CL_Agent> _agents;
	private List<CL_Pokemon> _pokemons;
	private List<String> _info;
	private static Point3D MIN = new Point3D(0, 100,0);
	private static Point3D MAX = new Point3D(0, 100,0);

	public Arena() {;
		_info = new ArrayList<String>();
	}

	private Arena(directed_weighted_graph g, List<CL_Agent> r, List<CL_Pokemon> p) {
		_gg = g;
		this.setAgents(r);
		this.setPokemons(p);
	}

	public void setPokemons(List<CL_Pokemon> f) {
		this._pokemons = f;
	}

	public void setAgents(List<CL_Agent> f) {
		this._agents = f;
	}

	public void setGraph(directed_weighted_graph g) {
		this._gg =g;
	}//init();}

	private void init( ) {
		MIN=null; MAX=null;
		double x0=0,x1=0,y0=0,y1=0;
		Iterator<node_data> iter = _gg.getV().iterator();
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

	public List<CL_Agent> getAgents() {
		return _agents;
	}

	public List<CL_Pokemon> getPokemons() {
		return _pokemons;
	}

	
	public directed_weighted_graph getGraph() {
		return _gg;
	}

	public List<String> get_info() {
		return _info;
	}

	public void set_info(List<String> _info) {
		this._info = _info;
	}

	////////////////////////////////////////////////////
	public static List<CL_Agent> getAgents(String aa, directed_weighted_graph gg) {
		ArrayList<CL_Agent> ans = new ArrayList<CL_Agent>();
		try {
			JSONObject ttt = new JSONObject(aa);
			JSONArray ags = ttt.getJSONArray("Agents");
			for(int i=0;i<ags.length();i++) {
				CL_Agent c = new CL_Agent(gg,0);
				c.update(ags.get(i).toString());
				ans.add(c);
			}
			//= getJSONArray("Agents");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ans;
	}

	public static ArrayList<CL_Pokemon> json2Pokemons(String fs) {
		ArrayList<CL_Pokemon> ans = new  ArrayList<CL_Pokemon>();
		try {
			JSONObject ttt = new JSONObject(fs);
			JSONArray ags = ttt.getJSONArray("Pokemons");
			for(int i=0;i<ags.length();i++) {
				JSONObject pp = ags.getJSONObject(i);
				JSONObject pk = pp.getJSONObject("Pokemon");
				int t = pk.getInt("type");
				double v = pk.getDouble("value");
				//double s = 0;//pk.getDouble("speed");
				String p = pk.getString("pos");
				CL_Pokemon f = new CL_Pokemon(new Point3D(p), t, v, 0, null);
				ans.add(f);
			}
		}
		catch (JSONException e) {e.printStackTrace();}
		return ans;
	}

	public static void updateEdge(CL_Pokemon fr, directed_weighted_graph g) {
		for (node_data n : g.getV()) {
			for (edge_data e : g.getE(n.getKey())) {
				boolean found = isOnEdge(fr.getLocation(), e,fr.getType(), g);
				if(found) {
					fr.set_edge(e);
				}
			}
		}
	}

/*	public static void updateEdge(CL_Agent fr, directed_weighted_graph g) {
		for (node_data n : g.getV()) {
			for (edge_data e : g.getE(n.getKey())) {
				boolean found = isOnEdge(fr.getLocation(), e, g);
				if(found) {
					fr.setCurrentEdge(e);
				}
			}
		}
	}*/

	public static edge_data getPokemonEdge(CL_Pokemon fr, directed_weighted_graph g) {
		Iterator<node_data> itr = g.getV().iterator();
		edge_data result = new EdgeData();
		while(itr.hasNext()) {
			node_data v = itr.next();
			Iterator<edge_data> iter = g.getE(v.getKey()).iterator();
			while(iter.hasNext()) {
				edge_data e = iter.next();
				boolean found = isOnEdge(fr.getLocation(), e,fr.getType(), g);
				if(found) {
					fr.set_edge(e);
					fr.set_edge(e);
					return e;
				}
			}
		}
		return result;
	}

	public static boolean isOnEdge(geo_location pos, edge_data edge, int type, directed_weighted_graph graph) {
		int src = edge.getSrc();
		int dest = edge.getDest();
		if ((type < 0 && dest > src) || (type > 0 && src > dest)) {
			return false;
		}
		geo_location src_pos = graph.getNode(src).getLocation();
		geo_location dest_pos = graph.getNode(dest).getLocation();
		double distance = src_pos.distance(dest_pos);
		double d1 = src_pos.distance(pos) + pos.distance(dest_pos);
		return distance > d1 - EPS;
	}

	public static boolean isOnEdge(geo_location pos, edge_data edge, directed_weighted_graph graph) {
		int src = edge.getSrc();
		int dest = edge.getDest();
		geo_location src_pos = graph.getNode(src).getLocation();
		geo_location dest_pos = graph.getNode(dest).getLocation();
		double distance = src_pos.distance(dest_pos);
		double d1 = src_pos.distance(pos) + pos.distance(dest_pos);
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
