package api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeoLocation implements geo_location {
    @Expose
    @SerializedName("x")
    double _x;
    @Expose
    @SerializedName("y")
    double _y;
    @Expose
    @SerializedName("z")
    double _z;
    double _distance;

    public GeoLocation() {
        this._x = 0.0;
        this._y = 0.0;
        this._z = 0.0;
        this._distance = 0.0;
    }

    public GeoLocation(double x, double y, double z, double distance) {
        this._x = x;
        this._y = y;
        this._z = z;
        this._distance = distance;
    }

    @Override
    public double x() {
        return this._x;
    }

    @Override
    public double y() {
        return this._y;
    }

    @Override
    public double z() {
        return this._z;
    }

    @Override
    public double distance(geo_location g) {
        return this._distance;
    }
}
