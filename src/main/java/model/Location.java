package model;

import java.io.Serializable;

public class Location implements Serializable {
    private final double lat;
    private final double lng;
    public double getLatitude() {return lat;}
    public double getLongitude() {return lng;}
    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
    
    public double distanceTo(Location other) {
        return 2 * Math.asin(Math.sqrt(Math.pow(Math.sin((lat - other.lng) / 2), 2) +
                (Math.cos(lat) * Math.cos(other.lng) * Math.pow(Math.sin((lng - other.lng) / 2), 2))));
    }

    @Override
    public String toString() {
        return "(" + lat + ", " + lng + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Location)) {
            return false;
        }
        Location that = (Location) o;
        return (this.lat == that.lat) && (this.lng == that.lng);
    }

    @Override
    public int hashCode() {
        return 3 + (int)(lat * 5) + (int)(lng * 11);
    }
}
