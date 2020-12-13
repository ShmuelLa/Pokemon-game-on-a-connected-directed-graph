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

    public GeoLocation() {
        this._x = 0.0;
        this._y = 0.0;
        this._z = 0.0;
    }

    public GeoLocation(double x, double y, double z) {
        this._x = x;
        this._y = y;
        this._z = z;
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
        double dx = this._x - g.x();
        double dy = this._y - g.y();
        double dz = this._z - g.z();
        double t = (dx*dx+dy*dy+dz*dz);
        return Math.sqrt(t);
    }
}
