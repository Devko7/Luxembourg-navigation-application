package project2;

import java.util.Map;

// Represents a geographical point
public class Point {
    private double lat;
    private double lon;

    public Point(double latitudeNumber, double longitudeNumber) {
        this.lat = latitudeNumber;
        this.lon = longitudeNumber;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public Map<String,Object> toJson() {
        return Map.of("lat", this.lat, "lon", this.lon);
    }
}
