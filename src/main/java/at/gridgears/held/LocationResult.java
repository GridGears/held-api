package at.gridgears.held;

import java.io.Serializable;
import java.util.*;

public class LocationResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Location> locations;

    public LocationResult(List<Location> locations) {
        this.locations = Collections.unmodifiableList(new ArrayList<>(locations));
    }

    public List<Location> getLocations() {
        return locations;
    }

    public boolean hasLocations() {
        return !locations.isEmpty();
    }

    public static LocationResult createFoundResult(List<Location> locations) {
        return new LocationResult(locations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocationResult that = (LocationResult) o;
        return Objects.equals(locations, that.locations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locations);
    }

    @Override
    public String toString() {
        return "LocationResult{" +
                "locations=" + locations +
                '}';
    }
}
