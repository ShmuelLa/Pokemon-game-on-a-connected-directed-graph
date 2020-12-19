package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gameClient.util.Point3D;
import org.json.JSONObject;

import java.util.HashSet;

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
		private long _sg_dt;
		private double _value;
		private HashSet<Integer> _targetArea;

		public CL_Agent(JsonObject json) {
			this._id = json.getAsJsonObject("Agent").get("id").getAsInt();
			this._value = json.getAsJsonObject("Agent").get("value").getAsDouble();
			this._speed = json.getAsJsonObject("Agent").get("speed").getAsDouble();
			this._location = new Point3D(json.getAsJsonObject("Agent").get("pos").getAsString());
			this._graph = Ex2._game_graph;
			this._curr_node = Ex2._game_graph.getNode(json.getAsJsonObject("Agent").get("src").getAsInt());
			this._curr_edge = Ex2._game_graph.getEdge(_curr_node.getKey(), json.getAsJsonObject("Agent").get("dest").getAsInt());
		}

		public void update(JsonObject json) {
			this._id = json.getAsJsonObject("Agent").get("id").getAsInt();
			this._value = json.getAsJsonObject("Agent").get("value").getAsDouble();
			this._speed = json.getAsJsonObject("Agent").get("speed").getAsDouble();
			this._location = new Point3D(json.getAsJsonObject("Agent").get("pos").getAsString());
			this._graph = Ex2._game_graph;
			this._curr_node = Ex2._game_graph.getNode(json.getAsJsonObject("Agent").get("src").getAsInt());
			this._curr_edge = Ex2._game_graph.getEdge(_curr_node.getKey(), json.getAsJsonObject("Agent").get("dest").getAsInt());
		}

		public int getSrcNode() {
			return this._curr_node.getKey();
		}

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

		public void setValue(double v) {
			this._value = v;
		}
	
		public void setNextNode(int dest) {
			int src = this._curr_node.getKey();
			this._curr_edge = _graph.getEdge(src, dest);
		}

		public void setCurrNode(int src) {
			this._curr_node = _graph.getNode(src);
		}

		public boolean isMoving() {
			return this._curr_edge!=null;
		}

		public String toString() {
			return "ID: "+this.getID()+" Val: "+this.getValue()+", "+_location+", "+isMoving()+", ";
		}

		public int getID() {
			return this._id;
		}
	
		public geo_location getLocation() {
			return _location;
		}

		
		public double getValue() {
			return this._value;
		}

		public int getNextNode() {
			int ans = -2;
			if(this._curr_edge==null) {
				ans = -1;}
			else {
				ans = this._curr_edge.getDest();
			}
			return ans;
		}

		public double getSpeed() {
			return this._speed;
		}

		public void setSpeed(double v) {
			this._speed = v;
		}

		public CL_Pokemon getCurrentTarget () {
			return this._current_target;
		}

		public void setCurrentTarget (CL_Pokemon curr_fruit) {
			this._current_target = curr_fruit;
		}

		public void set_SDT(long ddtt) {
			long ddt = ddtt;
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
			this.set_sg_dt(ddt);
		}
		
		public edge_data get_curr_edge() {
			return this._curr_edge;
		}

		public void setCurrentEdge(edge_data edge) {
			this._curr_edge = edge;
		}

		public long get_sg_dt() {
			return _sg_dt;
		}

		public void set_sg_dt(long _sg_dt) {
			this._sg_dt = _sg_dt;
		}

		public void setFormerTarget(CL_Pokemon pokemon) {
			this._former_target = pokemon;
		}

		public CL_Pokemon checkFormerTarget() {
			return this._former_target;
		}

		public void setTargetedArea(HashSet<Integer> area) {
			this._targetArea = area;
		}

		public HashSet<Integer> getTargetedArea() {
			return this._targetArea;
		}

		public void setFormerValue(double value) {
			this._former_value = value;
		}

		public double getFormerValue() {
			return this._former_value;
		}

		public void setLocation(Point3D location) {
			this._location = location;
		}
	}
