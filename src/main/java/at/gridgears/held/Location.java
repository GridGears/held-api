package at.gridgears.held;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return Double.compare(location.latitude, latitude) == 0 &&
                Double.compare(location.longitude, longitude) == 0 &&
                Double.compare(location.radius, radius) == 0 &&
                Objects.equals(timestamp, location.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, radius, timestamp);
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
