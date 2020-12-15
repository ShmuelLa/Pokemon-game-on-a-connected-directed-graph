package api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EdgeData implements edge_data {
    @Expose
    @SerializedName("src")
    private int _src;
    @Expose
    @SerializedName("w")
    private double _weight;
    @Expose
    @SerializedName("dest")
    private int _dest;
    private int _tag;
    private String _str;

    public EdgeData() {
        this._src = 0;
        this._dest = 0;
        this._weight = 0;
    }

    public EdgeData(int src, int dest, double weight) {
        this._src = src;
        this._dest = dest;
        this._weight = weight;
    }
    /**
     * The id of the source node of this edge.
     *
     * @return
     */
    @Override
    public int getSrc() {
        return this._src;
    }

    /**
     * The id of the destination node of this edge
     *
     * @return
     */
    @Override
    public int getDest() {
        return this._dest;
    }

    /**
     * @return the weight of this edge (positive value).
     */
    @Override
    public double getWeight() {
        return this._weight;
    }

    /**
     * Returns the remark (meta data) associated with this edge.
     *
     * @return
     */
    @Override
    public String getInfo() {
        return this._str;
    }

    /**
     * Allows changing the remark (meta data) associated with this edge.
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
     * This method allows setting the "tag" value for temporal marking an edge - common
     * practice for marking by algorithms.
     *
     * @param t - the new value of the tag
     */
    @Override
    public void setTag(int t) {
        this._tag = t;
    }
}
