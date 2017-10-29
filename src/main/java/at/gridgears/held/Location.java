package at.gridgears.held;

import java.io.Serializable;
import java.time.Instant;

public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double latitude;
    private final double longitude;
    private final double radius;
    private final Instant timestamp;

    public Location(double latitude, double longitude, double radius, Instant timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getRadius() {
        return radius;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius +
                ", timestamp=" + timestamp +
                '}';
    }
}
