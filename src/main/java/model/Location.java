package model;

import java.io.Serializable;

public class Location implements Serializable {
    private double lat;
    private double lng;
    public double getLatitude() {return lat;}
    public double getLongitude() {return lng;}
    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
    
    public double distanceTo(Location other) {
        return 2 * Math.asin(Math.sqrt( Math.pow(Math.sin((this.getLatitude() - other.getLatitude())/2), 2) +
                Math.cos(this.getLatitude())*Math.cos(other.getLatitude())
                        * Math.pow(Math.sin((this.getLongitude() - other.getLongitude())/2), 2)));
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
        return this.lat == that.lat && this.lng == that.lng;
    }

    @Override
    public int hashCode() {
        return 3 + (int)(lat * 5) + (int)(lng * 11);
    }
}
