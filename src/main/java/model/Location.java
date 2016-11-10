package model;

import com.lynden.gmapsfx.javascript.object.LatLong;

import java.io.Serializable;

/**
 * Represents a (latitude, longitude) coordinate
 */
public class Location implements Serializable {
    private final double lat;
    private final double lng;
    
    /**
     * @return latitude of this location
     */
    public double getLatitude() { return lat; }
    
    /**
     * @return longitude of this location
     */
    public double getLongitude() {return lng;}
    
    /**
     * Creates a new location
     * @param latLong An object that holds a (latitude, longitude) pair
     */
    public Location(LatLong latLong) {
        this(latLong.getLatitude(), latLong.getLongitude());
    }
    
    /**
     * Creates a new Location
     * @param lat Latitude
     * @param lng Longitude
     */
    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
    
    /**
     * Gives the distance in miles between this and another location
     * @param other location to find distance to
     * @return the distance in miles between this location and other
     */
    public double distanceTo(Location other) {
        double latDiff = Math.toRadians(other.lat - lat);
        double lngDiff = Math.toRadians(other.lng - lng);
        double a = Math.pow(Math.sin(latDiff / 2), 2) + (Math.cos(Math.toRadians(lat))
                * Math.cos(Math.toRadians(other.lat)) * Math.pow(Math.sin(lngDiff / 2), 2));
                
        final double EARTH_RADIUS_MILES = 3959;
        return EARTH_RADIUS_MILES * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
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
