package api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NodeData implements node_data, Comparable<node_data> {
    @Expose
    @SerializedName("pos")
    private geo_location _location;
    @SerializedName("id")
    private final int _key;
    private int _tag;
    private double _weight;
    private String _str;

    /**
     * The main node_data constructor. Creates a new node with the received ID
     *
     * @param k - The ID to be set for the new node
     */
    public NodeData(int k) {
        this._key = k;
        this._tag = -1;
        this._str = null;
        this._location = new GeoLocation();
    }

    /**
     * Returns the key (id) associated with this node.
     *
     * @return
     */
    @Override
    public int getKey() {
        return this._key;
    }

    /**
     * Returns the location of this node, if
     * none return null.
     *
     * @return
     */
    @Override
    public geo_location getLocation() {
        return this._location;
    }

    /**
     * Allows changing this node's location.
     *
     * @param p - new new location  (position) of this node.
     */
    @Override
    public void setLocation(geo_location p) {
        this._location = p;
    }

    /**
     * Returns the weight associated with this node.
     *
     * @return
     */
    @Override
    public double getWeight() {
        return this._weight;
    }

    /**
     * Allows changing this node's weight.
     *
     * @param w - the new weight
     */
    @Override
    public void setWeight(double w) {
        this._weight = w;
    }

    /**
     * Returns the remark (meta data) associated with this node.
     *
     * @return
     */
    @Override
    public String getInfo() {
        return this._str;
    }

    /**
     * Allows changing the remark (meta data) associated with this node.
     *
     * @param s
     */
    @Override
    public void setInfo(String s) {
        this._str = s;
    }

    /**
     * Temporal data (aka color: e,g, white, gray, black)
     * which can be used be algorithms
     *
     * @return
     */
    @Override
    public int getTag() {
        return this._tag;
    }

    /**
     * Allows setting the "tag" value for temporal marking an node - common
     * practice for marking by algorithms.
     *
     * @param t - the new value of the tag
     */
    @Override
    public void setTag(int t) {
        this._tag = t;
    }

    /**
     * Overrides the compareTo() method implemented by Comparable interface
     * This method is used to compare the tag value of two nodes and used in this project solely
     * for comparing the minimal distance in the shortestPath method based on Dijkstra's algorithm
     * This method prevents NullPointerException and ClassCastException bt catching them before comparing.
     * For more information about the comparable interface:
     * https://docs.oracle.com/javase/8/docs/api/java/lang/Comparable.html
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(node_data o) {
        if (this._tag > o.getTag()) return 1;
        else if (this._tag < o.getTag()) return -1;
        return 0;
    }
}
