package at.gridgears.held;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;

public final class FindLocationResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ERROR_LOCATION_UNKNOWN = "locationUnknown";

    private final FindLocationError error;
    private final List<Location> locations;
    private final List<LocationReference> locationReferences;

    public enum Status {
        FOUND,
        NOT_FOUND,
        ERROR
    }

    private FindLocationResult(List<Location> locations, List<LocationReference> locationReferences, @Nullable FindLocationError error) {
        Validate.noNullElements(locations, "locations must not be null or contain null elements");
        Validate.noNullElements(locationReferences, "locationReferences must not be null or contain null elements");

        this.error = error;
        this.locations = Collections.unmodifiableList(new ArrayList<>(locations));
        this.locationReferences = Collections.unmodifiableList(new ArrayList<>(locationReferences));
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<LocationReference> getLocationReferences() {
        return locationReferences;
    }

    public Optional<FindLocationError> getError() {
        return Optional.ofNullable(error);
    }

    public Status getStatus() {
        Status result;
        if (!locations.isEmpty() || !locationReferences.isEmpty()) {
            result = Status.FOUND;
        } else if (error.getCode().equals(ERROR_LOCATION_UNKNOWN)) {
            result = Status.NOT_FOUND;
        } else {
            result = Status.ERROR;
        }

        return result;
    }

    public static FindLocationResult createFailureResult(FindLocationError error) {
        Validate.notNull(error, "error must not be null");
        return new FindLocationResult(Collections.emptyList(), Collections.emptyList(), error);
    }

    public static FindLocationResult createFoundResult(List<Location> locations, List<LocationReference> locationReferences) {
        if (locations.isEmpty() && locationReferences.isEmpty()) {
            throw new IllegalArgumentException("locations and locationReferences must not both be empty for a success result");
        }
        return new FindLocationResult(locations, locationReferences, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FindLocationResult that = (FindLocationResult) o;
        return Objects.equals(error, that.error) &&
                Objects.equals(locations, that.locations) &&
                Objects.equals(locationReferences, that.locationReferences);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error, locations, locationReferences);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("error", error)
                .append("locations", locations)
                .append("locationReferences", locationReferences)
                .toString();
    }
}
