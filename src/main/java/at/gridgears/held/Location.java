package at.gridgears.held;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double latitude;
    private final double longitude;
    private final double radius;
    private final Instant timestamp;
    private final AmlData amlData;

    public Location(double latitude, double longitude, double radius, Instant timestamp) {
        this(latitude, longitude, radius, timestamp, null);
    }

    public Location(double latitude, double longitude, double radius, Instant timestamp, AmlData amlData) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.timestamp = timestamp;
        this.amlData = amlData;
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

    public Optional<AmlData> getAmlData() {
        return Optional.ofNullable(amlData);
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
                Objects.equals(timestamp, location.timestamp) &&
                Objects.equals(amlData, location.amlData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, radius, timestamp, amlData);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("latitude", latitude)
                .append("longitude", longitude)
                .append("radius", radius)
                .append("timestamp", timestamp)
                .append("amlData", amlData)
                .toString();
    }
}
