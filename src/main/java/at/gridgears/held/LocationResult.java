package at.gridgears.held;

import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.*;

public class LocationResult implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum StatusCode {
        LOCATION_FOUND,
        GENERAL_LIS_ERROR,
        LOCATION_UNKNOWN,
        TIMEOUT,
        CANNOT_PROVIDE_LI_TYPE,
        NOT_LOCATABLE,
        UNKNOWN_ERROR
    }

    private final String identifier;
    private final Status status;
    private final List<Location> locations;

    private LocationResult(String identifier, Status status, List<Location> locations) {
        Validate.notNull(identifier, "identifier must not be null");
        Validate.notNull(status, "status must not be null");
        Validate.noNullElements(locations, "locations must not be null or contain null elements");

        this.identifier = identifier;
        this.status = status;
        this.locations = Collections.unmodifiableList(new ArrayList<>(locations));
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public Status getStatus() {
        return status;
    }

    public boolean hasLocations() {
        return status.getStatusCode() == StatusCode.LOCATION_FOUND;
    }

    public static LocationResult createFailureResult(String identifier, Status status) {
        Validate.notNull(status, "status must not be null");
        Validate.validState(status.getStatusCode() != StatusCode.LOCATION_FOUND, "Status must not be LOCATION_FOUND for an error result");
        return new LocationResult(identifier, status, Collections.emptyList());
    }

    public static LocationResult createFoundResult(String identifier, List<Location> locations) {
        Validate.notEmpty(locations, "locations must not be empty for a LOCATION_FOUND result");
        return new LocationResult(identifier, new Status(StatusCode.LOCATION_FOUND, ""), locations);
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
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(locations, that.locations) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, locations, status);
    }

    @Override
    public String toString() {
        return "LocationResult{" +
                "identifier='" + identifier + '\'' +
                ", status=" + status +
                ", locations=" + locations +
                '}';
    }

    public static class Status implements Serializable {
        private static final long serialVersionUID = 1L;

        private final StatusCode statusCode;
        private final String message;

        public Status(StatusCode statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public StatusCode getStatusCode() {
            return statusCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Status status = (Status) o;
            return statusCode == status.statusCode &&
                    Objects.equals(message, status.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(statusCode, message);
        }

        @Override
        public String toString() {
            return "Status{" +
                    "statusCode=" + statusCode +
                    ", message=" + message +
                    '}';
        }
    }
}
